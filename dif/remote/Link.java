package com.lumenare.dif.remote;

public class Link implements java.io.Serializable {
	protected final String _left;
	protected final String _right;

	protected String _name;
	protected String _value;

	public Link(String left,String right) {
		_left = left;
		_right = right;
	}

	public String toString() {
		if((null == _name) && (null == _value)) {
			String s = "Link(" + _left + "," + _right + ")";
			return(s);
		}

		String s = "Link(";
		s += _left + "," + _right + ",";
		s += _name + "," + _value + ")";
		return(s);
	}

	public String left() {
		return(_left);
	}

	public String right() {
		return(_right);
	}

	public String name() {
		return(_name);
	}

	public String value() {
		return(_value);
	}

	public void decorate(String name,String value) {
		_name = name;
		_value = value;
	}
}
