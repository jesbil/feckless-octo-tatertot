package Middleware;

import Interface.GroupRemote;

import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * Created by oi12pjn on 2015-10-07.
 */
public class Group extends UnicastRemoteObject implements GroupRemote{

    private String name;

    private ArrayList<Member> members;
    private RemoteObject ro;

    public RemoteObject getRo(){
        return ro;
    }

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
