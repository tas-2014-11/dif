package com.lumenare.dif.core.unittest;

import junit.framework.*;

public class AllTests {
	public static Test suite() { 
		TestSuite suite = new TestSuite(); 
		suite.addTest(com.lumenare.dif.core.unittest.TestModel.suite());
		return(suite); 
	} 
}
