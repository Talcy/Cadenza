/*
 * Joseph Zhang
 * 5/17/2019
 * 
 * SettingPanel.java
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

public class SettingPanel extends JPanel implements MouseListener, MouseMotionListener
{
	public SwitchRequest request;
	
	private int dispWidth, dispHeight;
	private BufferedImage dispBuffer;
    
	private Timer bgTimer = null;
	private final int bgTimerTick = 50;	// ms
	private int bgLoop = 0;
	private Rectangle Option1 = new Rectangle(50, 140, 80, 80);
	private Rectangle Option2 = new Rectangle(50, 340, 80, 80);
	
	private List<Color> bgColor = new ArrayList<Color>();
	private static final int numBgBar = 10;
	
	private boolean getCurrentMode = true;
	
    
    public SettingPanel(int width, int height)
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
        	
        	bgColor.add(new Color(red, green, blue));
        }
        
        bgTimer = new Timer(bgTimerTick, new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	bgLoop += 1;
		        FlipDisplay(dispBuffer.getGraphics());
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
    	//Color Background
    	int rectWidth = dispWidth / numBgBar;
  		int rectHeight = dispWidth / numBgBar;
  		
  		for (int i = 0; i < numBgBar; i ++) {
  			for (int j = 0; j < numBgBar; j ++) {
	  			g.setColor(bgColor.get((bgLoop + i + j) % 256));
	  			g.fillRect(rectWidth * i, rectHeight * j, rectWidth, rectHeight);
  			}
  		}
  		
  		g.setColor(Color.BLACK);
  		
  		g.setFont(new Font("Roman", Font.BOLD, 50));
  		g.drawString("Mode Selection:", 50, 100);
  		
  		g.setFont(new Font("Roman", Font.BOLD, 26));
  		//Normal Mode
  		g.drawString("Peace Mode: After the notes are generated", 140, 170);
  		g.drawString("by music frequency, the notes are evenly", 140, 198);
  		g.drawString("disturbed throughout the columns.", 140, 226);
  		
  		//Classic Mode
  		g.drawString("Classic Mode: After the notes are generated", 140, 370);
  		g.drawString("by music frequency, the notes are put into", 140, 398);
  		g.drawString("the columns without being evenly disturbed.", 140, 426);
  		g.drawString("Warning: Majority of the notes will appear", 140, 454);
  		g.drawString("on the leftmost column.", 140, 482);
  		
  		
  		//Kim Mode
  		/*
  		g.drawString("Kim Mode: You can only play hardcore Havana.", 140, 570);
  		g.drawString("I am not sorry.", 140, 598);
  		*/
    	
    	g.setFont(new Font("Roman", Font.BOLD, 30));
    	
    	if(getCurrentMode)
    	{
    		g.setColor(Color.GREEN);
    		g.fillRect(50, 140, 80, 80);
    		DrawCenterString(g, 50, 140, 80, 80, "On", new Font("Roman", Font.BOLD, 30));	
    		g.setColor(Color.RED);
    		g.fillRect(50, 340, 80, 80);
    		DrawCenterString(g, 50, 340, 80, 80, "Off", new Font("Roman", Font.BOLD, 30));
    	}
    	else if(!(getCurrentMode))
    	{
    		g.setColor(Color.RED);
    		g.fillRect(50, 140, 80, 80);
    		DrawCenterString(g, 50, 140, 80, 80, "Off", new Font("Roman", Font.BOLD, 30));
    		g.setColor(Color.GREEN);
    		g.fillRect(50, 340, 80, 80);
    		DrawCenterString(g, 50, 340, 80, 80, "On", new Font("Roman", Font.BOLD, 30));	
    	}
    	
    	
    	
    	
	    //g.drawString("Setting panel is coming ...", 100, (bgLoop += 50) % 600);
	    
	    // Repaint display
  		repaint();
    }
    
    public void DrawCenterString(Graphics g, int x, int y, int width, int height, String s, Font font) {
	    FontRenderContext frc = new FontRenderContext(null, true, true);
	    
	    Rectangle2D r2D = font.getStringBounds(s, frc);
	    int rWidth = (int) Math.round(r2D.getWidth());
	    int rHeight = (int) Math.round(r2D.getHeight());
	    int rX = (int) Math.round(r2D.getX());
	    int rY = (int) Math.round(r2D.getY());

	    int a = (width / 2) - (rWidth / 2) - rX;
	    int b = (height / 2) - (rHeight / 2) - rY;
	    
	    g.setFont(font);
	    g.setColor(Color.BLACK);
	    g.drawString(s, x + a, y + b);
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
	}

	public void Stop() {
		bgTimer.stop();
	}

  	@Override
	public void mouseClicked(MouseEvent e) 
	{
  		if(Option1.contains(e.getX(), e.getY()) && getCurrentMode == true)
  			request.ChangeGameState(false);
  		else if(Option1.contains(e.getX(), e.getY()) && getCurrentMode == false)
  			request.ChangeGameState(true);
  		else if(Option2.contains(e.getX(), e.getY()) && getCurrentMode == true)
  			request.ChangeGameState(false);
  		else if(Option2.contains(e.getX(), e.getY()) && getCurrentMode == false)
  			request.ChangeGameState(true);
  		getCurrentMode = request.GetGameState();
  		repaint();
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
        			request.SettingSwitchRequest();
        	}
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }
	}
}
