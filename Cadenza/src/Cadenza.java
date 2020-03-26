/*
 * Joseph Zhang
 * 4/16/2019
 * 
 * Music game "Cadenza"
 */

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

interface SwitchRequest {
	void MenuSwitchRequest(int id);
	void GameSwitchRequest(boolean bForced);
	void HelpSwitchRequest();
	void SettingSwitchRequest();
	void SelectSwitchRequest();
	void SelectSwitchRequest(String name, int len, float diff, String logo);
	void InfoSwitchRequest();
	void FinishSwitchRequest();
	
	String GetWorkingDir();
	String GetSelectedMusic();
	String GetSelectedLogo();
	int GetScoreNumber(int type);
	void SetMusicFiles(String wav, String csv);
	void SetMusicBackground(String img);
	void ChangeGameState(boolean bMode);
	boolean GetGameState();
	
}

public class Cadenza extends JFrame implements SwitchRequest {
    private static final int winTile = 64;
    private static final int winWidth = winTile * (10 + 2);
    private static final int winHeight = winTile * (10 + 1);

    private Container contp;
	private CardLayout cards; 

	private MenuPanel menu = null;
	private GamePanel game = null;
	private HelpPanel help = null;
	private SettingPanel setting = null;
	private SelectPanel select = null;
	private InfoPanel info = null;
	private FinishPanel finish = null;

	public String workingDir = "";
	
	public static void main(String[] args) {
		// Getting reference to Main thread 
        Thread mainThread = Thread.currentThread(); 
        System.out.println("Current thread is " + mainThread.getName()); 
		
        // Launch game
        new Cadenza();
	}

	Cadenza() {
		super("Cadenza");
		
		workingDir = System.getProperty("user.dir");
		System.out.println("Working directory = " + workingDir);
		
		contp = getContentPane(); 
		
		cards = new CardLayout(0, 0);
		contp.setLayout(cards);
		
		menu = new MenuPanel(winWidth, winHeight);
		menu.RegisterCallback(this);
		
		game = new GamePanel(winWidth, winHeight);
		game.RegisterCallback(this);
		
		select = new SelectPanel(winWidth, winHeight);
		select.RegisterCallback(this);
		
		help = new HelpPanel(winWidth, winHeight);
		help.RegisterCallback(this);
		
		setting = new SettingPanel(winWidth, winHeight);
		setting.RegisterCallback(this);
		
		info = new InfoPanel(winWidth, winHeight);
		info.RegisterCallback(this);
		
		finish = new FinishPanel(winWidth, winHeight);
		finish.RegisterCallback(this);
		
		contp.add(menu, "Menu");
		contp.add(game, "Game");
		contp.add(help, "Help");
		contp.add(setting, "Setting");
		contp.add(select, "Select");
		contp.add(info, "Info");
		contp.add(finish, "Finish");
		
	    // Setup listener for window close
	    this.addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent e) {
	        	ExitGame();
	        }
	    });
	
		setSize(winWidth, winHeight);
    	setBackground(Color.BLACK);
		setResizable(false);
		
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        int winX = (screenSize.width - winWidth) / 2;
        int winY = (screenSize.height - winHeight) / 2;
        setLocation(winX, winY);

	    setDefaultCloseOperation(DISPOSE_ON_CLOSE);   
	    setVisible(true);
	    
		cards.show(contp, "Menu");
	    menu.Start(true);
		//cards.show(contp, "Select");
	    //select.Start();
		//cards.show(contp, "Info");
	    //info.Start("Music", 190, 1.0f, "");
		//cards.show(contp, "Finish");
	    //finish.Start();
		//cards.show(contp, "Help");
	    //help.Start();
		//cards.show(contp, "Setting");
	    //setting.Start();
	}

	public void ExitGame() {
    	menu.Stop(false);	// here should check the current mode!!!
    	game.Stop();
    	
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	System.out.println("Thank you for playing!");
    	
    	System.exit(0);
	}
	
	public void MenuSwitchRequest(int id) {
		if (id == 0) {	// main menu
			menu.Stop(true);
			cards.show(contp, "Game");
			game.Start();
		}
		else if (id == 1) {	// menu help
			menu.Stop(false);
			cards.show(contp, "Help");
			help.Start();
		}
		else if (id == 2) {	// menu exit
			ExitGame();
		}
		else if (id == 3) {	// menu setting
			menu.Stop(false);
			cards.show(contp, "Setting");
			setting.Start();
		}
		else if (id == 4) {	// menu load
			menu.Stop(false);
			cards.show(contp, "Select");
			select.Start();
		}
		
		System.out.println("Menu switch request from " + id);
	}
	
	public void GameSwitchRequest(boolean bForced) {
		game.Stop();
		
		if (bForced) {
			cards.show(contp, "Menu");
			menu.Start(true);
		}
		else {
			cards.show(contp, "Finish");
			finish.Start();
		}
		
		System.out.println("Game switch request");
	}
	
	public void HelpSwitchRequest() {
		help.Stop();
		cards.show(contp, "Menu");
		menu.Start(true);
		
		System.out.println("Help switch request");
	}

	public void SettingSwitchRequest() {
		setting.Stop();
		cards.show(contp, "Menu");
		menu.Start(true);
		
		System.out.println("Setting switch request");
	}

	public void SelectSwitchRequest() {
		select.Stop();
		cards.show(contp, "Menu");
		menu.Start(true);
		
		System.out.println("Select switch request");
	}
	
	public void SelectSwitchRequest(String name, int len, float diff, String logo) {
		select.Stop();
		cards.show(contp, "Info");
		info.Start(name, len, diff, logo);
		
		System.out.println("Select switch request");
	}
	
	public void InfoSwitchRequest() {
		info.Stop();
		cards.show(contp, "Select");
		select.Start();
		
		System.out.println("Info switch request");
	}
	
	public void FinishSwitchRequest() {
		finish.Stop();
		cards.show(contp, "Menu");
		menu.Start(true);
		
		System.out.println("Finish switch request");
	}

	public String GetWorkingDir() {
		return workingDir;
	}
	
	public String GetSelectedMusic() {
		return select.GetSelectedItemName();
	}
	
	public String GetSelectedLogo() {
		return select.GetSelectedItemLogo();
	}
	
	public int GetScoreNumber(int type) {
		return game.FeedbackScoreNumber(type);
	}

	public void SetMusicFiles(String wav, String csv) {
		game.SetMusicFiles(wav, csv);
	}
	
	public void SetMusicBackground(String img) {
		game.SetMusicBackground(img);
	}

	public void ChangeGameState(boolean bMode)
	{
		game.feedbackChangeGameState(bMode);
	}
	
	public boolean GetGameState()
	{
		return game.feedbackReturnGameState();
	}
}
