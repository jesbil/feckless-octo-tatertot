package Middleware;


import Interface.GComRemote;

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
public class CommunicationModule extends UnicastRemoteObject implements GComRemote {
    private static Member localMember;


    protected CommunicationModule(Member localMember) throws RemoteException {
        super();
        this.localMember = localMember;
    }

    // send
    protected void nonReliableMulticast(Message message) throws NotBoundException, UnknownHostException{

        GCom.getDebuggLog().add(new DebuggMessage("multicasting to group: " + message.getGroup().getName()));
        int nrOfRecievers = message.getGroup().getMembers().size();
        for (int i = 0; i < nrOfRecievers; i++) {
            Member member = message.getGroup().getMembers().get(i);
            if (!member.equals(localMember)) {
                try {
                    Registry registry = LocateRegistry.getRegistry(member.getIP(), member.getPort());
                    GComRemote remote = (GComRemote) registry.lookup(RMI_ID);
                    remote.receiveMulticast(message);
                }catch(RemoteException e) {
                    GCom.getDebuggLog().add(new DebuggMessage(("Member " + member.getName() + " disconnected!")));
                    GCom.removeMemberFromAllGroups(member);
                    nrOfRecievers--;
                    i--;
                }
            } else {

                GCom.receiveMessage(message);

            }
        }
    }




    @Override
    public void receiveMulticast(Message message)  throws RemoteException{
        GCom.getDebuggLog().add(new DebuggMessage("received multicast from: "+message.getSender().getName()));
        GCom.receiveMessage(message);
    }


    public Group fetchGroup(Member leader,String groupName) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(leader.getIP(), leader.getPort());
        GComRemote remote = (GComRemote) registry.lookup(RMI_ID);
        return remote.retrieveGroup(groupName);
    }

    @Override
    public Group retrieveGroup(String groupName) throws RemoteException {
        return GCom.getGroupByName(groupName);
    }
}
