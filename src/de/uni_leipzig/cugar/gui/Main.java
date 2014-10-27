package de.uni_leipzig.cugar.gui;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.PropertyConfigurator;

import de.uni_leipzig.cugar.data.Model;
import de.uni_leipzig.cugar.gui.Application;

public class Main {
	/**
	 * A demo of BorderFlow GUI version.
	 */
	public static void cugarDemo(){
		//org.apache.log4j.BasicConfigurator.configure();
		PropertyConfigurator.configure("Log4j.properties");
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");			
		} catch (Exception e) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e1) {	
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Model model = new Model();				
				JFrame jFrame = new JFrame(Application.NAME);
				jFrame.setSize(Application.WIDTH,Application.HEIGHT);	
				jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);			
				jFrame.setContentPane(new Application(model).getJContentPane());
				jFrame.setVisible(true);			
				jFrame.setIconImage(new ImageIcon(getClass().getResource(Application.LOGO)).getImage());
			}
		});		
	}	
	/**
	 * Entry point.
	 */
	public static void main(String[] args) {
		// TODO: start all algos. here with args para.
		if(args.length > 0)
			de.uni_leipzig.bf.cluster.Main.main(args);
		else
			cugarDemo();
	}
}