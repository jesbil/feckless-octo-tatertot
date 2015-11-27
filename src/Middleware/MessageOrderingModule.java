package Middleware;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by oi12pjn on 2015-10-08.
 */
public class MessageOrderingModule extends Observable{
    private final boolean unordered;
    private ArrayList<Message> holdBackQueue;
    private boolean paused;


    public MessageOrderingModule(boolean unordered) {
        paused = false;
        holdBackQueue = new ArrayList<>();
        this.unordered = unordered;
    }

    public void triggerSelfEvent(String groupName){
        GCom.getGroupByName(groupName).getVectorClock().triggerSelfEvent();
    }

    public void receiveMessage(Message message) {
        GCom.getDebuggLog().add(new DebuggMessage("Message from " + message.getSender().getName() + " put in holdbackqueue"));
        if(unordered){
            setChanged();
            notifyObservers(message);
        }else{
            holdBackQueue.add(message);
        }
    }

    public boolean allowedToDeliver(Message message) {
        if(paused){
            return false;
        }
        if(message.getGroup().getName().equals(GCom.getAllMembersGroupName())){
            return true;
        }
        if(GCom.getGroupByName(message.getGroup().getName()).getVectorClock().compare(message.getVectorClock(), message.getSender().getName())){
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

    public void pauseQueue() {
        paused = true;
    }

    public void startQueue() {
        paused = false;
        performNextIfPossible();
    }

    public void shuffleQueue() {
        Collections.shuffle(holdBackQueue);
    }

    public ArrayList<Message> getHoldBackQueue() {
        return holdBackQueue;
    }

    // NY MODUL REDO FÖR BUS största lögnen någonsinn
}
