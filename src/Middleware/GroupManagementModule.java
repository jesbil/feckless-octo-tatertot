package Middleware;

import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by oi12pjn on 2015-10-07.
 */
public class GroupManagementModule {
    private Group allMembers;

    private ArrayList<Group> groups;
    private ArrayList<Group> joinedGroups;
    private Member localMember;

    protected ArrayList<Group> getGroups() {
        return groups;
    }

    protected Group getAllMembers() {
        return allMembers;
    }

    protected GroupManagementModule(Member localMember) throws UnknownHostException{
        groups = new ArrayList<>();
        joinedGroups = new ArrayList<>();
        this.localMember = localMember;
        allMembers = new Group("allMembers",Integer.MAX_VALUE);
    }

    protected void addMemberToGroup(String groupName, Member sender){
        getGroupByName(groupName).addMemberToGroup(sender);
        if(sender.equals(localMember)){
            joinedGroups.add(getGroupByName(groupName));
        }
    }

    protected void removeMemberFromGroup(String groupName, Member sender) {
        getGroupByName(groupName).removeMemberFromGroup(sender);
        if(sender.equals(localMember)){
            joinedGroups.remove(getGroupByName(groupName));
        }
    }

    protected void removeGroup(String groupName){
        groups.remove(getGroupByName(groupName));
    }

    protected void groupCreated(String message, Member sender) {
        String[] splitted = message.split("#");
        Group group = new Group(splitted[0],Integer.parseInt(splitted[1]));
        group.addMemberToGroup(sender);
        groups.add(group);
        if(sender.equals(localMember)){
            joinedGroups.add(group);
        }
    }

    protected void setAllMembers(ArrayList<Member> mlist) {
        for(Member m: mlist){
            allMembers.addMemberToGroup(m);
        }
    }

    protected Group getGroupByName(String groupName) {
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

    protected ArrayList<Group> getJoinedGroups() {
        return joinedGroups;
    }

    protected void removeMemberFromAllGroups(Member member) {
        for(Group g: groups){
            removeMemberFromGroup(g.getName(),member);
        }
        allMembers.removeMemberFromGroup(member);
    }
}
