package Client;

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
        JPanel panel = new JPanel();
        panel.add(pauseStartHoldbackqueue(), BorderLayout.EAST);
        panel.add(shuffleHoldBackqueue(),BorderLayout.WEST);
        frame.add(panel, BorderLayout.NORTH);
        frame.add(logPane,BorderLayout.WEST);
        frame.add(waitingQueuePane,BorderLayout.EAST);
        frame.add(border,BorderLayout.CENTER);
        frame.setEnabled(true);
        frame.setVisible(true);
    }

    private JButton pauseStartHoldbackqueue() {
        JButton button = new JButton("Pause/Start Holdbackqueue");
        button.addActionListener(new PauseStartHoldbackqueue());
        return button;
    }

    private JButton shuffleHoldBackqueue() {
        JButton button = new JButton("Shuffle Holdbackqueue");
        button.addActionListener(new shuffleHoldbackqueueListener());
        return button;
    }



}
