package com.vikas.vm;
import static com.vikas.input.InputFile.inputFile;
import static com.vikas.output.OutputFile.outputFile;
import java.io.IOException;
import java.util.Random;
import com.vikas.input.InputFile;
import com.vikas.mos.MOS;
import com.vikas.output.OutputFile;
public class VirtualMachine {
	//All variable declaration
	public static char[][] M;
	public static char[] IR;
	public static byte IC;
	public static int RA;
	public static int VA;
	public static byte PI;
	public static byte TTC;
	public static byte LLC;
	private static char[] R;
	public static int PTR;
	private static boolean C;
	public static char SI;
	public static byte TI;
	public static String opcode;
	public static String oprand;
	
	//this array is used to store allocated frame no
	public static boolean mem[];

	public VirtualMachine() {
		M = new char[300][4];
		IR = new char[4];
		R = new char[4];
		mem=new boolean[30];
	}
	// init Function
	public static void init() {
		// init memory with
		System.out.println("****init *******\n");
		//init memeory
		for (int i = 0; i < 300; i++)
			M[i][0] = M[i][1] = M[i][2] = M[i][3] = 0;
		
		//init ir r
		for (int i = 0; i < 4; i++) 
			IR[i] = R[i] = 0;
			// PTR[i]=0;
			
		//init allocation blocks set to be false
		for(int i=0;i<30;i++)
			mem[i]=false;
		
		C = false;
		SI = 0;
		TI=IC =TTC=LLC=0;
		PCB.TTL = PCB.JOB_ID = PCB.TLL = 0;
		System.out.println("****init complited *******\n");
	}// init
	
	public static void load() {
		System.out.println("***********start of load*********");
		String buffer = null;
		boolean flag = false;
		try {
			// get input File
			inputFile = InputFile.getFile();
			while (((buffer = inputFile.readLine()) != null)) {
				System.out.println("************************************");
				System.out.println("buffer  : " + buffer + "   " + buffer.length());
				// chek for CC $DTA
				if (buffer.equals("$DTA"))
					break;
				// chek for CC $END
				if (buffer.startsWith("$END"))
					continue;
				// error cheking code
				if (buffer.length() > 40)
					throw new IOException("Incorrect Input File Size of words is more then 10");
				// condition for 1st line of job
				if (buffer.startsWith("$AMJ")) {
					// call init method
					init();
					PCB.JOB_ID = Integer.parseInt(buffer.substring(4, 8));
					PCB.TTL = Integer.parseInt(buffer.substring(8, 12));
					PCB.TLL = Integer.parseInt(buffer.substring(12, 16));
					//set flag true
					flag = true;
					//allocate frame for page table
					PTR = allocate() * 10;
					System.out.println("allocated block for page table  "+ PTR/10);
					System.out.println("PTR is  " + PTR);
					//init page table to * value
					for (int i = PTR; i < (PTR + 10); i++) {
						M[i][0] = '*';
						M[i][1] = '*';
						M[i][2] = '*';
						M[i][3] = '*';
					}
					VA = PTR;
					continue;
				} // if
				if (flag) {
					//allocate frame to program card
					RA = allocate();
					System.out.println("allocated frame   "+ RA);
					//create entry of pc in page table
					M[VA][2] = (char) ((RA / 10) + 48);
					M[VA][3] = (char) ((RA % 10) + 48);
					M[VA][0]='1';
					VA++;
					RA = (RA * 10);
					System.out.println("RA to " + RA);
					// Load PC into main memory allocated frame
					int i = 0;
					while (i < buffer.length()) {
						if (buffer.charAt(i) == 'H') {
							M[RA][0] = 'H';
							M[RA][1] = ' ';
							break;
						}
						M[RA][i % 4] = buffer.charAt(i);
						//System.out.println(M[RA][i%4]);
						i++;				
						if (i % 4 == 0)
							RA++;					
					}//end while
				} // if
			} // end while
			if (buffer != null)
				startExecution();
		} // try
		catch (IOException e) {
			System.out.println("Input Error" + e);
			e.getMessage();
		} // catch
		//close output file
		//outputFile
		outputFile.close();
		System.out.println("***********All Complited************");
		System.exit(1);
	}// load method

	public static void startExecution() {
		IC = 0;
		executeUserProgram();
	}// end startExecution

	public static void executeUserProgram() {
		boolean flag = true;	
		try {
			while (flag) {
				System.out.println("\n**************************************");
				//call address map function using  IC and get real address of frame 
				addressMap((IC+""));
				System.out.println("frame block  address RA  is  " + RA);
				IR[0] = M[RA][0];
				IR[1] = M[RA][1];
				IR[2] = M[RA][2];
				IR[3] = M[RA][3];
				IC++;
				//check of total time limit
				System.out.println("OPCODE IS  : " + (opcode = "" + IR[0] + IR[1]));
				System.out.println("oprand is : " + RA);
				System.out.println("Total time limit counter is  :  "+TTC);
				//call address map fun using VA and calculate real address of opcode
				addressMap(("" + IR[2] + IR[3]));
				System.out.println("Real address of opcode RA  " + RA);
				//check for pi
				if (PI != 0) {
					MOS.MOSInit();
					PI = 0;
				}
				switch (opcode) {
				case "GD":
					SI = '1';
					break;
				case "PD":
					SI = '2';
					break;
				case "LR":
					// write memory to Register
					System.out.print((R[0] = M[RA][0]));
					System.out.print((R[1] = M[RA][1]));
					System.out.print((R[2] = M[RA][2]));
					System.out.println((R[3] = M[RA][3]));
					break;
				case "SR":
					// write memory to Register
					System.out.print((M[RA][0] = R[0]));
					System.out.print((M[RA][1] = R[1]));
					System.out.print((M[RA][2] = R[2]));
					System.out.println((M[RA][3] = R[3]));
					break;
				case "CR":
					// compare R amd MEMORY ofM[RA[3-4]]
					if (R[0] == M[RA][0] && R[1] == M[RA][1] && R[2] == M[RA][2] && R[3] == M[RA][3])
						// set C valuse as false R==M[IR]
						C = true;
					else
						// set C valuse as false R!=M[IR]
						C = false;
					System.out.println(C);
					break;
				case "BT":
					// check C =T
					if (C)
						// set IC=IR[3-4]
						System.out.println("IN BT  IC Change to is : " + (IC = Byte.parseByte("" + IR[2] + IR[3])));
					break;
				case "H ":
					SI = '3';
					flag = false;
					break;
				default:
					PI = 1;
				}// end switchcase
				if((++TTC) == PCB.TTL)
					TI=2;
				MOS.MOSInit();
				SI = 0;
				
				
			} // loop
		} // try
		catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}// end executeUSer program
		// allocate

	public static int allocate() {
		boolean flag=true;
		int i=0;
		
		while (flag) {
			i=(new Random().nextInt(29));
			i=i%29;
			if(mem[i]==false) {
				mem[i]=true;
				flag=false;
			}
		}
		return i;
	}

	// address map
	public static void addressMap(String code) {
		if ("H ".equals("" + IR[0] + IR[1]))
			return;
		try {
			//get the real address of operation from frame or page table and calculate using Real address
			//validate real address and set pi value
			VA=Integer.parseInt(code);
			System.out.println("***************code    =  "+code+"   "+M[PTR+(VA / 10)][0]);
			if(M[PTR+(VA / 10)][0]=='1') {
				//return;
			System.out.println("alocated  ");
			RA = ((M[PTR + (VA / 10)][2] - 48) * 100) + (M[PTR + (VA / 10)][3] - 48) * 10 +(VA % 10);
			}else
				PI = 3;
		} catch (Exception e) {
			//if operand is incorrect pi =2
			PI = 2;
		}
	}
}// end class