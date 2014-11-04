package com.lumenare.dif.lifecycle.unittest;

import junit.framework.*;

public class AllTests {
	public static Test suite() { 
		TestSuite suite = new TestSuite(); 
		suite.addTest(com.lumenare.dif.lifecycle.unittest.TestDeviceBehaviorLocator.suite());
		suite.addTest(com.lumenare.dif.lifecycle.unittest.TestDeviceLifecycle.suite());
		suite.addTest(com.lumenare.dif.lifecycle.unittest.TestState.suite());
		return(suite); 
	} 
}
