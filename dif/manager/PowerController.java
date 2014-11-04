package com.lumenare.dif.manager;

import com.lumenare.common.domain.attribute.AttributeCollection;
import com.lumenare.common.domain.attribute.AttributeFactory;
import com.lumenare.common.domain.attribute.StringAttribute;
import com.lumenare.common.domain.attribute.adapter.AttributeCollectionImpl;

import com.lumenare.dif.language.DDSExecutor;
import com.lumenare.dif.language.DDSExecutionException;

public class PowerController extends DeviceDriver {
	public static final String TURNPOWERON = "turnpoweron";
	public static final String TURNPOWEROFF = "turnpoweroff";

	public static final String PORT = "port";

	protected static String _requiredFunctions[] = {
		TURNPOWERON,
		TURNPOWEROFF
	};

	// FIXME: What to throw...
	public PowerController(DDSExecutor ddse) throws DeviceRoleException {
		super(ddse,_requiredFunctions);
	}

	public String driverRoleName() {
		return("POWERCONTROLLER");
	}

	protected AttributeCollection marshalArgs(String port) {
		StringAttribute sa = AttributeFactory.createString(PORT,port);

		AttributeCollection ac = new AttributeCollectionImpl();

		ac.addAttribute(sa);

		return(ac);
	}

	public void turnpoweron(String port)
			throws MissingFunctionException,
			DDSExecutionException {

		AttributeCollection ac = marshalArgs(port);
		_ddse.setInputArgs(ac);
		_ddse.execute(TURNPOWERON);
	}

	public void turnpoweroff(String port)
			throws MissingFunctionException,
			DDSExecutionException {

		AttributeCollection ac = marshalArgs(port);
		_ddse.setInputArgs(ac);
		_ddse.execute(TURNPOWEROFF);
	}
}
