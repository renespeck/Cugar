package de.uni_leipzig.cugar.harden;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * Is a {@link de.uni_leipzig.cugar.harden.QualityMeasure} class.
 */
public class QualityMeasureSilhouette extends QualityMeasure{	
	/**
	 *  Same as in {@link de.uni_leipzig.bf.cluster.Cluster#getSilhouette()}
	 */
	@Override
	public double getQuality(TreeSet<Integer> elements, TreeSet<Integer> blackList, ClusterGraphInterface graph){
				
		double a[]= new double[elements.size()];
		double b[]= new double[elements.size()];		
		
		Integer[] elts = new Integer[elements.size()];
		elements.toArray(elts);				
		
		for(int i=0; i<elts.length; i++){
			a[i]=1;
			b[i]=0;
		}
		
		double max = 0;		
		for(int i=0; i<elts.length; i++){			
			double counter = 0;
			TreeSet<Integer> neighbours = graph.getNeighbours(elts[i]);			
			//neighbours.removeAll(blackList);
			
			// compute average similarity within cluster
			for(int j=0; j<elts.length; j++){
				if(i!=j && neighbours.contains(elts[j])){
					a[i] = a[i] + graph.getEdgeWeight(elts[i], elts[j]);             
					counter ++;
				}
			}
			if(counter > 0) 
				a[i] = a[i]/counter;
			// compute maximal similarity to other clusters
			neighbours = graph.getNeighbours(elts[i]);			
		//	neighbours.removeAll(blackList);
			
			Iterator<Integer> iter = neighbours.iterator();
			while(iter.hasNext()){
				Integer node = iter.next();
				if(!elements.contains(node)){
					double sim = graph.getEdgeWeight(node, elts[i]);
					if(max < sim) 
						max = sim;
				}
			}
			b[i] = max;
		}   
		// compute average silhouette
		double silhouette = 0;
		for(int i=0; i<a.length; i++)    
			silhouette = silhouette + (a[i]-b[i])/java.lang.Math.max(a[i], b[i]);		
		return silhouette/a.length;	
	}	
}