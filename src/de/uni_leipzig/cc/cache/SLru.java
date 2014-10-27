package de.uni_leipzig.cc.cache;

import java.util.LinkedList;
import java.util.List;
/**
* @author rspeck
*/
public class SLru extends AbstractCache {

	protected final List<Object> m_access = new LinkedList<Object>();
	protected final List<Object> m_accessSeg = new LinkedList<Object>();

	/** percentage of cache memory to use for segment */
	private final float SEGMENT_BORDER = 0.5f;
	/** # of elements in segment */
	private final int SEGMENT_SIZE = (int) (m_cacheMaxSize * SEGMENT_BORDER);

	/**
	 * Constructor calls super constructor with same parameters.
	 */
	public SLru(int size, int evictCount) {
		super(size, evictCount);
	}

	/**
	 * Evicts the 1st object  form m_access list m_evictCount times
	 */
	@Override
	protected void evict() {
		for(int i = 0; i < m_evictCount; i++){
			
			if(!m_access.isEmpty()){
				Object o = m_access.remove(0);				
				m_cacheMap.remove(o);
			}
						
			else if(!m_accessSeg.isEmpty()){
				Object o = m_accessSeg.remove(0);				
				m_cacheMap.remove(m_accessSeg.remove(0));
			}
		}
	}

	@Override
	protected void hitAccess(Object key) {	
		
		if(!m_access.remove(key))
			m_accessSeg.remove(key);			

		m_accessSeg.add(key);	
		
		if(m_accessSeg.size() > SEGMENT_SIZE)
			m_access.add(m_accessSeg.remove(0));	
		
	}	
	
	@Override
	protected void putAccess(Object key) {			
		m_access.add(key);			
	}
		
	@Override
	public List<Object> removeValues(Object value){

		List<Object> removed = super.removeValues(value);
		m_access.removeAll(removed);
		m_accessSeg.removeAll(removed);

		return removed;		
	}

	@Override
	public boolean test(){	
		return (size() > m_cacheMaxSize || (m_access.size() + m_accessSeg.size()) > m_cacheMaxSize) ? false : true;
	}

	@Override
	public String toString(){		
		return super.toString() + "\n" + m_access.toString();		
	}
}