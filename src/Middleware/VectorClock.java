package Middleware;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by c12jbr on 2015-10-13.
 *
 * Implementation of a vector clock for causal ordering
 */
public class VectorClock implements Serializable{
    private HashMap<String,Integer> clockValue;

    /**
     * creates a vector clock and puts the local member into it
     */
    public VectorClock() {
        clockValue = new HashMap<>();
        clockValue.put(GCom.getLocalMember().getName(),0);
    }

    /**
     * increases the local members vector value by one.
     */
    public void triggerSelfEvent() {
        int value = clockValue.get(GCom.getLocalMember().getName());
        value++;
        clockValue.put(GCom.getLocalMember().getName(),value);
    }

    /**
     * increses the vector value of a sender
     * @param sender - member name
     */
    public void triggerEvent(String sender) {
        int value = clockValue.get(sender);
        value++;
        clockValue.put(sender,value);
    }

    /**
     *
     * @return the clock
     */
    public HashMap<String,Integer> getClock(){
        return clockValue;
    }

    /**
     * Compares two vector clocks to detect if the incoming vector clock
     * is allowed to be received by the client.
     * @param vc - incoming vector clock
     * @param sender - sender of the message with incoming vector clock
     * @return true/false if it's allowed to receive the message
     */
    public boolean compare(VectorClock vc, String sender){

        if(clockValue.get(sender)==null){
            clockValue.put(sender,0);
        }
        if((clockValue.get(sender)+1==vc.getClock().get(sender))||(GCom.getLocalMember().getName().equals(sender))){
            Set<String> vcIds = clockValue.keySet();
            for (String id : vcIds) {
                if (vc.getClock().get(id)!=null && !id.equals(sender)) {
                    if(!(vc.getClock().get(id)<=clockValue.get(id))){
                        return false;
                    }
                }
            }
        }else{
            return false;
        }

        return true;
    }

    /**
     * equals function for vector clocks
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof VectorClock)){
            return false;
        }else{
            if(obj == this) {
                return true;
            }
            VectorClock vc = (VectorClock) obj;
            Set<String> vcIds = vc.getClock().keySet();
            if(clockValue.size()==vc.getClock().size()){
                int nr=0;
                int nrTrue=0;
                for(String id:vcIds){
                    nr++;
                    if(clockValue.containsKey(id) && clockValue.get(id)==vc.getClock().get(id)){
                        nrTrue++;
                    }
                }
                if(nr==nrTrue){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * hashcode generator
     * @return
     */
    @Override
    public int hashCode() {
        return 123;
    }

}
