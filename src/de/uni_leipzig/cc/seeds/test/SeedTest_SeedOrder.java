package de.uni_leipzig.cc.seeds.test;
import java.util.LinkedHashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.uni_leipzig.cc.seeds.RP;
import de.uni_leipzig.cc.seeds.InitialOrder;
import de.uni_leipzig.cc.seeds.SeedOrderInterface;

public class SeedTest_SeedOrder extends TestCase {
	/** logger */
	public static Logger log4j = Logger.getLogger(SeedTest_SeedOrder.class);

	private void test(SeedOrderInterface seedOrder){
		log4j.debug("Start test ...");
		final int LOOP = 1000;	
		// add seed
		for(int i = 0; i < LOOP ; i++)
			seedOrder.add(i);

		// get seed		
		for(int i = 0; i < LOOP/2 ; i++)
			assertEquals(i, seedOrder.getBestSeed().intValue());
		
		// black listed
		Set<Integer> blacklist = new LinkedHashSet<Integer>(); 
		for(int i = 0; i < LOOP/2 ; i++)
			blacklist.add(i);
		
		// don't use again
		assertEquals(false,seedOrder.hasUnusedSeeds(blacklist)); 
		
		// get seeds
		for(int i = LOOP/2; i < LOOP ; i++)
			assertEquals(i, seedOrder.getBestSeed().intValue());
		
		// empty seed pool	
		assertEquals(null, seedOrder.getBestSeed()); 	

		// try to use 3 old seeds, we used before
		blacklist = new LinkedHashSet<Integer>(); 
		blacklist.add(3);	blacklist.add(1);	blacklist.add(2);		
		assertEquals(false,seedOrder.addAll(blacklist));

		// online fashion, add 3 new seeds and use it
		Set<Integer> set = new LinkedHashSet<Integer>(); 
		set.add(LOOP + 3);	set.add(LOOP + 1);	set.add(LOOP + 2);		
		assertEquals(true,seedOrder.addAll(set));

		assertEquals(true,seedOrder.hasUnusedSeeds(set)); 				

		assertEquals(LOOP + 3, seedOrder.getBestSeed().intValue());
		assertEquals(LOOP + 1, seedOrder.getBestSeed().intValue());
		assertEquals(LOOP + 2, seedOrder.getBestSeed().intValue());		
	}
	
	
	@Test
	public void test1FifoOrder(){
		test(new InitialOrder());
		test(new RP());
	}	
}
