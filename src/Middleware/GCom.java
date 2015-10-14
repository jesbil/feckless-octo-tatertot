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
import java.util.Observable;

import static Interface.Constants.*;

/**
 * Created by oi12pjn on 2015-10-08.
 */
public class GCom extends Observable {
    private static String nameServiceAddress;
    private static GroupManagementModule groupManagement;
    private static MessageOrderingModule messageOrdering;
    private static CommunicationModule communication;
    private static NameServerCommunicator nameServerCommunicator;
    private static boolean unordered;
    private static boolean toAllMembers = true;
    private static boolean toGroup = false;

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

    public static void initiate(boolean unordered) throws UnknownHostException, RemoteException, AlreadyBoundException, NotBoundException {
        groupManagement = new GroupManagementModule();
        messageOrdering = new MessageOrderingModule();
        communication = new CommunicationModule(groupManagement.getLocalMember());
        nameServerCommunicator = new NameServerCommunicator();
        Registry register = LocateRegistry.createRegistry(Constants.port);
        register.bind(Constants.RMI_ID, communication);
        GCom.unordered = unordered;
    }

    public static void connectToNameService(String nameService) throws IOException, NotBoundException, GroupException {
        GCom.nameServiceAddress = nameService;
        ArrayList<Member> allMembers = nameServerCommunicator.retrieveMembers(nameServiceAddress);
        groupManagement.setAllMembers(allMembers);
        if(!unordered){
            messageOrdering.addToAllMembersClock(allMembers);
            messageOrdering.triggerSelfEvent(toAllMembers);
        }
        joinGroup(groupManagement.getAllMembers().getName());
    }

    public static Message getNextUserMessage(String groupName){
        return messageOrdering.getNextUserMessage(groupName);
    }




    public static void createGroup(String groupName) throws NotBoundException, UnknownHostException, GroupException {
        groupManagement.createGroup(groupName);
        messageOrdering.addGroup(groupName);
        if(!unordered){
            messageOrdering.triggerSelfEvent(toAllMembers);
        }
        try {
            communication.nonReliableMulticast(TYPE_CREATE_GROUP, groupManagement.getAllMembers(), groupName, messageOrdering.getAllMemberVectorClock());
        } catch (RemoteException e) {
            groupManagement.removeMemberFromGroup(groupManagement.getAllMembers().getName(), e.getMessage().substring(28, e.getMessage().indexOf(";")));
        }
    }

    public static void sendMessage(String text, String groupName) throws  NotBoundException, UnknownHostException {
        if(!unordered){
            messageOrdering.triggerSelfEvent(toGroup);
        }
        try {
            communication.nonReliableMulticast(TYPE_MESSAGE, groupManagement.getGroupByName(groupName),text, messageOrdering.getGroupVectorClock());
        } catch (RemoteException e) {
            groupManagement.removeMemberFromGroup(groupManagement.getAllMembers().getName(), e.getMessage().substring(28, e.getMessage().indexOf(";")));
        }
    }

    public static void joinGroup(String groupName) throws UnknownHostException,  NotBoundException, GroupException {
        if(!unordered && groupName!=getAllMembersGroupName()){
            messageOrdering.triggerSelfEvent(toGroup);
        }
        groupManagement.joinGroup(groupName);
        try {
            if(groupName.equals(getAllMembersGroupName())){
                System.out.println(messageOrdering.getAllMemberVectorClock().getClock().toString()+" skickar");
                communication.nonReliableMulticast(TYPE_JOIN_GROUP, groupManagement.getGroupByName(groupName),groupName, messageOrdering.getAllMemberVectorClock());

            }else{
                communication.nonReliableMulticast(TYPE_JOIN_GROUP, groupManagement.getGroupByName(groupName),groupName, messageOrdering.getGroupVectorClock());
            }
        } catch (RemoteException e) {
            groupManagement.removeMemberFromGroup(groupManagement.getAllMembers().getName(), e.getMessage().substring(28, e.getMessage().indexOf(";")));
        }
    }

    public static void leaveGroup(String groupName) throws IOException, NotBoundException {
        if(!unordered){
            messageOrdering.triggerSelfEvent(toGroup);
        }
        if(groupName.equals(groupManagement.getAllMembers().getName())){
            messageOrdering.triggerSelfEvent(toAllMembers);
            nameServerCommunicator.leave(nameServiceAddress);
        }
        Group temp = groupManagement.getGroupByName(groupName);
        groupManagement.leaveGroup(groupName);
        if(groupManagement.getGroupByName(groupName).getMembers().size()==0){
            messageOrdering.triggerSelfEvent(toAllMembers);
            groupManagement.removeGroup(groupName);
            communication.nonReliableMulticast(TYPE_REMOVE_GROUP,groupManagement.getAllMembers(), groupName, messageOrdering.getAllMemberVectorClock());
        }else{
            if(groupName.equals(groupManagement.getAllMembers().getName())) {
                communication.nonReliableMulticast(TYPE_LEAVE_GROUP, temp, groupName, messageOrdering.getAllMemberVectorClock());
            }else{
                communication.nonReliableMulticast(TYPE_LEAVE_GROUP, temp, groupName, messageOrdering.getGroupVectorClock());
            }
        }
    }




    protected static void groupCreated(String groupName, String sender, VectorClock vc) {
        if(unordered){
            Group newGroup = new Group(groupName);
            newGroup.addMemberToGroup(new Member(sender));
            groupManagement.groupCreated(newGroup);
            messageOrdering.addGroup(groupName);
        }else{
            System.out.println("casual kjapp group");
            if(messageOrdering.receiveCompare(groupName,vc, sender)){
                System.out.println("Grupp skapad, v ok");
                Group newGroup = new Group(groupName);
                newGroup.addMemberToGroup(new Member(sender));
                groupManagement.groupCreated(newGroup);
                messageOrdering.addGroup(groupName);
                messageOrdering.triggerSelfEvent(toAllMembers);
                messageOrdering.getAllMemberVectorClock().mergeWith(vc);
                messageOrdering.performNextIfPossible();
            }else{
                messageOrdering.orderMessage(null,sender,groupName,vc.getClock(),TYPE_CREATE_GROUP);
            }
        }
    }


    protected static void receiveMessage(String message, String sender, String groupName, VectorClock vc) {
        if(unordered){
            messageOrdering.orderMessage(message,sender,groupName,vc.getClock(),TYPE_MESSAGE);
                System.out.println("Message Received:\nMessage: "+message+"\nSent from: "+sender+"\nTo group: "+groupName+"\nReceived at: "+ getLocalMember().getIP()+"\n");

        }else{
            System.out.println("casual kjapp msg");
            if(messageOrdering.receiveCompare(groupName, vc, sender)){
                System.out.println("msg vektor ok");
                messageOrdering.acceptUserMessage(new Message(sender,message,null,groupName,TYPE_MESSAGE));
                System.out.println("Message Received:\nMessage: " + message + "\nSent from: " + sender + "\nTo group: " + groupName + "\nReceived at: " + getLocalMember().getIP() + "\n");
                messageOrdering.triggerSelfEvent(toGroup);
                messageOrdering.getGroupVectorClock().mergeWith(vc);
                messageOrdering.performNextIfPossible();
            }else{
                messageOrdering.orderMessage(null, sender, groupName, vc.getClock(),TYPE_MESSAGE);
            }
        }
    }

    protected static void groupJoined(String sender, String groupName, VectorClock vc){
        if(unordered){
            groupManagement.addMemberToGroup(sender, groupName);
        }else{
            if(messageOrdering.receiveCompare(groupName, vc, sender)){
                groupManagement.addMemberToGroup(sender, groupName);
                if(groupName.equals(getAllMembersGroupName())){
                    System.out.println(vc.getClock().toString()+" hämtar");
                    messageOrdering.triggerSelfEvent(toAllMembers);
                    ArrayList<Member> temp = new ArrayList<Member>();
                    temp.add(new Member(sender));
                    messageOrdering.getAllMemberVectorClock().mergeWith(vc);
                    messageOrdering.performNextIfPossible();
                }else{
                    messageOrdering.triggerSelfEvent(toGroup);
                    ArrayList<Member> temp = new ArrayList<Member>();
                    temp.add(new Member(sender));
                    messageOrdering.getGroupVectorClock().mergeWith(vc);
                    messageOrdering.performNextIfPossible();
                }
            }
            else{
                messageOrdering.orderMessage(null, sender, groupName, vc.getClock(), TYPE_JOIN_GROUP);
            }
        }
    }



    protected static void leftGroup(String groupName, String sender, VectorClock vc){
        if(unordered){
            groupManagement.removeMemberFromGroup(groupName, sender);
        }else{
            if(messageOrdering.receiveCompare(groupName, vc, sender)){
                if(groupName.equals(getAllMembersGroupName())){
                    messageOrdering.triggerSelfEvent(toAllMembers);
                    groupManagement.removeMemberFromGroup(groupName, sender);
                    messageOrdering.getAllMemberVectorClock().getClock().remove(sender);
                }else{
                    messageOrdering.triggerSelfEvent(toGroup);
                    groupManagement.removeMemberFromGroup(groupName, sender);
                    messageOrdering.getGroupVectorClock().mergeWith(vc);
                    messageOrdering.performNextIfPossible();
                }

            }else{
                messageOrdering.orderMessage(null,sender,groupName,vc.getClock(),TYPE_LEAVE_GROUP);
            }

        }
    }

    protected static void groupRemoved(String groupName, VectorClock vc, String sender) {
        if(unordered){
            groupManagement.removeGroup(groupName);
        }else{
            if(messageOrdering.receiveCompare(groupName, vc, sender)){
                groupManagement.removeGroup(groupName);
                messageOrdering.triggerSelfEvent(toGroup);
                messageOrdering.getGroupVectorClock().mergeWith(vc);
                messageOrdering.performNextIfPossible();
            }else{
                messageOrdering.orderMessage(null, sender, groupName, vc.getClock(),TYPE_REMOVE_GROUP);
            }
        }
    }


}
