

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Util
{
	public static void saveFile(File f, String data)
	{
		BufferedWriter writer = null;
		
		try
		{
			if(!f.exists())
				f.createNewFile();
			
			writer = new BufferedWriter(new FileWriter(f));
			writer.write(data);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(writer != null)
			{
				try
				{
					writer.close();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public static String readFile(File f, boolean createIfNotExist)
	{
		StringBuilder builder = new StringBuilder();
		
		BufferedReader reader = null;
		
		try
		{
			if(!f.exists() && createIfNotExist)
				f.createNewFile();
			
			reader = new BufferedReader(new FileReader(f));
			String line;
			
			while((line = reader.readLine()) != null)
				builder.append(line).append("\r\n");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(reader != null)
			{
				try
				{
					reader.close();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		return builder.toString();
	}
}
