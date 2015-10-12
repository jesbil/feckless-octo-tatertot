package Middleware;

/**
 * Created by c12jbr on 2015-10-12.
 */
public class GroupException extends Exception {
    private String exceptionMessage;

    @Override
    public String getMessage() {
        return exceptionMessage+" - "+ GCom.getCurrentGroup();
    }

    public GroupException(String s) {
        exceptionMessage = s;
    }
}
