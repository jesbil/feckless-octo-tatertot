package Middleware;

import Client.GUI;
import Interface.Constants;

import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import static Interface.Constants.*;

/**
 * Created by oi12pjn on 2015-10-08.
 */
public class GCom {
    private static String nameServiceAddress;
    private static GroupManagement groupManagement;
    private static MessageOrderingModule messageOrdering;
    private static CommunicationModule communication;
    private static NameServerCommunicator nameServerCommunicator;


    public GCom() throws UnknownHostException {


    }


    public static void connectToNameService(String nameService) throws IOException {
        GCom.nameServiceAddress = nameService;
        ArrayList<Member> allMembers=nameServerCommunicator.retrieveMembers(nameServiceAddress);
        groupManagement.setAllMembers(allMembers);
    }

    public static void createGroup(String groupName) throws RemoteException, NotBoundException, UnknownHostException {
        groupManagement.createGroup(groupName);
//        messageOrdering.order(groupName);
        communication.nonReliableMulticast(TYPE_CREATE_GROUP,groupManagement.getAllMembers(),groupName);
    }

    public static void groupCreated(Group group) {
//        messageOrdering.order(groupName);
        groupManagement.groupCreated(group);
    }

    public static void initiate() throws UnknownHostException, RemoteException, AlreadyBoundException {
        groupManagement = new GroupManagement();
        messageOrdering = new MessageOrderingModule();
        communication = new CommunicationModule(groupManagement.getLocalMember());
        Registry register = LocateRegistry.createRegistry(Constants.port);
        register.bind(Constants.RMI_ID, communication);
        nameServerCommunicator = new NameServerCommunicator();
    }
}
