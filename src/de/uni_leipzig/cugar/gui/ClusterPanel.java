package de.uni_leipzig.cugar.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import de.uni_leipzig.cugar.data.Model;
/**
 * It's an Observer JPanel for settings and configurations of a {@link de.uni_leipzig.cugar.cluster.ClusterAlgorithm}.
 * 
 * @author rspeck
 * 
 */ 
public class ClusterPanel extends JPanel implements Observer { 

	public final String INIT_ID = "ClusterPanel";	
	private static final long serialVersionUID = 1L;
	/** algorithm JPanel*/
	private JPanel algoPanel = null;
	/** algorithm JLabel*/
	private JLabel algoLabel = null;
	/** algorithm JComboBox*/
	private JComboBox algoCBox = null;		

	/** configuration JLabel*/
	private JLabel label_A = null;
	/** configuration JLabel*/
	private JLabel label_B = null;
	/** configuration JLabel*/
	private JLabel label_C = null;
	/** configuration JLabel*/
	private JLabel label_D = null;
	/** configuration JComboBox*/	
	private JComboBox cBox_A = null;
	/** configuration JComboBox*/
	private JComboBox cBox_B = null;
	/** configuration JComboBox*/
	private JComboBox cBox_C = null;
	/** configuration JComboBox*/
	private JComboBox cBox_D = null;

	/** seeds JPanel*/
	private JPanel seedPanel = null;
	/** seeds JLabel*/
	private JLabel seedLabel = null;
	/** seeds JScrollPane*/
	private JScrollPane jScrollPane = null;	
	/** seeds JPanel*/
	private JPanel seedBottomPanel = null;	
	/** seeds JPanel*/
	private JPanel seedUpperPanel = null;
	/** seeds JButton*/
	private JButton addButton = null;
	/** default table text*/
	public final String DEFAULT_SEED_TEXT = "click a node";	
	/** seeds JTable*/
	private JTable seedTable = null;
	/** seeds SeedTableModel*/
	private SeedTableModel seedTableModel = null;
	/** start JPanel*/
	private JPanel startPanel = null;
	/** cluster JButton*/
	private JButton clusterButton = null;
	/** threshold JSlider*/
	private JSlider jSlider = null;
	/** threshold JLabel*/
	private JLabel m_thresholdLabel = new JLabel("100%");
	/** threshold Box for JSlider, JLabel */
	private Box box = null;
	/**
	 * Default constructor.
	 */
	public ClusterPanel() {
		super();
		initialize();
	}
	/**
	 * This method initializes this.
	 * 
	 * @return void
	 */
	private void initialize() {
		seedTableModel = new SeedTableModel();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(Color.white);
		setForeground(new Color(51, 51, 51));
		add(getAlgoPanel(), null);
		add(getSeedPanel(), null);
		add(getStartPanel(), null);
	}
	/**
	 * This method initializes algoPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getAlgoPanel() {
		if (algoPanel == null) {
			algoPanel = new JPanel();
			algoPanel.setLayout(new GridLayout(0,2));			
			algoPanel.setMaximumSize(new Dimension(327,150));			
			algoPanel.setBackground(Color.white);
			algoPanel.setBorder(BorderFactory.createTitledBorder("Settings"));
			algoPanel.add(getAlgoLabel());
			algoPanel.add(getAlgoCBox());
			algoPanel.add(getLabel_A());
			algoPanel.add(getCBox_A());
			algoPanel.add(getLabel_B());
			algoPanel.add(getCBox_B());
			algoPanel.add(getLabel_C());
			algoPanel.add(getCBox_C());
			algoPanel.add(getLabel_D());
			algoPanel.add(getCBox_D());
		}
		return algoPanel;
	}
	/**
	 * This method initializes algoLabel
	 * 	
	 * @return javax.swing.JLabel
	 */		
	private JLabel getAlgoLabel() {
		if(algoLabel == null){
			algoLabel = new JLabel();
			algoLabel.setText("Algorithm");
			algoLabel.setBackground(Color.white);
		}
		return algoLabel;
	}
	/**
	 * This method initializes label_A
	 * 	
	 * @return javax.swing.JLabel
	 */	
	private JLabel getLabel_A() {
		if(label_A == null){
			label_A = new JLabel();
			label_A.setText("Harden");
			label_A.setBackground(Color.white);
		}
		return label_A;
	}
	/**
	 * This method initializes label_B
	 * 	
	 * @return javax.swing.JLabel
	 */
	private JLabel getLabel_B() {
		if(label_B == null){
			label_B = new JLabel();
			label_B.setText("Cache");
			label_B.setBackground(Color.white);
		}
		return label_B;
	}	
	/**
	 * This method initializes label_C
	 * 	
	 * @return javax.swing.JLabel
	 */
	private JLabel getLabel_C() {
		if(label_C == null){
			label_C = new JLabel();
			label_C.setText("Heuristic");
			label_C.setBackground(Color.white);
		}
		return label_C;
	}	
	/**
	 * This method initializes label_D
	 * 	
	 * @return javax.swing.JLabel
	 */
	private JLabel getLabel_D() {
		if(label_D == null){
			label_D = new JLabel();
			label_D.setText("Test One");
			label_D.setBackground(Color.white);
		}
		return label_D;
	}
	/**
	 * This method initializes algoCBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getAlgoCBox() {
		if (algoCBox == null) {
			algoCBox = new JComboBox();
			algoCBox.setBackground(Color.white);
		}
		return algoCBox;
	}
	/**
	 * This method initializes cBox_A	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getCBox_A() {
		if (cBox_A == null) {
			cBox_A = new JComboBox();
			cBox_A.setBackground(Color.white);
		}
		return cBox_A;
	}

	/**
	 * This method initializes cBox_B	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getCBox_B() {
		if (cBox_B == null) {
			cBox_B = new JComboBox();
			cBox_B.setBackground(Color.white);
		}
		return cBox_B;
	}

	/**
	 * This method initializes cBox_C	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getCBox_C() {
		if (cBox_C == null) {
			cBox_C = new JComboBox();
			cBox_C.setBackground(Color.white);
		}
		return cBox_C;
	}

	/**
	 * This method initializes cBox_D	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getCBox_D() {
		if (cBox_D == null) {
			cBox_D = new JComboBox();
			cBox_D.setBackground(Color.white);
		}
		return cBox_D;
	}

	/**
	 * This method initializes seedPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getSeedPanel() {
		if (seedPanel == null) {		
			seedPanel = new JPanel();
			seedPanel.setLayout(new FlowLayout());
			seedPanel.setSize(new Dimension(327,170));
			seedPanel.setMaximumSize(seedPanel.getSize());
			seedPanel.setMinimumSize(seedPanel.getSize());
			seedPanel.setPreferredSize(seedPanel.getSize());				
			seedPanel.setBackground(Color.white);
			seedPanel.setBorder(BorderFactory.createTitledBorder("Nodes"));
			seedPanel.add(getSeedUpperPanel(), null);
			seedPanel.add(getSeedBottomPanel(), null);			
		}
		return seedPanel;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();	
			jScrollPane.setViewportView(getSeedTable());
			jScrollPane.setBorder(BorderFactory.createEtchedBorder());
		}
		return jScrollPane;
	}
	/**
	 * This method initializes seedTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getSeedTable() {
		if (seedTable == null) {
			seedTable = new JTable();			
			seedTable.setModel(seedTableModel);			
			seedTable.setVisible(true);		
			seedTable.setTableHeader(null);
			seedTable.addKeyListener(new KeyAdapter() {   
				@Override
				public void keyPressed(KeyEvent e) {    				
					if(e.getKeyCode() == (KeyEvent.VK_DELETE)){
						int selectedRows[]= seedTable.getSelectedRows();							
						seedTableModel.deleteRows(selectedRows);	
						if(seedTableModel.getRowCount() == 0)
							setJSliderVisible(true);
					}					
				}
			});
		}
		return seedTable;
	}
	/**
	 * This method initializes seedBottomPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getSeedBottomPanel() {
		if (seedBottomPanel == null) {
			seedBottomPanel = new JPanel();					
			seedBottomPanel.setLayout(new BorderLayout());
			seedBottomPanel.setSize(new Dimension(300,80));
			seedBottomPanel.setMaximumSize(seedBottomPanel.getSize());
			seedBottomPanel.setPreferredSize(seedBottomPanel.getSize());
			seedBottomPanel.setMinimumSize(seedBottomPanel.getSize());				
			seedBottomPanel.add(getJScrollPane());
		}
		return seedBottomPanel;
	}
	/**
	 * This method initializes seedUpperPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getSeedUpperPanel() {
		if (seedUpperPanel == null) {
			seedUpperPanel = new JPanel();
			seedUpperPanel.setLayout(new GridLayout(0,2));
			seedUpperPanel.setBackground(Color.white);		
			seedUpperPanel.add(getSeedLabel(), new GridBagConstraints());
			seedUpperPanel.add(getAddButton(), getAddButton().getName());
		}
		return seedUpperPanel;
	}
	/**
	 * This method initializes seedLabel	
	 * 	
	 * @return javax.swing.JLabel
	 */
	private JLabel getSeedLabel() {
		if(seedLabel == null){
			seedLabel = new JLabel();
			seedLabel.setSize(150,30);
			seedLabel.setPreferredSize(seedLabel.getSize());
			seedLabel.setMinimumSize(seedLabel.getSize());
			seedLabel.setText(DEFAULT_SEED_TEXT);
		}return seedLabel;
	}
	/**
	 * This method initializes addButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAddButton() {
		if (addButton == null) {
			addButton = new JButton();
			addButton.setMaximumSize(new Dimension(150,30));
			addButton.setPreferredSize(addButton.getMaximumSize());
			addButton.setMinimumSize(addButton.getMaximumSize());
			addButton.setSize(addButton.getMaximumSize());
			addButton.setPreferredSize(addButton.getMaximumSize());
			addButton.setText("Add Node");
		}
		return addButton;
	}
	/**
	 * This method initializes clusterButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getClusterButton() {
		if (clusterButton == null) {
			clusterButton = new JButton();
			clusterButton.setMaximumSize(new Dimension(150,30));
			clusterButton.setPreferredSize(clusterButton.getMaximumSize());
			clusterButton.setMinimumSize(clusterButton.getMaximumSize());
			clusterButton.setSize(clusterButton.getMaximumSize());			
			clusterButton.setText("Cluster");			
		}
		return clusterButton;
	}

	/**
	 * This method initializes startPanel for cluster Button and threshold slider.
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getStartPanel() {
		if (startPanel == null) {
			startPanel = new JPanel();
			startPanel.setLayout(new GridLayout(0,1));
			startPanel.setSize(new Dimension(327,80));
			startPanel.setMaximumSize(startPanel.getSize());			
			startPanel.setMinimumSize(startPanel.getSize());
			startPanel.setPreferredSize(startPanel.getSize());			
			startPanel.setBorder(BorderFactory.createTitledBorder("Start"));		
			startPanel.setBackground(Color.white);			
			startPanel.add(getBox(), null);
		}
		return startPanel;
	}

	/**
	 * This method initializes jSlider	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getJSlider() {
		if (jSlider == null) {
			String s = m_thresholdLabel.getText().replace("%", "");			
			jSlider = new JSlider(SwingConstants.HORIZONTAL,0,100,Integer.parseInt(s));
			jSlider.setBackground(Color.white);
			jSlider.add(new JLabel("Threshold: "));
			jSlider.setPreferredSize(new Dimension(300,30));
			jSlider.setMaximumSize(new Dimension(300,30));			
			jSlider.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent e){	
					m_thresholdLabel.setText(String.valueOf(((JSlider)e.getSource()).getValue()) + "%");
				}			
			});			
		}
		return jSlider;
	}
	private JLabel jSliderLabel = null;
	private JLabel getJSliderLabel(){
		if (jSliderLabel == null)
			jSliderLabel =  new JLabel("Threshold: ");
		return jSliderLabel;
	}

	/**
	 * This method initializes box.
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private Box getBox() {
		if (box == null) {
			box = new Box(BoxLayout.X_AXIS);
			box.setBackground(Color.white);
			box.add(getJSliderLabel());
			box.add(getJSlider());
			box.add(m_thresholdLabel);	
			box.add(getClusterButton());				
		}
		return box;
	}
	public  void setJSliderVisible(boolean bo){
		getJSliderLabel().setVisible(bo);
		getJSlider().setVisible(bo);
		m_thresholdLabel.setVisible(bo);
		this.validate();
		this.repaint();
	}
	/**
	 * Sets configurations for an algorithm.
	 */
	@Override
	public void update(Observable o, Object arg) {
		if(o instanceof Model){
			// this is calling one time, just init this
			if(arg != null && arg.equals(INIT_ID) && algoCBox.getItemCount() == 0){			
				String[] names = ((Model)o).getClusterContext().getAlgorithmNames();
				for(String name : names)
					algoCBox.addItem(name);
				algoCBox.setSelectedItem( ((Model)o).getClusterContext().getCurrentAlgorithmName());
				algoCBox.addActionListener(new AlgoActionListener(o));
				clusterButton.addActionListener(new ClusterActionListener(o,this));
				addButton.addActionListener( new AddButtonActionListener(this, seedTableModel));
			}
			// this is for every algo switch
			if(arg != null)
				if(arg.equals(Model.SET_ALGO) || arg.equals(INIT_ID) ){
					cBox_A.removeAllItems();
					for(int i = 0 ; i < ((Model)o).getClusterContext().getA().length; i++){
						if(i == 0)
							label_A.setText(cutLongLabels(((Model)o).getClusterContext().getA())[i]);
						else
							cBox_A.addItem(cutLongLabels(((Model)o).getClusterContext().getA())[i]);
					}
					cBox_B.removeAllItems();
					for(int i = 0 ; i < ((Model)o).getClusterContext().getB().length; i++){
						if(i == 0)
							label_B.setText(cutLongLabels(((Model)o).getClusterContext().getB())[i]);
						else
							cBox_B.addItem(cutLongLabels(((Model)o).getClusterContext().getB())[i]);
					}
					cBox_C.removeAllItems();
					for(int i = 0 ; i < ((Model)o).getClusterContext().getC().length; i++){
						if(i == 0)
							label_C.setText(cutLongLabels(((Model)o).getClusterContext().getC())[i]);
						else
							cBox_C.addItem(cutLongLabels(((Model)o).getClusterContext().getC())[i]);
					}

					cBox_D.removeAllItems();
					for(int i = 0 ; i < ((Model)o).getClusterContext().getD().length; i++){
						if(i == 0)
							label_D.setText(cutLongLabels(((Model)o).getClusterContext().getD())[i]);
						else
							cBox_D.addItem(cutLongLabels(((Model)o).getClusterContext().getD())[i]);
					}
				}		
			if(arg != null)
				if(arg.equals(Model.OPENFILE) ){
					setJSliderVisible(true); 
					seedTableModel.deleteRows();	
					seedLabel.setText(DEFAULT_SEED_TEXT);
				}
		}
	}
	/**
	 * Cuts all Strings in config to a max. length of 18.
	 */
	private String[] cutLongLabels(String[] config){		
		int max = 18;
		for(int i = 0 ; i < config.length; i++)			
			if(config[i].length()>max)
				config[i] = config[i].substring(0,max-3) + "...";				
		return config;
	}
	/**
	 * Gets the JLabel for clicked nodes.
	 */
	public JLabel getSeedJLabel(){
		return seedLabel;
	}
	/**
	 * ActionListener implementation for a JButton to add a node to a inner TableModel instance.
	 */
	class AddButtonActionListener implements ActionListener{

		private JPanel jp = null;
		private TableModel tm = null;

		public AddButtonActionListener(JPanel jp, TableModel tm){
			this.jp = jp;		
			this.tm = tm;			
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			String node = "";
			if(jp instanceof ClusterPanel){
				node = ((ClusterPanel)jp).getSeedJLabel().getText();
				if(!node.equals(((ClusterPanel)jp).DEFAULT_SEED_TEXT) && tm instanceof SeedTableModel){					
					((SeedTableModel)tm).addObject( node);	
					((ClusterPanel)jp).setJSliderVisible(false); // hide slider
				}
			}
		} 		
	}	
	/**
	 * ActionListener implementation for a JComboBox to choose an algorithm.
	 */	
	class AlgoActionListener implements ActionListener{
		private Observable o = null;
		public AlgoActionListener(Observable o){
			this.o = o;			
		}
		@Override
		public void actionPerformed(ActionEvent e){		
			if(o instanceof Model){
				String s = ((JComboBox)e.getSource()).getSelectedItem().toString();		
				((Model)o).getClusterContext().setAlgorithm(s);	
				((Model)o).notifyGui(Model.SET_ALGO);
			}
		}	
	}
	/**
	 * ActionListener implementation for a JButton to start clustering.
	 */	
	class ClusterActionListener implements ActionListener{
		private Observable o = null;
		private JPanel jp = null;

		public ClusterActionListener(Observable o, JPanel jp){
			this.o = o;			
			this.jp = jp;
		}
		@Override
		public void actionPerformed(ActionEvent e){		
			if(o instanceof Model && jp instanceof ClusterPanel){			

				String[] nodes = null;
				double dThreshold = -1;

				if(seedTableModel.getRowCount()!= 0){
					nodes =  new String[ seedTableModel.getRowCount()];
					for(int i = 0 ; i < seedTableModel.getRowCount(); i++)
						nodes[i] = (String)seedTableModel.getValueAt(i, 0);
				}else{
					// 0 ... 100
					int iThreshold = Integer.parseInt(m_thresholdLabel.getText().replace("%", ""));

					if( iThreshold != 0)
						dThreshold = iThreshold/100.0;
				}
				// nodes == null -> use of threshold
				((Model)o).cluster(		
						nodes,
						dThreshold, 
						cBox_A.getSelectedItem().toString(),
						cBox_B.getSelectedItem().toString(),						 
						cBox_C.getSelectedItem().toString(), 
						cBox_D.getSelectedItem().toString()
				);
			}
		}	
	}

	class SeedTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;
		private Vector<String>  data = null;

		public SeedTableModel(){
			data = new Vector<String>();	
		}
		public void addObject(String o){
			if(!data.contains(o)){
				data.add(o);
				Collections.sort(data);
				fireTableDataChanged();
			}
		}
		public void deleteRows(int[] ii){
			String flag = "";  // empty string to avoid: node label = flag  
			for(int i = 0 ; i < ii.length ; i++)
				data.set(ii[i],flag);	
			while(data.remove(flag));
			fireTableDataChanged();
		}
		public void deleteRows(){
			data.removeAllElements();
			fireTableDataChanged();
		}
		@Override
		public String getColumnName(int col) { 
			return " "; 
		}		
		@Override
		public int getColumnCount() {
			return 1;
		}
		@Override
		public int getRowCount() {
			return data.size();
		}
		@Override
		public Object getValueAt(int arg0, int arg1) {			
			return data.get(arg0);
		}
	}
}