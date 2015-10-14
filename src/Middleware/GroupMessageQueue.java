package Middleware;

import java.util.ArrayList;

/**
 * Created by c12jbr on 2015-10-11.
 */
public class GroupMessageQueue {
    private ArrayList<Message> messageQueue;
    private String groupName;
    private ArrayList<Message> orderedUserMessages;

    public ArrayList<Message> getMessageQueue() {
        return messageQueue;
    }

    public String getGroupName() {
        return groupName;
    }



    public GroupMessageQueue(String groupName){
        messageQueue = new ArrayList<Message>();
        this.groupName = groupName;
        orderedUserMessages = new ArrayList<Message>();
    }

    public Message getNextUserMessage(){
        if(orderedUserMessages.size()==0){
            return null;
        }
        Message temp=orderedUserMessages.get(0);
        orderedUserMessages.remove(0);
        return temp;
    }

    public void setNextUserMessage(Message umsg){
        orderedUserMessages.add(umsg);
    }

}
