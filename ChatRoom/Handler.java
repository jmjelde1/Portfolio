/**
 * @author Joachim & Thor
 */

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Vector;

public class Handler 
{
	public static final int BUFFER_SIZE = 256;
	
	/**
	 * this method is invoked by a separate thread
	 */
	public void process(Socket client, Vector<String> messageQueue, ArrayList<BufferedWriter> clients) throws java.io.IOException {
		
		try{
			BufferedReader c = new BufferedReader(new InputStreamReader(client.getInputStream()));

			String line;

			while ( (line = c.readLine()) != null)
			{
				if(line.contains("LEAVE")){
					clients.remove(client);
				}
				messageQueue.add(line);
			} 

		} 
		catch (java.io.IOException ioe) 
		{
			System.err.println(ioe);
		}
		// finally
		// {
		// 	client.close();
		// }	
	}
}