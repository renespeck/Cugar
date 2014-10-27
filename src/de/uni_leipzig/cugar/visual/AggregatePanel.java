package de.uni_leipzig.cugar.visual;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.filter.GraphDistanceFilter;
import prefuse.action.layout.Layout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.ControlAdapter;
import prefuse.controls.HoverActionControl;
import prefuse.controls.NeighborHighlightControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Graph;
import prefuse.data.Schema;
import prefuse.data.Table;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.query.SearchQueryBinding;
import prefuse.data.search.KeywordSearchTupleSet;
import prefuse.data.search.SearchTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.render.PolygonRenderer;
import prefuse.render.Renderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.PrefuseLib;
import prefuse.util.ui.JFastLabel;
import prefuse.util.ui.JForcePanel;
import prefuse.util.ui.JSearchPanel;
import prefuse.util.ui.JValueSlider;
import prefuse.visual.AggregateItem;
import prefuse.visual.AggregateTable;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.HoverPredicate;
import prefuse.visual.expression.InGroupPredicate;

import de.uni_leipzig.cugar.data.Model;
import static de.uni_leipzig.cugar.cluster.ClusterTableSettings.CLUSTER_COLUMN_NAME;
import static de.uni_leipzig.cugar.data.GraphReaderSettings.NODE_NAME;
/**
 * <p>Is a JPanel class and uses a {@link prefuse.Visualization} instance {@link #m_vis} and 
 * a {@link prefuse.Display} instance {@link #m_display}. It shows a visualization with the help of a 
 * {@link prefuse.action.layout.graph.ForceDirectedLayout} instance {@link #m_forceLayout} and 
 * a {@link AggregateLayout} instance {@link #m_aggregateLayout}.
 * Builds a {@link  prefuse.util.ui.JForcePanel} gui for force configurations 
 * and a {@link prefuse.Display} overview instance with the {@link #getForceTab()} method.</p>
 * <p>
 * The {@link #addGraph(Graph)} method adds a new {@link prefuse.data.Graph} instance and updates all necessary data.
 * The {@link #addAggregate(Table)} method adds cluster data to a {@link prefuse.visual.AggregateTable} instance {@link #m_aTable}, 
 * the {@link prefuse.data.Table} parameter instance should have a 
 * column with {@link  de.uni_leipzig.cugar.cluster.ClusterTableSettings#CLUSTER_COLUMN_NAME} header field and 
 * {@link  de.uni_leipzig.cugar.cluster.ClusterTableSettings#CLUSTER_COLUMN_NAME_TYPE} data type, which contains 
 * the cluster data.
 * </p> 
 * <p>The parameters of {@link #update(Observable, Object)} method are a {@link de.uni_leipzig.cugar.data.Model} instance as 
 * Observable object and arguments as String objects from the {@link de.uni_leipzig.cugar.data.Model} instance to update used data.
 *   
 * @author rspeck
 */
public class AggregatePanel extends JPanel implements Observer{
	private static final long serialVersionUID = 1L;
	public final String INIT_ID = "AggregatePanel";
	/** {@link prefuse.Visualization} instance */
	protected Visualization m_vis = null;
	/** {@link prefuse.Display} instance */
	protected Display m_display = null;
	/** Action group names for {@link prefuse.Visualization}. */
	protected static enum Actions {
		DRAW, LAYOUT, AGGR      // DRAW and LAYOUT are paired
	}
	/** prefuse default id */
	public static final String GRAPH = "graph";
	/** prefuse default id */
	public static final String NODES = "graph.nodes";
	/** prefuse default id */
	public static final String EDGES = "graph.edges";
	/** prefuse default id */
	public static final String AGGRS = "aggregates";	
	/** decorator id */
	public static final String AGGR_DECORATORS = "aggrDeco";
	/** decorator Schema */
	private static final Schema DECORATOR_SCHEMA = PrefuseLib.getVisualItemSchema(); 
	/** decorator default Schema */
	static { 
		DECORATOR_SCHEMA.setDefault(VisualItem.INTERACTIVE, false); 		
		DECORATOR_SCHEMA.setDefault(VisualItem.TEXTCOLOR,ColorLib.gray(255));		
		DECORATOR_SCHEMA.setDefault(VisualItem.FONT, FontLib.getFont("Tahoma", Font.BOLD, 48));
	}
	/** visual aggr id */
	protected static final String AGGRS_ID = "id";	
	/** AggregateTable instance with aggregates data */
	private AggregateTable m_aTable;
	/** Layout for aggregates */
	protected AggregateLayout m_aggregateLayout = null;
	/** default filter values */
	protected int MAX_HOPS = 30;
	/** default filter values */
	protected int DEFAULT_HOPS = 2;  
	// change color
	private DataColorAction m_aggrs_color;
	protected Color BACKGROUND = Color.WHITE;
	private Layout m_forceLayout = null;
	// search gui elements
	private JFastLabel m_searchLabel = new JFastLabel(" ");
	private Box m_searchBox = null;
	// clicked node
	private JLabel seedLabel = null;
	// filter
	private GraphDistanceFilter distanceFilter = null;
	private JValueSlider distanceSlider = null;	
	JSearchPanel search = null;
	/**
	 * Constructs a new AggregatePanel.
	 * Initializes the  {@link prefuse.Visualization}, {@link prefuse.Display}, {@link prefuse.visual.AggregateTable} 
	 * and  {@link  AggregateLayout}.
	 * Calls the  {@link #init()} method.
	 */
	public AggregatePanel(int width,int height){ 	
		// init Visualization
		m_vis = new Visualization();		
		m_aTable = m_vis.addAggregates(AGGRS);
		m_aTable.addColumn(VisualItem.POLYGON, float[].class);
		m_aTable.addColumn(AGGRS_ID, int.class);
		// init layout
		m_aggregateLayout = new AggregateLayout(AGGRS);
		// init Display		
		m_display = new Display(m_vis);		 		
		m_display.setBackground(BACKGROUND);		
		m_display.setSize(width,height);
		m_display.pan(width/2, height/2);	
		m_display.resetKeyboardActions(); // remove 
		// init this JPanel
		setLayout(new BorderLayout());
		setBackground(BACKGROUND);
		add(m_display);		
		m_searchBox = new Box(BoxLayout.X_AXIS);  
		add(m_searchBox, BorderLayout.SOUTH);
		// init 
		init();
		setVisible(true);
	}
	/**
	 * This is a template method and used by constructors to initializes some data.
	 * The protected methods {@link #setRendererFactory()}, {@link #setActions()}, {@link #setListeners()} 
	 * are called in this order, feel free to override these. 
	 */
	protected final void init(){
		setRendererFactory();
		setActions();	
		setListeners();	
	}
	/** Sets renderers for {@link #NODES},{@link #EDGES}, {@link #AGGRS} and {@link #AGGR_DECORATORS} group. */
	protected void setRendererFactory(){
		// draw aggregates as polygons with curved edges
		Renderer pr = new PolygonRenderer(Constants.POLY_TYPE_CURVE);
		((PolygonRenderer)pr).setCurveSlack(0.15f);  
		// draw node labels
		Renderer nlr = new LabelRenderer(NODE_NAME);
		((LabelRenderer)nlr).setRoundedCorner(10,10);				
		// set renderers to factory
		// nodes and egeds
		DefaultRendererFactory drf = new DefaultRendererFactory();
		drf.add(new InGroupPredicate(NODES),nlr);
		drf.add(new InGroupPredicate(AGGRS), pr);
		// edge direction 
		drf.add(new InGroupPredicate(EDGES), new EdgeRenderer(Constants.EDGE_TYPE_LINE,Constants.EDGE_ARROW_FORWARD));
		// decorators
		drf.add(new InGroupPredicate(AGGR_DECORATORS), new LabelRenderer(AGGRS_ID));
		m_vis.setRendererFactory(drf);
	}
	/**
	 * Initializes colors, force, ... Actions.
	 */
	protected void setActions(){
		// DRAW_ACTIONS: node and edge color actions
		ColorAction nodeStroke = new ColorAction(NODES, VisualItem.STROKECOLOR);
		nodeStroke.setDefaultColor(ColorLib.gray(0)); 
		nodeStroke.add(VisualItem.HOVER, ColorLib.gray(0));			
		ColorAction edgeStroke = new ColorAction(EDGES, VisualItem.STROKECOLOR);
		edgeStroke.setDefaultColor(ColorLib.gray(0));
		ColorAction edgeFill = new ColorAction(EDGES, VisualItem.FILLCOLOR);
		edgeFill.setDefaultColor(ColorLib.gray(0));		
//		DataSizeAction edgeWidth = new DataSizeAction(EDGES, EDGE_WEIGHT);
		// bundle the node and edge color actions
		ActionList nodeEdgeList = new ActionList();
		nodeEdgeList.add(new NodeColorAction(NODES));
		nodeEdgeList.add(new ColorAction(NODES, VisualItem.TEXTCOLOR, ColorLib.gray(0)));
		nodeEdgeList.add(nodeStroke);
		nodeEdgeList.add(edgeStroke);		
		nodeEdgeList.add(edgeFill);
		//nodeEdgeList.add(edgeWidth); //paint edges width depends on weight
		nodeEdgeList.add(new RepaintAction());
		// we register our actions
		m_vis.putAction(Actions.DRAW.name(), nodeEdgeList);
		// now create the main layout routine
		ActionList layoutList = new ActionList(Activity.INFINITY);	
//		ActionList layoutList = new ActionList(Activity.DEFAULT_STEP_TIME);	
		distanceFilter = new GraphDistanceFilter(GRAPH, MAX_HOPS);
		layoutList.add(distanceFilter);			
		m_forceLayout = new ForceDirectedLayout(GRAPH, false);
//		m_forceLayout = new FruchtermanReingoldLayout(GRAPH);
		layoutList.add(m_forceLayout);  
		layoutList.add(new RepaintAction());
		// we register	
		m_vis.putAction(Actions.LAYOUT.name(), layoutList);
		m_vis.runAfter(Actions.DRAW.name(), Actions.LAYOUT.name());	
		// Actions aggregate color and layout
		ActionList aggrList = new ActionList(Activity.INFINITY);	
		aggrList.add(m_aggregateLayout);
		aggrList.add(new LabelLayout(AGGR_DECORATORS));	
		ColorAction aggrStroke = new ColorAction(AGGRS, VisualItem.STROKECOLOR);
		aggrStroke.setDefaultColor(ColorLib.gray(0));
		aggrStroke.add(VisualItem.HOVER, ColorLib.rgb(255,100,100));    

		aggrList.add(aggrStroke);
		m_aggrs_color = new DataColorAction(AGGRS, AGGRS_ID, Constants.ORDINAL, VisualItem.FILLCOLOR);

		aggrList.add(m_aggrs_color);
		aggrList.add(new RepaintAction());
		// we register	
		m_vis.putAction(Actions.AGGR.name(), aggrList);	
	}
	/** Adds {@link prefuse.controls.ControlAdapter} instances to {@link #m_display} */
	protected void setListeners(){
		// listeners for display
		// search box title
		m_searchLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		m_searchLabel.setBorder(BorderFactory.createEmptyBorder(3,0,0,0));
		m_searchLabel.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 16));
		m_searchLabel.setBackground(BACKGROUND);

		m_display.addControlListener(new ControlAdapter() {

			@Override
			public void itemClicked(VisualItem item, MouseEvent e){
				if ( item.canGetString(NODE_NAME) ){
					String selected = item.getString(NODE_NAME);
					if(seedLabel != null)
						seedLabel.setText(selected);
				}
			}

			@Override
			public void itemEntered(VisualItem item, MouseEvent e) {
				if ( item.canGetString(NODE_NAME) ){
					String selected = item.getString(NODE_NAME);
					m_searchLabel.setText(selected);
				}				
			}

			@Override
			public void itemExited(VisualItem item, MouseEvent e) {
				m_searchLabel.setText(null);
			}
		});
		m_display.addControlListener(new AggregateDragControl(AGGRS));
		m_display.addControlListener(new PanControl());
		m_display.addControlListener(new ZoomControl());
		m_display.addControlListener(new WheelZoomControl());
		m_display.addControlListener(new ZoomToFitControl());
		m_display.addControlListener(new NeighborHighlightControl());
		m_display.addControlListener(new HoverActionControl(Actions.DRAW.name()));			
		//m_display.addControlListener(new ToolTipControl(EDGE_WEIGHT)); 
	}	
	/**
	 * Adds a Graph, refreshes search items and removes aggregates.
	 * 
	 * @param graph
	 */
	protected void addGraph(Graph graph){
		if(graph == null) return;		
		// stop visualization
		m_vis.cancel(Actions.DRAW.name()).setEnabled(false);		
		m_vis.cancel(Actions.AGGR.name()).setEnabled(false);	
		// wait till layout calculations finished.
		while(m_aggregateLayout.runs); 
		// remove all rows 
		m_aTable.clear(); 
		// remove old groups
		m_vis.removeGroup(AGGR_DECORATORS);
		m_vis.removeGroup(Visualization.SEARCH_ITEMS);
		m_vis.removeGroup(GRAPH);
		// add new graph
		VisualGraph vg = m_vis.addGraph(GRAPH, graph);
		// set focus group
		VisualItem f = (VisualItem)vg.getNode(0); 
		m_vis.getGroup(Visualization.FOCUS_ITEMS).setTuple(f);
		//f.setFixed(true);		
		m_vis.setValue(EDGES, null, VisualItem.INTERACTIVE, Boolean.FALSE);			
		// refresh search group	
		SearchTupleSet searchTup = new KeywordSearchTupleSet();	
		m_vis.addFocusGroup(Visualization.SEARCH_ITEMS, searchTup);

		searchTup.addTupleSetListener(new TupleSetListener() {
			@Override
			public void tupleSetChanged(TupleSet t, Tuple[] add, Tuple[] rem) {			
				m_vis.run(Actions.DRAW.name() );							
			}
		});		
		//
		SearchQueryBinding sq = new SearchQueryBinding(	vg.getNodeTable(), NODE_NAME,
				(SearchTupleSet)m_vis.getGroup(Visualization.SEARCH_ITEMS));		
		// refresh search gui
		search = sq.createSearchPanel();
		search.setShowResultCount(true);
		search.setBorder(BorderFactory.createEmptyBorder(5,5,4,0));
		search.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 11));  
		
		// refresh box
		m_searchBox.removeAll();		
		m_searchBox.add(Box.createHorizontalStrut(10));
		m_searchBox.add(m_searchLabel);
		m_searchBox.add(Box.createHorizontalGlue());
		m_searchBox.add(search);
		m_searchBox.add(Box.createHorizontalStrut(3));	
		// run		
		m_vis.run(Actions.DRAW.name()).setEnabled(true);
		m_vis.run(Actions.DRAW.name());
		// 
		revalidate();
		repaint();
	}
	/**
	 * Adds the sorted data of column {@link #CLUSTER_COLUMN_NAME} from clusterTable parameter to 
	 * the {@link prefuse.visual.AggregateTable} instance {@link #m_aTable},
	 * sets a Decorator with the {@link #setDecorators()} method.
	 * 
	 * @param clusterTable
	 */
	public void addAggregate(Table clusterTable ){				
		// cancel AggregateLayout instance		
		if(m_vis.getAction(Actions.AGGR.name()).isEnabled())			
			m_vis.cancel(Actions.AGGR.name()).setEnabled(false);		
		// wait till layout calculations finished.
		while(m_aggregateLayout.runs); 
		// remove all rows 
		m_aTable.clear(); 		
		// add aggregates to visualization
		if(clusterTable == null) return;
		VisualGraph vg = (VisualGraph) m_vis.getGroup(GRAPH);
		// for all clusters		
		for ( int row = 0; row < clusterTable.getRowCount(); row++ ) {
			AggregateItem aitem = (AggregateItem)m_aTable.addItem();
			aitem.setInt(AGGRS_ID, row);			
			TreeSet<String> cluster = (TreeSet<String>)clusterTable.get(row, CLUSTER_COLUMN_NAME);			
			Iterator<VisualItem> nodes = vg.nodes();			
			// for all nodes
			while(nodes.hasNext()){          	
				VisualItem vi = nodes.next();            
				String name = vi.getString(NODE_NAME);
				// for all nodes in cluster	
				if(cluster.contains(name)){					
					aitem.addItem(vi);					
				}
			}			
		} 
		// refresh aggregate settings
		m_vis.setValue(AGGRS, null, VisualItem.INTERACTIVE, Boolean.TRUE);
		setDecorators();	
		// restart action
		if(!m_vis.getAction(Actions.AGGR.name() ).isEnabled())
			m_vis.run(Actions.AGGR.name()).setEnabled(true);
	}
	/**
	 * Refreshes {@link #AGGR_DECORATORS} for every new {@link #AGGRS} 
	 */
	protected void setDecorators(){
		// for aggregates	
		if( m_vis.getGroup(AGGRS) != null  ){	
			m_vis.removeGroup(AGGR_DECORATORS);
			m_vis.addDecorators(AGGR_DECORATORS, AGGRS, new HoverPredicate(), DECORATOR_SCHEMA);
		}
	}	

	/**
	 *  This method updates this Observer by arguments and data from {@link  de.uni_leipzig.cugar.data.Model} instance.
	 */
	@Override
	public void update(Observable model, Object arg) {
		//
		if(model instanceof Model){
			if(arg != null && arg.equals(INIT_ID)){			
				// change distance filter
				boolean on = ((Model)model).getDistanceFilter();
				distanceFilter.setEnabled(on);		
				if(!on)
					m_vis.setVisible(Visualization.ALL_ITEMS, null, true);
				// change quality
				m_display.setHighQuality(((Model)model).getQuality());			
				// change color
				// we have aggregates
				//if(((Table)m_vis.getGroup(AGGRS)).getRowCount() > 0){
				// set color
				if(((Model)model).getColor())
					m_aggrs_color.setDataType(Constants.NOMINAL);
				else
					m_aggrs_color.setDataType(Constants.NUMERICAL);		
				//	}
				if(m_vis.getGroup(NODES)!= null){
					m_vis.run(Actions.DRAW.name());
				}
			}
			//
			if(arg != null && arg.equals(Model.OPENFILE)){
				// refresh filter
				if(((Model)model).getDistanceFilter() == false)
					((Model)model).changeDistanceFilter();
				distanceFilter.setEnabled(true);
				distanceFilter.setDistance(DEFAULT_HOPS);		
				distanceSlider.setValue(DEFAULT_HOPS);				
				// add graph
				addGraph(((Model)model).getGraph());				
			}		
			if(arg != null && arg.equals(Model.CLUSTER)){
				addAggregate(((Model)model).getClusterTable());				
			}
			//
			if(arg != null && arg.equals(Model.PAUSE)){
				if(((Model)model).getPause())
					m_vis.cancel(Actions.LAYOUT.name());
				else
					m_vis.run(Actions.LAYOUT.name());			
			}
			//
			if(arg != null && arg.equals(Model.NODE_TABLE_EVENT)){
				VisualGraph vg = (VisualGraph)m_vis.getGroup(GRAPH);
				Iterator<VisualItem> nodes = vg.nodes();
				while(nodes.hasNext()){
					VisualItem vi = nodes.next();				
					String name = vi.getString(NODE_NAME);

					if(name.equals(((Model)model).getClickedTableNode()))	{	
						TupleSet ts = m_vis.getGroup(Visualization.FOCUS_ITEMS);
						Iterator<VisualItem> iter  = ts.tuples();
						while(iter.hasNext())
							iter.next().setFixed(false); // focus item 
						m_vis.getGroup(Visualization.FOCUS_ITEMS).setTuple(vi);		
						m_vis.run(Actions.DRAW.name());
					}
				}		
			}
			// search cluster , cause was clicked in cluster summary tab
			if(arg != null && arg.equals(Model.CLUSTER_TABLE_EVENT)){
				String query = ((Model)model).getClickedTableCluster().toString();
				query = query.substring(1, query.length() - 1);
				
				VisualGraph vg = (VisualGraph)m_vis.getGroup(GRAPH);
				Iterator<VisualItem> nodes = vg.nodes();
				Iterator<VisualItem> iter = m_vis.getGroup(Visualization.FOCUS_ITEMS).tuples();
				while(iter.hasNext())
					iter.next().setFixed(false); // focus item 
				boolean first_iteration = true;
				while(nodes.hasNext()){
					VisualItem vi = nodes.next();			
					String[] split = query.split(",");
					String name = vi.getString(NODE_NAME);					
					for(String node : split){
						if(name.equals(node.trim())){	
							if(first_iteration){
								first_iteration = false;
								m_vis.getGroup(Visualization.FOCUS_ITEMS).setTuple(vi);	
							}else
								m_vis.getGroup(Visualization.FOCUS_ITEMS).addTuple(vi);								
						}						
					}
					m_vis.run(Actions.DRAW.name());				}	
			}
		}	
	}
	/**
	 *  This method is used to set a JLabel instance for clicked nodes.
	 */
	public void setSeedLabel(JLabel seedLabel){
		this.seedLabel = seedLabel;
	}	
	/**
	 * Gets the Display to export an image in File.
	 */
	public Display getDisplay(){
		return m_display;
	}
	/**
	 * 
	 * Builds a JPanel with elements for force settings, an overview and a distance filter.
	 * 
	 * @return The force JPanel.
	 */
	public JPanel getForceTab(){	
		distanceSlider = new JValueSlider("Distance", 0, MAX_HOPS, DEFAULT_HOPS );
		distanceSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				distanceFilter.setDistance(distanceSlider.getValue().intValue());  
				if(m_vis.getGroup(NODES)!= null){
					m_vis.run(Actions.DRAW.name());	// node and edge color
				}					
			}
		});
		// set slider default value
		distanceFilter.setDistance(DEFAULT_HOPS);		
		distanceSlider.setBackground(BACKGROUND);
		distanceSlider.setPreferredSize(new Dimension(300,30));
		distanceSlider.setMaximumSize(new Dimension(300,30));
		// slider to box
		Box sBox = new Box(BoxLayout.Y_AXIS);
		sBox.setBorder(BorderFactory.createTitledBorder("Connectivity Filter"));
		sBox.add(distanceSlider);		

		Display overview = new Display(m_vis);    	
		overview.setSize(290,290);		
		overview.addItemBoundsListener(new FitOverviewListener()); 		
		// overview 
		JPanel opanel = new JPanel();
		opanel.setBackground(BACKGROUND);
		opanel.setPreferredSize(new Dimension(300,300));
		opanel.setMaximumSize(new Dimension(300,300));
		opanel.setMinimumSize(new Dimension(300,300));		
		opanel.add(overview);
		//opanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));			
		Box oBox = new Box(BoxLayout.Y_AXIS);		
		oBox.setBorder(BorderFactory.createTitledBorder("Overview"));
		oBox.add(opanel);
		JPanel forcePanel = null;
		
		if(m_forceLayout instanceof ForceDirectedLayout)
			forcePanel = new JForcePanel(((ForceDirectedLayout)m_forceLayout).getForceSimulator());
		if(forcePanel==null)
			forcePanel = new JPanel();
		// add overview	and slider	
		forcePanel.add(sBox); 
		forcePanel.add(oBox);
		return forcePanel;
	}
}