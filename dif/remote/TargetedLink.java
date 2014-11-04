package com.lumenare.dif.remote;

import com.avulet.db.Key;

public class TargetedLink extends TargetedObject {
	public TargetedLink(Key k,Link l) {
		super(k,l);
	}

	public Link link() {
		return((Link)_object);
	}
}
