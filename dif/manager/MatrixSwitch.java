package com.lumenare.dif.manager;

import com.lumenare.common.domain.attribute.AttributeCollection;
import com.lumenare.common.domain.attribute.AttributeFactory;
import com.lumenare.common.domain.attribute.StringAttribute;
import com.lumenare.common.domain.attribute.adapter.AttributeCollectionImpl;

import com.lumenare.dif.language.DDSExecutor;
import com.lumenare.dif.language.DDSExecutionException;

public class MatrixSwitch extends DeviceDriver {
	public static final String CONNECT = "connect";
	public static final String DISCONNECT = "disconnect";

	public static final String LEFT = "left";
	public static final String RIGHT = "right";

	protected static String _requiredFunctions[] = {
		CONNECT,
		DISCONNECT
	};

	// FIXME: What to throw...
	public MatrixSwitch(DDSExecutor ddse) throws DeviceRoleException {
		super(ddse,_requiredFunctions);
	}

	public String driverRoleName() {
		return("MATRIXSWITCH");
	}

	protected AttributeCollection marshalArgs(String left,String right,String name,String value) {
		AttributeCollection ac = new AttributeCollectionImpl();

		if(null != name) {
			String realvalue = value;
			if(null == realvalue) {
				String method = "marshalArgs(";
				method += left + "," + right + "," + name + "," + value;
				method += ")";

				log.info(method,":null value for name='" + name + "'");
				realvalue = "";
			}

			StringAttribute sa_other = AttributeFactory.createString(name,realvalue);
			ac.addAttribute(sa_other);
		}

		StringAttribute sa_left = AttributeFactory.createString(LEFT,left);
		StringAttribute sa_right = AttributeFactory.createString(RIGHT,right);

		ac.addAttribute(sa_left);
		ac.addAttribute(sa_right);

		return(ac);
	}

	public void connect(String left,String right,String name,String value)
			throws MissingFunctionException,
			DDSExecutionException {

		AttributeCollection ac = marshalArgs(left,right,name,value);
		_ddse.setInputArgs(ac);
		_ddse.execute(CONNECT);
	}

	public void disconnect(String left,String right,String name,String value)
			throws MissingFunctionException,
			DDSExecutionException {

		AttributeCollection ac = marshalArgs(left,right,name,value);
		_ddse.setInputArgs(ac);
		_ddse.execute(DISCONNECT);
	}
}
