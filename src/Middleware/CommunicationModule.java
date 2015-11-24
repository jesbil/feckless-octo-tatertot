package Middleware;


import Interface.MyRemote;

import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

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
    public void nonReliableMulticast(Message message) throws NotBoundException, UnknownHostException, GroupException {

        GCom.getDebuggLog().add(new DebuggMessage("multicasting to group: " + message.getGroup().getName()));
        for (int i = 0; i < message.getGroup().getMembers().size(); i++) {
            Member member = message.getGroup().getMembers().get(i);
            if (!member.equals(localMember)) {
                try {
                    Registry registry = LocateRegistry.getRegistry(member.getIP(), member.getPort());
                    MyRemote remote = (MyRemote) registry.lookup(RMI_ID);
                    remote.receiveMulticast(message);
                }catch(RemoteException e) {
                    throw new GroupException("Member "+member.getName()+" disconnected!",member);
                }
            } else {
                if (message.getMessage().equals(GCom.getAllMembersGroupName()) && message.getType() == TYPE_JOIN_GROUP) {

                } else {
                    GCom.receiveMessage(message);
                }
            }
        }
    }




    @Override
    public void receiveMulticast(Message message)  throws RemoteException{
        System.out.println("received multicast from: "+message.getSender().getName());
        GCom.receiveMessage(message);
    }

    public ArrayList<Group> fetchGroups(Member member) throws NotBoundException, RemoteException {
        System.out.println("Fetching groups from: "+member.getIP());
        Registry registry = LocateRegistry.getRegistry(member.getIP(),member.getPort());
        MyRemote remote = (MyRemote) registry.lookup(RMI_ID);
        return remote.retrieveGroups();
    }

    @Override
    public ArrayList<Group> retrieveGroups() throws RemoteException {
        return GCom.getGroups();
    }

}
