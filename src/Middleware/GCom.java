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
import java.util.HashMap;
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
    private static ArrayList<String> debuggLog;

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
        debuggLog = new ArrayList<String>();
    }

    public static void connectToNameService(String nameService) throws IOException, NotBoundException, GroupException {
        GCom.nameServiceAddress = nameService;
        ArrayList<Member> allMembers = nameServerCommunicator.retrieveMembers(nameServiceAddress);
        groupManagement.setAllMembers(allMembers);
        if(!unordered){
            messageOrdering.addToAllMembersClock(allMembers);
        }
        joinGroup(groupManagement.getAllMembers().getName());
        debuggLog.add("Connected to Name Service Server @ "+nameService);
    }

    public static Message getNextUserMessage(String groupName){
        return messageOrdering.getNextUserMessage(groupName);
    }




    public static void createGroup(String groupName) throws NotBoundException, UnknownHostException, GroupException {
        groupManagement.createGroup(groupName);
        messageOrdering.addGroup(groupName);
        messageOrdering.getGroupVectorClock().getClock().put(getLocalMember().getIP(),0);
        debuggLog.add("Group: " + groupName + " added");
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
            communication.nonReliableMulticast(TYPE_MESSAGE, groupManagement.getGroupByName(groupName), text, messageOrdering.getGroupVectorClock());
            debuggLog.add("Message: "+text+" Sent to group: "+ groupName);
        } catch (RemoteException e) {
            groupManagement.removeMemberFromGroup(groupManagement.getAllMembers().getName(), e.getMessage().substring(28, e.getMessage().indexOf(";")));
        }
    }

    public static void joinGroup(String groupName) throws UnknownHostException,  NotBoundException, GroupException {
        if(!unordered && groupName!=getAllMembersGroupName()){
            messageOrdering.triggerSelfEvent(toGroup);
        }
        groupManagement.joinGroup(groupName);
        debuggLog.add("Joined group: "+groupName);
        try {
            if(groupName.equals(getAllMembersGroupName())){
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
        debuggLog.add("Left group: "+groupName);
        if(groupManagement.getGroupByName(groupName).getMembers().size()==0){
            messageOrdering.triggerSelfEvent(toAllMembers);
            groupManagement.removeGroup(groupName);
            debuggLog.add("Removed group: " + groupName);
            communication.nonReliableMulticast(TYPE_REMOVE_GROUP, groupManagement.getAllMembers(), groupName, messageOrdering.getAllMemberVectorClock());
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
            debuggLog.add("Group: "+groupName+" created, requested by: "+sender);
        }else{
            if(messageOrdering.receiveCompare(groupName,vc, sender)){
                Group newGroup = new Group(groupName);
                newGroup.addMemberToGroup(new Member(sender));
                groupManagement.groupCreated(newGroup);
                debuggLog.add("Group: " + groupName + " created, requested by: " + sender);
                messageOrdering.addGroup(groupName);
                messageOrdering.triggerSelfEvent(toAllMembers);
                messageOrdering.getAllMemberVectorClock().mergeWith(vc);
                messageOrdering.performNextIfPossible();
            }else{
                messageOrdering.orderMessage(null,sender,groupName,vc.getClock(),TYPE_CREATE_GROUP);
                debuggLog.add("Group: "+groupName+" creation put in queue, requested by "+sender);
            }
        }
    }


    protected static void receiveMessage(String message, String sender, String groupName, VectorClock vc) {
        if(unordered){
            messageOrdering.orderMessage(message,sender, groupName, vc.getClock(), TYPE_MESSAGE);
            debuggLog.add("Message: "+message+" Sent from: "+sender+" To group: "+groupName+" Received");

        }else{
            if(messageOrdering.receiveCompare(groupName, vc, sender)){
                messageOrdering.acceptUserMessage(new Message(sender, message, null, groupName, TYPE_MESSAGE));
                messageOrdering.triggerSelfEvent(toGroup);
                messageOrdering.getGroupVectorClock().mergeWith(vc);
                debuggLog.add("Message: "+message+" Sent from: "+sender+" To group: "+groupName+" Received");
                messageOrdering.performNextIfPossible();
            }else{
                messageOrdering.orderMessage(null, sender, groupName, vc.getClock(),TYPE_MESSAGE);
                debuggLog.add("Message: "+message+" Sent from: "+sender+" To group: "+groupName+" Put in queue");
            }
        }
    }

    protected static void groupJoined(String sender, String groupName, VectorClock vc){
        if(unordered){
            groupManagement.addMemberToGroup(sender, groupName);
            debuggLog.add("User: "+sender+" joined group: "+groupName);
        }else{
            if(messageOrdering.receiveCompare(groupName, vc, sender)){
                groupManagement.addMemberToGroup(sender, groupName);
                if(groupName.equals(getAllMembersGroupName())){
                    messageOrdering.triggerSelfEvent(toAllMembers);
                    ArrayList<Member> temp = new ArrayList<Member>();
                    temp.add(new Member(sender));
                    messageOrdering.getAllMemberVectorClock().mergeWith(vc);
                    debuggLog.add("User: " + sender + " joined group: " + groupName);
                    messageOrdering.performNextIfPossible();
                }else{
                    messageOrdering.triggerSelfEvent(toGroup);
                    ArrayList<Member> temp = new ArrayList<Member>();
                    temp.add(new Member(sender));
                    messageOrdering.getGroupVectorClock().mergeWith(vc);
                    debuggLog.add("User: " + sender + " joined group: " + groupName);
                    messageOrdering.performNextIfPossible();
                }
            }
            else{
                messageOrdering.orderMessage(null, sender, groupName, vc.getClock(), TYPE_JOIN_GROUP);
                debuggLog.add("User: "+sender+" join group: "+groupName+" Request put in queue");

            }
        }
    }



    protected static void leftGroup(String groupName, String sender, VectorClock vc){
        if(unordered){
            groupManagement.removeMemberFromGroup(groupName, sender);
            debuggLog.add("User: "+sender+" left group: "+groupName);

        }else{
            if(messageOrdering.receiveCompare(groupName, vc, sender)){
                if(groupName.equals(getAllMembersGroupName())){
                    messageOrdering.triggerSelfEvent(toAllMembers);
                    groupManagement.removeMemberFromGroup(groupName, sender);
                    debuggLog.add("User: " + sender + " left group: " + groupName);
                    messageOrdering.getAllMemberVectorClock().getClock().remove(sender);
                }else{
                    messageOrdering.triggerSelfEvent(toGroup);
                    groupManagement.removeMemberFromGroup(groupName, sender);
                    messageOrdering.getGroupVectorClock().mergeWith(vc);
                    debuggLog.add("User: " + sender + " left group: " + groupName);
                    messageOrdering.performNextIfPossible();
                }

            }else{
                messageOrdering.orderMessage(null,sender,groupName,vc.getClock(),TYPE_LEAVE_GROUP);
                debuggLog.add("User: "+sender+" leaving group: "+groupName+" Request put in queue");

            }

        }
    }

    protected static void groupRemoved(String groupName, VectorClock vc, String sender) {
        if(unordered){
            groupManagement.removeGroup(groupName);
            debuggLog.add("Group: "+groupName+"removed\n");
        }else{
            if(messageOrdering.receiveCompare(groupName, vc, sender)){
                groupManagement.removeGroup(groupName);
                messageOrdering.triggerSelfEvent(toAllMembers);
                messageOrdering.getAllMemberVectorClock().mergeWith(vc);
                debuggLog.add("Group: "+groupName+"removed\n");
                messageOrdering.performNextIfPossible();
            }else{
                messageOrdering.orderMessage(null, sender, groupName, vc.getClock(),TYPE_REMOVE_GROUP);
                debuggLog.add("Group: "+groupName+"removal put in queue");
            }
        }
    }


    public static ArrayList<String> getDebuggLog() {
        return debuggLog;
    }

    public static void sendInInvalidOrder() throws NotBoundException, UnknownHostException {
        messageOrdering.getGroupVectorClock().getClock().put(getLocalMember().getIP(), messageOrdering.getGroupVectorClock().getClock().get(getLocalMember().getIP())+1);
        sendMessage("sent first but should be received last", getCurrentGroup());
        messageOrdering.getGroupVectorClock().getClock().put(getLocalMember().getIP(), messageOrdering.getGroupVectorClock().getClock().get(getLocalMember().getIP()) - 2);
        sendMessage("sent last but should be received first",getCurrentGroup());

    }
}
