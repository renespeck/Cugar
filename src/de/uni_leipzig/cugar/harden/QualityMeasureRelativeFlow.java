package de.uni_leipzig.cugar.harden;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * Is a {@link de.uni_leipzig.cugar.harden.QualityMeasure} class.
 */
public class QualityMeasureRelativeFlow extends QualityMeasure{	
	/**
	 *  Same as in {@link de.uni_leipzig.bf.cluster.Cluster#getRelativeFlow()}, but ignore Integers of blackList.
	 * 
	 */
	@Override
	public double getQuality(TreeSet<Integer> cluster, TreeSet<Integer> blackList, ClusterGraphInterface graph){
		//Todo: reduce operations cause it's very slow.
		if(cluster.size()<=1)
			return 0;	
		
		double flowIn = 0, flowOut = 0;
		// calc. inner border
		for(Integer innerNode: cluster){
			TreeSet<Integer> innerNeighbours = graph.getNeighbours(innerNode);
			for(Integer innerNeighbour:innerNeighbours){
				if(!cluster.contains(innerNeighbour)){		
					// now we know innerNode is an inner bordernode
					TreeSet<Integer> innerBorderNeighbours = graph.getNeighbours(innerNode);
					innerBorderNeighbours.removeAll(blackList);
					// get flow in and flow out caused by each inner bordernode
					Iterator<Integer> itera = innerBorderNeighbours.iterator();
					while(itera.hasNext()){ 
						Integer innerBorderNeighbour = itera.next();	
					//	if(!blackList.contains(innerBorderNeighbour))
							if(cluster.contains(innerBorderNeighbour))
								flowIn +=  graph.getEdgeWeight(innerBorderNeighbour, innerNode);
							else
								flowOut +=  graph.getEdgeWeight(innerBorderNeighbour, innerNode);							
					}					
					break;
				}				
			}			
		}				
		if(flowOut!=0)
			return flowIn/flowOut;
		else return Integer.MAX_VALUE;
	}
}