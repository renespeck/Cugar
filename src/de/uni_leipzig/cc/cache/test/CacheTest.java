package de.uni_leipzig.cc.cache.test;


import de.uni_leipzig.cc.cache.CacheInterface;

import org.apache.log4j.Logger;
import org.junit.Test;

import junit.framework.TestCase;

public class CacheTest extends TestCase {	
	/** logger */
	public static Logger log4j = Logger.getLogger(CacheTest.class);
	
	@Test
	public static void testSize(CacheInterface cache){
		log4j.debug("Start test ...");
		
		final int size = cache.maxSize();		
		
		// fill
		for(int i = 0;  i < 2  * size ; i++){
			assertNull(cache.get(i)); 
			assertNull(cache.put(i, i));
		}
		assertEquals(cache.size(),10);	
	}
}