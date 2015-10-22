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
    private static ArrayList<String> members = new ArrayList<String>();

    public static void main(String[] args) {
        NameServer nameServer = new NameServer();
        try {
            serverSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        System.out.println("Naming service Server Started");
        byte[] receiveData = new byte[1024];

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
            pw.trim();
            if(pw.startsWith("hej")) {
                System.out.println(pw);
                InetAddress iPAddress = receivePacket.getAddress();
                members.add(iPAddress.toString().substring(1) + "," + pw.substring(pw.indexOf(",")+1));
                System.out.println("Added Member: "+iPAddress.toString().substring(1)+pw.substring(pw.indexOf(",")));
                int port = receivePacket.getPort();
                try {
                    nameServer.sendMembers(iPAddress, port, iPAddress.toString().substring(1)+","+pw.substring(pw.indexOf(",")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(pw.startsWith("baj")) {
                InetAddress iPAddress = receivePacket.getAddress();
                members.remove(iPAddress.toString().substring(1)+","+pw.substring(pw.indexOf(",")));
                System.out.println("Removed Member: "+iPAddress.toString().substring(1)+pw.substring(pw.indexOf(",")));
            }
        }
    }

    private void sendMembers(InetAddress IPAddress, int port, String currentMember) throws IOException {
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
        for(String member : members){
            if(member.equals(currentMember)){
            }else{
                System.out.println("sent member: "+ member +" to: "+ IPAddress);
                serverSocket.send(new DatagramPacket(member.getBytes(), member.length(), IPAddress, port));
            }
        }


    }





}
