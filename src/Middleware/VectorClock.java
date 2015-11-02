package Middleware;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by c12jbr on 2015-10-13.
 */
public class VectorClock implements Serializable{
    private HashMap<String,Integer> clockValue;

    public VectorClock() {
        clockValue = new HashMap<>();
        clockValue.put(GCom.getLocalMember().getName(),0);
    }

    public void triggerSelfEvent() {
        int value = clockValue.get(GCom.getLocalMember().getName());
        value++;
        clockValue.put(GCom.getLocalMember().getName(),value);
    }


    public HashMap<String,Integer> getClock(){
        return clockValue;
    }


    public void mergeWith(VectorClock vc){
        Set<String> vcIds = vc.getClock().keySet();
        for(String id:vcIds){
            if(clockValue.containsKey(id)){
                clockValue.put(id,max(clockValue.get(id),vc.getClock().get(id)));
            }
            else{
                clockValue.put(id,vc.getClock().get(id));
            }
        }
        GCom.getDebuggLog().add(new DebuggMessage("Clock merged: " + clockValue.toString()));
    }

    private int max(int a, int b){
        if(a>b){
            return a;
        }
        return b;
    }

    public boolean compare(VectorClock vc, String sender){
        if(compare2(vc, sender)) {
            return true;
        }
        return false;
    }

    private boolean compare2(VectorClock vc, String sender) {

        boolean earlier = false;
        boolean later = false;
        if(clockValue.get(sender)==null){
            clockValue.put(sender,0);
        }
        System.out.println("mysender: "+clockValue.get(sender) +" vs "+" msgsender: "+vc.getClock().get(sender));
        if(clockValue.get(sender)+1 != vc.getClock().get(sender) && !GCom.getLocalMember().getName().equals(sender)){
            return false;
        }

        Set<String> vcIds = clockValue.keySet();
        for (String id : vcIds) {
            System.out.println("Comparing"+id+": on s:"+sender+": & r:"+GCom.getLocalMember().getName());
            System.out.println(clockValue.get(id)+":"+vc.getClock().get(id));
            if (vc.getClock().get(id)!=null && !id.equals(sender)) {
                if(clockValue.get(id).compareTo(vc.getClock().get(id))==-1) {
                    earlier = true;
                }else if(clockValue.get(id).compareTo(vc.getClock().get(id))==1) {
                    later = true;
                }
            }
        }
    if(earlier && !later){
        return false;
    }
    return true;
}

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

    @Override
    public int hashCode() {
        return 123;
    }
}
