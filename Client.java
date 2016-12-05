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
    private static String ip;
	private static InetAddress address;
    private static BufferedReader serverIn;
	private static DataOutputStream serverOut;
	private static Socket clientSocket;
	private static JLabel chatbox = new JLabel("Server Chat");
	
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
			
			//chatbox.setText("Welcome to the server");
			//chatbox.setVerticalTextPosition(chatbox.getVerticalTextPosition()+10);
			
			//start a temp JFrame that is jank and awkward, but it works
			JFrame frame = new JFrame();
			JOptionPane.showMessageDialog(frame, "Connected to " + address);
			
			while (true)
			{
				//init new Jframe for a new chat
				JFrame chatWindow = new JFrame();
				String chat;
				
				//when we get a new message from the server...
				chat = serverIn.readLine();
				//make a dialog window
				JOptionPane.showMessageDialog(chatWindow, chat);
				//chatbox.setText(chat);
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
