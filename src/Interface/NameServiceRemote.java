package Interface;

import Middleware.Member;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * Created by oi12pjn on 2015-12-02.
 */
public interface NameServiceRemote extends Remote {

    public Member getLeader(String groupName) throws RemoteException;
    public void setLeader(Member leader, String groupName) throws RemoteException;
}
