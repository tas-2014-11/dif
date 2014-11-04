package com.lumenare.dif.language;

public class DDS_console_terminalserver extends DDSElement {
	protected final String _location;

	public DDS_console_terminalserver(String location) {
		_location = location;
	}

	public String toString() {
		String s = "DDS_console_terminalserver(" + _location + ")";
		return(s);
	}

	public String location() {
		return(_location);
	}
}
