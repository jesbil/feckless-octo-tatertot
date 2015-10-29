package Client;

import Middleware.GCom;
import Middleware.GroupException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by c12jbr on 2015-10-11.
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
            } catch (GroupException e) {
                System.out.println(e.getMessage());
            }
        }


    }
}
