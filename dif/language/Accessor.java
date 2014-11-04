package com.lumenare.dif.language;

public abstract class Accessor {
	protected Log log = new Log(this);

	protected String _fieldName = "UNDEFINED";

	public String toString() {
		String s = this.getClass().getName() + "(" + _fieldName + ")";
		return(s);
	}

	public void describe() {
		log.describe();
	}

	public String fieldName() {
		return(_fieldName);
	}

	public abstract String fetchValue(ExecutionContext ec);
}
