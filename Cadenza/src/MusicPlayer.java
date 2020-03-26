/*
 * Joseph Zhang
 * 5/10/2019
 * 
 * MusicPlayer.java
 */

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

//The class where is controls the music played
public class MusicPlayer extends Thread implements LineListener {
	private Clip audioClip;
	private boolean playCompleted;

	public Feedback mainCallback = null;

	MusicPlayer(String audioFilePath) {
		File audioFile = new File(audioFilePath);

		try {
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
			AudioFormat format = audioStream.getFormat();

			DataLine.Info info = new DataLine.Info(Clip.class, format);

			audioClip = (Clip) AudioSystem.getLine(info);

			audioClip.addLineListener(this);

			audioClip.open(audioStream);
        } catch (UnsupportedAudioFileException ex) {
            System.out.println("The specified audio file is not supported.");
            ex.printStackTrace();
        } catch (LineUnavailableException ex) {
            System.out.println("Audio line for playing back is unavailable.");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Error playing the audio file.");
            ex.printStackTrace();
        }
	}
	
	//Feedback to the other classes
	public void RegisterFeedback(Feedback callback) {
		mainCallback = callback;
	}

	//starts the music
	public void run() {
		audioClip.start();

		while (!playCompleted) {
			// Wait for the playback completes
			try 
			{
				long pos = audioClip.getMicrosecondPosition() / 1000;
				if (mainCallback != null)
					mainCallback.FeedbackPositionMusic(pos);
				
				Thread.sleep(500);
			} 
			catch (InterruptedException ex) 
			{
				ex.printStackTrace();
            }
        }
         
     	audioClip.close();
	}
	
	//Stops the music
	public void Stop() {
		if (audioClip.isRunning()) {
			if (audioClip.isActive()) {
				audioClip.stop();
			}
		}
    }
     
    /**
     * Listens to the START and STOP events of the audio line.
     */
    @Override
    public void update(LineEvent event) {
        LineEvent.Type type = event.getType();
         
        if (type == LineEvent.Type.START) {
        	if (mainCallback != null)
        		mainCallback.FeedbackStartMusic();
        } 
        else if (type == LineEvent.Type.STOP) {
            playCompleted = true;
            
        	if (mainCallback != null)
        		mainCallback.FeedbackStopMusic();
        }
    }
}
