package NameServer;

import Interface.Constants;
import Interface.NameServiceRemote;
import Middleware.Member;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by c12jbr on 2015-10-05.
 */
public class NameServer extends UnicastRemoteObject implements NameServiceRemote {

    private Map<String, Member> map;

    public static void main(String[] args) {

        try {
            new NameServer();

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }
        while(true){
        }

    }

    public NameServer() throws RemoteException, AlreadyBoundException {
        super();
        map = Collections.synchronizedMap(new HashMap<String,Member>());
        Registry register = LocateRegistry.createRegistry(Constants.NAME_SERVICE_PORT);
        register.bind(Constants.NAME_SERVICE_ID,this);
    }


    @Override
    public Member getLeader(String groupName) throws RemoteException {
       return map.get(groupName);
    }

    @Override
    public void setLeader(Member leader,String groupName) throws RemoteException {
        map.put(groupName,leader);
        System.out.println("Grupp: "+groupName+ "ledare: "+leader.getName());
    }
}
