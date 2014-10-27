package de.uni_leipzig.cc.cache.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.uni_leipzig.cc.cache.CacheInterface;
import de.uni_leipzig.cc.cache.Fifo2ndChance;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Test;


public class CacheTest_Fifo2ndChance extends TestCase {	
	
	/** logger */
	public static Logger log4j = Logger.getLogger(CacheTest_Fifo2ndChance.class);
	
	@Test
	public void testSize(){	
		log4j.debug("Start test ...");
		
		CacheTest.testSize(new Fifo2ndChance(10,1));		
	}
		
	@Test
	public void testFifo2ndChanceInteger(){		
		log4j.debug("Start test ...");
		
		
		boolean verbose = false;
		final int size = 10;				
		CacheInterface cache = new Fifo2ndChance(size,1);	
	
		// fill
		for(int i = 0;  i < size ; i++){
			// get
			assertNull(cache.get(i)); 
			// put
			assertNull(cache.put(i, i));
		}
		// cache full
		if(verbose)
			System.out.println(cache.toString());
				
		// get all, that sets the 2nd chance state
		for(int i = 0;  i < size ; i++){
			// get
			assertEquals(cache.get(i),i); 		
		}
		if(verbose)
			System.out.println(cache.toString());
		
		// fill with new elements, so that is Fifo behavior
		for(int i = size;  i < 2 * size ; i++){
			// get elements with 2nd state
			assertEquals(cache.get(i - size),i - size);
			// get unknown elements
			assertNull(cache.get(i)); 
			// put unknown elements
			assertNull(cache.put(i, i));
			// elements with 2nd chance should deleted
			assertNull(cache.get(i - size));
		}
		// cache full with new elements			
		
		// set 2nd chance to the half of cache
		for(int i = size;  i < (size + size/2) ; i++)
			assertEquals(cache.get(i),i); 
		
		// fill with new elements, so that is Fifo2ndChance
		for(int i = 0;  i < size/2 ; i++){
			assertNull(cache.get(i)); 
			assertNull(cache.put(i, i));
			}
		
		System.out.println(cache.toString());
		// get the half of old elements (2nd elements)
		for(int i = size;  i < (size + size/2) ; i++)
			assertEquals(cache.get(i),i); 
		
		// get the half of new elements and set 2nd state
		for(int i = 0;  i < size/2 ; i++)
			assertEquals(cache.get(i),i); 
	}	

	public void testFifo2ndChance(){
		
		CacheInterface cache = new Fifo2ndChance(10,1);
		List<Integer> m_int_list = null;
		
		m_int_list = new ArrayList<Integer>();		
		for(int i = 0; i < 10; i++)
			m_int_list.add(i);
		
		m_int_list.addAll(m_int_list);
		m_int_list.addAll(m_int_list);		
		Collections.shuffle(m_int_list);
		
		for(int i = 0; i < m_int_list.size(); i++)			
			if(cache.get(m_int_list.get(i)) == null)
				assertNull(cache.put(m_int_list.get(i),m_int_list.get(i)));	
			
		assertEquals(cache.size(),10);
		assertNull(cache.put(11, 11));
		assertNull(cache.put(12, 12));		
		assertEquals(cache.size(),10);
		assertEquals(cache.get(11),11);
		assertEquals(cache.get(12),12);
		assertEquals(cache.size(),10);	
		
		for(int j = 100 ; j < 110; j++)
			assertNull(cache.put(j,j));
		
		assertNull(cache.get(100));
		assertEquals(cache.get(11),11);
		assertEquals(cache.get(12),12);
		
		for(int j = 110 ; j < 119; j++)
			assertNull(cache.put(j,j));
		
		assertNull(cache.get(100));
		assertNull(cache.get(11));
		assertNull(cache.get(12));
			
		//System.out.println(cache);
	}
}