package Middleware;


import Interface.Constants;
import Interface.MyRemote;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import static Interface.Constants.*;

/**
 * Created by c12jbr on 2015-10-08.
 */
public class CommunicationModule extends UnicastRemoteObject implements  MyRemote {
    private static Member localMember;


    public CommunicationModule(Member localMember) throws RemoteException {
        super();
        this.localMember = localMember;
    }

    // send
    public void nonReliableMulticast(int type, Group group, String msg) throws RemoteException, NotBoundException, UnknownHostException {

        switch (type){
            case TYPE_LEAVE_GROUP:
                for(Member m : group.getMembers()){
                    if(!m.getIP().equals(localMember.getIP())) {
                        Registry registry = LocateRegistry.getRegistry(m.getIP(), Constants.port);
                        MyRemote remote = (MyRemote) registry.lookup(Constants.RMI_ID);
                        remote.leaveGroup(InetAddress.getLocalHost().getHostAddress(),group.getName());

                    }
                }
                break;
            case TYPE_JOIN_GROUP:
                for(Member m : group.getMembers()){
                    if(!m.getIP().equals(localMember.getIP())) {
                        Registry registry = LocateRegistry.getRegistry(m.getIP(), Constants.port);
                        MyRemote remote = (MyRemote) registry.lookup(Constants.RMI_ID);
                        remote.joinGroup(InetAddress.getLocalHost().getHostAddress(), group.getName());
                    }
                }
                break;
            case TYPE_CREATE_GROUP:
                for(Member m : group.getMembers()){
                    if(!m.getIP().equals(localMember.getIP())){

                        Registry registry = LocateRegistry.getRegistry(m.getIP(), Constants.port);
                        MyRemote remote = (MyRemote) registry.lookup(Constants.RMI_ID);
                        remote.createGroup(msg,localMember.getIP());
                        System.out.println("fixade biffen");
                    }
                }
                break;


            case TYPE_MESSAGE:
                for(Member m : group.getMembers()){
                    Registry registry = LocateRegistry.getRegistry(m.getIP(), Constants.port);
                    MyRemote remote = (MyRemote) registry.lookup(Constants.RMI_ID);
                    System.out.println("Sending message:\nMessage: "+msg+"\nSent from: "+ InetAddress.getLocalHost().getHostAddress()+"\nTo group: "+group.getName()+"\n");
                    remote.message(msg, InetAddress.getLocalHost().getHostAddress(), group.getName());
                }
                break;
        }


    }


    // receive

    @Override
    public void createGroup(String groupName, String leader) throws RemoteException {
        System.out.println("Group created:\nGroup name: "+groupName+"\nHost: "+leader+"\n");
        GCom.groupCreated(groupName,leader);
    }

    @Override
    public void joinGroup(String name, String groupName) throws RemoteException {
        System.out.println("Member joined group:\nGroup name:"+groupName+"\nMember: "+ name+"\n");
        GCom.groupJoined(name, groupName);
    }

    @Override
    public void leaveGroup(String name, String groupName) throws RemoteException {
        System.out.println(name+" has left the group: "+groupName+"\n");
        GCom.leftGroup(groupName,name);
    }

    @Override
    public void message(String message, String sender, String groupName) throws RemoteException {
        GCom.receiveMessage(message,sender,groupName);

    }

}
