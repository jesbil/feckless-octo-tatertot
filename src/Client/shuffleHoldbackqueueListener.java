package Client;

import Middleware.GCom;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by c12jbr on 2015-11-02.
 */
public class shuffleHoldbackqueueListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        GCom.shuffleHbq();
    }
}
