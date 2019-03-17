package com.vikas.mos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import com.vikas.input.InputFile;
import com.vikas.output.OutputFile;
import com.vikas.vm.PCB;
import com.vikas.vm.VirtualMachine;
import static com.vikas.input.InputFile.inputFile;
import static com.vikas.output.OutputFile.outputFile;

public class MOS {
	// input output static variables
	public MOS() {
		// TODO Auto-generated constructor stub
	}

	// init method of mos
	public static void MOSInit() {

		switch ((VirtualMachine.TI + "" + VirtualMachine.PI)) {
		case "01":
			terminate(4);
			break;
		case "02":
			terminate(5);
			break;
		case "03":
			if (VirtualMachine.opcode.equals("GD") || VirtualMachine.opcode.equals("SR")) {	
				int oprand = Integer.parseInt("" + VirtualMachine.IR[2] + VirtualMachine.IR[3]);
				if(VirtualMachine.M[VirtualMachine.PTR+oprand/10][3]=='*') {
				VirtualMachine.VA = VirtualMachine.allocate();
				VirtualMachine.M[(VirtualMachine.PTR + oprand / 10)][0]='1';
				VirtualMachine.M[(VirtualMachine.PTR + oprand / 10)][2] = (char) ((VirtualMachine.VA / 10) + 48);
				VirtualMachine.M[(VirtualMachine.PTR + oprand / 10)][3] = (char) ((VirtualMachine.VA % 10) + 48);
				VirtualMachine.RA = VirtualMachine.VA * 10;
				}
			} else {				

					terminate(6);
				
			}
			break;
		case "21":
			terminate(7);
			break;
		case "22":
			terminate(8);
			break;
		case "23":
			terminate(3);
			break;
		}
		// switch case of mos for si
		String s = VirtualMachine.TI + "" + VirtualMachine.SI;
		switch (s) {
		case "01":
			read();
			break;
		case "02":
			write();
			break;
		case "03":
			terminate(0);
			break;
		case "21":
			terminate(3);
		case "22":
			write();
			terminate(3);
			break;
		case "23":
			terminate(0);
			break;
		}
		VirtualMachine.SI=0;
		VirtualMachine.PI=0;
		

	}

	// read method of mos
	public static void read() {
		String buffer = null;
		byte oprand = 0;
		try {
			// get input File
			if (inputFile == null) {
				inputFile = InputFile.getFile();
			}
			// CHECK for CC $END to EXIT
			if ((buffer = inputFile.readLine()).startsWith(("$END"))) {
				terminate(1);
				//System.out.println("Terminate call");
				return;
			}
			System.out.println("Buffer is   : " + buffer + buffer.length());
			// error cheking code more the 10 words in line
			if ((buffer.length()) > 40)
				throw new IOException("Incorrect Input File");
			// get oprand from memory
			System.out.println("****GD insite memory *****");
			// write input data into memory GD ISTRACTION
			System.out.println("GD DATA is stord in frame : "+VirtualMachine.RA);
			//store read data in memory
			int i = 0;
			while (buffer.length() != i) {
				VirtualMachine.M[VirtualMachine.RA][i % 4] = buffer.charAt(i++);
				System.out.print(VirtualMachine.M[VirtualMachine.RA][(i - 1) % 4]);
				if (i % 4 == 0)
					VirtualMachine.RA++;
			}
		} catch (IOException e) {
			// printing an exceptions
			System.out.println(e);
		}
	}

	public static void write() {
		String buffer = "";
		byte oprand = 0;
		try {
			if (++VirtualMachine.LLC > PCB.TLL) {
				terminate(2);
				return;
			}
			// get oprand form memory
			System.out.println(
					"oprand is : " + (oprand = Byte.parseByte("" + VirtualMachine.IR[2] + VirtualMachine.IR[3])));
			System.out.println(VirtualMachine.RA);
			System.out.println("****PD insite memory *****\n read content of memory");
			// write memory data into buffer
			int i = 0;
			while (40 != i) {
				buffer += VirtualMachine.M[VirtualMachine.RA][i % 4];
				i++;
				if (i % 4 == 0)
					VirtualMachine.RA++;
			}
			// write buffer into output file
			System.out.println("Write to f "  +buffer);
			// get output file
			if (outputFile == null) {
				outputFile = OutputFile.getFile();
			}
			outputFile.println(buffer);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void terminate(int EM) {
		// get output file
		if (outputFile == null) {
			outputFile = OutputFile.getFile();
		}
		//System.out.println("*******Error code : ****** "+EM);
		outputFile.print(PCB.JOB_ID+"   ");
		switch (EM) {
		case 0:
			outputFile.println("no error");
			break;
		case 1:
			outputFile.println("Out of Data");
			break;
		case 2:
			outputFile.println("Line Limit Exceeded ");
			break;
		case 3:
			outputFile.println("Time Limit Exceeded");
			break;
		case 4:
			outputFile.println("Operation Code Error");
			break;
		case 5:
			outputFile.println("Operand Error");
			break;
		case 6:
			outputFile.println("Invalid Page Fault");
			break;
		case 7:
			outputFile.println("Time Limit Exceeded with Operation Code Error");
			break;
		case 8:
			outputFile.println("Time Limit Exceeded with Operand Error");
			break;

		}
		outputFile.print(VirtualMachine.IC+"   "+VirtualMachine.IR[0]+VirtualMachine.IR[1]+VirtualMachine.IR[2]+VirtualMachine.IR[3]);
		outputFile.print("    "+VirtualMachine.TTC+"  "+VirtualMachine.LLC);
		outputFile.println("\n\n\n");
		VirtualMachine.load();	
	}
}