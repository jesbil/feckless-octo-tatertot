package Middleware;

import java.util.Observable;

/**
 * Created by c12jbr on 2015-11-24.
 */
public class Debugg extends Observable implements Runnable {
    private boolean debugging;

    public void run() {
        debugging = true;
        while(debugging){
            while(GCom.getDebuggLog().size()>0){
                setChanged();
                notifyObservers(GCom.getDebuggLog().get(0));
                GCom.getDebuggLog().remove(0);
            }

            if(GCom.getHoldBackQueue().size()>0){
                setChanged();
                notifyObservers(new HoldbackQueueMessages(GCom.getHoldBackQueue()));
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopDebugging(){
        debugging = false;
    }

}
