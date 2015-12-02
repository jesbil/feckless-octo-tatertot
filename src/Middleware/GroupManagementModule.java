package Middleware;

import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by oi12pjn on 2015-10-07.
 */
public class GroupManagementModule {

    private ArrayList<Group> joinedGroups;
    private Member localMember;

    protected GroupManagementModule(Member localMember) throws UnknownHostException{
        joinedGroups = new ArrayList<>();
        this.localMember = localMember;
    }

    protected void addMemberToGroup(String groupName, Member sender){
        getGroupByName(groupName).addMemberToGroup(sender);
    }

    protected void removeMemberFromGroup(String groupName, Member sender) {
        getGroupByName(groupName).removeMemberFromGroup(sender);
        if(sender.equals(localMember)){
            joinedGroups.remove(getGroupByName(groupName));
        }
    }

    protected void removeGroup(String groupName){
        joinedGroups.remove(getGroupByName(groupName));
    }

    protected void addGroup(Group g){
        joinedGroups.add(g);
    }

    protected Group getGroupByName(String groupName) {

        for(Group group : joinedGroups){
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
        for(Group g: joinedGroups){
            removeMemberFromGroup(g.getName(),member);
        }
    }
}
