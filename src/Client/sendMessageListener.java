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
public class sendMessageListener implements ActionListener {
    private static JTextArea writeField;

    public sendMessageListener(JTextArea writeField) {
        this.writeField = writeField;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        //TODO RIKTIGT GROUPNAME
        try {
//            String a = "09876543210987654321098765432109876543210987654321";
//            for (int i = 0; i < 50; i++) {
//                GCom.sendMessage(writeField.getText()+a.substring(50-i),GCom.getCurrentGroup());
//            }
            GCom.sendMessage(writeField.getText(),GCom.getCurrentGroup());
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
