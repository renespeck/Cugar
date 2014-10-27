package de.uni_leipzig.cugar.visual;

import prefuse.Visualization;
import prefuse.action.assignment.ColorAction;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
/**
 * A new ColorAction for all nodes.
 *
 * @author rspeck
 *
 */
public class NodeColorAction extends ColorAction {
	
	public final int blue = ColorLib.rgb(100,100,255);
	public final int green = ColorLib.rgb(100,255,100);
	public final int red = ColorLib.rgb(255,100,100);
	public final int orange = ColorLib.rgb(255,200,125);
	
	public NodeColorAction(String group) {
		super(group, VisualItem.FILLCOLOR,ColorLib.gray(255));
		// keep this order
		add(VisualItem.HOVER,blue);
		add(new InGroupPredicate(Visualization.SEARCH_ITEMS),green);
		add(VisualItem.HIGHLIGHT,orange );	
		add(new InGroupPredicate(Visualization.FOCUS_ITEMS), red);  		
		add(VisualItem.FIXED, red);  		
	}
}