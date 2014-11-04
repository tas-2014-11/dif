package com.lumenare.dif.manager;

import com.avulet.element.ConsoleLocation;

import com.lumenare.common.domain.attribute.AttributeCollection;

import com.lumenare.datastore.device.LmDeviceVo;

import com.lumenare.dif.language.DDSExecutor;
import com.lumenare.dif.language.DDSExecutionException;

public abstract class DeviceDriver {
	protected final Log log = new Log(this);

	protected final DDSExecutor _ddse;

	public static final String SANITYCHECK = "sanitycheck";

	private String _universallyRequiredFunctions[] = {
		SANITYCHECK
	};

	public DeviceDriver(DDSExecutor ddse,String requiredFunctions[])
			throws DeviceRoleException {

		_ddse = ddse;

		sanityCheckRoleName();
		validateDriverForRole(requiredFunctions);
		validateDriverForRole(_universallyRequiredFunctions);
	}

	public String toString() {
		String s = Log.trim(this);
		s += "(" + _ddse + ")";
		return(s);
	}

	public void describe() {
		log.describe();
		_ddse.describe();
	}

	public abstract String driverRoleName();

	protected void validateDriverForRole(String[] a)
			throws MissingFunctionException {

		String method = "validateDriverForRole()";

		// TODO: Check all, notify, then return.
		for(int i=0;i<a.length;i++) {
			String funcName = a[i];
			boolean itDoes = _ddse.supportsFunction(funcName);
			if(itDoes) {
				log.info(method,"SUPPORTEDFUNCTION(" + funcName + ")");
			}
			else {
				String s = "MISSINGFUNCTION(" + funcName + ")";
				log.info(method,s);
				throw(new MissingFunctionException(funcName,this));
			}
		}
	}

	protected void sanityCheckRoleName() throws DeviceRoleException {
		String method = "sanityCheckRoleName()";
		String s1 = driverRoleName();
		String s2 = _ddse.deviceRoleName();
		if(!s1.equals(s2)) {
			// data error

			String s = "driver='" + s1 + "','" + "device='" + s2 + "'";
			throw(new DeviceRoleException(s));
		}
	}

	public void close() {
		_ddse.close();
	}

	protected void finalize() throws Throwable {
		close();
	}

	public AttributeCollection executeByName(String functionName,AttributeCollection ac)
			throws MissingFunctionException,
			DDSExecutionException {

		_ddse.setInputArgs(ac);
		return(_ddse.execute(functionName));
	}

	public String[] functionNames() {
		return(_ddse.functionNames());
	}

	public void describeFunction(String functionName) {
		_ddse.describeFunction(functionName);
	}

	public ConsoleLocation consoleLocation() {
		return(_ddse.consoleLocation());
	}

	public AttributeCollection sanityCheck()
			throws MissingFunctionException,
			DDSExecutionException {

		return(_ddse.execute(SANITYCHECK));
	}

	public LmDeviceVo ldvHack() {
		return(_ddse.ldvHack());
	}
}
