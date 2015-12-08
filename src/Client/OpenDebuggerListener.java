package Client;

import Middleware.Debugg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by c12jbr on 2015-10-14.
 *
 *  * Actionlistener for open debugger button
 */
public class OpenDebuggerListener implements ActionListener {
    private JFrame frame;
    private JScrollPane logPane;
    private JScrollPane waitingQueuePane;
    private JFrame mainFrame;
    private JTextArea log;
    private JTextArea waitingQueue;
    private JPanel border;
    private Debugg debugg;

    /**
     * Constructor creates all parts of the debug window and binds the
     * gui to observe the debugg class of the middleware
     *
     * @param mainFrame - used for positioning
     * @param gui - observer for Debugg class
     */
    public OpenDebuggerListener(JFrame mainFrame, GUI gui) {
        this.mainFrame = mainFrame;
        debugg = new Debugg();
        debugg.addObserver(gui);

        log = new JTextArea();
        log.setEditable(false);
        logPane = new JScrollPane(log);
        logPane.setPreferredSize(new Dimension(600,400));


        waitingQueue = new JTextArea();
        waitingQueue.setEditable(false);
        waitingQueuePane = new JScrollPane(waitingQueue);
        waitingQueuePane.setPreferredSize(new Dimension(700,400));

        border = new JPanel();
        border.setBackground(Color.black);
    }

    /**
     *
     * @return log
     */
    public JTextArea getLog() {
        return log;
    }

    /**
     *
     * @return waitingQueue
     */
    public JTextArea getWaitingQueue() {
        return waitingQueue;
    }


    /**
     * makes the debug window visible and adds the other parts to the
     * frame
     * @param actionEvent
     */
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        frame = new JFrame("DEBUGGER");
        frame.setSize(new Dimension(1305, 500));
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
        createCloseOperation();

        new Thread(debugg).start();

    }

    /**
     * Creates button to pause or start the holdbackqueue making messages appear
     * to be unreceived
     * @return
     */
    private JButton pauseStartHoldbackqueue() {
        JButton button = new JButton("hold incoming message");
        button.addActionListener(new PauseStartHoldbackqueue());
        return button;
    }

    /**
     * Creats button to shuffle the holdbackqueue making messages appear to
     * have been received in a different order.
     * @return
     */
    private JButton shuffleHoldBackqueue() {
        JButton button = new JButton("Shuffle Holdbackqueue");
        button.addActionListener(new shuffleHoldbackqueueListener());
        return button;
    }


    /**
     * Stop the debugging when the debug window closes
     * @return
     */
    private WindowListener createCloseOperation() {
        WindowListener exitListener = new WindowAdapter() {

            // disconnect possible connection before shutting down
            @Override
            public void windowClosing(WindowEvent e) {
                debugg.stopDebugging();

            }

        };
        return exitListener;
    }

    /**
     * repaints the frame
     */
    public void update() {
        if(frame!=null){
            frame.repaint();
        }
    }
}
