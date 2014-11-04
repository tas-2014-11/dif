package com.lumenare.dif.manager;

import com.lumenare.dif.language.DDSExecutor;

public abstract class PassiveDeviceDriver extends DeviceDriver {
	protected static final String _requiredFunctions[] = {
	};

	// FIXME: What to throw...
	public PassiveDeviceDriver(DDSExecutor ddse) throws DeviceRoleException {
		super(ddse,_requiredFunctions);
	}

	public abstract String driverRoleName();
}
