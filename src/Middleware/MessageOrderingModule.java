package Middleware;

import Interface.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by oi12pjn on 2015-10-08.
 */
public class MessageOrderingModule extends Observable{
    private ArrayList<Message> holdBackQueue;
    private VectorClock groupVectorClock;
    private VectorClock allMemberVectorClock;

    public VectorClock getAllMemberVectorClock() {
        return allMemberVectorClock;
    }

    public MessageOrderingModule() {
        holdBackQueue = new ArrayList<Message>();
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

    public void receiveMessage(Message message) {
        holdBackQueue.add(message);
    }

    public VectorClock getGroupVectorClock() {
        return groupVectorClock;
    }

    public boolean allowedToDeliver(Message message) {
        if(message.getGroup().getName().equals(GCom.getAllMembersGroupName())){
            if(allMemberVectorClock.compare(message.getVectorClock(),message.getSender().getName())==Constants.CLOCK_TYPE_EQ){
                return true;
            }
            return false;
        }else{
            if(groupVectorClock.compare(message.getVectorClock(),message.getSender().getName())==Constants.CLOCK_TYPE_EQ){
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


    public void performNextIfPossible() {

        for (int i = 0; i < holdBackQueue.size(); i++) {
            Message message = holdBackQueue.get(i);
            if(allowedToDeliver(message)){
                Message temp = new Message(message.getSender(),message.getMessage(),message.getVectorClock(),message.getGroup(),message.getType());
                holdBackQueue.remove(i);
                i--;
                setChanged();
                notifyObservers(temp);
            }
        }
    }

    public void addObserver(Observer observer) {
        super.addObserver(observer);
    }

    // NY MODUL REDO FÖR BUS största lögnen någonsinn
}
