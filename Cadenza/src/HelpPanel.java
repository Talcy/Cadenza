/*
 * Joseph Zhang
 * 5/17/2019
 * HelpPanel.java
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

public class HelpPanel extends JPanel implements MouseListener, MouseMotionListener
{
	public SwitchRequest request;
	
	private int dispWidth, dispHeight;
	private BufferedImage dispBuffer;
    
	private Timer bgTimer = null;
	private Timer smTimer = null;
	private final int bgTimerTick = 50;	// ms
	private final int smTimerTick = 2100;
	private int bgLoop = 0;
	private int movLoop = 0;
	private int random = 0;
    
    private Color bgColor [] = new Color[256];
    private List<Color> bgColor2 = new ArrayList<Color>();
    private static final int numBgBar = 10;
    
    public HelpPanel(int width, int height)
    {
		// Menu display size
		dispWidth = width;
		dispHeight = height;

		setBackground(Color.BLACK); 
        setPreferredSize(new Dimension(dispWidth, dispHeight));
        //setMaximumSize(new Dimension(dispWidth, dispHeight));
        //setMinimumSize(new Dimension(dispWidth, dispHeight));
        //setDoubleBuffered(true);
		
		// Display buffer 
		dispBuffer = new BufferedImage(dispWidth, dispHeight, BufferedImage.TYPE_INT_ARGB);
		
	    float colour_f = (float)0.15;
        for (int i = 0; i < 256; i ++) {
        	int green = (int)(Math.sin(colour_f * i + 0) * 127 + 128);
        	int blue = (int)(Math.sin(colour_f * i + 2) * 127 + 128);
        	int red = (int)(Math.sin(colour_f * i + 4) * 127 + 128);
        	
        	bgColor[i] = new Color(red, green, blue);
        	bgColor2.add(new Color(red, green, blue));
        }
        
      
        
        bgTimer = new Timer(bgTimerTick, new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	bgLoop += 1;
		        FlipDisplay(dispBuffer.getGraphics());
		    }
		});
        
        smTimer = new Timer(smTimerTick, new ActionListener() {
        	 public void actionPerformed(ActionEvent e) {
        		 random = (int)(Math.random() * 256);
 		    }
        });
	    
        // Monitor mouse actions
		addMouseListener(this);
		addMouseMotionListener(this);
		
		// Monitor keyboard
        addKeyListener(new KeyHandler());
    }
    
  	//Paints the background rectangles and start button  
    public void FlipDisplay(Graphics g) {
    	
    	int rectWidth = dispWidth / numBgBar;
  		int rectHeight = dispWidth / numBgBar;
  		
  		for (int i = 0; i < numBgBar; i ++) {
  			for (int j = 0; j < numBgBar; j ++) {
	  			g.setColor(bgColor2.get((bgLoop + i + j) % 256));
	  			g.fillRect(rectWidth * i, rectHeight * j, rectWidth, rectHeight);
  			}
  		}
    	
  		/*
    	g.setColor(bgColor[random]);
    	g.drawOval(100, (movLoop += 5) % 400, 60, 60);
    	
    	g.setColor(Color.RED);
    	g.drawRect(100, 400, 60, 60);
    	
    	g.setColor(bgColor[random]);
    	g.drawRect(300, (movLoop += 5) % 400, 60, 60);
    	
    	g.setColor(Color.BLUE);
    	g.drawRect(300, 400, 60, 60);
    	*/
  		
  		
  		g.setColor(Color.BLACK);
  		g.setFont(new Font("Roman", Font.BOLD, 30));
    	g.drawString("General Instructions and Credits:", 50, 70);
    	g.setFont(new Font("Roman", Font.BOLD, 20));
    	g.drawString("This is Cadenza, a music game based on Guitar Hero and Osu!", 50, 130);
    	
    	g.drawString("- Move your mouse near the buttons to make it pop up for you", 50, 160);
    	g.drawString("for you to click it", 50, 190);
    	
    	g.drawString("- Press the escape key to go back to the previous screen", 50, 220);
    	g.drawString("Use it to exit game and music selection screens", 50, 250);
    	g.drawString("Once you finish a song, also press the esc key to exit the", 50, 280);
    	g.drawString("game over screen", 50, 310);
    	
    	g.drawString("- How to select music in the \"Load\" screen", 50, 340);
    	g.drawString("1. Select the music you want to play, the song screen is displayed", 50, 370);   	
    	g.drawString("with song name, length and diffculty", 50, 400);
    	
    	g.drawString("2. Exit the screen, now you have choosen the music!", 50, 430);
    	g.drawString("3. Go back to the menu screen to press the Play button", 50, 460);
    	g.drawString("to play the song!", 50, 490);
    	
    	g.drawString("- Gameplay Instructions", 50, 520);
    	g.drawString("When the notes fall into the receivers (rectangles), press", 50, 550);
    	g.drawString("numbers 1 to 0 on the keyboard to receive the notes and get points", 50, 580);
    	g.drawString("Press \"[\" and \"]\" to adjust background transparency", 50, 610);
    	g.setFont(new Font("Roman", Font.BOLD, 30));
    	g.drawString("Cadenza version 1.0 by Joseph Zhang", 50, 650);
    	
    	
    	
	    // Repaint display
  		repaint();
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        grabFocus();
        g.drawImage(dispBuffer, 0, 0, this);
    }
    
    void RegisterCallback(SwitchRequest call) {
    	  request = call;
    }

	public void Start() {
		bgTimer.start();
		smTimer.start();
	}

	public void Stop() {
		bgTimer.stop();
		smTimer.stop();
	}

  	@Override
	public void mouseClicked(MouseEvent e) 
	{
		
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
        			request.HelpSwitchRequest();
        	}
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }
	}
}
