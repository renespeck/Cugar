package de.uni_leipzig.cugar.visual;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Iterator;

import javax.swing.SwingUtilities;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.controls.ControlAdapter;
import prefuse.visual.AggregateItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

/**
 * Interactive drag control that is "aggregate-aware"
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author rspeck
 * 
 */
class AggregateDragControl extends ControlAdapter {

	private VisualItem activeItem;
	private VisualItem focusItem;
	protected Point2D down = new Point2D.Double();
	protected Point2D temp = new Point2D.Double();
	protected boolean dragged;
	protected String m_group = "";

	/**
	 * Creates a new drag control that issues repaint requests as an item
	 * is dragged.
	 */
	public AggregateDragControl(String group) { 	
		m_group = group;		
	}
	/**
	 * @see prefuse.controls.Control#itemEntered(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
	 */
	@Override
	public void itemEntered(VisualItem item, MouseEvent e) {		

		if ( item instanceof NodeItem ){
			Display d = (Display)e.getSource();
			d.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));			
			return;			
		}

		if ( item instanceof AggregateItem ){
			Display d = (Display)e.getSource();
			d.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			activeItem = item;
		}		
	}

	/**
	 * @see prefuse.controls.Control#itemExited(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
	 */
	@Override
	public void itemExited(VisualItem item, MouseEvent e) {

		Display d = (Display)e.getSource();
		d.setCursor(Cursor.getDefaultCursor());

		if ( !(item instanceof AggregateItem))
			return;

		if ( item instanceof AggregateItem ){
			if ( activeItem == item ) {
				activeItem = null;
				setFixed(item, false);
			}
		}
	}
	/**
	 * @see prefuse.controls.Control#itemPressed(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
	 */
	@Override
	public void itemPressed(VisualItem item, MouseEvent e) {
		if (!SwingUtilities.isLeftMouseButton(e)) return;

		dragged = false;
		Display d = (Display)e.getComponent();
		d.getAbsoluteCoordinate(e.getPoint(), down);

		if ( item instanceof AggregateItem )				
			setFixed(item, true);	

		if ( item instanceof NodeItem ){
			if(!item.isFixed()){
				if(focusItem != null){	
					focusItem.setFixed(false);
				}
				focusItem = item;
				item.setFixed(true);
				d.getVisualization().getGroup(Visualization.FOCUS_ITEMS).setTuple(item);
				d.revalidate();
				d.repaint();
			}else
				item.setFixed(false);
		}			
	}

	/**
	 * @see prefuse.controls.Control#itemReleased(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
	 */
	@Override
	public void itemReleased(VisualItem item, MouseEvent e) {	
		if (!SwingUtilities.isLeftMouseButton(e)) return;

		if ( item instanceof NodeItem )
			return;

		if ( item instanceof AggregateItem )
			if ( dragged ) {
				activeItem = null;
				setFixed(item, false);
				dragged = false;
			}     	
	}

	/**
	 * @see prefuse.controls.Control#itemDragged(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
	 */
	@Override
	public void itemDragged(VisualItem item, MouseEvent e) {
		if (!SwingUtilities.isLeftMouseButton(e)) return;

		dragged = true;
		Display d = (Display)e.getComponent();
		d.getAbsoluteCoordinate(e.getPoint(), temp);
		double dx = temp.getX()-down.getX();
		double dy = temp.getY()-down.getY();

		move(item, dx, dy);
		down.setLocation(temp);

	}

	protected static void setFixed(VisualItem item, boolean fixed) {

		if ( item instanceof AggregateItem ) {
			Iterator items = ((AggregateItem)item).items();
			while ( items.hasNext() ) 			
				setFixed((VisualItem)items.next(), fixed);
		}
		if ( item instanceof NodeItem )
				item.setFixed(fixed);
	}


	protected static void move(VisualItem item, double dx, double dy) {

		if ( item instanceof AggregateItem ) {
			Iterator items = ((AggregateItem)item).items();
			while ( items.hasNext() ) {
				move((VisualItem)items.next(), dx, dy);
			}
		} else {
			double x = item.getX();
			double y = item.getY();
			item.setStartX(x);  item.setStartY(y);
			item.setX(x+dx);    item.setY(y+dy);
			item.setEndX(x+dx); item.setEndY(y+dy);
		}
	}
} // end of class AggregateDragControl