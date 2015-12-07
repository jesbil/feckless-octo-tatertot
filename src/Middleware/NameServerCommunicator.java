package Middleware;


import Interface.NameServiceRemote;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import static Interface.Constants.NAME_SERVICE_ID;
import static Interface.Constants.NAME_SERVICE_PORT;


/**
 * Created by c12jbr on 2015-10-05
 *  Communicates with a name service using RMI
 */

public class NameServerCommunicator {


    private static String address;
    /**
     * Sets the leader for a group
     * @param leader
     * @param groupName
     * @throws RemoteException
     */
    public static void setLeader(Member leader,String groupName) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(address,NAME_SERVICE_PORT);
        NameServiceRemote remote = (NameServiceRemote) registry.lookup(NAME_SERVICE_ID);
        remote.setLeader(leader,groupName);
    }
    /**
     * Returns the leader from the desired group
     * @param groupName
     * @return
     * @throws RemoteException
     */
    public static Member getLeader(String groupName) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(address,NAME_SERVICE_PORT);
        NameServiceRemote remote = (NameServiceRemote) registry.lookup(NAME_SERVICE_ID);
        return remote.getLeader(groupName);
    }

    public static void setAddress(String address) {
        NameServerCommunicator.address = address;
    }

}
