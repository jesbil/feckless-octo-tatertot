package Client;

import Middleware.GCom;
import Middleware.GroupException;
import Middleware.Message;

import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by c12jbr on 2015-10-10.
 */
public class Main {
    private static ArrayList<String> groupNames = new ArrayList<String>();
    private static OpenDebuggerListener debugger;

    public static void main(String[] args) {
        try {
            GCom gcom = new GCom();
            GUI gui = new GUI();

            String nameService = gui.nameServerRequest();
            if(nameService==null){
                return;
            }
            gcom.initiate(gui.askUnordered(), gui);
            gui.buildAndStart("GCom");
            gcom.connectToNameService(nameService);


        } catch (UnknownHostException e) {
            //GCOM initiate
            e.printStackTrace();
        } catch (IOException e) {
            //connect to nameService
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            //GCOM initiate
            e.printStackTrace();
        } catch (NotBoundException e) {
            // GCOM join i initiate
            e.printStackTrace();
        } catch (GroupException e) {
            System.err.println(e.toString());
        }

    }
}
