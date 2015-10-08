package Middleware;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by oi12pjn on 2015-10-07.
 */
public class GroupManagement {
    private ArrayList<Member> members;
    private ArrayList<Group> groups;
    private Member LocalMember;


    public GroupManagement() throws UnknownHostException {
        groups = new ArrayList<Group>();
        members = new ArrayList<Member>();
        LocalMember = new Member(InetAddress.getLocalHost().getHostAddress());
    }

    public void createGroup(String name) {
        groups.add(new Group(name));

        //SKICKA VIDARE TILL MESSAGE ORDERING
    }

    public void joinGroup(String name) throws UnknownHostException {
        for (int i = 0; i <groups.size() ; i++) {
            if (groups.get(i).getName().equals(name)){
                groups.get(i).addMemberToGroup(LocalMember);
            }
        }

        //SKICKA VIDARE TILL MESSAGE ORDERING
    }

    public void leaveGroup(String name) {
        for (int i = 0; i <groups.size() ; i++) {
            if (groups.get(i).getName().equals(name)){
                groups.get(i).removeMemberFromGroup(LocalMember);
            }
        }

        //SKICKA VIDARE
    }

    public void sendMessage(String msg){



    }


}
