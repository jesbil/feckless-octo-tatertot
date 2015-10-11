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
            GCom.sendMessage(writeField.getText(),Main.getCurrentGroup());
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
