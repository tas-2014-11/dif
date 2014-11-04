package com.lumenare.dif.remote;

import java.util.HashSet;
import java.util.Set;

import com.avulet.db.Key;

import com.lumenare.dif.Util;

public class TargetedObject implements java.io.Serializable {
	protected final Key _key;
	protected final Object _object;
	protected final String _toString;

	public TargetedObject(Key k,Object o) {
		_key = k;
		_object = o;
		_toString = Util.basename4class(getClass()) + "(" + _key + "," + _object + ")";
	}

	public Key key() {
		return(_key);
	}

	protected Object object() {
		return(_object);
	}

	public String toString() {
		return(_toString);
	}

	public static String describe(TargetedObject[] to) {
		return(com.lumenare.dif.Util.describe(to));
	}

	public static String describe(TargetedAttributeCollection[] o) {
		StringBuffer s = new StringBuffer();
		for(int i=0;i<o.length;i++) {
			if(i > 0) { s.append(","); }
			s.append(o[i]);
		}
		return(s.toString());
	}

	public static Set keySet(TargetedObject[] to) {
		HashSet keys = new HashSet();
		for(int i=0;i<to.length;i++) {
			keys.add(to[i]._key);
		}
		return(keys);
	}
}
