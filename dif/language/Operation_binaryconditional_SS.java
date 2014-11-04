package com.lumenare.dif.language;

public abstract class Operation_binaryconditional_SS extends Operation_baseconditional {
	protected final String _left;
	protected final String _right;

	protected final String _toString;

	public Operation_binaryconditional_SS(String left,String right) {
		_left = left;
		_right = right;

		_toString = _basename4class + "(" + _left + "," + _right + ")";
	}

	public String toString() {
		return(_toString);
	}

	public void describe() {
		log.describe();
		super.describe();
	}
}
