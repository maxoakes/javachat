import java.io.*;
import java.net.*;
import java.time.*;

/**
 * A server that manages a multi-user chat program
 * This server takes strings that are read from internet users
 * attaches a timestamp and username with it, and sends it to all clients connected
 * Will also print the full message to a file on server's computer
 * 
 * @author Max Oakes, Justin Ohta
 * @version 1.0.0
 */
public class Server {
	
	private static final int MAX_USER = 50;
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
            while(true)
			{
				//when user connects
                connection[connectionNumber] = accepting.accept();
				BufferedReader clientIn = new BufferedReader(new InputStreamReader(connection[connectionNumber].getInputStream()));
				username[connectionNumber] = clientIn.readLine();
				System.out.println("User Connected: " + username[connectionNumber] + " from " + connection[connectionNumber].getInetAddress());
				//Ready to send that new user messages
                DataOutputStream clientOut = new DataOutputStream(connection[connectionNumber].getOutputStream());
				System.out.println("FLAG");
				String welcome = "Welcome to the server, " + username[connectionNumber] + "!\n";
				clientOut.writeBytes(welcome);
				//connectionNumber++;
				
                // get message from client and captilatize the letters
                //clientMessage = clientIn.readLine();
                //System.out.println("Got the message: " + clientMessage);
                //serverReply = "Ack for client at " + connectionSocket.getInetAddress() + ": " + clientMessage.toUpperCase() + "\n";

		            // send client reply 
                //clientOut.writeBytes(serverReply);
                connection[connectionNumber].close();
            }
        }
        catch (Exception e)
		{
            System.out.println("An error occurred while creating server socket or reading/writing data to/from client.");
        }
    }
}
