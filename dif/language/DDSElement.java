package com.lumenare.dif.language;

import com.lumenare.dif.Util;

public abstract class DDSElement {
	protected final Log log = new Log(this);
	protected final String _basename4class = Util.basename4class(this.getClass());

	protected final int _line;
	protected final int _column;

	protected static org.xml.sax.Locator _locator; // Demeter forgive me!

	public DDSElement() {
		if(null == _locator) {
			_line = _column = -1;
		}
		else {
			_line = _locator.getLineNumber();
			_column = _locator.getColumnNumber();
		}
	}

	public static void setLocator(org.xml.sax.Locator locator) {
		_locator = locator;
	}

	public String loc() {
		return("[" + _line + "," + _column + "]");
	}

	public String toString() {
		return("DDSElement()");
	}

	public void describe() {
		log.describe();
	}

	protected void abort(DDSElement el,String message) throws DDSParseException {
		// TODO: Does this count as DoubleNotification ???
		log.warn(el.toString(),loc() + ":" + message);

		throw(new DDSParseException(el.toString() + ":" + loc() + ":" + message));
	}

	protected String checknull(DDSElement el,String value,String name) throws DDSParseException {
		if(null == value)	{ abort(el,"NULLSTRING(" + name + ")"); }
		return(value);
	}

	protected String checklength(DDSElement el,String value,String name) throws DDSParseException {
		checknull(el,value,name);
		if(0 == value.length())	{ abort(el,"ZEROLENGTHSTRING(" + name + ")"); }
		return(value);
	}

	/*
	// TODO: should this push down into DDSOperation???
	protected void fail(String method,String message) throws DDSExecutionException {
		log.info(method,message);
		throw(new DDSExecutionException(message));
	}
	*/
}
