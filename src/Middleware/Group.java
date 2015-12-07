package Middleware;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by oi12pjn on 2015-10-07.
 *
 * A group used for communication in GCom
 */
public class Group implements Serializable {

    private String name;
    private ArrayList<Member> members;
    private VectorClock vectorClock;
    private Member leader;

    /**
     * @return leader
     */
    public Member getLeader() {
        return leader;
    }

    /**
     * Sets the leader in the group
     *
     * @param leader
     */
    public void setLeader(Member leader) {
        this.leader = leader;
    }

    /**
     * @return members
     */
    public ArrayList<Member> getMembers() {
        return members;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @return vectorClock
     */
    public VectorClock getVectorClock() {
        return vectorClock;
    }

    /**
     * Constructor
     *
     * @param name
     */
    public Group(String name) {
        members = new ArrayList<>();
        this.name = name;
        vectorClock = new VectorClock();
    }

    /**
     * Constructor to make a copy of a group
     *
     * @param g
     */
    public Group(Group g) {
        this.name = g.getName();
        this.members = new ArrayList<>(g.getMembers());
        this.vectorClock = g.getVectorClock();
    }

    /**
     * Adds a member to the group
     *
     * @param m - the member
     */
    public void addMemberToGroup(Member m) {
        if (!members.contains(m)) {
            members.add(m);
            if (vectorClock.getClock().get(m.getName()) == null) {
                vectorClock.getClock().put(m.getName(), 0);
            }
        }
    }

    /**
     * Removes member m from the group
     *
     * @param m
     */
    public void removeMemberFromGroup(Member m) {
        members.remove(m);
    }

    /**
     * equals on groupname
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Group) {
            if (((Group) obj).getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Hashcode method
     *
     * @return
     */
    @Override
    public int hashCode() {
        return 66667;
    }

    /**
     * returns a member
     * @param message - member name yo
     * @return
     */
    public Member getMember(String message) {
        for(int i=0;i<members.size();i++){
            if(members.get(i).getName().equals(message)){
                return members.get(i);
            }
        }
        return null;
    }
}