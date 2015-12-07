package Middleware;

/**
 * Created by c12jbr on 2015-11-02.
 *
 * A message stored in the debugger
 */
public class DebuggMessage {
    private String message;

    /**
     * Constructor
     * @param message
     */
    public DebuggMessage(String message){
        this.message = message;
    }

    /**
     * Returns the text message in  a debuggmessage
     * @return message
     */
    public String getMessage(){
        return message;
    }
}
