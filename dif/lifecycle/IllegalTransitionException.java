package com.lumenare.dif.lifecycle;

import com.avulet.db.Key;

public class IllegalTransitionException extends Exception {
	protected State _start;
	protected State _end;
	protected Key _deviceKey;

	public IllegalTransitionException(State start,State end,Key deviceKey) {
		_start = start;
		_end = end;
		_deviceKey = deviceKey;
	}

	// accessors return immutables
	public State getStart() {
		return(_start);
	}

	public State getEnd() {
		return(_end);
	}

	public String toString() {
		String s = "IllegalTransitionException(";
		s += _start;
		s += ",";
		s += _end;
		s += ",";
		s += _deviceKey;
		s += ")";
		return(s);
	}
}
