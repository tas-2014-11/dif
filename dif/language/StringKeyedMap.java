package com.lumenare.dif.language;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class StringKeyedMap {
	protected final Log _log = new Log(this);

	protected final HashMap _map = new HashMap();

	public String toString() {
		String s = "StringKeyedMap(" + _map.size() + ")";
		return(s);
	}

	public void describe() {
		_log.startDescribe();

		Set keys = _map.keySet();
		for(Iterator i = keys.iterator();i.hasNext();) {
			String name = (String)i.next();
			Object value = _map.get(name);
			_log.describe(name + ":" + value);
		}

		_log.endDescribe();
	}

	public void declare(String name) {
		String method = "declare(" + name + ")";
		_log.invoke(method);
		_map.put(name,null);
	}

	protected void assign(String name,Object value) {
		String method = "assign(" + name + "," + value + ")";
		_log.invoke(method);

		if(!_map.containsKey(name)) {
			_log.warn(method,"ASSIGNUNDECLAREDVARIABLE(" + name + "," + value + ")");
			_log.warn(method,"SHOULDNOTHAPPEN");
		}

		_map.put(name,value);
	}

	protected Object get(String name) {
		return(_map.get(name));
	}

	public boolean hasBeenDeclared(String name) {
		return(_map.containsKey(name));
	}
}

