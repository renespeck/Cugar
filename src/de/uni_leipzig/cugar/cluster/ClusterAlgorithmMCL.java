package de.uni_leipzig.cugar.cluster;

import static de.uni_leipzig.cugar.cluster.ClusterTableSettings.CLUSTER_COLUMN_NAME;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import net.sf.javaml.clustering.mcl.MCL;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.AbstractSimilarity;
import net.sf.javaml.distance.CosineSimilarity;
import net.sf.javaml.distance.JaccardIndexSimilarity;
import net.sf.javaml.distance.MaxProductSimilarity;
import net.sf.javaml.distance.NormalizedEuclideanSimilarity;
import net.sf.javaml.distance.RBFKernel;
import net.sf.javaml.distance.dtw.DTWSimilarity;
import net.sf.javaml.tools.data.FileHandler;
import prefuse.data.Table;


public class ClusterAlgorithmMCL extends ClusterAlgorithm {	

	public ClusterAlgorithmMCL(){
		config_A = new String[]{ "Similarity", "DTW", "RBFKernel", "MaxProduct", "NormalizedEuclidean","JaccardIndex", "Cosine"};	
	}	
	@Override
	protected Table clustering(
			String[] seeds, double threshold, String values_A, String values_B, String values_C, String values_D
	){
		final String sep = getSeparator();
		// init index
		if(logger.isDebugEnabled())
			logger.debug("indexing ... ");
		initializeIndex(filename,sep);	
		String tmppath = 
			System.getProperty("java.io.tmpdir") + 
			System.getProperty("file.separator") + 
			"cvk.tmpfile."; 

		String indexFile = tmppath + "index.mcl";
		try {
			// write edges
			if(logger.isDebugEnabled())
				logger.debug("writing tmp file ... ");
			BufferedReader reader = new BufferedReader(new FileReader(filename));  
			PrintWriter writer = new PrintWriter(new BufferedWriter( new FileWriter(indexFile)));
			String line = "";			          
			while ((line = reader.readLine()) != null) {
				if (line.contains(sep)) {
					String split[] = line.split(sep);					
					String w = split[2] != null ? split[2] : "1.0";										
					writer.write(index.get(split[0]) + sep + index.get(split[1]) + sep + w + "\n");						
					writer.write(index.get(split[1]) + sep + index.get(split[0]) + sep + w + "\n");	
				}				
			}
			writer.close();
		}catch(IOException ioe){
			ioe.printStackTrace();			
		}
		if(logger.isDebugEnabled())
			logger.debug("loading data from tmp file ... ");
		Dataset ds = null;
		try {
			ds = FileHandler.loadDataset(new File(indexFile), 2, sep);
		} catch (IOException e) {
			e.printStackTrace();
		}
		AbstractSimilarity as = new  CosineSimilarity();
		if(values_A.equals("DTW"))
			as = new  DTWSimilarity();
		if(values_A.equals("RBFKernel"))
			as = new  RBFKernel();
		if(values_A.equals("MaxProduct"))
			as = new  MaxProductSimilarity();
		if(values_A.equals("NormalizedEuclidean"))
			as = new  NormalizedEuclideanSimilarity(ds);
		if(values_A.equals("JaccardIndex"))
			as = new  JaccardIndexSimilarity();
		if(values_A.equals("Cosine"))
			as = new  CosineSimilarity();

		if(logger.isDebugEnabled())
			logger.debug("clustering ... ");
		Dataset[] datasets = new MCL(as).cluster(ds);
		if(logger.isDebugEnabled())
			logger.debug("clustering done. Writing results ... ");

		Map<Integer,TreeSet<String>> clusters = new HashMap<Integer,TreeSet<String>>();

		for(Dataset dataset : datasets){
			TreeSet<String> rindexes = new TreeSet<String>();
			TreeSet<Double> indexes = new TreeSet<Double>();
			
			for(Instance instance : dataset)
				indexes.addAll(instance.values());

			for(Double dindex : indexes)
				rindexes.add(reverseIndex.get(dindex.intValue()));

			clusters.put(clusters.size(),rindexes);	
		}
		// prepare table
		Table table = getTable();
		// read in table
		if(clusters != null)
			for(TreeSet<String> cluster : clusters.values())										
				table.set(table.addRow(), CLUSTER_COLUMN_NAME, cluster);	
		if(logger.isDebugEnabled())
			logger.debug("clustering end. ");
		return table;
	}
	@Override
	public String getName() {
		return "Markov Clustering (a)";
	}
}        