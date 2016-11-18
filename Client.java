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
			serverOut.writeBytes(username + '\n');
            
			//Welcome message from server
			reply = serverIn.readLine();
			System.out.println(reply);

			(new Thread(new Client())).start();
			JFrame frame = new JFrame();
			JOptionPane.showMessageDialog(frame, "Connected to " + address);
			
			while (true)
			{
				JFrame chatWindow = new JFrame();
				String chat;
				chat = serverIn.readLine();
				JOptionPane.showMessageDialog(chatWindow, chat);
				
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
		String msg;
		while (true)
		{
			System.out.print("\n" + username + "(me): ");
			msg = System.console().readLine();
			try
			{
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
		if (inMessage.charAt(0) == '/')
		{
				clientSocket.close();
				System.out.println("CLIENT - Disconnected from server.");
				System.exit(0);
		}
		serverOut.writeBytes("/chat" + username + ": " + inMessage + '\n');
	}
}
