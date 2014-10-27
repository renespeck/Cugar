package de.uni_leipzig.cugar.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import prefuse.util.display.ExportDisplayAction;
import prefuse.util.io.SimpleFileFilter;

import de.uni_leipzig.cugar.data.Model;
import de.uni_leipzig.cugar.visual.AggregatePanel;

import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import java.awt.Dimension;
/**
 * 
 * The Application class is used to build all Swing components
 * with all other classes in  this package and with a {@link de.uni_leipzig.cugar.data.Model} instance 
 * to get all necessary data.
 *  
 * @author rspeck
 * 
 */
public class Application extends MouseAdapter implements KeyListener,ActionListener{

	public static final int WIDTH = 1024;
	public static final int HEIGHT = 768;
	public final static String NAME = "CUGAR - Graph Clustering and Visualization Framework";
	public final static String LOGO = "/CUGAR.png";

	private final String version = "beta 0.6";
	private final String source = "http://sourceforge.net/projects/borderflow";

	private Model m_model = null;
	private ExportDisplayAction m_exportDisplayAction = null;
	// JToolBar
	// arguments key to action
	private static final String SAVE = "Save Cluster";
	private static final String EXPORT = "Export Image";
	private static final String FILTER = "Filter";
	private static final String DIRECTION = "Direction";
	private static final String COLOR = "Color";
	private static final String QUIT = "Quit";
	//private static final String QUALITY = "Quality";
	
	private JPanel jContentPane = null;
	private JSplitPane jSplitPane = null;
	private AggregatePanel aggrPanel = null;
	private JTabbedPane rightTabbedPane = null;
	private ClusterPanel clusterPanel = null;
	private GraphStatisticPanel graphStatPanel = null;
	private JMenuItem aboutMenuItem = null;
	private JDialog aboutDialog = null;
	private JPanel aboutContentPane = null;
	private JTextField aboutSrcField = null, aboutVersionField = null;
	private JFileChooser openChooser = null, saveChooser = null;
	/**
	 * Constructs a new Application by initializing a {@link AggregatePanel},
	 *  {@link ClusterPanel} and  a {@link GraphStatisticPanel}   {@link java.util.Observer} instance
	 * and some other Swing components with data of the given {@link Model} instance data.
	 * Registers all the Observers to the given model.
	 * 
	 * @param model the  {@link Model} instance contains the data.
	 */
	public Application(Model model){	
		m_model = model;	
	}
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	public JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJToolBar(), BorderLayout.PAGE_START);	
			jContentPane.add(getJSplitPane(), BorderLayout.CENTER);
		}
		return jContentPane;
	}
	/**
	 * This method initializes jSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setLeftComponent(getAggrPanel());
			jSplitPane.setRightComponent(getRightTabbedPane());
			jSplitPane.setOneTouchExpandable(true); 		
			jSplitPane.setContinuousLayout(false);  	
			jSplitPane.setDividerLocation(-1);
			jSplitPane.setResizeWeight(1.0);	
		}
		return jSplitPane;
	}
	/**
	 * This method initializes aggrPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getAggrPanel(){
		if (aggrPanel == null ) {
			aggrPanel = new AggregatePanel(WIDTH,HEIGHT);				
			aggrPanel.setSeedLabel(((ClusterPanel)getClusterPanel()).getSeedJLabel());
			aggrPanel.update(m_model, aggrPanel.INIT_ID);				
			aggrPanel.getDisplay().addKeyListener(this);
			aggrPanel.getDisplay().addMouseListener(this);	
			aggrPanel.getDisplay().requestFocus();
			m_model.addObserver(aggrPanel);	
		}
		return aggrPanel;
	}
	/**
	 * This method initializes rightTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private JTabbedPane getRightTabbedPane() {
		if (rightTabbedPane == null) {
			rightTabbedPane = new JTabbedPane();
			rightTabbedPane.setBackground(Color.white);					
			rightTabbedPane.addTab("force",(((AggregatePanel)getAggrPanel()).getForceTab()));
			rightTabbedPane.addTab("cluster", getClusterPanel());
			rightTabbedPane.addTab("graph", getGraphStatPanel());
			rightTabbedPane.addTab("log",LogManager.instance().getLogPanelAppender().getLogPanel());
		}
		return rightTabbedPane;
	}
	/**
	 * This method initializes clusterPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getClusterPanel(){
		if (clusterPanel == null ) {
			clusterPanel = new ClusterPanel(); 				
			clusterPanel.update(m_model, clusterPanel.INIT_ID);
			clusterPanel.addKeyListener(this);
			clusterPanel.addMouseListener(this);	
			m_model.addObserver(clusterPanel);
		}
		return clusterPanel;
	}	
	/**
	 * This method initializes graphStatPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getGraphStatPanel(){
		if (graphStatPanel == null ) {
			graphStatPanel = new GraphStatisticPanel();						
			graphStatPanel.update(m_model,graphStatPanel.INIT_ID);
			graphStatPanel.addKeyListener(this);
			graphStatPanel.addMouseListener(this);
			m_model.addObserver(graphStatPanel);
		}
		return graphStatPanel;
	}	
	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getAboutMenuItem() {
		if (aboutMenuItem == null) {
			aboutMenuItem = new JMenuItem();
			aboutMenuItem.setText("About");
			aboutMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JDialog aboutDialog = getAboutDialog();			
					try{
						//com.sun.awt.AWTUtilities.setWindowOpacity(aboutDialog, .7f);							
					}catch(Exception ee){
						//only works for win
					}
					aboutDialog.pack();
					Point loc = getJContentPane().getParent().getLocationOnScreen();
					loc.translate(getJContentPane().getParent().getWidth()/2-aboutDialog.getWidth()/2,getJContentPane().getParent().getHeight()/2 - aboutDialog.getHeight()/2);
					aboutDialog.setLocation(loc);
					aboutDialog.setVisible(true);
				}
			});
		}
		return aboutMenuItem;
	}
	/**
	 * This method initializes aboutDialog	
	 * 	
	 * @return javax.swing.JDialog
	 */
	private JDialog getAboutDialog() {
		if (aboutDialog == null) {
			aboutDialog = new JDialog();
			aboutDialog.setTitle(NAME);
			aboutDialog.setContentPane(getAboutContentPane());
			aboutDialog.setIconImage(new ImageIcon(getClass().getResource(m_model.IMG_INFO)).getImage());
		}
		return aboutDialog;
	}
	/**
	 * This method initializes aboutContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getAboutContentPane() {
		if (aboutContentPane == null) {
			JLabel aboutSrcLabel = new JLabel();
			JLabel aboutVersionLabel = new JLabel();

			aboutSrcLabel.setText("Source");
			aboutVersionLabel.setText("Version");				

			aboutContentPane = new JPanel();
			aboutContentPane.setLayout(new GridLayout(0,1));
			aboutContentPane.add(aboutSrcLabel);	
			aboutContentPane.add(getAboutSrcField());
			aboutContentPane.add(aboutVersionLabel);
			aboutContentPane.add(getAboutVersionField());			
		}
		return aboutContentPane;
	}
	/**
	 * This method initializes aboutSrcField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getAboutSrcField() {
		if (aboutSrcField == null) {
			aboutSrcField = new JTextField();
			aboutSrcField.setEditable(false);
			aboutSrcField.setText(source);
		}
		return aboutSrcField;
	}
	/**
	 * This method initializes aboutVersionField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getAboutVersionField() {
		if (aboutVersionField == null) {
			aboutVersionField = new JTextField();
			aboutVersionField.setEditable(false);
			aboutVersionField.setText(version);
		}
		return aboutVersionField;
	}
	/**
	 * 
	 * @return
	 */
	public JFileChooser getOpenChooser(){
		if(openChooser==null){
			openChooser = new JFileChooser();
			openChooser.setDialogType(JFileChooser.OPEN_DIALOG);
			openChooser.setDialogTitle("Open File");
			openChooser.setAcceptAllFileFilterUsed(false);		        
			// GraphML
			SimpleFileFilter ff = new SimpleFileFilter("xml",
			"GraphML File (*.xml, *.graphml, *.gz)");
			ff.addExtension("graphml");
			ff.addExtension("gz");
			openChooser.setFileFilter(ff);			
			// edge lists
			ff = new SimpleFileFilter("txt",
			"Edge List File (*.txt, *.tab, *.csv, *.ssv)");
			ff.addExtension("txt"); 
			ff.addExtension("tab");
			ff.addExtension("csv");
			ff.addExtension("ssv");
			openChooser.setFileFilter(ff);
		}
		return openChooser;
	}
	/**
	 * 
	 * @return
	 */
	public JFileChooser getSaveChooser(){
		if(saveChooser==null){
			saveChooser = new JFileChooser();	
			saveChooser.setDialogTitle("Save Cluster");
			saveChooser.setDialogType(JFileChooser.SAVE_DIALOG);			
		}
		return saveChooser;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if(cmd.equals(Model.OPENFILE)){
			int value =  getOpenChooser().showOpenDialog(getJContentPane().getParent()); 	
			if( getOpenChooser().getSelectedFile() != null && value == JFileChooser.APPROVE_OPTION )			
				m_model.openFile(getOpenChooser().getSelectedFile());
		}
		if(cmd.equals(SAVE)){	
			int value = getSaveChooser().showSaveDialog(getJContentPane().getParent()); 				
			if(getSaveChooser().getSelectedFile() != null && value == JFileChooser.APPROVE_OPTION )		
				m_model.saveFile(getSaveChooser().getSelectedFile());			
		}
		if(cmd.equals(QUIT)){
			System.exit(0);			
		}		
		if(cmd.equals(COLOR)){
			m_model.changeColor();	
			m_model.notifyGui(aggrPanel.INIT_ID);			
		}
		if(cmd.equals(FILTER)){
			m_model.changeDistanceFilter();	
			m_model.notifyGui(aggrPanel.INIT_ID);				
		}	
		if(
				cmd.equals(Model.TOPPED)||
				cmd.equals(Model.CLIQUE)||
				cmd.equals(Model.ERDOSRENYI)||
				cmd.equals(Model.BARABASI)||
				cmd.equals(Model.EPPSTEIN)||
				cmd.equals(Model.KLEINBERG)||
				cmd.equals(Model.GRID_GRAPH)||
				cmd.equals(Model.BALANCED_TREE)||
				cmd.equals(Model.DIAMOND_TREE)||
				cmd.equals(Model.HONEYCOMB)
		){
			m_model.openGraph(cmd);
		}
		if(cmd.equals(Model.PAUSE)){
			m_model.changePause();	
			m_model.notifyGui(Model.PAUSE);									
		}
		if(cmd.equals(DIRECTION)){
			m_model.changeDirection();	
			m_model.notifyGui(aggrPanel.INIT_ID);							
		}		
		if(cmd.equals(Model.QUALITY)){
			m_model.changeQuality();	
			m_model.notifyGui(aggrPanel.INIT_ID);			
		}
		if(cmd.equals(EXPORT)){
			if(m_exportDisplayAction == null)
				m_exportDisplayAction = new ExportDisplayAction(((AggregatePanel)getAggrPanel()).getDisplay());
			m_exportDisplayAction.actionPerformed(null);
		}
		filterB.setIcon(new ImageIcon(getClass().getResource(m_model.getDistanceFilterImg()), FILTER));	
		pauseB.setIcon(new ImageIcon(getClass().getResource(m_model.getPauseImg()), Model.PAUSE));
		colorB.setIcon(new ImageIcon(getClass().getResource(m_model.getColorImg()), COLOR));
		qualityB.setIcon(new ImageIcon(getClass().getResource(m_model.getQualityImg()), Model.QUALITY));
	}
	/**
	 * keyPressed(KeyEvent e) to actionPerformed(ActionEvent e) 
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.isControlDown())
			switch(e.getKeyCode()){
			case KeyEvent.VK_1: actionPerformed(new ActionEvent(this,0,Model.TOPPED));break;
			case KeyEvent.VK_2: actionPerformed(new ActionEvent(this,0,Model.CLIQUE));break;
			case KeyEvent.VK_3: actionPerformed(new ActionEvent(this,0,Model.ERDOSRENYI));break;
			case KeyEvent.VK_4: actionPerformed(new ActionEvent(this,0,Model.BARABASI));break;
			case KeyEvent.VK_5: actionPerformed(new ActionEvent(this,0,Model.EPPSTEIN));break;
			case KeyEvent.VK_6: actionPerformed(new ActionEvent(this,0,Model.KLEINBERG));break;
			case KeyEvent.VK_7: actionPerformed(new ActionEvent(this,0,Model.GRID_GRAPH));break;
			case KeyEvent.VK_8: actionPerformed(new ActionEvent(this,0,Model.BALANCED_TREE));break;
			case KeyEvent.VK_9: actionPerformed(new ActionEvent(this,0,Model.DIAMOND_TREE));break;
			case KeyEvent.VK_0: actionPerformed(new ActionEvent(this,0,Model.HONEYCOMB));break;
			case KeyEvent.VK_O: actionPerformed(new ActionEvent(this,0,Model.OPENFILE));break;
			case KeyEvent.VK_P: actionPerformed(new ActionEvent(this,0,Model.PAUSE));break;
			case KeyEvent.VK_S: actionPerformed(new ActionEvent(this,0,SAVE));break;
			case KeyEvent.VK_Q: actionPerformed(new ActionEvent(this,0,QUIT));break;
			case KeyEvent.VK_F: actionPerformed(new ActionEvent(this,0,FILTER));break;
			case KeyEvent.VK_D: actionPerformed(new ActionEvent(this,0,DIRECTION));break;
			case KeyEvent.VK_C: actionPerformed(new ActionEvent(this,0,COLOR));break;
			case KeyEvent.VK_H: actionPerformed(new ActionEvent(this,0,Model.QUALITY));break;
			case KeyEvent.VK_E: actionPerformed(new ActionEvent(this,0,EXPORT));break;
			default:break;
			}
	}
	/** Empty */
	@Override
	public void keyReleased(KeyEvent e) {}
	/** Empty */
	@Override
	public void keyTyped(KeyEvent e) {}
	/**
	 * Sets focus to clicked Component
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		e.getComponent().requestFocusInWindow();		
	}
	private JToggleButton colorB = null, filterB = null, pauseB= null, qualityB= null;
	private JToolBar jToolBar = null;
	/**
	 * This method initializes jToolBar, uses JButtons with actionPerformed(ActionEvent e) method
	 * 	
	 * @return javax.swing.JToolBar	
	 */
	private JToolBar getJToolBar() {
		if (jToolBar == null) {
			jToolBar = new JToolBar();
			jToolBar.setRollover(true);				

			// open 
			JMenu submenu = new JMenu("Open Graph");			
			submenu.add(getMenuItem(Model.TOPPED,KeyEvent.VK_1));
			submenu.add(getMenuItem(Model.CLIQUE,KeyEvent.VK_2));
			submenu.addSeparator();
			submenu.add(getMenuItem(Model.ERDOSRENYI,KeyEvent.VK_3));
			submenu.add(getMenuItem(Model.BARABASI,KeyEvent.VK_4));			
			submenu.add(getMenuItem(Model.EPPSTEIN,KeyEvent.VK_5));
			submenu.add(getMenuItem(Model.KLEINBERG,KeyEvent.VK_6));			
			submenu.addSeparator();
			submenu.add(getMenuItem(Model.GRID_GRAPH,KeyEvent.VK_7));
			submenu.add(getMenuItem(Model.BALANCED_TREE,KeyEvent.VK_8));
			submenu.add(getMenuItem(Model.DIAMOND_TREE,KeyEvent.VK_9));
			submenu.add(getMenuItem(Model.HONEYCOMB,KeyEvent.VK_0));	

			JPopupMenu load  = new JPopupMenu();
			load.add(getMenuItem(Model.OPENFILE,KeyEvent.VK_O));
			load.add(submenu);	
			load.add(getMenuItem(SAVE,KeyEvent.VK_S));

			load.add(getMenuItem(EXPORT,KeyEvent.VK_E));
			load.addSeparator();	
			load.add(getMenuItem(QUIT,KeyEvent.VK_Q));
			jToolBar.add(makeButton(load, "", new ImageIcon(getClass().getResource(m_model.IMG_FOLDER))));

			// color
			colorB = new JToggleButton();				
			colorB.setActionCommand(COLOR);
			colorB.addActionListener(this);	

			colorB.setIcon(new ImageIcon(getClass().getResource(m_model.getColorImg()), COLOR));
			colorB.setToolTipText("Toggle Color (Ctrl + C)");
			jToolBar.add(colorB);

			// filter
			filterB = new JToggleButton();
			filterB.setActionCommand(FILTER);
			filterB.addActionListener(this);
			filterB.setIcon(new ImageIcon(getClass().getResource(m_model.getDistanceFilterImg()), FILTER));	 
			filterB.setToolTipText("Toggle Distance Filter (Ctrl + F)");
			jToolBar.add(filterB);

			// pause
			pauseB = new JToggleButton();
			pauseB.setActionCommand(Model.PAUSE);
			pauseB.addActionListener(this);
			pauseB.setIcon(new ImageIcon(getClass().getResource(m_model.getPauseImg()), Model.PAUSE));	 
			pauseB.setToolTipText("Start/Pause Animation (Ctrl + P)");
			jToolBar.add(pauseB);

			// settings
			//JPopupMenu animation  = new JPopupMenu();
			//animation.add(getMenuItem(QUALITY,KeyEvent.VK_H));	
			//animation.add(getMenuItem(DIRECTION,KeyEvent.VK_D));			
			//jToolBar.add(makeButton(animation, "", new ImageIcon(getClass().getResource(m_model.IMG_ANIMATION))));			
			qualityB = new JToggleButton();
			qualityB.setActionCommand(Model.QUALITY);
			qualityB.addActionListener(this);
			qualityB.setIcon(new ImageIcon(getClass().getResource(m_model.getQualityImg()), Model.QUALITY));	 
			qualityB.setToolTipText("Toggle High Quality (Ctrl + H)"); 
			jToolBar.add(qualityB);
			// help
			JPopupMenu help  = new JPopupMenu();		
			help.add(getAboutMenuItem());
			jToolBar.add(makeButton(help,"",new ImageIcon(getClass().getResource(m_model.IMG_INFO))));
			//
			jToolBar.addKeyListener(this);
			jToolBar.addMouseListener(this);
		}
		return jToolBar;
	}

	private AbstractButton makeButton(JPopupMenu pop, String title, ImageIcon icon) {
		MenuToggleButton b = new MenuToggleButton(title, icon);
		if(pop!=null)
			b.setPopupMenu(pop);		
		return b;
	}
	/** Builds a JMenuItem object*/
	private JMenuItem getMenuItem(String cmd, int keyEvent) {		
		JMenuItem menuItem = new JMenuItem();
		menuItem.setText(cmd);
		menuItem.addActionListener(this);
		menuItem.setActionCommand(cmd);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(keyEvent, Event.CTRL_MASK, true));			
		return menuItem;
	}
	/**
	 * Adding an arrow to Icon
	 */
	static class MenuArrowIcon implements Icon {
		// http://terai.xrea.jp/Swing/ToolButtonPopup.html
		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics2D g2 = (Graphics2D)g;
			g2.setPaint(Color.BLACK);
			g2.translate(x,y);
			g2.drawLine( 2, 3, 6, 3 );
			g2.drawLine( 3, 4, 5, 4 );
			g2.drawLine( 4, 5, 4, 5 );
			g2.translate(-x,-y);
		}
		@Override
		public int getIconWidth()  { return 9; }
		@Override
		public int getIconHeight() { return 9; }
	}
	/**
	 * Adding JPopupMenu to JToolBar-Button
	 */
	static class MenuToggleButton extends JToggleButton {
		// http://terai.xrea.jp/Swing/ToolButtonPopup.html
		private static final Icon i = new MenuArrowIcon();
		protected JPopupMenu pop;

		public MenuToggleButton() {
			this("", null);
		}
		public MenuToggleButton(Icon icon) {
			this("", icon);
		}
		public MenuToggleButton(String text) {
			this(text, null);
		}
		public MenuToggleButton(String text, Icon icon) {
			super();
			Action a = new AbstractAction(text) {
				@Override
				public void actionPerformed(ActionEvent ae) {
					MenuToggleButton b = (MenuToggleButton)ae.getSource();
					if(pop!=null) pop.show(b, 0, b.getHeight());
				}
			};
			a.putValue(Action.SMALL_ICON, icon);
			setAction(a);
			setFocusable(false);
			setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4+i.getIconWidth()));
		}

		public void setPopupMenu(final JPopupMenu pop) {
			this.pop = pop;
			pop.addPopupMenuListener(new PopupMenuListener() {
				@Override
				public void popupMenuCanceled(PopupMenuEvent e) {}
				@Override
				public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
				@Override
				public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
					setSelected(false);
				}
			});
		}
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Dimension dim = getSize();
			Insets ins = getInsets();
			int x = dim.width-ins.right;
			int y = ins.top+(dim.height-ins.top-ins.bottom-i.getIconHeight());
			i.paintIcon(this, g, x, y);
		}
	}
}