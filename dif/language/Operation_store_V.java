package com.lumenare.dif.language;

public class Operation_store_V extends Operation_store {
	protected final String _variable;

	public Operation_store_V(String path,String variable) throws DDSParseException {
		super(path);
		_variable = checknull(this,variable,"variable");
	}

	public String toString() {
		String s = "Operation_store_V(";
		s += _path + ",";
		s += _variable + ")";
		return(s);
	}

	protected String fetchValue(ExecutionContext ec) {
		String method = toString() + "fetchValue(" + ec + ")";

		String value = symtabGetValue(method,ec,_variable);
		if(null == value) { return(""); }

		return(value);
	}
}
