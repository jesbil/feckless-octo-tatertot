package Interface;

import Middleware.Member;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * Created by oi12pjn on 2015-12-02.
 *
 * Remote methods used in NameServerCommunicator
 */
public interface NameServiceRemote extends Remote {

    /**
     * Returns the leader from the desired group
     * @param groupName
     * @return
     * @throws RemoteException
     */
    public Member getLeader(String groupName) throws RemoteException;

    /**
     * Sets the leader for a group
     * @param leader
     * @param groupName
     * @throws RemoteException
     */
    public void setLeader(Member leader, String groupName) throws RemoteException;
}
