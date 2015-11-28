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

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public Group getAllMembers() {
        return allMembers;
    }

    public GroupManagementModule(Member localMember) throws UnknownHostException{
        groups = new ArrayList<>();
        joinedGroups = new ArrayList<>();
        this.localMember = localMember;
        allMembers = new Group("allMembers",Integer.MAX_VALUE);
    }

    public void addMemberToGroup(String groupName, Member sender){
        getGroupByName(groupName).addMemberToGroup(sender);
        if(sender.equals(localMember)){
            joinedGroups.add(getGroupByName(groupName));
        }
    }

    public void removeMemberFromGroup(String groupName, Member sender) {
        getGroupByName(groupName).removeMemberFromGroup(sender);
        if(sender.equals(localMember)){
            joinedGroups.remove(getGroupByName(groupName));
        }
    }

    public void removeGroup(String groupName){
        groups.remove(getGroupByName(groupName));
    }

    public void groupCreated(String message, Member sender) {
        String[] splitted = message.split("#");
        Group group = new Group(splitted[0],Integer.parseInt(splitted[1]));
        group.addMemberToGroup(sender);
        groups.add(group);
        if(sender.equals(localMember)){
            joinedGroups.add(group);
        }
    }

    public void setAllMembers(ArrayList<Member> mlist) {
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
    
    public void removeMemberFromAllGroups(Member member) {
        for(Group g: groups){
            removeMemberFromGroup(g.getName(),member);
        }
        allMembers.removeMemberFromGroup(member);
    }
}
