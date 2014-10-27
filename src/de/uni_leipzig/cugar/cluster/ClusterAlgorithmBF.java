package de.uni_leipzig.cugar.cluster;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.Map.Entry;

import prefuse.data.Table;

import de.uni_leipzig.bf.cluster.BorderFlow;
import de.uni_leipzig.cc.cache.SGDStar;
import de.uni_leipzig.cc.coyotecache.CoyoteCache;
import de.uni_leipzig.cc.seeds.RP;
import de.uni_leipzig.cc.seeds.InitialOrder;
import de.uni_leipzig.cugar.gui.LogManager;
import de.uni_leipzig.cugar.harden.HardenMaxQuality;
import de.uni_leipzig.cugar.harden.HardenSuperset;
import de.uni_leipzig.cugar.harden.QualityMeasureRelativeFlow;
import de.uni_leipzig.cugar.harden.QualityMeasureSilhouette;

import static de.uni_leipzig.cugar.cluster.ClusterTableSettings.CLUSTER_COLUMN_NAME;
/**
 * Is a {@link de.uni_leipzig.cugar.cluster.ClusterAlgorithm} and an adapt for BorderFlow algorithm of {@link de.uni_leipzig.bf.cluster.BorderFlow} class.
 * Implements the {@link #clustering(String[], double, String, String, String, String)} method and overrides default available settings in abstract class. 
 */
public class ClusterAlgorithmBF extends ClusterAlgorithm{	
	/** new available settings */
	public ClusterAlgorithmBF(){

		BorderFlow.logger.addAppender(LogManager.instance().getLogPanelAppender());
		config_A = new String[]{ "Harden", "Superset", "max. relative flow","max. silhouette", "off"};
		config_B = new String[]{ "Cache", "SGD* + RP", "SGD*", "off" };
		//config_C = new String[]{ "Heuristic" , "on", "off" };
		config_C = new String[]{ "Cache size" , "500", "1000","2000","4000","8000" };
		config_D = new String[]{ "Test One" , "on", "off" };		
	}
	/** implemented abstract method*/
	@Override
	protected Table clustering(	String[] seeds, double threshold, String values_A, String values_B, String values_C, String values_D){

		// heuristic 
		boolean he = true;
		//if(values_C.equals(config_C[1]))
		//	he = true;		
		
		// test for one termination
		boolean o = false;
		if(values_D.equals(config_D[1]))
			o = true;		
		
		// cache
		CoyoteCache coyoteCache = null;		
		
		if(values_B.equals(config_B[1]))//"SGD* + RP"
			 coyoteCache = new CoyoteCache(new SGDStar(new Integer(values_C),1),new RP());
		else if(values_B.equals(config_B[2]))//"SGD*"
			coyoteCache = new CoyoteCache(new SGDStar(new Integer(values_C),1),new InitialOrder());
		
		System.out.println("size:" + new Integer(values_C));
		BorderFlow bf =  new BorderFlow(filename, getSeparator(),coyoteCache);	
		// label to index 
		//initializeIndex(filename, getSeparator());
		
		// clustering
		if(seeds != null)
			clusterSeedMap = bf.cluster(seeds, o, he);
		else
			clusterSeedMap = bf.cluster(threshold, o, he);

		// hardening default
		if(values_A.equals(config_A[1]))
			hardening(new HardenSuperset(),bf.getGraph());

		if(values_A.equals(config_A[2]))
			hardening(new HardenMaxQuality(),new QualityMeasureRelativeFlow(),bf.getGraph());

		if(values_A.equals(config_A[3]))
			hardening(new HardenMaxQuality(),new QualityMeasureSilhouette(),bf.getGraph());		
		
		// index to label
		//clusterSeedMapLabels = getLabels(clusterSeedMap);
		clusterSeedMapLabels = bf.getLabels(clusterSeedMap);
		
		// prepare table
		Table t = getTable();	
		t.addColumn("Seeds", TreeSet.class);
		t.addColumn("Silhouette", String.class);
		t.addColumn("Relative Flow", String.class);

		// read in table
		Iterator<Entry<TreeSet<Integer>, TreeSet<Integer>>> iter = clusterSeedMap.entrySet().iterator();
		for(Entry<TreeSet<String>, TreeSet<String>> entry : clusterSeedMapLabels.entrySet()){
			int row = t.addRow();										
			t.set(row, CLUSTER_COLUMN_NAME,  entry.getKey());				
			t.set(row, "Seeds",  entry.getValue());		
			String[] s =  bf.getSilhouetteAndRelativeFlow(iter.next().getKey()).split("\t");
			t.set(row, "Silhouette",s[0]);				
			t.set(row, "Relative Flow", s[1]);
		}
		return t;		
	}
	@Override
	public String getName() {
		return "BorderFlow";
	}
}