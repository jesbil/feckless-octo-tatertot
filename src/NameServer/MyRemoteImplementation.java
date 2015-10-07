package NameServer;

import Interface.MyRemote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by c12jbr on 2015-10-05.
 */
public class MyRemoteImplementation extends UnicastRemoteObject implements MyRemote {

    public MyRemoteImplementation() throws RemoteException {
        super();
    }

    @Override
    public boolean is(String str) throws RemoteException {
        if(str.equals("is")){
            System.out.println("hej");
            return true;
        }
        System.out.println("nej");
        return false;
    }
}
