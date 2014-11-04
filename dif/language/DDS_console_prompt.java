package com.lumenare.dif.language;

public class DDS_console_prompt extends DDSElement {
	protected final String _text;

	public DDS_console_prompt(String text) {
		_text = text;
	}

	public String toString() {
		String s = "DDS_console_prompt(" + _text + ")";
		return(s);
	}

	public String text() {
		return(_text);
	}
}
