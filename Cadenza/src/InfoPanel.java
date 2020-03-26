/*
 * Joseph Zhang
 * 
 * 5/19/19
 * SelectPanel.java
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class InfoPanel extends JPanel implements MouseListener, MouseMotionListener {
	public SwitchRequest request = null;
	
	private int dispWidth, dispHeight;
	private Image imageBG = null;

	public String name = "";	// without ext.
	public int length = 0;		// (s)
	public float difficulty;	// factor
	public String logo = "";	// with ext.
	
	private Color bgColor [] = new Color[256];
	
	public InfoPanel(int width, int height)
	{
		// Menu display size
		dispWidth = width;
		dispHeight = height;
		
		setBackground(Color.BLACK); 
        setPreferredSize(new Dimension(dispWidth, dispHeight));
        //setMaximumSize(new Dimension(dispWidth, dispHeight));
        //setMinimumSize(new Dimension(dispWidth, dispHeight));
        //setDoubleBuffered(true);
		
        // Monitor mouse actions
		addMouseListener(this);
		addMouseMotionListener(this);
		
		// Monitor keyboard
        addKeyListener(new KeyHandler());
        
        float colour_f = (float)0.25;
        for (int i = 0; i < 256; i ++) {
        	int green = (int)(Math.sin(colour_f * i + 0) * 127 + 128);
        	int blue = (int)(Math.sin(colour_f * i + 2) * 127 + 128);
        	int red = (int)(Math.sin(colour_f * i + 4) * 127 + 128);
        	
        	bgColor[i] = new Color(red, green, blue);
        }
        
	}
	
	

	public void SetBackgroundImage(String file) {
        // Display background
        try {
            File imgFile = new File(file);
            imageBG = ImageIO.read(imgFile);
        }
        catch (IOException e) {
        	File imgFile = new File("Music-Info-768x704-1.jpg");
            try {
				imageBG = ImageIO.read(imgFile);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
	}
	
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        
        if (imageBG != null)
        	g.drawImage(imageBG, 0, 0, null);
        
        // Show information of music
        
        int random = (int)(Math.random() * 256);
    	g.setColor(bgColor[random]);
    	
	    g.setFont(new Font("Roman", Font.BOLD, 48));
	    g.drawString(name, 5, 64);
	    
	    int mins = length / 60;
	    int second = length % 60;
	    
	    g.setFont(new Font("Roman", Font.BOLD, 32));
	    g.drawString("Music length: "+mins+":"+second, 5, 200);
	    
	    g.drawString("Difficulty: "+difficulty, 5, 250);
        
	    grabFocus();
    }

	public void Start(String name, int len, float diff, String logo) {
		this.name = name;
		this.length = len;
		this.difficulty = diff;
		this.logo = logo;
		
        SetBackgroundImage(this.logo);
        repaint();
	}

	public void Stop() {
	}

  	void RegisterCallback(SwitchRequest call) {
	  	request = call;
  	}

  	@Override
	public void mouseClicked(MouseEvent e) 
	{
		if (request != null)
			request.InfoSwitchRequest();
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
        			request.InfoSwitchRequest();
        	}
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }
	}
}
