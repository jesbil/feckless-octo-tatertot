package Middleware;


import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by oi12pjn on 2015-10-07.
 */
public class Group {

    private String name;

    private ArrayList<Member> members;


    public Group(String name) throws RemoteException {
        super();
        members = new ArrayList<Member>();
        this.name=name;
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

    public String getName(){
        return name;
    }
}
