import java.time.*;

/**
 * A message class that contains:
 * a time stamp
 * username
 * string
 * 
 * @author Max Oakes, Justin Ohta
 * @version 1.0.0
 */
public class Message
{
    private Clock timestamp;
    private String username;
	private Sring message;
    
    public Message(String inTime, String inUsername, String inMessage)
    {
        timestamp = inTime;
        username = inUsername;
        message = inMessage;
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public String getStringTime()
    {
        return className;
    }
    
    public int getUsername()
    {
        return username;
    }
    
}