package de.uni_leipzig.cugar.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import prefuse.data.Table;
import prefuse.data.util.Sort;
import de.uni_leipzig.cugar.cluster.Eval;
import de.uni_leipzig.cugar.data.Model;
/**
 * It's an Observer JPanel to show graph statistics.
 * 
 * @author rspeck
 *  
 */  
public class GraphStatisticPanel extends JPanel implements Observer {

	public final String INIT_ID = "GraphStatisticPanel";
	private static final long serialVersionUID = 1L;
	private JScrollPane nodesScrollPane = null;

	private JTable nodeTable = null;
	private JTable edgeTable = null;

	private JTabbedPane jTabbedPane = null;
	private JScrollPane clusterScrollPane = null;
	private JScrollPane edgesScrollPane = null;
	private JTable clusterTable = null;
	private JPanel jPanel = null;
	private JLabel nodesLabel = null;
	private JTextField nodeField = null;
	private JPanel numberOfPanel = null;
	private JLabel edgesLabel = null;
	private JTextField edgeField = null;
	private JLabel clustersLabel = null;
	private JTextField clusterField = null;
	//
	private JPanel measurePanel = null;
	private JLabel nCutLabel = null;
	private JTextField nCutField = null;
	private JLabel rFlowLabel = null;
	private JTextField rFlowField = null;
	private JLabel hardenLabel = null;	
	private JTextField hardenField = null;
	private JLabel silhouetteLabel = null;
	private JTextField silhouetteField = null;	
	private JButton measureButton = null;
	//
	private JPanel clusterPropertiesPanel = null;
	private JTextField clusterSizeField = null;
	private JLabel clusterSizeLabel = null;
	private JTextField nodesInClusterField = null;
	private JLabel nodesInClusterLabel = null;
	/**
	 * This is the default constructor
	 */
	public GraphStatisticPanel() {
		super();
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(305, 200);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setPreferredSize(new Dimension(305, 200));
		this.setBackground(Color.white);		
		this.add(getJTabbedPane(), null);
	}
	/**
	 * This method initializes nodesScrollPane
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getNodesScrollPane() {
		if (nodesScrollPane == null) {
			nodesScrollPane = new JScrollPane();
			nodesScrollPane.setBackground(Color.white);
			nodesScrollPane.setViewportView(getNodeTable());
		}
		return nodesScrollPane;
	}
	/**
	 * This method initializes nodeTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getNodeTable() {
		if (nodeTable == null) {
			nodeTable = new JTable();			
		}
		return nodeTable;
	}
	/**
	 * This method initializes jScrollPane
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getEdgesScrollPane() {
		if (edgesScrollPane == null) {
			edgesScrollPane = new JScrollPane();
			edgesScrollPane.setBackground(Color.white);
			edgesScrollPane.setViewportView(getEdgeTable());
		}
		return edgesScrollPane;
	}
	/**
	 * This method initializes edgeTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getEdgeTable() {
		if (edgeTable == null) {
			edgeTable = new JTable();			
			edgeTable.setAutoCreateRowSorter(true);			
		}
		return edgeTable;
	}	
	/**
	 * This method initializes jTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.addTab("nodes", getNodesScrollPane());
			jTabbedPane.addTab("edges", getEdgesScrollPane());
			jTabbedPane.addTab("cluster",getClusterScrollPane());
			jTabbedPane.addTab("summary", getJPanel());
			jTabbedPane.setBackground(Color.white);
		}
		return jTabbedPane;
	}
	/**
	 * This method initializes clusterScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getClusterScrollPane() {
		if (clusterScrollPane == null) {
			clusterScrollPane = new JScrollPane();
			clusterScrollPane.setViewportView(getClusterTable());
		}
		return clusterScrollPane;
	}

	/**
	 * This method initializes clusterTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getClusterTable() {
		if (clusterTable == null) {
			clusterTable = new JTable();			
			//clusterTable.setAutoCreateRowSorter(true);				
		}
		return clusterTable;
	}
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {			
			jPanel = new JPanel();			
			jPanel.setBackground(Color.white);
			jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
			jPanel.add(getNumberOfPanel());
			jPanel.add(getClusterPropertiesPanel());
			jPanel.add(getMeasurePanel());
			jPanel.add(getButtonPanel());
		}
		return jPanel;
	}
	/**
	 * This method initializes numberOfPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getNumberOfPanel() {
		if (numberOfPanel == null) {
			numberOfPanel = new JPanel();
			numberOfPanel.setMaximumSize(new Dimension(325,120));
			numberOfPanel.setPreferredSize(numberOfPanel.getMaximumSize());
			numberOfPanel.setMinimumSize(numberOfPanel.getMaximumSize());
			numberOfPanel.setSize(numberOfPanel.getMaximumSize());
			numberOfPanel.setBorder(BorderFactory.createTitledBorder("Number of"));
			numberOfPanel.setBackground(Color.white);
			numberOfPanel.setLayout(new GridLayout(3,2));
			numberOfPanel.add(getNodesLabel());			
			numberOfPanel.add(getNodeField());			
			numberOfPanel.add(getEdgesLabel());
			numberOfPanel.add(getEdgeField());	
			numberOfPanel.add(getClustersLabel());
			numberOfPanel.add(getClusterField());

		}
		return numberOfPanel;
	}	
	/**
	 * This method initializes clusterPropertiesPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getClusterPropertiesPanel() {
		if (clusterPropertiesPanel == null) {
			clusterPropertiesPanel = new JPanel();
			clusterPropertiesPanel.setMaximumSize(new Dimension(325,95));
			clusterPropertiesPanel.setPreferredSize(clusterPropertiesPanel.getMaximumSize());
			clusterPropertiesPanel.setMinimumSize(clusterPropertiesPanel.getMaximumSize());
			clusterPropertiesPanel.setSize(clusterPropertiesPanel.getMaximumSize());
			clusterPropertiesPanel.setBorder(BorderFactory.createTitledBorder("Clustering results"));
			clusterPropertiesPanel.setBackground(Color.white);
			clusterPropertiesPanel.setLayout(new GridLayout(2,2));

			clusterPropertiesPanel.add(getHardenLabel());
			clusterPropertiesPanel.add(getHardenField());

			clusterPropertiesPanel.add(getNodesInClusterLabel());
			clusterPropertiesPanel.add(getNodesInClusterField());

			//clusterPropertiesPanel.add(getClusterSizeLabel());
			//clusterPropertiesPanel.add(getClusterSizeField());
		}
		return clusterPropertiesPanel;
	}
	/**
	 * This method initializes measurePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMeasurePanel() {
		if (measurePanel == null) {
			measurePanel = new JPanel();
			measurePanel.setMaximumSize(new Dimension(325,120));
			measurePanel.setPreferredSize(measurePanel.getMaximumSize());
			measurePanel.setMinimumSize(measurePanel.getMaximumSize());
			measurePanel.setSize(measurePanel.getMaximumSize());
			measurePanel.setBorder(BorderFactory.createTitledBorder("Measures"));
			measurePanel.setBackground(Color.white);
			measurePanel.setLayout(new GridLayout(3,2));
			measurePanel.add(getSilhouetteLabel());
			measurePanel.add(getSilhouetteField());
			measurePanel.add(getRFlowLabel());
			measurePanel.add(getRFlowField());
			measurePanel.add(getNCutLabel());
			measurePanel.add(getNCutField());		
		}
		return measurePanel;
	}

//	/**
//	 * This method initializes clusterSizeField	
//	 * 	
//	 * @return javax.swing.JTextField	
//	 */
//	private JTextField getClusterSizeField() {
//		if(clusterSizeField == null){
//			clusterSizeField = new JTextField();	
//			clusterSizeField.setEditable(false);
//			clusterSizeField.setBackground(Color.white);
//		}		
//		return clusterSizeField;
//	}
//	/**
//	 * This method initializes clusterSizeLabel	
//	 * 	
//	 * @return javax.swing.JLabel	
//	 */
//	private JLabel getClusterSizeLabel() {
//		if(clusterSizeLabel == null){		
//			clusterSizeLabel = new JLabel();			
//			clusterSizeLabel.setText("Cluster size > 1");
//			clusterSizeLabel.setBackground(Color.white);	
//		}		
//		return clusterSizeLabel;
//	}
	/**
	 * This method initializes nodesInClusterField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getNodesInClusterField() {
		if(nodesInClusterField == null){
			nodesInClusterField = new JTextField();	
			nodesInClusterField.setEditable(false);
			nodesInClusterField.setBackground(Color.white);
		}		
		return nodesInClusterField;
	}
	/**
	 * This method initializes nodesInClusterLabel	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getNodesInClusterLabel() {
		if(nodesInClusterLabel == null){		
			nodesInClusterLabel = new JLabel();			
			nodesInClusterLabel.setText("All nodes in cluster");
			nodesInClusterLabel.setBackground(Color.white);	
		}		
		return nodesInClusterLabel;
	}
	/**
	 * This method initializes measureButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getMeasureButton() {
		if (measureButton == null) {
			measureButton = new JButton();
			measureButton.setMaximumSize(new Dimension(150,30));
			measureButton.setPreferredSize(measureButton.getMaximumSize());
			measureButton.setMinimumSize(measureButton.getMaximumSize());
			measureButton.setSize(measureButton.getMaximumSize());			
			measureButton.setText("Start");			
		}
		return measureButton;
	}
	private JPanel buttonPanel = null;
	/**
	 * This method initializes buttonPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtonPanel(){
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setMaximumSize(new Dimension(305,40));
			buttonPanel.setPreferredSize(buttonPanel.getMaximumSize());
			buttonPanel.setMinimumSize(buttonPanel.getMaximumSize());
			buttonPanel.setBackground(Color.white);
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));		
			buttonPanel.add(getMeasureButton());
		}
		return buttonPanel;
	}
	/**
	 * This method initializes nodesLabel
	 * 
	 * @return  javax.swing.JLabel
	 */
	private JLabel getNodesLabel(){
		if(nodesLabel ==null){
			nodesLabel = new JLabel();			
			nodesLabel.setText("Nodes");
			nodesLabel.setBackground(Color.white);			
		}
		return nodesLabel;
	}
	/**
	 * This method initializes nodeField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getNodeField() {
		if (nodeField == null) {
			nodeField = new JTextField();
			nodeField.setEditable(false);
			nodeField.setBackground(Color.white);
		}
		return nodeField;
	}
	/**
	 * This method initializes edgesLabel
	 * 
	 * @return  javax.swing.JLabel
	 */
	private JLabel getEdgesLabel(){
		if(edgesLabel ==null){
			edgesLabel = new JLabel();			
			edgesLabel.setText("Edges");
			edgesLabel.setBackground(Color.white);			
		}
		return edgesLabel;
	}
	/**
	 * This method initializes edgeField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getEdgeField() {
		if (edgeField == null) {
			edgeField = new JTextField();
			edgeField.setEditable(false);
			edgeField.setBackground(Color.white);
		}
		return edgeField;
	}
	/**
	 * This method initializes silhouetteLabel
	 * 
	 * @return  javax.swing.JLabel
	 */
	private JLabel getSilhouetteLabel(){
		if(silhouetteLabel ==null){
			silhouetteLabel = new JLabel();			
			silhouetteLabel.setText("Median Silhouette Width ");
			silhouetteLabel.setBackground(Color.white);			
		}
		return silhouetteLabel;
	}
	/**
	 * This method initializes hardenLabel
	 * 
	 * @return  javax.swing.JLabel
	 */
	private JLabel getHardenLabel(){
		if(hardenLabel ==null){
			hardenLabel = new JLabel();			
			hardenLabel.setText("Hard Cluster");
			hardenLabel.setBackground(Color.white);			
		}
		return hardenLabel;
	}
	/**
	 * This method initializes rFlowLabel
	 * 
	 * @return  javax.swing.JLabel
	 */
	private JLabel getRFlowLabel(){
		if(rFlowLabel ==null){
			rFlowLabel = new JLabel();			
			rFlowLabel.setText("Median Relative Flow");
			rFlowLabel.setBackground(Color.white);			
		}
		return rFlowLabel;
	}
	/**
	 * This method initializes nCutLabel
	 * 
	 * @return  javax.swing.JLabel
	 */
	private JLabel getNCutLabel(){
		if(nCutLabel ==null){
			nCutLabel = new JLabel();		
			nCutLabel.setText("Normalized Cut");
			nCutLabel.setBackground(Color.white);			
		}
		return nCutLabel;
	}
	/**
	 * This method initializes clustersLabel
	 * 
	 * @return  javax.swing.JLabel
	 */
	private JLabel getClustersLabel(){
		if(clustersLabel ==null){
			clustersLabel = new JLabel();			
			clustersLabel.setText("Clusters");
			clustersLabel.setBackground(Color.white);			
		}
		return clustersLabel;
	}
	/**
	 * This method initializes silhouetteField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getSilhouetteField() {
		if (silhouetteField == null) {
			silhouetteField = new JTextField();
			silhouetteField.setEditable(false);
			silhouetteField.setBackground(Color.white);
		}
		return silhouetteField;
	}
	/**
	 * This method initializes hardenField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getHardenField() {
		if (hardenField == null) {
			hardenField = new JTextField();
			hardenField.setEditable(false);
			hardenField.setBackground(Color.white);
		}
		return hardenField;
	}
	/**
	 * This method initializes rFlowField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getRFlowField() {
		if (rFlowField == null) {
			rFlowField = new JTextField();
			rFlowField.setEditable(false);
			rFlowField.setBackground(Color.white);
		}
		return rFlowField;
	}
	/**
	 * This method initializes nCutField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getNCutField() {
		if (nCutField == null) {
			nCutField = new JTextField();
			nCutField.setEditable(false);
			nCutField.setBackground(Color.white);
		}
		return nCutField;
	}
	/**
	 * This method initializes clusterField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getClusterField() {
		if (clusterField == null) {
			clusterField = new JTextField();
			clusterField.setEditable(false);
			clusterField.setBackground(Color.white);
		}
		return clusterField;
	}
	/**
	 * Updates this view.
	 */
	@Override
	public void update(Observable o, Object arg) {	

		if(o instanceof Model){			
			if(arg != null && arg.equals(Model.OPENFILE)){

				// add node table
				// sort node table
				Table t = ((Model)o).getGraph().getNodeTable();
				if(t!=null){
					t = t.select(null, 
							new Sort(
									new String[] {
											t.getColumnName(0)
									}
							)
					);

					nodeTable.setModel(new TableModel(t));	
					nodesScrollPane.validate();
					nodesScrollPane.repaint();	

					edgeTable.setModel(new TableModel(((Model)o).getEdgeListToTable()));
					edgesScrollPane.validate();
					edgesScrollPane.repaint();	
				}

				// add node edge count
				nodeField.setText(String.valueOf(((Model)o).getGraph().getNodeCount()));
				edgeField.setText(String.valueOf(((Model)o).getGraph().getEdgeCount()));

				// clear cluster
				clusterTable.setModel(new TableModel(((Model)o).getClusterTable()));
				clusterField.setText(String.valueOf(((Model)o).getClusterTable().getRowCount()));				
				// clear measures
				clearFields();

			}	
			if(arg != null && arg.equals(Model.CLUSTER) && ((Model)o).getClusterTable() != null){
				// add cluster
				clusterTable.setModel(new TableModel(((Model)o).getClusterTable()));
				clusterField.setText(String.valueOf(((Model)o).getClusterTable().getRowCount()));
				// clear
				clearFields();
			}
			if(arg != null && arg.equals(INIT_ID)){				
				nodeTable.addMouseListener( new NodeTableMouseListener((Model)o));	
				measureButton.addActionListener(new MeasureButtonAction(o));
				clusterTable.addMouseListener(new ClusterTableMouseListener((Model)o));
			}
		}
	}	
	private void clearFields(){
		nCutField.setText("");
		rFlowField.setText("");
		hardenField.setText("");
		silhouetteField.setText("");
		//clusterSizeField.setText("");
		nodesInClusterField.setText("");
	}
	/**
	 * ActionListener to perform measureButton actions.
	 */
	class MeasureButtonAction implements ActionListener{
		Observable m_o = null;
		public MeasureButtonAction(Observable o){
			m_o = o;			
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if(m_o instanceof Model)
				if(((Model)m_o).getClusterTable() != null && ((Model)m_o).getClusterTable().getRowCount() >0){				
					if(Eval.checkClusterSize(((Model)m_o).getClusterTable()))
						//clusterSizeField.setText("yes");
						nCutField.setText(String.valueOf(Math.round(Eval.ncut(((Model)m_o).getGraph(), ((Model)m_o).getClusterTable())*10000)/10000.0));
					else						
						nCutField.setText("not available");

					hardenField.setText(Eval.isHarden(((Model)m_o).getClusterTable())?"yes":"no");
					nodesInClusterField.setText(Eval.allNodesInCluster(((Model)m_o).getGraph(),((Model)m_o).getClusterTable())?"yes":"no");		

					double v = Math.round(Eval.getRelativeFlow(((Model)m_o).getGraph(), ((Model)m_o).getClusterTable())*10000)/10000.0;
					if(v >=  Integer.MAX_VALUE)
						rFlowField.setText("infinity");						
					else
						rFlowField.setText(String.valueOf(v));	
					silhouetteField.setText(String.valueOf(Math.round(Eval.silhouetteMean(((Model)m_o).getGraph(), ((Model)m_o).getClusterTable())*10000)/10000.0));				
				}
		}
	}
	
	class ClusterTableMouseListener extends MouseAdapter {  

		Model m_m = null;

		public ClusterTableMouseListener(Model o){
			m_m = o;			
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			if(SwingUtilities.isLeftMouseButton(e)){						
				Point p = e.getPoint();
				int row = clusterTable.rowAtPoint(p);		
				Set cluster = (Set<Integer>)clusterTable.getModel().getValueAt(row, 0);
				ListSelectionModel model = clusterTable.getSelectionModel();
				model.setSelectionInterval(row, row );
				m_m.setClickedTableCluster(cluster);
			}
		}
	}
	/**
	 * MouseListener for nodeTable 
	 */
	class NodeTableMouseListener extends MouseAdapter {  

		Model m_m = null;

		public NodeTableMouseListener(Model o){
			m_m = o;			
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			if(SwingUtilities.isLeftMouseButton(e)){						
				Point p = e.getPoint();
				int row = nodeTable.rowAtPoint(p);		
				String nodeName = (String)nodeTable.getModel().getValueAt(row, 0);
				ListSelectionModel model = nodeTable.getSelectionModel();
				model.setSelectionInterval(row, row );
				m_m.setClickedTableNode(nodeName);
			}
		}
	}	
	/**
	 * A new TableModel to hold the data.
	 */
	class TableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;
		private String[] columnNames = null;
		private Object[][]  data = null;

		public TableModel(Table clusterTable){

			columnNames =  new String[clusterTable.getColumnCount()];
			data = new Object[clusterTable.getRowCount()][clusterTable.getColumnCount()];

			// headers
			for(int i = 0 ; i < clusterTable.getColumnCount(); i++)
				columnNames[i] = clusterTable.getColumnName(i);

			// content
			for(int i = 0 ; i < clusterTable.getRowCount(); i++){
				for(int ii = 0 ; ii < clusterTable.getColumnCount(); ii++){	    				
					data[i][ii] = clusterTable.get(i, ii);			
				}		
			}
		}	        
		@Override
		public int getColumnCount() {
			return columnNames.length;
		}
		@Override
		public int getRowCount() {
			return data.length;
		}
		@Override
		public String getColumnName(int col) { 			
			return columnNames[col]; 			
		}
		@Override
		public Object getValueAt(int row, int col) { 
			if(data.length > 0)
				return data[row][col]; 	
			else
				return -1;
		}
		@Override
		public Class<? extends Object> getColumnClass(int c) { 
			Class<? extends Object> o = null;
			try{
				o = getValueAt(0, c).getClass();
			}catch(Exception e ){}		
			if(o==null)
				return "".getClass();
			return o; 
		}		
	}
}