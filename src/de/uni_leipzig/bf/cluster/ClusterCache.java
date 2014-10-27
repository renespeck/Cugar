package de.uni_leipzig.bf.cluster;
  
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
/**
 * ClusterCache implements a very simple caching strategy.
 */
public class ClusterCache {	
	protected boolean m_enabled = true;
	/* all clusters to m_id */
	protected HashMap<TreeSet<Integer>, Integer> m_cacheHistory = null;
	/* m_id to final cluster */
	protected HashMap<Integer, TreeSet<Integer>> m_cache = null;
	/* for every m_id we log all clusters */
	protected ArrayList<TreeSet<Integer>> m_log = null;
	/* */
	protected int m_id = 0;
	/**
	 * ClusterCache enabled.
	 */
	public ClusterCache(){
		m_enabled = true;
		init();
	}
	/**
	 * @param enabled true to enable cache
	 */
	public ClusterCache(boolean enabled){
		m_enabled = enabled;
		if(enabled)
			init();

	}
	private void init(){
		m_cacheHistory = new HashMap<TreeSet<Integer>, Integer>();
		m_cache = new HashMap<Integer, TreeSet<Integer>>();			
		m_log = new ArrayList<TreeSet<Integer>>();
		m_id = 0;			
	}
	/**
	 * Adds new final cluster elements to m_cache and all logged elements in m_log for this cluster to m_cacheHistory.
	 * Clears m_log and increments id for next cluster calculations.
	 * 
	 * @param finalElements the final cluster elements
	 * @return same as parameter, the final cluster elements
	 */
	public TreeSet<Integer> add(TreeSet<Integer> finalElements){		
		
		if(!m_enabled)
			return finalElements;
		
		Iterator<TreeSet<Integer>> logIter = m_log.iterator();
		while (logIter.hasNext()) 
			m_cacheHistory.put( logIter.next(), new Integer(m_id));

		m_cache.put(new Integer(m_id), finalElements);		
		m_id++;

		m_log.clear();	
		return finalElements;
	}
	/**
	 * Checks for cached elements in m_cacheHistory or adds elements to m_log.
	 * 
	 * @param elements 
	 * @return  final elements or null 
	 */
	public TreeSet<Integer> cache(TreeSet<Integer> elements){
		
		if(!m_enabled)
			return null;

		if (m_cacheHistory.containsKey(elements)) {	
			if (!m_log.isEmpty()) {		
				// add log to cache history
				Iterator<TreeSet<Integer>> logIter = m_log.iterator();
				while (logIter.hasNext()) {
					TreeSet<Integer> logElem = logIter.next();
					m_cacheHistory.put( logElem, m_cacheHistory.get(elements));
				}					
			}// return cached elements
			m_log.clear();
			return  m_cache.get((Integer) m_cacheHistory.get(elements));			
		} else 
			m_log.add(new TreeSet<Integer>(elements)); // we don't know this cluster, add to log
		return null;
	}
}