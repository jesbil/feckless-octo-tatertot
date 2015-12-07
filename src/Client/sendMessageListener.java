package Client;

import Middleware.GCom;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;

/**
 * Created by c12jbr on 2015-10-11.
 *
 * actionlistener for the send message button
 */
public class sendMessageListener implements ActionListener {
    private static JTextArea writeField;

    /**
     * sets the writefield that the button fetches text from.
     * @param writeField
     */
    public sendMessageListener(JTextArea writeField) {
        this.writeField = writeField;
    }

    /**
     * sends the message to the middleware
     * @param actionEvent
     */
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            GCom.sendMessage(writeField.getText(),null);
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
