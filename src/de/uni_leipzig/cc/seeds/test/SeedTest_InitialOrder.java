package de.uni_leipzig.cc.seeds.test;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.uni_leipzig.cc.seeds.InitialOrder;
import de.uni_leipzig.cc.seeds.SeedOrderInterface;

public class SeedTest_InitialOrder extends TestCase {

	/** logger */	
	public static Logger log4j = Logger.getLogger(SeedTest_InitialOrder.class);
	
	@Test
	public void testInitialOrder(){
		
		log4j.debug("Start test getBestSeed ...");
		
		List<Integer>  list = new ArrayList<Integer>();			
		for(int i = 0; i < 10; list.add(i++));

		Collections.shuffle(list);				
		
		SeedOrderInterface initialOrder = new InitialOrder();		
		initialOrder.addAll(new LinkedHashSet<Integer>(list));
		
		for(Integer i : list )
			assertEquals(i , initialOrder.getBestSeed());			
	
		assertEquals(null , initialOrder.getBestSeed());			
	}
	@Test
	public void testInitialOrder2(){
		
		log4j.debug("Start test hasUnusedSeeds ...");
		
		List<Integer>  list = new ArrayList<Integer>();			
		for(int i = 0; i < 10; list.add(new Integer(i++)));			
		
		SeedOrderInterface initialOrder = new InitialOrder();		
		initialOrder.addAll(new LinkedHashSet<Integer>(list));
		
		for(int i = 0; i < 5; i++)
			assertEquals(i , initialOrder.getBestSeed().intValue());
		
		Set<Integer> set = new HashSet<Integer>();
		Collections.addAll(set,5,6,7,8,9);
			
		assertEquals(true , initialOrder.hasUnusedSeeds(set));
		
		for(int i = 5; i < 10-1; i++)
			assertEquals(new Integer(i) , initialOrder.getBestSeed());
		
		assertEquals(true , initialOrder.hasUnusedSeeds(set));
		assertEquals(9 , initialOrder.getBestSeed().intValue());
		assertEquals(false , initialOrder.hasUnusedSeeds(set));		
	}
}
