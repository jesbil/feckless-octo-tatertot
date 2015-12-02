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
    private static GroupManagementModule groupManagement;
    private static MessageOrderingModule messageOrdering;
    private static CommunicationModule communication;
    private static List<DebuggMessage> debuggLog;
    private static Member localMember;

    private static String port;


    protected static Member getLocalMember(){
        return localMember;
    }

    protected static ArrayList<Group> getJoinedGroups(){
        return groupManagement.getJoinedGroups();
    }

    protected static Group getGroupByName(String groupName) {
        return groupManagement.getGroupByName(groupName);
    }


    protected static List<DebuggMessage> getDebuggLog() {
        return debuggLog;
    }

    protected static ArrayList<Message> getHoldBackQueue() {
        return messageOrdering.getHoldBackQueue();
    }

    public void initiate(boolean unordered, Observer observer) throws UnknownHostException, RemoteException, AlreadyBoundException, NotBoundException {
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


    protected static void receiveMessage(Message message) {
        messageOrdering.receiveMessage(message);
        messageOrdering.performNextIfPossible();
    }

    private void deliverMessage(Message message) {
        debuggLog.add(new DebuggMessage("delivering message: " + message.getMessage()));
        switch (message.getType()){
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

    public static void createGroup(String groupName) throws NotBoundException, UnknownHostException, RemoteException {
        //TODO: SKICKA INFO OM GRUPPNAMN OCH LEDARE TILL NAMNSERVERN
        if(NameServerCommunicator.getLeader(groupName)==null){
            NameServerCommunicator.setLeader(localMember,groupName);
            Group g = new Group(groupName);
            g.addMemberToGroup(localMember);
            groupManagement.addGroup(g);
            System.out.println("Grupp skapad: "+ groupName);
        }else{
            System.out.println("Grupp kunde inte skapas");
        }
    }

    public static void joinGroup(String groupName) throws NotBoundException, UnknownHostException, RemoteException {
        //TODO: PRATA MED NAME SERVICE
        Member leader = NameServerCommunicator.getLeader(groupName);
        if(leader!=null){
            Group g=communication.fetchGroup(leader,groupName);
            groupManagement.addGroup(g);
            g.addMemberToGroup(localMember);
            System.out.println(g.getMembers().size());
            Message message = new Message(localMember,groupName,g.getVectorClock(),g,TYPE_JOIN_GROUP);
            communication.nonReliableMulticast(message);
        }
    }

    public static void leaveGroup(String groupName) throws NotBoundException, UnknownHostException{
        Message message = new Message(localMember,groupName,getGroupByName(groupName).getVectorClock(),getGroupByName(groupName),TYPE_LEAVE_GROUP);
        communication.nonReliableMulticast(message);

    }


    public static void sendMessage(String text, ArrayList<Group> groups) throws NotBoundException, UnknownHostException{
        if(groups==null){
            for (Group group : groupManagement.getJoinedGroups()){
                messageOrdering.triggerSelfEvent(group.getName());
                Message message = new Message(localMember,text,group.getVectorClock(),group,TYPE_MESSAGE);
                System.out.println("hejehejtjotjo");
                communication.nonReliableMulticast(message);
            }
        }else{
            for (Group group : groups){
                messageOrdering.triggerSelfEvent(group.getName());
                Message message = new Message(localMember,text,group.getVectorClock(),group,TYPE_MESSAGE);
                communication.nonReliableMulticast(message);

            }
        }
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

        messageOrdering.performNextIfPossible();
    }

    public static void shutdown() throws IOException, NotBoundException {
        ArrayList<Group> groups = new ArrayList<>(getJoinedGroups());
        for(Group group : groups){
            leaveGroup(group.getName());
        }
    }

    public static void pauseStartHbq(boolean paused) {
        if(paused){
            messageOrdering.pauseQueue();
        }else{
            messageOrdering.startQueue();
        }
    }

    public  static void shuffleHbq() {
        messageOrdering.shuffleQueue();
    }

    protected static void removeMemberFromAllGroups(Member member) {
        groupManagement.removeMemberFromAllGroups(member);
    }

    public static void setNameServiceAddress(String address) {
        NameServerCommunicator.setAddress(address);
    }
}

//TODO unordered (if-satseR)
