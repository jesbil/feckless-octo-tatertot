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
        clockValue = new HashMap<String,Integer>();
        clockValue.put(GCom.getLocalMember().getIP(),0);
    }
    public VectorClock(HashMap<String,Integer> clockValue){
        this.clockValue= new HashMap<String, Integer>(clockValue);
    }


    public void triggerSelfEvent() {
        int value = clockValue.get(GCom.getLocalMember().getIP());
        value++;
        clockValue.put(GCom.getLocalMember().getIP(),value);


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
        System.out.println("Clock merged, result:\n"+clockValue.toString());
    }

    private int max(int a, int b){
        System.out.println("MAX COMPARE IN MERGE:"+a+":"+b);
        if(a>b){
            return a;
        }
        return b;
    }

    public int compare(VectorClock vc, String sender){
        if (clockValue.size()!=vc.getClock().size()){
            Set<String> keys = clockValue.keySet();
            for(String key : keys){
                if(!vc.getClock().containsKey(key)){
                    vc.getClock().put(key,0);
                }
            }
            keys = vc.getClock().keySet();
            for(String key : keys){
                if(!clockValue.containsKey(key)){
                    clockValue.put(key,0);
                }
            }
        }
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

        Set<String> vcIds = vc.getClock().keySet();
        System.out.println("mysize: "+clockValue.size()+"\nrecsize: "+vc.getClock().size());
        int nr = 0;
        int nrTrue = 0;
        for (String id : vcIds) {
            if(id.equals(sender) || id.equals(GCom.getLocalMember().getIP()) || sender.equals(GCom.getLocalMember().getIP())){
            }else{
                System.out.println(id+":s:"+sender+":id:"+GCom.getLocalMember().getIP());
                nr++;
                if (clockValue.containsKey(id) && clockValue.get(id) == vc.getClock().get(id)) {
                    nrTrue++;
                }
            }
        }
        System.out.println("nr: "+nr +" nrtrue:"+nrTrue);
        if (nr == nrTrue) {
            return true;
        }
        return false;
    }

    public boolean lessThen(VectorClock vc, String sender){
        Set<String> vcIds = vc.getClock().keySet();
        int nr=0;
        int nreq=0;
        int nrlt=0;
        for(String id:vcIds){
            if(id.equals(sender) || id.equals(GCom.getLocalMember().getIP())) {
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
            if(id.equals(sender) || id.equals(GCom.getLocalMember().getIP())) {
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
        return this.hashCode();
    }
}
