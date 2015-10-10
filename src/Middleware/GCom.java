package Middleware;

import Client.GUI;
import Interface.Constants;

import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

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
        nameServerCommunicator.retrieveMembers(nameServiceAddress);
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

    public static void initiate() throws UnknownHostException {
        groupManagement = new GroupManagement();
        messageOrdering = new MessageOrderingModule();
        communication = new CommunicationModule(groupManagement.getLocalMember());
        nameServerCommunicator = new NameServerCommunicator();
    }
}
