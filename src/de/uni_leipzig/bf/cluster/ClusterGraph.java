package de.uni_leipzig.bf.cluster;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import semiosys.colt.xtend.SparseSuperDoubleMatrix2D;
/**
 * @author an
 */
public class ClusterGraph implements de.uni_leipzig.cugar.harden.ClusterGraphInterface{

	HashMap<String, Integer> index;
	HashMap<Integer, String> reverseIndex;
	public int size = 0;
	protected SparseSuperDoubleMatrix2D matrix;
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ClusterGraph.class);
	/** 
	 * We ignore edges with the same node as source and target.
	 */
	public boolean initialize(String file,String seperator) {
		logger.setLevel(org.apache.log4j.Level.INFO);
		try {
			// first build index
			System.out.println("Reading " + file);
			BufferedReader reader = new BufferedReader(new FileReader(file));             
			logger.info("Generating index ...");
			String line = "";			
			TreeSet<String> terms = new TreeSet<String>();             
			while ((line = reader.readLine()) != null) {
				if (line.contains(seperator)) {
					String split[] = line.split(seperator);
					terms.add(split[0]);
					terms.add(split[1]);
				}
			}
			reader.close();
			//fill the index table
			index = new HashMap<String, Integer>();
			reverseIndex = new HashMap<Integer, String>();             
			Iterator<String> iter = terms.iterator();
			int id = 0;
			while (iter.hasNext()) {
				String term = iter.next();
				index.put(term, new Integer(id));
				reverseIndex.put(new Integer(id), term);
				id++;
			}
			//now build matrix
			logger.info("Reading in graph ...");
			// set graph size
			size = terms.size();             
			matrix = new SparseSuperDoubleMatrix2D(size,size);
			reader = new BufferedReader(new FileReader(file));          
			while ((line = reader.readLine()) != null) {					
				String split[] = line.split(seperator);		
				if (index.get(split[0]).compareTo(index.get(split[1])) == 0)
					continue;				
				if (index.get(split[0]).compareTo(index.get(split[1])) > 0) 
					matrix.set(index.get(split[0]).intValue(), index.get(split[1]).intValue(), Double.parseDouble(split[2]));
				else
					matrix.set(index.get(split[1]).intValue(), index.get(split[0]).intValue(), Double.parseDouble(split[2]));
			}
			reader.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 */
	@Override
	public double getEdgeWeight(Integer source, Integer target) {		
		if (source.compareTo(target) == 0)
			return 0;
		if (source.compareTo(target) > 0) 
			return matrix.get(source.intValue(), target.intValue());
		else 
			return matrix.get(target.intValue(), source.intValue());
	}
	/**
	 */
	@Override
	public TreeSet<Integer> getNeighbours(int index) {
		TreeSet<Integer> neighbours = new TreeSet<Integer>();
		if (index < size) {
			for (int i = 0; i < size; i++) {
				if (index > i) {                    
					if (matrix.get(index, i) > 0) 
						neighbours.add(new Integer(i));                    
				} else if (matrix.get(i, index) > 0) 
					neighbours.add(new Integer(i));                
			}
		}
		return neighbours;
	}
	/**
	 */
	public double getNumberOfNeighbours(Integer index) {
		return getNeighbours(index).size();
	}
	/**
	 */
	@Override
	public String toString() {
		String s = index.toString();
		s += "\n";
		s += matrix.toString();
		return s;
	}
	@Override
	public int size() {
		return size;
	}
	/** 
	 */
	public static void main(String args[]) {
		ClusterGraph g = new ClusterGraph();
		g.initialize("test/6-5-partite-clique.txt","\t");       
		System.out.println(g);
		System.out.println(g.getNeighbours(0));
	}
}