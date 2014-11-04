package com.lumenare.dif.manager;

public class RoleMismatchException extends DeviceRoleException {
	public RoleMismatchException(String driverRoleName,String deviceRoleName) {
		super("driver=" + driverRoleName + "," + "device=" + deviceRoleName);
	}
}
