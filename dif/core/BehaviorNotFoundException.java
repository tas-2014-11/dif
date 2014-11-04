package com.lumenare.dif.core;

// FIXME: Does this Exception belong in core or lifecycle ?

// FIXME: some caller is probably gonna want to examine the wrapped exception.
// FIXME: It should probably be a Throwable instead of an Exception.

/*
public class BehaviorNotFoundException extends Exception {
	protected DeviceAttributes _deviceAttributes;
	protected Exception _exception;

	// _deviceAttributes will be null
	public BehaviorNotFoundException(Exception exception) {
		_exception = exception;
	}

	// _exception will be null
	public BehaviorNotFoundException(DeviceAttributes deviceAttributes) {
		_deviceAttributes = deviceAttributes;
	}

	public BehaviorNotFoundException(DeviceAttributes deviceAttributes,
			Exception exception) {

		_deviceAttributes = deviceAttributes;
		_exception = exception;
	}

	public String toString() {
		String s = this.getClass().getName();
		s += "(";
		s += _deviceAttributes;
		s += ",";
		s += _exception;
		s += ")";
		return(s);
	}
}
*/

import com.avulet.db.Key;

public class BehaviorNotFoundException extends Exception {
	protected Key _deviceKey;
	protected Exception _exception;

	public BehaviorNotFoundException(Exception exception) {
		_exception = exception;
	}

	public BehaviorNotFoundException(Key deviceKey,Exception exception) {
		_deviceKey = deviceKey;
		_exception = exception;
	}

	public BehaviorNotFoundException(Key deviceKey) {
		_deviceKey = deviceKey;
	}

	public String toString() {
		String s = this.getClass().getName();
		s += "(";
		s += _deviceKey;
		s += ",";
		s += _exception;
		s += ")";
		return(s);
	}
}
