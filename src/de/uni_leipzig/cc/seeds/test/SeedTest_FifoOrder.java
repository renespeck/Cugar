package de.uni_leipzig.cc.seeds.test;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.uni_leipzig.cc.seeds.RP;
import de.uni_leipzig.cc.seeds.SeedOrderInterface;

public class SeedTest_FifoOrder extends TestCase {
	/** logger */
	public static Logger log4j = Logger.getLogger(SeedTest_FifoOrder.class);
	@Test
	public void testFifoOrder(){
		log4j.debug("Start test ...");
		
		SeedOrderInterface seedOrder = new RP();

		Set<Integer> seeds = new LinkedHashSet<Integer>();
		Collections.addAll(seeds, 10,2,8,4,6);
		// init seeds
		seedOrder.addAll(seeds);
		// get next seed 
		assertEquals(10, seedOrder.getBestSeed().intValue());
		// add clusters (10,11), (10,11,8), (10,11,8,7,2)
		{
			Set<Integer> cluster = new LinkedHashSet<Integer>();
			Collections.addAll(cluster,10,11);

			assertEquals(0, seedOrder.getUnusedSeeds(cluster).size());		
			assertEquals(false, seedOrder.hasUnusedSeeds(cluster));		
			assertEquals(false, seedOrder.update(cluster));					
		}
		{
			Set<Integer> cluster = new LinkedHashSet<Integer>();
			Collections.addAll(cluster,10,11,8);
			assertEquals(1, seedOrder.getUnusedSeeds(cluster).size());		
			assertEquals(true, seedOrder.hasUnusedSeeds(cluster));		
			assertEquals(true, seedOrder.update(cluster));					
		}
		{
			Set<Integer> cluster = new LinkedHashSet<Integer>();
			Collections.addAll(cluster,10,11,8,7,2);

			assertEquals(2, seedOrder.getUnusedSeeds(cluster).size());		
			assertEquals(true, seedOrder.hasUnusedSeeds(cluster));		
			assertEquals(true, seedOrder.update(cluster));					
		}
		// get next seed 
		assertEquals(8, seedOrder.getBestSeed().intValue());
		// add clusters (8,9), (8,9,4,2)
		{
			Set<Integer> cluster = new LinkedHashSet<Integer>();
			Collections.addAll(cluster,8,9);

			assertEquals(0, seedOrder.getUnusedSeeds(cluster).size());		
			assertEquals(false, seedOrder.hasUnusedSeeds(cluster));		
			assertEquals(false, seedOrder.update(cluster));					
		}
		{
			Set<Integer> cluster = new LinkedHashSet<Integer>();
			Collections.addAll(cluster,8,9,4,2);

			assertEquals(2, seedOrder.getUnusedSeeds(cluster).size());		
			assertEquals(true, seedOrder.hasUnusedSeeds(cluster));		
			assertEquals(true, seedOrder.update(cluster));					
		}
		// get next seed
		assertEquals(2, seedOrder.getBestSeed().intValue());
		// add clusters (2,3,5,9)
		{
			Set<Integer> cluster = new LinkedHashSet<Integer>();
			Collections.addAll(cluster,2,3,5,9);

			assertEquals(0, seedOrder.getUnusedSeeds(cluster).size());		
			assertEquals(false, seedOrder.hasUnusedSeeds(cluster));		
			assertEquals(false, seedOrder.update(cluster));					
		}
		
		// add new seeds (online)
		seedOrder.add(20);
		seedOrder.add(22);		
		
		// get next seed
		assertEquals(4, seedOrder.getBestSeed().intValue());
		// add clusters (4,19), (4,19,22)
		{
			Set<Integer> cluster = new LinkedHashSet<Integer>();
			Collections.addAll(cluster,4,19);

			assertEquals(0, seedOrder.getUnusedSeeds(cluster).size());		
			assertEquals(false, seedOrder.hasUnusedSeeds(cluster));		
			assertEquals(false, seedOrder.update(cluster));					
		}	
		{
			Set<Integer> cluster = new LinkedHashSet<Integer>();
			Collections.addAll(cluster,4,19,22);

			assertEquals(1, seedOrder.getUnusedSeeds(cluster).size());		
			assertEquals(true, seedOrder.hasUnusedSeeds(cluster));		
			assertEquals(true, seedOrder.update(cluster));					
		}			
		// get next seed
		assertEquals(22, seedOrder.getBestSeed().intValue());
		// get next seed
		assertEquals(6, seedOrder.getBestSeed().intValue());
		// get next seed
		assertEquals(20, seedOrder.getBestSeed().intValue());		
		// get next seed
		assertEquals(null, seedOrder.getBestSeed());
	}	
}