package de.uni_leipzig.cc.seeds.test;
import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.uni_leipzig.cc.seeds.RandomOrder;
import de.uni_leipzig.cc.seeds.SeedOrderInterface;

public class SeedTest_RandomOrder extends TestCase {
	
	/** logger */
	public static Logger log4j = Logger.getLogger(SeedTest_RandomOrder.class);
	
	@Test
	public void testRandomOrder(){
		log4j.debug("Start test ...");
		
		final int LOOP = 1000;
		
		SeedOrderInterface roOne = new RandomOrder();
		for(int i = 0; i < LOOP ; i++)
			roOne.add(i);
		
		SeedOrderInterface roTwo = new RandomOrder();
		for(int i = 0; i < LOOP ; i++)
			roTwo.add(i);
		
		int count_true = 0;
		for(int i = 0; i < LOOP ; i++){
			Integer seedOne = roOne.getBestSeed();
			Integer seedTwo = roTwo.getBestSeed();
			if(seedOne == seedTwo)
				count_true++;
		}		
		assertTrue(count_true != LOOP); 
	}
}
