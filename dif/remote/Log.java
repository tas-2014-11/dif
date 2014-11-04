package com.lumenare.dif.remote;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;

import com.lumenare.util.logit.LogIt;

public class Log {
	protected static LogIt _log = LogIt.get("difrmi");

	public static void main(String[] args) {
		try {
			init();
		}
		catch(Throwable t) {
			t.printStackTrace();
		}
	}

	public static void init() {
		//String filename = "/homedirs/tas/weblogic/config/thomDomain/logging.xml";
		String filename = "logging.xml";
		System.out.println("Log.init()");
		org.apache.log4j.xml.DOMConfigurator.configureAndWatch(filename);
	}

	public static void log(String msg) {
		_log.info(msg);
	}

	public static void log(String tag,Object o) {
		log(tag + ":" + o);
	}

	public static void call(Object o) {
		log("CALL",o);
	}

	public static void ret(Object o) {
		log("RETU",o);
	}

	public static void info(String s) {
		log("INFO",s);
	}

	public static void stack(String s) {
		log("STACK",s);
	}

	public static void stack(String method,Exception e) {
		stack(method + ":" + e.toString());

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		e.printStackTrace(pw);

		StringTokenizer st = new StringTokenizer(sw.toString(),"\n");
		while(st.hasMoreTokens()) {
			stack(st.nextToken());
		}
	}
}


/*
public class Log {
	protected static final long _start = System.currentTimeMillis();

	public static String timestamp() {
		long diff = System.currentTimeMillis() - _start;

		String s = "";

		long secs = diff / 1000;
		if(secs < 10)		{ s += "0000"; }
		else if(secs < 100)	{ s += "000"; }
		else if(secs < 1000)	{ s += "00"; }
		else if(secs < 10000)	{ s += "0"; }

		s += secs;

		s += ".";

		long millis = diff % 1000;
		if(millis < 10)		{ s += "00"; }
		else if(millis < 100)	{ s += "0"; }

		s += millis;

		return(s);
	}

	public static void log(String msg) {
		String s = timestamp();
		s += ":";
		s += msg;

		System.out.println(s);
		System.out.flush();
	}

	public static void log(String tag,Object o) {
		String s = timestamp();
		s += ":";
		s += tag;
		s += ":";
		s += Thread.currentThread().getThreadGroup().getName();
		s += ":";
		s += Thread.currentThread().getName();
		s += ":";
		s += o;

		System.out.println(s);
		System.out.flush();
	}

	public static void call(Object o) {
		log("CALL",o);
	}

	public static void ret(Object o) {
		log("RETU",o);
	}

	public static void info(String s) {
		log("INFO",s);
	}
}
*/
