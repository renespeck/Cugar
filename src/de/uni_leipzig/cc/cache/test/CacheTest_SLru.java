package de.uni_leipzig.cc.cache.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.uni_leipzig.cc.cache.CacheInterface;
import de.uni_leipzig.cc.cache.SLru;
import junit.framework.TestCase;

public class CacheTest_SLru extends TestCase {	
	/** logger */
	public static Logger log4j = Logger.getLogger(CacheTest_SLru.class);

	@Test
	public void testSize(){	
		log4j.debug("Start test ...");

		CacheTest.testSize(new SLru(10,1));		
	}

	@Test
	public void test(){	
		log4j.debug("Start test ...");

		CacheInterface cache = new SLru(10,1);			
		// put 20 elements 0-19, 0 is lru
		for(int i = 0; i < 20; i++)
			cache.put(i, String.valueOf(i));
		// in cache yet: 10 - 19
		for(int i = 0; i < 10; i++)
			assertEquals(null,cache.get(i));	
		// size 10 and segment size should be 0
		assertEquals(cache.size(),10);			
		// test should be true
		assertEquals(true, cache.test());
		// cache get last 5 elements 15-19
		for(int i = 15; i < 20; i++)
			assertEquals(String.valueOf(i),cache.get(i));	
		
		// put 5 new elements  20 - 24 and get the last 5 removed elements 10 - 14 
		for(int i = 20; i < 25; i++){
			cache.put(i, String.valueOf(i));
			assertEquals(null, cache.get(i - 10));
		}	
		// put 5 new elements 25-29 end get last 5 removed elements 20- 24 
		for(int i = 25; i < 30; i++){
			cache.put(i, String.valueOf(i));	
			assertEquals(null, cache.get(i - 5));			
		}
		
		// now in lru-segment:[15-19] , lru-list:[25-29]
		
		// get this elements a 10 times
		// this swaps segment and lru-list
		for(int j = 0 ; j < 10; j++){
			// get last 5 elements from segment 
			for(int i = 15; i < 20; i++)
				assertEquals(String.valueOf(i),cache.get(i));

			// size 10
			assertEquals(cache.size(),10);
			// test should be true
			assertEquals(true, cache.test());	

			// get last 5 cold elements 
			for(int i = 25; i < 30; i++)
				assertEquals(String.valueOf(i),cache.get(i));
		}
		
		// now lru-segment:[25-29] , lru:[15-19]
		// put 5 new elements 0-5 end get last 5 removed elements 15-19 
		for(int i = 0; i < 5; i++){
			cache.put(i, String.valueOf(i));	
			assertEquals(null, cache.get(i + 15));			
		}
		// get this new elements 0-5
		for(int i = 0; i < 5; i++)
			assertEquals(String.valueOf(i),cache.get(i));
		
		// put 5 new elements 5-10 end get last 5 removed elements 25-29 
		for(int i = 5; i < 10; i++){
			cache.put(i, String.valueOf(i));	
			assertEquals(null, cache.get(i + 20));			
		}	
		// now lru-segment:[0-5] , lru:[5-10]
		// all other uesed elements not in cache
		for(int i = 10; i < 30; i++){
			assertEquals(null, cache.get(i ));		
		}
	}		
}