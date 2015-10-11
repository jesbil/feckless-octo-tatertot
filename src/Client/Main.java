package Client;

import Middleware.GCom;
import Middleware.Message;

import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.util.ArrayList;

/**
 * Created by c12jbr on 2015-10-10.
 */
public class Main {
    private static ArrayList<String> groupNames = new ArrayList<String>();

    public static void main(String[] args) {
        try {
            GUI gui = new GUI();
            GCom.initiate();

            GCom.connectToNameService(gui.nameServerRequest());
            gui.buildAndStart("GCom");


            while(true){

                boolean changed = false;


                if(GCom.getGroupNames()!=null){
                    for(String groupName : GCom.getGroupNames()){
                        if(!groupNames.contains(groupName)){
                            gui.getJtaNameList().setText("");
                            changed = true;
                            groupNames.add(groupName);
                            gui.getJtaNameList().append(groupName);
                        }

                    }
                }

                Message message;
                if((message=GCom.getNextMessage("balle"))!=null){
                    gui.getChatField().append(message.getSender() + ": " + message.getMessage() + "\n");
                    changed = true;
                }

                if(changed){
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
