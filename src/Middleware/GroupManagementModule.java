package Middleware;

import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by oi12pjn on 2015-10-07.
 * A module that handles the groups in GCom
 */
public class GroupManagementModule {

    private ArrayList<Group> joinedGroups;
    private Member localMember;

    /**
     * Constructor
     *
     * @param localMember
     * @throws UnknownHostException
     */
    protected GroupManagementModule(Member localMember) throws UnknownHostException{
        joinedGroups = new ArrayList<>();
        this.localMember = localMember;
    }

    /**
     * Adds a member to a specified group
     * @param groupName - the group
     * @param sender - member to be added
     */
    protected void addMemberToGroup(String groupName, Member sender){
        getGroupByName(groupName).addMemberToGroup(sender);
    }

    /**
     * Removes a member from a group
     * @param groupName - the group
     * @param sender - the member
     */
    protected void removeMemberFromGroup(String groupName, Member sender) {
        getGroupByName(groupName).removeMemberFromGroup(sender);
        if(sender.equals(localMember)){
            joinedGroups.remove(getGroupByName(groupName));
        }
    }

    /**
     * Removes a group from the module
     * @param groupName
     */
    protected void removeGroup(String groupName){
        joinedGroups.remove(getGroupByName(groupName));
    }

    /**
     * Adds a new group to the module
     * @param g
     */
    protected void addGroup(Group g){
        joinedGroups.add(g);
    }

    /**
     * Returns the group with the specified group name
     * @param groupName
     * @return the group
     */
    protected Group getGroupByName(String groupName) {

        for(Group group : joinedGroups){
            if(group.getName().equals(groupName)){
                return group;
            }
        }
        return null;
    }

    /**
     * Returns tall of the groups currently joined
     * @return groups
     */
    protected ArrayList<Group> getJoinedGroups() {
        return joinedGroups;
    }

    /**
     * Removes a member from all groups
     * @param member
     */
    protected void removeMemberFromAllGroups(Member member) {
        for(Group g: joinedGroups){
            removeMemberFromGroup(g.getName(),member);
        }
    }
}
