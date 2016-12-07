import java.io.*;
import java.net.*;
import java.lang.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * A client that connects to a server on startup
 * Client must input a username when prompted
 * The client then connect to the server
 * The user can type messages and send them to the server,
 * and the user can wait and see messages from other users displayed in the terminal
 * 
 * @author Justin Ohta, Max Oakes
 * @version 1.0.0
 */
public class Client implements Runnable {
    
	private static String message;
	private static String reply;
	private static String username;
    private static String ip;
	private static InetAddress address;
    private static BufferedReader serverIn;
	private static DataOutputStream serverOut;
	private static Socket clientSocket;
	
	public static void main(String[] args) throws IOException
	{
 		
		//get the username
		System.out.print("\nUsername: ");
		username = System.console().readLine();
		
		//get address
		System.out.print("\nServer IP: ");
		ip = System.console().readLine();
		address = InetAddress.getByName(ip);
		
        try {
            // create reader to acquire text
            BufferedReader userIn = new BufferedReader(
                new InputStreamReader(System.in));
        
            //connect via address given, send username with it
            clientSocket = new Socket(address, 2016);
			
			//Read before you can write so no messages are lost when coming to you
			serverOut = new DataOutputStream(clientSocket.getOutputStream());
			serverIn = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
			
			//send the server your username
			serverOut.writeBytes(username + '\n');
            
			//Welcome message from server
			reply = serverIn.readLine();
			System.out.println(reply);

			//spawn new thread
			//this new one will manage you typing and chatting
			(new Thread(new Client())).start();
			
			//make an external window			
			//start a JFrame that will be used as the message log
			JFrame frame = new JFrame();
			
			//an area to add messages to the chat window
			JTextArea chatArea= new JTextArea("Chat: " + username + "@" + address + "\n");
			frame.setSize(500,700);
			chatArea.setEditable(false);			
			JScrollPane scroll = new JScrollPane (chatArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			frame.add(scroll);
			frame.setVisible(true);
			
			
			String message = "";
			//look for any messages sent by the server
			while (true)
			{
				String chat;
				
				//when we get a new message from the server...
				chat = serverIn.readLine();
				
				//update chat area
				chatArea.append(chat+"\n");
			}
			
        }
        catch(Exception e)
		{
            System.out.println("Exception in Thread #0");
        }
    }
	
	public void run()
	{
		//this manages any disconnects that the user might do
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run()
			{
				try
				{
					System.out.println("Program Closed");
					serverOut.writeBytes("/quit\n");
				}
				catch (Exception e)
				{
					System.out.println("end Message failed");
				}
			}
		});
		
		//ready your next message
		String msg;
		while (true)
		{
			//an initial prompt for the user
			System.out.print("\n" + username + "(me): ");
			
			//get input chat message
			msg = System.console().readLine();
			try
			{
				//send the message to the server
				sendMessage(msg);
			}
			catch (Exception e)
			{
				//System.out.println("Message failed to send");
			}
		}
    }
	
	public void sendMessage(String inMessage) throws IOException
	{
		//if it is a command message, assume it is /quit and exit the server, and tells the server that they are leaving
		if (inMessage.charAt(0) == '/')
		{
				clientSocket.close();
				System.exit(1);
		}
		//if it not a command, send the message to the server
		serverOut.writeBytes("/chat" + inMessage + '\n');
	}
}
