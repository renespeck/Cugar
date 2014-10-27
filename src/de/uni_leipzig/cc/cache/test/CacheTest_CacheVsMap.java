package de.uni_leipzig.cc.cache.test;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import de.uni_leipzig.cc.cache.CacheInterface;
import de.uni_leipzig.cc.cache.Element;
import de.uni_leipzig.cc.cache.Lfu;

import junit.framework.TestCase;

public class CacheTest_CacheVsMap extends TestCase {
	
	/** logger */
	public static Logger log4j = Logger.getLogger(CacheTest_CacheVsMap.class);

	public void testCacheVsMap(){
		log4j.debug("Start test ...");

		CacheInterface cache = new Lfu(2,1);		
		
		TreeSet<Integer> ts = new TreeSet<Integer>();
		ts.add(1);ts.add(2);ts.add(3);ts.add(4);
		
		final int cost = 1;
		Element e = new Element(ts,cost);
		Element ee = new Element(ts,cost);

		Integer id = 0;
		assertNull(cache.get(e)); 		
		assertEquals(cache.put(e, id),null);
		assertEquals(cache.get(e), id);
		assertEquals(cache.get(ee), id);
		assertFalse(e == ee);
		assertTrue(e.equals(ee));
		
		///////////////////////////////////
		
		Integer i = new Integer(1);
		Integer ii = new Integer(1);	
		
		Map<Integer,Integer> map = new HashMap<Integer,Integer>();

		assertNull(map.get(i)); 		
		assertEquals(map.put(i, id),null);
		assertEquals(map.get(i), id);
		assertEquals(map.get(ii), id);
		assertFalse(i == ii);
		assertTrue(i.equals(ii));		
	}
}
