package com.lumenare.dif.core;

// FIXME: Don't carry behavior here.
// FIXME: Sometimes a Device of a particular model has no behavior
// FIXME: (like during authoring).
// FIXME: Sometimes that same Device has behavior (like when realized).

/*
 *
 * immutable
 *
 */

public class Model {
	protected String _modelName;

	// FIXME:  Is there any need for a no-arg constructor?

	public Model(String modelName) {
		_modelName = modelName;
	}

	// It's ok.  String is immutable.
	public String getName() {
		return(_modelName);
	}

	public String toString () {
		String s = "Model(";
		s += _modelName;
		s += ")";
		return(s);
	}
}
