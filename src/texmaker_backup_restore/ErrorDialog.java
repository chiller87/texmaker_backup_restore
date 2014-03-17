package texmaker_backup_restore;

import java.awt.Dimension;
import java.awt.Font;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class ErrorDialog {

	public ErrorDialog() {
		super();
	}
	
	
	public static void showErrorDialog(JFrame parent, Exception e) 
	{
		ErrorDialog.showErrorDialog(parent, "An error occured", e);
	}
	
	
	public static void showErrorDialog(JFrame parent, String error) {
		ErrorDialog.showErrorDialog(parent, "An error occured", error);
	}
	
	public static void showErrorDialog(JFrame parent, String title, String error) {
		JLabel label = new JLabel(error);
		JOptionPane.showMessageDialog(parent, label, title, JOptionPane.ERROR_MESSAGE);
	}
	
	
	public static void showErrorDialog(JFrame parent, String title, Exception e) {
		
		// create and configure a text area - fill it with exception text.
		final JTextArea textArea = new JTextArea();
		textArea.setFont(new Font("Sans-Serif", Font.PLAIN, 10));
		textArea.setEditable(false);
		StringWriter writer = new StringWriter();
		e.printStackTrace(new PrintWriter(writer));
		textArea.setText(writer.toString());
		
		// stuff it in a scrollpane with a controlled size.
		JScrollPane scrollPane = new JScrollPane(textArea);		
		scrollPane.setPreferredSize(new Dimension(350, 150));
		
		// pass the scrollpane to the joptionpane.				
		JOptionPane.showMessageDialog(parent, scrollPane, title, JOptionPane.ERROR_MESSAGE);
	}
	
	
	
}
