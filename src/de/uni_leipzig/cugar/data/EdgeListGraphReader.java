package de.uni_leipzig.cugar.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import prefuse.data.Graph;
import prefuse.data.Table;
import prefuse.data.io.AbstractGraphReader;
import prefuse.data.io.DataIOException;
import prefuse.data.util.TableIterator;
import de.uni_leipzig.cugar.gui.LogManager;

import static de.uni_leipzig.cugar.data.GraphReaderSettings.EDGE_WEIGHT;
import static de.uni_leipzig.cugar.data.GraphReaderSettings.MAX_EDGE_WEIGHT;
import static de.uni_leipzig.cugar.data.GraphReaderSettings.NODE_NAME;
/**
 * Reads a given edge list to a {@link  prefuse.data.Graph} instance. 
 */
public class EdgeListGraphReader extends AbstractGraphReader{
	// private
	private String m_separator;
	private static final String EDGE_SOURCE = "source";
	private static final String EDGE_TARGET = "target";		
	// log
	private static Logger logger = Logger.getLogger(EdgeListGraphReader.class);
	static{
		logger.removeAllAppenders();
		logger.setLevel(Level.WARN);
		logger.addAppender(LogManager.instance().getLogPanelAppender());
	}
	// flag
	private boolean directed = false;	
	/**
	 */
	public EdgeListGraphReader(String separator){
		m_separator = separator;
	}	
	/***/
	public void setSeparator(String separator){
		m_separator = separator;
	}
	/**
	 * 
	 * Reads an edge list to a {@link prefuse.data.Graph} instance. 
	 * 
	 * @param file
	 * @param p_directed 
	 * @return a Graph instance
	 */
	public Graph readGraph(File file, boolean p_directed ){
		directed = p_directed;
		try {
			return readGraph(file);

		}catch(DataIOException e){
			e.printStackTrace();			
		}
		return null;		
	}	
	/**
	 * 
	 * Reads an edge list to a {@link prefuse.data.Graph} instance. 
	 * 
	 * @param file
	 * @param p_directed 
	 * @return a Graph instance
	 */
	public Graph readGraph(String file, boolean p_directed ){
		return  readGraph(new File(file),p_directed);
	}
	/**
	 * @see prefuse.data.io.GraphReader#readGraph(java.io.InputStream)
	 */
	@Override
	public Graph readGraph(InputStream is){

		Table nodeTable = new Table();		
		nodeTable.addColumn(NODE_NAME, String.class);

		Table edgeTable = new Table();
		edgeTable.addColumn(EDGE_SOURCE, int.class);
		edgeTable.addColumn(EDGE_TARGET, int.class);
		edgeTable.addColumn(EDGE_WEIGHT, double.class);

		if (is != null) {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				String line = "";								
				Set<String> set = new HashSet<String>();	//check duplicates
				int linecount = 0;
				while((line = in.readLine()) != null) { 
					linecount++;					
					line = line.trim();
					if (line.contains(m_separator)) {									
						String[] split = line.split(m_separator);
						if(split.length == 3){
							if(!split[0].equals(split[1])){
								// add nodes
								int[] nodeIds = new int[2];			//0:source 1:target 
								for(int i = 0 ; i < 2; i++){ 
									if(!set.contains(split[i].trim())){		                    	
										int row = nodeTable.addRow();
										nodeTable.set(row, NODE_NAME, split[i].trim());									
										set.add(split[i].trim());									
										nodeIds[i] = row;									
									}else{	
										// node added before
										TableIterator iter = nodeTable.iterator();
										int row = -1;
										while(iter.hasNext()){
											row = (Integer)iter.next();
											if(nodeTable.getString(row, NODE_NAME).equals(split[i].trim()))		    
												nodeIds[i] = row;
										}
									}
								}	              							
								double edgeWeight = 0.0;
								try{
									edgeWeight = Double.parseDouble(split[2]);
								}catch(NumberFormatException e ){
									logger.warn("wrong number format in line: " + linecount + ", we use edge weight of 1.0");
									logger.info("use double for edge weight in line: " + linecount);
									edgeWeight = 1.0;
								}
								if(edgeWeight > MAX_EDGE_WEIGHT){
									edgeWeight = MAX_EDGE_WEIGHT;
									logger.warn("reaches max. edge weight: " + MAX_EDGE_WEIGHT + " in line: " + linecount );
								}	
								if(edgeWeight < 0.0){
									logger.warn("only positiv edge weight allowed in line: " + linecount + ", we use edge weight of 1.0");
									edgeWeight = 1;
								}							
								int row = edgeTable.addRow();
								edgeTable.set(row, EDGE_SOURCE, nodeIds[0]);
								edgeTable.set(row, EDGE_TARGET, nodeIds[1]);
								edgeTable.set(row, EDGE_WEIGHT, edgeWeight);	
							}
						}else // split.length != 3							
							logger.warn("column count isn't equal to 3 in line: " + linecount);
					}
				}		
				is.close();in.close();
				
			}catch(IOException e){
				e.printStackTrace();
			}
			finally{ 
				try{
					is.close();
				}catch(IOException e){
					e.printStackTrace();					
				}				
			}
			if(nodeTable.getRowCount() < 1){
				logger.warn("Node table empty. Check your file format.");
				return null;
			}else 
				return new Graph(nodeTable,edgeTable,directed); 
		}return null; //is == null
	}
}