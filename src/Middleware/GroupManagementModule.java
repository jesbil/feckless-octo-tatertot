package Middleware;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by oi12pjn on 2015-10-07.
 */
public class GroupManagementModule {
    private Group allMembers;

    private ArrayList<Group> groups;
    private ArrayList<Group> joinedGroups;
    private Member localMember;

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public Group getAllMembers() {
        return allMembers;
    }

    public Member getLocalMember() {
        return localMember;
    }


    public GroupManagementModule() throws UnknownHostException{
        groups = new ArrayList<>();
        allMembers = new Group("allMembers");
        localMember = new Member(InetAddress.getLocalHost().getHostAddress(),Integer.parseInt(GCom.getPort()));
    }

    public void addMemberToGroup(String groupName, Member sender) {
        getGroupByName(groupName).addMemberToGroup(sender);
        if(sender.equals(localMember)){
            joinedGroups.add(getGroupByName(groupName));
        }
    }

    public void removeMemberFromGroup(String groupName, Member sender) {
        getGroupByName(groupName).addMemberToGroup(sender);
        if(sender.equals(localMember)){
            joinedGroups.remove(getGroupByName(groupName));
        }
    }

    public void removeGroup(String groupName){
        groups.remove(getGroupByName(groupName));
    }

    public void groupCreated(String message, Member sender) {
        Group group = new Group(message);
        group.addMemberToGroup(sender);
        if(sender.equals(localMember)){
            joinedGroups.add(group);
        }
    }

    public void setAllMembers(ArrayList<Member> mlist){
        for(Member m: mlist){
            allMembers.addMemberToGroup(m);
        }
    }

    public Group getGroupByName(String groupName) {
        if(groupName.equals(allMembers.getName())){
            return allMembers;
        }
        for(Group group : groups){
            if(group.getName().equals(groupName)){
                return group;
            }
        }
        return null;
    }

    public ArrayList<Group> getJoinedGroups() {
        return joinedGroups;
    }
}
