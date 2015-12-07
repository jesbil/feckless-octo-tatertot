package Client;

import Middleware.GCom;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by c12jbr on 2015-10-08.
 *
 * Actionlistener for create group button
 */
public class CreateGroupListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String groupName = JOptionPane.showInputDialog(null, "Name your group", "groupName");
        if(groupName!=null){
            try {
                GCom.createGroup(groupName);
            } catch (NotBoundException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
