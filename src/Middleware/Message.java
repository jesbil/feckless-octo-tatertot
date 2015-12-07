package Middleware;

import java.io.Serializable;

/**
 * Created by c12jbr on 2015-10-08.
 *
 * A message that can be sent through GCom
 */
public class Message implements Serializable{
    private Group group;
    private Member sender;
    private String message;
    private int type;

    /**
     *
     * @return type
     */
    public int getType() {
        return type;
    }

    /**
     *
     * @return sender
     */
    public Member getSender() {
        return sender;
    }

    /**
     *
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @return group
     */
    public Group getGroup() {
        return group;
    }

    /**
     * Constructor
     * @param sender
     * @param message
     * @param group
     * @param type
     */
    public Message(Member sender, String message, Group group, int type){
        this.sender = sender;
        this.message = message;
        this.group = group;
        this.type = type;
    }

    @Override
    /**
     * Equals method, a message can never be equal to another message
     */
    public boolean equals(Object obj){
        return false;
    }

    @Override
    /**
     *  Nice hashcode brah
     */
    public int hashCode(){
        return (type*2)+7;
    }


}
