package de.uni_leipzig.cugar.visual;

import java.util.Iterator;

import prefuse.action.layout.Layout;
import prefuse.visual.DecoratorItem;
/**
 * Set label positions. Labels are assumed to be DecoratorItem instances,
 * decorating their respective nodes. The layout simply gets the bounds
 * of the decorated node and assigns the label coordinates to the center
 * of those bounds.
 */
class LabelLayout extends Layout {
	
	public LabelLayout(String group) {
		super(group);
	}
	
	@Override
	public void run(double frac) {
		Iterator<DecoratorItem> iter = m_vis.items(m_group);
		while ( iter.hasNext() ) {
			DecoratorItem decorator = iter.next();
			setX(decorator, null, decorator.getDecoratedItem().getBounds().getCenterX());
			setY(decorator, null, decorator.getDecoratedItem().getBounds().getCenterY());
		}
	}
}