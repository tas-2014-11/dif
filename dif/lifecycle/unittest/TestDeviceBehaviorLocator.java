package com.lumenare.dif.lifecycle.unittest;

import junit.framework.*;

import com.avulet.db.Key;
import com.avulet.db.KeyImpl;
import com.lumenare.dif.core.BehaviorNotFoundException;
import com.lumenare.dif.core.DeviceBehavior;

import com.lumenare.dif.lifecycle.DeviceBehaviorLocator;

import java.util.Properties;

public class TestDeviceBehaviorLocator extends TestCase {

	public TestDeviceBehaviorLocator(String name) {
		super(name);
	}

	public static TestSuite suite() {
		return(new TestSuite(TestDeviceBehaviorLocator.class));
	}

	protected Properties _difProps;
	protected Properties _inventoryProps;
	protected String _deviceKeyString;
	protected Key _deviceKey;
	protected String _modelName;

	protected void setUp() {
		_difProps = new Properties();
		_inventoryProps = new Properties();

		_deviceKeyString = "TestKeyZeta";
		_deviceKey = new KeyImpl(_deviceKeyString);

		_modelName = "TestModelZed";



		// stuff the dif data
		String behaviorHandlerName = "dif.simple.SimpleDeviceBehavior";

		setBehaviorHandler(_modelName,behaviorHandlerName);

		//dif.Log.info("_difProps",_difProps);



		// now do inventory data
		String mgmtInterfaceName = "TestMgmtInterfaceNameZulu";

		setInventoryModelName(_deviceKeyString,_modelName);
		setInventoryMgmtInterfaceName(_deviceKeyString,mgmtInterfaceName);

		//dif.Log.info("_inventoryProps",_inventoryProps);
	}

	protected void tearDown() {
		_difProps = null;
		_inventoryProps = null;
		_deviceKeyString = null;
		_deviceKey = null;
		_modelName = null;
	}

	protected void setInventoryModelName(String deviceKey,String modelName) {
		_inventoryProps.setProperty("modelName." + deviceKey,
			modelName);
	}

	protected void setInventoryMgmtInterfaceName(String deviceKey,
			String mgmtInterfaceName) {

		_inventoryProps.setProperty("mgmtInterfaceName." + deviceKey,
			mgmtInterfaceName);
	}

	protected void setBehaviorHandler(String modelName,String behaviorHandlerName) {
		_difProps.setProperty("dif.behavior." + modelName,
			behaviorHandlerName);
	}



	public void testSimple()
			throws BehaviorNotFoundException {

		//DeviceBehaviorLocator dbl = new DeviceBehaviorLocator(_difProps);
		//DeviceBehavior db = dbl.locate(_deviceKey,_inventoryProps);

		DeviceBehaviorLocator dbl = new DeviceBehaviorLocator();
		DeviceBehavior db = dbl.locate(_deviceKey);

		System.out.println("Ook");
		System.out.println(db);

		// FIXME: Compare something to something else to see if it worked.
	}

/*
	public void testSimpleWithBadData() {
		//DeviceBehaviorLocator dbl = new DeviceBehaviorLocator(_difProps);
		DeviceBehaviorLocator dbl = new DeviceBehaviorLocator();

		//setInventoryModelName(_deviceKeyString,_modelName + "THIS_IS_AN_ERROR");

		// Since the dif database doesn't know about our dorked up model name
		// it should throw here.

		// FIXME: But what if we have a legitimate problem that prevents
		// FIXME: from find the right behavior?
		// FIXME: It will throw anyway and we'll never know. :(

		try {
			//DeviceBehavior db = dbl.locate(_deviceKey,_inventoryProps);
			DeviceBehavior db = dbl.locate(_deviceKey);
			fail("An expected exception was not thrown.");
		}
		catch(BehaviorNotFoundException bnfe) {
			System.out.println("Ook");
		}
	}
*/
}
