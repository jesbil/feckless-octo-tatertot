package Client;

/**
 * Created by c12jbr on 2015-10-08.
 */
import Middleware.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


import javax.swing.*;

import static Interface.Constants.*;
import static Interface.Constants.TYPE_MESSAGE;

/**
 * The graphical user interface of the program
 *
 * @author c12jbr
 *
 */
public class GUI implements Observer{
    private JFrame frame;
    private JTextArea jtaNameList;

    private OpenDebuggerListener debugger;

    private JTextArea chatField;

    private JTextArea writeField;

    private JScrollPane nlsp;


    public GUI() {
    }

    /**
     * Asks for and stores the name service server adress
     * @return String or null on exit
     */
    public String nameServerRequest() {
        String nameService = null;
        while(nameService==null){
            nameService = JOptionPane.showInputDialog(null,"What name service server do you want to connect to?", "bellatrix");
            if(nameService==null){
                int answer = JOptionPane.showConfirmDialog(null, "Do you want to exit?", "Warning",JOptionPane.YES_NO_OPTION);
                if(answer == JOptionPane.YES_OPTION){
                    return null;
                }
            }
        }

        return nameService;
    }


    /**
     * Builds the components of the gui and makes the gui visible
     *
     * @param title
     */
    public void buildAndStart(String title){
        frame = new JFrame(title);
        frame.addWindowListener(createCloseOperation());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(700,500));
        frame.setMinimumSize(new Dimension(500,200));
        frame.setLocationRelativeTo(null);
        frame.setJMenuBar(createMenuBar());
    //    frame.add(createGroupNameList(),BorderLayout.WEST);
        frame.add(createChatArea(),BorderLayout.CENTER);
        frame.setEnabled(true);
        frame.setVisible(true);
    }

    /**
     * Creates the menu bar and adds items to it
     *
     * @return
     */
    private JMenuBar createMenuBar(){
        JMenuBar mb = new JMenuBar();

        JMenu fm = new JMenu("Options");
        fm.add(createGroupButton());
        fm.add(joinGroupButton());
        fm.add(leaveGroupButton());
        fm.add(DebugWindowButton());
        mb.add(fm);

        return mb;
    }

    /**
     * Creates a menu item and gives it the actionlistener to create a group.
     *
     * @return
     */
    private JMenuItem createGroupButton(){
        JMenuItem item = new JMenuItem("Create Group");
        item.addActionListener(new CreateGroupListener());

        return item;
    }

    /**
     * Creates a menu item and gives it the actionlistener to join a group.
     *
     * @return
     */
    private JMenuItem joinGroupButton(){
        JMenuItem item = new JMenuItem("Join Group");
        item.addActionListener(new JoinGroupListener());

        return item;
    }

    /**
     * Creates a menu item and gives it the actionlistener to leave a group.
     *
     * @return
     */
    private JMenuItem leaveGroupButton(){
        JMenuItem item = new JMenuItem("Leave Group");
        item.addActionListener(new LeaveGroupListener());

        return item;
    }

    /**
     * Creates a menu item and gives it the actionlistener to open the debugger.
     *
     * @return
     */
    private JMenuItem DebugWindowButton(){
        JMenuItem item = new JMenuItem("Open Debugger");
        debugger = new OpenDebuggerListener(frame,this);
        item.addActionListener(debugger);

        return item;
    }

    /**
     * Creates the JTextArea where the user names of users connected to the chat server is shown
     *
     * @return
     */
    private JScrollPane createGroupNameList(){
        jtaNameList = new JTextArea();
        jtaNameList.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        jtaNameList.setEditable(false);
        jtaNameList.setDisabledTextColor(Color.black);
        nlsp = new JScrollPane(jtaNameList);
        nlsp.setPreferredSize(new Dimension(200,440));
        nlsp.createHorizontalScrollBar();
        jtaNameList.setEditable(false);

        return nlsp;
    }

    /**
     * Creates the Panel that contains the JTextArea of the chat, the JTextArea where messages are
     * written and the button to send the message
     *
     * @return
     */
    private JPanel createChatArea(){
        JPanel ca = new JPanel();
        ca.setLayout(new BorderLayout());
        ca.setBackground(Color.cyan);
        ca.add(createChatField(),BorderLayout.CENTER);
        ca.add(createWriteField(),BorderLayout.SOUTH);
        return ca;
    }

    /**
     * Creates the JScrollPane that shows the chat
     *
     * @return
     */
    private JScrollPane createChatField(){
        chatField = new JTextArea();
        chatField.setEditable(false);
        chatField.setDisabledTextColor(Color.BLACK);

        JScrollPane cfsp = new JScrollPane(chatField);
        cfsp.createHorizontalScrollBar();

        return cfsp;
    }

    /**
     * Creates The JTextArea where the user writes its messages
     *
     * @return
     */
    private JPanel createWriteField(){
        JPanel wfp = new JPanel();
        wfp.setLayout(new BorderLayout());

        writeField = new JTextArea();
        writeField.append("write message here");


        JScrollPane wfsp = new JScrollPane(writeField);
        wfsp.setPreferredSize(new Dimension(300,60));

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new sendMessageListener(writeField));


        wfp.add(wfsp,BorderLayout.CENTER);
        wfp.add(sendButton,BorderLayout.EAST);

        return wfp;
    }

    /**
     * Used to disconnect the client from GCom when the client is closed
     *
     * @return
     */
    private WindowListener createCloseOperation() {
        WindowListener exitListener = new WindowAdapter() {

            // disconnect possible connection before shutting down
            @Override
            public void windowClosing(WindowEvent e) {

                try {
                    GCom.shutdown();
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                } catch (NotBoundException e1) {
                    e1.printStackTrace();
                } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        };
        return exitListener;
    }

    /**
     * used at startup to know what ordering type to use
     * @return
     */
    public boolean askUnordered() {
        int answer = JOptionPane.showConfirmDialog (null, "Yes for unordered. No for causal ordering", "Warning",JOptionPane.YES_NO_OPTION);
        if(answer == JOptionPane.YES_OPTION){
            return true;
        }
        return false;
    }


    /**
     * the GCom is observed and gives the information to update the gui here.
     *
     * Message - group chat message.
     * HoldbackQueueMessages - shows up in the debugger
     * DebuggMessage - shows in the debugger
     *
     * @param observable
     * @param o Message, HolbackQueueMessages or DebuggMessage
     */
    @Override
    public void update(Observable observable, Object o) {

        if(o instanceof Message){
            Message message = (Message) o;

            switch (message.getType()) {
                case TYPE_CREATE_GROUP:
                   // jtaNameList.append(message.getMessage()+"\n");
                    break;
                case TYPE_REMOVE_GROUP:
                  //  jtaNameList.setText(jtaNameList.getText().replace(message.getMessage() + "\n", ""));
                   // jtaNameList.getText().replace("* "+message.getMessage()+"\n","");
                    break;
                case TYPE_JOIN_GROUP:
                  //  jtaNameList.append(message.getMessage()+"\n");
                    break;
                case TYPE_MESSAGE:
                    chatField.append(message.getSender().getName()+"@"+message.getGroup().getName()+": "+message.getMessage()+"\n");
                    chatField.setCaretPosition(chatField.getDocument().getLength());
                    break;
            }
        }

        if(o instanceof HoldbackQueueMessages){
            debugger.getWaitingQueue().setText("");
            for (int i = 0; i < ((HoldbackQueueMessages) o).getSize(); i++) {
                debugger.getWaitingQueue().append(((HoldbackQueueMessages) o).getMessage(i)+"\n");
            }
        }

        if(o instanceof DebuggMessage){
            debugger.getLog().append(((DebuggMessage) o).getMessage() + "\n");
        }

        debugger.update();
        frame.repaint();

    }

}