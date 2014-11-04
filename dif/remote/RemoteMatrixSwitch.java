package com.lumenare.dif.remote;

import com.lumenare.dif.language.DDSExecutionException;
import com.lumenare.dif.manager.DeviceRoleException;
import com.lumenare.dif.manager.MissingFunctionException;

public interface RemoteMatrixSwitch extends RemoteDriver {
	public void connect(TargetedLink[] links)
		throws
		NoSuchDriverException,BadArgumentException,
		DeviceRoleException,MissingFunctionException,
		DDSExecutionException;

	public void disconnect(TargetedLink[] link)
		throws
		NoSuchDriverException,BadArgumentException,
		DeviceRoleException,MissingFunctionException,
		DDSExecutionException;

	public boolean isBreakable(TargetedLink link)
		throws
		NoSuchDriverException,BadArgumentException;

	public boolean isMakeable(TargetedLink link)
		throws
		NoSuchDriverException,BadArgumentException;
}
