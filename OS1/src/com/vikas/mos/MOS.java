package com.vikas.mos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import com.vikas.input.InputFile;
import com.vikas.output.OutputFile;
import com.vikas.vm.VirtualMachine;
import static com.vikas.input.InputFile.inputFile;
import static com.vikas.output.OutputFile.outputFile;

public class MOS {
	//input output static variables
	public MOS() {
		// TODO Auto-generated constructor stub
	}
	//init method of mos
	public static void mosInit() {
		//switch case of mos for si
		switch (VirtualMachine.SI) {
		case '1':
				read();
				break;
		case '2':
				write();
				break;
		case '3' :
				terminate();
				break;
		}
	}
	//read method of mos
	public static void read() {
		String buffer=null;
		byte oprand=0;
		try {
			//get input File
			if(inputFile==null) {
				inputFile=InputFile.getFile();
			}
			//CHECK for CC $END to EXIT
			if((buffer=inputFile.readLine()).startsWith(("$END")))
				return;
			System.out.println("Buffer is   : "+buffer+buffer.length());
			//error cheking code more the 10 words in line
			if((buffer.length())>40)
				throw new IOException("Incorrect Input File");
			//get oprand from memory
			System.out.println("oprand is : "+ (oprand=Byte.parseByte(""+VirtualMachine.IR[2]+VirtualMachine.IR[3])));
			System.out.println("****GD insite memory *****");
			//write input data into memory GD ISTRACTION
			for(int i=0;i<(buffer.length());i++) {
				System.out.print((VirtualMachine.M[oprand][0]=buffer.charAt(i++)));
				if(i<buffer.length())
				System.out.print((VirtualMachine.M[oprand][1]=buffer.charAt(i++)));
				if(i<buffer.length())
				System.out.print((VirtualMachine.M[oprand][2]=buffer.charAt(i++)));
				if(i<buffer.length())
				System.out.println((VirtualMachine.M[oprand][3]=buffer.charAt(i)));
				oprand++;
			}		
		}
		catch(IOException e) {
			//printing an exceptions
			System.out.println(e);	
		}
	}
	public static void write() {
		String buffer="";
		byte oprand=0;
		try {
			//get output file
			if(outputFile==null) {
				outputFile=OutputFile.getFile();
			}
			//get oprand form memory
			System.out.println("oprand is : "+ (oprand=Byte.parseByte(""+VirtualMachine.IR[2]+VirtualMachine.IR[3])));
			System.out.println("****PD insite memory *****\n read content of memory");
			//write memory data into buffer
			for(int i=(oprand+10);oprand<i;oprand++) {
				if(VirtualMachine.M[oprand][0]!=0) 
					buffer+=VirtualMachine.M[oprand][0];
				else 
					break;
				if(VirtualMachine.M[oprand][1]!=0) 
					buffer+=VirtualMachine.M[oprand][1];
				else 
					break;
				if(VirtualMachine.M[oprand][2]!=0) 
					buffer+=VirtualMachine.M[oprand][2];
				else 
					break;
				if(VirtualMachine.M[oprand][3]!=0) 
					buffer+=VirtualMachine.M[oprand][3];
				else 
					break;
			}
			//write buffer into output file
			System.out.println(buffer);
			outputFile.println(buffer);
		}
		catch(Exception e) {
			System.out.println(e);	
		}
	}
	public static void terminate() {
		//get output file
		if(outputFile==null) {
			outputFile=OutputFile.getFile();
		}
		outputFile.println("\n");
		VirtualMachine.load();
		outputFile.close();
	}
}