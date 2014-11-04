package com.lumenare.dif.lifecycle;

import com.lumenare.common.domain.attribute.AttributeCollection;
import com.lumenare.dif.core.DeviceBehaviorException;

public interface DLCSessionTransitions {
	public void XgrabDeviceForSession(AttributeCollection configuration)
		throws IllegalTransitionException, DeviceBehaviorException;

	public void XreleaseDeviceFromSession()
		throws IllegalTransitionException,DeviceBehaviorException;

	public AttributeCollection XextractConfiguration()
		throws IllegalTransitionException, DeviceBehaviorException;

	public boolean XmeasureUtilization()
		throws IllegalTransitionException, DeviceBehaviorException;
}
