package de.uni_leipzig.cc.cache;

import java.util.Collection;
import java.util.List;
/**
* @author rspeck
*/
public interface CacheInterface {
	
	/** 
	 * Returns the value to which the specified key is cached, 
	 * or null if the cache dosn't contain the key.
	 * 
	 * @param key the key whose associated value is to be returned 
	 * @return value the value to which the specified key is cached, 
	 * or null if the cache dosn't contain the key
	 */
	public Object get(Object key);

	/**
	 * 
	 * Associates the specified value with the specified key 
	 * in the cache. If the cache previously contained the key, 
	 * the old value is replaced. 
	 * 
	 * @param key with which the specified value is to be associated
	 * @param value to be associated with the specified key 
	 * @return the previous value associated with key, 
	 * or null if there was no caching for key
	 */
	public Object put(Object key, Object value);
	/**
	 * Removes all objects with the value from cache map.
	 * 
	 * @param value
	 * @return removed objects
	 */
	public List<Object> removeValues(Object value);
	
	/**
	 * Returns the cache size.
	 * 
	 * @return the size of the cache
	 */
	public int size();
	
	/**
	 * Returns the cache max size.
	 * 
	 * @return the max size of the cache
	 */
	public int maxSize();	
	
	/**
	 * This method should be used for testing.
	 * Returns false on an error.
	 * 
	 * @return true if the cache works fine
	 */
	public boolean test();
	public Collection<Object> values();
}