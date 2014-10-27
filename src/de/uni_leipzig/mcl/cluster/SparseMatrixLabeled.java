package de.uni_leipzig.mcl.cluster;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.io.*;
/**
 *
 * @author an, rspeck
 */
public class SparseMatrixLabeled
{
	ArrayList<String> indexes;
	public SparseMatrix _mat;
	/** Creates a new instance of SparseMatrixLabeled */
	public SparseMatrixLabeled(){
		indexes = new ArrayList<String>();
		_mat = new SparseMatrix();
	}

	private ArrayList<String> getIndexes(){
		return indexes;
	}
	public SparseMatrix getMatrix(){
		return _mat;
	}

	public void setMatrix(SparseMatrix a){
		_mat = a;
	}
	/*
	public void write(PrintWriter w){        
		String rowLabel="";
		SparseVector v ;
		for(int i=0; i<_mat.size(); i++) {
			rowLabel = indexes.get(i);
			v = _mat.get(i);
			for (int j : v.keySet())
			{

				w.print(rowLabel + " ");
				w.print(indexes.get(j) + ";    ");
				w.println(v.get(j));
			}
		}
	}
	 */
	
	public String toString(){
		StringBuffer s = new StringBuffer();
		String rowLabel="";
		SparseVector v ;
		for(int i=0; i<_mat.size(); i++)
		{
			rowLabel = indexes.get(i);
			v = _mat.get(i);
			for (int j : v.keySet())
			{

				s.append(rowLabel + " ");
				s.append(indexes.get(j) + ";    ");
				s.append(v.get(j) + "\n");
			}
		}
		return s.toString();
	}
	/**
	 * Add an edge to matrix and index
	 */
	public double add(String source, String target, double value){
		int ia, ib;
		if(indexes.contains(source))
			ia = indexes.indexOf(source);
		else
		{
			indexes.add(source);
			ia = indexes.size()-1;
		}
		if(indexes.contains(target))
			ib = indexes.indexOf(target);
		else
		{
			indexes.add(target);
			ib = indexes.size()-1;
		}
		//makes the matrix symmetrical. Wanted? This is important for MCL but not wanted for Spread.
		_mat.add(ib, ia, value);        
		return _mat.add(ia, ib, value);
	}
	/**
	 * reads in a file.
	 * @param file
	 * @param separator
	 * @return matrix of LabeledSparseMatrix
	 */
	public static SparseMatrixLabeled loadMatrix(String file, String separator){
		SparseMatrixLabeled matrix = new SparseMatrixLabeled();
		try{

			BufferedReader br = new BufferedReader(new FileReader(file));			
			String s;
			while ((s = br.readLine()) != null){
				String[] split = s.split(separator);

				if(!split[0].equals(split[1])){
					double weight = 0;
					weight = Double.parseDouble(split[2]);
					matrix.add(split[0], split[1], weight);
					matrix.add(split[1], split[0], weight);	
				}
			}
			br.close();
			return matrix;
		}

		catch (FileNotFoundException e){
			e.printStackTrace();
		}
		catch (IOException e){
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * gets all cluster in a List of TreeSet
	 * @return
	 */
	public ArrayList<TreeSet<String>> getCluster(){

		int[] sparseMatrixSize = getMatrix().getSize();
		ArrayList<TreeSet<String>> clusters = new   ArrayList<TreeSet<String>>();
		ArrayList<Integer> used = new ArrayList<Integer>();

		for (int i = 0; i < sparseMatrixSize[0]; i++) {
			if(!used.contains(i)){
				TreeSet<String> cluster = new TreeSet<String>(); 
				//System.out.println(getMatrix().get(i, i));
				if ( getMatrix().get(i, i) > 0.0 ) {				
					for (int j = 0; j < sparseMatrixSize[0]; j++) {
						double value = getMatrix().get(j, i);
						if (value != 0){
							cluster.add(getIndexes().get(j));
							used.add(j);
						}
					}
					clusters.add(cluster);
				}
			}
		}
		return clusters;
	}

	/*public ArrayList<TreeSet<String>> getCluster2(){  

	ArrayList<TreeSet<String>> clusters = new   ArrayList<TreeSet<String>>();
	ArrayList<Integer> used = new ArrayList<Integer>();

	int[] sparseMatrix = getMatrix().getSize();

	for (int i = 0; i < sparseMatrix[0]; i++) {
		if(!used.contains(i))
			if (getMatrix().getColum(i).getLength() != 0) {

				Iterator<Integer> it = getMatrix().getColum(i).keySet().iterator();
				TreeSet<String> cluster = new TreeSet<String>();               
				while (it.hasNext()) {
					int index = it.next();							
					used.add(index);
					cluster.add(getIndexes().get(index));     					
				}                
				clusters.add(cluster);                
			}
	}        
	return clusters;	    
}*/
}