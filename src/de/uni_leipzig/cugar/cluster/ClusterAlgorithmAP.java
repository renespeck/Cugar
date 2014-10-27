package de.uni_leipzig.cugar.cluster;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;

import listeners.ConsoleIterationListener;

import affinitymain.InteractionData;
import algorithm.abs.AffinityPropagationAlgorithm;
import algorithm.abs.AffinityPropagationAlgorithm.AffinityConnectingMethod;
import algorithm.smart.SmartPropagationAlgorithm;

import prefuse.data.Table;

import static de.uni_leipzig.cugar.cluster.ClusterTableSettings.CLUSTER_COLUMN_NAME;
//TODO:
// use preferences parameter with slider for threshold parameter
// where is the noise parameter descried in the manual?
/**
 * This class is a modification of RunAlgorithm class from de.uni_leipzig.ap.affinitymain package 
 */
public class ClusterAlgorithmAP extends ClusterAlgorithm{

	private AffinityPropagationAlgorithm smartPropagationAlgorithm;
	private boolean takeLog = false; // we need this?
	private boolean refine = true;
	private Integer steps = null;  // we need this?
	private AffinityConnectingMethod connMode = AffinityPropagationAlgorithm.AffinityConnectingMethod.ORIGINAL;
	/**
	 * Builds new available settings.
	 */
	public ClusterAlgorithmAP() {

		config_A = new String[]{ "lambda", "0.5","0","0.1","0.2","0.3","0.4","0.6","0.7","0.8","0.9","0.99"};	
		config_B = new String[]{ "iterations","300", "1","10", "20","30","50","100","150","500"};	 
		config_C = new String[]{ "preferences", "0.5", "0","0.25","0.75","1.0","1.5","2","3","5","10"};	 
		config_D = new String[]{ "convits","30", "0","5", "10", "20","50","100","150","300"};		

	}
	/** implemented abstract method*/
	@Override
	protected Table clustering(String[] p_seeds, double threshold, String values_A, String values_B, String values_C, String values_D){

		double lambda = Double.parseDouble(values_A);
		int iterations = Integer.parseInt(values_B);
		double preferences = Double.parseDouble(values_C);;        
		Integer convits = Integer.parseInt(values_D);

		smartPropagationAlgorithm = new SmartPropagationAlgorithm();
		smartPropagationAlgorithm.setLambda(lambda);
		smartPropagationAlgorithm.setIterations(iterations);
		smartPropagationAlgorithm.setConvits(convits);
		smartPropagationAlgorithm.setSteps(steps);
		smartPropagationAlgorithm.setRefine(refine);
		smartPropagationAlgorithm.setConnectingMode(connMode);
		smartPropagationAlgorithm.addIterationListener(new ConsoleIterationListener(iterations));

		// read file
		Collection<InteractionData> ints = new HashSet<InteractionData>();
		// build index
		String sep = getSeparator();
		initializeIndex(filename,sep);
		// read file
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));             
			String line = "";			          
			while ((line = reader.readLine()) != null) {
				if (line.contains(sep)) {
					String split[] = line.split(sep);
					Double sim = Double.parseDouble(split[2]);
					ints.add(new InteractionData(String.valueOf(index.get(split[0])), String.valueOf(index.get(split[1])), sim));
					ints.add(new InteractionData(String.valueOf(index.get(split[1])),String.valueOf(index.get(split[0])), sim));   //undirected
				}
			}
			reader.close();

		} catch (IOException ex) {
			logger.warn("AP read file Error.");
		} 
		//
		smartPropagationAlgorithm.setN(index.size() + 1); 
		smartPropagationAlgorithm.init();

		for (InteractionData intData : ints) {
			Double val;
			if (takeLog) {
				if (intData.getSim() > 0)
					val = Math.log(intData.getSim());
				else
					val = Double.valueOf(0);				
			} else
				val = intData.getSim();

			Integer source = Integer.valueOf(intData.getFrom());
			Integer target = Integer.valueOf(intData.getTo());
			smartPropagationAlgorithm.setSimilarityInt(source, target, val);
		}
		Double pref;
		if (takeLog) {
			if (preferences > 0) 
				pref = Math.log(preferences);
			else 
				pref = Double.valueOf(0);			
		}else 
			pref = preferences;

		smartPropagationAlgorithm.setConstPreferences(pref);
		// get cluster
		Map<Integer, Integer> clusters = smartPropagationAlgorithm.doClusterInt();            
		Collection<Integer> node_ids = clusters.keySet();
		TreeSet<Integer> cluster_ids = new TreeSet<Integer>();
		cluster_ids.addAll(clusters.values());
	
		clusterSeedMap = new HashMap<TreeSet<Integer>, TreeSet<Integer>>() ;
		if (clusters != null) {
			// all clusters 
			for (Integer cluster_id : cluster_ids){		
				// ignore 1st? 1st line in file was 0 every time!!
				// we don't use the index/center value of 0!!
				if(reverseIndex.get(cluster_id) != null){
					TreeSet<Integer> cluster = new TreeSet<Integer>();
					TreeSet<Integer> seeds = new TreeSet<Integer>();
					seeds.add(cluster_id);        
					// all nodes
					for(Integer node_id : node_ids)
						if(clusters.get(node_id) == cluster_id)
							cluster.add(node_id);            					
					clusterSeedMap.put(cluster, seeds);      
				}
			}
		}		
		clusterSeedMapLabels = getLabels(clusterSeedMap);
		// prepare table
		Table table = getTable();	
		table.addColumn("Seeds", TreeSet.class);
		// read in table
		for(Entry<TreeSet<String>, TreeSet<String>> entry : clusterSeedMapLabels.entrySet()){
			int row = table.addRow();
			table.set(row, CLUSTER_COLUMN_NAME,  entry.getKey());
			table.set(row, "Seeds",  entry.getValue());
		}
		return table;			
	}
	@Override
	public String getName() {
		return "Affinity Propagation";
	}
}