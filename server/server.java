import java.io.*;
import java.net.*;

/**
 * example of creating a server receiving/sending data over TCP.
 * process must be explicitly stopped to shut down server
 * 
 * @author Tammy VanDeGrift
 * @version Fall 2016 (CS445)
 */
public class TCPServer {
    
    /** starts a server that receives/sends data over TCP.
     * uses port 9999 for communication
     */
    public static void main(String[] args) throws IOException {
        String clientMessage, serverReply;
        
        try {
            // create socket connection to port 9999
            ServerSocket accepting = new ServerSocket(9999);
            
            // wait for clients to make connections
            while(true) {
                Socket connectionSocket = accepting.accept();
                BufferedReader clientIn = new BufferedReader(
                    new InputStreamReader(connectionSocket.getInputStream()));
                
                DataOutputStream clientOut = 
                    new DataOutputStream(connectionSocket.getOutputStream());
                
                // get message from client and captilatize the letters
                clientMessage = clientIn.readLine();
                System.out.println("Got the message: " + clientMessage);
                serverReply = "Ack for client at " + connectionSocket.getInetAddress() + ": " + clientMessage.toUpperCase() + "\n";

		            // send client reply 
                clientOut.writeBytes(serverReply);
                connectionSocket.close();
            }
        }
        catch (Exception e) {
            System.out.println("An error occurred while creating server socket or reading/writing data to/from client.");
        }
    }
}
