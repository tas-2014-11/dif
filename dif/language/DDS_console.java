package com.lumenare.dif.language;

import java.net.UnknownHostException;

import com.avulet.element.ConsoleLocation;

import com.lumenare.datastore.device.LmDeviceVo;
import com.lumenare.dif.language.InventoryAccessorFactory;


import com.lumenare.dif.Util;

public class DDS_console extends DDSElement {
	protected final String _name;

	protected ConsoleAccessor _accessor;  // TODO: figure out how to make this final

	public DDS_console(String name) {
		_name = name;
	}

	public String toString() {
		String s = "DDS_console(" + _name + "," + _accessor + ")";
		return(s);
	}

	public void describe() {
		log.describe();
	}

	public String name() {
		return(_name);
	}

	protected ElementAggregator _promptAggregator = new SimpleElementAggregator();

	public void add(DDS_console_prompt p) {
		_promptAggregator.append(p);
	}

	public String[] prompts() {
		int size = _promptAggregator.size();
		String[] a = new String[size];
		for(int i=0;i<size;i++) {
			DDS_console_prompt p = (DDS_console_prompt)_promptAggregator.get(i);
			a[i] = p.text();
		}
		return(a);
	}

	public void add(DDS_console_direct cd) {
		_accessor = new ConsoleAccessor_direct(cd);
	}

	public void add(DDS_console_terminalserver ct) {
		_accessor = new ConsoleAccessor_terminalserver(ct);
	}

	public boolean accessorIsNull() {
		return(null == _accessor);
	}

	public String accessorLocation() {
		if(null == _accessor) { return("NULL"); } // FIXME: dont be dumb!!!
		return(_accessor.location());
	}

	public ConsoleLocation consoleLocation(SystemContext sc,LmDeviceVo ldv)
			throws UnknownHostException {

		if(null == _accessor) {
			log.info("NULLCONSOLEACCESSOR:" + this + ":SHOULDNOTHAPPEN");
			return(null);
		}
		return(_accessor.fetchValue(sc,ldv));
	}

	public ConsoleLocation consoleLocation(ExecutionContext ec) throws UnknownHostException {
		return(consoleLocation(ec.getSystemContext(),ec.getLdv()));
	}
}


abstract class ConsoleAccessor {
	protected final Log _log = new Log(this);

	protected final String _location;

	public ConsoleAccessor(String location) {
		_location = location;
	}

	public String toString() {
		String s = this.getClass().getName() + "(" + _location + ")";
		return(s);
	}

	public void describe() {
		_log.describe();
	}

	public String location() {
		return(_location);
	}

	/*
	public ConsoleLocation fetchValue(ExecutionContext ec) throws UnknownHostException {
		return(fetchValue(ec.getSystemContext(),ec.getLdv()));
	}
	*/

	public abstract ConsoleLocation fetchValue(SystemContext sc,LmDeviceVo ldv)
		throws UnknownHostException;
}

class ConsoleAccessor_direct extends ConsoleAccessor {
	protected final int _port;

	protected static final InventoryAccessor_mgmtIfIpAddr
		_mgmtIfIpAddr = new InventoryAccessor_mgmtIfIpAddr();

	public ConsoleAccessor_direct(DDS_console_direct cd) {
		super(cd.location());
		_port = cd.port();
	}

	public String toString() {
		String s = "ConsoleAccessor_direct(" + _location + "," + _port + ")";
		return(s);
	}

	public ConsoleLocation fetchValue(SystemContext sc,LmDeviceVo ldv)
			throws UnknownHostException {

		// TODO: Should I also check the value of InventoryAccessor_mgmtIfLocation ???
		// TODO: Yes!!!
		// TODO: Should mgmtIfLocation == _location
		// TODO: Yes!!!

		String mgmtIfIpAddr = _mgmtIfIpAddr.fetchValue(ldv);
		ConsoleLocation cl = Util.constructConsoleLocation(mgmtIfIpAddr,_port);
		return(cl);
	}
}

class ConsoleAccessor_terminalserver extends ConsoleAccessor {
	public ConsoleAccessor_terminalserver(DDS_console_terminalserver ct) {
		super(ct.location());
	}

	public String toString() {
		String s = "ConsoleAccessor_terminalserver(" + _location + ")";
		return(s);
	}

	// TODO: doesn't use ldv.
	public ConsoleLocation fetchValue(SystemContext sc,LmDeviceVo ldv) {
		ConsoleLocation cl = sc.getTerminalServerConsoleLocation(_location);
		return(cl);
	}
}
