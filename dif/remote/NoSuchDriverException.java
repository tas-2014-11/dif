package com.lumenare.dif.remote;

import com.avulet.db.Key;

public class NoSuchDriverException extends Exception {
	public NoSuchDriverException() {
		super();
	}

	public NoSuchDriverException(String s) {
		super(s);
	}

	public NoSuchDriverException(Key k) {
		super((k != null) ? k.toString() : "NULLKEY");
	}
}
