package de.uni_leipzig.cc.cache;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
/**
* Test cache
* 
* @author rspeck
*/
public class Cost extends AbstractCache{

	protected final Map<Object, Double> m_access = new LinkedHashMap<Object, Double>();	

	public Cost(int size, int evictCount) {
		super(size, evictCount);
	}

	@Override
	protected void evict(){
		
		List<Double> costs = new LinkedList<Double>(m_access.values());
		Collections.sort(costs,Collections.reverseOrder());				
		costs = costs.subList(0, m_evictCount);

		Iterator<Entry<Object, Double>> accessIter = m_access.entrySet().iterator();

		while(accessIter.hasNext() && !costs.isEmpty()){	
			Entry<Object, Double> entry = accessIter.next();

			if(costs.contains(entry.getValue())){				
				costs.remove(entry.getValue()); 
				m_access.remove(entry.getKey());
				m_cacheMap.remove(entry.getKey());	
				
			}	
		}			
	}
	@Override
	protected void hitAccess(Object key) {
		putAccess(key);
	}
	@Override
	protected void putAccess(Object key) {
		
		Double cost = m_access.get(key);
		if(key instanceof Element){					
			
			if(cost == null || cost > ((Element)key).getCost())
				cost = ((Element)key).getCost();
			
			m_access.put(key, cost);
			
		}else{	
			log4j.warn("You have to use instances of Element for the cache key. Otherwiese the cache uses Fifo behavior.");
			
			if(cost == null)
				m_access.put(key, 1.0);
		}				
	}
	@Override
	public List<Object> removeValues(Object value){		
		List<Object> removed = super.removeValues(value);
		for(Object o : removed)
			m_access.remove(o);		
		return removed;		
	}
}