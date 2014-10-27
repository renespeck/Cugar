package de.uni_leipzig.cc.seeds.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SeedTestAll {

	public static Test suite() {

		TestSuite suite = new TestSuite();		
		
		suite.addTestSuite(SeedTest_SeedOrder.class);		
		suite.addTestSuite(SeedTest_FifoOrder.class);
		suite.addTestSuite(SeedTest_FixRandomOrder.class);	
		suite.addTestSuite(SeedTest_RandomOrder.class);	
		suite.addTestSuite(SeedTest_InitialOrder.class);	
				
		return suite;
	}
}
