/*
 * Joseph Zhang
 * 
 * 5/20/19
 * SelectPanel.java
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class FinishPanel extends JPanel implements MouseListener, MouseMotionListener {
	public SwitchRequest request = null;
	
	private int dispWidth, dispHeight;
	private BufferedImage dispBuffer;
	private Image imageBG = null;
	private Timer bgTimer = null;
	private final int bgTimerTick = 1000;	// ms
	private int bgLoop = 0;

	public int scores[] = new int[2];

	public FinishPanel(int width, int height)
	{
		// Menu display size
		dispWidth = width;
		dispHeight = height;
		
		setBackground(Color.BLACK); 
        setPreferredSize(new Dimension(dispWidth, dispHeight));
        //setMaximumSize(new Dimension(dispWidth, dispHeight));
        //setMinimumSize(new Dimension(dispWidth, dispHeight));

		// Display buffer 
		dispBuffer = new BufferedImage(dispWidth, dispHeight, BufferedImage.TYPE_INT_ARGB);
		
		// Set timer for background change
        bgTimer = new Timer(bgTimerTick, new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        FlipDisplay(dispBuffer.getGraphics());
		    }
		});

        // Monitor mouse actions
		addMouseListener(this);
		addMouseMotionListener(this);
		
		// Monitor keyboard
        addKeyListener(new KeyHandler());
        
    	SetBackgroundImage("Music-Finish-768x704-0.jpg");
	}		

  	//Paints the background rectangles and start button  
    public void FlipDisplay(Graphics g) {
    	int score_total = scores[0]+scores[1];
    	
        if (imageBG != null)
        	g.drawImage(imageBG, 0, 0, null);
        
        // Show score number
    	g.setColor(Color.RED);
	    g.setFont(new Font("Roman", Font.BOLD, 48));
	    g.drawString("Your score is "+score_total, 200, (bgLoop += 50) % 700);
        
	    // Repaint display
  		repaint();
    }

	public void SetBackgroundImage(String file) {
        // Display background
        try {
            File imgFile = new File(file);
            imageBG = ImageIO.read(imgFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
	}
	
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        grabFocus();
        g.drawImage(dispBuffer, 0, 0, this);
    }
    
    public void Start() {
		if (request != null) {
			scores[0] = request.GetScoreNumber(0);
			scores[1] = request.GetScoreNumber(1);
		}
		bgTimer.start();
    }
    
    public void Stop() {
		bgTimer.stop();
    }

  	void RegisterCallback(SwitchRequest call) {
	  	request = call;
  	}

  	@Override
	public void mouseClicked(MouseEvent e) 
	{
		if (request != null)
			request.FinishSwitchRequest();
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

  	@Override
  	public void mouseMoved(MouseEvent e) 
  	{
  	}
	  	
	private class KeyHandler extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
        	int key = e.getKeyCode();
        	//System.out.println("Key pressed - " + key);

        	if (key == KeyEvent.VK_ESCAPE) {
        		if (request != null)
        			request.FinishSwitchRequest();
        	}
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }
	}
}