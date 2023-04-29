/**
 * This program is a rudimentary demonstration of Swing GUI programming.
 * Note, the default layout manager for JFrames is the border layout. This
 * enables us to position containers using the coordinates South and Center.
 *
 * Usage:
 *	java ChatScreen
 *
 * When the user enters text in the textfield, it is displayed backwards 
 * in the display area.
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.net.*;


public class ChatScreen extends JFrame implements ActionListener, KeyListener
{
	private JButton sendButton;
	private JButton exitButton;
	private JTextField sendText;
	private JTextArea displayArea;
	private static String username = "";
	static BufferedWriter toServer;

	public ChatScreen() {

		/**
		 * a panel used for placing components
		 */
		JPanel p = new JPanel();

		Border etched = BorderFactory.createEtchedBorder();
		Border titled = BorderFactory.createTitledBorder(etched, "Message Chatroom");
		/*p.setBorder(titled);
		p.setBackground(new Color(19, 19, 19));
		p.setForeground(Color.white);*/

		/**
		 * set up all the components
		 */
		sendText = new JTextField(30);
		sendButton = new JButton(">");
		exitButton = new JButton("Exit");

		/**
		 * register the listeners for the different button clicks
		 */
		sendText.addKeyListener(this);
		sendButton.addActionListener(this);
		exitButton.addActionListener(this);

		/**
		 * add the components to the panel
		 */
		p.add(sendText);
		p.add(sendButton);
		p.add(exitButton);

		/**
		 * add the panel to the "south" end of the container
		 */
		getContentPane().add(p,"South");

		/**
		 * add the text area for displaying output. Associate
		 * a scrollbar with this text area. Note we add the scrollpane
		 * to the container, not the text area
		 */
		displayArea = new JTextArea(15,40);
		displayArea.setEditable(false);
		displayArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
		//displayArea.setBackground(Color.black);
		//displayArea.setForeground(Color.white);

		JScrollPane scrollPane = new JScrollPane(displayArea);
		getContentPane().add(scrollPane,"Center");

		/**
		 * set the title and size of the frame
		 */
		setTitle("CRP Chatroom");
		pack();

		setVisible(true);
		sendText.requestFocus();

		/** anonymous inner class to handle window closing events */
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				System.exit(0);
			}
		} );

	}

	/**
	 * Displays a message
	 */
	public void displayMessage(String message) {
 
		if(message.contains("JOIN")){
			int startUser = message.indexOf(' ');
			String user = message.substring(startUser+1, message.length());

			displayArea.append("-----> " + user + " has entered the chat\n");
		}
		if(message.contains("SEND")){
			int startUser = message.indexOf(' ');
			int endUser = message.indexOf(' ', startUser+1);

			String user = message.substring(startUser+1, endUser);
			String mess = message.substring(endUser+1, message.length());

			displayArea.append(user + ":" + mess + "\n");
		} 
		else if(message.contains("CRP1.0LEAVE")){
			int startUser = message.indexOf(' ');
			String user = message.substring(startUser+1, message.length());

			displayArea.append("<----- " + user + " has left the chat\n");
		}
	}

	/**
	 * This gets the text the user entered and outputs it
	 * in the display area.
	 */
	public void displayText() {
		String message = sendText.getText().trim();

		String mess = "CRP1.0SEND " + username + " " + message + "\r\n";


		try{toServer.write(mess);
			toServer.flush();}
		catch(Exception e){System.out.println(e);}

        sendText.setText("");
        sendText.requestFocus();
	}


	/**
	 * This method responds to action events .... i.e. button clicks
	 * and fulfills the contract of the ActionListener interface.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();

        if (source == sendButton) 
            displayText();
        else if (source == exitButton){
			try{toServer.write("CRP1.0LEAVE " + username + "\r\n");
				toServer.flush();}
			catch(Exception e){System.out.println(e);}
			System.exit(0);
		}   
	}

	/**
	 * These methods responds to keystroke events and fulfills
	 * the contract of the KeyListener interface.
	 */

	/**
	 * This is invoked when the user presses
	 * the ENTER key.
	 */
	public void keyPressed(KeyEvent e) { 
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		displayText();
	}

	/** Not implemented */
	public void keyReleased(KeyEvent e) { }

	/** Not implemented */
	public void keyTyped(KeyEvent e) { }

	public static void main(String[] args) {
		try {
			Socket server = new Socket(args[0], 15001); //port 15001
			ChatScreen win = new ChatScreen();
			username = args[1];
			toServer = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
			String joinReq = "CRP1.0JOIN " + username + "\r\n";

			toServer.write(joinReq);
			toServer.flush();

			Thread ReaderThread = new Thread(new ReaderThread(server, win));

			ReaderThread.start();
		}
		catch (UnknownHostException uhe) { System.out.println(uhe); }
		catch (IOException ioe) { System.out.println(ioe); }


	}
}
