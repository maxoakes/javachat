import java.io.*;
import java.net.*;

/**
 * Example of creating a client and connection using TCP
 * 
 * @author Tammy VanDeGrift 
 * @version Fall 2016 (CS445)
 */
public class TCPClient {
    
    /**
     * creates a connection on the client side for sending data over TCP
     * reads from standard input stream and sends that data to the server
     * uses port 9999 on the localhost
     */
    public static void main(String[] args) throws IOException {
        // create Strings to store message and reply
        String message, reply;
        
        try {
            // create reader to acquire text
            BufferedReader userIn = new BufferedReader(
                new InputStreamReader(System.in));
        
            // get message from user
            message = userIn.readLine();
        
            // create socket connection on current machine at port 9999
            // and attach stream for writing data
            // connection is made to localhost (same machine), but could 
            // instead put in 32-bit or 128-bit unsigned number
            // for IP address or a string with the host name
            Socket clientSocket = new Socket("localhost", 9999);
            DataOutputStream serverOut = 
                new DataOutputStream(clientSocket.getOutputStream());
            
            // create stream for reading data from server
            BufferedReader serverIn = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
            
            // send data to server
            serverOut.writeBytes(message + '\n');
            // get data from server
            reply = serverIn.readLine();
            // print reply from server
            System.out.println("REPLY RECEIVED: " + reply);
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
