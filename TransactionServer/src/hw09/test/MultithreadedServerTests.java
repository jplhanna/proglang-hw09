package hw09.test;

import hw09.*;

import java.io.*;
import java.lang.Thread.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.*;
import java.util.Random;

import junit.framework.TestCase;

import org.junit.Test;

public class MultithreadedServerTests extends TestCase {
    private static final int A = constants.A;
    private static final int Z = constants.Z;
    private static final int numLetters = constants.numLetters;
    private static Account[] accounts;
            
    protected static void dumpAccounts() {
	    // output values:
	    for (int i = A; i <= Z; i++) {
	       System.out.print("    ");
	       if (i < 10) System.out.print("0");
	       System.out.print(i + " ");
	       System.out.print(new Character((char) (i + 'A')) + ": ");
	       accounts[i].print();
	       System.out.print(" (");
	       accounts[i].printMod();
	       System.out.print(")\n");
	    }
	 }    
     
        
     @Test
	 public void testIncrement() throws IOException {
	
		// initialize accounts 
		accounts = new Account[numLetters];
		for (int i = A; i <= Z; i++) {
			accounts[i] = new Account(Z-i);
		}			 
		
		MultithreadedServer.runServer("hw09/data/increment", accounts);
	
		// assert correct account values
		for (int i = A; i <= Z; i++) {
			Character c = new Character((char) (i+'A'));
			assertEquals("Account "+c+" differs",Z-i+1,accounts[i].getValue());
		}		

	 }
	 	  	 
	@Test
	public void testBasics() throws IOException{
		//initialize accounts
		System.out.println("Test Basics Starting: ");
		accounts=new Account[numLetters];
		for(int i = A; i <= Z; i++){
			accounts[i]=new Account(0);
		}
		accounts[1]=new Account(1);//B
		accounts[2]=new Account(2);//C
		accounts[3]=new Account(5);//D
		accounts[4]=new Account(2);//E
		accounts[5]=new Account(3);//F
		accounts[6]=new Account(1);//G
		accounts[8]=new Account(2);//I
		accounts[9]=new Account(6);//J
		accounts[10]=new Account(3);//K
		MultithreadedServer.runServer("hw09/data/basicData", accounts);
		for (int i = A; i <= Z; i++){
			System.out.println(i + " " + accounts[i].getValue());
		}
		assertEquals("Account A differs",3,accounts[0].getValue());
		assertEquals("Account D differs",3,accounts[3].getValue());
		assertEquals("Account E differs",5,accounts[4].getValue());
		assertEquals("Account J might be wrong",accounts[9].getValue(),-1);
		assertEquals("Account K might be wrong",accounts[10].getValue(),-3);
	}
     
	@Test
	public void referenceTest() throws IOException{
		//initialize accounts
		System.out.println("Test reference Starting: ");
		accounts=new Account[numLetters];
		for(int i=A;i<=Z;i++){
			accounts[i]=new Account(0);
		}
		accounts[A]=new Account(37);//a
		accounts[1]=new Account(8);//b
		accounts[3]=new Account(40);//d
		accounts[4]=new Account(8);//e
		accounts[6]=new Account(125);//g
		accounts[8]=new Account(26);//i
		accounts[9]=new Account(360);//j
		accounts[21]=new Account(450);//v
		accounts[22]=new Account(10);//w
		
		MultithreadedServer.runServer("hw09/data/referenceData", accounts);
		
		assertEquals("Reference of A does not work",accounts[A].getValue(),accounts[2].getValue());
		assertEquals("Double reference of G does not work",accounts[5].getValue(),accounts[8].getValue());
		assertEquals("Reference subtraction might not work",47,accounts[7].getValue());
		assertEquals("Reference subtraction might not work",58,accounts[7].getValue());
	}
	
	@Test
	public void multiTransactionsTest() throws IOException{
		System.out.println("Test multitransactions Starting: ");
		accounts=new Account[numLetters];
		
		//initialize accounts
		accounts=new Account[numLetters];
		for(int i=A;i<=Z;i++){
			accounts[i]=new Account(0);
		}
		accounts[1]=new Account(3);//b
		accounts[2]=new Account(5);//c
		accounts[3]=new Account(8);//d
		accounts[5]=new Account(20);//f
		accounts[6]=new Account(10);//g
		accounts[7]=new Account(5);//h
		accounts[9]=new Account(40);//j
		accounts[10]=new Account(2);//k
		accounts[11]=new Account(8);//l
		accounts[13]=new Account(300);//n
		accounts[14]=new Account(200);//o
		accounts[15]=new Account(100);//p
		accounts[18]=new Account(50);//s
		accounts[22]=new Account(125);//w
		
		MultithreadedServer.runServer("hw09/data/multiTransactions",accounts);
		
		assertEquals("Tri addition differs",16,accounts[A].getValue());
		assertEquals("Tri subtraction differs",5,accounts[4].getValue());
		assertEquals("Mixed evaluation differs",46,accounts[8].getClass());
		assertEquals("Multi reference evaluation differs",125,accounts[12].getValue());
	}
}