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
import java.util.*;

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
    private static List<DebuggMessage> debuggLog;
    private static Member localMember;

    private static String port;

    public static String getAllMembersGroupName(){
        return groupManagement.getAllMembers().getName();
    }

    public static Member getLocalMember(){
        return localMember;
    }

    public static ArrayList<Group> getJoinedGroups(){
        return groupManagement.getJoinedGroups();
    }

    public static Group getGroupByName(String groupName) {
        return groupManagement.getGroupByName(groupName);
    }

    public static ArrayList<Group> getGroups() {
        return groupManagement.getGroups();
    }

    public static List<DebuggMessage> getDebuggLog() {
        return debuggLog;
    }

    public static ArrayList<Message> getHoldBackQueue() {
        return messageOrdering.getHoldBackQueue();
    }

    public void initiate(boolean unordered, Observer observer) throws UnknownHostException, RemoteException, AlreadyBoundException, NotBoundException {
        nameServerCommunicator = new NameServerCommunicator();
        Registry register = LocateRegistry.createRegistry(0);
        port = register.toString().substring(register.toString().indexOf(":") + 1, register.toString().indexOf("("));
        port = port.substring(port.indexOf(":") + 1, port.indexOf("]"));
        port = port.substring(port.indexOf(":") + 1);
        localMember = new Member(InetAddress.getLocalHost().getHostAddress(),Integer.parseInt(port));
        groupManagement = new GroupManagementModule(localMember);
        messageOrdering = new MessageOrderingModule(unordered);
        messageOrdering.addObserver(this);
        communication = new CommunicationModule(localMember);
        debuggLog = Collections.synchronizedList(new ArrayList<DebuggMessage>());

        register.bind(Constants.RMI_ID, communication);
        addObserver(observer);
    }

    public void connectToNameService(String nameService) throws IOException, NotBoundException{
        nameServiceAddress = nameService;
        ArrayList<Member> allMembers = nameServerCommunicator.retrieveMembers(nameServiceAddress,port);
        groupManagement.setAllMembers(allMembers);
        messageOrdering.addToAllMembersClock(allMembers);
        debuggLog.add( new DebuggMessage("Connected to Name Service Server @ " + nameService));
        ArrayList<Group> temp;
        if((temp = communication.fetchGroups(allMembers.get(0)))!=null){
            for(Group group : temp){
                groupManagement.groupCreated(group.getName()+"#"+group.getSize(),group.getMembers().get(0));
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
        debuggLog.add(new DebuggMessage("delivering message: " + message.getMessage()));
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
    }

    public static void createGroup(String groupName) throws NotBoundException, UnknownHostException{
        Message message = new Message(localMember,groupName,groupManagement.getAllMembers().getVectorClock(),groupManagement.getAllMembers(),TYPE_CREATE_GROUP);
        communication.nonReliableMulticast(message);
    }

    public static void joinGroup(String groupName) throws NotBoundException, UnknownHostException{
        if(!getGroupByName(groupName).isStarted()){
            Message message = new Message(localMember,groupName,groupManagement.getAllMembers().getVectorClock(), groupManagement.getAllMembers(),TYPE_JOIN_GROUP);
            communication.nonReliableMulticast(message);
        }
    }

    public static void leaveGroup(String groupName) throws NotBoundException, UnknownHostException{
        Message message = new Message(localMember,groupName,groupManagement.getAllMembers().getVectorClock(),groupManagement.getAllMembers(),TYPE_LEAVE_GROUP);
        communication.nonReliableMulticast(message);
        if(getGroupByName(groupName).getMembers().size()==0&&groupName!=getAllMembersGroupName()){
            message = new Message(localMember,groupName,groupManagement.getAllMembers().getVectorClock(),groupManagement.getAllMembers(),TYPE_REMOVE_GROUP);
            communication.nonReliableMulticast(message);
        }
    }


    public static void sendMessage(String text, ArrayList<Group> groups) throws NotBoundException, UnknownHostException{
        if(groups==null){
            for (Group group : groupManagement.getJoinedGroups()){
                messageOrdering.triggerSelfEvent(group.getName());
                Message message = new Message(localMember,text,group.getVectorClock(),group,TYPE_MESSAGE);

                    if(group.isStarted()){
                        communication.nonReliableMulticast(message);
                    }

            }
        }else{
            for (Group group : groups){
                messageOrdering.triggerSelfEvent(group.getName());
                Message message = new Message(localMember,text,group.getVectorClock(),group,TYPE_MESSAGE);

                    communication.nonReliableMulticast(message);

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
        int size=0;
        if(((Message) o).getType()==TYPE_REMOVE_GROUP) {
            size = getGroupByName(((Message) o).getMessage()).getSize();
        }
        deliverMessage((Message) o);
        if(((Message) o).getType()==TYPE_REMOVE_GROUP){
            Message temp=(Message)o;
            Message msg = new Message(temp.getSender(),temp.getMessage()+"#"+size,temp.getVectorClock(),temp.getGroup(),temp.getType());
            setChanged();
            notifyObservers(msg);
        }else{
            setChanged();
            notifyObservers(o);
        }

        messageOrdering.performNextIfPossible();
    }

    public static void shutdown() throws IOException, NotBoundException {
        ArrayList<Group> groups = new ArrayList<>(getJoinedGroups());
        for(Group group : groups){
            leaveGroup(group.getName());
        }
        leaveGroup(getAllMembersGroupName());
        nameServerCommunicator.leave(nameServiceAddress,port);
    }

    public static void pauseStartHbq(boolean paused) {
        if(paused){
            messageOrdering.pauseQueue();
        }else{
            messageOrdering.startQueue();
        }
    }

    public static void shuffleHbq() {
        messageOrdering.shuffleQueue();
    }

    public static void removeMemberFromAllGroups(Member member) {
        groupManagement.removeMemberFromAllGroups(member);
    }
}

//TODO unordered (if-satseR)
