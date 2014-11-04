package com.lumenare.dif.language;

import java.net.InetAddress;

import com.avulet.element.ConsoleLocation;

public class MutableSystemContext extends SystemContext {
	public MutableSystemContext() {
		super();
	}

	public void systemTftpServerMgmtIfIpAddr(String s) {
		_systemTftpServerMgmtIfIpAddr = s;
	}

	public void addTerminalServerConsoleLocation(String location,ConsoleLocation cl) {
		_terminalServerConsoleLocationMap.put(location,cl);
	}
}

