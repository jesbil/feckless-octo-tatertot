package Middleware;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by oi12pjn on 2015-10-08.
 * Handles the ordering of messages in GCom.
 * Can do causal ordering
 */
public class MessageOrderingModule extends Observable{
    private final boolean unordered;
    private ArrayList<Message> holdBackQueue;
    private boolean paused;

    /**
     * Constructor
     * @param unordered
     */
    protected MessageOrderingModule(boolean unordered) {
        paused = false;
        holdBackQueue = new ArrayList<>();
        this.unordered = unordered;
    }

    /**
     * Triggers an event in a vector clock from a group
     * @param groupName
     */
    protected void triggerSelfEvent(String groupName){
        GCom.getGroupByName(groupName).getVectorClock().triggerSelfEvent();
    }

    /**
     * Puts a message in a hold back queue if unordered, else send the message to GCom
     * @param message
     */
    protected void receiveMessage(Message message) {
        GCom.getDebuggLog().add(new DebuggMessage("Message from " + message.getSender().getName() + " put in holdbackqueue"));
        if(unordered){
            setChanged();
            notifyObservers(message);
        }else{
            holdBackQueue.add(message);
        }
    }

    /**
     * Checks if a message can be delivered (Causal ordering)
     *
     * @param message
     * @return
     */
    protected boolean allowedToDeliver(Message message) {
        if(paused){
            return false;
        }

        if(GCom.getGroupByName(message.getGroup().getName()).getVectorClock().compare(message.getGroup().getVectorClock(), message.getSender().getName())){
            return true;
        }
        return false;
    }

    /**
     * Sends the next message to GCom if allowed
     */
    protected void performNextIfPossible() {

        for (int i = 0; i < holdBackQueue.size(); i++) {
            Message message = holdBackQueue.get(i);
            System.out.println(GCom.getLocalMember().getName()+ " Jag är lokal");
            System.out.println(message.getSender().getName()+ " sender");
            System.out.println(GCom.getGroupByName(message.getGroup().getName()).getVectorClock().getClock().toString() + " min klocka före trigger");
            System.out.println(message.getGroup().getVectorClock().getClock().toString() + " klocka i meddelande");
            if(allowedToDeliver(message)){
                Message temp = new Message(message.getSender(),message.getMessage(),message.getGroup(),message.getType());
                holdBackQueue.remove(i);
                i--;
                if(!GCom.getLocalMember().getName().equals(message.getSender().getName())){
                    GCom.getGroupByName(message.getGroup().getName()).getVectorClock().triggerEvent(message.getSender().getName());
                }
                System.out.println(GCom.getGroupByName(message.getGroup().getName()).getVectorClock().getClock().toString() + " min klocka");
                setChanged();
                notifyObservers(temp);
            }
        }
    }

    /**
     * Adds an observer
     * @param observer
     */
    public void addObserver(Observer observer) {
        super.addObserver(observer);
    }

    /**
     * pauses the hold back queue
     */
    protected void pauseQueue() {
        paused = true;
    }

    /**
     * Starts the hold back queue
     */
    protected void startQueue() {
        paused = false;
        performNextIfPossible();
    }

    /**
     * Shuffle the hold back queue
     */
    protected void shuffleQueue() {
        Collections.shuffle(holdBackQueue);
    }

    /**
     *
     * @return the hold back queue
     */
    protected ArrayList<Message> getHoldBackQueue() {
        return holdBackQueue;
    }

    // NY MODUL REDO FÖR BUS största lögnen någonsinn
}
