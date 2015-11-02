package Middleware;

import Interface.Constants;

import java.io.IOException;
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
public class GCom extends Observable implements Observer {
    private static String nameServiceAddress;
    private static GroupManagementModule groupManagement;
    private static MessageOrderingModule messageOrdering;
    private static CommunicationModule communication;
    private static NameServerCommunicator nameServerCommunicator;
    private static boolean unordered;
    private static ArrayList<String> debuggLog;
    private static Member localMember;

    private static String port;

    public static String getPort() {
        return port;
    }

    public static String getAllMembersGroupName(){
        return groupManagement.getAllMembers().getName();
    }

    public static Member getLocalMember(){
        return localMember;
    }

    public static ArrayList<Group> getJoinedGroups(){
        return groupManagement.getJoinedGroups();
    }

    public static ArrayList<String> getDebuggLog() {
        return debuggLog;
    }

    public static Group getGroupByName(String groupName) {
        return groupManagement.getGroupByName(groupName);
    }

    public static ArrayList<Group> getGroups() {
        return groupManagement.getGroups();
    }

    public void initiate(boolean unordered, Observer observer) throws UnknownHostException, RemoteException, AlreadyBoundException, NotBoundException {
        nameServerCommunicator = new NameServerCommunicator();
        Registry register = LocateRegistry.createRegistry(0);
        port = register.toString().substring(register.toString().indexOf(":") + 1, register.toString().indexOf("("));
        port = port.substring(port.indexOf(":") + 1, port.indexOf("]"));
        port = port.substring(port.indexOf(":")+1);
        localMember = new Member(InetAddress.getLocalHost().getHostAddress(),Integer.parseInt(port));
        groupManagement = new GroupManagementModule(localMember);
        messageOrdering = new MessageOrderingModule();
        messageOrdering.addObserver(this);
        communication = new CommunicationModule(localMember);

        register.bind(Constants.RMI_ID, communication);
        GCom.unordered = unordered;
        debuggLog = new ArrayList<>();
        addObserver(observer);
    }

    public void connectToNameService(String nameService) throws IOException, NotBoundException, GroupException {
        nameServiceAddress = nameService;
        ArrayList<Member> allMembers = nameServerCommunicator.retrieveMembers(nameServiceAddress,port);
        groupManagement.setAllMembers(allMembers);
        messageOrdering.addToAllMembersClock(allMembers);
        debuggLog.add("Connected to Name Service Server @ " + nameService);
        ArrayList<Group> temp;
        if((temp = communication.fetchGroups(allMembers.get(0)))!=null){
            for(Group group : temp){
                groupManagement.groupCreated(group.getName(),group.getMembers().get(0));
                for (int i = 1; i < group.getMembers().size(); i++) {
                    groupManagement.addMemberToGroup(group.getName(),group.getMembers().get(i));
                }
            }
            setChanged();
            notifyObservers(groupManagement.getGroups());
        }
        joinGroup(getAllMembersGroupName());
    }


    protected static void receiveMessage(Message message) {
        messageOrdering.receiveMessage(message);
        messageOrdering.performNextIfPossible();
    }

    private void deliverMessage(Message message) {
        System.out.println("delivering message of type: "+message.getType());
        switch (message.getType()){
            case TYPE_CREATE_GROUP:
                groupCreated(message);
                break;
            case TYPE_JOIN_GROUP:
                groupJoined(message);
                break;
            case TYPE_LEAVE_GROUP:
                leftGroup(message);
                break;
            case TYPE_REMOVE_GROUP:
                groupRemoved(message);
                break;
        }
        messageOrdering.performNextIfPossible();
    }

    public static void createGroup(String groupName) throws NotBoundException, UnknownHostException, GroupException {
//        messageOrdering.triggerSelfEvent(getAllMembersGroupName());
        Message message = new Message(localMember,groupName,groupManagement.getAllMembers().getVectorClock(),groupManagement.getAllMembers(),TYPE_CREATE_GROUP);
        communication.nonReliableMulticast(message);
    }

    public static void joinGroup(String groupName) throws NotBoundException, UnknownHostException, GroupException {
        if(!getGroupByName(groupName).isStarted()){
//            messageOrdering.triggerSelfEvent(getAllMembersGroupName());
            Message message = new Message(localMember,groupName,groupManagement.getAllMembers().getVectorClock(), groupManagement.getAllMembers(),TYPE_JOIN_GROUP);
            communication.nonReliableMulticast(message);
        }
    }

    public static void leaveGroup(String groupName) throws NotBoundException, UnknownHostException, GroupException {
//        messageOrdering.triggerSelfEvent(getAllMembersGroupName());
        Message message = new Message(localMember,groupName,groupManagement.getAllMembers().getVectorClock(),groupManagement.getAllMembers(),TYPE_LEAVE_GROUP);
        communication.nonReliableMulticast(message);
    }


    public static void sendMessage(String text, ArrayList<Group> groups) throws NotBoundException, UnknownHostException, GroupException {
        if(groups==null){
            for (Group group : groupManagement.getJoinedGroups()){
                messageOrdering.triggerSelfEvent(group.getName());
                Message message = new Message(localMember,text,group.getVectorClock(),group,TYPE_MESSAGE);
                try {
                    if(group.isStarted()){
                        communication.nonReliableMulticast(message);
                    }
                } catch (GroupException e) {
                    groupManagement.removeMemberFromAllGroups(e.getMember());
                    throw e;
                }
            }
        }else{
            for (Group group : groups){
                messageOrdering.triggerSelfEvent(group.getName());
                Message message = new Message(localMember,text,group.getVectorClock(),group,TYPE_MESSAGE);
                try {
                    communication.nonReliableMulticast(message);
                } catch (GroupException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void groupCreated(Message message){
        groupManagement.groupCreated(message.getMessage(),message.getSender());
    }

    private static void groupJoined(Message message){
        groupManagement.addMemberToGroup(message.getMessage(), message.getSender());
    }

    private static void leftGroup(Message message){
        groupManagement.removeMemberFromGroup(message.getMessage(), message.getSender());
    }

    private static void groupRemoved(Message message) {
        groupManagement.removeGroup(message.getMessage());
    }

    @Override
    public void update(Observable observable, Object o) {
        deliverMessage((Message) o);
        setChanged();
        notifyObservers(o);
    }

    public static void shutdown() throws IOException, NotBoundException, GroupException {
        ArrayList<Group> groups = new ArrayList<>(getJoinedGroups());
        for(Group group : groups){
            leaveGroup(group.getName());
        }
        leaveGroup(getAllMembersGroupName());
        nameServerCommunicator.leave(nameServiceAddress);
    }
}

//TODO unordered (if-satseR)
//TODO ta bort RÄTT member från namnservern
//TODO vectorklockorna (?)
//TODO abstrahera bort middlewareklasser från clienten
