package com.lumenare.dif.language;

public class SymbolTable extends StringKeyedMap {
	public String toString() {
		String s = "SymbolTable(" + _map.size() + ")";
		return(s);
	}

	public void declare(String name) {
		String method = "assign(" + name + ")";
		_log.invoke(method);

		super.declare(name);
		super.assign(name,"");
	}

	public void assign(String name,String value) {
		String method = "assign(" + name + "," + value + ")";
		_log.invoke(method);

		String realValue;
		if(null == value) { realValue = ""; }
		else { realValue = value.trim(); }

		super.assign(name,realValue);
	}

	public String getValue(String name) {
		return((String)super.get(name));
	}

	public void assign(String name,int value) {
		super.assign(name,Integer.toString(value));
	}
}
