package Middleware;

import Interface.Constants;

import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collections;
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
    private static boolean toAllMembers = true;
    private static boolean toGroup = false;
    private static ArrayList<String> debuggLog;

    private static String port;

    public static String getPort() {
        return port;
    }

    public static String getAllMembersGroupName(){
        return groupManagement.getAllMembers().getName();
    }

    public static Member getLocalMember(){
        return groupManagement.getLocalMember();
    }

    public static String getCurrentGroup(){
        return groupManagement.getCurrentGroup();
    }

    public static ArrayList<String> getGroupNames(){
        ArrayList<String> groupNames = new ArrayList<String>();
        for (int i = 0; i < groupManagement.getGroups().size(); i++) {
            groupNames.add(groupManagement.getGroups().get(i).getName());
        }
        return  groupNames;
    }

    public static ArrayList<String> getDebuggLog() {
        return debuggLog;
    }

    public void initiate(boolean unordered, Observer observer) throws UnknownHostException, RemoteException, AlreadyBoundException, NotBoundException {
        nameServerCommunicator = new NameServerCommunicator();
        Registry register = LocateRegistry.createRegistry(0);
        port = register.toString().substring(register.toString().indexOf(":") + 1, register.toString().indexOf("("));
        port = port.substring(port.indexOf(":") + 1, port.indexOf("]"));
        port = port.substring(port.indexOf(":")+1);

        groupManagement = new GroupManagementModule();
        messageOrdering = new MessageOrderingModule();
        messageOrdering.addObserver(this);
        communication = new CommunicationModule(groupManagement.getLocalMember());

        register.bind(Constants.RMI_ID, communication);
        GCom.unordered = unordered;
        debuggLog = new ArrayList<String>();
        addObserver(observer);
    }

    public void connectToNameService(String nameService) throws IOException, NotBoundException, GroupException {
        GCom.nameServiceAddress = nameService;
        ArrayList<Member> allMembers = nameServerCommunicator.retrieveMembers(nameServiceAddress,port);
        groupManagement.setAllMembers(allMembers);
        if(!unordered){
            messageOrdering.addToAllMembersClock(allMembers);
        }
        debuggLog.add("Connected to Name Service Server @ "+nameService);
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
            case TYPE_MESSAGE:
                messageSent(message);
                break;
        }
        messageOrdering.performNextIfPossible();
    }

    public static void createGroup(String groupName) throws RemoteException, NotBoundException, UnknownHostException {
        Message message = new Message(groupManagement.getLocalMember(),groupName,messageOrdering.getAllMemberVectorClock(),groupManagement.getAllMembers(),TYPE_CREATE_GROUP);
        messageOrdering.triggerSelfEvent(toAllMembers);
        communication.nonReliableMulticast(message);
    }


    public static void joinGroup(String groupName) throws RemoteException, NotBoundException, UnknownHostException {
        Message message = new Message(groupManagement.getLocalMember(),groupName,messageOrdering.getAllMemberVectorClock(), groupManagement.getAllMembers(),TYPE_JOIN_GROUP);
        messageOrdering.triggerSelfEvent(toAllMembers);
        communication.nonReliableMulticast(message);
    }

    public static void leaveGroup(String groupName) throws RemoteException, NotBoundException, UnknownHostException {
        Message message = new Message(groupManagement.getLocalMember(),groupName,messageOrdering.getAllMemberVectorClock(),groupManagement.getAllMembers(),TYPE_JOIN_GROUP);
        messageOrdering.triggerSelfEvent(toAllMembers);
        communication.nonReliableMulticast(message);
    }


    public static void sendMessage(String text, String groupName) throws RemoteException, NotBoundException, UnknownHostException {
        System.out.println("send msg: "+text +"to: "+groupName);
        Message message = new Message(groupManagement.getLocalMember(),text,messageOrdering.getAllMemberVectorClock(),groupManagement.getGroupByName(groupName),TYPE_MESSAGE);
        messageOrdering.triggerSelfEvent(toAllMembers);
        communication.nonReliableMulticast(message);
    }



    private static void groupCreated(Message message) {
        groupManagement.groupCreated(message.getMessage(),message.getSender());
    }

    private static void groupJoined(Message message){
        groupManagement.addMemberToGroup(message.getSender(), message.getGroup());
    }

    private static void leftGroup(Message message){
        groupManagement.removeMemberFromGroup(message.getSender(), message.getGroup());

    }

    private static void groupRemoved(Message message) {
        groupManagement.removeGroup(message.getMessage());
    }

    private static void messageSent(Message message) {

    }


    public static void sendInInvalidOrder() throws NotBoundException, UnknownHostException, RemoteException {
        for(String key : messageOrdering.getGroupVectorClock().getClock().keySet()){
            messageOrdering.getGroupVectorClock().getClock().put(key, messageOrdering.getGroupVectorClock().getClock().get(key)+1);
        }
        sendMessage("sent first but should be received last", getCurrentGroup());
        for(String key : messageOrdering.getGroupVectorClock().getClock().keySet()){
            messageOrdering.getGroupVectorClock().getClock().put(key, messageOrdering.getGroupVectorClock().getClock().get(key));
        }
        sendMessage("sent last but should be received first",getCurrentGroup());

    }


    @Override
    public void update(Observable observable, Object o) {
        deliverMessage((Message) o);
        setChanged();
        notifyObservers(o);
    }
}
