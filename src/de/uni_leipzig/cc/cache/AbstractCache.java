package de.uni_leipzig.cc.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
/**
* @author rspeck
*/
public abstract class AbstractCache implements CacheInterface {
	
	/** logger */
	public static Logger log4j = Logger.getLogger(AbstractCache.class);
	
	/** cache */
	protected Map<Object, Object> m_cacheMap = new Hashtable<Object, Object>();	

	/** cache size */
	protected final int m_cacheMaxSize;	
	
	/**  evict count */
	protected final int m_evictCount;	
	
	/**
	 * 
	 * @param size
	 * @param evictCount
	 */
	public AbstractCache(int size, int evictCount) {
		m_cacheMaxSize = size;
		m_evictCount = evictCount;		
	}	
	@Override
	public Collection<Object>  values(){
		return m_cacheMap.values();
	}
	@Override
	public Object get(Object key) {

		Object value = m_cacheMap.get(key);		
		if(value != null)
			hitAccess(key);		

		if(log4j.isDebugEnabled()){
			if(value != null)			
				log4j.debug("hit:" + key.toString());
			else		
				log4j.debug("miss: " + key.toString());
		}

		return value;
	}		

	protected abstract void hitAccess(Object key);	
	
	@Override
	public int maxSize() {
		return m_cacheMaxSize;	
	}
	
	@Override
	public Object put(Object key, Object val) {

		if(m_cacheMaxSize > 0){					
			if(size() >= m_cacheMaxSize)
				evict();
			putAccess(key);	

			// debug info
			if(log4j.isDebugEnabled()) 
				log4j.debug("put:" + key.toString());

			return m_cacheMap.put(key, val);
		}return null;
	}

	protected abstract void evict();
	protected abstract void putAccess(Object key);	

	@Override
	public List<Object> removeValues(Object value){
		
		List<Object> removed = new ArrayList<Object>();	
		Iterator<Object> iter = m_cacheMap.keySet().iterator();		
		while(iter.hasNext()){
			Object o = iter.next();
			if(m_cacheMap.get(o) == value){
				removed.add(o);
				iter.remove();				
			}
		}return removed;
	}
	
	@Override
	public int size() {
		return m_cacheMap.size();	
	}
	
	@Override
	public boolean test(){
		return false;
	}
	
	@Override
	public String toString(){
		return m_cacheMap.toString();
	}	
}