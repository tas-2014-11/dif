package com.lumenare.dif.remote;

import com.avulet.db.Key;

import com.lumenare.dif.language.DDSExecutionException;
import com.lumenare.dif.manager.DeviceRoleException;
import com.lumenare.dif.manager.MissingFunctionException;

public interface RemoteManagedDevice extends RemoteDriver {
	public void configure(TargetedAttributeCollection[] configs)
		throws
		NoSuchDriverException,BadArgumentException,
		DeviceRoleException,MissingFunctionException,
		DDSExecutionException;

	public TargetedAttributeCollection[] extractConfiguration(Key[] deviceKeys)
		throws
		NoSuchDriverException,BadArgumentException,
		DeviceRoleException,MissingFunctionException,
		DDSExecutionException;

	public TargetedAttributeCollection[] measureUtilization(Key[] deviceKeys)
		throws
		NoSuchDriverException,BadArgumentException,
		DeviceRoleException,MissingFunctionException,
		DDSExecutionException;

}
