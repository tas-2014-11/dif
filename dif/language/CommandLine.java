package com.lumenare.dif.language;

import com.lumenare.dif.cli.PromptDetector;
import com.lumenare.dif.cli.SimplePromptDetector;

public class CommandLine extends com.lumenare.dif.cli.CommandLine {
	protected Log _log = new Log(this);

	public CommandLine(com.avulet.element.ConsoleLocation cl) throws java.io.IOException {
		super(cl);
	}

	public CommandLine(java.net.InetAddress ia,int port) throws java.io.IOException {
		super(ia,port);
	}

	public String toString() {
		return(super.toString());
	}

	public void describe() {
		_log.describe();
	}

	public void installPromptDetector(String[] prompts) {
		PromptDetector pd = new SimplePromptDetector(prompts);
		setPromptDetector(pd);
	}
}
