package com.lumenare.dif.manager;

import com.lumenare.dif.language.DDSExecutor;

public class EthernetHub extends PassiveDeviceDriver {
	public EthernetHub(DDSExecutor ddse) throws DeviceRoleException {
		super(ddse);
	}

	public String driverRoleName() {
		return("ETHERNETHUB");
	}
}
