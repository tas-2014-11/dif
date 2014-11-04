package com.lumenare.dif.language;

public class DDSException extends Exception {
	protected Log log = new Log(this);

	public DDSException(String s) {
		super(s);
	}

/*
	public DDSException() {
		super();
	}
*/

/*
	public String toString() {
		String s = this.getClass().getName();
		return(s);
	}
*/

	public void describe() {
		log.describe();
	}
}

