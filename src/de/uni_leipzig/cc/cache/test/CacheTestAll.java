package de.uni_leipzig.cc.cache.test;

import org.apache.log4j.PropertyConfigurator;

import junit.framework.Test;
import junit.framework.TestSuite;

public class CacheTestAll {
	static{
		PropertyConfigurator.configure("Log4j.properties");
	}
  public static Test suite() {
	 
	  
    TestSuite suite = new TestSuite();
    
    suite.addTestSuite(CacheTest_CacheVsMap.class);
    suite.addTestSuite(CacheTest_Cost.class);
    suite.addTestSuite(CacheTest_Fifo.class);    
    suite.addTestSuite(CacheTest_Fifo2ndChance.class);
    suite.addTestSuite(CacheTest_Lfu.class);
    suite.addTestSuite(CacheTest_Lfuda.class);   
    suite.addTestSuite(CacheTest_Lru.class);
    suite.addTestSuite(CacheTest_SLru.class);

    return suite;
  }
}
