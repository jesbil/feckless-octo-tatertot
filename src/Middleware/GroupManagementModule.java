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

    public ArrayList<Group> getGroups() {
        return groups;
    }
    private String  currentGroup;
    private ArrayList<Group> groups;
    private Member localMember;


    public GroupManagementModule() throws UnknownHostException{
        groups = new ArrayList<Group>();
        allMembers = new Group("allMembers");
        localMember = new Member(InetAddress.getLocalHost().getHostAddress(),Integer.parseInt(GCom.getPort()));
    }

    public boolean createGroup(String name) throws GroupException {
        if(currentGroup!=null){
            throw new GroupException("You are already in a group");
        }
        for(Group g : groups){
            if(g.getName().equals(name)){
                return false;
            }
        }
        groups.add(new Group(name));
        groups.get(groups.size()-1).addMemberToGroup(localMember);
        currentGroup=name;
        return true;
    }

    public void joinGroup(String name) throws GroupException {
        if(currentGroup!=null){
            throw new GroupException("You are already in a group");
        }
        getGroupByName(name).addMemberToGroup(localMember);
        if(name!=allMembers.getName()){
            currentGroup=name;
        }
    }

    public void leaveGroup(String name) {
        getGroupByName(name).removeMemberFromGroup(localMember);
        currentGroup=null;
    }

    public void sendMessage(String msg){

    }
    public void removeGroup(String name){
        groups.remove(getGroupByName(name));
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

    private Member findMember(String name, Group g){
        ArrayList<Member> temp = g.getMembers();
        for(Member m: temp){
            String name1 = m.getIP()+","+m.getPort();
            if(name1.equals(name)){
                return m;
            }
        }
        return null;
    }

    public void addMemberToGroup(String name, String groupName, int port) {
        getGroupByName(groupName).addMemberToGroup(new Member(name.substring(0,name.indexOf(",")),port));
    }

    public void removeMemberFromGroup(String groupName, String name) {
        getGroupByName(groupName).removeMemberFromGroup(findMember(name,getGroupByName(groupName)));

    }

    public String getCurrentGroup() {
        return currentGroup;
    }

}
