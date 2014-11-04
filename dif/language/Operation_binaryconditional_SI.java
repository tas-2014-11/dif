package com.lumenare.dif.language;

public abstract class Operation_binaryconditional_SI extends Operation_baseconditional {
	protected final String _left;
	protected final int _right;

	protected final String _toString;

	public Operation_binaryconditional_SI(String left,int right) {
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

