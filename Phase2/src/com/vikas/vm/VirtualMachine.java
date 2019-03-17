package com.vikas.vm;
import java.io.BufferedReader;
import static com.vikas.input.InputFile.inputFile;
import java.io.FileReader;
import java.io.IOException;
import com.vikas.input.InputFile;
import com.vikas.mos.MOS;
public class VirtualMachine {

	public static char[][] M;
	public static char[] IR;
	private static byte IC;
	private static char[] R;
	private static boolean C;
	public static char SI;
	private static int JOB_ID;
	private static int TTL;
	private static int TLL;
	public static byte BLOCK=0;
	public VirtualMachine(){
		M=new char[100][4];
		IR=new char[4];
		R=new char[4];
	}
	//init Function
	public static void init() {
		//init memory with 
		System.out.println("in init\n****memory int *******\n");
		for(int i=0;i<100;i++) {
				M[i][0]=0;
				M[i][1]=0;
				M[i][2]=0;
				M[i][3]=0;
		}
		System.out.println("\n****IR int *******\n");
		for(int i=0;i<4;i++) {
			IR[i]=0;
			R[i]=0;
		}
		SI=0;
		IC=0;
		C=false;
		TTL=0;
		JOB_ID=0;
		TLL=0;
		BLOCK=-1;
	}//init
	//\\//\\//\\//\\.//..\\~~//\\//---Load Function
	public static void load() {
		System.out.println("start of load");
		String buffer=null;
		byte block=0;
		boolean flag=false;
		try {
				//get input File
				inputFile=InputFile.getFile();
				while(((buffer=inputFile.readLine())!=null)) {
					//chek for CC $DTA
					if(buffer.equals("$DTA"))break;
					if(buffer.startsWith("$END"))continue;
					System.out.println("buffer  : "+buffer+"   "+buffer.length());
					//error cheking code
					if(buffer.length()>40)
						throw new IOException("Incorrect Input File Size of words is more then 10");
					//condition for 1st line of job
					if(buffer.startsWith("$AMJ")) {
						//call init method
						init();
						System.out.println("JOB ID  "+(JOB_ID=Integer.parseInt(buffer.substring(4,8))));
						System.out.println("TTL  "+(TTL=Integer.parseInt(buffer.substring(8,12))));
						System.out.println("TLL  "+(TLL=Integer.parseInt(buffer.substring(12,16))));
						flag=true;
						continue;
					}//if
					if(flag) {
						if(++BLOCK>9)
							throw new IOException("MEMORY ERROR 10 Blocks excceds");
						//chek for GD instraction
						if(BLOCK==0 && !(buffer.startsWith("GD")))
								throw new IOException("Incorrect instraction 1st instraction must be GD");
						block=(byte) (BLOCK*10);
						//Load CC into main memory
						for(int i=0;i<buffer.length();) {
							if(BLOCK > TTL)
								throw new IOException("INSTRACTIONS NOT MATCH MORE THEN SPECIFIED");
							if(buffer.charAt(i)=='H') {
								M[block][0]='H';
								M[block][1]=' ';
								break;
							}
							M[block][0]=buffer.charAt(i++);
							M[block][1]=buffer.charAt(i++);
							M[block][2]=buffer.charAt(i++);
							M[block][3]=buffer.charAt(i++);
							block++;	
						}
					}//if
				}//end while
				if(buffer!=null)
					startExecution();
			}//try	
			catch(IOException e) {
				System.out.println("Input Error"+e);
				e.getMessage();
			}//catch
	}//load method
	public static void startExecution() {
		IC=0;
		executeUserProgram();
	}//end startExecution
	public static  void executeUserProgram() {	
		String opcode="";	
		boolean flag=true;
		byte oprand=0;
		try {
			//chking the memory
			System.out.println("Memory after load");
			for(int i=0;i<100;i++)
				for(int j=0;j<4;j++)
						System.out.print(M[i][j]+" ");
			while(flag){
				IR[0]=M[IC][0];
				IR[1]=M[IC][1];
				IR[2]=M[IC][2];
				IR[3]=M[IC][3];
				IC++;
				System.out.println("OPCODE IS  : "+(opcode=""+IR[0]+IR[1]));
				switch(opcode) {
					case "GD" : 
								SI='1';
								break;
					case "PD" :
								SI='2';
								break;
					case "LR" :
								//write memory  to Register
								System.out.println("oprand is : "+ (oprand=Byte.parseByte(""+IR[2]+IR[3])));
								System.out.print((R[0]=M[oprand][0]));
								System.out.print((R[1]=M[oprand][1]));
								System.out.print((R[2]=M[oprand][2]));
								System.out.println((R[3]=M[oprand][3]));
								break;
					case "SR" :
								//write memory  to Register
								System.out.println("oprand is : "+ (oprand=Byte.parseByte(""+IR[2]+IR[3])));
								System.out.print((M[oprand][0]=R[0]));
								System.out.print((M[oprand][1]=R[1]));
								System.out.print((M[oprand][2]=R[2]));
								System.out.println((M[oprand][3]=R[3]));
								break;
					case "CR" :
							//compare R amd MEMORY ofM[IR[3-4]] 
							System.out.println("oprand is : "+ (oprand=Byte.parseByte(""+IR[2]+IR[3])));
							if(R[0]==M[oprand][0] && R[1]==M[oprand][1] && R[2]==M[oprand][2] && R[3]==M[oprand][3])
								//set C valuse as false R==M[IR]
								C=true;
							else
								//set C valuse as false R!=M[IR]
								C=false;
							System.out.println(C);
								break;
					case "BT" :
								//check C =T
								if(C)
									//set IC=IR[3-4]
									System.out.println("IN BT  IC Change to is : "+ (IC=Byte.parseByte(""+IR[2]+IR[3])));
								break;
					case "H " :	SI='3';
								flag=false;
								break;
					default :
							throw new Exception("INCORRECT INSTRACTION IN INPUT FILE");
				}//end switchcase
				MOS.mosInit();	
				SI=0;
			}//loop
		}//try
		catch(Exception e) {
			System.out.println(e);
		}
	}//end executeUSer program
}//end class