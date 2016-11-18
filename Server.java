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
			//this new thread will manage incoming chat
			(new Thread(new Server())).start();
			
			//current thread will manage incoming users
			System.out.println("SERVER - Thread Spawned, in main thread now\n");
			
            while(true)
			{
				//when user connects
				System.out.println("Awaiting users to join\n");
				//accept their connection
                connection[connectionNumber] = accepting.accept();
				
				//get a stream from client to this server
				clientIn = new BufferedReader(new InputStreamReader(connection[connectionNumber].getInputStream()));
				username[connectionNumber] = clientIn.readLine();
				System.out.println("User Connected: " + username[connectionNumber] + " from " + connection[connectionNumber].getInetAddress() + "\n");
				
				//get a stream from server to client
                clientOut = new DataOutputStream(connection[connectionNumber].getOutputStream());
				
				//send welcome message
				String welcome = "Welcome to the server, " + username[connectionNumber] + "!\n";
				clientOut.writeBytes(welcome);
				
				//iterate connection for new user
				//connectionNumber++;
            }
        }
        catch (Exception e)
		{
            System.out.println("An error occurred while creating server socket or reading/writing data to/from client.\n");
        }
    }
	
	//Function for second thread
	//Its job is to manage incoming messages
	public void run()
	{
		//init next chat message
		String msg = "";
		
		//start wait function for chat
		while (true)
		{
			try
			{
				//wait for new chat message
				msg = clientIn.readLine();
				
				//System.out.println(msg+"\n");
				
				//dont tell me when nobody is sending anything
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
			
			//if there is a chat message
			if (msg.length() > 5)
			{
				//... and it is indeed a chat message from a person
				if (msg.startsWith("/chat"))
				{
					//get the string of the chat
					msg = msg.substring(5);
					
					//print it so the server (and later a log)
					System.out.println(msg);
					try
					{
						//send it to all clients (currently just one client)
						clientOut.writeBytes(msg+'\n');
						//System.out.println("DEBUG - MSG SENT: "+ msg + "\n");
					}
					catch (Exception e)
					{
						System.out.println("Error when sending\n");
					}
				}
			}
			//reset the next message
			msg = "";
		}
	}
}
