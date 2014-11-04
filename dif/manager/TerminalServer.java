package com.lumenare.dif.manager;

import com.lumenare.dif.language.DDSExecutor;

public class TerminalServer extends PassiveDeviceDriver {
	public TerminalServer(DDSExecutor ddse) throws DeviceRoleException {
		super(ddse);
	}

	public String driverRoleName() {
		return("TERMINALSERVER");
	}
}
