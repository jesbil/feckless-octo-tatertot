package Middleware;

import java.io.IOException;
import java.math.BigInteger;
import java.net.*;

import java.util.ArrayList;

/**
 * Created by c12jbr on 2015-10-05.
 */
public class NameServerCommunicator {
    private ArrayList<Member> members;

    //    public static void main(String[] args) throws RemoteException, NotBoundException {
//        Registry reg = LocateRegistry.getRegistry("localhost", Constants.port);
//        MyRemote myrem = (MyRemote) reg.lookup(Constants.RMI_ID);
//        System.out.println(myrem.is("is"));
//        System.out.println(myrem.is("isa"));
//    }
    public static void main(String[] args) {
        NameServerCommunicator asd = new NameServerCommunicator();
        try {
            asd.retrieveMembers("Draco");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public NameServerCommunicator(){
        members=new ArrayList<Member>();
    }

    public ArrayList<Member> retrieveMembers(String nameServiceAddress) throws IOException {

        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName(nameServiceAddress);
        byte[] sendData = "hej".getBytes();
        byte[] nrOfMemz = new byte[4];
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 4444);
        clientSocket.send(sendPacket);
        DatagramPacket receiveIntPacket = new DatagramPacket(nrOfMemz, nrOfMemz.length);
        clientSocket.receive(receiveIntPacket);

        int numberOfMembers= new BigInteger(receiveIntPacket.getData()).intValue();

        for (int i = 0; i < numberOfMembers; i++) {
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            Member m = new Member(new String(receivePacket.getData()).trim());
            members.add(m);


        }

        clientSocket.close();
        return members;
    }


    public void leave(String nameServiceAddress) throws IOException {
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName(nameServiceAddress);
        byte[] sendData = "baj".getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 4444);
        clientSocket.send(sendPacket);
    }
}
