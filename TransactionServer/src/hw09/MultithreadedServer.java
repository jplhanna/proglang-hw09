package hw09;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class CachedAccount{
	private boolean read = false;
	private boolean written = false;
	private int initial;
	private Account account;
	
	public CachedAccount(Account a) {
		account = a;
		initial = a.getValue();
	}
	
	public void updateread() {
		read = true;
	}
	
	public void updatewrite() {
		written = true;
	}
	
	
	public int getinitial() {
		return initial;
	}
	
	public int peek() {
		return account.peek();
	}
	
	public void verify(int expected) throws TransactionAbortException {
		account.verify(expected);
	}
	
	public void update(int value) {
		account.update(value);
	}
	
	public void open(boolean forWriting) throws TransactionAbortException {
		account.open(forWriting);
	}
	
	public void close() {
		account.close();
	}
	
	public void print() {
		account.print();
	}
	
	public void printMod() {
		account.printMod();
	}
	
	public void getValue() {
		account.getValue();
	}
}
// TO DO: Task is currently an ordinary class.
// You will need to modify it to make it a task,	DONE
// so it can be given to an Executor thread pool.
//
class Task implements Runnable {
    private static final int A = constants.A;
    private static final int Z = constants.Z;
    private static final int numLetters = constants.numLetters;

    private Account[] accounts;
    private CachedAccount[] cachedAccounts;
    private String transaction;

    // TO DO: The sequential version of Task peeks at accounts
    // whenever it needs to get a value, and opens, updates, and closes
    // an account whenever it needs to set a value.  This won't work in
    // the parallel version.  Instead, you'll need to cache values
    // you've read and written, and then, after figuring out everything
    // you want to do, (1) open all accounts you need, for reading,
    // writing, or both, (2) verify all previously peeked-at values,
    // (3) perform all updates, and (4) close all opened accounts.

    public Task(Account[] allAccounts, String trans) {
        accounts = allAccounts;
        cachedAccounts = new CachedAccount[accounts.length];
        for (int i = 0; i < accounts.length; i++) {
        	cachedAccounts[i] = new CachedAccount(accounts[i]);
        }
        transaction = trans;
    }
    
    // TO DO: parseAccount currently returns a reference to an account.
    // You probably want to change it to return a reference to an
    // account *cache* instead.
    //
    private CachedAccount parseAccount(String name) {
        int accountNum = (int) (name.charAt(0)) - (int) 'A';
        if (accountNum < A || accountNum > Z)
            throw new InvalidTransactionError();
        
        Account a = accounts[accountNum];
        CachedAccount b = cachedAccounts[accountNum];
        
        for (int i = 1; i < name.length(); i++) {
            if (name.charAt(i) != '*')
                throw new InvalidTransactionError();
            
            accountNum = (accounts[accountNum].peek() % numLetters);
            a = accounts[accountNum];
            b = cachedAccounts[accountNum];
        }
        
        return b;
    }

    private int parseAccountOrNum(String name) {
        int rtn;
        if (name.charAt(0) >= '0' && name.charAt(0) <= '9') {
            rtn = new Integer(name).intValue();
        } else {
            rtn = parseAccount(name).peek();
        }
        return rtn;
    }

    public void run() {
        // tokenize transaction
        String[] commands = transaction.split(";");

        for (int i = 0; i < commands.length; i++) {
            String[] words = commands[i].trim().split("\\s");
            if (words.length < 3)
                throw new InvalidTransactionError();
            CachedAccount lhs = parseAccount(words[0]);
            if (!words[1].equals("="))
                throw new InvalidTransactionError();
            int rhs = parseAccountOrNum(words[2]);
            for (int j = 3; j < words.length; j+=2) {
                if (words[j].equals("+"))
                    rhs += parseAccountOrNum(words[j+1]);
                else if (words[j].equals("-"))
                    rhs -= parseAccountOrNum(words[j+1]);
                else
                    throw new InvalidTransactionError();
            }
            try {
                lhs.open(true);
            } catch (TransactionAbortException e) {
                // won't happen in sequential version
            }
            lhs.update(rhs);
            lhs.close();
        }
        System.out.println("commit: " + transaction);
    }
}

public class MultithreadedServer {

	// requires: accounts != null && accounts[i] != null (i.e., accounts are properly initialized)
	// modifies: accounts
	// effects: accounts change according to transactions in inputFile
    public static void runServer(String inputFile, Account accounts[])
        throws IOException {

        // read transactions from input file
        String line;
        BufferedReader input =
            new BufferedReader(new FileReader(inputFile));

        // TO DO: you will need to create an Executor and then modify the
        // following loop to feed tasks to the executor instead of running them
        // directly.  
        
        ExecutorService pool = Executors.newCachedThreadPool();
        while ((line = input.readLine()) != null) {
        	pool.execute(new Task(accounts,line));
        }
        pool.shutdown();
        
        input.close();

    }
}
