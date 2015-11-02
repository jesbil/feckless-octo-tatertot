package Middleware;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

import static Interface.Constants.*;

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
        GCom.getDebuggLog().add("Clock merged: " + clockValue.toString());
    }

    private int max(int a, int b){
        if(a>b){
            return a;
        }
        return b;
    }

    public int compare(VectorClock vc, String sender){
        if(this.equals(vc,sender)){
            System.out.println("equals");
            return CLOCK_TYPE_EQ;
        }
        if(this.lessThen(vc,sender)){
            System.out.println("lessthen");
            return CLOCK_TYPE_LT;
        }
        if(this.biggerThen(vc,sender)){
            System.out.println("biggerthen");
            return CLOCK_TYPE_BT;
        }
        System.out.println("concurrent");
        return CLOCK_TYPE_CONC;
    }

    private boolean equals(VectorClock vc, String sender) {

        boolean earlier = false;
        boolean later = false;
        if(clockValue.get(sender)+1 != vc.getClock().get(sender)){
            return false;
        }

        Set<String> vcIds = clockValue.keySet();
        System.out.println("mysize: "+clockValue.size()+"\nrecsize: "+vc.getClock().size());
        for (String id : vcIds) {
            System.out.println("Comparing"+id+": on s:"+sender+": & r:"+GCom.getLocalMember().getName());
            System.out.println(clockValue.get(id)+":"+vc.getClock().get(id));
            if (vc.getClock().get(id)!=null && !clockValue.get(id).equals(vc.getClock().get(id))) {
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

    public boolean lessThen(VectorClock vc, String sender){
        Set<String> vcIds = vc.getClock().keySet();
        int nr=0;
        int nreq=0;
        int nrlt=0;
        for(String id:vcIds){
            if(id.equals(sender) || id.equals(GCom.getLocalMember().getName())) {
            }
            else{
                nr++;
                if (clockValue.containsKey(id)) {
                    if (clockValue.get(id) == vc.getClock().get(id)) {
                        nreq++;
                    }
                    if (clockValue.get(id) < vc.getClock().get(id)) {
                        nrlt++;
                    }

                }
            }
        }
        if(nrlt>0 && nrlt+nreq==nr){
            return true;
        }
        return false;
    }

    public boolean biggerThen(VectorClock vc, String sender){
        Set<String> vcIds = vc.getClock().keySet();
        int nr=0;
        int nreq=0;
        int nrbt=0;
        for(String id:vcIds){
            if(id.equals(sender) || id.equals(GCom.getLocalMember().getName())) {
            }
            else{
                nr++;
                if(clockValue.containsKey(id)) {
                    if (clockValue.get(id) == vc.getClock().get(id)) {
                        nreq++;
                    }
                    if (clockValue.get(id) > vc.getClock().get(id)) {
                        nrbt++;
                    }
                }
            }
        }
        if(nrbt>0 && nrbt+nreq==nr){
            return true;
        }
        return false;
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
