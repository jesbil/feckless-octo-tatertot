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
    private Member leader;


    public Member getLeader() {
        return leader;
    }
    public void setLeader(Member leader) {
        this.leader = leader;
    }


    public ArrayList<Member> getMembers(){
        return members;
    }

    public  String getName(){
        return name;
    }

    public  VectorClock getVectorClock() {
        return vectorClock;
    }

    public  Group(String name) {
        members = new ArrayList<>();
        this.name=name;
        vectorClock = new VectorClock();
    }

    public Group(Group g){
        this.name=g.getName();
        this.members=new ArrayList<>(g.getMembers());
        this.vectorClock=g.getVectorClock();
    }

    public  void addMemberToGroup(Member m) {
        if(!members.contains(m)){
            members.add(m);
            if(vectorClock.getClock().get(m.getName())==null){
                vectorClock.getClock().put(m.getName(),0);
            }
        }
    }

    public  void removeMemberFromGroup(Member m){
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
        return 66667;
    }



}
