package Interface;

import Middleware.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by c12jbr on 2015-10-05.
 */
public interface MyRemote extends Remote{

    public void createGroup(String name) throws RemoteException;

    public void joinGroup(String name) throws RemoteException;

    public void leaveGroup(String name) throws RemoteException;

    public void message(Message message) throws RemoteException;




}
