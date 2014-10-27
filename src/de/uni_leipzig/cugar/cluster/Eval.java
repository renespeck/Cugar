package de.uni_leipzig.cugar.cluster;

import static de.uni_leipzig.cugar.cluster.ClusterTableSettings.CLUSTER_COLUMN_NAME;
import static de.uni_leipzig.cugar.data.GraphReaderSettings.EDGE_WEIGHT;
import static de.uni_leipzig.cugar.data.GraphReaderSettings.NODE_NAME;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.util.collections.IntIterator;
/**
 * Only undirected supported.
 */
public class Eval{
	// list for median
	private static List<Double> m_list = new ArrayList<Double>();	
	// median
	public static double median(List<Double> list){
		java.util.Collections.sort(list);
		int n = list.size();
		if(n%2==0)			
			return (list.get((n/2)-1) + list.get(n/2 + 1 - 1))/2D;	
		else
			return list.get((n+1)/2 - 1);	
	}
	// median for relative flow
	public static double getRelativeFlow(Graph graph, Table clusterTable){
		m_list.clear();
		IntIterator rows = clusterTable.rows();
		while(rows.hasNext())
			m_list.add(getRelativeFlow(graph,((TreeSet<String>) clusterTable.get(rows.nextInt(), CLUSTER_COLUMN_NAME))));		
		return median(m_list);
	}	

	public static double getRelativeFlow(Graph graph, TreeSet<String> p_cluster){
		TreeSet<Integer> cluster = clusterIndexes(graph, p_cluster);			
		if(cluster.size()<=1)
			return 0;			
		double flowIn = 0, flowOut = 0;
		// calc. inner border
		for(Integer innerNode:cluster){
			Iterator<Node> neighbours = graph.getNode(innerNode).neighbors();
			while(neighbours.hasNext()){
				int neighbour = neighbours.next().getRow();
				if(!cluster.contains(neighbour)){		
					// now we know innerNode is an inner bordernode
					Iterator<Node> neighbours2 = graph.getNode(innerNode).neighbors();					
					// get flow in and flow out caused by each inner bordernode					
					while(neighbours2.hasNext()){ 
						int neighbour2 = neighbours2.next().getRow();
						int ei = graph.getEdge(neighbour2,innerNode);
						if(ei == -1)
							ei = graph.getEdge(innerNode,neighbour2);					
						if(ei != -1)
							if(cluster.contains(neighbour2)){	
								flowIn +=  graph.getEdgeTable().getDouble(ei, EDGE_WEIGHT);
							}
							else
								flowOut += graph.getEdgeTable().getDouble(ei, EDGE_WEIGHT);							
					}					
					break;
				}				
			}			
		}				
		if(flowOut!=0)
			return flowIn/flowOut;
		else return Integer.MAX_VALUE;
	}	
	/**
	 * checks all cluster size > 1
	 */
	public static boolean checkClusterSize(Table clusterTable){
		return Eval.checkClusterSize(clusterTable, 1);
	}
	/**
	 * checks the size of all cluster
	 * @param clusterTable
	 * @param size size to check 
	 * @return gets false if one cluster size <= size  else true
	 */
	public static boolean checkClusterSize( Table clusterTable,int size){
		IntIterator rows = clusterTable.rows();
		TreeSet<String> allNodes = new TreeSet<String>();
		while(rows.hasNext()){
			TreeSet<String> c = ((TreeSet<String>) clusterTable.get(rows.nextInt(), CLUSTER_COLUMN_NAME));
			if(c.size()<=size)
				return false;
		}
		return true;
	}		
	/***/
	public static boolean allNodesInCluster(Graph graph, Table clusterTable){
		IntIterator rows = clusterTable.rows();
		TreeSet<String> allNodes = new TreeSet<String>();
		while(rows.hasNext())
			allNodes.addAll((TreeSet<String>) clusterTable.get(rows.nextInt(), CLUSTER_COLUMN_NAME));

		if(allNodes.size() == graph.getNodeTable().getRowCount())
			return true;		
		return false;
	}		
	/** */
	private static TreeSet<Integer> clusterIndexes(Graph graph, TreeSet<String> cluster){
		TreeSet<Integer> clusterIndexes = new TreeSet<Integer>();
		for( int row = 0; row < graph.getNodeTable().getRowCount(); row++ ) {
			String gn = graph.getNodeTable().getString(row, NODE_NAME);			
			for(String cn : cluster)
				if(cn.equals(gn))
					clusterIndexes.add(row);		
		}return clusterIndexes;		
	}
	/** gets true if a disjoint sets of clusters is given in clusterTable */
	public static boolean isHarden(Table clusterTable){		
		if(clusterTable.getRowCount()==1) return true;
		IntIterator rows = clusterTable.rows();
		TreeSet<String> allNodes = new TreeSet<String>();
		while(rows.hasNext()){
			TreeSet<String> cluster = (TreeSet<String>) clusterTable.get(rows.nextInt(), CLUSTER_COLUMN_NAME );
			int nsize = allNodes.size();			
			allNodes.addAll(cluster);
			if(allNodes.size() != (cluster.size() + nsize))
				return false;	
		}		
		return true;		
	}
	/** computes median of average silhouette for all clusters*/
	public static double silhouetteMean(Graph graph, Table p_clusterTable){		
		// median
		m_list.clear();
		if(p_clusterTable.getRowCount()<1) return -1;
		IntIterator titer = p_clusterTable.rows();
		while(titer.hasNext())
			m_list.add(silhouette(graph,(TreeSet<String>)p_clusterTable.get(titer.nextInt(), CLUSTER_COLUMN_NAME )));
		return median(m_list);
	}
	/** computes average silhouette */
	private static double silhouette(Graph graph, TreeSet<String> cluster){	

		TreeSet<Integer> clusterIndexes = clusterIndexes(graph, cluster);			

		double a[]= new double[cluster.size()];
		double b[]= new double[cluster.size()];		

		for(int i=0; i < cluster.size(); i++){
			a[i]=1;
			b[i]=0;
		}

		double max = 0;		
		int i = 0;
		for(Integer ci : clusterIndexes ){				
			// neighbours index
			TreeSet<Integer> ni =  new TreeSet<Integer>();
			Iterator<Node> neighbours = graph.getNode(ci).neighbors();
			while(neighbours.hasNext()){
				Node n = neighbours.next();
				ni.add(n.getRow());
			}
			// compute average similarity within cluster
			double counter = 0;
			for(Integer j : clusterIndexes ){
				if(ci!=j && ni.contains(j)){		
					int ei = graph.getEdge(ci, j);	
					if(ei == -1)
						ei = graph.getEdge(j,ci);					
					if(ei != -1)
						a[i] += graph.getEdgeTable().getDouble(ei, EDGE_WEIGHT);       

					counter++;
				}
			}

			if(counter > 0) 
				a[i] = a[i]/counter;
			// compute maximal similarity to other clusters			
			for(Integer n : ni){		
				if(!clusterIndexes.contains(n)){
					double sim = 0;
					int ei = graph.getEdge(n,ci) ;	
					if(ei==-1)
						ei = graph.getEdge(ci,n);
					if(ei!=-1)
						sim = graph.getEdgeTable().getDouble(ei, EDGE_WEIGHT);

					if(sim > max) 
						max = sim;
				}
			}
			b[i] = max;
			i++;
		}   
		// compute average silhouette
		double silhouette = 0.0;
		for(int ii=0; ii<cluster.size(); ii++)
			silhouette += (a[ii]-b[ii])/java.lang.Math.max(a[ii], b[ii]);
		return silhouette/cluster.size();
	}
	/** */
	public static double ncut(Graph graph, Table clusters){
		double sum = 0.0;		
		IntIterator titer = clusters.rows();
		while(titer.hasNext()){
			TreeSet<String> cluster = (TreeSet<String>) clusters.get(titer.nextInt(), CLUSTER_COLUMN_NAME );	
			sum += flow(graph,cluster)/vol(graph,cluster);
		}		
		return sum/2.0;
	}	
	/** gets the total weight of all nodes in cluster to all there neighbors */
	private static double vol(Graph graph, TreeSet<String> cluster){		
		if(cluster.size()<2)
			return Double.NaN;		// avoid div. by zero

		TreeSet<Integer> clusterIndexes = clusterIndexes(graph, cluster);
		double sum = 0;
		// get all neighbors of node ci which is in cluster
		for(int ci : clusterIndexes ) {
			Iterator<Node> iter = graph.getNode(ci).neighbors();			
			// over all neighbors
			while(iter.hasNext()){				
				Node n = iter.next();
				int ni = n.getRow(); // neighbor index		
				int ei = graph.getEdge(ci, ni);
				if(ei == -1)
					ei = graph.getEdge(ni,ci);					
				if(ei != -1)
					sum += graph.getEdgeTable().getDouble(ei, EDGE_WEIGHT);	
			}
		}
		return sum;			
	}

	/** gets the total weight of edges with nodes in cluster and neighbors aren't in cluster*/
	public static double flow(Graph graph, TreeSet<String> cluster){
		TreeSet<Integer> clusterIndexes = clusterIndexes(graph, cluster);
		double sum = 0;
		// compute total similarity to other clusters	
		for(int ci : clusterIndexes ) {			
			Iterator<Node> iter = graph.getNode(ci).neighbors();			
			while(iter.hasNext()){								

				int ni = iter.next().getRow(); 			
				if(!clusterIndexes.contains(ni)){
					int ei = graph.getEdge(ci, ni);
					if(ei == -1)
						ei = graph.getEdge(ni,ci);					
					if(ei != -1)
						sum += graph.getEdgeTable().getDouble(ei, EDGE_WEIGHT);
				}			
			}
		}
		return sum;		
	}	
}