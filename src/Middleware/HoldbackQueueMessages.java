package Middleware;

import java.util.ArrayList;

/**
 * Created by c12jbr on 2015-11-02.
 */
public class HoldbackQueueMessages {
    private ArrayList<Message> messages;

    public HoldbackQueueMessages(ArrayList<Message> messages) {
        this.messages = new ArrayList<>(messages);
    }

    public String getMessage(int pos){
        return messages.get(pos).getSender()+"@"+ messages.get(pos).getGroup()+": "+ messages.get(pos).getMessage();
    }

    public int getSize(){
        return messages.size();
    }

}
