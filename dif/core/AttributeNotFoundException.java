package com.lumenare.dif.core;

// FIXME: The constructor should take an AttributeId.

public class AttributeNotFoundException extends Exception {
	protected String _attributeName;

	public AttributeNotFoundException(String attributeName) {
		_attributeName = attributeName;
	}

	public String toString() {
		String s = this.getClass().getName();
		s += "(" + _attributeName + ")";
		return(s);
	}
}
