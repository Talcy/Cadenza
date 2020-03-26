/*
 * Joseph Zhang
 * 
 * 4/26/19
 * MenuPanel.java
 */

import java.awt.*;  import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.*; import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

//basic menu panel
public class MenuPanel extends JPanel implements MouseListener, MouseMotionListener
{ 
	public SwitchRequest request = null;

	private int dispWidth, dispHeight;
	private BufferedImage dispBuffer;
	private static final int numBgBar = 10;
	
	private List<Color> bgColor = new ArrayList<Color>();
	
	final String title = "Cadenza";
	final Font font = new Font("Roman", Font.BOLD, 50);
	
	private Timer bgTimer = null;
	private final int bgTimerTick = 50;	// ms
	private int bgLoop = 0;
	private int [] bgMusicBar = new int[numBgBar];
	
	public MainMenu mainMenu;
	public int mainMenuSize;
	public SubMenu subMenu1, subMenu2, subMenu3, subMenu4;
	
    private String MUSIC_FILE = "Darth_Vader.wav";
    private MusicPlayer mPlayer = null;

	public MenuPanel(int width, int height)
	{
		// Menu display size
		dispWidth = width;
		dispHeight = height;
		
		// Display buffer 
		dispBuffer = new BufferedImage(dispWidth, dispHeight, BufferedImage.TYPE_INT_ARGB);
		
		setBackground(Color.BLACK); 
        setPreferredSize(new Dimension(dispWidth, dispHeight));
        //setMaximumSize(new Dimension(dispWidth, dispHeight));
        //setMinimumSize(new Dimension(dispWidth, dispHeight));
        setDoubleBuffered(true);
		
        // Set background color
		// https://krazydad.com/tutorials/makecolors.php
        float colour_f = (float)0.15;
		int amplitude = 127;
		int center = 128;
		/*
		 * Gray color
		 * 
		for (int i = 0; i < 256; i ++) {
		   int value = (int)(Math.sin(colour_f * i) * amplitude + center);

		   bgColor.add(new Color(value, value, value));
		}
		*/
        for (int i = 0; i < 256; i ++) {
        	int green = (int)(Math.sin(colour_f * i + 0) * amplitude + center);
        	int blue = (int)(Math.sin(colour_f * i + 2) * amplitude + center);
        	int red = (int)(Math.sin(colour_f * i + 4) * amplitude + center);
        	
        	bgColor.add(new Color(red, green, blue));
        }
        
        // Generate music bar high randomly
        for (int i = 0; i < numBgBar; i ++) {
        	bgMusicBar[i] = (int)(Math.random() * dispHeight);
        }

        // Build up menus
	    mainMenuSize = dispWidth / 2;
		mainMenu = new MainMenu((dispWidth - mainMenuSize) / 2, (dispHeight - mainMenuSize) / 2, mainMenuSize, mainMenuSize, "Start");
	    
		subMenu1 = new SubMenu((dispWidth - mainMenuSize) / 2, (dispHeight - mainMenuSize) / 2, mainMenuSize / 2, mainMenuSize / 2, 1, "Help");
	    subMenu2 = new SubMenu((dispWidth - mainMenuSize) / 2, (dispHeight - mainMenuSize) / 2 + mainMenuSize / 2, mainMenuSize / 2, mainMenuSize / 2, 2, "Exit");
	    subMenu3 = new SubMenu((dispWidth - mainMenuSize) / 2 + mainMenuSize / 2, (dispHeight - mainMenuSize) / 2 + mainMenuSize / 2, mainMenuSize / 2, mainMenuSize / 2, 3, "Setting");
	    subMenu4 = new SubMenu((dispWidth - mainMenuSize) / 2 + mainMenuSize / 2, (dispHeight - mainMenuSize) / 2, mainMenuSize / 2, mainMenuSize / 2, 4, "Load");
        
		// Set timer for background change
        bgTimer = new Timer(bgTimerTick, new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	bgLoop += 1;
				
		    	for (int i = 0; i < numBgBar; i ++) {
		    		bgMusicBar[i] -= 10;
		    		
		    		if (bgMusicBar[i] <= 0)
		    			bgMusicBar[i] = (int)(Math.random() * dispHeight);
		    	}
		    	
		    	mainMenu.Shape();
				subMenu1.Move();
				subMenu2.Move();
				subMenu3.Move();
				subMenu4.Move();
				
		        FlipDisplay(dispBuffer.getGraphics());
		    }
		});
    
		addMouseListener(this);
		addMouseMotionListener(this);
        
		// Play background music
        mPlayer = new MusicPlayer(MUSIC_FILE);
		mPlayer.RegisterFeedback(new MusicFeedback());
	}
	
	class MusicFeedback implements Feedback {
		@Override
		public void FeedbackStartDisplay() { }
		public void FeedbackStopDisplay() { }
		public void FeedbackForceStop() { }
		public void FeedbackPositionMusic(long pos) { }
		public int FeedbackTimeSegmentSize() { return -1;}
		public int FeedbackFreqSegmentSize() { return 0; }
		public List<Float> FeedbackFreqData(int segment) { return null; }
		public void FeedbackScore(int type, int score) {}
		public int FeedbackScoreNumber(int type) { return 0; }

		@Override
		public void FeedbackStartMusic() { 
			System.out.println("Title music started");
		}
		
		public void FeedbackStopMusic() {
			// Continue playing background music
			/*
			mPlayer = null;
			mPlayer = new MusicPlayer(MUSIC_FILE);
			mPlayer.start();
			*/
		}
		@Override
		public void feedbackChangeGameState(boolean bMode) {
			// TODO Auto-generated method stub
			
		}
	}
	
  	//Paints the background rectangles and start button  
    public void FlipDisplay(Graphics g) {
  		// Draw background
  		int rectWidth = dispWidth / numBgBar;

  		Graphics2D g2d = (Graphics2D)g.create();
  		
  		for (int i = 0; i < numBgBar; i ++) {
  			/* 
  			 * Transparency music bar 
  	  		BufferedImage buffImg = new BufferedImage(rectWidth, dispHeight, BufferedImage.TYPE_INT_ARGB);
  	  		Graphics2D gbi = buffImg.createGraphics();
  		  		
	  		// Draw rainbow bar
  			gbi.setPaint(bgColor.get((bgLoop + i) % 256));
  			gbi.fillRect(0, 0, rectWidth, dispHeight);
  			
			// Draw music bar
  	  		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
  	        gbi.setComposite(ac);
  	        
  			gbi.setPaint(bgColor.get((bgLoop + 4 + i) % 256));
  			gbi.fillRect(0, 0, rectWidth, dispHeight - bgMusicBar[i]);
  			
  			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1.0f));
  			g2d.drawImage(buffImg, rectWidth * i, 0, null);

  			gbi.dispose();
  			buffImg = null;
  			*/
  			g.setColor(bgColor.get((bgLoop + i) % 256));
  			g.fillRect(rectWidth * i, 0, rectWidth, dispHeight);
  			
  			g.setColor(bgColor.get((bgLoop + i + 2) % 256));
  			g.fillRect(rectWidth * i, dispHeight - bgMusicBar[i], rectWidth, bgMusicBar[i]);
  		}
  		
	    // Draw menu area
  		/*
  		g.setColor(Color.GRAY);
  		g.drawLine(dispWidth / 2, dispHeight / 10, dispWidth / 2, dispHeight / 10 * 9);
  		g.drawLine(dispWidth / 10, dispHeight / 2, dispWidth / 10 * 9, dispHeight / 2);
  		g.drawRect((dispWidth - mainMenuSize) / 2, (dispHeight - mainMenuSize) / 2, mainMenuSize, mainMenuSize);
	  	*/
  		
  		// Draw sub menu circles
  		subMenu1.bgColor = bgColor.get((bgLoop + 16) % 256);
  		subMenu1.Draw(g); 
  		subMenu2.bgColor = bgColor.get((bgLoop + 32) % 256);
  		subMenu2.Draw(g);
  		subMenu3.bgColor = bgColor.get((bgLoop + 48) % 256);
  		subMenu3.Draw(g);
  		subMenu4.bgColor = bgColor.get((bgLoop + 64) % 256);
  		subMenu4.Draw(g);
  		
  		// Draw main menu circle
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.9f));
  		
  		mainMenu.bgColor = bgColor.get((bgLoop + 80) % 256);
  		mainMenu.Draw(g2d);
        
  		// Draw game tital
		DrawCenterString(g2d, 0, 0, dispWidth, dispHeight / 8, title, font);
		
  		g2d.dispose();

  		// Repaint display
  		repaint();
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        grabFocus();
        g.drawImage(dispBuffer, 0, 0, this);
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
	    
		g.setColor(Color.GRAY);
	    g.drawString(s, x + a + 3, y + b + 3);
		g.setColor(Color.WHITE);
	    g.drawString(s, x + a, y + b);
	}
	
  	@Override
	public void mouseClicked(MouseEvent e) 
	{
		int x = e.getX();
		int y = e.getY();
		
		if (mainMenu.IsInside(x, y)) {
			if (request != null) {
				String name = request.GetSelectedMusic();
				if (name != null) {
					String path = request.GetWorkingDir() + "\\";
					
					if (new File(path + name + ".wav").exists()) {
				    	if (new File(path + name + ".csv").exists()) {
			    			request.SetMusicFiles(path + name + ".wav", path + name + ".csv");
			    			request.SetMusicBackground(request.GetSelectedLogo());
				    	}
					}
				}
			
				request.MenuSwitchRequest(0);
			}
		}
		else if (subMenu1.IsInside(x, y)) {
			if (request != null)
				request.MenuSwitchRequest(1);
			
			subMenu1.Close();
		}
		else if (subMenu2.IsInside(x, y)) {
			/*
		    String[] buttons = { "Yes", "No" };
		    int rc = JOptionPane.showOptionDialog(null, "Do you want to exit game?", "Confirmation",
		        JOptionPane.WARNING_MESSAGE, 0, null, buttons, buttons[1]);
		    
			if (rc == 0 && request != null)
			*/
				request.MenuSwitchRequest(2);
			
			subMenu2.Close();
		}
		else if (subMenu3.IsInside(x, y)) {
			if (request != null)
				request.MenuSwitchRequest(3);
			
			subMenu3.Close();
		}
		else if (subMenu4.IsInside(x, y)) {
			if (request != null)
				request.MenuSwitchRequest(4);
			/*
		    FileDialog dialog = new FileDialog((Frame)null, "Select Music File (*.wav)");
		    
		    dialog.setMode(FileDialog.LOAD);
		    if (request != null)
		    	dialog.setDirectory(request.GetWorkingDir());
		    dialog.setFile("*.wav");
		    dialog.setVisible(true);
		    
		    String path = dialog.getDirectory();
		    String file = dialog.getFile();
		    
		    if (path != null && file != null) {
			    String name = file.substring(0, file.lastIndexOf("."));
			    String ext = "";
			    
			    int dot = file.lastIndexOf('.');
			    if (dot > 0) {
			        ext = file.substring(dot + 1);
			    }
			    System.out.println(path + name + "." + ext);			
	
			    if (ext.equals("wav") && new File(path + name + ".wav").exists()) {
			    	if (new File(path + name + ".csv").exists()) {
			    		if (request != null) {
			    			request.SetMusicFiles(path + file, path + name + ".csv");
			    		}
			    	}
			    	else {
						JOptionPane.showMessageDialog(null, "Music data file does not exist!");
			    	}
			    }
		    }
		    */
			subMenu4.Close();
		}
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
  	public void mouseMoved (MouseEvent e) 
  	{
		int x = e.getX();
		int y = e.getY();
		
		if (mainMenu.IsInside(x, y))
			mainMenu.Active(true);
		else
			mainMenu.Active(false);
		
		if (subMenu1.IsInside(x, y)) {
			if (subMenu1.IsClose()) {
				subMenu1.Open();
				subMenu2.Close();
				subMenu3.Close();
				subMenu4.Close();
			}
			//else if (subMenu1.IsOpen())
			//	subMenu1.Close();
		}
		else if (subMenu2.IsInside(x, y)) {
		     if (subMenu2.IsClose()) {
				subMenu1.Close();
				subMenu2.Open();
				subMenu3.Close();
				subMenu4.Close();
		     }
		     //else if (subMenu2.IsOpen())
		     //	subMenu2.Close();
		}
		else if (subMenu3.IsInside(x, y)) {
		      if (subMenu3.IsClose()) {
		    	  subMenu1.Close();
		    	  subMenu2.Close();
		    	  subMenu3.Open();
		    	  subMenu4.Close();
		      }
		      //else if (subMenu3.IsOpen())
		      //  subMenu3.Close();
		}
		else if (subMenu4.IsInside(x, y)) {
		      if (subMenu4.IsClose()) {
		    	  subMenu1.Close();
		    	  subMenu2.Close();
		    	  subMenu3.Close();
		    	  subMenu4.Open();
		      }
		      //else if (subMenu4.IsOpen())
		      //  subMenu4.Close();
		}
  	}
  
  	void RegisterCallback(SwitchRequest call) {
  		request = call;
  	}

	public void Start(boolean bMusic) {
		bgTimer.start();
		
		if (mPlayer == null) {
			mPlayer = new MusicPlayer(MUSIC_FILE);
		}
		
		if (mPlayer.getState() == Thread.State.NEW && bMusic) {
			mPlayer.start();
		}
	}
	
	public void Stop(boolean bMusic) {
		bgTimer.stop();
		
		if (bMusic) {
			mPlayer.Stop();
			mPlayer = null;
		}
	}
	
  	public class MainMenu {
  		private int loop = -1;
  		private int shape = 0;
  		private boolean bActive = false;
	  
  		public int [] shape_normal = new int[] {-2, -4, -6, -2, 2, 0};
  		public int [] shape_active = new int[] {-4, -8, -16, -6, 2, 6, 2, 0};
	  
  		public int x, y, width, height;
  		String title = "";
  		Color bgColor = Color.RED;
    	public Font font = new Font("Roman", Font.BOLD, 50);
	  
  		MainMenu(int x, int y, int width, int height, String title) {
  			this.x = x + 32; 
  			this.y = y + 32;
  			this.width = width - 64;
  			this.height = height - 64;
  			this.title = title;
  		}
	  
  		public void Draw(Graphics g) {
  			g.setColor(bgColor);
  			g.fillOval(x - shape / 2, y - shape / 2, width + shape, height + shape);
  			DrawCenterString(g, x, y, width, height, title, font);
  		}
	  
  		public void Shape() {
  			if (bActive) {
  				loop = (loop + 1) % shape_active.length; 
  				shape = shape_active[loop];
  			}
  			else {
  				loop = (loop + 1) % shape_normal.length; 
  				shape = shape_normal[loop];
  			}
  		}
	  
  		public void Active(boolean bActive) {
  			this.bActive = bActive;
  		}
	  
  		public boolean IsInside(int x, int y) {
  			if (x >= this.x && x < this.x + this.width &&
  				y >= this.y && y < this.y + this.height) {
  				return true;
  			}
		  
  			return false;
  		}
  	}
  
  	public class SubMenu {
  		private int xo, yo;
  		private int xd, yd;
  		private final int step = 8;
	  
  		private boolean bRunning = false;
  		private boolean bOpen = false;

  		public int x, y, width, height;
  		public int direction;
  		public String title = "";
  		public Color bgColor = Color.YELLOW;
    	public Font font = new Font("Roman", Font.BOLD, 28);
	  
  		SubMenu(int x, int y, int width, int height, int direction, String title) {
  			this.x = x; 
  			this.y = y;
  			this.width = width;
  			this.height = height;
  			this.direction = direction;
  			this.title = title;
		  
  			switch (this.direction) {
  			case 1: xo = x - step * 15; yo = y - step * 15; break;
  			case 2: xo = x - step * 15; yo = y + step * 15; break;
  			case 3: xo = x + step * 15; yo = y + step * 15; break;
  			case 4: xo = x + step * 15; yo = y - step * 15; break;
  			default: xo = x; yo = y; break;
  			}
  		}

  		public void Draw(Graphics g) {
  			g.setColor(bgColor);
  			g.fillOval(x + xd, y + yd, width, height);
  			
  			g.setColor(Color.WHITE);
  			DrawCenterString(g, x + xd, y + yd, width, height, title, font);
  		}
	  
  		public void Open() {
  			if (!bOpen) {
  				bOpen = true;
  				bRunning = true;
  			}
  		}
	  
  		public void Close() {
  			if (bOpen) {
  				bOpen = false;
  				bRunning = true;
  			}
  		}
	  
  		public boolean IsOpen() {
  			if (bOpen && !bRunning)
  				return true;
		  
  			return false;
  		}
	  
  		public boolean IsClose() {
  			if (!bOpen && !bRunning)
  				return true;
		  
  			return false;
  		}
	  
  		public boolean IsInside(int x, int y) {
  			if (!bRunning) {
  				if (x >= this.x + xd && x < this.x + xd + width &&
  					y >= this.y + yd  && y < this.y + yd + height) {
  					return true;
  				}
  			}
		  
  			return false;
  		}
	  
  		public void Move() {
  			if (bRunning && bOpen)
  				switch (direction) {
  				case 1: 
  					xd -= step; yd -= step;
  					if (x + xd < xo || y + yd < yo) {
  						xd = xo - x; yd = yo - y;
  						bRunning = false;
  					}
  					break;
  				case 2: 
  					xd -= step; yd += step;
  					if (x + xd < xo || y + yd > yo) {
  						xd = xo - x; yd = yo - y;
  						bRunning = false;
  					}
  					break;
  				case 3: 
  					xd += step; yd += step; 
  					if (x + xd > xo || y + yd > yo) {
  						xd = xo - x; yd = yo - y;
  						bRunning = false;
  					}
  					break;
  				case 4: 
  					xd += step; yd -= step; 
  					if (x + xd > xo || y + yd < yo) {
  						xd = xo - x; yd = yo - y;
  						bRunning = false;
  					}
  					break;
  				default:
  					xd = 0; xd = 0;
  					bRunning = false;
  					break;
  				}
  			else if (bRunning && !bOpen) {
  				switch (direction) {
  				case 1: 
  					xd += step; yd += step;
  					if (x + xd > x || y + yd > y) {
  						xd = 0; yd = 0;
  						bRunning = false;
  					}
  					break;
  				case 2: 
  					xd += step; yd -= step; 
  					if (x + xd > x || y + yd < y) {
  						xd = 0; yd = 0;
  						bRunning = false;
  					}
  					break;
  				case 3: 
  					xd -= step; yd -= step; 
  					if (x + xd < x || y + yd < y) {
  						xd = 0; yd = 0;
  						bRunning = false;
  					}
  					break;
  				case 4: 
  					xd -= step; yd += step; 
  					if (x + xd < x || y + yd > y) {
  						xd = 0; yd = 0;
  						bRunning = false;
  					}
  					break;
  				default:
  					xd = 0; xd = 0;
  					bRunning = false;
  					break;
  				}
  			}
  		}
  	}
}
