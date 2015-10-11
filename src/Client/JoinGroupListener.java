package Client;

import Middleware.GCom;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by c12jbr on 2015-10-11.
 */
public class JoinGroupListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String groupName = null;
        while (groupName==null){
            groupName = JOptionPane.showInputDialog(null, "What group do you want to join", "balle");
        }
        try {
            GCom.joinGroup(groupName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }
}