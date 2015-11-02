package Middleware;

import Interface.Constants;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by oi12pjn on 2015-10-08.
 */
public class MessageOrderingModule extends Observable{
    private ArrayList<Message> holdBackQueue;



    public MessageOrderingModule() {
        holdBackQueue = new ArrayList<Message>();
    }

    public void triggerSelfEvent(String groupName){
        GCom.getGroupByName(groupName).getVectorClock().triggerSelfEvent();
    }

    public void receiveMessage(Message message) {
        System.out.println("Message from "+message.getSender().getName()+" put in holdbackqueue");
        holdBackQueue.add(message);
    }

    public boolean allowedToDeliver(Message message) {

        if(message.getVectorClock().compare(GCom.getGroupByName(message.getGroup().getName()).getVectorClock(),message.getSender().getName())){
            GCom.getGroupByName(message.getGroup().getName()).getVectorClock().mergeWith(message.getVectorClock());
            return true;
        }
        return false;
    }

    public void addToAllMembersClock(ArrayList<Member> members) {
        for(Member m: members){
            GCom.getGroupByName(GCom.getAllMembersGroupName()).getVectorClock().getClock().put(m.getName(), 0);
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
