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
	
	private static final int MAX_USER = 100;
	private static BufferedReader[] clientIn = new BufferedReader[MAX_USER];
	private static DataOutputStream[] clientOut = new DataOutputStream[MAX_USER];
	private static String[] username = new String[MAX_USER];
	private static Socket[] connection = new Socket[MAX_USER];
	private static Thread[] messageHandler = new Thread[MAX_USER];
	private static int connectionNumber = 0; //inducates which users are connected, cell number indicates UID, 1=active, 0=nobody there
	private static int[] usedNumbers = new int[MAX_USER];
	private static PrintWriter log;
	
    //Start a server
    public static void main(String[] args) throws IOException
	{
        String clientMessage;
		String serverReply;
		
		//init usedNumbers
		for (int u = 0; u < MAX_USER; u++)
		{
			usedNumbers[u] = 0;
		}
        try
		{
			//open file for logging
			log = new PrintWriter("server.log", "UTF-8");
			
            //made a port
            ServerSocket accepting = new ServerSocket(2016); //port 2016, because why not
			
			//current thread will manage incoming users			
            while(true)
			{
				//when user connects
				System.out.println("Awaiting users to join\n");
				//accept their connection
                connection[connectionNumber] = accepting.accept();
				
				//one connected user will have their own thread, each thread will manage their incoming chat messages
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
				usedNumbers[connectionNumber] = 1;
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
		//to handle Control-C
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run()
			{
				log.close();
				for (int i = 0; i<MAX_USER; i++)
				{
					if (usedNumbers[i] == 1)
					{
						try
						{
							clientOut[i].writeBytes("Server closed\n");
						}
						catch (Exception e)
						{
							//System.out.println("Error when sending\n");
						}
					}
				}
			}
		});
		
		int thisThreadNum = connectionNumber;
		//System.out.println("In new thread #"+thisThreadNum);
		
		//init next chat message
		String msg = "";
		
		//start wait function for chat
		while (true)
		{
			try
			{
				//wait for new chat message
				msg = clientIn[thisThreadNum].readLine();
				
				//dont tell me when nobody is sending anything
				while(msg == "" || msg == null);
			}
			catch (Exception e)
			{
				try
				{
					Thread.sleep(100);
				}
				catch (Exception f)
				{
					//System.out.println("Sleep2 Failed.\n");
				}
			}
			
			//if there is a chat message
			if (msg.length() > 4)
			{
				//Do string operations: add timestamp
				long time = System.currentTimeMillis();
				Date date = new Date(time);
				DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
				String strTime = "[" + formatter.format(date) + "]";
					
				//... and it is indeed a chat message from a person
				if (msg.startsWith("/chat"))
				{
					//get the string of the chat
					msg = msg.substring(5);
					
					String fullMessage = strTime + " " + username[thisThreadNum] + ": " + msg + '\n';
					
					
					//print to log file outside of the for loop so it prints to file only once
					log.println(fullMessage);
								
					//send it to every active user/address/thread, what-have-you
					System.out.print(fullMessage);
					for (int i = 0; i<MAX_USER; i++)
					{
						if (usedNumbers[i] == 1)
						{
							try
							{
								//send it to all clients
								clientOut[i].writeBytes(fullMessage);
							}
							catch (Exception e)
							{
								System.out.println("Error when sending chat message\n");
							}
						}
					}

				}
				if (msg.startsWith("/quit"))
				{
					String quitMessage = strTime + " " + username[thisThreadNum] + " has left the server.\n";
					//print to log file once
					log.println(quitMessage);
					for (int i = 0; i<MAX_USER; i++)
					{
						if (usedNumbers[i] == 1)
						{
							try
							{	
								//deactivate User
								usedNumbers[thisThreadNum] = 0;
								
								//send it to all remaining clients
								clientOut[i].writeBytes(quitMessage);
								
								System.out.println("User #" + thisThreadNum + " to 0.");
								messageHandler[thisThreadNum].interrupt(); //stop the thread
							}
							catch (Exception e)
							{
								System.out.println("Error when sending quit message\n");
							}
						}
					}
				}
			}
			//reset the next message
			msg = "";
		}
	}
}
