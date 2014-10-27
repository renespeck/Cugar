package de.uni_leipzig.cc.cache.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.uni_leipzig.cc.cache.CacheInterface;
import de.uni_leipzig.cc.cache.Lfu;

import junit.framework.TestCase;

public class CacheTest_Lfu extends TestCase {	
	
	/** logger */
	public static Logger log4j = Logger.getLogger(CacheTest_Lfu.class);
	
	@Test
	public void testSize(){	
		log4j.debug("Start test ...");
		
		CacheTest.testSize(new Lfu(10,1));		
	}
	
	public void testLfu(){	
		// Lfuda size 10
		CacheInterface cache = new Lfu(10,1);
		// put 20 elements
		for(int i = 0; i < 20; i++)
			cache.put(i, String.valueOf(i));
		
		// size 10
		assertEquals(cache.size(),10);
		
		// test should be true
		assertEquals(true, cache.test());
		
		// cache contains last 10 elements
		for(int i = 10; i < 20; i++)
			assertEquals(String.valueOf(i),cache.get(i));	
		
		// and again
		for(int i = 10; i < 20; i++)
			assertEquals(String.valueOf(i),cache.get(i));	
		
		// put 5 new elements
		for(int i = 20; i < 25; i++){
			cache.put(i, String.valueOf(i));
			assertEquals(String.valueOf(i), cache.get(i));
		}
		
		// only the last is in cache
		assertEquals(String.valueOf(24), cache.get(24));
		assertEquals(null, cache.get(23));		
	}
}