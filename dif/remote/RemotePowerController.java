package com.lumenare.dif.remote;

import com.lumenare.dif.language.DDSExecutionException;
import com.lumenare.dif.manager.DeviceRoleException;
import com.lumenare.dif.manager.MissingFunctionException;

public interface RemotePowerController extends RemoteDriver {
	public void turnPowerOn(TargetedString[] locations)
		throws
		NoSuchDriverException,BadArgumentException,
		DeviceRoleException,MissingFunctionException,
		DDSExecutionException;

	public void turnPowerOff(TargetedString[] locations)
		throws
		NoSuchDriverException,BadArgumentException,
		DeviceRoleException,MissingFunctionException,
		DDSExecutionException;

	public boolean powerIsOn(TargetedString location)
		throws
		NoSuchDriverException,BadArgumentException;
}
