package com.lumenare.dif.tmp;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;

import com.lumenare.util.logit.LogIt;

import com.lumenare.dif.language.ExecutionContext;

public class Log implements java.io.Serializable {
	public static final String DEFAULT_CATEGORY = "dds";

	protected static LogIt _log = LogIt.get(DEFAULT_CATEGORY);

	protected String _category = "NULL";
	protected Object _o = "UNDEFINED";

	public Log(String category,Object o) {
		_category = category;
		_o = o;
	}

	public Log(Object o) {
		this(DEFAULT_CATEGORY,o);
	}

	public String toString() {
		return(this.getClass().getName());
	}

	protected static int _depth = 0;
	protected static String _suffix = "";

	protected static void buildSuffix() {
		_suffix = "";
		for(int i = 0;i<_depth;i++) {
			if(i == (_depth-1)) { _suffix += "\\_"; }
			else { _suffix += "  "; }
		}
	}

	public static void push() {
		_depth++;
		buildSuffix();
	}

	public static void pop() {
		_depth--;
		if(_depth < 0) {
			String s = "********  DEPTHUNDERRUN ********" + _depth;
			_log.warn(s);
			System.out.println(s);
			_depth = 0;
		}
		buildSuffix();
	}

	public static String trim(Object o) {
		String className = o.getClass().getName();
		String packageName = o.getClass().getPackage().getName();
		int i = packageName.length();
		i++;  //skip the period
		String basename = className.substring(i);
		return(basename);
	}

	/*
	protected static String pre0 = "driver.language.";
	protected static String pre1 = "driver.manager.";
	protected static String pre2 = "driver.";

	public static String trim(String s) {
		String pre[] = { pre0,pre1,pre2 };
		for(int i=0;i<pre.length;i++) {
			if(s.startsWith(pre[i])) {
				return(s.substring(pre[i].length()));
			}
		}
		return(s);
	}
	*/

	protected void log(String tag,String s) {
		if(tag.equals(WARNING))	{ _log.warn(s); return; }
		if(tag.equals(INFO))	{ _log.info(s); return; }
		if(tag.equals(INVOKE))	{ _log.trace(s); return; }
		if(tag.equals(RETURN))	{ _log.trace(s); return; }
		_log.debug(s);
	}

	protected void one(String tag) {
		//String s = _category + ":";
		String s = "";
		s += tag + ":";
		s += _o.toString() + ":";
		//System.out.println(s);
		log(tag,s);
	}

	protected void one(String tag,String message) {
		//String s = _category + ":";
		String s = "";
		s += tag + ":";
		s += _o.toString() + ".";
		s += message + ":";
		//System.out.println(s);
		log(tag,s);
	}

	protected void one(String tag,String message,ExecutionContext ec) {
		String suffix = "";
		int depth = ec.stackDepth();
		for(int i = 0;i<depth;i++) {
			if(i == (depth-1)) { suffix += "\\_"; }
			else { suffix += "  "; }
		}
		one(tag + suffix,message);
	}

	protected void one(String tag,String method,String message) {
		String s = method + ":" + message;
		one(tag,s);
	}

	static final String INVOKE = "INVOKE";
	static final String RETURN = "RETURN";
	static final String EXCEPTION = "EXCEPTION";
	static final String STACK = "STACK";
	static final String INFO = "INFO";
	static final String DESCRIBE = "DESCRIBE-------";
	static final String WARNING = "WARNING";
	static final String STARTDESCRIBE = "DESCRIBE-START-";
	static final String ENDDESCRIBE = "DESCRIBE-END---";

	public void invoke(String method,String message) {
		one(INVOKE,method,message);
	}

	public void invoke(String method) {
		one(INVOKE,method);
	}

	public void invoke(String method,ExecutionContext ec) {
		one(INVOKE,method,ec);
	}

	public void ret(String method) {
		one(RETURN,method);
	}

	public void ret(String method,ExecutionContext ec) {
		one(RETURN,method,ec);
	}

	public static final String lines = "--------------------------------";

	public void info(String method,Exception e) {
		one(EXCEPTION,method,lines);
		one(EXCEPTION,method,e.toString());

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);

		StringTokenizer st = new StringTokenizer(sw.toString(),"\n");
		while(st.hasMoreTokens()) {
			one(STACK,method,st.nextToken());
		}

		one(EXCEPTION,method,lines);
	}

	public void info(String method,String message) {
		one(INFO,method,message);
	}

	public void warn(String method,String message) {
		one(WARNING,method,message);
	}

	public void info(String message) {
		one(INFO,message);
	}

	protected void innerDescribe(String tag,String message) {
		one(tag + _suffix,message);
	}

	public void describe(String message) {
		innerDescribe(DESCRIBE,message);
	}

	public void startDescribe(String message) {
		innerDescribe(STARTDESCRIBE,message);
		push();
	}

	public void endDescribe(String message) {
		pop();
		innerDescribe(ENDDESCRIBE,message);
	}

	protected void innerDescribe(String tag) {
		one(tag + _suffix);
	}

	public void describe() {
		innerDescribe(DESCRIBE);
	}

	public void startDescribe() {
		innerDescribe(STARTDESCRIBE);
		push();
	}

	public void endDescribe() {
		pop();
		innerDescribe(ENDDESCRIBE);
	}
}
