package com.lumenare.dif;

public class MissingPropertyException extends Exception {
	public MissingPropertyException(String propertyName,String filename) {
		super("missing property '" + propertyName + "' in '" + filename + "'");
	}
}
