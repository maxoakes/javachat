import java.io.*;
import java.net.*;
import java.time.*;
import java.lang.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
	private static BufferedReader[] clientIn = new BufferedReader[MAX_USER];
	private static DataOutputStream[] clientOut = new DataOutputStream[MAX_USER];
	private static String[] username = new String[MAX_USER];
	private static Socket[] connection = new Socket[MAX_USER];
	private static Thread[] messageHandler = new Thread[MAX_USER];
	private static int connectionNumber = 0;
	private static PrintWriter log;
	
    //Start a server
    public static void main(String[] args) throws IOException
	{
        String clientMessage;
		String serverReply;
		
        try
		{
			//open file for logging
			log = new PrintWriter("server.log", "UTF-8");
			
            //made a port
            ServerSocket accepting = new ServerSocket(2016); //port 2016, because why not
            
            // wait for clients to make connections
			//this new thread will manage incoming chat
			//(new Thread(new Server())).start();
			
			//current thread will manage incoming users
			System.out.println("SERVER - Thread Spawned, in main thread now\n");
			
            while(true)
			{
				//when user connects
				System.out.println("Awaiting users to join\n");
				//accept their connection
                connection[connectionNumber] = accepting.accept();
				messageHandler[connectionNumber] = new Thread(new Server());
				messageHandler[connectionNumber].start();
				
				//get a stream from client to this server
				clientIn[connectionNumber] = new BufferedReader(new InputStreamReader(connection[connectionNumber].getInputStream()));
				username[connectionNumber] = clientIn[connectionNumber].readLine();
				System.out.println("User Connected: " + username[connectionNumber] + " from " + connection[connectionNumber].getInetAddress() + "\n");
				
				//get a stream from server to client
                clientOut[connectionNumber] = new DataOutputStream(connection[connectionNumber].getOutputStream());
				
				//send welcome message
				String welcome = "Welcome to the server, " + username[connectionNumber] + "!\n";
				clientOut[connectionNumber].writeBytes(welcome);
				
				//iterate connection for new user
				connectionNumber++;
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
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run()
			{
				log.close();
				System.out.println("Server closed");
				for (int i = 0; i<connectionNumber; i++)
				{
					try
					{
						clientOut[i].writeBytes("Server closed\n");
					}
					catch (Exception e)
					{
						System.out.println("Error when sending\n");
					}
				}
			}
		});
		
		int thisThreadNum = connectionNumber;
		System.out.println("In new thread #"+thisThreadNum);
		//init next chat message
		String msg = "";
		
		//start wait function for chat
		while (true)
		{
			try
			{
				//wait for new chat message
				msg = clientIn[thisThreadNum].readLine();
				
				//full message and message type has first chars, /chat for example
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
					
					//Do string operations: add timestamp
					long time = System.currentTimeMillis();
					Date date = new Date(time);
					DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
					//System.out.println(formatter.format(date));
					
					String fullMessage = "[" + formatter.format(date) + "] " + msg + '\n';
					
					//print it so the server (and later a log)
					System.out.println(fullMessage);
					for (int i = 0; i<connectionNumber; i++)
					{
						try
						{
							//send it to all clients (currently just one client)
							clientOut[thisThreadNum].writeBytes(fullMessage);
							
							//print to log file
							
							log.println(fullMessage);
							//System.out.println("DEBUG - MSG SENT: "+ msg + "\n");
						}
						catch (Exception e)
						{
							System.out.println("Error when sending\n");
						}
					}

				}
			}
			//reset the next message
			msg = "";
		}
	}
}
