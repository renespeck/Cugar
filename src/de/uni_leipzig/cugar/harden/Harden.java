package de.uni_leipzig.cugar.harden;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
/**
 * Is an abstract base class for a concrete Harden implementation.
 */
public abstract class Harden {

	protected QualityMeasure m_qualityMeasure = null;
	protected ClusterGraphInterface m_clusterGraph = null;	
	
	public static final boolean VERBOSE = false;
	/** Empty constructor. */
	public Harden(){ }
	/** Initializes m_qualityMeasure. */
	public Harden(QualityMeasure qm){
		m_qualityMeasure = qm;
	}
	/** Gets m_qualityMeasure. */
	public QualityMeasure qualityMeasure(){
		return m_qualityMeasure;
	}
	/** Sets the a QualityMeasure. */
	public void qualityMeasure(QualityMeasure qm){		
			m_qualityMeasure = qm;
	}
	/** Method for hardening. Sets  m_clusterGraph and calls abstract harden method */
	public Map<TreeSet<Integer>, TreeSet<Integer>> harden(
			Map<TreeSet<Integer>, TreeSet<Integer>> clusterSeedMap, ClusterGraphInterface graph
			){
		m_clusterGraph = graph;
		return harden(clusterSeedMap);
	}
	/** 
	 * Implement this abstract method for your concrete Harden class.
	 * 
	 * @param clusterSeedMap soft
	 * @return clusterSeedMap hard
	 */
	protected abstract Map<TreeSet<Integer>, TreeSet<Integer>> harden(Map<TreeSet<Integer>, TreeSet<Integer>> clusterSeedMap);
	/** Method for a concrete harden implementation. */
	protected void mergeAddMap(Map<TreeSet<Integer>, TreeSet<Integer>> clusterSeedMap, TreeSet<Integer> cluster, TreeSet<Integer> seeds) {
		if (clusterSeedMap.containsKey(cluster))
			clusterSeedMap.get(cluster).addAll(seeds);
		else 
			clusterSeedMap.put(cluster, seeds);
	}
	/** Method for a concrete harden implementation. */
	protected double compareSet(Set<Integer> setA, Set<Integer> setB) {
		double cnt = 0;
		double size = Math.max(setA.size(), setB.size());
		for (Integer i : setA) 
			if (setB.contains(i)) 
				cnt++;			
		return cnt / size;
	}
	/** Method for a concrete harden implementation. */
	protected double getFlowFromNodeToSet(TreeSet<Integer> nodeSet, Integer node) {		
		Iterator<Integer> iter = nodeSet.iterator();		
		double flow = 0;
		while (iter.hasNext()) 			
			flow += m_clusterGraph.getEdgeWeight(node, iter.next());
		return flow;
	}
	/** Method for a concrete harden implementation. */
	public void removeDuplicateAndEmptyClusters(Map<TreeSet<Integer>, TreeSet<Integer>> clusterSeedMap){
		
		Map<TreeSet<Integer>, TreeSet<Integer>> tmp = new HashMap<TreeSet<Integer>, TreeSet<Integer>>();
	
		for (Map.Entry<TreeSet<Integer>, TreeSet<Integer>> entry : clusterSeedMap.entrySet()) {
			TreeSet<Integer> seeds = entry.getValue();
			
			if (tmp.containsKey(entry.getKey())) 
				seeds.addAll(tmp.get(entry.getKey()));
			
			if (!entry.getKey().isEmpty()) 
				tmp.put(entry.getKey(), seeds);
		}
		clusterSeedMap = tmp;
	}
	/** gets this class name */
	@Override
	public String toString(){
		return this.getClass().getSimpleName();
	}
}