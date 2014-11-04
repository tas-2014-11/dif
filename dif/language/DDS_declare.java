package com.lumenare.dif.language;

public class DDS_declare extends DDSElement {
	protected final String _name;

	public DDS_declare(String name) throws DDSParseException {
		_name = name;
	}

	public String toString() {
		String s = "DDS_declare(" + _name + ")";
		return(s);
	}

	public String name() {
		return(_name);
	}
}
