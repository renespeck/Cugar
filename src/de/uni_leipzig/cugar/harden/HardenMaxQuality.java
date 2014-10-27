package de.uni_leipzig.cugar.harden;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Is a {@link de.uni_leipzig.cugar.harden.Harden} class and uses a {@link de.uni_leipzig.cugar.harden.QualityMeasure} instance 
 * to find the best clusters.
 */
public class HardenMaxQuality extends Harden {
	/** all used nodes */
	protected TreeSet<Integer> m_nodeBlackList = new TreeSet<Integer>();
	
	public HardenMaxQuality(){
		super();	
	}
	public HardenMaxQuality(QualityMeasure clusterQualityMeasure){
		super(clusterQualityMeasure);
	}	
	@Override
	protected HashMap<TreeSet<Integer>, TreeSet<Integer>> harden(Map<TreeSet<Integer>, TreeSet<Integer>> clusterSeedMap){
		int size = m_clusterGraph.size();
		if(VERBOSE)System.out.println("preprocess");
		// remove superset
		if(clusterSeedMap.size()>1)
			for(Map.Entry<TreeSet<Integer>, TreeSet<Integer>> e:clusterSeedMap.entrySet()){
				if(e.getKey().size() == size)
					e.getKey().clear();
			}
		if(VERBOSE)System.out.println("hardening");	
		// harden map
		HashMap<TreeSet<Integer>, TreeSet<Integer>>	hardenClusterSeedMap = new HashMap<TreeSet<Integer>, TreeSet<Integer>>();
		boolean allHarden = false;
		while(!allHarden){		
			// 0. remove empty
			clusterSeedMap = new HashMap<TreeSet<Integer>, TreeSet<Integer>>(clusterSeedMap);
			clusterSeedMap.remove(new TreeSet<Integer>());
			clusterSeedMap = new HashMap<TreeSet<Integer>, TreeSet<Integer>>(clusterSeedMap);
			// 1. calc. quality
			HashMap<TreeSet<Integer>, Double>	clusterQualityMap = m_qualityMeasure.getQuality(clusterSeedMap.keySet(),m_nodeBlackList,m_clusterGraph);					
			// 2. sort qualityValues of clusterQualityMap in desc. order				
			List<Double> qualityValues = new ArrayList<Double>(clusterQualityMap.values());	
			Collections.sort(qualityValues,Collections.reverseOrder());				
			// 3. pick clusters with max qualityValue to maxClusters List			
			if(qualityValues.size() < 1){
				allHarden = true;
				break;
			}
			double maxQualityValue = qualityValues.get(0);
			if(maxQualityValue <= 0){
				allHarden = true;
				break;
			}
			ArrayList<TreeSet<Integer>> maxClusters = new ArrayList<TreeSet<Integer>>();		
			for(TreeSet<Integer> cluster : clusterQualityMap.keySet())
				if(clusterQualityMap.get(cluster).equals(maxQualityValue))
					maxClusters.add(cluster);	
			// 4. harden clusters with current maxQualityValue or empty if all soft clusters 
			ArrayList<TreeSet<Integer>> hardClusters = extractHardClusters(maxClusters);
			// 5a. check results
			// hard clusters found
			if(!hardClusters.isEmpty()){
				for(TreeSet<Integer> hardCluster : hardClusters){					
					m_nodeBlackList.addAll(hardCluster);		
					TreeSet<Integer> seeds = null;
					
					if(clusterSeedMap.get(hardCluster) == null)
						seeds = new TreeSet<Integer>();
					else 
						seeds = new TreeSet<Integer>(clusterSeedMap.get(hardCluster));

					hardenClusterSeedMap.put(new TreeSet<Integer>(hardCluster),seeds);
				}
				deleteList(clusterSeedMap,m_nodeBlackList);
			}else
				// 5b.
				// only shared clusters
				while(!maxClusters.isEmpty()){		
					HashMap<TreeSet<Integer>, TreeSet<Integer>>	sharedClusterSeedMap = new HashMap<TreeSet<Integer>, TreeSet<Integer>>();
					boolean shared = true;
					TreeSet<Integer> nodes = new TreeSet<Integer>();
					while(shared){	// we search for clusters with same nodes
						shared = false;
						// for all clusters						
						for(TreeSet<Integer> cluster : maxClusters){ 
							// take 1st
							if(sharedClusterSeedMap.isEmpty()){
								sharedClusterSeedMap.put(cluster,clusterSeedMap.get(cluster));
								nodes.addAll(cluster);
								continue;
							}
							if(sharedClusterSeedMap.get(cluster) == null) 
								for(Integer node : nodes){ 
									if(cluster.contains(node)){
										nodes.addAll(cluster);
										sharedClusterSeedMap.put(cluster,clusterSeedMap.get(cluster));
										shared = true;
										break;				
									}
								}
						}// end loop all clusters
					}// found all shared
					// build new hard clusters
					HashMap<TreeSet<Integer>, TreeSet<Integer>> sharedTohardenMap = buildCluster(sharedClusterSeedMap);
					// add to final 
					for(TreeSet<Integer> key : sharedTohardenMap.keySet()){
						hardenClusterSeedMap.put(new TreeSet<Integer>(key), sharedTohardenMap.get(key));						
						m_nodeBlackList.addAll(key);				
					}
					// remove old shared clusters from max list
					for(TreeSet<Integer> sharedCluster : sharedClusterSeedMap.keySet())
						maxClusters.remove(sharedCluster);
				}// end while maxClusters
			deleteList(clusterSeedMap,m_nodeBlackList);
			//end else no finals 			
		}	
		/* hardening end */
		if(VERBOSE)System.out.println("postprocess");
		//for (Entry<TreeSet<Integer>, TreeSet<Integer>> clusters : hardenClusterSeedMap.entrySet())			
		//	if(clusters.getKey().size()==1)
		//		clusters.getKey().removeAll(clusters.getKey());

		hardenClusterSeedMap = new HashMap<TreeSet<Integer>, TreeSet<Integer>>(hardenClusterSeedMap);
		hardenClusterSeedMap.remove(new TreeSet<Integer>());
		hardenClusterSeedMap = new HashMap<TreeSet<Integer>, TreeSet<Integer>>(hardenClusterSeedMap);

		for(Map.Entry<TreeSet<Integer>,TreeSet<Integer>> fc : hardenClusterSeedMap.entrySet())
			if(fc.getValue() == null)
				fc.setValue(new TreeSet<Integer>());
		return assureMembership(hardenClusterSeedMap,size);		 
	}
	/**
	 * Gets an ArrayList of TreeSet with different Integers in every TreeSet. 
	 */
	protected ArrayList<TreeSet<Integer>> extractHardClusters(ArrayList<TreeSet<Integer>> clusters){		

		List<TreeSet<Integer>> tmpClusters = new ArrayList<TreeSet<Integer>>();	
		tmpClusters.addAll(clusters);

		ArrayList<TreeSet<Integer>> hardClusters = new ArrayList<TreeSet<Integer>>();

		Iterator<TreeSet<Integer>> clustersIter = clusters.iterator();		
		while(clustersIter.hasNext()){
			boolean share = false;
			TreeSet<Integer> out = clustersIter.next();

			Iterator<TreeSet<Integer>> tmpClustersIter = tmpClusters.iterator();
			while(tmpClustersIter.hasNext()){
				TreeSet<Integer> in = tmpClustersIter.next();

				if(!out.equals(in))
					if(compareSet(out, in) != 0.0 ){	
						share = true;					
						break;
					}
			}
			if(!share){
				hardClusters.add(new TreeSet<Integer>(out));		
				tmpClusters.remove(out);
			}
		}
		return hardClusters;
	}
	/**
	 * Deletes all Integer contains in blackList from map key and map value.
	 */
	private void deleteList(Map<TreeSet<Integer>, TreeSet<Integer>> map, TreeSet<Integer> blackList){
		if(VERBOSE)System.out.println("delete blackList, size: " + blackList.size());
		Iterator<Map.Entry<TreeSet<Integer>, TreeSet<Integer>>> entryIter = map.entrySet().iterator();		
		while(entryIter.hasNext()){
			Map.Entry<TreeSet<Integer>, TreeSet<Integer>> e = entryIter.next();			
			e.getKey().removeAll(blackList);
			e.getValue().removeAll(blackList);			
		}
	}
	/**
	 * Removes shared nodes and adds this to the best cluster, calculated with rematchCluster() method.
	 * If all nodes are shared we add all nodes to one cluster.
	 * 
	 * @param sharedClusterSeedMap soft cluster and there seeds
	 */
	private HashMap<TreeSet<Integer>, TreeSet<Integer>> buildCluster(HashMap<TreeSet<Integer>, TreeSet<Integer>> sharedClusterSeedMap){

		TreeSet<Integer> sharedNodes = new TreeSet<Integer>();
		TreeSet<Integer> nodes = new TreeSet<Integer>();
		// find shared nodes
		// all clusters
		for(TreeSet<Integer> cluster : sharedClusterSeedMap.keySet()){
			if(nodes.isEmpty()){
				nodes.addAll(cluster);
				continue;
			}
			for(Integer i : cluster)
				if(nodes.contains(i)){					
					sharedNodes.add(i);	
				}
			nodes.addAll(cluster);
		}
		for(Map.Entry<TreeSet<Integer>, TreeSet<Integer>>  e : sharedClusterSeedMap.entrySet()){
			if(e.getValue() != null)
				e.getValue().removeAll(sharedNodes);
			e.getKey().removeAll(sharedNodes);			
		}
		// all nodes shared we merge to one cluster
		if(nodes.size() ==  sharedNodes.size() ){
			sharedClusterSeedMap.put(new TreeSet<Integer>(sharedNodes), null);
			sharedNodes.removeAll( sharedNodes);
		}
		sharedClusterSeedMap = new HashMap<TreeSet<Integer>, TreeSet<Integer>>(sharedClusterSeedMap);
		sharedClusterSeedMap.remove(new TreeSet<Integer>());				
		return assureMembership(new HashMap<TreeSet<Integer>, TreeSet<Integer>>(sharedClusterSeedMap),sharedNodes);
	}
	private HashMap<TreeSet<Integer>, TreeSet<Integer>> assureMembership( HashMap<TreeSet<Integer>, TreeSet<Integer>> clusterSeedMap, int size) {
		TreeSet<Integer> nodes = new   TreeSet<Integer>();
		for(int i = 0; i < size ; i++)
			nodes.add(i);
		return assureMembership(clusterSeedMap,nodes);
	}
	/**
	 * Finds nodes arn't available in any cluster, scores node to best cluster and adds.
	 * Stops, if all nodes in clusters.
	 * @param clusterSeedMap
	 * @param nodes
	 */
	private HashMap<TreeSet<Integer>, TreeSet<Integer>> assureMembership( HashMap<TreeSet<Integer>, TreeSet<Integer>> clusterSeedMap, TreeSet<Integer> nodes) {

		boolean tryagain = true;	
		int tryagainTimes = 5;
		while (tryagain && tryagainTimes>0) {					
			tryagain = false;
			// all nodes
			for (Integer node : nodes) {
				boolean found = false;
				// all cluster
				for (TreeSet<Integer> cluster : clusterSeedMap.keySet()) {
					if (cluster.contains(node))
						found = true;
				}
				if (!found) {
					// node isn't available in any cluster
					double maxScore = 0.0;
					TreeSet<Integer> maxscoredCluster = null;
					// all cluster
					for (TreeSet<Integer> cluster : clusterSeedMap.keySet()) {
						// check for relative flow
						double score = 0;						
						score = getFlowFromNodeToSet(cluster, node) / cluster.size();
						//System.out.println(score);
						
						if (score > maxScore) {
							if (maxscoredCluster != null) 
								maxscoredCluster.remove(node);
							maxscoredCluster = cluster;
							maxScore = score;
						}else
							cluster.remove(node); // ? we need this?
					}// all cluster end					
					if (maxScore > 0)
						maxscoredCluster.add(node);
					else{
						tryagainTimes--;
						tryagain = true;
					}
				}
			}
		}return clusterSeedMap;
	}
}