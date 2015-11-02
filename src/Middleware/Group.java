package Middleware;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by oi12pjn on 2015-10-07.
 */
public class Group implements Serializable{

    private String name;
    private ArrayList<Member> members;
    private VectorClock vectorClock;
    private int size;
    private boolean started;

    public ArrayList<Member> getMembers(){
        return members;
    }

    public String getName(){
        return name;
    }

    public VectorClock getVectorClock() {
        return vectorClock;
    }

    public Group(String name, int size) {
        started = false;
        members = new ArrayList<>();
        this.name=name;
        this.size=size;
        vectorClock = new VectorClock();
    }

    public void addMemberToGroup(Member m) {
        members.add(m);
        if(members.size()==size){
            for(Member member :members){
                vectorClock.getClock().put(member.getName(),0);
            }
            started=true;
        }
    }

    public void removeMemberFromGroup(Member m){
        members.remove(m);
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof Group){
            if(((Group) obj).getName().equals(name)){
                return true;
            }
        }
        return false;
    }


    @Override
    public int hashCode(){
        return this.hashCode();
    }


    public boolean isStarted() {
        return started;
    }

    public int getSize() {
        return size;
    }
}
