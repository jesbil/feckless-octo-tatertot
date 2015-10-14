package Middleware;

import Interface.Constants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by oi12pjn on 2015-10-08.
 */
public class MessageOrderingModule{
    private ArrayList<GroupMessageQueue> groupMessageQueues;
    private VectorClock groupVectorClock;
    private VectorClock allMemberVectorClock;

    public VectorClock getAllMemberVectorClock() {
        return allMemberVectorClock;
    }



    public MessageOrderingModule() {
        groupMessageQueues = new ArrayList<GroupMessageQueue>();
        groupMessageQueues.add(new GroupMessageQueue(GCom.getAllMembersGroupName()));
        groupVectorClock = new VectorClock();
        allMemberVectorClock = new VectorClock();
    }

    public void triggerSelfEvent(boolean toAllMembers){

        if(toAllMembers){
            allMemberVectorClock.triggerSelfEvent();
            GCom.getDebuggLog().add("EVENT TRIGGERED IN ALLMEMBERS CLOCK: " + allMemberVectorClock.getClock().toString());
        }
        else{
            groupVectorClock.triggerSelfEvent();
            GCom.getDebuggLog().add("EVENT TRIGGERED IN GROUP CLOCK: " + groupVectorClock.getClock().toString());
        }
    }

    /**
     * returns the next message or null if there are no messages for the current group
     * @param groupName
     * @return Message
     */
    public Message getNextUserMessage(String groupName){
        for(GroupMessageQueue gmq : groupMessageQueues) {
            if(gmq.getGroupName().equals(groupName)) {
                return gmq.getNextUserMessage();
            }
        }
        return null;
    }

    public void addGroup(String groupName){
        groupMessageQueues.add(new GroupMessageQueue(groupName));
    }

    public void orderMessage(String message, String sender, String groupName, HashMap<String,Integer> clockValue, int type) {
        for(GroupMessageQueue gmq : groupMessageQueues){
            if(gmq.getGroupName().equals(groupName)){
                gmq.getMessageQueue().add(new Message(sender,message,clockValue,groupName,type));
            }
        }
    }

    public VectorClock getGroupVectorClock() {
        return groupVectorClock;
    }

    public boolean receiveCompare(String groupName,VectorClock vc, String sender) {
        if(groupName.equals(GCom.getAllMembersGroupName())){
            if(allMemberVectorClock.compare(vc,sender)==Constants.CLOCK_TYPE_EQ){
                return true;
            }
            return false;
        }else{
            if(groupVectorClock.compare(vc,sender)==Constants.CLOCK_TYPE_EQ){
                return true;
            }
            return false;

        }
    }

    public void addToAllMembersClock(ArrayList<Member> members) {
        for(Member m: members){
            allMemberVectorClock.getClock().put(m.getIP(),0);

        }
    }

    public void acceptUserMessage(Message message) {
        for(GroupMessageQueue gmq : groupMessageQueues){
            if(gmq.getGroupName().equals(message.getGroupName())){
                gmq.setNextUserMessage(message);
            }
        }
    }

    public void performNextIfPossible() {
        for(GroupMessageQueue gmq : groupMessageQueues){
            for (int i = 0; i < gmq.getMessageQueue().size(); i++) {
                Message msg = gmq.getMessageQueue().get(i);
                if(receiveCompare(msg.getGroupName(), new VectorClock(msg.getClockValue()), msg.getSender())){
                    switch (msg.getType()){
                        case Constants.TYPE_REMOVE_GROUP:
                            GCom.groupRemoved(msg.getGroupName(),new VectorClock(msg.getClockValue()),msg.getSender());
                            break;
                        case Constants.TYPE_CREATE_GROUP:
                            GCom.groupCreated(msg.getGroupName(), msg.getSender(), new VectorClock(msg.getClockValue()));
                            break;
                        case Constants.TYPE_JOIN_GROUP:
                            GCom.groupJoined(msg.getSender(),GCom.getAllMembersGroupName(), msg.getGroupName(), new VectorClock(msg.getClockValue()));
                            break;
                        case Constants.TYPE_LEAVE_GROUP:
                            GCom.leftGroup(msg.getGroupName(),msg.getSender(),new VectorClock(msg.getClockValue()));
                            break;
                        case Constants.TYPE_MESSAGE:
                            GCom.receiveMessage(msg.getMessage(),msg.getSender(),msg.getGroupName(),new VectorClock(msg.getClockValue()));

                    }
                }
            }
        }
    }

    // NY MODUL REDO FÖR BUS största lögnen någonsinn
}
