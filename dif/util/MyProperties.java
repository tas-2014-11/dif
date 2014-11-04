package com.lumenare.dif.util;

import java.io.FileInputStream;
import java.io.IOException;

public class MyProperties extends java.util.Properties {
	public MyProperties(String fileName) {
		try {
			String path;
			path = System.getProperty("user.dir",".");
			path += "/";
			path += fileName;

			Log.debug("MyProperties:" + path);

			FileInputStream fis = new FileInputStream(path);
			load(fis);
		}
		catch(IOException ioe) {
			String logmsg = "MyProperties(" + fileName + ")";
			Log.error(logmsg,ioe);
		}
	}
}
