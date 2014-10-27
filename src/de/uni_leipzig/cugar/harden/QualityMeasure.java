package de.uni_leipzig.cugar.harden;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Is an abstract base class for a concrete QualityMeasure implementation.
 */
public abstract class QualityMeasure{	
	/**
	 * Implement this abstract method for your concrete QualityMeasure class.
	 */
	public abstract double getQuality(TreeSet<Integer> clusters, TreeSet<Integer> blackList, ClusterGraphInterface clusterGraph);	
	/** 
	 * Gets the quality for a Set with the overloaded method {@link #getQuality(TreeSet, TreeSet, ClusterGraph)}, and
	 * ignores null and empty TreeSets in clusters.
	 */
	public HashMap<TreeSet<Integer>, Double> getQuality(Set<TreeSet<Integer>> clusters, TreeSet<Integer> blackList, ClusterGraphInterface clusterGraph)	{
		
		HashMap<TreeSet<Integer>, Double> clusterQualityMap = new HashMap<TreeSet<Integer>, Double>();				
		Iterator<TreeSet<Integer>> clustersIter = clusters.iterator();
		
		while(clustersIter.hasNext()){			
			TreeSet<Integer> cluster = clustersIter.next();
			if(cluster != null && cluster.size() > 0 ){
				double score =  getQuality(cluster,blackList,clusterGraph);
				if(clusterQualityMap.put(cluster, score) != null){	
					System.out.println(this.toString());
					System.out.println("clusterQualityMap replaced key -> duplicates in clusters");
				}
			}		
		}
		return clusterQualityMap;
	}
	/**
	 * Gets this class name.
	 */
	@Override
	public String toString(){
		String classpath = this.getClass().getName();
		if(classpath.contains("."))
			return classpath.substring(classpath.lastIndexOf(".")+1);		
		else
			return classpath;
	}
}