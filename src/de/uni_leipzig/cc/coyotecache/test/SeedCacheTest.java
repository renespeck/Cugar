package de.uni_leipzig.cc.coyotecache.test;

import java.util.Collections;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import de.uni_leipzig.cc.cache.Fifo;
import de.uni_leipzig.cc.coyotecache.CoyoteCache;
import junit.framework.TestCase;

public class SeedCacheTest extends TestCase {
	
	static {
		PropertyConfigurator.configure("Log4j.properties");
	}
	
	/** logger */
	public static Logger log4j = Logger.getLogger(SeedCacheTest.class);

	public void test() {
		log4j.debug("Start test ...");
		
		CoyoteCache coyoteCache = new CoyoteCache(new Fifo(2, 1));
		////////////////////////////////////////////////////////////
		// 1. cluster
		TreeSet<Integer> one1 =   new TreeSet<Integer>();
		TreeSet<Integer> two1 =   new TreeSet<Integer>();
		TreeSet<Integer> final1 = new TreeSet<Integer>();

		Collections.addAll(one1,    1);
		Collections.addAll(two1,    1,2);
		Collections.addAll(final1,  1,2,3);
		// 2. cluster
		TreeSet<Integer> one2 =   new TreeSet<Integer>();
		TreeSet<Integer> two2 =   new TreeSet<Integer>();
		TreeSet<Integer> final2 = new TreeSet<Integer>();

		Collections.addAll(one2,   4);
		Collections.addAll(two2,   4,5);
		Collections.addAll(final2, 4,5,6);		
		// 3. cluster
		TreeSet<Integer> one3 =   new TreeSet<Integer>();
		TreeSet<Integer> two3 =   new TreeSet<Integer>();
		TreeSet<Integer> final3 = new TreeSet<Integer>();

		Collections.addAll(one3,   8);
		Collections.addAll(two3,   8,9);
		Collections.addAll(final3, 8,9,10);	
		////////////////////////////////////////////////////////////
		// get 1.		
		assertNull(coyoteCache.get(one1));
		assertNull(coyoteCache.get(two1));
		assertNull(coyoteCache.get(final1));
		// put 1.
		coyoteCache.put(final1);
		// get 1.
		assertNull(coyoteCache.get(one1));
		assertEquals(coyoteCache.get(two1), final1);
		assertEquals(coyoteCache.get(final1), final1);
		// get 2.
		assertNull(coyoteCache.get(one2));
		assertNull(coyoteCache.get(two2));
		assertNull(coyoteCache.get(final2));
		// put 2.
		coyoteCache.put(final2);
		// get 2.
		assertNull(coyoteCache.get(one2));
		assertEquals(coyoteCache.get(two2), final2);
		assertEquals(coyoteCache.get(final2), final2);
		// get 1.
		assertNull(coyoteCache.get(one1));
		assertEquals(coyoteCache.get(two1), final1);
		assertEquals(coyoteCache.get(final1), final1);
		// get 3.
		assertNull(coyoteCache.get(one3));
		assertNull(coyoteCache.get(two3));
		assertNull(coyoteCache.get(final3));
		// put 3.		
		coyoteCache.put(final3);
		// get 3.
		assertNull(coyoteCache.get(one3));
		assertEquals(coyoteCache.get(two3), final3);
		assertEquals(coyoteCache.get(final3), final3);
		// get 1.
		assertNull(coyoteCache.get(one1));
		assertNull(coyoteCache.get(two1));
		// 
		assertEquals(coyoteCache.get(two2), final2);
	}
}