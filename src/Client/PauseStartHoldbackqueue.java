package Client;

import Middleware.GCom;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by c12jbr on 2015-10-14.
 */
public class PauseStartHoldbackqueue implements ActionListener {
    boolean paused;

    public PauseStartHoldbackqueue(){
        paused=false;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        paused = !paused;
        GCom.pauseStartHbq(paused);

    }
}
