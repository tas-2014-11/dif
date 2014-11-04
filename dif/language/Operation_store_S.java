package com.lumenare.dif.language;

public class Operation_store_S extends Operation_store {
	protected final String _value;

	public Operation_store_S(String path,String value) throws DDSParseException {
		super(path);
		_value = checknull(this,value,"value");
	}

	public String toString() {
		String s = "Operation_store_S(";
		s += _path + ",";
		s += _value + ")";
		return(s);
	}

	protected String fetchValue(ExecutionContext ec) {
		return(_value);
	}
}
