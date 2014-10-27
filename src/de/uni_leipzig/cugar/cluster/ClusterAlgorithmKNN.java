package de.uni_leipzig.cugar.cluster;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.Map.Entry;

import prefuse.data.Table;
import de.uni_leipzig.bf.cluster.BorderFlow;

import de.uni_leipzig.cugar.gui.LogManager;
import de.uni_leipzig.cugar.harden.HardenMaxQuality;
import de.uni_leipzig.cugar.harden.HardenSuperset;
import de.uni_leipzig.cugar.harden.QualityMeasureRelativeFlow;
import de.uni_leipzig.cugar.harden.QualityMeasureSilhouette;
import static de.uni_leipzig.cugar.cluster.ClusterTableSettings.CLUSTER_COLUMN_NAME;
/**
 * Is a {@link de.uni_leipzig.cugar.cluster.ClusterAlgorithm} and an adapt for knn algorithm of {@link de.uni_leipzig.bf.cluster.BorderFlow} class.
 * Implements the {@link #clustering(String[], double, String, String, String, String)} method and overrides default available settings in abstract class. 
 */
public class ClusterAlgorithmKNN extends ClusterAlgorithm{

	private final int k = 100;
	/**
	 * Builds new available settings.
	 */
	public ClusterAlgorithmKNN() {
		BorderFlow.logger.addAppender(LogManager.instance().getLogPanelAppender());
		config_A = new String[]{ "Harden", "Superset", "max. relative flow","max. silhouette", "off"};	
		config_B = new String[k + 1];
		config_B[0] = "k";
		for(int i = 1 ; i <=k ; i++)
			config_B[i] = String.valueOf(i);
	}
	/** implemented abstract method*/
	@Override
	protected Table clustering(String[] seeds, double threshold, String values_A, String values_B, String values_C, String values_D){
		BorderFlow bf =  new BorderFlow(filename, getSeparator(),null);			
		if(seeds == null)
			clusterSeedMap = bf.knn(threshold, Integer.parseInt(values_B));
		else 
			clusterSeedMap =  bf.knn(seeds, Integer.parseInt(values_B));
		// hardening default
		if(values_A.equals(config_A[1]))
			hardening(new HardenSuperset(),bf.getGraph());

		if(values_A.equals(config_A[2]))
			hardening(new HardenMaxQuality(),new QualityMeasureRelativeFlow(),bf.getGraph());

		if(values_A.equals(config_A[3]))
			hardening(new HardenMaxQuality(),new QualityMeasureSilhouette(),bf.getGraph());	
		
		
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
			t.set(row, "Relative Flow",s[1]);
		}
		return t;			
	}
	@Override
	public String getName() {
		return "K Nearest Neighbors";
	}
}