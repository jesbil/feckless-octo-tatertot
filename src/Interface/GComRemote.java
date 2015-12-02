package Interface;

import Middleware.Group;
import Middleware.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by c12jbr on 2015-10-05.
 */
public interface GComRemote extends Remote{

    public void receiveMulticast(Message message) throws RemoteException;

    public Group retrieveGroup(String groupName) throws RemoteException;
}
