package Middleware;

import java.io.Serializable;

/**
 * Created by oi12pjn on 2015-10-07.
 */
public class Member implements Serializable{

    private String ip;
    private int port;

    public Member(String ip, int port) {
        this.port = port;
        this.ip = ip;
    }

    public String getIP() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return ip+","+port;
    }

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
    public int hashCode(){
        return this.hashCode();
    }

}
