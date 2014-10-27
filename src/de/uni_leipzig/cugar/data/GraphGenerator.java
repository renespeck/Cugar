package de.uni_leipzig.cugar.data;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.algorithms.generators.random.BarabasiAlbertGenerator;
import edu.uci.ics.jung.algorithms.generators.random.EppsteinPowerLawGenerator;
import edu.uci.ics.jung.algorithms.generators.random.KleinbergSmallWorldGenerator;
import edu.uci.ics.jung.graph.SparseMultigraph;
//import edu.uci.ics.jung.algorithms.metrics.Metrics;
//import edu.uci.ics.jung.algorithms.shortestpath.DistanceStatistics;
import prefuse.data.Node;
/**
 * Generates graphs.
 */
public class GraphGenerator{

	///////////////////////////////////////////////////////////////////////////////////
	// 	Jung 
	//  
	//  TODO:
	//	DistanceStatistics.diameter(graph))
	//	DistanceStatistics.averageDistances(graph);
	//	Metrics.clusteringCoefficients(graph);
	//	
	Factory<edu.uci.ics.jung.graph.Graph<Integer,Number>> graphFactory = new Factory<edu.uci.ics.jung.graph.Graph<Integer,Number>>() {
		public edu.uci.ics.jung.graph.Graph<Integer,Number> create() {
			return new SparseMultigraph<Integer,Number>();
		}
	};
	Factory<Integer> vertexFactory = new Factory<Integer>() {
		int count;
		public Integer create() {
			return count++;
		}
	};
	Factory<Number> edgeFactory = new Factory<Number>() {
		int count;
		public Number create() {
			return count++;
		}
	};
	//TODO: ADD PARAMETER
	public edu.uci.ics.jung.graph.Graph<Integer, Number> getKleinbergSmallWorld(){
		KleinbergSmallWorldGenerator<Integer, Number> generator = 
			new KleinbergSmallWorldGenerator<Integer, Number>(
					graphFactory, vertexFactory, edgeFactory,
					32,32,2
			);	
		return  generator.create();
	}

	//TODO: ADD PARAMETER
	public edu.uci.ics.jung.graph.Graph<Integer, Number> getEppsteinPowerLaw(){
		EppsteinPowerLawGenerator<Integer, Number> generator = 
			new EppsteinPowerLawGenerator<Integer, Number>(
					graphFactory, vertexFactory, edgeFactory, 
					1000,5000,10000//10000000
			);
		return  generator.create();
	}

	public edu.uci.ics.jung.graph.Graph<Integer, Number> getBarabasiAlbertGraph(){
		
		int init_vertices = 4;
		int num_timesteps = 400;
		int edges_to_add_per_timestep = 2;
		int random_seed = (int)(Math.random() * 10000);

		return getBarabasiAlbertGraph(init_vertices,edges_to_add_per_timestep,random_seed,num_timesteps);
	}
	/**
	 * 
	 * @param init_vertices
	 * @param numEdgesToAttach
	 * @param seed
	 * @param num_timesteps
	 * @return
	 */
	public edu.uci.ics.jung.graph.Graph<Integer, Number>
		getBarabasiAlbertGraph(int init_vertices, int numEdgesToAttach, int seed, int num_timesteps){		

		BarabasiAlbertGenerator<Integer,Number> generator = 
			new BarabasiAlbertGenerator<Integer,Number>(
					graphFactory, vertexFactory, edgeFactory,
					init_vertices,
					numEdgesToAttach,
					seed, 
					new HashSet<Integer>()
			);

		generator.evolveGraph(num_timesteps);
		edu.uci.ics.jung.graph.Graph<Integer, Number> graph = generator.create();

		return graph;		
	}
	/**
	 * Writes a edu.uci.ics.jung.graph.Graph to String.
	 * 
	 * @param sep separator to us in return String
	 * @param graph instance of a edu.uci.ics.jung.graph.Graph
	 * 
	 * @return delimited Sting instance
	 */
	public String writeToString(String sep,edu.uci.ics.jung.graph.Graph<Integer, Number> graph){
		Collection<Number> c = graph.getEdges();
		String out ="";
		for(Number n : c)
			out += graph.getEndpoints(n).getFirst() + sep + graph.getEndpoints(n).getSecond() + sep + "1.0\n";	
		return (out);		
	}
	/**
	 * 
	 * Writes a edu.uci.ics.jung.graph.Graph to a file.
	 * 
	 * @param filename 
	 * @param sep separator to us in file
	 * @param graph instance of a edu.uci.ics.jung.graph.Graph
	 */
	public void writeToFile(String filename,String sep,edu.uci.ics.jung.graph.Graph<Integer, Number> graph){	
		try{		
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
			bw.write(writeToString(sep,graph));
			bw.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	// 	end Jung 
	///////////////////////////////////////////////////////////////////////////////////

	///////////////////////////////////////////////////////////////////////////////////
	// 	Prefuse
	public prefuse.data.Graph grid(){
		return prefuse.util.GraphLib.getGrid(10, 10);
	}
	public prefuse.data.Graph honeycomb(){
		return prefuse.util.GraphLib.getHoneycomb(4);
	}

	public prefuse.data.Graph balancedTree(){
		return prefuse.util.GraphLib.getBalancedTree(3, 5);
	}
	public prefuse.data.Graph diamondTree(){
		return prefuse.util.GraphLib.getDiamondTree(5, 3, 3);
	}
	// 	end Prefuse
	///////////////////////////////////////////////////////////////////////////////////


	///////////////////////////////////////////////////////////////////////////////////
	// 	self to Prefuse graph
	/** 
	 * Random clique n = 3...15, m = 2...14 and m > n.
	 */
	public prefuse.data.Graph clique(){

		int m = Integer.MAX_VALUE ;
		int n  =  (int) (3 + Math.round(Math.random()*100)%13) ;
		while(n==m || m > n)// keep the order of node labels in cliques
			m = (int) (2 + Math.round(Math.random()*100)%13);

		return  clique(n,m);
	}
	/**
	 * Generates a (n,k)-partite-clique.
	 * @param n #nodes
	 * @param k #cliques
	 */
	public prefuse.data.Graph clique(int n , int k){
		if (n <= 0 || k <= 0)
			throw new IllegalArgumentException("A positive # of nodes must be specified for n and m.");

		prefuse.data.Graph graph = new  prefuse.data.Graph();  
		Node nodes[] = new Node[n*k];
		for(int i = 0; i < n*k; i++)			
			nodes[i] = graph.addNode();
		for(int m = 0 ; m < k ; m++)
			for (int i = n * m; i < n*(m+1); i++)
				for (int j = i; j < n*(m+1); j++)
					if ( i != j )
						graph.addEdge(nodes[i], nodes[j]);	
		for(int oc = 0; oc < k; oc++)		
			for(int ic = oc; ic < k - 1; ic++)				
				for(int v = 0; v < n; v++)
					graph.addEdge(nodes[oc * n + v], nodes[ic * n + v + n]);
		return graph;		
	}	
	/**
	 * Generates a topped tetrahedron.
	 */
	public prefuse.data.Graph topped(){
		prefuse.data.Graph g = new  prefuse.data.Graph();
		Node nodes[] = new Node[3*4];		
		for(int i = 0; i < 3*4; i++)			
			nodes[i] = g.addNode();			
		for (int i = 0; i < 3; i++ ) 
			for ( int j = i; j < 3; j++ )
				if ( i != j )
					g.addEdge(nodes[i], nodes[j]);			
		for (int i = 3; i < 3*2; i++ ) 
			for ( int j = i; j < 3*2; j++ )
				if ( i != j )
					g.addEdge(nodes[i], nodes[j]);			
		for (int i = 6; i < 3*3; i++ ) 
			for ( int j = i; j < 3*3; j++ )
				if ( i != j )
					g.addEdge(nodes[i], nodes[j]);			
		for (int i = 9; i < 3*4; i++ ) 
			for ( int j = i; j < 3*4; j++ )
				if ( i != j )
					g.addEdge(nodes[i], nodes[j]);			

		g.addEdge(nodes[0], nodes[3]);
		g.addEdge(nodes[6], nodes[9]);
		g.addEdge(nodes[5], nodes[11]);
		g.addEdge(nodes[10], nodes[2]);
		g.addEdge(nodes[1], nodes[8]);
		g.addEdge(nodes[7], nodes[4]);
		return g;
	}
	/**
	 * Generates a graph with the Erdos-Renyi binomial model,
	 * with  k = 2.4 , n = 200 (approximately k = n * p)
	 */
	public  prefuse.data.Graph erdosRenyi() {
		int n = 200; 		
		return erdosRenyi(n, 2.4/n, -1);
	}
	/**
	 * Generates a graph with the Erdos-Renyi binomial model.
	 * 
	 * @param n #nodes
	 * @param p probability
	 * @param randSeed use a seed for Random or don't with argument -1 for real random Graph with a random seed.
	 */
	public  prefuse.data.Graph erdosRenyi(int n, double p,long randSeed) {
		if (n <= 0)
			throw new IllegalArgumentException("A positive # of n must be specified.");

		if (p < 0 || p > 1)
			throw new IllegalArgumentException("p must be between 0 and 1.");

		Random random = new Random();
		if(randSeed != -1)
			random.setSeed(randSeed);

		prefuse.data.Graph graph = new  prefuse.data.Graph();        

		Node nodes[] = new Node[n];
		for(int i = 0; i < n; i++)			
			nodes[i] = graph.addNode();

		for (int i = 0; i < n-1; i++)
			for (int j = i+1; j < n; j++)
				if (random.nextDouble() < p)
					graph.addEdge(nodes[i],nodes[j]);
		return graph;
	}
}
