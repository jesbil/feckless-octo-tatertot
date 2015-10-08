package NameServer;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import java.util.ArrayList;

/**
 * Created by c12jbr on 2015-10-05.
 */
public class NameServer {


    private static final int port = 4444;
    private static DatagramSocket serverSocket;
    private ArrayList<String> members = new ArrayList<String>();




    public static void main(String[] args) {
        NameServer nameServer = new NameServer();
        try {
            serverSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        System.out.println("Naming service Server Started");
        byte[] receiveData = new byte[3];

        while(true) {
            DatagramPacket receivePacket = new DatagramPacket(
                    receiveData, receiveData.length);
            try {
                System.out.println("waiting for receive");
                serverSocket.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String pw = new String( receivePacket.getData());
            System.out.println(pw.length());
            System.out.println("Received: "+pw);
            if(pw.equals("hej")) {
                System.out.println("hej hej");
                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();
                try {
                    nameServer.sendMembers(IPAddress, port);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    public void sendMembers(InetAddress IPAddress, int port) throws IOException {
        byte[] nrOfMembers = BigInteger.valueOf(members.size()).toByteArray();
        byte[] nomAs4bytes = new byte[4];
        switch (nrOfMembers.length){
            case 1:
                nomAs4bytes[3] = nrOfMembers[0];
                break;
            case 2:
                nomAs4bytes[2] = nrOfMembers[0];
                nomAs4bytes[3] = nrOfMembers[1];
                break;
            case 3:
                nomAs4bytes[1] = nrOfMembers[2];
                nomAs4bytes[2] = nrOfMembers[1];
                nomAs4bytes[3] = nrOfMembers[0];
                break;
            case 4:
                nomAs4bytes[0] = nrOfMembers[0];
                nomAs4bytes[1] = nrOfMembers[1];
                nomAs4bytes[2] = nrOfMembers[2];
                nomAs4bytes[3] = nrOfMembers[3];
                break;
        }
        serverSocket.send(new DatagramPacket(nomAs4bytes,nomAs4bytes.length,IPAddress,port));
        int i=0;
        for(String member : members){
            System.out.println("sent member: "+ members.get(i));
            serverSocket.send(new DatagramPacket(member.getBytes(), member.length(), IPAddress, port));
            i++;
        }


    }





}
