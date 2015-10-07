package Middleware;

import Interface.Constants;
import Interface.MyRemote;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by c12jbr on 2015-10-05.
 */
public class NameServerCommunicator {

    public static void main(String[] args) throws RemoteException, NotBoundException {
        Registry reg = LocateRegistry.getRegistry("localhost", Constants.port);
        MyRemote myrem = (MyRemote) reg.lookup(Constants.RMI_ID);
        System.out.println(myrem.is("is"));
        System.out.println(myrem.is("isa"));
    }

}
