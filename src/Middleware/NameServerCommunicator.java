package Middleware;

import Interface.GComRemote;
import Interface.NameServiceRemote;

import java.io.IOException;
import java.math.BigInteger;
import java.net.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import static Interface.Constants.NAME_SERVICE_ID;
import static Interface.Constants.NAME_SERVICE_PORT;
import static Interface.Constants.RMI_ID;

/**
 * Created by c12jbr on 2015-10-05.
 */
public class NameServerCommunicator {


    private static String address;

    public static void setLeader(Member leader,String groupName) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(address,NAME_SERVICE_PORT);
        NameServiceRemote remote = (NameServiceRemote) registry.lookup(NAME_SERVICE_ID);
        remote.setLeader(leader,groupName);
    }

    public static Member getLeader(String groupName) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(address,NAME_SERVICE_PORT);
        NameServiceRemote remote = (NameServiceRemote) registry.lookup(NAME_SERVICE_ID);
        return remote.getLeader(groupName);
    }

    public static void setAddress(String address) {
        NameServerCommunicator.address = address;
    }

}
