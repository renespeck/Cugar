package de.uni_leipzig.cugar.cluster;

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

import prefuse.data.Table;
import static de.uni_leipzig.cugar.cluster.ClusterTableSettings.CLUSTER_COLUMN_NAME;

// TODO: parameter in properties
// TODO: parameter update strategy missing -u ?
/**
 */
public class ClusterAlgorithmCW extends ClusterAlgorithm {	


	public ClusterAlgorithmCW(){
		config_A = new String[]{ "iterations","30", "1","10", "20","30","50","100","150","500"};		
		config_B = new String[]{ "algo. option","top","dist_nolog","dist_log","vote 0","vote 0.1","vote 0.2","vote 0.3","vote 0.4","vote 0.5","vote 0.6","vote 0.7","vote 0.8","vote 0.9","vote 1.0"};	 
		config_C = new String[]{ "mutation rate", "constant 0.0", "constant 0.5","constant 1.0","constant 1.5","constant 2.0","constant 3.0","constant 2.0","constant 5.0", "dec 0.0","dec 1","dec 2","dec 3","dec 4","dec 5"};	 
		//config_D = new String[]{ "update strategy","constant", "stepwise"};
		config_D = new String[]{ "keep color rate","0.0","0.01", "0.05","0.1","0.2","0.3","0.4","0.5","0.6","0.7","0.8","0.9","1.0"};
	}	
	@Override
	protected Table clustering(String[] seeds, double threshold, String values_A, String values_B, String values_C, String values_D){

		final String sep = getSeparator();
		final String  sep_cw = "\t";
		// init index
		initializeIndex(filename,sep);						
		// write files		
		String edgelist = filename + ".edgeslist.cw";
		String nodelist = filename + ".nodeslist.cw";
		String clusterlist = filename + ".clusterlist.cw";

		int max_weight = 0;		
		try {
			// write edges
			BufferedReader reader = new BufferedReader(new FileReader(filename));  
			PrintWriter writer = new PrintWriter(new BufferedWriter( new FileWriter(edgelist)));
			String line = "";			          
			while ((line = reader.readLine()) != null) {
				if (line.contains(sep)) {
					String split[] = line.split(sep);
					int sim = 0;
					try{
						sim = Double.valueOf(split[2]).intValue();
					}catch( NumberFormatException e){
						logger.warn("Wrong number format for edges!");
						logger.warn("Set edge 2eight to 1.");
						sim = 1;
					}//
					if(sim > max_weight)
						max_weight = sim;
					writer.append(String.valueOf(index.get(split[0])));
					writer.append(sep_cw);
					writer.append(String.valueOf(index.get(split[1])));
					writer.append(sep_cw);
					writer.append(String.valueOf(sim));
					writer.append("\n");
					//undirected
					writer.append(String.valueOf(index.get(split[1])));
					writer.append(sep_cw);
					writer.append(String.valueOf(index.get(split[0])));
					writer.append(sep_cw);
					writer.append(String.valueOf(sim));
					writer.append("\n");					
				}
			}
			reader.close();
			writer.close();
			// write nodes
			writer = new PrintWriter(new BufferedWriter( new FileWriter(nodelist)));
			TreeSet<Integer> sorted_ids = new TreeSet<Integer>(index.values());
			for(Integer id : sorted_ids)
				writer.println(id + sep_cw + reverseIndex.get(id));
			writer.close();			
		} catch (IOException ex) {
			logger.warn("CW read input file Error.");
		} 	
		//		ChineseWhispers cw = new ChineseWhispers();
		//		cw.setCWParameters(args_min_weight, algOption1, algOption2, args_keep, args_mut1, args_mut2, args_update, args_iterations, isFileOrDBOut)
		//		cw.setCWGraph(nodelist,edgelist);
		//		cw.run();		
		//		cw.show_clusters();
		//		cw.getColorVertex( , );
		//		cw.max_node_nr

		//		   -F Use files specified by -i as input.
		//		   -i Use files as input.
		//		    filename1  The node list 2-col.
		//		    filename2  The edge list 3-col.

		//		   -a Sets the algorithm options
		//		    "top"
		//		    "dist_nolog"
		//		    "dist_log"
		//		    "vote x" with x in [0.0,1.0]

		//		   -t Weight threshold (default 0)
		//		   -k Keep class rate (default 0.0)
		//		   -m Mutation mode [dec|constant] value(pos.real)
		//		   -d Number of iterations x>0 (default x=20).
		//		   -o Writes clustering to filename.
		//		    filename    The filename for output.
		//		   -S Do not renumber input./
		int t = 0;
		if(threshold == -1)
			t = max_weight;
		else
			t = new Double(Math.rint(threshold * max_weight)).intValue();		
		int d = Integer.valueOf(values_A);		
		String m = values_C;	
		String a = values_B;
		//String u = values_D;
		String k = values_D;
		String args = "-S -F -i " + nodelist+ " " + edgelist + " -o " + clusterlist + 
		" -d " + d + 
		" -t " + t + 
		" -m " + m +
		" -a " + a +
	//	" -u " + u;   ??
		" -k " + k;		
		de.uni_leipzig.asv.toolbox.ChineseWhispers.main.Start.main(args.split(" "));
		// read clusters
		// TODO: read more information from file to table
		Map<Integer,TreeSet<String>> clusters = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(clusterlist));  
			clusters = new HashMap<Integer,TreeSet<String>>() ;
			String line = "";	
			String log_message = "results of CW:\nnode ID\tnode label\tclass ID\tclass 1 ID\tclass 1 %\tclass2 ID\tclass 2 %\t \n";
			while ((line = reader.readLine()) != null) {
				log_message += line + "\n";				
				if (line.contains(sep_cw)) {
					String split[] = line.split(sep_cw);
						Integer cluster_id = Integer.valueOf(split[2]);
						if(clusters.get(cluster_id) != null){
							clusters.get(cluster_id).add(split[1]);
						}else{
							TreeSet<String> cluster = new TreeSet<String>();
							cluster.add(split[1]);
							clusters.put(cluster_id, cluster);
						}
				}
			}
			reader.close();
			logger.info(log_message);
		}catch (IOException ex) {
			logger.warn("CW read cluster file Error.");
		} 
		// delete files
		new File(clusterlist).delete();
		new File(clusterlist+".read").delete();
		new File(edgelist).delete();
		new File(nodelist).delete();
		new File(nodelist).delete();
		new File(nodelist+".idx").delete();
		new File(nodelist+".bin").delete();
		new File(edgelist+".meta").delete();
		new File(edgelist+".idx").delete();
		new File(edgelist+".bin").delete();				
		// prepare table
		Table table = getTable();
		// read in table
		if(clusters != null)
		for(TreeSet<String> cluster : clusters.values())										
			table.set(table.addRow(), CLUSTER_COLUMN_NAME,cluster);			
		return table;
	}
	@Override
	public String getName() {
		return "Chinese Whispers";
	}
}        