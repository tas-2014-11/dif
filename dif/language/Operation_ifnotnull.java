package com.lumenare.dif.language;

public class Operation_ifnotnull extends Operation_unaryconditional {
	public Operation_ifnotnull(String variableName) {
		super(variableName);
	}

	public String toString() {
		String s = "Operation_ifnotnull(" + _variableName + ")";
		return(s);
	}

	protected boolean condition(ExecutionContext ec) {
		String method = "condition()";

		String value = symtabGetValue(method,ec,_variableName);
		log.info(method,_variableName + "=" + value);

		if(null == value) { // ShouldNotHappen
			return(false);
		}

		if(value.length() > 0) { return(true); }
		return(false);
	}
}
