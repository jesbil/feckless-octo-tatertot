package Middleware;


import Interface.Constants;
import Interface.MyRemote;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static Interface.Constants.*;

/**
 * Created by c12jbr on 2015-10-08.
 */
public class CommunicationModule implements  MyRemote{

    // send
    public void nonReliableMulticast(int type, String msg, Group group) throws RemoteException, NotBoundException, UnknownHostException {

        switch (type){
            case TYPE_LEAVE_GROUP:
                for(Member m : group.getMembers()){
                    Registry registry = LocateRegistry.getRegistry(m.getIP(), Constants.port);
                    MyRemote remote = (MyRemote) registry.lookup(Constants.RMI_ID);
                    remote.leaveGroup(InetAddress.getLocalHost().getHostAddress());
                }
                break;
            case TYPE_JOIN_GROUP:
                for(Member m : group.getMembers()){
                    Registry registry = LocateRegistry.getRegistry(m.getIP(), Constants.port);
                    MyRemote remote = (MyRemote) registry.lookup(Constants.RMI_ID);
                    remote.joinGroup(InetAddress.getLocalHost().getHostAddress());
                }
                break;
            case TYPE_CREATE_GROUP:
                for(Member m : group.getMembers()){
                    Registry registry = LocateRegistry.getRegistry(m.getIP(), Constants.port);
                    MyRemote remote = (MyRemote) registry.lookup(Constants.RMI_ID);
                    remote.createGroup(InetAddress.getLocalHost().getHostAddress());
                }
                break;


            case TYPE_MESSAGE:
                for(Member m : group.getMembers()){
                    Registry registry = LocateRegistry.getRegistry(m.getIP(), Constants.port);
                    MyRemote remote = (MyRemote) registry.lookup(Constants.RMI_ID);
                    remote.message(new Message(InetAddress.getLocalHost().getHostAddress(),msg));
                }
                break;
        }


    }


    // receive

    @Override
    public void createGroup(String name) throws RemoteException {

    }

    @Override
    public void joinGroup(String name) throws RemoteException {

    }

    @Override
    public void leaveGroup(String name) throws RemoteException {

    }

    @Override
    public void message(Message message) throws RemoteException {

    }
}
