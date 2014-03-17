package texmaker_backup_restore;

import java.awt.Button;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

public class Tebare extends JFrame {

	/**
	 * default serialVersionID
	 */
	private static final long serialVersionUID = 1L;
	
	private static Tebare mInstance;
	
	private FileOperator mFileOperator;
	private String mSystem;
	
	private Panel mMainPanel;
	private MenuBar mMenubar;
	private Button mBackupButton;
	private Button mRestoreButton;
	
	
	
	private ActionListener backupListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			mFileOperator.backup(mInstance, mSystem);
		}
	};
	
	private ActionListener restoreListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			mFileOperator.restore(mInstance, mSystem);
		}
	};
	
	
	
	
	private Tebare() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 450);
		setMinimumSize(new Dimension(350, 100));
		setVisible(true);
		setTitle("Texmaker Backup and Restore");
		
		mFileOperator = FileOperator.getInstance();
		determineOS();
		if(mSystem == null) {
			ErrorDialog.showErrorDialog(this, "An error occured", "Your System is not supported!");
			dispose();
		}
		else
			System.out.println("Your OS: "+ mSystem);
		
		initComponents();
		drawMenubar();
	}
	
	
	private void determineOS() {
		String os = System.getProperty("os.name").toLowerCase();
		if(os.indexOf("win") >= 0)
			mSystem = "win";
		else if(os.indexOf("mac") >= 0)
			mSystem = "mac";
		else if(os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0)
			mSystem = "unix";
	}


	private void drawMenubar() {
		mMenubar = new MenuBar();
		
		Menu file = new Menu("File");
		MenuItem settings = new MenuItem("Settings");
		file.add(settings);
		
		mMenubar.add(file);
		setMenuBar(mMenubar);
	}


	private void initComponents() {
		
		mMainPanel = new Panel();
		
		mBackupButton = new Button("Backup");
		mBackupButton.addActionListener(backupListener);
		mRestoreButton = new Button("Restore");
		mRestoreButton.addActionListener(restoreListener);
		
		mMainPanel.add(mBackupButton);
		mMainPanel.add(mRestoreButton);
		
		add(mMainPanel);
		pack();
	}
	
	
	
	public static Tebare getInstance() {
		if(mInstance == null)
			mInstance = new Tebare();
		
		return mInstance;
	}
	
	
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// start the program
					Tebare tbr = Tebare.getInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
}
