package de.uni_leipzig.cc.cache.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.uni_leipzig.cc.cache.CacheInterface;
import de.uni_leipzig.cc.cache.LfuDA;

import junit.framework.TestCase;
/**
 * Test with integer of Lfu with dynamic aging.
 * 
 * @author rspeck
 *
 */
public class CacheTest_Lfuda extends TestCase {	
	/** logger */
	public static Logger log4j = Logger.getLogger(CacheTest_Lfuda.class);
	
	@Test
	public void testSize(){	
		log4j.debug("Start test ...");
		
		CacheTest.testSize(new LfuDA(10,1));		
	}
	
	
	// test one
	@Test
	public void testLfudaOne(){	
		log4j.debug("Start test ...");
		
		// Lfuda size 10 evict 1
		CacheInterface cache = new LfuDA(10,1);
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
		// and again
		for(int i = 10; i < 20; i++)
			assertEquals(String.valueOf(i),cache.get(i));
		// System.out.println(cache);
		// put 5 new elements
		for(int i = 20; i < 25; i++){
			cache.put(i, String.valueOf(i));
			assertEquals(String.valueOf(i), cache.get(i));
		}
		// System.out.println(cache);
		// not only the last is in cache
		// TODO  : that isnt a good test
		assertEquals(String.valueOf(24), cache.get(24));
		assertEquals(String.valueOf(23), cache.get(23));	
		
		// test should be true
		assertEquals(true, cache.test());
	}
	// test two
	@Test
	public void testLfudaTwo(){	
		// Lfuda size 3
		// System.out.println("put 1,2,3\n");	
		CacheInterface cache = new LfuDA(3,1);
		cache.put(1, "1");
		cache.put(2, "2");
		cache.put(3, "3");
		// System.out.println(cache + "\n");	  // 1
		
		// System.out.println("put 4\n");
		cache.put(4, "4");
		// System.out.println(cache + "\n");	 // 2
		
		// System.out.println("get 1\n");
		assertEquals(null, cache.get(1));
		// System.out.println(cache + "\n");	 // 3
		
		// System.out.println("put 5\n");
		cache.put(5, "5");
		// System.out.println(cache + "\n");	 // 4
		
		// System.out.println("get 2,3,3,3 \n");
		assertEquals(null, cache.get(2));
		assertEquals("3", cache.get(3));
		assertEquals("3", cache.get(3));
		assertEquals("3", cache.get(3));
		// System.out.println(cache + "\n");	 // 5
		
		// System.out.println("put 6\n");
		cache.put(6, "6");
		assertEquals(null, cache.get(4));
		// System.out.println(cache + "\n");	 // 6
		
		// System.out.println("put 7\n");
		cache.put(7, "7");
		assertEquals(null, cache.get(5));
		// System.out.println(cache + "\n");	 // 7
		
		// System.out.println("get 6,7\n");
		assertEquals("6", cache.get(6));
		assertEquals("7", cache.get(7));		
		// System.out.println(cache + "\n");	
		

		// System.out.println("get 6,7\n");
		assertEquals("6", cache.get(6));
		assertEquals("7", cache.get(7));		
		// System.out.println(cache + "\n");
		
		
		// System.out.println("put 8\n");
		assertEquals(null, cache.put(8,"8"));			
		// System.out.println(cache + "\n");			
		
		// System.out.println("put 9\n");
		assertEquals(null, cache.put(9,"9"));			
		// System.out.println(cache + "\n");		
		
		// System.out.println("---------------------------\n");
	}
	@Test
	public void testLfudaThree(){	
		// Lfuda size 3
		CacheInterface cache = new LfuDA(3,1);
		cache.put(1, "1");
		cache.put(2, "2");
		cache.put(3, "3");
		// System.out.println(cache);
		// System.out.println();
		
		cache.put(4, "4");
		assertEquals(null, cache.get(1));
		cache.put(5, "5");
		assertEquals(null, cache.get(2));
		assertEquals("3", cache.get(3));
		assertEquals("3", cache.get(3));
		assertEquals("3", cache.get(3));
		// System.out.println(cache);
		// System.out.println();
		
		cache.put(6, "6");
		assertEquals(null, cache.get(4));
		cache.put(7, "7");
		assertEquals(null, cache.get(5));
		// System.out.println(cache);
		// System.out.println();
		
		assertEquals("6", cache.get(6));
		assertEquals("7", cache.get(7));
		// System.out.println(cache);
		// System.out.println();
		
		assertEquals(null, cache.put(8,"8"));			
		// System.out.println(cache);
		// System.out.println();				
	}	
}
