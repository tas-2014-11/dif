package com.lumenare.dif.language;

public abstract class Operation_unaryconditional extends Operation_baseconditional {
	protected final String _variableName;

	public Operation_unaryconditional(String variableName) {
		_variableName = variableName;
	}

	public String toString() {
		String s = _basename4class + "(" + _variableName + ")";
		return(s);
	}

	public void describe() {
		log.describe();
		super.describe();
	}
}
