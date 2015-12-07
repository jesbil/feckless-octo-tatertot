package Interface;

import Middleware.Group;
import Middleware.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by c12jbr on 2015-10-05.
 *
 *
 * Remote methods used in Communication module
 */
public interface GComRemote extends Remote{
    /**
     * Receives a multicast from a remote user
     * @param message
     * @throws RemoteException
     */
    public void receiveMulticast(Message message) throws RemoteException;

    /**
     * Returns a group from the group leader
     * @param groupName
     * @return
     * @throws RemoteException
     */
    public Group retrieveGroup(String groupName) throws RemoteException;
}
