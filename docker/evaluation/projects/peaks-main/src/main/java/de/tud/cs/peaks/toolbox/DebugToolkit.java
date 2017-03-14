package de.tud.cs.peaks.toolbox;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class DebugToolkit {
	
	private static LinkedList<String> output = new LinkedList<String>();
	
	public static void add(String s)
	{
		output.add(s);
	}
	
	public static void saveAll()
	{
		try 
		{
			BufferedWriter out = new BufferedWriter(new FileWriter("sootOutput/ReflectionOutput.txt"));
			for(String s: output)
			{
				out.write(s);
				out.newLine();
			}
        	out.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

}
