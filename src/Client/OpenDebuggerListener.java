package Client;

import sun.org.mozilla.javascript.tools.shell.JSConsole;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by c12jbr on 2015-10-14.
 */
public class OpenDebuggerListener implements ActionListener {
    private JFrame frame;
    private JScrollPane logPane;
    private JScrollPane waitingQueuePane;
    private JFrame mainFrame;
    private JTextArea log;
    private JTextArea waitingQueue;
    private JPanel border;

    public JTextArea getLog() {
        return log;
    }

    public JTextArea getWaitingQueue() {
        return waitingQueue;
    }

    public OpenDebuggerListener(JFrame mainFrame){
        this.mainFrame = mainFrame;

        log = new JTextArea();
        log.setEditable(false);
        logPane = new JScrollPane(log);
        logPane.setPreferredSize(new Dimension(700,400));


        waitingQueue = new JTextArea();
        waitingQueue.setEditable(false);
        waitingQueuePane = new JScrollPane(waitingQueue);
        waitingQueuePane.setPreferredSize(new Dimension(295,400));

        border = new JPanel();
        border.setBackground(Color.black);

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        frame = new JFrame("DEBUGGER");
        frame.setSize(new Dimension(1000, 500));
        frame.setLocationRelativeTo(mainFrame);
        frame.add(SendMessagesInInvalidOrder(), BorderLayout.NORTH);
        frame.add(logPane,BorderLayout.WEST);
        frame.add(waitingQueuePane,BorderLayout.EAST);
        frame.add(border,BorderLayout.CENTER);
        frame.setEnabled(true);
        frame.setVisible(true);
    }

    private JButton SendMessagesInInvalidOrder() {
        JButton button = new JButton("Send Messages in invalid Order");
        button.addActionListener(new InvalidOrderMessageslistener());
        return button;
    }



}
