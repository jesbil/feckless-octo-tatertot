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
            System.out.println("EVENT TRIGGERED, ALLMEMBERS CLOCK:\n" + allMemberVectorClock.getClock().toString());
        }
        else{
            groupVectorClock.triggerSelfEvent();
            System.out.println("EVENT TRIGGERED, GROUP CLOCK:\n" + groupVectorClock.getClock().toString());
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

    public void orderMessage(String message, String sender, String groupName, HashMap<String,Integer> clockValue) {
        for(GroupMessageQueue gmq : groupMessageQueues){
            if(gmq.getGroupName().equals(groupName)){
                gmq.getMessageQueue().add(new Message(sender,message,clockValue,groupName));
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
//        switch (groupVectorClock.compare(vc,sender)){
//            case Constants.CLOCK_TYPE_EQ:
//                return true;
//                break;
//            case Constants.CLOCK_TYPE_LT:
//                return false;
//            break;
//        }
    }

    public void addToAllMembersClock(ArrayList<Member> members) {
        for(Member m: members){

            System.out.println("Member: "+m.getIP() + " added to allMembers");
            allMemberVectorClock.getClock().put(m.getIP(),0);

        }
    }

    public Message findNextMessage(String groupName, VectorClock vc, String sender) {
        return null;
    }

    public void acceptUserMessage(Message message) {
        for(GroupMessageQueue gmq : groupMessageQueues){
            if(gmq.getGroupName().equals(message.getGroupName())){
                gmq.setNextUserMessage(message);
            }
        }
    }

    // NY MODUL REDO FÖR BUS
}
