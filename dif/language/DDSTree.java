package com.lumenare.dif.language;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.List;

public class DDSTree {
	protected final Log _log = new Log(this);
	protected final String _name;
	protected final String _description;
	protected final String _authorname;
	protected final String _authorcompany;
	protected final String _authoremail;

	public DDSTree(String name,String description,String authorname,String authorcompany,String authoremail) {
		_name = name;
		_description = description;
		_authorname = authorname;
		_authorcompany = authorcompany;
		_authoremail = authoremail;
	}

	public String getName() {
		return(_name);
	}

	public String getDescription() {
		return(_description);
	}

	public String getAuthorName() {
		return(_authorname);
	}

	public String getAuthorCompany() {
		return(_authorcompany);
	}

	public String getAuthorEmail() {
		return(_authoremail);
	}

	public String toString() {
		String s = "DDSTree(";
		s += _name + ",";
		s += _description + ",";
		s += _authorname + ",";
		s += _authorcompany + ",";
		s += _authoremail + ")";
		return(s);
	}

	public void describe() {
		_log.startDescribe();
		_declareMap.describe("_declareMap",_log);
		_consoleMap.describe("_consoleMap",_log);
		_functionMap.describe("_functionMap",_log);
		describeSupporteddts();
		_log.endDescribe();
	}

	protected void describeSupporteddts() {
		String s = "_supporteddtsList.size()=" + _supporteddtsList.size();
		_log.startDescribe(s);
		for(int i=0;i<_supporteddtsList.size();i++) {
			DDS_supporteddts sd = (DDS_supporteddts)_supporteddtsList.get(i);
			sd.describe();
		}
		_log.endDescribe(s);
	}

	protected ElementMap _declareMap = new ElementMap();
	protected ElementMap _consoleMap = new ElementMap();
	protected ElementMap _functionMap = new ElementMap();
	protected ArrayList _supporteddtsList = new ArrayList();

	public void add(DDS_declare d) {
		// FIXME: check for null
		_declareMap.put(d.name(),d);
	}

	public void add(DDS_console c) {
		// FIXME: check for null
		_consoleMap.put(c.name(),c);
	}

	public void add(DDS_function f) {
		// FIXME: check for null
		_functionMap.put(f.name(),f);
	}

	public void add(DDS_supporteddts s) {
		// FIXME: check for null
		_supporteddtsList.add(s);
	}

	public DDS_function locateFunction(String funcName) {
		String method = "locateFunction(" + funcName + ")";

		if(null == funcName) {
			_log.info(method,"NULLFUNCTIONNAME");
			return(null);
		}

		DDS_function f = (DDS_function)_functionMap.get(funcName);

		return(f);
	}

	// TODO: move generate methods into DDSExecutor
	public SymbolTable generateSymbolTable() {
		SymbolTable symtab = new SymbolTable();

		Collection c = _declareMap.values();
		for(Iterator i = c.iterator();i.hasNext();) {
			DDS_declare d = (DDS_declare)i.next();
			symtab.declare(d.name());
		}

		return(symtab);
	}

	public boolean variableHasBeenDeclared(String name) {
		return(_declareMap.containsKey(name));
	}

	public Iterator consoleMapValuesIterator() {
		// demeter forgive me
		return(_consoleMap.values().iterator());
	}

	public boolean consoleHasBeenDeclared(String name) {
		return(_consoleMap.containsKey(name));
	}

	public String[] functionNames() {
		String a[] = new String[_functionMap.size()];
		int j = 0;
		Set keys = _functionMap.keySet();
		for(Iterator i = keys.iterator();i.hasNext();) {
			a[j++] = (String)i.next();
		}
		return(a);
	}

	public List getSupportedDtsList() {
		return(_supporteddtsList);
	}
}

class ElementMap {
	protected HashMap _map = new HashMap();

	protected void describe(String name,Log log) {
		String s = name + ".size()=" + _map.size();
		log.startDescribe(s);
		Collection c = _map.values();
		for(Iterator i = c.iterator();i.hasNext();) {
			DDSElement el = (DDSElement)i.next();
			el.describe();
		}
		log.endDescribe(s);
	}

	public void put(String s,DDSElement el) {
		_map.put(s,el);
	}

	public DDSElement get(String s) {
		return((DDSElement)_map.get(s));
	}

	public Set keySet()			{ return(_map.keySet()); }
	public int size()			{ return(_map.size()); }
	public Collection values()		{ return(_map.values()); }
	public boolean containsKey(String s)	{ return(_map.containsKey(s)); }
}
