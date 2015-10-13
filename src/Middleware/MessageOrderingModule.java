package Middleware;

import java.util.ArrayList;

/**
 * Created by oi12pjn on 2015-10-08.
 */
public class MessageOrderingModule{
    private ArrayList<GroupMessageQueue> groupMessageQueues;
    private VectorClock vectorClock;


    public MessageOrderingModule() {
        groupMessageQueues = new ArrayList<GroupMessageQueue>();
        vectorClock = new VectorClock();
    }

    public void order(String message) {


    }


    public void triggerSelfEvent(){
        vectorClock.triggerSelfEvent();
    }

    /**
     * returns the next message or null if there are no messages for the current group
     * @param groupName
     * @return Message
     */
    public Message getNextMessage(String groupName){
        for(GroupMessageQueue gmq : groupMessageQueues){
            if(gmq.getGroupName().equals(groupName) && gmq.getMessageQueue().size()>0){
                Message msg = gmq.getMessageQueue().get(0);
                gmq.getMessageQueue().remove(0);
                return msg;
            }

        }
        return null;
    }

    public void addGroup(String groupName){
        groupMessageQueues.add(new GroupMessageQueue(groupName));
    }

    public void orderMessage(String message, String sender, String groupName) {
        for(GroupMessageQueue gmq : groupMessageQueues){
            if(gmq.getGroupName().equals(groupName)){
                gmq.getMessageQueue().add(new Message(sender,message));
            }
        }
    }

    public VectorClock getVectorClock() {
        return vectorClock;
    }

    // NY MODUL REDO FÖR BUS
}
