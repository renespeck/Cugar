package de.uni_leipzig.cc.coyotecache.test;

import org.apache.log4j.PropertyConfigurator;

import de.uni_leipzig.cc.cache.test.CacheTestAll;
import de.uni_leipzig.cc.seeds.test.SeedTestAll;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	static {
		PropertyConfigurator.configure("Log4j.properties");
	}

	public static Test suite() {

		TestSuite suite = new TestSuite();

		suite.addTest(CacheTestAll.suite());
		suite.addTest(SeedTestAll.suite());

		suite.addTestSuite(SeedCacheTest.class);

		return suite;
	}
}
