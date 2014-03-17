package texmaker_backup_restore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class FileOperator {
	
	private static final int BACKUP_DIALOG = 0;
	private static final int RESTORE_DIALOG = 1;
	

	private static FileOperator mInstance;
	
	private String mUserPath = System.getProperty("user.dir");
	private String mWinLocation;
	private String mUnixLocation;
	
	private String mTempFile = "texmaker.tmp";
	
	private File mToLocation;
	private File mFromLocation;
	
	
	private FileOperator() {
		loadDefaults();
	}
	
	private void loadDefaults() {
		String home = System.getProperty("user.home");
		mWinLocation = home +"\\AppData\\Roaming\\xm1\\texmaker.ini";
		mUnixLocation = home+"/.config/xm1/texmaker.ini";
	}

	public static FileOperator getInstance() {
		if(mInstance == null)
			mInstance = new FileOperator();
		
		return mInstance;
	}
	
	
	public String getLocation(String system) {
		if(system == "win")
			return mWinLocation;
		else if(system == "unix")
			return mUnixLocation;
		else
			return null;
	}
	
	
	private File showFileDialog(JFrame parent, int option) {
		JFileChooser fc = new JFileChooser(mUserPath);

		int retVal = -1;
		if(option == RESTORE_DIALOG)
			retVal = fc.showOpenDialog(parent);
		else if(option == BACKUP_DIALOG)
			retVal = fc.showSaveDialog(parent);
		
		if(retVal == JFileChooser.CANCEL_OPTION || retVal == -1)
			return null;
		
		return fc.getSelectedFile();
	}
	
	
	
	public boolean backup(JFrame parent, String system) {
		
		String location = getLocation(system);
		System.out.println(location);
		File from = new File(location);
		File to = showFileDialog(parent, BACKUP_DIALOG);
		
		if(system == null) {
			ErrorDialog.showErrorDialog(parent, "Your System is not supported!");
			return false;
		}
		if(location == null) {
			ErrorDialog.showErrorDialog(parent, "no File Location specified for OS '"+system+"'!");
			return false;
		}
		if(!from.exists()) {
			ErrorDialog.showErrorDialog(parent, "the specified file '"+location+"' does not exist!");
			return false;
		}
		if(to == null) {
			ErrorDialog.showErrorDialog(parent, "no file has been choosen!");
			return false;
		}
		System.out.println("backup-file: "+to.getAbsolutePath());
		
		
		
		FileReader fr;
		BufferedReader br;
		
		FileWriter fw;
		BufferedWriter bw;
		
		try {
			fr = new FileReader(from);
			br = new BufferedReader(fr);
			
			if(!to.exists())
				to.createNewFile();
			fw = new FileWriter(to.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			
			String currLine;
			
			while((currLine = br.readLine()) != null) {
				
				if(isRelevant(currLine)) {
					System.out.println(currLine);
					bw.write(currLine+"\n");
				}
			}
			
			br.close();
			bw.close();
			
			
			JOptionPane.showMessageDialog(parent, "backup completeted", "Success!", JOptionPane.INFORMATION_MESSAGE);
			return true;
			
		} catch (IOException e) {
			ErrorDialog.showErrorDialog(parent, "error reading file", e);
			e.printStackTrace();
			return false;
		}
	}
	
	
	
	public boolean restore(JFrame parent, String system) {
		
		String location = getLocation(system);
		System.out.println(location);
		File to = new File(location);
		File from = showFileDialog(parent, RESTORE_DIALOG);
		
		// error handling
		if(system == null) {
			ErrorDialog.showErrorDialog(parent, "Your System is not supported!");
			return false;
		}
		if(location == null) {
			ErrorDialog.showErrorDialog(parent, "no File Location specified for OS '"+system+"'!");
			return false;
		}
		if(!to.exists()) {
			ErrorDialog.showErrorDialog(parent, "the specified file '"+location+"' does not exist!");
			return false;
		}
		if(from == null) {
			ErrorDialog.showErrorDialog(parent, "no file has been choosen!");
			return false;
		}
		if(!from.exists()) {
			ErrorDialog.showErrorDialog(parent, "the file you have choosen does not exist!");
			return false;
		}
		
		File tmpFile = new File(mTempFile);
		HashMap<String,	String> lines = new HashMap<>();
		System.out.println("backup-file: "+from.getAbsolutePath());
		// for reading and writing
		FileReader fr;
		BufferedReader br;
		FileWriter fw;
		BufferedWriter bw;
		
		try {
			// created a reader for the backupfile
			fr = new FileReader(from);
			br = new BufferedReader(fr);			
			String currLine;
			
			// read the backupfile
			while((currLine = br.readLine()) != null) {	
				// get the first part of the line (0 to '=')
				String relevantPart = getRelevantPart(currLine);
				// put the complete line in the map with relevant part as key
				lines.put(relevantPart, currLine);
			}
			br.close();
			
			// open the original texmaker file
			fr = new FileReader(to);
			br = new BufferedReader(fr);
			
			// open the tmp-file
			fw = new FileWriter(tmpFile);
			bw = new BufferedWriter(fw);
			
			// if the current line is in the map, doent write it in the tmp-file
			while((currLine = br.readLine()) != null) {
				if(isRelevant(lines.keySet(), currLine))
					bw.write(currLine+"\n");
			}
			
			// append the lines out of the map
			for(String line : lines.values())
				bw.write(line+"\n");
						
			bw.close();
			br.close();
			
			
			if(!to.delete()) {
				ErrorDialog.showErrorDialog(parent, "could not delete original config file!");
				return false;
			}
			
			
			// rename tmp-file to original texmaker config file
			boolean succ = tmpFile.renameTo(to);
			System.out.println("success? "+ succ);
			
			JOptionPane.showMessageDialog(parent, "restore completed", "Success!", JOptionPane.INFORMATION_MESSAGE);
			
			return true;
			
		} catch (IOException e) {
			ErrorDialog.showErrorDialog(parent, "error reading file", e);
			e.printStackTrace();
			return false;
		}
	}
	
	
	
	private boolean isRelevant(Set<String> keySet, String currLine) {
		for(String start : keySet) {
			if(currLine.startsWith(start))
				return false;
		}
		return true;
	}


	private String getRelevantPart(String line) {
		int pos = line.indexOf("=");
		if(pos < 0)
			return null;
		return line.subSequence(0, pos).toString();
		
	}
	
	
	private boolean isRelevant(String line) {
		if(line.startsWith("Editor\\UserCompletion") || line.startsWith("User\\Menu") || (line.startsWith("User\\Tag") && !line.startsWith("User\\TagList")))
			return true;		
		return false;
	}

}
