package com.lumenare.dif.language;

public class DDS_console_direct extends DDSElement {
	protected final String _location;
	protected final int _port;

	public DDS_console_direct(String location,int port) {
		_location = location;
		_port = port;
	}

	public String toString() {
		String s = "DDS_console_direct(" + _location + "," + _port + ")";
		return(s);
	}

	public String location() {
		return(_location);
	}

	public int port() {
		return(_port);
	}
}
