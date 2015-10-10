package Middleware;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by oi12pjn on 2015-10-07.
 */
public class GroupManagement {
    private Group allMembers;
    private ArrayList<Group> groups;
    private Member localMember;


    public GroupManagement() throws UnknownHostException {
        groups = new ArrayList<Group>();
        allMembers = new Group("allMembers");
        localMember = new Member(InetAddress.getLocalHost().getHostAddress());
    }

    public boolean createGroup(String name) {
        for(Group g : groups){
            if(g.getName().equals(name)){
                return false;
            }
        }
        groups.add(new Group(name));
        groups.get(groups.size()-1).addMemberToGroup(localMember);
        return true;
        //SKICKA VIDARE TILL MESSAGE ORDERING
    }

    public void joinGroup(String name) throws UnknownHostException {
        for (int i = 0; i <groups.size() ; i++) {
            if (groups.get(i).getName().equals(name)){
                groups.get(i).addMemberToGroup(localMember);
            }
        }

        //SKICKA VIDARE TILL MESSAGE ORDERING
    }

    public void leaveGroup(String name) {
        for (int i = 0; i <groups.size() ; i++) {
            if (groups.get(i).getName().equals(name)){
                groups.get(i).removeMemberFromGroup(localMember);
            }
        }

        //SKICKA VIDARE
    }

    public void sendMessage(String msg){



    }


    public Group getAllMembers() {
        return allMembers;
    }

    public Member getLocalMember() {
        return localMember;
    }

    public void groupCreated(Group group) {
        groups.add(group);
    }
}
