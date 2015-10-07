package NameServer;

import Interface.Constants;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Hashtable;
import java.util.Random;

/**
 * Created by c12jbr on 2015-10-05.
 */
public class NameServer {
    private static Hashtable<Integer,String> state =
            new Hashtable<Integer, String>();
    private static Hashtable<Integer,String> color =
            new Hashtable<Integer, String>();
    private static Hashtable<Integer,String> creature =
            new Hashtable<Integer, String>();

    private static final int port = 4444;
    private static DatagramSocket serverSocket;

    
    
    
    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
//        try {
//            serverSocket = new DatagramSocket(port);
//        } catch (SocketException e) {
//            System.out.println("port is probably in use already");
//        }
//        initializeNameTables();
//        System.out.println("Naming service Server Started");
//        byte[] receiveData = new byte[1024];
//
//        while(true) {
//            DatagramPacket receivePacket = new DatagramPacket(
//                    receiveData, receiveData.length);
//            try {
//                serverSocket.receive(receivePacket);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            String sentence = new String( receivePacket.getData());
//            System.out.println("Received: "+sentence);
//            InetAddress IPAddress = receivePacket.getAddress();
//            int port = receivePacket.getPort();
//            byte[] sendData = generateName().getBytes();
//            DatagramPacket sendPacket = new DatagramPacket(
//                    sendData, sendData.length, IPAddress, port);
//            try {
//                serverSocket.send(sendPacket);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        MyRemoteImplementation impl = new MyRemoteImplementation();
        Registry reg = LocateRegistry.createRegistry(Constants.port);
        reg.bind(Constants.RMI_ID, impl);

    }


    private static String generateName(){
        Random rn = new Random();
        return state.get(rn.nextInt(state.size())) + color.get(rn.nextInt(color.size())) + creature.get(rn.nextInt(creature.size()));
    }


    private static void initializeNameTables() {

        state.put(0, "Rabid");
        state.put(1, "Disgusting");
        state.put(2, "Kinky");

        color.put(0, "Blue");
        color.put(1, "Red");
        color.put(2, "Green");

        creature.put(0,"Donkey");
        creature.put(1,"Monkey");
        creature.put(2,"Seahorse");

    }



}
