package de.uni_leipzig.cc.cache;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
/**
* @author rspeck
*/
public class GDStar extends AbstractGD {
	
	public Double m_beta = 1D;
	
	public GDStar(int size, int evictCount) {
		super(size, evictCount);
	}

	@Override
	protected void hitAccess(Object key) {			
		((GDStarElement)m_accessMap.get(key)).update(((Element)key).getCost());		
	}

	@Override
	protected void evict() {
		// set age and remove min element
		if(m_evictCount < m_accessMap.values().size()){
			
			List<GDElement> evictElements = new LinkedList<GDElement>(m_accessMap.values());						
			Collections.sort(evictElements);	
			// LFU_DA_Element objects 
			evictElements = evictElements.subList(0, m_evictCount);				
			// min credit
			m_cacheAge = evictElements.get(0).getCredit();				
			Iterator<Entry<Object, GDElement>> accessIter = m_accessMap.entrySet().iterator();		
			while(accessIter.hasNext() && !evictElements.isEmpty()){
				Entry<Object, GDElement> entry = accessIter.next();

				if(evictElements.contains(entry.getValue())){
					evictElements.remove(entry.getValue());					
					m_cacheMap.remove(entry.getKey());						
					accessIter.remove();
				}	
			}	
			
		}else{	
			m_cacheMap.clear();
			m_accessMap.clear();			
		}		
	}	
	
	@Override
	public List<Object> removeValues(Object value){

		List<Object> removed = super.removeValues(value);
		for(Object o : removed)
			m_accessMap.remove(o);

		return removed;		
	}

	/**
	 * return false, on an error.
	 */
	@Override
	public boolean test(){	
		return(
				size() > m_cacheMaxSize ||
				m_accessMap.size() > m_cacheMaxSize ||
				m_accessMap.size() > size()

		) ? false : true;
	}	

	@Override
	protected void putAccess(Object key) {		
		m_accessMap.put(key, new GDStarElement(m_cacheAge, ((Element)key).getCost(),Double.valueOf(((Set)((Element)key).getObject()).size())));
	}

	/**
	 * Refined inner class for an element to compare on special value depends on hits
	 */
	protected class GDStarElement extends GDElement{
		
		private Integer m_hits = 0;
		private Double m_size = 0D;
		private Double m_cost = 0D;
		
		public GDStarElement(Double age,Double cost,Double size) {
			m_size = size;	
			m_cost= cost;
			update(cost);
		}

		@Override
		public void update(Double cost){
			
			m_cost += cost; m_cost /= 2;			
			m_hits++;			

			super.update(Math.pow(((cost*m_hits)/ m_size ),1/m_beta));			
		}		
	}
}