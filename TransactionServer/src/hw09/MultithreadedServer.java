package hw09;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//A class which boxes an account object, holding additional information. Acts an initial layer of to call functions
//on Accounts which will be accessed often.
class CachedAccount{
	private boolean read = false; //shared mutable
	private boolean written = false; //shared mutable
	private int initial; //shared imutable
	private int temp; //shared mutable
	private Account account;
	
	//requires:a!=null and a.getValue()!=null
	//modifies:account and initial
	//effects:sets account to a and initial to the value of account a
	public CachedAccount(Account a) {
		account = a;
		initial = a.getValue();
	}
	//requires:nothing
	//modifies:read
	//effects:sets read to true
	public void updateRead() {
		read = true;
	}
	//requires:nothing
	//modifies:written
	//effects:sets written to true
	public void updateWrite() {
		written = true;
	}

	//requires:a!=null
	//modifies:temp
	//effects:sets temp to the new value which account might set to, a. 
	public void updateTemp(int a){
		temp=a;
	}
	//output:returns the initial value of the stored Account
	public int getInitial() {
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
	//output:returns whether this account will be read.
	public boolean getRead(){
		return read;
	}
	//output:returns whether this account will be written to
	public boolean getWritten(){
		return written;
	}
	//outpout:returns what the temporary value that account might be.
	public int getTemp(){
		return temp;
	}
	
	public int getValue() {
		return account.getValue();
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

    private CachedAccount[] cachedAccounts;
    private String transaction;
    
    //Requires:cachedAccounts!=null;
    //modifies:cachedAcounts
    //effects:Runs through all accounts in cachedAcounts and closes them.
    private void closeEverything(){
    	for (int i = A; i <= Z; i++){
    		if (cachedAccounts[i].getRead() || cachedAccounts[i].getWritten()){
    			try {
					cachedAccounts[i].close();
				}
				catch (TransactionUsageError exception) {
					//do nothing, but catch the exception...
				}
    		}
    	}
    }

    // TO DO: The sequential version of Task peeks at accounts
    // whenever it needs to get a value, and opens, updates, and closes
    // an account whenever it needs to set a value.  This won't work in
    // the parallel version.  Instead, you'll need to cache values
    // you've read and written, and then, after figuring out everything
    // you want to do, (1) open all accounts you need, for reading,
    // writing, or both, (2) verify all previously peeked-at values,
    // (3) perform all updates, and (4) close all opened accounts.

    //requires: allAccounts!=null&allAccounts.length==26,trans!=null and start with the form "char = " 
    //modifies:cashedAccounts and transaction
    //sets: boxes in the array of Accounts into an Array of CachedAccounts and sets cachedAccounts to that. Sets transactions to trans.
    public Task(Account[] allAccounts, String trans) {
        cachedAccounts = new CachedAccount[allAccounts.length];
        for (int i = 0; i < allAccounts.length; i++) {
        	cachedAccounts[i] = new CachedAccount(allAccounts[i]);
        }
        transaction = trans;
    }
    
    // TO DO: parseAccount currently returns a reference to an account.
    // You probably want to change it to return a reference to an	DONE
    // account *cache* instead.
    //
    private CachedAccount parseAccount(String name) {
        int accountNum = (int) (name.charAt(0)) - (int) 'A';
        if (accountNum < A || accountNum > Z)
            throw new InvalidTransactionError();
        
        CachedAccount b = cachedAccounts[accountNum];
        
        for (int i = 1; i < name.length(); i++) {
            if (name.charAt(i) != '*')
                throw new InvalidTransactionError();
            
            accountNum = (cachedAccounts[accountNum].peek() % numLetters);
            b = cachedAccounts[accountNum];
        }
        
        return b;
    }

    private int parseAccountOrNum(String name) {
        int rtn;
        if (name.charAt(0) >= '0' && name.charAt(0) <= '9') {
            rtn = new Integer(name).intValue();
        } else {
        	//updates to say that the value is only being read, not written, peeks for returning
        	CachedAccount tempaccount=parseAccount(name);
        	tempaccount.updateRead();
            rtn = tempaccount.peek();
        }
        return rtn;
    }

    //requires: cachedAccounts!=null&transaction!=null
    //modifies: cachedAccounts
    //effects: Reads through the list of transactions from data files and constructs a set of instructions to complete
    //Which it then feeds into the Threaded Pool to complete, if possible. These changes are input into cachedAccounts.
    public void run() {
        // tokenize transaction
        String[] commands = transaction.split(";");
        
        //keeps going until run has succeeded has succeeded
        while (true){
	        for (int i = 0; i < commands.length; i++) {
	            String[] words = commands[i].trim().split("\\s");
	            if (words.length < 3)
	                throw new InvalidTransactionError();
	            CachedAccount lhs = parseAccount(words[0]);
	            //marks that the account will be written to later
	            lhs.updateWrite();
	            
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
	            /*try {
	                lhs.open(true);
	            } catch (TransactionAbortException e) {
	                // won't happen in sequential version
	            }*/
	            lhs.updateTemp(rhs);
	            
	            //lhs.close();
	        }
	        
	        //now we attempt to open all the accounts...
	        try {
	        	for (int i = A; i <= Z; i++){
	        		//if both written and read are marked
	        		if (cachedAccounts[i].getRead() && cachedAccounts[i].getWritten()) {
	        			cachedAccounts[i].open(false);
	        			cachedAccounts[i].open(true);
					}
					//if only written is marked
					else if (cachedAccounts[i].getWritten()) {
						cachedAccounts[i].open(true);
					}
					//if only read is marked
					else if (cachedAccounts[i].getRead()) {
						cachedAccounts[i].open(false);
					}
	        	}
	        } catch (TransactionAbortException exception) {
	        	//close all accounts and reset
	        	closeEverything();
	        	continue;
	        }
	        
	        //if we've made it this far, verify that the read values are accurate
	        try {
	        	
	        	for (int i = A; i <= Z; i++){
	        		if (cachedAccounts[i].getRead()){
	        			cachedAccounts[i].verify(cachedAccounts[i].getInitial());
	        		}
	        	}
	        } catch (TransactionAbortException exception) {
	        	//close all accounts and reset again
	        	closeEverything();
	        	continue;
	        }
	        
	        //if we have passed everything else, we can finally actually write the values
	        for (int i = A; i <= Z; i++){
	        	if (cachedAccounts[i].getWritten()){
	        		//System.out.println("caching: " + cachedAccounts[i].getValue());
	        		cachedAccounts[i].update(cachedAccounts[i].getTemp());
	        		//System.out.println("cached: " + cachedAccounts[i].getValue());
	        	}
	        }
	        
	        //close all accounts and reset and break since we've succeeded finally
	        closeEverything();
	        break;
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
        
        //waits for everything to terminate (so the 
        try {
        	pool.awaitTermination(120,TimeUnit.SECONDS);
        } catch (InterruptedException exception) {
        	System.err.println("Pool failed to terminate in alotted time");
        }
        
        input.close();

    }
}
