package de.uni_leipzig.cc.coyotecache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import de.uni_leipzig.cc.cache.CacheInterface;
import de.uni_leipzig.cc.cache.Element;
import de.uni_leipzig.cc.seeds.SeedOrderInterface;
/**
* @author rspeck
*/
public class CoyoteCache implements CoyoteCacheInterface{	

	public static final boolean online = false;

	/** logger */
	public static Logger log4j = Logger.getLogger(CoyoteCache.class);

	/** current replacement policy */
	public CacheInterface m_cache = null;

	/** current seed policy */
	protected SeedOrderInterface m_seedPolicy = null;

	/** partial cluster */
	protected List<Element> m_partialCluster = new ArrayList<Element>();

	/** final cluster dictionary */
	private int maxSize ;
	protected Map<Integer,Set<Integer>> m_finalCluster = new Hashtable<Integer, Set<Integer>>();

	/** current working set id */
	private	int m_id = 0;

	/** cache statistic */
	public Statistic m_statistic = new Statistic();

	/**
	 * 
	 * @param cache the Cache to use
	 * @param seedPolicy set to null without a SeedPolicy
	 */
	public CoyoteCache(CacheInterface cache, SeedOrderInterface seedPolicy){ 
		m_cache = cache;
		m_seedPolicy = seedPolicy;		
		maxSize = m_cache.maxSize();
	}

	/**
	 */
	public CoyoteCache(CacheInterface cache){ 
		m_cache = cache;
		maxSize = m_cache.maxSize();
	}

	/**
	 * Adds final set to {@link #m_finalCluster} and puts {@link #m_partialCluster} to {@link #m_cachePolicy}
	 * @param finalSet
	 */
	@Override
	public Set<Integer> put(Set<Integer> finalSet){
		
		if(online)
			putOnline(finalSet);
		else 
			putOffline(finalSet);
		
		updateFinalClusterMap();
		// put final to dictionary
		m_finalCluster.put(m_id, finalSet);
		// debug info
		m_statistic.setMaxLogSize(m_partialCluster.size());		
		// next round
		m_partialCluster.clear();
		m_id++;
		return finalSet;
		
	}
	private void putOnline(Set<Integer> finalSet){
		// copy log to cache 						
		for(ListIterator<Element> i = m_partialCluster.listIterator(0); i.hasNext(); ){
			Element element = i.next();
			// update seeds
			if(m_seedPolicy != null)
				m_seedPolicy.update((Set<Integer>) element.getObject());	
			// update cache
			if(!element.getObject().equals(finalSet)){					
				m_cache.put(element,m_id);
			}
		}	
	}
	private void putOffline(Set<Integer> finalSet){
		// copy log to cache 						
		for(ListIterator<Element> i = m_partialCluster.listIterator(0); i.hasNext(); ){
			Element element = i.next();
			// update seeds and cache 
			if(m_seedPolicy != null){
				if(m_seedPolicy.hasUnusedSeeds((Set<Integer>) element.getObject())){
					m_seedPolicy.update((Set<Integer>) element.getObject());	
					// update cache
					if(!element.getObject().equals(finalSet))				
						m_cache.put(element,m_id);
				}
				else break;

			}else 
				if(!element.getObject().equals(finalSet))				
					m_cache.put(element,m_id);	
		}
	}
	private void updateFinalClusterMap(){
		// remove an object from dictionary if max size reached
		if(m_finalCluster.size() >= maxSize){						
			// all ids in cache
			Collection<Integer> idsInCache = (Collection)m_cache.values();
			// all ids in dictionary
			Iterator<Entry<Integer, Set<Integer>>> ii = m_finalCluster.entrySet().iterator();
			// if nothing found (all ids in cache and dictionary )
			Integer smallestClusterId = null;
			Integer smallestClusterSize = null;
			//
			while(ii.hasNext()){
				Entry<Integer, Set<Integer>> entry = ii.next();
				// remember smallest cluster id
				if(smallestClusterId == null || smallestClusterSize > entry.getValue().size()){
					smallestClusterId = entry.getKey();
					smallestClusterSize = entry.getValue().size();
				}
				// remove all ids from dictionary that are not in cache			
				if(!idsInCache.contains(entry.getKey()))
					ii.remove();
			}
			// all ids in cache and dictionary, nothing was removed
			// remove the smallest cluster from map and update cache
			if(m_finalCluster.size() >= maxSize && smallestClusterId != null){
				m_finalCluster.remove(smallestClusterId);
				m_cache.removeValues(smallestClusterId);
			}				
		}
	}

	/** 
	 * Gets the finalElement to element if exists, else null
	 */
	@Override
	public Set<Integer> get(Set<Integer> element){
		return get(element, 0D);		
	}

	/** 
	 * Gets the finalElement to element if exists, else null
	 */
	@Override
	public Set<Integer> get(Set<Integer> set, Double cost){	

		// is set to small to cluster?
		if(set.size() < 2){
			m_statistic.incDismissed();	
			return null;
		}	

		// is set a know final cluster?
		Integer id_final = null;
		for(Integer id : m_finalCluster.keySet())

			if(m_finalCluster.get(id).equals(set)){	
				id_final = id;
				m_statistic.incDictionaryHits();				
				break;
			}	

		//  do we have cached a sub element and get a final one?
		//  we ask the cache
		Element element = new Element(new LinkedHashSet<Integer>(set),cost);
		if(id_final == null){				
			id_final = (Integer) m_cache.get(element);

			if(id_final == null)
				m_statistic.incFaults();
			else
				m_statistic.incHitCost(cost);
		}

		// do we have a final element from dictionary or cache?
		if(id_final != null){					
			if (!m_partialCluster.isEmpty()){					
				for(Iterator<Element> i = m_partialCluster.iterator(); i.hasNext();){
					Element logElement = i.next();
					// update seeds
					if(m_seedPolicy != null)
						m_seedPolicy.update((Set<Integer>) logElement.getObject());								
					// don't add final cluster
					if(!m_finalCluster.containsValue(logElement.getObject())){  					
						m_cache.put(logElement, id_final);
					}		
				}
			}
			m_partialCluster.clear();
			return  m_finalCluster.get(id_final);   			
		}else{	
			addElementToLog(element); 
			return null;
		}
	}	

	private boolean addElementToLog(Element element){
		if(m_seedPolicy != null){
			if(m_seedPolicy.hasUnusedSeeds((Set<Integer>)element.getObject()))				
				return m_partialCluster.add(element);				
			else return false;			

		}else return m_partialCluster.add(element); 
	}

	@Override
	public String toString(){
		return 
		m_cache.getClass().getSimpleName() + "\t" + 
		m_cache.maxSize();// + "\t" + 
		//m_statistic;		
	}

	@Override
	public Integer getBestSeed(){
		return m_seedPolicy.getBestSeed();
	}

	@Override
	public void setSeeds(Set<Integer> seeds) {
		if(m_seedPolicy != null)
			m_seedPolicy.addAll(seeds);
		else 
			log4j.warn("No seed order policy is used.");
	}
}