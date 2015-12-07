package Middleware;


import Interface.GComRemote;

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
 *
 * A class that handles message sending through RMI
 */
public class CommunicationModule extends UnicastRemoteObject implements GComRemote {
    private static Member localMember;

    /**
     * Constructor
     *
     * @param localMember
     * @throws RemoteException
     */
    protected CommunicationModule(Member localMember) throws RemoteException {
        super();
        this.localMember = localMember;
    }

    /**
     * Multicasts a given message to a given group
     *
     * @param message
     * @throws NotBoundException
     * @throws UnknownHostException
     */
    protected void nonReliableMulticast(Message message) throws NotBoundException, UnknownHostException{

        GCom.getDebuggLog().add(new DebuggMessage("multicasting to group: " + message.getGroup().getName()));
        int nrOfRecievers = message.getGroup().getMembers().size();
        ArrayList<Member> temp = new ArrayList<>(message.getGroup().getMembers());
        for (int i = 0; i < nrOfRecievers; i++) {
            Member member = temp.get(i);
            if (!member.equals(localMember)) {
                try {
                    Registry registry = LocateRegistry.getRegistry(member.getIP(), member.getPort());
                    GComRemote remote = (GComRemote) registry.lookup(RMI_ID);
                    remote.receiveMulticast(message);
                }catch(RemoteException e) {
                    GCom.getDebuggLog().add(new DebuggMessage(("Member " + member.getName() + " disconnected!")));
                    System.out.println("Member krashch!!");
                    GCom.removeMemberFromAllGroups(member);
                    message.getGroup().removeMemberFromGroup(member);
                    Message message2 = new Message(member,message.getMessage(),message.getGroup(),message.getType());
                    GCom.leaderElection(message2);


                    //TODO: KOLLA OM DETTA FUNKAR!?!?!? FÖRSÖKER FIXA NY LEDARE OM EN LEDARE KASCHAR
                }
            } else {
                GCom.receiveMessage(message);

            }
        }
    }



    /**
     * Receives a multicast from a remote user
     * @param message
     * @throws RemoteException
     */
    @Override
    public void receiveMulticast(Message message)  throws RemoteException{
        GCom.getDebuggLog().add(new DebuggMessage("received multicast from: "+message.getSender().getName()));
        GCom.receiveMessage(message);
    }

    /**
     * Remote function that returns a group from the leader in the group
     * @param leader
     * @param groupName
     * @return
     * @throws RemoteException
     * @throws NotBoundException
     */
    public Group fetchGroup(Member leader,String groupName) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(leader.getIP(), leader.getPort());
        GComRemote remote = (GComRemote) registry.lookup(RMI_ID);
        return remote.retrieveGroup(groupName);
    }

    @Override
    /**
     * Returns a group from the group leader
     * @param groupName
     * @return
     * @throws RemoteException
     */
    public Group retrieveGroup(String groupName) throws RemoteException {
        return GCom.getGroupByName(groupName);
    }
}
