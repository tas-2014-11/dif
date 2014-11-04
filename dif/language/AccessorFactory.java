package com.lumenare.dif.language;

import com.lumenare.dif.Util;

public abstract class AccessorFactory {
	protected Log log = new Log(this);

	protected String _description;

	public AccessorFactory(String path) {
		String method = "AccessorFactory()";
		_description = Util.basename4class(this.getClass()) + "(" + path + ")";
	}

	public String toString() {
		return(_description);
	}

	public void describe() {
		log.describe();
	}

	protected abstract Accessor locateAccessor(String path);
}
