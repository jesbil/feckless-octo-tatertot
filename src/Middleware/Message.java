package Middleware;

import java.util.HashMap;

/**
 * Created by c12jbr on 2015-10-08.
 */
public class Message {
    private String groupName;
    private String sender;
    private String message;
    private HashMap<String,Integer> clockValue;


    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getGroupName() {
        return groupName;
    }

    public Message(String name, String message, HashMap<String,Integer> clockValue, String groupName){
        this.sender = name;
        this.message = message;
        this.clockValue = clockValue;
        this.groupName=groupName;

    }

    public HashMap<String, Integer> getClockValue() {
        return clockValue;
    }
}
