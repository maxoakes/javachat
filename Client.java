import java.io.*;
import java.net.*;

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
public class Client {
    
    public static void main(String[] args) throws IOException {
        // create Strings to store message and reply
        String message;
		String reply;
		String username;
        String address;
		
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
            Socket clientSocket = new Socket(address, 2016);
			
			//Read before you can write so no messages are lost when coming to you
			DataOutputStream serverOut = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader serverIn = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
			serverOut.writeBytes(username + '\n');
            reply = serverIn.readLine();
			System.out.println(reply);
            // create stream for reading data from server

            
            // send data to server
            //serverOut.writeBytes(message + '\n');
            // get data from server
            reply = serverIn.readLine();
            // print reply from server
            //System.out.println("REPLY RECEIVED: " + reply);
            clientSocket.close();
        }
        catch(ConnectException e) {
            System.out.println("Error connecting to server. Check that server is running and accepting connections.");
        }
        catch(Exception e) {
            System.out.println("Exception occurred: " + e.getMessage());
        }
    }
}
