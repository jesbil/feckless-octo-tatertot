package Middleware;

import java.io.Serializable;

/**
 * Created by oi12pjn on 2015-10-07.
 *
 * A member that can join groups and communicate with other members
 */
public class Member implements Serializable{

    private String ip;
    private int port;

    /**
     * Constructor
     * @param ip
     * @param port
     */
    public  Member(String ip, int port) {
        this.port = port;
        this.ip = ip;
    }

    /**
     *
     * @return ip
     */
    public  String getIP() {
        return ip;
    }

    /**
     *
     * @return port
     */
    public  int getPort() {
        return port;
    }

    /**
     *
     * @return name
     */
    public  String getName() {
        return ip+","+port;
    }

    /**
     * Equals function on ip and port
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj){
        if(obj instanceof Member){
            if(((Member) obj).getIP().equals(ip)){
                if(((Member) obj).getPort()==port){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    /**
     * Hahscode that return the port
     */
    public int hashCode(){
        return port;
    }

}
