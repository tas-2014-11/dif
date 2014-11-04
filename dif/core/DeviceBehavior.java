package com.lumenare.dif.core;

import com.lumenare.common.domain.attribute.AttributeCollection;

/**
 * Defines behaviors supported on all devices.
 *
 */

public interface DeviceBehavior {

	/**
	 * 
	 * Establishes control over a device.
	 * 
	 * @throws DeviceBehaviorException on any error
	 */

	public void acquireControl() throws DeviceBehaviorException;

	/**
	 * Apply a configuration to a device.
	 * 
	 * @param	configuration
	 * 		The configuration to be applied
	 * 
	 * @throws	DeviceBehaviorException on any error
	 */

	public void configure(AttributeCollection configuration)
		throws DeviceBehaviorException;
		
	/**
	 * 
	 * Extract the configuration information over a device.
	 * 
	 * @throws DeviceBehaviorException on any error
	 */

	public AttributeCollection extractConfiguration() throws DeviceBehaviorException;

	/**
	 * 
	 * Measures whether a device is currently "utilized".
	 * 
	 * @throws DeviceBehaviorException on any error
	 * @return true if the device is "utilized", false if not
	 */

	public boolean measureUtilization() throws DeviceBehaviorException;
}
