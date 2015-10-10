package Client;

/**
 * Created by c12jbr on 2015-10-08.
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

/**
 * The graphical user interface of the program
 *
 * @author c12jbr
 *
 */
public class GUI implements Observer{
    private JFrame frame;
    private JTextArea jtaNameList;
    private JTextArea chatField;
    private JTextArea writeField;
    private JScrollPane nlsp;


    /**
     * Creates the GUI and asks the user of its name.
     */
    public GUI() {
    }

    public String nameServerRequest() {
        String nameService = null;
        while (nameService==null){
            nameService = JOptionPane.showInputDialog(null,"What name service server do you want to connect to?", "draco.cs.umu.se");
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
        frame.add(createNameList(),BorderLayout.WEST);
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



    /**
     * Creates the JTextArea where the user names of users connected to the chat server is shown
     *
     * @return
     */
    private JScrollPane createNameList(){
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
                //LEAVE GROUP
                //LEAVE MASTER-GROUP

            }
        };
        return exitListener;
    }

    /**
     * Used since the GUI observes the class Conn.
     * Gets a byte[] and parse it to make changes in the gui
     * for example update the user list or show a received message in the chat
     */
    @Override
    public void update(Observable arg0, Object obj) {

        frame.repaint();


    }

}