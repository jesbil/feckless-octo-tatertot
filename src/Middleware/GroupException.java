package Middleware;

/**
 * Created by c12jbr on 2015-10-12.
 */
public class GroupException extends Exception {
    private String exceptionMessage;
    private Member member;

    @Override
    public String getMessage() {
        return exceptionMessage+" - ";
    }

    protected Member getMember(){
        return member;
    }

    public GroupException(String s, Member m) {
        exceptionMessage = s;
        member = m;
    }
}
