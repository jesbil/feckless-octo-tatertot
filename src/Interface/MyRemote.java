package Interface;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by c12jbr on 2015-10-05.
 */
public interface MyRemote extends Remote{

    public boolean is(String str) throws RemoteException;

}
