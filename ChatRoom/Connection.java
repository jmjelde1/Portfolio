/**
 * This is the separate thread that services each
 * incoming echo client request.
 *
 * @author Greg Gagne 
 */

import java.net.*;
import java.util.ArrayList;
import java.util.Vector;
import java.io.*;


public class Connection implements Runnable
{
	private Socket	client;
	private Vector<String> MessageQueue;
	private static Handler handler = new Handler();
	private ArrayList<BufferedWriter> Clients;
	
	public Connection(Socket client, Vector<String> messageQueue, ArrayList<BufferedWriter> clients) {
		this.client = client;
		MessageQueue = messageQueue;
		Clients = clients;
	}

    /**
     * This method runs in a separate thread.
     */	
	public void run() { 
		try {
			handler.process(client, MessageQueue, Clients);
		}
		catch (java.io.IOException ioe) {
			System.err.println(ioe);
		}
	}
}

