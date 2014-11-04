package com.lumenare.dif.language;

import java.net.InetAddress;
import java.util.HashMap;

import com.avulet.element.ConsoleLocation;

// FIXME: Keep this in sync with dif.remote.Server !!!
// FIXME: Keep this in sync with dif.DifApplication !!!

// TODO: Since this has a Log (LogIt -> log4j) embedded in
// TODO: it, how big does it get when serialized ???

public class SystemContext implements java.io.Serializable {
	protected final Log _log = new Log(this);

	protected String _systemTftpServerMgmtIfIpAddr;
	protected final HashMap _terminalServerConsoleLocationMap = new HashMap();
	protected final HashMap _consoleSoftportMap = new HashMap();

	public SystemContext() {
	}

	public String toString() {
		String s = this.getClass().getName();
		s += "(";
		s += _systemTftpServerMgmtIfIpAddr + ",";
		s += _terminalServerConsoleLocationMap + ",";
		s += _consoleSoftportMap + ")";
		return(s);
	}

	public void describe() {
		_log.describe();
	}


	public String systemTftpServerMgmtIfIpAddr() {
		return(_systemTftpServerMgmtIfIpAddr);
	}

	public ConsoleLocation getTerminalServerConsoleLocation(String location) {
		return((ConsoleLocation)_terminalServerConsoleLocationMap.get(location));
	}

	public Integer getConsoleSoftport(String location) {
		return((Integer)_consoleSoftportMap.get(location));
	}

	// TODO: move this to MutableSystemContext
	// standalone calls this.
	// lms/ims does not call this.
	// i mean it.
	public void addConsoleSoftport(String location,int port) {
		Integer i = new Integer(port);
		_consoleSoftportMap.put(location,i);
	}
}
