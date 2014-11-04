package com.lumenare.dif.util;

import com.lumenare.util.logit.LogIt;

public class Log {
	protected static LogIt _log = LogIt.get(LogIt.TAG_DIF);

	protected Log() {
	}

	public static void error(String s) {
		_log.error(s);
	}

	public static void error(String s1,String s2) {
		_log.error(s1,s2);
	}

	public static void error(String s1,Throwable t) {
		if(t != null) {
			_log.error(s1);
			t.printStackTrace(System.out);
		}
		else {
			_log.error(s1 + ":NULL THROWABLE");
		}
	}

	public static void error(String s1,String s2,Throwable t) {
		if(t != null) {
			_log.error(s1 + s2);
			t.printStackTrace(System.out);
		}
		else {
			_log.error(s1 + ":" + s2 + ":NULL THROWABLE");
		}
	}

	public static void info(String s1) {
		_log.info(s1);
	}

	public static void info(String s1,java.util.Properties props) {
		if(props == null) {
			debug(s1 + ":NULL PROPERTIES");
			return;
		}

		java.util.Enumeration e = props.propertyNames();
		while(e.hasMoreElements()) {
			String key = (String)e.nextElement();
			String value = props.getProperty(key);
			info(s1 + ":" + key + "=" + value);
		}
	}

	public static void debug(String s1,java.util.Properties props) {
		if(props == null) {
			debug(s1 + ":NULL PROPERTIES");
			return;
		}

		java.util.Enumeration e = props.propertyNames();
		while(e.hasMoreElements()) {
			String key = (String)e.nextElement();
			String value = props.getProperty(key);
			debug(s1 + ":" + key + "=" + value);
		}
	}

	public static void debug(String s1) {
		_log.debug(s1);
	}

	public static void trace(String s1) {
		_log.trace(s1);
	}

	public static LogIt get() {
		return(_log);
	}
}
