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
		
		MultithreadedServer.runServer("src/hw09/data/increment", accounts);
	
		// assert correct account values
		for (int i = A; i <= Z; i++) {
			Character c = new Character((char) (i+'A'));
			assertEquals("Account "+c+" differs",Z-i+1,accounts[i].getValue());
		}		

	 }
	 	  	 
	@Test
	public void testBasics() throws IOException{
		//initialize accounts
		accounts=new Account[11];
		accounts[A]=new Account(0);//a
		accounts[1]=new Account(1);//b
		accounts[2]=new Account(2);//c
		accounts[3]=new Account(5);//d
		accounts[4]=new Account(0);//e
		accounts[5]=new Account(3);//f
		accounts[6]=new Account(1);//g
		accounts[7]=new Account(0);//h
		accounts[8]=new Account(2);//i
		accounts[9]=new Account(6);//j
		accounts[10]=new Account(3);//k
		
		MultithreadedServer.runServer("src/hw09/data/basicData", accounts);
		assertEquals("Account A differs",3,accounts[0].getValue());
		assertEquals("Account D differs",8,accounts[3].getValue());
		assertEquals("Account E differs",5,accounts[4].getValue());
		assertEquals("Account H might be wrong",accounts[7].getValue(),8);
		assertEquals("Account H might be wrong",accounts[7].getValue(),6);
	}
     
	@Test
	public void referenceTest() throws IOException{
		//initialize accounts
		accounts=new Account[numLetters];
		accounts[A]=new Account(37);//a
		accounts[1]=new Account(8);//b
		accounts[2]=new Account(0);//c
		accounts[3]=new Account(40);//d
		accounts[4]=new Account(8);//e
		accounts[5]=new Account(0);//f
		accounts[6]=new Account(125);//g
		accounts[7]=new Account(0);//h
		accounts[8]=new Account(26);//i
		accounts[9]=new Account(360);//j
		accounts[21]=new Account(450);//v
		accounts[22]=new Account(10);//v
		
		MultithreadedServer.runServer("src/hw09/data/referenceData", accounts);
		
		assertEquals("Reference of A does not work",accounts[A],accounts[2]);
		assertEquals("Double reference of G does not work",accounts[5],accounts[8]);
		assertEquals("Reference subtraction might not work",47,accounts[7]);
		assertEquals("Reference subtraction might not work",58,accounts[7]);
	}
}