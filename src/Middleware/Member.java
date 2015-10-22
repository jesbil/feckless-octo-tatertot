package Middleware;

/**
 * Created by oi12pjn on 2015-10-07.
 */
public class Member {

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
}
