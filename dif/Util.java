package com.lumenare.dif;

import java.io.FileInputStream;
import java.io.IOException;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.avulet.element.ConsoleLocation;
import com.avulet.consoleserver.ConsoleLocationImpl;

public class Util {
	private Util() { }

	public static byte[] file2bytes(String filename) throws IOException {
		FileInputStream fis = new FileInputStream(filename);
		StringBuffer sb = new StringBuffer();
		byte[] b = new byte[8192];
		int len;
		while((len = fis.read(b)) > 0) { sb.append(new String(b,0,len)); }
		return(sb.toString().getBytes());
	}

	public static String describe(Object[] o) {
		StringBuffer s = new StringBuffer();
		for(int i=0;i<o.length;i++) {
			if(i > 0) { s.append(","); }
			s.append(o[i]);
		}
		return(s.toString());
	}

	public static int atoi(String a) throws BadValueException {
		try {
			int i = Integer.parseInt(a);
			return(i);
		}
		catch(NumberFormatException nfe) {
			String s = "atoi(" + a + "):" + nfe.getMessage();
			throw(new BadValueException(s));
		}
	}

	public static void sleep(int ms) {
		try { Thread.sleep(ms); }
		catch(InterruptedException ie) { }
	}

	public static ConsoleLocation constructConsoleLocation(InetAddress ia,int port) {
		ConsoleLocation cl = new ConsoleLocationImpl();
		cl.setInetAddress(ia);
		cl.setPort(port);
		return(cl);
	}

	public static ConsoleLocation constructConsoleLocation(String hostname,int port)
			throws UnknownHostException {

		return(constructConsoleLocation(InetAddress.getByName(hostname),port));
	}

	public static String basename4class(Class c) {
		if(null == c) { return("NULLCLASS"); }

		String fqcn = c.getName();

		if(null == c.getPackage()) { return(fqcn); }

		String fqpn = c.getPackage().getName();

		int i = 1 + fqpn.length();  // length of package name plus dot

		String basename = fqcn.substring(i);

		return(basename);
	}
}
