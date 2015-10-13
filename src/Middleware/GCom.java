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
            messageOrdering.triggerSelfEvent(toAllMembers);
            messageOrdering.addToAllMembersClock(allMembers);
        }
        joinGroup(groupManagement.getAllMembers().getName());
    }

    public static Message getNextMessage(String groupName){
        return messageOrdering.getNextMessage(groupName);
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
            communication.nonReliableMulticast(TYPE_JOIN_GROUP, groupManagement.getGroupByName(groupName),groupName, messageOrdering.getGroupVectorClock());
        } catch (RemoteException e) {
            groupManagement.removeMemberFromGroup(groupManagement.getAllMembers().getName(), e.getMessage().substring(28, e.getMessage().indexOf(";")));
        }
    }

    public static void leaveGroup(String groupName) throws IOException, NotBoundException {
        if(!unordered){
            messageOrdering.triggerSelfEvent(toGroup);
        }
        if(groupName.equals(groupManagement.getAllMembers().getName())){
            nameServerCommunicator.leave(nameServiceAddress);
        }

        Group temp = groupManagement.getGroupByName(groupName);
        groupManagement.leaveGroup(groupName);
        if(groupManagement.getGroupByName(groupName).getMembers().size()==0){
            groupManagement.removeGroup(groupName);
            communication.nonReliableMulticast(TYPE_REMOVE_GROUP,groupManagement.getAllMembers(),groupName, messageOrdering.getGroupVectorClock());
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
                messageOrdering.triggerSelfEvent(toGroup);
                messageOrdering.getGroupVectorClock().mergeWith(vc);
            }
        }
    }


    protected static void receiveMessage(String message, String sender, String groupName, VectorClock vc) {
        if(unordered){
            messageOrdering.orderMessage(message,sender,groupName);
                System.out.println("Message Received:\nMessage: "+message+"\nSent from: "+sender+"\nTo group: "+groupName+"\nReceived at: "+ getLocalMember().getIP()+"\n");

        }else{
            System.out.println("casual kjapp msg");
            if(messageOrdering.receiveCompare(groupName, vc, sender)){
                System.out.println("msg vektor ok");
                messageOrdering.orderMessage(message,sender,groupName);
                System.out.println("Message Received:\nMessage: " + message + "\nSent from: " + sender + "\nTo group: " + groupName + "\nReceived at: " + getLocalMember().getIP() + "\n");
                messageOrdering.triggerSelfEvent(toGroup);
                messageOrdering.getGroupVectorClock().mergeWith(vc);
            }
        }
    }

    protected static void groupJoined(String sender, String groupName, VectorClock vc){
        if(unordered){
            groupManagement.addMemberToGroup(sender, groupName);
        }else{
            if(messageOrdering.receiveCompare(groupName, vc, sender)){

                groupManagement.addMemberToGroup(sender, groupName);
                messageOrdering.triggerSelfEvent(toGroup);
                messageOrdering.getGroupVectorClock().mergeWith(vc);
            }
        }
    }



    protected static void leftGroup(String groupName, String sender, VectorClock vc){
        if(unordered){
            groupManagement.removeMemberFromGroup(groupName, sender);
        }else{
            if(messageOrdering.receiveCompare(groupName, vc, sender)){
                if(groupName.equals(groupManagement.getAllMembers().getName())){
                    messageOrdering.triggerSelfEvent(toAllMembers);
                    groupManagement.removeMemberFromGroup(groupName, sender);
                    messageOrdering.getAllMemberVectorClock().getClock().remove(sender);

                }else{
                    messageOrdering.triggerSelfEvent(toGroup);
                    groupManagement.removeMemberFromGroup(groupName, sender);
                    messageOrdering.getGroupVectorClock().mergeWith(vc);
                }
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
            }
        }
    }


}
