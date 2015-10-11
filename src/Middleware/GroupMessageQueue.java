package Middleware;

import java.util.ArrayList;

/**
 * Created by c12jbr on 2015-10-11.
 */
public class GroupMessageQueue {
    private ArrayList<Message> messageQueue;
    private String groupName;

    public ArrayList<Message> getMessageQueue() {
        return messageQueue;
    }

    public String getGroupName() {
        return groupName;
    }



    public GroupMessageQueue(String groupName){
        messageQueue = new ArrayList<Message>();
        this.groupName = groupName;
    }

}
