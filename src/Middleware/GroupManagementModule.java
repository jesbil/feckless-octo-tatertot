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
    private Member localMember;


    public GroupManagementModule() throws UnknownHostException, RemoteException {
        groups = new ArrayList<Group>();
        allMembers = new Group("allMembers");
        localMember = new Member(InetAddress.getLocalHost().getHostAddress());
    }

    public boolean createGroup(String name) throws RemoteException {
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
        for (int i = 0; i <groups.size() ; i++) {
            System.out.println(groups.get(i).getName());
        }
    }

    public void setAllMembers(ArrayList<Member> mlist){
        for(Member m: mlist){
            allMembers.addMemberToGroup(m);

        }
    }

    public Group getGroupByName(String groupName) {
        for(Group group : groups){
            if(group.getName().equals(groupName)){
                return group;
            }
        }
        return null;
    }

    public void addMemberToGroup(String name, String groupName) {
        getGroupByName(groupName).addMemberToGroup(new Member(name));
    }
}
