package Middleware;


import Interface.Constants;
import Interface.MyRemote;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import static Interface.Constants.*;

/**
 * Created by c12jbr on 2015-10-08.
 */
public class CommunicationModule implements  MyRemote {
    private static Member localMember;


    public CommunicationModule(Member localMember) throws RemoteException, AlreadyBoundException {
        this.localMember = localMember;
    }

    // send
    public void nonReliableMulticast(int type, Group group, String msg) throws RemoteException, NotBoundException, UnknownHostException {

        switch (type){
            case TYPE_LEAVE_GROUP:
                for(Member m : group.getMembers()){
                    if(m!=localMember){
                        Registry registry = LocateRegistry.getRegistry(m.getIP(), Constants.port);
                        MyRemote remote = (MyRemote) registry.lookup(Constants.RMI_ID);
                        remote.leaveGroup(InetAddress.getLocalHost().getHostAddress(),group.getName());

                    }
                }
                break;
            case TYPE_JOIN_GROUP:
                for(Member m : group.getMembers()){
                    Registry registry = LocateRegistry.getRegistry(m.getIP(), Constants.port);
                    MyRemote remote = (MyRemote) registry.lookup(Constants.RMI_ID);
                    remote.joinGroup(InetAddress.getLocalHost().getHostAddress(),group.getName());
                }
                break;
            case TYPE_CREATE_GROUP:
                for(Member m : group.getMembers()){
                    if(!m.getIP().equals(localMember.getIP())){

                        Registry registry = LocateRegistry.getRegistry(m.getIP(), Constants.port);
                        MyRemote remote = (MyRemote) registry.lookup(Constants.RMI_ID);
                        Group newGroup = new Group(msg);
                        newGroup.addMemberToGroup(localMember);
                        remote.createGroup(newGroup);
                        System.out.println("fixade biffen");
                    }
                }
                break;


            case TYPE_MESSAGE:
                for(Member m : group.getMembers()){
                    Registry registry = LocateRegistry.getRegistry(m.getIP(), Constants.port);
                    MyRemote remote = (MyRemote) registry.lookup(Constants.RMI_ID);
                    remote.message(new Message(InetAddress.getLocalHost().getHostAddress(),msg),group.getName());
                }
                break;
        }


    }


    // receive

    @Override
    public void createGroup(Group group) throws RemoteException {
        System.out.println("fan då");
        GCom.groupCreated(group);
    }

    @Override
    public void joinGroup(String name, String groupName) throws RemoteException {

    }

    @Override
    public void leaveGroup(String name, String groupName) throws RemoteException {

    }

    @Override
    public void message(Message message, String groupName) throws RemoteException {

    }

}
