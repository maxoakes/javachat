import java.io.*;
import java.net.*;
import java.lang.*;
import javax.swing.*;
import java.awt.*;

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
    private static String address;
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
		address = System.console().readLine();
		
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
			
			//start a JFrame that will be used as the message log
			JFrame frame = new JFrame();
			
			//an area to add messages to the chat window
			JLabel label = new JLabel();
			frame.setSize(300,400);
			label.setText("<html>" + "Connected to " + address + "</html>");
			frame.add(label);
			frame.setVisible(true);
			
			//initial welcoming message for the user
			String message = "Welcome";

			
			while (true)
			{
				String chat;
				
				//when we get a new message from the server...
				chat = serverIn.readLine();
				
				//sets the text in the label to show each message sent
				label.setText("<html>" + message + "<br>" + chat + "<br>" + "</html>");
				
				//allows chat to update, putting a new message under the ones that came before it
				//basically allowing to log new messages
				message = message + "<br>" + chat;
			}
			
        }
        catch(ConnectException e)
		{
            System.out.println("Error connecting to server. Check that server is running and accepting connections.");
        }
        catch(Exception e)
		{
            System.out.println("Chat client closed.");
        }
    }
	
	public void run()
	{
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
				System.out.println("Message failed");
			}
		}
    }
	
	public void sendMessage(String inMessage) throws IOException
	{
		//if it is a command message, assume it is /quit and exit the server
		if (inMessage.charAt(0) == '/')
		{
				clientSocket.close();
				System.out.println("CLIENT - Disconnected from server.");
				System.exit(0);
		}
		//if it not a command, send the message to the server
		serverOut.writeBytes("/chat" + username + ": " + inMessage + '\n');
	}
}
