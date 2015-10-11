package Middleware;

import Interface.Constants;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import static Interface.Constants.*;

/**
 * Created by oi12pjn on 2015-10-08.
 */
public class GCom extends Observable {
    private static String nameServiceAddress;
    private static GroupManagementModule groupManagementModule;
    private static MessageOrderingModule messageOrdering;
    private static CommunicationModule communication;
    private static NameServerCommunicator nameServerCommunicator;

    public static void initiate() throws UnknownHostException, RemoteException, AlreadyBoundException {
        groupManagementModule = new GroupManagementModule();
        messageOrdering = new MessageOrderingModule();
        communication = new CommunicationModule(groupManagementModule.getLocalMember());
        nameServerCommunicator = new NameServerCommunicator();
        Registry register = LocateRegistry.createRegistry(Constants.port);
        register.bind(Constants.RMI_ID, communication);
    }

    public static ArrayList<String> getGroupNames(){
        ArrayList<String> groupNames = new ArrayList<String>();
        for(Group group : groupManagementModule.getGroups()){
            groupNames.add(group.getName());
        }
        return  groupNames;
    }

    public static void connectToNameService(String nameService) throws IOException {
        GCom.nameServiceAddress = nameService;
        ArrayList<Member> allMembers=nameServerCommunicator.retrieveMembers(nameServiceAddress);
        groupManagementModule.setAllMembers(allMembers);
    }

    public static void createGroup(String groupName) throws RemoteException, NotBoundException, UnknownHostException {
        groupManagementModule.createGroup(groupName);
        messageOrdering.addGroup(groupName);
//        messageOrdering.order(groupName);
        communication.nonReliableMulticast(TYPE_CREATE_GROUP, groupManagementModule.getAllMembers(), groupName);
    }

    public static void sendMessage(String text, String groupName) throws RemoteException, NotBoundException, UnknownHostException {
//        messageOrdering.order(text);
        communication.nonReliableMulticast(TYPE_MESSAGE, groupManagementModule.getGroupByName(groupName),text);
    }

    public static Message getNextMessage(String groupName){
        return messageOrdering.getNextMessage(groupName);
    }

    protected static void groupCreated(String groupName, String leader) throws RemoteException {
        Group newGroup = new Group(groupName);
        newGroup.addMemberToGroup(new Member(leader));
        groupManagementModule.groupCreated(newGroup);
        messageOrdering.addGroup(groupName);
    }


    protected static void receiveMessage(String message, String sender, String groupName) {
        messageOrdering.orderMessage(message,sender,groupName);
        try {
            System.out.println("Message Received:\nMessage: "+message+"\nSent from: "+sender+"\nTo group: "+groupName+"\nReceived at: "+ InetAddress.getLocalHost().getHostAddress()+"\n");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    protected static void groupJoined(String name, String groupName){
        groupManagementModule.addMemberToGroup(name, groupName);
    }

    public static void joinGroup(String groupName) throws UnknownHostException, RemoteException, NotBoundException {
        groupManagementModule.joinGroup(groupName);
        communication.nonReliableMulticast(TYPE_JOIN_GROUP,groupManagementModule.getGroupByName(groupName),groupName);
    }
    public static void leaveGroup(String groupName) throws RemoteException, NotBoundException, UnknownHostException {
        Group temp = groupManagementModule.getGroupByName(groupName);
        groupManagementModule.leaveGroup(groupName);
        communication.nonReliableMulticast(TYPE_LEAVE_GROUP,temp,groupName);
    }

    protected static void leftGroup(String groupName,String name){
        groupManagementModule.removeMemberFromGroup(groupName,name);
    }

}
