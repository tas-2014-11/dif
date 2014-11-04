package com.lumenare.dif.remote;

import com.avulet.db.Key;

public class TargetedString extends TargetedObject {
	public TargetedString(Key k,String s) {
		super(k,s);
	}

	public String string() {
		return((String)_object);
	}
}
