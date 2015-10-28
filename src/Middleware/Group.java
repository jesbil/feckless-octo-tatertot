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

    public ArrayList<Member> getMembers(){
        return members;
    }

    public String getName(){
        return name;
    }

    public VectorClock getVectorClock() {
        return vectorClock;
    }

    public Group(String name) {
        members = new ArrayList<Member>();
        this.name=name;
    }


    public void addMemberToGroup(Member m){
        members.add(m);
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

}
