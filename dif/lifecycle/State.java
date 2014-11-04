package com.lumenare.dif.lifecycle;

// TODO: My first inclination was to make State an interface and
// TODO: then provide this no-frills implementation as SimpleState.
// TODO: But so far I have no place to reuse a State interface.  YAGNI.

// FIXME: This class shouldn't be called State.  It should be something
// FIXME: more basic.  It's really just a root for an enumerated type.

public class State {
	// TODO: is there any real value here or should my callers just use instanceof
	public boolean equals(State pol) {
		// TODO: should null args return false or throw?
		if(null == pol) { return(false); }

		Class c1 = this.getClass();
		Class c2 = pol.getClass();
		return(c1.equals(c2)); // FIXME: Does this work?
	}

	public String toString() {
		return("State(" + getClass().getName() + ")");
	}

	// this is to make JUnit's assertEquals() happy
	public boolean equals(Object obj) {

		// TODO: should null return false or should it throw?
		if(null == obj) { return(false); }

		if(obj instanceof State) {
			return(equals((State)obj));
		}

		// TODO: should we return(false) here or return Object.equals(Object)?
		// TODO: i.e. does this equals have the same semantics as our custom equals?
		// TODO: OTOH, if we get here won't it always be false?  Because we've
		// TODO: already established that there's a type mismatch.

		return(((Object)this).equals(obj));
	}
}
