package Client;

import Middleware.GCom;

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
