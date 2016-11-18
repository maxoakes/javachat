import java.io.*;
import java.net.*;
import java.time.*;
import java.lang.*;

/**
 * A server that manages a multi-user chat program
 * This server takes strings that are read from internet users
 * attaches a timestamp and username with it, and sends it to all clients connected
 * Will also print the full message to a file on server's computer
 * 
 * @author Max Oakes, Justin Ohta
 * @version 1.0.0
 */
public class Server implements Runnable{
	
	private static final int MAX_USER = 50;
	private static BufferedReader clientIn;
	private static DataOutputStream clientOut;
	
    //Start a server
    public static void main(String[] args) throws IOException
	{
        String clientMessage;
		String serverReply;
		
		String[] username = new String[MAX_USER]; //hard cap, if connectionNumber goes above it, it'll break
		
        Socket[] connection = new Socket[MAX_USER];
		int connectionNumber = 0;
		
        try
		{
            //made a port
            ServerSocket accepting = new ServerSocket(2016); //port 2016, because why not
            
            // wait for clients to make connections
			(new Thread(new Server())).start();
			System.out.println("SERVER - Thread Spawned, in main thread now\n");
			
            while(true)
			{
				//when user connects
				System.out.println("Awaiting users to join\n");
                connection[connectionNumber] = accepting.accept();
				clientIn = new BufferedReader(new InputStreamReader(connection[connectionNumber].getInputStream()));
				username[connectionNumber] = clientIn.readLine();
				System.out.println("User Connected: " + username[connectionNumber] + " from " + connection[connectionNumber].getInetAddress() + "\n");
				
				//Ready to send that new user messages
                clientOut = new DataOutputStream(connection[connectionNumber].getOutputStream());
				String welcome = "Welcome to the server, " + username[connectionNumber] + "!\n";
				clientOut.writeBytes(welcome);
				//connectionNumber++;
				
                // get message from client and captilatize the letters
                //clientMessage = clientIn.readLine();
                //System.out.println("Got the message: " + clientMessage);
                //serverReply = "Ack for client at " + connectionSocket.getInetAddress() + ": " + clientMessage.toUpperCase() + "\n";

		            // send client reply 
                //clientOut.writeBytes(serverReply);
                //connection[connectionNumber].close();
            }
        }
        catch (Exception e)
		{
            System.out.println("An error occurred while creating server socket or reading/writing data to/from client.\n");
        }
    }
	
	public void run()
	{
		String msg = "";
		while (true)
		{
			try
			{
				msg = clientIn.readLine();
				//System.out.println(msg+"\n");
				while(msg == "" || msg == null);
				/*
				{
					try
					{
						Thread.sleep(500);
					}
					catch (Exception e)
					{
						System.out.println("Sleep Failed.\n");
					}
				}
				*/
			}
			catch (Exception e)
			{
				try
				{
					Thread.sleep(100);
				}
				catch (Exception f)
				{
					System.out.println("Sleep2 Failed.\n");
				}
			}
			
			if (msg.length() > 5)
			{
				if (msg.startsWith("/chat"))
				{
					msg = msg.substring(5);
					System.out.println(msg);
					try
					{
						clientOut.writeBytes(msg+'\n');
						//System.out.println("DEBUG - MSG SENT: "+ msg + "\n");
					}
					catch (Exception e)
					{
						System.out.println("Error when sending\n");
					}
				}
			}
			msg = "";
		}
	}
}
