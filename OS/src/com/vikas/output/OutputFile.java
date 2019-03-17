package com.vikas.output;

import java.io.File;
import java.io.PrintWriter;
public class OutputFile {
	public static PrintWriter outputFile=null;
	public static PrintWriter getFile() {
		try {
			if(outputFile==null)
				outputFile=new PrintWriter("output.txt");
		}//try
		catch (Exception e) {
			System.out.println("Exception " +e);
		}//catch
		return outputFile;
	}//getFile
}//class