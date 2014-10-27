package de.uni_leipzig.bf.cluster;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.uni_leipzig.bf.eval.Timer;
import de.uni_leipzig.bf.util.QuickSort;
import de.uni_leipzig.bf.util.WeightedKeyword;
import de.uni_leipzig.cc.coyotecache.CoyoteCache;
/**
 *
 * @author an
 *
 */
public class BorderFlow {

	protected Connection con = null;
	protected String ip, database, user, password;

	protected ClusterGraph graph = new ClusterGraph();
	protected CoyoteCache cache = null;	

	public static Logger logger = Logger.getLogger(BorderFlow.class);
	static{
		logger.setLevel(Level.INFO);
	}
	/**
	 * Constructs new instance.
	 *
	 * @param file path to file
	 * @param separator file separator
	 */
	public BorderFlow(String file, String separator,CoyoteCache p_cache) {
		init(file, separator);
		cache = p_cache;
	}
	/**
	 * Constructs new instance for tabulator separated files.
	 *
	 * @param file path to file
	 * @param p_harden  a {@link de.uni_leipzig.cugar.harden.Harden} instance.
	 */
	public BorderFlow(String file,CoyoteCache p_cache) {
		init(file,"\t");
		cache = p_cache;
	}
	private void init(String file, String separator){
		if (file != null && separator.length() > 0)
			graph.initialize(file,separator);

	}
	public ClusterGraph getGraph() {
		return graph;
	}
	/**
	 *
	 * Starts Borderflow with the given configuration to write in file.
	 *
	 * @param outputFile 	Output is written in outputFile
	 * @param connThreshold gives the percentage of the maximal connectivity that a node
	 * 						can maximally have to be used as seed
	 * @param testOne		sets the type of test to use
	 * @param heuristic 	heuristic or the optimal version of borderflow
	 * @param caching 		sets cache version or not
	 */
	public void clusterToFile(String outputFile, double connThreshold, boolean testOne, boolean heuristic) {
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter( new FileWriter(outputFile)));
			writer.println(writeToString(cluster(connThreshold, testOne, heuristic)));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 *
	 * Starts Borderflow with the given configuration to write in String.
	 *
	 * @param connThreshold gives the percentage of the maximal connectivity that a node
	 * 						can maximally have to be used as seed
	 * @param testOne		sets the type of test to use
	 * @param heuristic 	heuristic or the optimal version of borderflow
	 * @param caching 		sets cache version or not
	 *
	 * @return clusters
	 */
	public Map<TreeSet<Integer>, TreeSet<Integer>> cluster(double connThreshold, boolean testOne, boolean heuristic) {


		// get set of seeds
		double connectivity = 0, threshold = 0;
		Map<Integer, Integer> connectMap = new HashMap<Integer, Integer>();
		Map<TreeSet<Integer>, TreeSet<Integer>>	clusterSeedMap = new HashMap<TreeSet<Integer>, TreeSet<Integer>>();
		int neighbours = 0;
		for (int i = 0; i < graph.index.size(); i++) {
			neighbours = graph.getNeighbours(i).size();
			connectivity = connectivity + neighbours;
			connectMap.put(new Integer(i), new Integer(neighbours));
		}
		connectivity = connectivity / graph.index.size();
		threshold = connectivity * connThreshold;
		String cacheMessage = "off";
		if(cache!=null){			
			// set seeds
			Set<Integer> seeds = new LinkedHashSet<Integer>();
			for(int node = 0 ; node < graph.size; node++){
				if (connectMap.get(node).doubleValue() <= threshold || threshold < 0) {
					seeds.add(new Integer(node));			
				}
			}
			cache.setSeeds(seeds); 			
			cacheMessage = cache.toString();
		}

		String m = "\n" + "********************************************* \n"
		+ "*  Options are set to: \n"
		+ "*  Clustering " + connThreshold * 100 + "% of connectivity mean\n"
		+ "*  Threshold = " + threshold + " nodes\n"
		+ "*  Use heuristic = " + heuristic + "\n"
		+ "*  One-node test for termination = " + testOne+ "\n"
		+ "*  Caching = " + cacheMessage + ".\n";
		m+= "*********************************************\n";
		logger.info("Clustering ...");	


		Integer integer;
		TreeSet<Integer> cluster;
		// set timers
		long startSystemTimeNano = Timer.getSystemTime();
		long startUserTimeNano = Timer.getUserTime();

		if(cache!=null)
			while((integer = cache.getBestSeed())!= null) {	
				if (connectMap.get(integer).doubleValue() <= threshold || threshold < 0) {
					// decide on how to cluster
					if (heuristic){
						cluster = testOne ? heuristicGetClusterTestOne(integer) : heuristicGetCluster(integer);
					}else{//optimal
						cluster = testOne ? optimalGetClusterTestOne(integer) : optimalGetCluster(integer);
					}
					if (clusterSeedMap.containsKey(cluster))
						clusterSeedMap.get(cluster).add(integer);
					else {
						TreeSet<Integer> set = new TreeSet<Integer>();
						set.add(integer);
						clusterSeedMap.put(cluster, set);
					}
				}
			}
		else{ //cache off
			for (int i = 0; i < graph.index.size(); i++) {
				integer = new Integer(i);
				if (connectMap.get(integer).doubleValue() <= threshold || threshold < 0) {
					// decide on how to cluster
					if (heuristic){
						cluster = testOne ? heuristicGetClusterTestOne(integer) : heuristicGetCluster(integer);
					}else{//optimal
						cluster = testOne ? optimalGetClusterTestOne(integer) : optimalGetCluster(integer);
					}
					if (clusterSeedMap.containsKey(cluster))
						clusterSeedMap.get(cluster).add(integer);
					else {
						TreeSet<Integer> set = new TreeSet<Integer>();
						set.add(integer);
						clusterSeedMap.put(cluster, set);
					}
				}
			}
		}

		m += "*  Clustering ends with " + clusterSeedMap.size() + " clusters\n";
		m += "*********************************************\n";
		// read timer
		long taskUserTimeNano = Timer.getUserTime() - startUserTimeNano;
		long taskSystemTimeNano = Timer.getSystemTime() - startSystemTimeNano;
		int hits =0;
		if(cache!=null)
			hits = cache.m_statistic.getHits();
		m +="*  Time in ms.\n*  User: " + taskUserTimeNano / 1000000
		+ "\n*  System: " + taskSystemTimeNano / 1000000
		+ "\n*  CPU: " + (taskUserTimeNano + taskSystemTimeNano)/ 1000000
		+ "\n*  Cache Hits: " + hits
		+ "\n*********************************************";
		logger.info(m);
		//logger.info(cache.toString());
		clusterSeedMap = removeDuplicateAndEmptyClusters(clusterSeedMap);
		return clusterSeedMap;
	}
	/**
	 *
	 * Starts Borderflow with the given configuration to write in String.
	 *
	 * @param p_seeds		Strings to be used as seed
	 * @param testOne		sets the type of test to use
	 * @param heuristic 	heuristic or the optimal version of borderflow
	 * @param caching 		sets cache version or not
	 *
	 * @return clusters
	 */
	public Map<TreeSet<Integer>, TreeSet<Integer>> cluster(String[] p_seeds, boolean testOne, boolean heuristic) {

		Set<Integer> seeds = new LinkedHashSet<Integer>();
		for(int i = 0 ; i < p_seeds.length; i++)
			seeds.add(graph.index.get(p_seeds[i]));

		String cacheMessage = "off";
		if(cache!=null){
			cache.setSeeds(seeds);
			cacheMessage = cache.toString();

		}
		String m = "\n*********************************************\n"
			+ "*  Options are set to: \n"
			+ "*  Clustering with seeds\n"
			+ "*  Use heuristic = " + heuristic + "\n"
			+ "*  One-node test for termination = " + testOne+ "\n"
			+ "*  Caching = " + cacheMessage + "\n";
		m+= "*********************************************\n";

		Map<TreeSet<Integer>, TreeSet<Integer>>	clusterSeedMap 	= new HashMap<TreeSet<Integer>, TreeSet<Integer>>();
		logger.info("Clustering ...");
		// for each seed
		TreeSet<Integer> cluster;
		// set timers
		long startSystemTimeNano = Timer.getSystemTime();
		long startUserTimeNano = Timer.getUserTime();
		Integer index = null;
		if(cache!=null)
			while((index = cache.getBestSeed())!= null) {	

				// decide on how to cluster
				if (heuristic){
					cluster = testOne ? heuristicGetClusterTestOne(index) : heuristicGetCluster(index);
				}else{//optimal
					cluster = testOne ? optimalGetClusterTestOne(index) : optimalGetCluster(index);
				}
				if (clusterSeedMap.containsKey(cluster))
					clusterSeedMap.get(cluster).add(index);
				else {
					TreeSet<Integer> set = new TreeSet<Integer>();
					set.add(index);
					clusterSeedMap.put(cluster, set);
				}
			}
		else{ // cache off
			Integer integer;
			for(int i = 0 ; i < p_seeds.length; i++){
				index = graph.index.get(p_seeds[i]);
				integer = new Integer(index);
				// decide on how to cluster
				if (heuristic){
					cluster = testOne ? heuristicGetClusterTestOne(integer) : heuristicGetCluster(integer);
				}else{//optimal
					cluster = testOne ? optimalGetClusterTestOne(integer) : optimalGetCluster(integer);
				}
				if (clusterSeedMap.containsKey(cluster))
					clusterSeedMap.get(cluster).add(integer);
				else {
					TreeSet<Integer> set = new TreeSet<Integer>();
					set.add(integer);
					clusterSeedMap.put(cluster, set);
				}
			}
		}
		m += "*  Clustering ends with " + clusterSeedMap.size() + " clusters\n";
		m += "*********************************************\n";
		// read timer
		long taskUserTimeNano = Timer.getUserTime() - startUserTimeNano;
		long taskSystemTimeNano = Timer.getSystemTime() - startSystemTimeNano;
		int hits =0;
		if(cache!=null)
			hits = cache.m_statistic.getHits();
		m +="*  Time in ms.\n*  User: " + taskUserTimeNano / 1000000
		+ "\n*  System: " + taskSystemTimeNano / 1000000
		+ "\n*  CPU: " + (taskUserTimeNano + taskSystemTimeNano)/ 1000000
		+ "\n*  Cache Hits: " + hits
		+ "\n*********************************************";
		logger.info(m);
		clusterSeedMap = removeDuplicateAndEmptyClusters(clusterSeedMap);
		return clusterSeedMap;
	}


	public Map<TreeSet<String>, TreeSet<String>> getLabels(Map<TreeSet<Integer>, TreeSet<Integer>> clusterSeedMap){

		Map<TreeSet<String>, TreeSet<String>> clusterSeedMapLabels = new HashMap<TreeSet<String>, TreeSet<String>>();
		for (Entry<TreeSet<Integer>, TreeSet<Integer>> entry : clusterSeedMap.entrySet()) {

			TreeSet<String> keyClear = new TreeSet<String>();
			TreeSet<String> seedClear = new TreeSet<String>();

			for (int i : entry.getKey())
				keyClear.add(graph.reverseIndex.get(i));
			for (int j : entry.getValue())
				seedClear.add(graph.reverseIndex.get(j));

			clusterSeedMapLabels.put(keyClear, seedClear);

		}return clusterSeedMapLabels;
	}


	protected Map<TreeSet<Integer>, TreeSet<Integer>> removeDuplicateAndEmptyClusters(Map<TreeSet<Integer>, TreeSet<Integer>> clusterSeedMap){

		Map<TreeSet<Integer>, TreeSet<Integer>> tmpClusterSeedMap = new HashMap<TreeSet<Integer>, TreeSet<Integer>>();

		for (Entry<TreeSet<Integer>, TreeSet<Integer>> entry : clusterSeedMap.entrySet()) {
			TreeSet<Integer> seeds = entry.getValue();

			if (tmpClusterSeedMap.containsKey(entry.getKey()))
				seeds.addAll(tmpClusterSeedMap.get(entry.getKey()));

			if (!entry.getKey().isEmpty())
				tmpClusterSeedMap.put(entry.getKey(), seeds);
		}
		return tmpClusterSeedMap;
	}


	/**
	 * Writes clusterSeedMap to String.
	 *
	 * @param clusterSeedMap	with clusters and seeds
	 * @return clusters
	 */
	protected String writeToString(Map<TreeSet<Integer>, TreeSet<Integer>> clusterSeedMap){

		System.out.println("Writing out clusters...");
		String out = "ID\tCluster\tSeeds\tSilhouette\tRelative flow\n";
		int id = 0;
		for (Entry<TreeSet<Integer>, TreeSet<Integer>> entry : clusterSeedMap.entrySet()) {

			TreeSet<String> keyClear 	= new TreeSet<String>();
			TreeSet<String> seedClear 	= new TreeSet<String>();

			for (int i : entry.getKey())
				keyClear.add(graph.reverseIndex.get(i));
			for (int j : entry.getValue())
				seedClear.add(graph.reverseIndex.get(j));

			out += id +
			"\t" + keyClear +
			"\t" + seedClear +
			"\t" + getSilhouetteAndRelativeFlow(entry.getKey()) +
			"\n";
			id++;
		}
		return out;
	}

	/**
	 * Starts knn with the given configuration to write in file.
	 */
	public void knnToFile(String outputFile, double connThreshold, int k) {
		try{
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
			writer.println(writeToString(knn(connThreshold,k)));
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Starts knn with the given configuration to write in String.
	 */
	public Map<TreeSet<Integer>, TreeSet<Integer>> knn( double connThreshold, int k) {
		// get set of seeds
		double connectivity = 0;
		double threshold = 0;
		Map<Integer, Integer> connectMap = new HashMap<Integer, Integer>();
		Map<TreeSet<Integer>, TreeSet<Integer>> clusterSeedMap = new HashMap<TreeSet<Integer>, TreeSet<Integer>>();
		int size = graph.index.size();
		int neighbours = 0;
		for (int i = 0; i < size; i++) {
			neighbours = graph.getNeighbours(i).size();
			connectivity = connectivity + neighbours;
			connectMap.put(new Integer(i), new Integer(neighbours));
		}

		connectivity = connectivity / size;
		threshold = connectivity * connThreshold;
		System.out.println("Threshold = " + threshold + " nodes");

		// for each seed
		logger.info("Clustering ...");

		String m = "\n" + "********************************************* \n"
		+ "*  Options are set to: \n"
		+ "*  Clustering " + connThreshold * 100 + "% of connectivity mean\n"
		+ "*  Threshold = " + threshold + " nodes\n" ;
		m+= "*********************************************\n";

		long startSystemTimeNano = Timer.getSystemTime();
		long startUserTimeNano = Timer.getUserTime();

		for (int i = 0; i < size; i++) {
			Integer integer = new Integer(i);
			if (connectMap.get(integer).doubleValue() <= threshold || threshold < 0) {
				//System.out.println("Seed = " + i);
				TreeSet<Integer> cluster = knnCluster(integer, k);
				if (clusterSeedMap.containsKey(cluster))
					clusterSeedMap.get(cluster).add(integer);
				else {
					TreeSet<Integer> set = new TreeSet<Integer>();
					set.add(integer);
					clusterSeedMap.put(cluster, set);
				}
			}
		}
		m += "*  Clustering ends with " + clusterSeedMap.size() + " clusters\n";
		m += "*********************************************\n";
		// read timer
		long taskUserTimeNano = Timer.getUserTime() - startUserTimeNano;
		long taskSystemTimeNano = Timer.getSystemTime() - startSystemTimeNano;

		int hits =0;
		if(cache!=null)
			hits = cache.m_statistic.getHits();
		
		m +="*  Time in ms.\n*  User: " + taskUserTimeNano / 1000000
		+ "\n*  System: " + taskSystemTimeNano / 1000000
		+ "\n*  CPU: " + (taskUserTimeNano + taskSystemTimeNano)/ 1000000
		+ "\n*  Cache Hits: " + hits
		+ "\n*********************************************";
		logger.info(m);
		clusterSeedMap = removeDuplicateAndEmptyClusters(clusterSeedMap);
		return clusterSeedMap;
	}
	/**
	 * Starts knn with the given configuration to write in String.
	 */
	public Map<TreeSet<Integer>, TreeSet<Integer>> knn( String[] p_seeds, int k) {
		Map<TreeSet<Integer>, TreeSet<Integer>> clusterSeedMap = new HashMap<TreeSet<Integer>, TreeSet<Integer>>();
		logger.info("Clustering ...");
		String m = "\n" + "********************************************* \n"
		+ "*  Options are set to: \n"
		+ "*  Clustering with seeds\n";
		m+= "*********************************************\n";

		long startSystemTimeNano = Timer.getSystemTime();
		long startUserTimeNano = Timer.getUserTime();
		for(int i = 0 ; i < p_seeds.length; i++){

			Integer integer = new Integer( graph.index.get(p_seeds[i]));
			//System.out.println("Seed = " + i);
			TreeSet<Integer> cluster = knnCluster(integer, k);
			if (clusterSeedMap.containsKey(cluster))
				clusterSeedMap.get(cluster).add(integer);
			else {
				TreeSet<Integer> set = new TreeSet<Integer>();
				set.add(integer);
				clusterSeedMap.put(cluster, set);
			}
		}
		m += "*  Clustering ends with " + clusterSeedMap.size() + " clusters\n";
		m += "*********************************************\n";
		// read timer
		long taskUserTimeNano = Timer.getUserTime() - startUserTimeNano;
		long taskSystemTimeNano = Timer.getSystemTime() - startSystemTimeNano;
		int hits =0;
		if(cache!=null)
			hits = cache.m_statistic.getHits();
		m +="*  Time in ms.\n*  User: " + taskUserTimeNano / 1000000
		+ "\n*  System: " + taskSystemTimeNano / 1000000
		+ "\n*  CPU: " + (taskUserTimeNano + taskSystemTimeNano)/ 1000000
		+ "\n*  Cache Hits: " + hits
		+ "\n*********************************************";
		logger.info(m);
		clusterSeedMap = removeDuplicateAndEmptyClusters(clusterSeedMap);
		return clusterSeedMap;
	}
	/**
	 * Compute cluster for a given seed using heuristic and test one version of BorderFlow.
	 */
	public TreeSet<Integer> heuristicGetClusterTestOne(Integer seed) {
		logger.debug("starting heuristicGetClusterTestOne for index: " + seed.intValue());

		Cluster c = new Cluster(graph, seed);
		double R_old = -1, R_new = 0, rPlusOne = -1;
		ArrayList<Integer> lastCandidates = new ArrayList<Integer>();
		boolean  closedCluster = false;
		long startUserTimeNano = new Date().getTime();
		long startTime = startUserTimeNano;
		
		while (R_old <= R_new && R_old <= rPlusOne) {
			
			// check cache
			long time = startUserTimeNano - startTime;
			if(time == 0) time = 1;
			Double cost = 1D/time;
			TreeSet<Integer> elements = null;
			if(cache != null)
				if((elements = (TreeSet<Integer>) cache.get(c.getElements(),cost)) != null)
					return elements;
			startUserTimeNano = new Date().getTime();
			
			// for each element of the outer border, compute r
			// closed cluster
			if (c.outerBorder.size() == 0){
				closedCluster = true;
				break;
			}

			ArrayList<WeightedKeyword> rMaxList = new ArrayList<WeightedKeyword>();
			ArrayList<WeightedKeyword> candidateList = new ArrayList<WeightedKeyword>();
			lastCandidates = new ArrayList<Integer>();

			Iterator<Integer> iter = c.outerBorder.iterator();
			while (iter.hasNext()) {
				Integer candidate =  iter.next();
				// compute r(candidate)
				candidateList.add(
						new WeightedKeyword(
								candidate.intValue() + "",
								getInverseRelativeFlow(c,candidate)
						)
				);
			}
			candidateList = QuickSort.quickSort(candidateList);

			// now retrieve elements which produced maximal r
			double r_max = candidateList.get(candidateList.size() - 1).getWeight();
			for (int i = candidateList.size() - 1; i >= 0; i--) {
				if (candidateList.get(i).getWeight() == r_max) {
					rMaxList.add(new WeightedKeyword(candidateList.get(i).getKeyword(), 0));
				}
				// else break;
			}

			// now compute the flow to the outerborder in case more than one
			// element produced maximal flow
			if (rMaxList.size() > 1) {
				for (int i = 0; i < rMaxList.size(); i++) {
					rMaxList.get(i).setWeight(
							getFlowFromNodeToSet(c.outerBorder, new Integer(
									rMaxList.get(i).getKeyword())));
				}

				// sort and get elements with maximum
				rMaxList = QuickSort.quickSort(rMaxList);
				double max = rMaxList.get(0).getWeight();
				boolean testOne = false;
				for (int i = 0; i < rMaxList.size(); i++) {
					if (rMaxList.get(i).getWeight() == max) {
						lastCandidates.add(new Integer(rMaxList.get(i).getKeyword()));
						c.addNode(new Integer(rMaxList.get(i).getKeyword()));
						if (!testOne) {
							rPlusOne = c.getRelativeFlow();
							testOne = true;
						}
					}
					// else break;
				}
			} // compute new R
			else {
				c.addNode(new Integer(rMaxList.get(0).getKeyword()));
				lastCandidates.add(new Integer(rMaxList.get(0).getKeyword()));
				rPlusOne = c.getRelativeFlow();
			}
			R_old = R_new;
			R_new = c.getRelativeFlow();
		}

		TreeSet<Integer> finalCluster = new TreeSet<Integer>();
		if( !closedCluster){
			Iterator<Integer> elementsIter = c.getElements().iterator();
			while (elementsIter.hasNext()) {
				Integer cElement = elementsIter.next();
				if (!lastCandidates.contains(cElement))
					finalCluster.add(cElement);
			}
		}else
			finalCluster = c.getElements();
		// This is really a new cluster.
		// Then we need to add it to the cache.
		if(cache!=null)
			return (TreeSet<Integer>) cache.put(finalCluster);
		else 
			return finalCluster;
	}
	/**
	 * Compute cluster for a given seed using heuristic version of BorderFlow.
	 */
	public TreeSet<Integer> heuristicGetCluster(Integer seed) {
		logger.debug("starting heuristicGetCluster for index: " + seed.intValue());

		Cluster c = new Cluster(graph, seed);
		double R_old = -1, R_new = 0;
		ArrayList<Integer> lastCandidates = new ArrayList<Integer>();
		boolean  closedCluster = false;
		long startUserTimeNano = new Date().getTime();
		long startTime = startUserTimeNano;
		while (R_old <= R_new) {
			
			// check cache
			long time = startUserTimeNano - startTime;
			if(time == 0) time = 1;
			Double cost = 1D/time;
			TreeSet<Integer> elements = null;
			if(cache != null)
				if((elements = (TreeSet<Integer>) cache.get(c.getElements(),cost)) != null)
					return elements;
			startUserTimeNano = new Date().getTime();
			// for each element of the outer border, compute r
			// closed cluster
			if (c.outerBorder.size() == 0){
				closedCluster = true;
				break;
			}

			ArrayList<WeightedKeyword> rMaxList = new ArrayList<WeightedKeyword>();
			ArrayList<WeightedKeyword> candidateList = new ArrayList<WeightedKeyword>();
			lastCandidates = new ArrayList<Integer>();
			Iterator<Integer>  iter = c.outerBorder.iterator();
			while (iter.hasNext()) {
				Integer candidate = iter.next();
				// compute r(candidate)
				candidateList.add(
						new WeightedKeyword(
								candidate.intValue() + "",
								getInverseRelativeFlow(c,candidate)
						)
				);
			}
			candidateList = QuickSort.quickSort(candidateList);

			// now retrieve elements which produced maximal r
			double r_max = candidateList.get(candidateList.size() - 1).getWeight();
			for (int i = candidateList.size() - 1; i >= 0; i--) {
				if (candidateList.get(i).getWeight() == r_max) {
					rMaxList.add(new WeightedKeyword(candidateList.get(i).getKeyword(), 0));
				}
				//else 	break;
			}

			// now compute the flow to the outerborder in case more than one
			// element produced maximal flow
			if (rMaxList.size() > 1) {
				for (int i = 0; i < rMaxList.size(); i++) {
					rMaxList.get(i).setWeight(
							getFlowFromNodeToSet(c.outerBorder, new Integer(
									rMaxList.get(i).getKeyword())));
				}
				// sort and get elements with maximum
				rMaxList = QuickSort.quickSort(rMaxList);
				double max = rMaxList.get(0).getWeight();
				for (int i = 0; i < rMaxList.size(); i++)
					if (rMaxList.get(i).getWeight() == max) {
						lastCandidates.add(new Integer(rMaxList.get(i).getKeyword()));
						c.addNode(new Integer(rMaxList.get(i).getKeyword()));
					}// else break;
			} // compute new R
			else {
				c.addNode(new Integer(rMaxList.get(0).getKeyword()));
				lastCandidates.add(new Integer(rMaxList.get(0).getKeyword()));
			}
			R_old = R_new;
			R_new = c.getRelativeFlow();
		}

		TreeSet<Integer> finalCluster = new TreeSet<Integer>();
		if( !closedCluster){
			Iterator<Integer> elementsIter = c.getElements().iterator();
			while (elementsIter.hasNext()) {
				Integer cElement = elementsIter.next();
				if (!lastCandidates.contains(cElement))
					finalCluster.add(cElement);
			}
		}else
			finalCluster = c.getElements();
		// This is really a new cluster.
		// Then we need to add it to the cache.
		if(cache!=null)
			return (TreeSet<Integer>) cache.put(finalCluster);
		else 
			return finalCluster;
	}
	/**
	 * Compute cluster for a given seed using optimal version of BorderFlow.
	 */
	public TreeSet<Integer> optimalGetCluster(Integer seed) {
		logger.debug("starting optimalGetCluster for index: " + seed.intValue());
		Cluster c = new Cluster(graph, seed);
		double R_old = -1, R_new = 0;
		ArrayList<Integer> lastCandidates = new ArrayList<Integer>();
		boolean  closedCluster = false;
		long startUserTimeNano = new Date().getTime();
		long startTime = startUserTimeNano;
		while (R_old <= R_new) {
			// check cache
			long time = startUserTimeNano - startTime;
			if(time == 0) time = 1;
			Double cost = 1D/time;
			TreeSet<Integer> elements = null;
			if(cache != null)
				if((elements = (TreeSet<Integer>) cache.get(c.getElements(),cost)) != null)
					return elements;
			startUserTimeNano = new Date().getTime();
			// for each element of the outer border, compute r
			// closed cluster
			if (c.outerBorder.size() == 0){
				closedCluster = true;
				break;
			}

			ArrayList<WeightedKeyword> rMaxList = new ArrayList<WeightedKeyword>();
			ArrayList<WeightedKeyword> candidateList = new ArrayList<WeightedKeyword>();
			lastCandidates = new ArrayList<Integer>();
			Iterator<Integer> iter = c.outerBorder.iterator();

			while (iter.hasNext()) {
				Integer candidate = iter.next();
				// compute copy of c and simulate adding a node
				Cluster c2 = c.getCopy();
				c2.addNode(candidate);
				candidateList.add(
						new WeightedKeyword(
								candidate.intValue() + "",
								c2.getRelativeFlow()
						)
				);
			}

			// now retrieve elements which produced maximal R and add them to
			// rMaxList
			candidateList = QuickSort.quickSort(candidateList);
			double rMax = candidateList.get(0).getWeight();
			for (int i = 0; i < candidateList.size(); i++)
				if (candidateList.get(i).getWeight() == rMax)
					rMaxList.add(candidateList.get(i));

			// now compute the flow to the outerborder in case more than one
			// element produced maximal flow
			if (rMaxList.size() > 1) {
				for (int i = 0; i < rMaxList.size(); i++) {
					rMaxList.get(i).setWeight(
							getFlowFromNodeToSet(c.outerBorder, new Integer(
									rMaxList.get(i).getKeyword())));
				}

				// sort and get elements with maximum
				rMaxList = QuickSort.quickSort(rMaxList);
				double max = rMaxList.get(0).getWeight();
				for (int i = 0; i < rMaxList.size(); i++) {
					if (rMaxList.get(i).getWeight() == max) {
						lastCandidates.add(new Integer(rMaxList.get(i).getKeyword()));
						c.addNode(new Integer(rMaxList.get(i).getKeyword()));
					}// else break;
				}
			} // compute new R
			else {
				c.addNode(new Integer(rMaxList.get(0).getKeyword()));
				lastCandidates.add(new Integer(rMaxList.get(0).getKeyword()));
			}
			R_old = R_new;
			R_new = c.getRelativeFlow();
		}

		TreeSet<Integer> finalCluster = new TreeSet<Integer>();
		if( !closedCluster){
			Iterator<Integer> elementsIter = c.getElements().iterator();
			while (elementsIter.hasNext()) {
				Integer cElement = elementsIter.next();
				if (!lastCandidates.contains(cElement))
					finalCluster.add(cElement);
			}
		}else
			finalCluster = c.getElements();
		// This is really a new cluster.
		// Then we need to add it to the cache.
		if(cache!=null)
			return (TreeSet<Integer>) cache.put(finalCluster);
		else 
			return finalCluster;
	}
	/**
	 * Compute cluster for a given seed using optimal and test one version of BorderFlow.
	 */
	public TreeSet<Integer> optimalGetClusterTestOne(Integer seed) {
		logger.debug("starting optimalGetClusterTestOne for index: " + seed.intValue());
		Cluster c = new Cluster(graph, seed);
		double R_old = -1, rPlusOne = -1, R_new = 0;
		ArrayList<Integer> lastCandidates = new ArrayList<Integer>();
		boolean  closedCluster = false;
		long startUserTimeNano = new Date().getTime();
		long startTime = startUserTimeNano;
		while (R_old <= R_new && R_old <= rPlusOne) {
			// check cache
			long time = startUserTimeNano - startTime;
			if(time == 0) time = 1;
			Double cost = 1D/time;
			TreeSet<Integer> elements = null;
			if(cache != null)
				if((elements = (TreeSet<Integer>) cache.get(c.getElements(),cost)) != null)
					return elements;
			startUserTimeNano = new Date().getTime();
			// for each element of the outer border, compute r
			// closed cluster
			if (c.outerBorder.size() == 0){
				closedCluster = true;
				break;
			}

			ArrayList<WeightedKeyword> rMaxList = new ArrayList<WeightedKeyword>();
			ArrayList<WeightedKeyword> candidateList = new ArrayList<WeightedKeyword>();
			lastCandidates = new ArrayList<Integer>();
			Iterator<Integer>  iter = c.outerBorder.iterator();

			while (iter.hasNext()) {
				Integer candidate = iter.next();
				// compute copy of c and simulate adding a node
				Cluster c2 = c.getCopy();
				c2.addNode(candidate);
				candidateList.add(
						new WeightedKeyword(
								candidate.intValue() + "",
								c2.getRelativeFlow()
						)
				);
			}

			// now retrieve elements which produced maximal R and add them to
			// rMaxList
			candidateList = QuickSort.quickSort(candidateList);
			double rMax = candidateList.get(0).getWeight();
			for (int i = 0; i < candidateList.size(); i++)
				if (candidateList.get(i).getWeight() == rMax)
					rMaxList.add(candidateList.get(i));

			// now compute the flow to the outerborder in case more than one
			// element produced maximal flow
			if (rMaxList.size() > 1) {
				for (int i = 0; i < rMaxList.size(); i++) {
					rMaxList.get(i).setWeight(
							getFlowFromNodeToSet(c.outerBorder, new Integer(
									rMaxList.get(i).getKeyword())));
				}

				// sort and get elements with maximum
				rMaxList = QuickSort.quickSort(rMaxList);
				double max = rMaxList.get(0).getWeight();
				boolean testOne = false;
				for (int i = 0; i < rMaxList.size(); i++) {
					if (rMaxList.get(i).getWeight() == max) {
						lastCandidates.add(new Integer(rMaxList.get(i).getKeyword()));
						c.addNode(new Integer(rMaxList.get(i).getKeyword()));
						if (!testOne) {
							rPlusOne = c.getRelativeFlow();
							testOne = true;
						}
					}// else break;
				}
			} // compute new R
			else {
				c.addNode(new Integer(rMaxList.get(0).getKeyword()));
				lastCandidates.add(new Integer(rMaxList.get(0).getKeyword()));
				rPlusOne = c.getRelativeFlow();
			}
			R_old = R_new;
			R_new = c.getRelativeFlow();
		}

		TreeSet<Integer> finalCluster = new TreeSet<Integer>();
		if( !closedCluster){
			Iterator<Integer> elementsIter = c.getElements().iterator();
			while (elementsIter.hasNext()) {
				Integer cElement = elementsIter.next();
				if (!lastCandidates.contains(cElement))
					finalCluster.add(cElement);
			}
		}else
			finalCluster = c.getElements();
		// This is really a new cluster.
		// Then we need to add it to the cache.
		if(cache!=null)
			return (TreeSet<Integer>) cache.put(finalCluster);
		else 
			return finalCluster;
	}
	/**
	 * Compute cluster for a given seed.
	 */
	public TreeSet<Integer> knnCluster(Integer seed, int k) {
		Cluster c = new Cluster(graph, seed);
		int size = 0;
		while (size < k) {
			// for each element of the outer border, compute r
			// closed cluster
			if (c.outerBorder.size() == 0)
				return c.getElements();

			ArrayList<WeightedKeyword> candidateList = new ArrayList<WeightedKeyword>();

			Iterator<Integer> iter =  c.outerBorder.iterator();
			while (iter.hasNext()) {
				Integer candidate =  iter.next();
				// compute r(candidate)
				candidateList.add(new WeightedKeyword(
						candidate.intValue() + "",
						getAbsoluteFlow(c, candidate)));
			}
			candidateList = QuickSort.quickSort(candidateList);

			int counter = 0;
			while (size < k && counter < candidateList.size()) {
				c.addNode(new Integer(candidateList.get(counter).getKeyword()));
				size++;
				counter++;
			}
		}
		TreeSet<Integer> elements = c.getElements();
		return elements;
	}
	/**
	 * Set database variables.
	 */
	public void setDatabaseVariables(String _ip, String _database, String _user, String _password) {
		ip = _ip;
		database = _database;
		user = _user;
		password = _password;
	}
	/**
	 * Get connection to database
	 */
	public Connection getConnection() {
		con = null;
		try {
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			con = DriverManager.getConnection("jdbc:mysql://" + ip + "/"
					+ database, user, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}
	/** Clusters to the database found under connection */
	public void clusterToDatabase(double connThreshold) {
		//cache = new ClusterCache(false);
		// get set of seeds
		double connectivity = 0;
		double threshold;
		HashMap<Integer, Integer> connectMap = new HashMap<Integer, Integer>();
		HashMap<TreeSet<Integer>, TreeSet<Integer>> clusterSeedMap = new HashMap<TreeSet<Integer>, TreeSet<Integer>>();
		int size = graph.index.size();
		int neighbours = 0;
		for (int i = 0; i < size; i++) {
			neighbours = graph.getNeighbours(i).size();
			connectivity = connectivity + neighbours;
			connectMap.put(new Integer(i), new Integer(neighbours));
		}
		// System.out.println("ConnectMap = "+connectMap);
		connectivity = connectivity / size;
		threshold = connectivity * connThreshold;
		System.out.println("Threshold = " + threshold + " nodes");
		Integer integer;
		Iterator iter, iter2;
		String query;

		try {
			// write index
			Integer index;
			iter = graph.reverseIndex.keySet().iterator();
			getConnection();
			java.sql.Statement stmt = con.createStatement();
			// write terms
			System.out.println("Writing terms in database ...");
			while (iter.hasNext()) {
				index = (Integer) iter.next();
				query = "INSERT into category(catname, ID) values('"
					+ graph.reverseIndex.get(index) + "', " + index.intValue()
					+ ");";
				stmt.executeUpdate(query);
			}

			// write similarity matrix
			System.out.println("Writing similarity matrix in database ...");
			double value;
			for (int i = 0; i < graph.index.size(); i++) {
				for (int j = i + 1; j < graph.index.size(); j++) {
					value = graph.getEdgeWeight(new Integer(j), new Integer(i));
					if (value > 0) {
						query = "INSERT into similarity(catid1, catid2, similarity) values("
							+ i + ", " + j + ", " + value + ");";
						stmt.executeUpdate(query);
					}
				}
			}

			closeConnection();
			// for each seed
			TreeSet<Integer> cluster;
			logger.info("Clustering ...");
			for (int i = 0; i < size; i++) {
				integer = new Integer(i);
				if (connectMap.get(integer).doubleValue() <= threshold) {
					System.out.println("Seed = " + i);
					cluster = heuristicGetCluster(integer);
					if (clusterSeedMap.containsKey(cluster)) {
						clusterSeedMap.get(cluster).add(integer);
					} else {
						TreeSet<Integer> set = new TreeSet<Integer>();
						set.add(integer);
						clusterSeedMap.put(cluster, set);
					}
				}
			}

			System.out.println("Writing out clusters ...");
			getConnection();
			// write output to database

			// clean database
			// query = "DELETE from category;";
			// stmt.executeUpdate(query);
			// query = "DELETE from cluster;";
			// stmt.executeUpdate(query);
			// query = "DELETE from catincluster;";
			// stmt.executeUpdate(query);
			// query = "DELETE from seeds;";
			// stmt.executeUpdate(query);
			// query = "DELETE from similarity;";
			// stmt.executeUpdate(query);

			// write clusters in database
			java.sql.ResultSet set;
			stmt = con.createStatement();
			int counter = 0;
			iter = clusterSeedMap.keySet().iterator();
			double silhouette, R;
			String s, split[];
			TreeSet<Integer> seeds;
			Integer seed;
			while (iter.hasNext()) {
				cluster = (TreeSet<Integer>) iter.next();
				s = getSilhouetteAndRelativeFlow(cluster);
				split = s.split("\t");
				silhouette = new Double(split[0]).doubleValue();
				R = new Double(split[1]).doubleValue();
				query = "INSERT into cluster(ID, R, clusterlevel, silhouette, nodecount) values("
					+ counter
					+ ", "
					+ R
					+ ", 0, "
					+ silhouette
					+ ", "
					+ cluster.size() + ")";
				stmt.executeUpdate(query);

				// write categories in cluster
				iter2 = cluster.iterator();
				Integer category;
				while (iter2.hasNext()) {
					category = (Integer) iter2.next();
					query = "INSERT into catincluster(clusterID, catID) values("
						+ counter + ", " + category.intValue() + ")";
					stmt.executeUpdate(query);
				}

				// write seeds in database
				seeds = clusterSeedMap.get(cluster);
				iter2 = seeds.iterator();
				while (iter2.hasNext()) {
					seed = (Integer) iter2.next();
					query = "INSERT into seeds(clusterID, seedID) values("
						+ counter + ", " + seed.intValue() + ")";
					stmt.executeUpdate(query);
				}
				counter++;
			}
			closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Close connection to database.
	 */
	public void closeConnection() {
		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public double compareSet(Set<Integer> set1, Set<Integer> set2) {

		double cnt = 0;
		double size = Math.max(set1.size(), set2.size());

		for (Integer o : set1)
			if (set2.contains(o))
				cnt++;

		return cnt / size;
	}
	/**
	 * Computes the inverse relative flow to make sure that no division by zero
	 * is carried out
	 */
	public double getInverseRelativeFlow(Cluster c, Integer node) {

		TreeSet<Integer> innerBorder = c.innerBorder;
		Iterator<Integer> iter = graph.getNeighbours(node).iterator();
		Integer neighbour;
		double flowIn = 0, flowOut = 0;

		while (iter.hasNext()) {
			neighbour =  iter.next();
			if(innerBorder.contains(neighbour) == true )
				flowIn += graph.getEdgeWeight(neighbour, node);
			else
				flowOut += graph.getEdgeWeight(neighbour, node);
		}
		return flowOut / flowIn;
	}
	/**
	 * Computes the inverse relative flow to make sure that no division by zero
	 * is carried out
	 */
	public double getAbsoluteFlow(Cluster c, Integer node) {

		TreeSet<Integer> outerBorder = c.outerBorder;
		Iterator<Integer> iter = graph.getNeighbours(node).iterator();

		double flow = 0;
		while (iter.hasNext()) {
			Integer neighbour = iter.next();
			if (outerBorder.contains(neighbour))
				flow += graph.getEdgeWeight(neighbour, node);
		}
		return flow;
	}
	/**
	 * Computes the silhouette for a set of nodes
	 */
	public String getSilhouetteAndRelativeFlow(TreeSet<Integer> nodes) {
		Iterator<Integer> iter = nodes.iterator();
		if (iter.hasNext()) {
			Cluster c = new Cluster(graph, iter.next());
			while (iter.hasNext())
				c.addNode( iter.next());
			return c.getSilhouette() + "\t" + c.getRelativeFlow();
		}
		else return "Nan\tNan";
	}
	/**
	 */
	public double getRelativerFlow(TreeSet<Integer> nodes) {
		Iterator<Integer> iter = nodes.iterator();
		Cluster c;
		if (iter.hasNext()) {
			c = new Cluster(graph, iter.next());
			while (iter.hasNext())
				c.addNode(iter.next());
			return c.getRelativeFlow();
		}
		else return -1;
	}
	/**
	 * Compute flow from node to set of nodes
	 */
	public double getFlowFromNodeToSet(TreeSet<Integer> nodeSet, Integer node) {
		Iterator<Integer> iter = nodeSet.iterator();
		double flow = 0;
		while (iter.hasNext())
			flow += graph.getEdgeWeight(node,  iter.next());
		return flow;
	}
}