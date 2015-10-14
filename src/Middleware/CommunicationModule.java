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
    public void nonReliableMulticast(int type, Group group, String msg, VectorClock vectorClock) throws RemoteException, NotBoundException, UnknownHostException {
        switch (type){
            case TYPE_LEAVE_GROUP:
                for(Member m : group.getMembers()){
                    if(!m.getIP().equals(localMember.getIP())) {
                        Registry registry = LocateRegistry.getRegistry(m.getIP(), Constants.port);
                        MyRemote remote = (MyRemote) registry.lookup(Constants.RMI_ID);
                        remote.leaveGroup(InetAddress.getLocalHost().getHostAddress(),group.getName(), vectorClock);
                    }
                }
                break;
            case TYPE_JOIN_GROUP:
                for(Member m : group.getMembers()){
                    if(!m.getIP().equals(localMember.getIP())) {
                        Registry registry = LocateRegistry.getRegistry(m.getIP(), Constants.port);
                        MyRemote remote = (MyRemote) registry.lookup(Constants.RMI_ID);
                        remote.joinGroup(InetAddress.getLocalHost().getHostAddress(), group.getName(), msg, vectorClock);
                    }
                }
                break;
            case TYPE_CREATE_GROUP:
                for(Member m : group.getMembers()){
                    if(!m.getIP().equals(localMember.getIP())){
                        Registry registry = LocateRegistry.getRegistry(m.getIP(), Constants.port);
                        MyRemote remote = (MyRemote) registry.lookup(Constants.RMI_ID);
                        remote.createGroup(msg,localMember.getIP(), vectorClock);
                    }
                }
                break;


            case TYPE_MESSAGE:
                for(Member m : group.getMembers()){
                    Registry registry = LocateRegistry.getRegistry(m.getIP(), Constants.port);
                    MyRemote remote = (MyRemote) registry.lookup(Constants.RMI_ID);
                    remote.message(msg, InetAddress.getLocalHost().getHostAddress(), group.getName(), vectorClock);
                }
                break;

            case TYPE_REMOVE_GROUP:
                for(Member m : group.getMembers()){
                    if(!m.getIP().equals(localMember.getIP())) {
                        Registry registry = LocateRegistry.getRegistry(m.getIP(), Constants.port);
                        MyRemote remote = (MyRemote) registry.lookup(Constants.RMI_ID);
                        remote.removeGroup(msg, InetAddress.getLocalHost().getHostAddress(), vectorClock);
                    }
                }
        }


    }


    // receive

    @Override
    public void createGroup(String groupName, String sender, VectorClock vc) throws RemoteException {
        GCom.groupCreated(groupName,sender, vc);
    }

    @Override
    public void joinGroup(String sender, String groupName, String groupJoined, VectorClock vc) throws RemoteException {
        GCom.groupJoined(sender, groupName, groupJoined, vc);
    }

    @Override
    public void leaveGroup(String sender, String groupName, VectorClock vc) throws RemoteException {
        GCom.leftGroup(groupName, sender, vc);
    }

    @Override
    public void message(String message, String sender, String groupName, VectorClock vc) throws RemoteException {
        GCom.receiveMessage(message,sender,groupName, vc);

    }

    @Override
    public void removeGroup(String groupName, String sender, VectorClock vc) throws RemoteException{
        GCom.groupRemoved(groupName, vc, sender);
    }

}
