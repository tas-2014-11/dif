package com.lumenare.dif;

import java.io.Serializable;
import com.avulet.db.Key;

public class UtilizationMeasurement implements Serializable {
	protected final Boolean _utilized;
	protected final Key _deviceKey;

	public UtilizationMeasurement(Boolean utilized,Key deviceKey) {
		_deviceKey = deviceKey;
		_utilized  = utilized;
	}

	public Boolean isUtilized() {
		return(_utilized);
	}

	public Key getDeviceKey() {
		return(_deviceKey);
	}

	public String toString() {
		return("UtilizationMeasurement(" + _deviceKey + "," + _utilized + ")");
	}
}
