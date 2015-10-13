package Middleware;

import Interface.Constants;

import java.util.ArrayList;

/**
 * Created by oi12pjn on 2015-10-08.
 */
public class MessageOrderingModule{
    private ArrayList<GroupMessageQueue> groupMessageQueues;
    private VectorClock groupVectorClock;

    public VectorClock getAllMemberVectorClock() {
        return allMemberVectorClock;
    }

    private VectorClock allMemberVectorClock;



    public MessageOrderingModule() {
        groupMessageQueues = new ArrayList<GroupMessageQueue>();
        groupVectorClock = new VectorClock();
    }

    public void triggerSelfEvent(){
        groupVectorClock.triggerSelfEvent();
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

    public VectorClock getGroupVectorClock() {
        return groupVectorClock;
    }

    public boolean receiveCompare(VectorClock vc, String sender) {
        if(groupVectorClock.compare(vc,sender)==Constants.CLOCK_TYPE_EQ){
            return true;
        }
        return false;
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
            allMemberVectorClock.getClock().put(m.getIP(),0);
        }
    }

    // NY MODUL REDO FÖR BUS
}
