package Interface;

import Middleware.Group;
import Middleware.Message;
import Middleware.VectorClock;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by c12jbr on 2015-10-05.
 */
public interface MyRemote extends Remote{

    public void receiveMulticast(Message message) throws RemoteException;

}
