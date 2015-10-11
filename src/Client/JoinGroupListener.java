package Client;

import Middleware.GCom;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        GCom.joinGroup(groupName);
    }
}
