package com.vikas.input;
import java.io.*;
public class InputFile {
	public static BufferedReader inputFile=null;
	public  InputFile() {	
	}
	public static BufferedReader getFile(){
		try{
			if(inputFile==null)
				inputFile=new BufferedReader(new FileReader(new File("input1.txt")));
		}//try
		catch (Exception e) {
				System.out.println("Exception occurse"+e);
		}//catch
		return inputFile;
	}//getFile
}//class
