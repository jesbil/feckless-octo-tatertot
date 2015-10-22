package Middleware;

import java.io.Serializable;

/**
 * Created by c12jbr on 2015-10-08.
 */
public class Message implements Serializable{
    private Group group;
    private Member sender;
    private String message;
    private int type;
    private VectorClock vectorClock;

    public int getType() {
        return type;
    }

    public Member getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public Group getGroup() {
        return group;
    }

    public VectorClock getVectorClock() {
        return vectorClock;
    }

    public Message(Member sender, String message, VectorClock vectorClock, Group group, int type){
        this.sender = sender;
        this.message = message;
        this.vectorClock = vectorClock;
        this.group = group;
        this.type = type;
    }

    @Override
    public boolean equals(Object obj){
        //TODO
        return false;
    }

    @Override
    public int hashCode(){
        return this.hashCode();
    }


}
