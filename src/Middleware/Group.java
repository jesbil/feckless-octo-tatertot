package Middleware;

import java.util.ArrayList;

/**
 * Created by oi12pjn on 2015-10-07.
 */
public class Group {

    private ArrayList<Member> members;

    public Group(){
        members = new ArrayList<Member>();
    }
    public void addMemberToGroup(Member m){
        members.add(m);
    }
    public void removeMemberFromGroup(Member m){
        members.remove(m);
    }

    public ArrayList<Member> getMembers(){
        return members;
    }
}
