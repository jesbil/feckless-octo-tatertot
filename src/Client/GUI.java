package Client;

/**
 * Created by c12jbr on 2015-10-08.
 */
import Middleware.GCom;

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


import javax.swing.*;

/**
 * The graphical user interface of the program
 *
 * @author c12jbr
 *
 */
public class GUI{
    private JFrame frame;
    private JTextArea jtaNameList;

    private OpenDebuggerListener debugger;

    public JTextArea getChatField() {
        return chatField;
    }

    private JTextArea chatField;

    private JTextArea writeField;
    private JScrollPane nlsp;
    /**
     * Creates the GUI and asks the user of its name.
     */
    public GUI() {
    }


    public OpenDebuggerListener getDebugger() {
        return debugger;
    }

    public String nameServerRequest() {
        String nameService = null;
        while(nameService==null){
            nameService = JOptionPane.showInputDialog(null,"What name service server do you want to connect to?", "shadowcat.cs.umu.se");
            if(nameService==null){
                int answer = JOptionPane.showConfirmDialog (null, "Do you want to exit?", "Warning",JOptionPane.YES_NO_OPTION);
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
        frame.add(createGroupNameList(),BorderLayout.WEST);
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
     * Creates a button and gives it the actionlistener to change encryption key.
     *
     * @return
     */
    private JMenuItem createGroupButton(){
        JMenuItem item = new JMenuItem("Create Group");
        item.addActionListener(new CreateGroupListener());

        return item;
    }

    private JMenuItem joinGroupButton(){
        JMenuItem item = new JMenuItem("Join Group");
        item.addActionListener(new JoinGroupListener());

        return item;
    }

    private JMenuItem leaveGroupButton(){
        JMenuItem item = new JMenuItem("Leave Group");
        item.addActionListener(new LeaveGroupListener());

        return item;
    }

    private JMenuItem DebugWindowButton(){
        JMenuItem item = new JMenuItem("Open Debugger");
        debugger = new OpenDebuggerListener(frame);
        item.addActionListener(debugger);

        return item;
    }

    public JTextArea getJtaNameList() {
        return jtaNameList;
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
     * written and the buttons to encrypt, compress or send the message
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
     * Creates the JTextArea that shows the chat
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
     * Used to disconnect the client from a chat server when the client is closed
     *
     * @return
     */
    private WindowListener createCloseOperation() {
        WindowListener exitListener = new WindowAdapter() {

            // disconnect possible connection before shutting down
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if(GCom.getCurrentGroup()!=null){
                        GCom.leaveGroup(GCom.getCurrentGroup());
                    }
                    GCom.leaveGroup(GCom.getAllMembersGroupName());
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                } catch (NotBoundException e1) {
                    e1.printStackTrace();
                } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                //TODO Gcom.getAllMembers

            }
        };
        return exitListener;
    }

    /**
     * Used since the GUI observes the class Conn.
     * Gets a byte[] and parse it to make changes in the gui
     * for example update the user list or show a received message in the chat
     */
    public void update() {

        frame.repaint();


    }

    public boolean askUnordered() {
        int answer = JOptionPane.showConfirmDialog (null, "Yes for unordered. No for casual ordering", "Warning",JOptionPane.YES_NO_OPTION);
        if(answer == JOptionPane.YES_OPTION){
            return true;
        }
        return false;
    }
}