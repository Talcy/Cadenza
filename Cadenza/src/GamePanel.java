/*
 * Joseph Zhang
 * 5/10/2019
 * 
 * GamePanel.java
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

interface Feedback {
	// Feedback from display
	void FeedbackStartDisplay();
	void FeedbackStopDisplay();
	
	// Feedback from music
	void FeedbackStartMusic();
	void FeedbackStopMusic();
	void FeedbackForceStop();
	void FeedbackPositionMusic(long pos);
	
	// Feedback from data
    int FeedbackTimeSegmentSize();
    int FeedbackFreqSegmentSize();
    List<Float> FeedbackFreqData(int segment);
	
	// Feedback from score card
    void FeedbackScore(int type, int score);
    int FeedbackScoreNumber(int type);
    
    // Feedback from setting
    void feedbackChangeGameState(boolean bMode);
}

public class GamePanel extends JPanel implements Feedback {
	public SwitchRequest request;
	
    private int winWidth;
    private int winHeight;

	// Resource files
    private String MUSIC_FILE = "Dragon_Force.wav";
    private String FREQ_FILE = "Dragon_Force.csv";

    private String BG_FILE = "Music-Background-640x640-0.jpg";
	private Timer timerBG = null;
	private int seqBG_FILE = 0;
	
    // Data
    private ReadMusicFreqData freqData = null;
    private boolean bShuffle = false;
    
    // Music
    private MusicPlayer mPlayer = null;
    
    // Music display
    private MusicVisualizerDisplay mvDisplay = null;
    
    // Score card display
    private ScoreCardDisplay scDisplay = null;
    
    // Message display
    private JLabel msgDisplay = new JLabel("Message:");

    // Flag for game interrupted
    private boolean bForceStop = true;
    
	GamePanel(int width, int height) {
		winWidth = width;
		winHeight = height;

		int winTile = winWidth / (10 + 2);
	    
		if (winHeight / (10 + 1) != winTile) {
			System.out.println("Display size does not match design!");
        	System.exit(0);
		}
		
        // Read music data
        freqData = new ReadMusicFreqData(FREQ_FILE);

		// Setup music display
        mvDisplay = new MusicVisualizerDisplay(winTile * 10, winHeight - winTile);
        mvDisplay.RegisterFeedback(this);
        mvDisplay.SetBackgroundImage(BG_FILE);

        // Setup scorecard display
        scDisplay = new ScoreCardDisplay(winTile * 2, winHeight - winTile);
        scDisplay.RegisterFeedback(this);

        // Play music
        mPlayer = new MusicPlayer(MUSIC_FILE);
        mPlayer.RegisterFeedback(this);
        
		setLayout(new BorderLayout());
		
		this.add(new JButton("WEST"), BorderLayout.WEST);
	    this.add(mvDisplay, BorderLayout.WEST);
	    this.add(scDisplay, BorderLayout.CENTER);
	    this.add(msgDisplay, BorderLayout.SOUTH);
		
		timerBG = new Timer(freqData.GetTimeSegmentSize()*100, new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	seqBG_FILE = (seqBG_FILE + 1) % 10;
		    	BG_FILE = "Music-Background-640x640-" + seqBG_FILE + ".jpg";
		        mvDisplay.SetBackgroundImage(BG_FILE);
		    }
		});
		
		addMouseListener(new MouseAdapter() { 
			public void mousePressed(MouseEvent me) { 
				//request.GameSwitchRequest();
			} 
		}); 
	}
	
	public void Start() {
		bForceStop = false;
        mvDisplay.Start();
	}
	
	public void Stop() {
		mPlayer.Stop();
	}
	
    void RegisterCallback(SwitchRequest call) {
  	  request = call;
    }
    
    void SetMusicFiles(String wav, String csv) {
    	mPlayer = null;
        mPlayer = new MusicPlayer(wav);
        mPlayer.RegisterFeedback(this);

        freqData = null;
        freqData = new ReadMusicFreqData(csv);
    }
    
    void SetMusicBackground(String img) {
    	mvDisplay.SetBackgroundImage(img);
    }

	public void FeedbackStartDisplay() {
		Thread.State status = mPlayer.getState();
		System.out.println("Music player thread status is " + status);
		
		if (status == Thread.State.TERMINATED) {
			mPlayer = null;
			mPlayer = new MusicPlayer(MUSIC_FILE);
	        mPlayer.RegisterFeedback(this);
		}
		
		timerBG.start();
		mPlayer.start();
        
		System.out.println("Display started");
	}
	
	public void FeedbackStopDisplay() {
		timerBG.stop();
		System.out.println("Display stopped");
	}
	
	public void FeedbackStartMusic() {
		System.out.println("Music started");
	}
	
	public void FeedbackStopMusic() {
		mvDisplay.Stop();
		
		if (bForceStop) {
			System.out.println("Music forced stop");
		}
		else {
			request.GameSwitchRequest(bForceStop);
			
			System.out.println("Music stopped");
		}
	}
	
	public void FeedbackForceStop() {
		bForceStop = true;
		request.GameSwitchRequest(bForceStop);
	}
	
	public void FeedbackPositionMusic(long pos) {
		mvDisplay.SyncTimePosition(pos);
		
		msgDisplay.setText("Music playback: " + Long.toString(pos));
	}

    public int FeedbackTimeSegmentSize() { 
		System.out.println("Number of time segment is " + freqData.GetTimeSegmentSize());
    	return freqData.GetTimeSegmentSize();
    }
    
    public int FeedbackFreqSegmentSize() { 
		System.out.println("Number of freq segment is " + freqData.GetFreqSegmentSize());
    	return freqData.GetFreqSegmentSize();
    }
    
    public List<Float> FeedbackFreqData(int segment) { 
    	if (bShuffle) {
    		List<Float> list = freqData.GetFreqData(segment);
    		Collections.shuffle(list);
    		return list;
    	}
    	
    	return freqData.GetFreqData(segment);
    }
    
    public void FeedbackScore(int type, int score) {
    	scDisplay.UpdateScore(type, score);
    }
    
    public int FeedbackScoreNumber(int type) {
    	return scDisplay.GetScoreNumber(type);
    }
    
    public void feedbackChangeGameState(boolean bMode)
    {
    	bShuffle = bMode;
    }
    
    public boolean feedbackReturnGameState()
    {
    	return bShuffle; 
    }
}
