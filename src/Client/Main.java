package Client;

import Middleware.GCom;
import Middleware.GroupException;
import Middleware.Message;

import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.util.ArrayList;

/**
 * Created by c12jbr on 2015-10-10.
 */
public class Main {
    private static ArrayList<String> groupNames = new ArrayList<String>();

    public static void main(String[] args) {
        try {
            GUI gui = new GUI();

            String nameService = gui.nameServerRequest();
            if(nameService==null){
                return;
            }
            GCom.initiate(gui.askUnordered());
            GCom.connectToNameService(nameService);
            gui.buildAndStart("GCom");


            while(true){

                boolean changed = false;


                if(GCom.getGroupNames()!=null){
                    for(String groupName : GCom.getGroupNames()){
                        if(!groupNames.contains(groupName)){
                            changed = true;
                            groupNames.add(groupName);
                        }

                    }
                }
                for (int i = 0; i < groupNames.size(); i++) {
                    if(!GCom.getGroupNames().contains(groupNames.get(i))){
                        groupNames.remove(i);
                        i--;
                        changed = true;
                    }
                }

                Message message;
                if((message=GCom.getNextMessage(GCom.getCurrentGroup()))!=null){
                    gui.getChatField().append(message.getSender() + ": " + message.getMessage() + "\n");
                    changed = true;
                }

                if(changed){
                    gui.getJtaNameList().setText("");
                    for(String groupName : groupNames){
                        gui.getJtaNameList().append(groupName+"\n");
                    }
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
        } catch (NotBoundException e) {
            // GCOM join i initiate
            e.printStackTrace();
        } catch (GroupException e) {
            System.err.println(e.toString());
        }

    }

}
