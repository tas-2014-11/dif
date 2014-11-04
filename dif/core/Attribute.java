package com.lumenare.dif.core;

import java.util.Properties;

import com.avulet.system.Config;

import com.lumenare.dif.util.Log;
import com.lumenare.dif.util.MyProperties;

public abstract class Attribute {
	// name mean type, name does not mean key
	protected String _attributeName;
	protected String _value;

	public Attribute() throws AttributeNotFoundException {
		_attributeName = calculateAttributeName();
		// Hmmm.
		//_value = fetchValue();
	}

	// FIXME: This doesn't sanity check the class name.

	protected final String calculateAttributeName() {
		String className = this.getClass().getName();
		String packageName = this.getClass().getPackage().getName();

		int i = packageName.length();
		i += 11;
		String baseName = className.substring(i);

		return(baseName);
	}

	protected abstract String fetchFromDatadock() throws AttributeNotFoundException;

	//protected abstract String fetchFromProperties() throws AttributeNotFoundException;

	protected String fetchFromProperties() throws AttributeNotFoundException {
		String key = calculateKey();
		String s = InventoryProperties.get().getProperty(key);
		return(s);
	}

	protected abstract String calculateKey();

	protected final String fetchValue() throws AttributeNotFoundException {
		if(iShouldFetchFromProperties()) {
			return(fetchFromProperties());
		}
		else {
			return(fetchFromDatadock());
		}
	}

	protected final boolean iShouldFetchFromProperties() {
		String propertyName = "iShouldFetchFromProperties";
		Properties props = Config.get().getProperties();
		String s = props.getProperty(propertyName);

		if(s == null) { return(false); }

		s = s.toUpperCase();

		if(s.equals("YES")) { return(true); }

		return(false);
	}

	public final String getValue() {
		return(_value);
	}

	public final String getAttributeName() {
		return(_attributeName);
	}

	public String toString() {
		String s = this.getClass().getName();
		s += "(";
		s += _attributeName;
		s += ",";
		s += _value;
		s += ")";
		return(s);
	}
}

class InventoryProperties {
	protected MyProperties _props;
	protected String inventoryPropertiesFilename = "inventory.properties";

	protected InventoryProperties() {
		_props = new MyProperties(inventoryPropertiesFilename);
	}

	protected static InventoryProperties instance = null;
	public static InventoryProperties get() {
		if(null == instance) {
			instance = new InventoryProperties();
		}
		return(instance);
	}

	// getAttribute() ??? Not really.  But not really getProperty() either.
	// This is the glue.
	public String getProperty(String key) throws AttributeNotFoundException {
		String logmsg = toString() + ".getProperty(" + key + ")";
		Log.trace(logmsg);

		String value = _props.getProperty(key);
		if(null == value) {
			AttributeNotFoundException anfe = new AttributeNotFoundException(key);
			Log.error(logmsg,anfe);
			throw(anfe);
		}

		return(value);
	}

	public String toString() {
		String s = this.getClass().getName();
		s += "()";
		return(s);
	}
}
