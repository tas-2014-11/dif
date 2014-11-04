package com.lumenare.dif.language;

public class ConsoleTable extends StringKeyedMap {
	public String toString() {
		String s = "ConsoleTable(" + _map.size() + ")";
		return(s);
	}

	public void declare(Console console) {
		String method = "declare(" + console + ")";
		_log.invoke(method);
		super.declare(console.name());
	}

	public void assign(Console console) {
		String method = "assign(" + console + ")";
		_log.invoke(method);
		super.assign(console.name(),console);
	}

	public Console getConsole(String name) {
		return((Console)super.get(name));
	}

	public void close() {
		throw(new NullPointerException(this.toString()));
		/*
		if(null != _cl) {
			_cl.close();
			_cl = null;
		}
		*/
	}

	public Console getOne() {
		if(0 == _map.size()) { return(null); }

		java.util.Collection c = _map.values();
		for(java.util.Iterator i = c.iterator();i.hasNext();) {
			return((Console)i.next());
		}
		return(null);
	}
}
