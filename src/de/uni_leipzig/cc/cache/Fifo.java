package de.uni_leipzig.cc.cache;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
/**
* @author rspeck
*/
public class Fifo extends AbstractCache{

	protected Queue<Object> m_access = new LinkedList<Object>();

	public Fifo(int size, int evictCount) {
		super(size,evictCount);
	}	
	@Override
	protected void evict() {
			
		for(int i = 0 ; i < m_evictCount && !m_access.isEmpty(); i++){
			 Object o = m_access.poll();
			m_cacheMap.remove(o);		
		}
	}
	
	@Override
	protected void hitAccess(Object key) { /**/	}

	@Override
	protected void putAccess(Object key) {
		m_access.add(key);
	}
	
	@Override
	public List<Object> removeValues(Object value){
		List<Object> remove = super.removeValues(value);
		m_access.removeAll(remove);
		return remove;		
	}
	
	/**
	 * return false, if we have an error.
	 */
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