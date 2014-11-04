package com.lumenare.dif.manager;

import com.lumenare.common.domain.attribute.AttributeCollection;

import com.lumenare.dif.language.DDSExecutor;
import com.lumenare.dif.language.DDSExecutionException;

public class ManagedDevice extends DeviceDriver {
	public static final String CONFIGURE = "configure";
	public static final String EXTRACTCONFIGURATION = "extractconfiguration";
	public static final String MEASUREUTILIZATION = "measureutilization";

	protected static String _requiredFunctions[] = {
		CONFIGURE,
		EXTRACTCONFIGURATION,
		MEASUREUTILIZATION
	};

	// FIXME: What to throw...
	public ManagedDevice(DDSExecutor ddse) throws DeviceRoleException {
		super(ddse,_requiredFunctions);
	}

	public String driverRoleName() {
		return("MANAGEDDEVICE");
	}

	public void configure(AttributeCollection configuration)
			throws MissingFunctionException,
			DDSExecutionException {

		_ddse.setInputArgs(configuration);
		_ddse.execute(CONFIGURE);
	}

	public AttributeCollection extractConfiguration()
			throws MissingFunctionException,
			DDSExecutionException {

		// FIXME: Write this!!!
		return(_ddse.execute(EXTRACTCONFIGURATION));
	}

	public AttributeCollection measureUtilization()
			throws MissingFunctionException,
			DDSExecutionException {

		// FIXME: Write this!!!
		return(_ddse.execute(MEASUREUTILIZATION));
	}
}
