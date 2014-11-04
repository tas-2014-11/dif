package com.lumenare.dif.core;

// FIXME: Does this Exception belong in core or lifecycle ?

// FIXME: I'll just bet that somebody is gonna want to interrogate
// FIXME: this guy for the Throwable he's hiding.
// FIXME: See also java.lang.reflect.InvocationTargetException

// FIXME: Is this really some kind of NestedThrowable or
// FIXME: a OneThrowableSpawnsAnother or a ChainOfThrows (ThrowChain?)

public class DeviceBehaviorException extends Exception {
	protected Throwable _throwable;

	// _throwable goes uninitialized here.  I think that's ok.
	public DeviceBehaviorException() {
	}

	public DeviceBehaviorException(Throwable throwable) {
		_throwable = throwable;
	}

	public String toString() {
		String s = this.getClass().getName();
		s += "(" + _throwable + ")";;
		return(s);
	}
}
