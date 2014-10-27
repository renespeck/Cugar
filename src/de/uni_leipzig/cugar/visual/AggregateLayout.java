package de.uni_leipzig.cugar.visual;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import prefuse.action.layout.Layout;
import prefuse.util.GraphicsLib;
import prefuse.visual.AggregateItem;
import prefuse.visual.AggregateTable;
import prefuse.visual.VisualItem;
/**
 * Layout algorithm that computes a convex hull surrounding
 * aggregate items and saves it in the "_polygon" field.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author rspeck 
 *  
 */
public class AggregateLayout extends Layout {	

	private int m_margin = 5; // convex hull pixel margin
	private double[] m_pts;   // buffer for computing convex hulls
	/** 
	 * Flag that indicates the state of {@link #run(double)} member method. 
	 * This flag is set to false in the last line of this method, 
	 * to be sure the {@link prefuse.action.Action} is canceled.
	 * The flag is set to true in first line. 
	 */
	public boolean runs = false;  
	public AggregateLayout(String aggrGroup) {
		super(aggrGroup);
	}
    /**
     * Computes convex hull if aggregates are visible.
     * 
     * @see prefuse.action.Action#run(double)
     */
	@Override
	public void run(double frac) {
		runs = true;
		AggregateTable aggr = (AggregateTable)m_vis.getGroup(m_group);
		// do we have any  to process?
		int num = aggr.getTupleCount();
		if ( num == 0 ){
			runs = false;
			return;				
		}				        
		// update buffers
		int maxsz = 0;
		for ( Iterator<AggregateItem> aggrs = aggr.tuples(); aggrs.hasNext();  ){
			maxsz = Math.max(maxsz, 4*2* (aggrs.next()).getAggregateSize());     
		}
		//System.out.println(maxsz);
		if ( m_pts == null || maxsz > m_pts.length ) 
			m_pts = new double[maxsz];            
		// compute and assign convex hull for each aggregate
		Iterator<AggregateItem> aggrs = m_vis.items(m_group);        
		while ( aggrs.hasNext()) {  			
			AggregateItem aitem = aggrs.next();				
			if ( aitem.getAggregateSize() == 0 ){
				continue;
			}
			int idx = 0;
			VisualItem vaitem = null;
			Iterator<VisualItem> aitem_iter = aitem.items();
			while ( aitem_iter.hasNext() ) {
				vaitem = aitem_iter.next();
				if ( vaitem.isVisible() ) {
					addPoint(m_pts, idx, vaitem, m_margin);
					idx += 2*4;
				}
			}				
			// if aggregates are visible
			if ( idx != 0 ){             
				// compute convex hull
				double[] nhull = GraphicsLib.convexHull(m_pts, idx);            
				// prepare viz attribute array
				float[]  fhull = (float[])aitem.get(VisualItem.POLYGON);
				if ( fhull == null || fhull.length < nhull.length )
					fhull = new float[nhull.length];
				else if ( fhull.length > nhull.length )
					fhull[nhull.length] = Float.NaN;            
				// copy hull values
				for ( int j = 0; j < nhull.length; j++ )
					fhull[j] = (float)nhull[j];
				aitem.set(VisualItem.POLYGON, fhull);
				aitem.setValidated(false); // force invalidation	            
				aitem.setVisible(true);
			}
			else 
				aitem.setVisible(false);  	                  	
		}
		runs = false;
	}
	private static void addPoint(double[] pts, int idx, VisualItem item, int growth){
		Rectangle2D b = item.getBounds();
		double minX = (b.getMinX())-growth, minY = (b.getMinY())-growth;
		double maxX = (b.getMaxX())+growth, maxY = (b.getMaxY())+growth;
		pts[idx]   = minX; pts[idx+1] = minY;
		pts[idx+2] = minX; pts[idx+3] = maxY;
		pts[idx+4] = maxX; pts[idx+5] = minY;
		pts[idx+6] = maxX; pts[idx+7] = maxY;
	}
}