package com.lumenare.dif.manager;

public class MissingFunctionException extends DeviceRoleException {
	public MissingFunctionException(String funcName,DeviceDriver dd) {
		super("function=" + funcName + ",DeviceDriver=" + dd);
	}

	public MissingFunctionException(String funcName) {
		super("function=" + funcName);
	}
}
