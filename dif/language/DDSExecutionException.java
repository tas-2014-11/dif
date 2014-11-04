package com.lumenare.dif.language;

public class DDSExecutionException extends DDSException {
	protected Log log = new Log(this);

	public DDSExecutionException(String s) {
		super(s);
	}

/*
	public DDSExecutionException() {
		super();
	}
*/

	public String toString() {
		String s = this.getClass().getName();
		return(s);
	}

	public void describe() {
		log.describe();
	}
}

