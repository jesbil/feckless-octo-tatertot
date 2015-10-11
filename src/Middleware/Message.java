package Middleware;

/**
 * Created by c12jbr on 2015-10-08.
 */
public class Message {
    private String sender;
    private String message;


    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public Message(String name, String message){
        this.sender = name;
        this.message = message;

    }
}
