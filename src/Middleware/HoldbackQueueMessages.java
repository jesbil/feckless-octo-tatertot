package Middleware;

import java.util.ArrayList;

/**
 * Created by c12jbr on 2015-11-02.
 * The hold back queue with all its messages
 */
public class HoldbackQueueMessages {
    private ArrayList<Message> messages;

    public  HoldbackQueueMessages(ArrayList<Message> messages) {
        this.messages = new ArrayList<>(messages);
    }

    public  String getMessage(int pos){
        return messages.get(pos).getSender().getName()+"@"+ messages.get(pos).getGroup().getName()+": "+ messages.get(pos).getMessage() + "VC: "+messages.get(pos).getGroup().getVectorClock().getClock();
    }

    public  int getSize(){
        return messages.size();
    }

}
