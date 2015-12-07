package Middleware;

/**
 * Created by c12jbr on 2015-10-12.
 * An exception for groups
 */
public class GroupException extends Exception {
    private String exceptionMessage;
    private Member member;

    @Override
    /**
     * returns the exception message
     */
    public String getMessage() {
        return exceptionMessage+" - ";
    }

    /**
     * returns a member from the message
     * @return
     */
    protected Member getMember(){
        return member;
    }

    /**
     * Constructor
     * @param s
     * @param m
     */
    public GroupException(String s, Member m) {
        exceptionMessage = s;
        member = m;
    }
}
