package com.lumenare.dif.lifecycle;

import com.lumenare.dif.core.DeviceBehaviorException;

public interface DLCAutoTransitions {
	public void XdeviceBirth() throws IllegalTransitionException,DeviceBehaviorException;
	public void XgoToSleep() throws IllegalTransitionException;
	public void XdeviceIsReadyForEvilUsers() throws IllegalTransitionException;
}
