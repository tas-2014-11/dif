package com.lumenare.dif.lifecycle.unittest;

import junit.framework.*;

import java.util.ArrayList;
import java.util.List;

import com.avulet.db.Key;
import com.avulet.db.KeyImpl;

import com.lumenare.common.domain.attribute.AttributeCollection;
import com.lumenare.common.domain.attribute.AttributeFactory;
import com.lumenare.common.domain.attribute.ListAttribute;
import com.lumenare.common.domain.attribute.StringAttribute;
import com.lumenare.common.domain.attribute.adapter.AttributeCollectionImpl;
import com.lumenare.common.domain.attribute.names.DeviceAttributes;

import com.lumenare.dif.core.BehaviorNotFoundException;
import com.lumenare.dif.core.DeviceBehaviorException;

import com.lumenare.dif.lifecycle.DLCMaintTransitions;
import com.lumenare.dif.lifecycle.DLCSessionTransitions;
import com.lumenare.dif.lifecycle.DeviceBehaviorLocator;
import com.lumenare.dif.lifecycle.DeviceLifecycle;
import com.lumenare.dif.lifecycle.IllegalTransitionException;

import java.util.Properties;

public class TestDeviceLifecycle extends TestCase {

	public TestDeviceLifecycle(String name) {
		super(name);
	}

	public static TestSuite suite() {
		return(new TestSuite(TestDeviceLifecycle.class));
	}

	DeviceLifecycle _dlc;
	Key _deviceKey;
	AttributeCollection _configuration;

	// FIXME: make this constructor not throw (or throw something sane)
	protected void setUp()
			throws
			IllegalTransitionException,
			BehaviorNotFoundException,
			DeviceBehaviorException {

		_deviceKey = new KeyImpl("12274");
		_dlc = new DeviceLifecycle(_deviceKey);

		String key;
		String value;

		key = DeviceAttributes.CONFIG_IMAGENAME_RUNTIME_LIST;
		value = "mySpecialImage";
		List imageList = new ArrayList();
		imageList.add(value);
		ListAttribute configImagenameRuntime =
			AttributeFactory.createList(key,imageList);

		key = DeviceAttributes.CONFIG_TEXT_STRING;
		value = "Hello World!";
		StringAttribute configText =
			AttributeFactory.createString(key,value);

		_configuration = new AttributeCollectionImpl();
		_configuration.addAttribute(configImagenameRuntime);
		_configuration.addAttribute(configText);
	}

	protected void tearDown() {
		_deviceKey = null;
	}

	public void testNormalSessionPath() throws IllegalTransitionException,DeviceBehaviorException {
		runNormalSessionPath(_dlc);
	}

	protected void runNormalSessionPath(DLCSessionTransitions dlcst)
			throws IllegalTransitionException,DeviceBehaviorException {

		dlcst.XgrabDeviceForSession(_configuration);
		//dlcst.XdeviceIsReadyForEvilUsers();
		dlcst.XreleaseDeviceFromSession();
	}

	public void testNormalMaintPath() throws IllegalTransitionException {
		runNormalMaintPath(_dlc);
	}

	// TODO:  I'm not implementing Maint transitions right now.

	protected void runNormalMaintPath(DLCMaintTransitions dlcmt)
			throws IllegalTransitionException {

		//dlcmt.XremoveFromService();
		//dlcmt.XreturnToService();
		dlcmt.XdeviceDeath();
	}

	public void testCombinedPaths() throws IllegalTransitionException,DeviceBehaviorException {
			runNormalSessionPath(_dlc);
			runNormalSessionPath(_dlc);
			runNormalSessionPath(_dlc);

			//runNormalMaintPath(_dlc);
			//runNormalMaintPath(_dlc);
			//runNormalMaintPath(_dlc);

			runNormalSessionPath(_dlc);
			//runNormalMaintPath(_dlc);

			runNormalSessionPath(_dlc);
			runNormalMaintPath(_dlc);
	}

	public void testInvalidPath1() {
		try {
			_dlc.XdeviceBirth();
			fail();
		}
		// FIXME: Um...
		catch(IllegalTransitionException ite) { }
		catch(DeviceBehaviorException dbe) { }
	}

	public void testInvalidPath2() {
		try {
			_dlc.XgoToSleep();
			fail();
		}
		catch(IllegalTransitionException ite) { }
	}
}


// FIXME: Write a device behavior which misbehaves to make sure
// FIXME: the engine can handle it.
