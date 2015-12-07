package Client;

import Middleware.GCom;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.rmi.NotBoundException;

/**
 * Created by c12jbr on 2015-10-11.
 *
 * Actionlistener for leave group button
 */
public class LeaveGroupListener implements ActionListener {


    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String groupName = JOptionPane.showInputDialog(null, "What group do you want to leave", "groupName");
        if(groupName!=null){
            try {
                GCom.leaveGroup(groupName);
            } catch (NotBoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
