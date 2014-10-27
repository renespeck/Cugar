package de.uni_leipzig.cc.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
/**
* @author rspeck
*/
public class Lfu extends AbstractCache{
	/**
	 * cluster (map key) to hits (map value).
	 */
	protected final Map<Object, Integer> m_access = new LinkedHashMap<Object,Integer>();

	public Lfu(int size, int evictCount) {
		super(size,evictCount);
	}

	@Override
	protected void evict(){
		
		if(m_evictCount < m_access.size()){		
		
			List<Integer> minHits = new ArrayList<Integer>(m_access.values());
			Collections.sort(minHits);
			minHits = minHits.subList(0, m_evictCount);
	
			Iterator<Entry<Object, Integer>> accessIter = m_access.entrySet().iterator();		
			while(accessIter.hasNext() && !minHits.isEmpty()){
				Entry<Object, Integer> entry = accessIter.next();
			
				if(minHits.contains(entry.getValue())){
					minHits.remove(entry.getValue());
					accessIter.remove();
					m_cacheMap.remove(entry.getKey());	
				}
			}		

		}else{
			m_access.clear();	
			m_cacheMap.clear();
		}
	}

	@Override
	protected void hitAccess(Object key) {		
		Integer id = m_access.get(key);
		if(id != null)
			m_access.put(key, ++id);
		else{ 
			m_access.put(key, 0);
			log4j.error("Can't find key. Was it realy a hit?");
		}
	}

	@Override
	protected void putAccess(Object key) {
		Integer id = m_access.get(key);
		if(id != null)
			m_access.put(key, ++id);
		else 
			m_access.put(key, 0);		
	}

	@Override
	public List<Object> removeValues(Object value){

		List<Object> removed = super.removeValues(value);
		for(Object o : removed)
			m_access.remove(o);

		return removed;		
	}

	@Override
	public boolean test(){	
		if(size() > m_cacheMaxSize || m_access.size() > size() || m_access.size() > m_cacheMaxSize )
			return false;	
		return true;
	}

	@Override
	public String toString(){		
		return super.toString() + "\n" + m_access.toString();		
	}	
}