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
 *
 * GCom main class that binds all modules together.
 * Methods called from the client are found here.
 */
public class GCom extends Observable implements Observer {
    private static GroupManagementModule groupManagement;
    private static MessageOrderingModule messageOrdering;
    private static CommunicationModule communication;
    private static List<DebuggMessage> debuggLog;
    private static Member localMember;

    private static String port;

    /**
     *
     * @return localMember
     */
    protected static Member getLocalMember(){
        return localMember;
    }

    /**
     *
     * @return all joined groups
     */
    protected static ArrayList<Group> getJoinedGroups(){
        return groupManagement.getJoinedGroups();
    }

    /**
     *
     * @param groupName
     * @return the group with the name groupName
     */
    protected static Group getGroupByName(String groupName) {
        return groupManagement.getGroupByName(groupName);
    }

    /**
     *
     * @return debuggLog
     */
    protected static List<DebuggMessage> getDebuggLog() {
        return debuggLog;
    }

    /**
     *
     * @return holdBackQueue
     */
    protected static ArrayList<Message> getHoldBackQueue() {
        return messageOrdering.getHoldBackQueue();
    }

    /**
     * Inittiates GCom including every module
     *
     * @param unordered
     * @param observer
     * @throws UnknownHostException
     * @throws RemoteException
     * @throws AlreadyBoundException
     * @throws NotBoundException
     */
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

    /**
     * Receives a message from the message ordering module
     * @param message
     */
    protected static void receiveMessage(Message message) {
        messageOrdering.receiveMessage(message);
        messageOrdering.performNextIfPossible();
    }

    /**
     * Delivers a message
     * @param message
     */
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

    /**
     * Creates a group for communication
     *
     * @param groupName
     * @throws NotBoundException
     * @throws UnknownHostException
     * @throws RemoteException
     */
    public static void createGroup(String groupName) throws NotBoundException, UnknownHostException, RemoteException {
        if(NameServerCommunicator.getLeader(groupName)==null){
            NameServerCommunicator.setLeader(localMember,groupName);
            Group g = new Group(groupName);
            g.addMemberToGroup(localMember);
            g.setLeader(localMember);
            groupManagement.addGroup(g);
            System.out.println("Grupp skapad: "+ groupName);
        }else{
            System.out.println("Grupp kunde inte skapas");
        }
    }

    /**
     * Joins a specified group
     * @param groupName
     * @throws NotBoundException
     * @throws UnknownHostException
     * @throws RemoteException
     */
    public static void joinGroup(String groupName) throws NotBoundException, UnknownHostException, RemoteException {
        //TODO: PRATA MED NAME SERVICE
        Member leader = NameServerCommunicator.getLeader(groupName);
        if(leader!=null){
            Group g=communication.fetchGroup(leader,groupName);
            g.setLeader(leader);
            groupManagement.addGroup(g);
            g.addMemberToGroup(localMember);
            messageOrdering.triggerSelfEvent(g.getName());
            Message message = new Message(localMember,groupName,getGroupByName(groupName),TYPE_JOIN_GROUP);
            communication.nonReliableMulticast(message);
        }
    }

    /**
     * Leaves a specified group
     * @param groupName
     * @throws NotBoundException
     * @throws UnknownHostException
     */
    public static void leaveGroup(String groupName) throws NotBoundException, UnknownHostException{
        System.out.println("Lämnar grupp: "+groupName);
        messageOrdering.triggerSelfEvent(groupName);
        Message message = new Message(localMember,groupName,getGroupByName(groupName),TYPE_LEAVE_GROUP);
        if(message.getGroup().getMembers().size()==1){
            try {
                NameServerCommunicator.setLeader(null,groupName);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        communication.nonReliableMulticast(message);

    }

    /**
     * Sends a message to the joined groups
     * @param text
     * @param groups
     * @throws NotBoundException
     * @throws UnknownHostException
     */
    public static void sendMessage(String text, ArrayList<Group> groups) throws NotBoundException, UnknownHostException{
        if(groups==null){
            for (Group group : groupManagement.getJoinedGroups()){
                messageOrdering.triggerSelfEvent(group.getName());
                Message message = new Message(localMember,text,group,TYPE_MESSAGE);
                communication.nonReliableMulticast(message);
            }
        }else{
            for (Group group : groups){
                messageOrdering.triggerSelfEvent(group.getName());
                Message message = new Message(localMember,text,group,TYPE_MESSAGE);
                communication.nonReliableMulticast(message);

            }
        }
    }

    /**
     * A new member has joined a group
     * @param message
     */
    private static void groupJoined(Message message){
        groupManagement.addMemberToGroup(message.getMessage(), message.getSender());

    }

    /**
     * A member has left a group
     * @param message
     */
    private static void leftGroup(Message message){
        groupManagement.removeMemberFromGroup(message.getMessage(), message.getSender());
        leaderElection(message,null);
    }

    /**
     * Elects a new leader for a group
     * @param message
     * @param member
     */
    protected static void leaderElection(Message message, Member member){
        if(member!=null){
            message.getGroup().removeMemberFromGroup(member);
        }
        if(!message.getSender().equals(localMember)){
            System.out.println(message.getSender().getName() +" sändare");
            System.out.println(groupManagement.getGroupByName(message.getGroup().getName() + " grupp"));
            System.out.println(groupManagement.getGroupByName(message.getGroup().getName()).getMembers().get(0)+ " ny ledare");
            if(message.getSender().equals(message.getGroup().getLeader())){
                groupManagement.getGroupByName(message.getGroup().getName()).setLeader(groupManagement.getGroupByName(message.getGroup().getName()).getMembers().get(0));
            }

            if(localMember.equals(groupManagement.getGroupByName(message.getGroup().getName()).getLeader())){
                try {
                    System.out.println("Byter ledare för grupp: "+message.getGroup().getName()+" tilL "+localMember.getName());
                    NameServerCommunicator.setLeader(localMember,message.getGroup().getName());
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (NotBoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * A group should be removed
     * @param message
     */
    private static void groupRemoved(Message message) {
        groupManagement.removeGroup(message.getMessage());

    }

    @Override
    /**
     * Sends an update to the gui with the message that it is allowed to show
     */
    public void update(Observable observable, Object o) {

        deliverMessage((Message) o);
        setChanged();
        notifyObservers(o);
        messageOrdering.performNextIfPossible();
    }

    /**
     * Shuts down GCom
     *
     * @throws IOException
     * @throws NotBoundException
     */
    public static void shutdown() throws IOException, NotBoundException {
        ArrayList<Group> groups = new ArrayList<>(getJoinedGroups());
        for(Group group : groups){
            leaveGroup(group.getName());
        }
    }

    /**
     * Pauses and starts the hold back queue
     * @param paused
     */
    public static void pauseStartHbq(boolean paused) {
        if(paused){
            messageOrdering.pauseQueue();
        }else{
            messageOrdering.startQueue();
        }
    }

    /**
     * Shuffles the hbq
     */
    public  static void shuffleHbq() {
        messageOrdering.shuffleQueue();
    }

    /**
     * removes a member from all groups
     * @param member
     */
    protected static void removeMemberFromAllGroups(Member member) {
        groupManagement.removeMemberFromAllGroups(member);
    }

    /**
     * Sets the addres to the name service
     * @param address
     */
    public static void setNameServiceAddress(String address) {
        NameServerCommunicator.setAddress(address);
    }
}

