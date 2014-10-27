package de.uni_leipzig.bf.cluster;
  
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
/**
 *
 * @author an
 */
public class Cluster{
	
	TreeSet<Integer> innerBorder;
	TreeSet<Integer> outerBorder;

	protected TreeSet<Integer> elements;
	protected HashMap<Integer, TreeSet<Integer>> border;
	protected ClusterGraph graph;
	protected int seed;
	/**
	 */
	public Cluster(ClusterGraph _graph, Integer _seed){
		// init
		elements = new TreeSet<Integer>();
		innerBorder = new TreeSet<Integer>();
		outerBorder = new TreeSet<Integer>();
		graph = _graph;
		seed = _seed;
		border = new HashMap<Integer, TreeSet<Integer>>();
		// fill
		elements.add(seed);
		innerBorder.add(seed);
		outerBorder.addAll(graph.getNeighbours(seed));		
		border.put(seed, graph.getNeighbours(seed));
	}
	/**
	 */
	public void addNode(Integer node){
		// update elements
		elements.add(node);
		// update inner border
		// check the border map to know whether some nodes connections to the outside is empty		 
		ArrayList<Integer> toRemove = new ArrayList<Integer>();
		Iterator<Integer> iter = innerBorder.iterator();		
		while(iter.hasNext()){
			Integer innerNode = iter.next();
			border.get(innerNode).remove(node);
			
			if(border.get(innerNode).isEmpty()){
				border.remove(innerNode);
				toRemove.add(innerNode);
			}
		}
		// remove nodes with no connection to the outside from innerBorder
		for(int i=0; i<toRemove.size(); i++)
			innerBorder.remove(toRemove.get(i));

		// if all neighbours of the new node are in the set of inner nodes, then the node is
		// a regular element. Else, it is a inner border node		 
		TreeSet<Integer> outerLinks = new TreeSet<Integer>();
		TreeSet<Integer> neighbours = graph.getNeighbours(node);
		iter = neighbours.iterator();
		while(iter.hasNext()){
			Integer neighbour = iter.next();
			if(!elements.contains(neighbour))
				outerLinks.add(neighbour);
		}
		//System.out.println("Outer links of node "+node+" are "+outerLinks);
		// if node brings new connection to the outside
		if (outerLinks.size()!=0){
			border.put(node, outerLinks);
			innerBorder.add(node);
			outerBorder.addAll(outerLinks);
		}
		// compute outerBorder
		iter = innerBorder.iterator();
		outerBorder = new TreeSet<Integer>();
		while(iter.hasNext())
			outerBorder.addAll(border.get(iter.next()));
	}
	/**
	 */
	public String toString(){
		return elements.toString();
	}

	/** Get current state of cluster
	 */
	public String getState(){
		String s = "Elements "+elements+"\n";
		s = s+"InnerBorder "+innerBorder+"\n";
		s = s+"OuterBorder "+outerBorder+"\n";
		s = s+"Border "+border+"\n";
		s = s+"Relative flow "+getRelativeFlow()+"\n";
		s = s+"Silhouette "+getSilhouette()+"\n";
		return s;
	}

	/** Generate a copy of this clusters
	 */
	public Cluster getCopy(){
		Cluster c = new Cluster(graph, seed);
		Iterator<Integer> iter = elements.iterator();
		while(iter.hasNext())    
			c.addNode(new Integer((iter.next()).intValue()));                           
		return c;
	}
	/** Computes the silhouette value of the clusters at hand
	 */
	public double getSilhouette(){		
		double a[]= new double[elements.size()];
		double b[]= new double[elements.size()];		
		
		Integer[] elts = new Integer[elements.size()];
		elements.toArray(elts);				
		
		for(int i=0; i<elts.length; i++){
			a[i]=1;
			b[i]=0;
		}
		
		double max = 0;		
		for(int i=0; i<elts.length; i++){			
			double counter = 0;
			TreeSet<Integer> neighbours = graph.getNeighbours(elts[i]);
			// compute average similarity within cluster
			for(int j=0; j<elts.length; j++){
				if(i!=j && neighbours.contains(elts[j])){
					a[i] = a[i] + graph.getEdgeWeight(elts[i], elts[j]);             
					counter ++;
				}
			}
			if(counter > 0) 
				a[i] = a[i]/counter;
			// compute maximal similarity to other clusters
			neighbours = graph.getNeighbours(elts[i]);
			Iterator<Integer> iter = neighbours.iterator();
			while(iter.hasNext()){
				Integer node = iter.next();
				if(!elements.contains(node)){
					double sim = graph.getEdgeWeight(node, elts[i]);
					if(max < sim) 
						max = sim;
				}
			}
			b[i] = max;
		}   
		// compute average silhouette
		double silhouette = 0;
		for(int i=0; i<a.length; i++)    
			silhouette = silhouette + (a[i]-b[i])/java.lang.Math.max(a[i], b[i]);		
		return silhouette/a.length;
	}
	/** Computes the silhouette value of the clusters at hand
	 */
	public double getSilhouetteFull(){
		double a[]= new double[elements.size()];
		double b[]= new double[elements.size()];
		
		Integer[] elts = new Integer[elements.size()];
		elements.toArray(elts);
			
		for(int i=0; i<elts.length; i++){
			a[i]=1;
			b[i]=0;
		}
		
		double max=0;
		for(int i=0; i<elts.length; i++){
			// compute average distance within cluster
			for(int j=0; j<elts.length; j++){
				if(i!=j)
					a[i] = a[i] + graph.getEdgeWeight(elts[i], elts[j]);             
			}
			if(elts.length > 1) 
				a[i] = a[i]/(elts.length - 1);
			// compute minimal distance to other clusters
			TreeSet<Integer> neighbours = graph.getNeighbours(elts[i]);
			Iterator<Integer> iter = neighbours.iterator();
			while(iter.hasNext()){
				Integer node = iter.next();
				if(!elements.contains(node)){
					double sim = graph.getEdgeWeight(node, elts[i]);
					if(max < sim) 
						max = sim;
				}
			}
			b[i] = max;
		}   
		// compute average silhouette
		double silhouette = 0;
		for(int i=0; i<a.length; i++) 
			silhouette += (a[i]-b[i])/java.lang.Math.max(a[i], b[i]);
		return silhouette/a.length;
	}
	/**
	 */
	public double getRelativeFlow(){
		
		double flowIn = 0, flowOut = 0;		
		Iterator<Integer> itera, iter;			
		// get all elements of inner border
		iter = innerBorder.iterator();		
		while(iter.hasNext()){			
			Integer borderNode = iter.next();
			TreeSet<Integer> neighbours = graph.getNeighbours(borderNode);
			itera = neighbours.iterator();
			// get flow in and flow out caused by each inner bordernode
			while(itera.hasNext()){
				Integer node = itera.next();				
				if(elements.contains(node))
					flowIn +=  graph.getEdgeWeight(node, borderNode);
				else
					flowOut +=  graph.getEdgeWeight(node, borderNode);
			}
		}
		if(flowOut!=0)
			return flowIn/flowOut;
		else return Integer.MAX_VALUE;
	}
	/**
	 */
	public TreeSet<Integer> getElements(){
		return elements;
	}

	public static void main(String args[]){
		ClusterGraph g = new ClusterGraph();
		g.initialize("test/6-5-partite-clique.txt","\t"); 
		
		Cluster c = new Cluster(g, new Integer(0));
		System.out.println(c.getState());
		
		c.addNode(new Integer(1));
		System.out.println(c.getState());
		c.addNode(new Integer(4));
		System.out.println(c.getState());
		c.addNode(new Integer(5));
		System.out.println(c.getState());
	}
}
