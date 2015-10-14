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



    public void createGroup(String groupName, String leader, VectorClock vc) throws RemoteException;

    public void joinGroup(String name, String groupname, String groupJoined, VectorClock vc) throws RemoteException;

    public void leaveGroup(String name, String groupname, VectorClock vc) throws RemoteException;



    public void message(String message, String sender, String groupName, VectorClock vc) throws RemoteException;

    public void removeGroup(String groupName,String name, VectorClock vc) throws RemoteException;

}
