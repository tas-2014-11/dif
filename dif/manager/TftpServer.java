package com.lumenare.dif.manager;

import com.lumenare.dif.language.DDSExecutor;

public class TftpServer extends PassiveDeviceDriver {
	public TftpServer(DDSExecutor ddse) throws DeviceRoleException {
		super(ddse);
	}

	public String driverRoleName() {
		return("TFTPSERVER");
	}
}
