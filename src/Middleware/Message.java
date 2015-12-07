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


    public Message(Member sender, String message, Group group, int type){
        this.sender = sender;
        this.message = message;
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
        return (type*2)+7;
    }


}
