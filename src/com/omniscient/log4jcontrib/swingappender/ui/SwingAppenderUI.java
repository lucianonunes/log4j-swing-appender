/**
 * 
 */
package com.omniscient.log4jcontrib.swingappender.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

/** Creates a UI to display log messages from a SwingAppender
 * @author pshah
 *
 */
public class SwingAppenderUI {
	//UI attributes
	private JFrame jframe;
	private JButton startPause;
	private JButton stop;
    /**
     * Button to clear off the textArea
     */
    private JButton clear;
	private JPanel buttonsPanel;
	private JTextArea logMessagesDisp;
	private JScrollPane scrollPane;
	//buffer to hold log statements when the UI is set to PAUSE
	private List logBuffer; 
	/**flag to indicate if we should display new log events */
	private int appState;

	/* Constants */
	public static final String START = "start";
	public static final String PAUSE = "pause";
	public static final String STOP = "stop";
    public static final String CLEAR = "clear";
	public static final int STARTED = 0;
	public static final int PAUSED = 1;
	public static final int STOPPED = 2;

    /**
     * a instance for SwingAppenderUI class
     */
    private static SwingAppenderUI instance;

    /**
     * method to get an instance of the this class
     * @return
     */
    public static SwingAppenderUI getInstance() {
        if (instance == null) {
            instance = new SwingAppenderUI();
        }
        return instance;
    }
    
	/**Initializes the object
	 */
	private SwingAppenderUI() {
		//set internal attributes
		logBuffer = new ArrayList();
		appState = STARTED;
		
		//create main window
		jframe = new JFrame();
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		initButtonsPanel();
		
		//create text area to hold the log messages
		initMessageDispArea();
		
		//add components to the contentPane
		jframe.getContentPane().add(BorderLayout.NORTH, buttonsPanel);
		jframe.getContentPane().add(BorderLayout.CENTER, scrollPane);
		jframe.setSize(300,600);
		jframe.setVisible(true);
	}
	
	/**Displays the log in the text area unless dispMsg is set to false in which
	 * case it adds the log to a buffer. When dispMsg becomes true, the buffer
	 * is first flushed and it's contents are displayed in the text area.
	 * @param log The log message to be displayed in the text area
	 */
	public void doLog(String log) {
		if(appState == STARTED) {
			if(!logBuffer.isEmpty()) {
				System.out.println("flushing buffer");
				Iterator iter = logBuffer.iterator();
				while(iter.hasNext()) {
					logMessagesDisp.append((String)iter.next()+"\n");
					iter.remove();
				}
			}
			logMessagesDisp.append(log+"\n");
            //Code to set the scrollbar always at the bottom 
            logMessagesDisp.setCaretPosition(logMessagesDisp.getText().length());
		}
		else if(appState == PAUSED){
			System.out.println("Sending log msg to buffer");
			logBuffer.add(log);
		}
		else {
			System.out.println("Not accepting any log statements");
		}
	}
	
	/**creates a panel to hold the buttons
	 */
	private void initButtonsPanel() {
		// TODO: Add clear button to clear the log statements.
		buttonsPanel = new JPanel();
		startPause = new JButton(PAUSE);
		startPause.addActionListener(new StartPauseActionListener());
		stop = new JButton(STOP);
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				appState = STOPPED;
				startPause.setText(START);
			}
		});
        clear = new JButton(CLEAR);
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                logMessagesDisp.setText("");
            }
        });
		buttonsPanel.add(startPause);
		buttonsPanel.add(stop);
        buttonsPanel.add(clear);
	}

	/**Creates a scrollable text area
	 */
	private void initMessageDispArea() {
		logMessagesDisp = new JTextArea();
		scrollPane = new JScrollPane(logMessagesDisp);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	}
	
	/**************** inner classes *************************/
	
	/**Accepts and responds to action events generated by the startPause
	 * button.
	 */
	class StartPauseActionListener implements ActionListener {
		/**Toggles the value of the startPause button. Also toggles
		 * the value of dispMsg.
		 * @param evt The action event
		 */
		public void actionPerformed(ActionEvent evt) {
			JButton srcButton = (JButton)evt.getSource();
			if(srcButton.getText().equals(START)) {
				srcButton.setText(PAUSE);
				appState = STARTED;
			}
			else if(srcButton.getText().equals(PAUSE)) {
				System.out.println("setting dispMsg to false");
				appState = PAUSED;
				srcButton.setText(START);
			}
		}
	}
	
	public void close() {
		// clean up code for UI goes here. 
		jframe.setVisible(false);
	}
}