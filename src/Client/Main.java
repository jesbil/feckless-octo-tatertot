package Client;

import Middleware.GCom;
import Middleware.Message;

import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;

/**
 * Created by c12jbr on 2015-10-10.
 */
public class Main {

    public static void main(String[] args) {

        try {
            GUI gui = new GUI();
            GCom.initiate();

            GCom.connectToNameService(gui.nameServerRequest());
            gui.buildAndStart("GCom");

            //GRUPPEN FINS INTE I GMQ!!! ""balle

            while(true){
                Message message;
                if((message=GCom.getNextMessage("balle"))!=null){
                    gui.getChatField().append(message.getSender()+": "+message.getMessage()+"\n");
                    gui.update();
                }
            }

        } catch (UnknownHostException e) {
            //GCOM initiate
            e.printStackTrace();
        } catch (IOException e) {
            //connect to nameService
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            //GCOM initiate
            e.printStackTrace();
        }

    }

}
