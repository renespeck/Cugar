package de.uni_leipzig.cugar.cluster;

import java.util.ArrayList;
import java.util.TreeSet;

import de.uni_leipzig.mcl.cluster.MarkovClustering;
import de.uni_leipzig.mcl.cluster.SparseMatrixLabeled;

import static de.uni_leipzig.cugar.cluster.ClusterTableSettings.CLUSTER_COLUMN_NAME;

import prefuse.data.Table;
/**
 * Is a {@link de.uni_leipzig.cugar.cluster.ClusterAlgorithm} and an adaptation for mcl algorithm of {@link de.uni_leipzig.mcl.cluster.MarkovClustering} class.
 * Implements the {@link #clustering(String[], double, String, String, String, String)} method and overrides default available settings in abstract class.
 */
public class ClusterAlgorithmMCL2 extends ClusterAlgorithm {

	public ClusterAlgorithmMCL2(){
		config_A = new String[]{"Inflation","2.0","1.9","1.8","1.7","1.6","1.5","1.4","1.3","1.2","1.1"};
		//config_B = new String[]{"Residual","0.001","0.005","0.01"};
	}
	@Override
	protected Table clustering(
			String[] seeds, double threshold, String values_A, String values_B, String values_C, String values_D
	){
		//double maxResidual = Double.parseDouble(values_B);
		double maxResidual = 0.001;
		double gammaExp = Double.parseDouble(values_A);
		double loopGain = 0.00;
		double zeroMax = 0.001;

		SparseMatrixLabeled matrix = SparseMatrixLabeled.loadMatrix(filename, getSeparator());
		matrix.setMatrix(matrix.getMatrix().transpose());
		matrix.setMatrix(new MarkovClustering().run(matrix.getMatrix(), maxResidual, gammaExp, loopGain, zeroMax));
		// prepare table
		Table table = getTable();
		ArrayList<TreeSet<String>> clusterList = matrix.getCluster();
		// read in table
		for(TreeSet<String> cluster : clusterList)
			table.set(table.addRow(), CLUSTER_COLUMN_NAME,cluster);
		return table;
	}
	@Override
	public String getName() {
		return "Markov Clustering (b)";
	}
}