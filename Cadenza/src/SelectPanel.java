/*
 * Joseph Zhang
 * 
 * 5/18/19
 * SelectPanel.java
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Timer;

public class SelectPanel extends JPanel implements MouseListener, MouseMotionListener {
	public SwitchRequest request = null;

	private int dispWidth, dispHeight;
	private static final int numBgBar = 10;
	private BufferedImage dispBuffer;

	private List<Color> bgColor = new ArrayList<Color>();
	
	private Timer bgTimer = null;
	private final int bgTimerTick = 100;	// ms
	private int bgLoop = 0;

	enum GameDifficulty {
		EASY, 
		MEDIUM,
		HARD
	}
	private final int numGroups = 3;
	private SelectGroup selGroups = null;
	public SelectItem selItem = null;
	
	private int mouseX = 0;
	private int mouseY = 0;
	
	public SelectPanel(int width, int height)
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
		
        for (int i = 0; i < 256; i ++) {
        	int green = (int)(Math.sin(colour_f * i + 0) * amplitude + center);
        	int blue = (int)(Math.sin(colour_f * i + 2) * amplitude + center);
        	int red = (int)(Math.sin(colour_f * i + 4) * amplitude + center);
        	
        	bgColor.add(new Color(red, green, blue));
        }
        
		// Set timer for background change
        bgTimer = new Timer(bgTimerTick, new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	bgLoop += 1;
		    	
		    	selGroups.UpdatePosition();
		        FlipDisplay(dispBuffer.getGraphics());
		    }
		});
    
		// Load all select items
		selGroups = new SelectGroup();
		LoadItemAll();
		
        // Monitor mouse actions
		addMouseListener(this);
		addMouseMotionListener(this);
		
		// Monitor keyboard
        addKeyListener(new KeyHandler());
	}
	
	public void LoadItemAll() {
		selGroups.AddItem("Camila_Cabello_Havana", 138, 0.1f, "Camila_Cabello_Havana_Logo.jpg");
		selGroups.AddItem("Darth_Vader", 190, 0.1f, "Darth_Vader_Logo.jpg");
		selGroups.AddItem("Legends_Never_Die", 241, 1.0f, "Legends_Never_Die_Logo.jpg");
		
		selGroups.AddItem("Mine_Diamonds", 237, 1.1f, "Mine_Diamonds_Logo.jpg");
		selGroups.AddItem("Beat_It", 263, 1.1f, "Beat_It.jpg");
		selGroups.AddItem("Despacito", 288, 1.1f, "Despacito_Logo.jpg");
		
		selGroups.AddItem("Bitch_Lasagna", 138, 7.0f, "Bitch_Lasagna_Logo.jpg");
		selGroups.AddItem("Littleroot_Town", 217, 6.0f, "Little_Root_Town_Logo.jpg");
		selGroups.AddItem("Dragon_Force", 307, 31.4f, "Dragon_Force.jpg");
	}

  	//Paints the background rectangles and start button  
    public void FlipDisplay(Graphics g) {
  		// Draw background
  		int rectWidth = dispWidth / numBgBar;
  		int rectHeight = dispWidth / numBgBar;

  		Graphics2D g2d = (Graphics2D)g.create();
  		
  		for (int i = 0; i < numBgBar; i ++) {
  			for (int j = 0; j < numBgBar; j ++) {
	  			g.setColor(bgColor.get((bgLoop + i + j) % 256));
	  			g.fillRect(rectWidth * i, rectHeight * j, rectWidth, rectHeight);
  			}
  		}
  		/*
  		g.setColor(Color.BLACK);
  		g.fillRect(0, 0, dispWidth, dispHeight);
  		
  		for (int i = 1; i < (numGroups * 2); i ++) {
  			g.setColor(Color.GRAY);
  			g.drawLine(0, dispHeight / (numGroups * 2) * i, dispWidth, dispHeight / (numGroups * 2) * i);
  			g.drawLine(dispWidth / (numGroups * 2) * i, 0, dispWidth / (numGroups * 2) * i, dispHeight);
  		}
  		*/
        for (int i = 0; i < numGroups; i ++) {
        	List<SelectItem> list = selGroups.GetGroupList(i);
			Iterator<SelectItem> iter = list.iterator();
			ViewPort view = selGroups.GetViewPort(i);
			
			int count = 0;
			while (iter.hasNext()) {
				SelectItem item = iter.next();
				
				if (view.IsInsideView(item)) {
					item.disp_color = bgColor.get((bgLoop + i * 16 + count * 2) % 256);
					item.Draw(g, view.GetOffset(), view.GetDirection());
				
					count ++;
				}
			 }
        }
        
  		g2d.dispose();

  		// Repaint display
  		repaint();
    }

    public void paintComponent(Graphics g) {
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
	
	public String GetSelectedItemName() {
		if (selItem != null)
			return selItem.name;
		
		return null;
	}
	
	public String GetSelectedItemLogo() {
		if (selItem != null)
			return selItem.logo;
		
		return null;
	}

	public class SelectItem {
		private int disp_zone;	// zone position
		private int disp_seq;	// sequence position
		private int disp_width, disp_height;	// for display
		private Color disp_color;	// for display
    	private final Font disp_font = new Font("Roman", Font.BOLD, 16);
		
		public String name = "";	// without ext.
		public int length = 0;		// (s)
		public float difficulty = 0.0f;
		public String logo = "";	// with ext.
		
		SelectItem(String name, int len, float diff, String logo) {
			this.name = name;
			this.length = len;
			this.difficulty = diff;
			this.logo = logo;
			
			disp_width = dispWidth / (numGroups + 1);
			disp_height = disp_width;
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
		    
			//g.setColor(Color.GRAY);
		    //g.drawString(s, x + a + 2, y + b + 2);
			g.setColor(Color.WHITE);
		    g.drawString(s, x + a, y + b);
		}
		
		public void Draw(Graphics g, int offset, int direction) {
			g.setColor(disp_color);
			
			int x = disp_zone - disp_width / 2;
			int y = (offset - disp_seq) - disp_height / 2;
			
			g.fillOval(x, y, disp_width, disp_height);
			DrawCenterString(g, x, y, disp_width, disp_height, name, disp_font);
		}
		
		public boolean IsInside(int x, int y) {
			int x1 = disp_zone - disp_width / 2;
			int x2 = disp_zone + disp_width / 2;
			int y1 = disp_seq + disp_height / 2;
			int y2 = disp_seq - disp_height / 2;
			
			//System.out.println("x1="+x1+", x2="+x2+", y1="+y1+", y2="+y2);
  			if (x > x1 && x < x2 && y < y1 && y > y2) {
  				//System.out.println("mouse position: "+x+", "+y+" <true>");
  	  			return true;
  			}
  			  
  			//System.out.println("mouse position: "+x+", "+y+" <false>");
  	  		return false;
		}
	}
	
	public class ViewPort {
		public int vp_top;
		public int vp_bottom;
		public int vp_direction;
		
		public int limit_top;
		public int limit_bottom;
		
		ViewPort () {
			System.out.println(dispHeight);
			System.out.println(numGroups);
			
			vp_bottom = -(dispHeight / numGroups * 2);
			vp_top = dispHeight / numGroups * 1;
			vp_direction = 1;	// 1: view port goes up, -1: viwe port goes down
			limit_top = 0;
			limit_bottom = 0;
		}
		
		public boolean IsInsideView(SelectItem item) {
			int item_top = item.disp_seq + item.disp_height / 2;
			int item_bottom = item.disp_seq - item.disp_height / 2;
			
			if (item_top > vp_bottom || item_bottom < vp_top) {
				//System.out.println("it="+item_top+", ib="+item_bottom+" <true>");
				return true;
			}
			
			//System.out.println("it="+item_top+", ib="+item_bottom+" <false>");
			return false;
		}
		
		public int GetOffset() {
			return vp_top;
		}
		
		public int GetDirection() {
			return vp_direction;
		}
	}
	
	public class SelectGroup {
		private List<List<SelectItem>> group_list = new ArrayList<List<SelectItem>>();
		private List<ViewPort> view_list = new ArrayList<ViewPort>();
		
		SelectGroup() {
			for (int i = 0; i < numGroups; i ++) {
				List<SelectItem> item_list = new ArrayList<SelectItem>();
				group_list.add(item_list);
				
				view_list.add(new ViewPort());
			}
		}

		public int GetDifficultyGroup(float diff) {
			if (diff <= 1.0) 
				return 0;	// GameDifficulty.EASY;
			else if (diff <= 5.0)
				return 1;	// GameDifficulty.MEDIUM
			
			return 2;		// GameDifficulty.HARD
		}
		
		public void AddItem(String name, int len, float diff, String bg) {
			int group = GetDifficultyGroup(diff);
			
			if (group >= 0 && group < numGroups) {
				List<SelectItem> item_list = group_list.get(group);
				SelectItem item = new SelectItem(name, len, diff, bg);
				
				int item_count = item_list.size();
				if (group == 0)
					item.disp_zone = dispWidth / (numGroups * 2) * 1;
				else if (group == 1)
					item.disp_zone = dispWidth / (numGroups * 2) * 3;
				else if (group == 2)
					item.disp_zone = dispWidth / (numGroups * 2) * 5;
				
				item.disp_seq = (dispHeight / 3) * item_count;
				item_list.add(item);
				
				ViewPort view = view_list.get(group);
				if (item.disp_seq > view.limit_top)
					view.limit_top = item.disp_seq;
				
				System.out.println("Group:" + group + ", " + name + "at: " + item.disp_seq);
			}
		}
		
		public void UpdatePosition() {
			final int move_step = 5;
			
			int zone_x = mouseX / (dispWidth / numGroups);
			int zone_y = mouseY / (dispHeight / (numGroups * 2)) - 2;
			if (zone_y > 0)
				zone_y -= 1;
	    	//System.out.println("ZoneX: " + zone_x + ", ZoneY: " + zone_y);
			
			for (int i = 0; i < numGroups; i ++) {
				ViewPort view = view_list.get(i);
				
				// check end of view
				//System.out.println("limit="+view.limit_top);
				if (view.vp_bottom > view.limit_top) {	// out of end
					view.vp_direction = -1;
				}
				else if (view.vp_top < view.limit_bottom) {	// out of begin
					view.vp_direction = 1;
				}
				
				if (zone_x == i) {	// current focused view
					if (zone_y > 0) {	// view port moving down, display up
						if (view.vp_direction > 0) {
							view.vp_top = view.vp_top - move_step * (zone_y + 1);
							view.vp_bottom = view.vp_bottom - move_step * (zone_y + 1);
						}
						else if (view.vp_direction < 0) {
							view.vp_top = view.vp_top + move_step * (zone_y + 1);
							view.vp_bottom = view.vp_bottom + move_step * (zone_y + 1);
						}
					}
					else if (zone_y < 0) {	// view port moving up, display down
						if (view.vp_direction > 0) {
							view.vp_top = view.vp_top - move_step * (zone_y - 1);
							view.vp_bottom = view.vp_bottom - move_step * (zone_y - 1);
						}
						else if (view.vp_direction < 0) {
							view.vp_top = view.vp_top + move_step * (zone_y - 1);
							view.vp_bottom = view.vp_bottom + move_step * (zone_y - 1);
						}
					}
					else {
						SelectItem item = IsSelectItem(mouseX, mouseY);
						if (item != null) {
							//int gap = view.vp_top - item.disp_seq - mouseY;
							//System.out.println(gap);
							if (bgLoop % 2 == 1) {
								view.vp_top = view.vp_top + move_step;
								view.vp_bottom = view.vp_bottom + move_step;
							}
							else {
								view.vp_top = view.vp_top - move_step;
								view.vp_bottom = view.vp_bottom - move_step;
							}
						}
						else {
							if (view.vp_direction > 0) {
								view.vp_top = view.vp_top + move_step;
								view.vp_bottom = view.vp_bottom + move_step;
							}
							else if (view.vp_direction < 0) {
								view.vp_top = view.vp_top - move_step;
								view.vp_bottom = view.vp_bottom - move_step;
							}
						}
					}
					
					//System.out.println("top="+view.vp_top+", bottom="+view.vp_bottom+", dir="+view.vp_direction);
				}
				else {
					if (view.vp_direction > 0) {
						view.vp_top = view.vp_top + move_step;
						view.vp_bottom = view.vp_bottom + move_step;
					}
					else if (view.vp_direction < 0) {
						view.vp_top = view.vp_top - move_step;
						view.vp_bottom = view.vp_bottom - move_step;
					}
				}
			}
		}
		
		public SelectItem IsSelectItem(int x, int y) {
			SelectItem item = null;
			
			// Looking for zone where mouse clicked at
			int zone_x = mouseX / (dispWidth / numGroups);
			ViewPort view = view_list.get(zone_x);
			int zone_y = view.vp_top - mouseY;
			
			// Searching item list
        	List<SelectItem> list = selGroups.GetGroupList(zone_x);
			Iterator<SelectItem> iter = list.iterator();
			while (iter.hasNext()) {
				item = iter.next();
				if (view.IsInsideView(item)) {
					if (item.IsInside(mouseX, zone_y)) {
						return item;
					}
				}
			}
			
			return item;
		}
		
		public List<SelectItem> GetGroupList(int group) {
			return group_list.get(group);
		}
		
		public ViewPort GetViewPort(int group) {
			return view_list.get(group);
		}
	}
	
  	@Override
	public void mouseClicked(MouseEvent e) 
	{
		int x = e.getX();
		int y = e.getY();

		SelectItem item = selGroups.IsSelectItem(x, y);
		if (item != null) {
    		if (request != null) {
    			request.SelectSwitchRequest(
    					item.name, item.length, item.difficulty, item.logo);
    		}
    		selItem = item;
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
  	public void mouseMoved(MouseEvent e) 
  	{
  		mouseX = e.getX();
		mouseY = e.getY();
  	}
	  	
	private class KeyHandler extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
        	int key = e.getKeyCode();
        	//System.out.println("Key pressed - " + key);

        	if (key == KeyEvent.VK_ESCAPE) {
        		if (request != null)
        			request.SelectSwitchRequest();
        	}
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }
	}
}
