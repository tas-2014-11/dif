package com.lumenare.dif.language;

import java.io.IOException;
import java.util.Properties;

import com.avulet.consoleserver.telnet.TelnetConsoleController;
import com.avulet.element.ConsoleLocation;
import com.avulet.system.Config;

import com.lumenare.datastore.device.LmDeviceVo;

import com.lumenare.dif.BadValueException;
import com.lumenare.dif.Util;

public class Console {
	protected final Log _log = new Log(this);

	protected final DDS_console _ddsConsole;

	protected CommandLine _commandLine;
	protected TelnetConsoleController _tcc;

	public Console(DDS_console ddsConsole) {
		_ddsConsole = ddsConsole;
	}

	public String toString() {
		String s = "Console(";
		s += _ddsConsole + ",";
		s += _commandLine + ",";
		s += _tcc + ")";
		return(s);
	}

	public void describe() {
		_log.describe();
	}

	public String name() {
		return(_ddsConsole.name());
	}

	public void open(ExecutionContext ec)
			throws IOException,
			DDSExecutionException,
			BadValueException {

		if(null != _commandLine) {
			_log.info("ALREADYOPEN");
			return;
		}

		ConsoleLocation hardConsole = _ddsConsole.consoleLocation(ec);

		if(null == hardConsole) {
			String s = "NULLHARDCONSOLE:" + this;
			_log.info(s + "SHOULDNOTHAPPEN");
			throw(new DDSExecutionException(s)); // TODO: BVE instead ???
		}

		_tcc = TelnetConsoleControllerFactory.create(hardConsole,ec,
			name(),_ddsConsole.accessorLocation(),
			_log);

		ConsoleLocation softConsole = _tcc.getConsoleLocation();
		_commandLine = new CommandLine(softConsole);

		// TODO: pass prompts into CommandLine constructor to avoid race condition
		String[] prompts = _ddsConsole.prompts();
		_commandLine.installPromptDetector(prompts);
	}

	/*
	public void close() {
		if(null != _commandLine) {
			_commandLine.close();
		}
		_commandLine = null;

		if(null != _tcc) {
			_tcc.close();
		}
		_tcc = null;
	}
	*/

	public CommandLine commandLine() {
		return(_commandLine);
	}

	public ConsoleLocation consoleLocation() {
		if(null == _tcc) { return(null); }
		return(_tcc.getConsoleLocation());
	}
}

class TelnetConsoleControllerFactory {
	//protected final Log _log = new Log(this);

	protected static TelnetConsoleController create(ConsoleLocation hardConsole,
			ExecutionContext ec,
			String consoleName,
			String accessorLocation,
			Log log)
			throws IOException,
			BadValueException {

		String method = "TelnetConsoleController.create(";
		method += hardConsole + ",";
		method += ec + ",";
		method += consoleName + ",";
		method += accessorLocation + ")";

		SystemContext sc = ec.getSystemContext();
		Integer softport = sc.getConsoleSoftport(accessorLocation);

		log.info(method,"softport=" + softport);

		/*
		FIXME: The softport must be keyed on the console name specified
		FIXME: in the dds.  Not on the interface name.
		FIXME: For now we'll just always calculate.

		if(null == softport) {
		*/
			LmDeviceVo ldv = ec.getLdv();
			String seed = ldv.getPrimaryKey();

			log.info(method,"seed=" + seed);

			TelnetConsoleController tcc = createWithRetryOnBindFailure(hardConsole,seed);

			// now remember the softport
			ConsoleLocation softConsole = tcc.getConsoleLocation();
			int i = softConsole.getPort();
			sc.addConsoleSoftport(accessorLocation,i);

			return(tcc);
		/*
		}

		return(new TelnetConsoleController(hardConsole,softport.intValue()));
		*/
	}

	protected static TelnetConsoleController createWithRetryOnBindFailure(
			ConsoleLocation cl,String seed)
			throws
			IOException,
			BadValueException {

		// TODO: variableify these
		final int NTRIES = 100;
		final int SLEEPTIME = 100;

		SoftPortCalculator spc = new SoftPortCalculator(seed);
		int ntry = 0;
		while(true) {
			int softPort = spc.calculate();
			try {
				TelnetConsoleController tcc =
					new TelnetConsoleController(cl,softPort);

				return(tcc);
			}
			catch(java.net.BindException be) {
				String s = be.getMessage();
				s += ":" + cl + ":" + softPort + ":";
				//_log.info(s);

				if(++ntry > NTRIES) {
					s += "giving up after " + ntry + " attempts ";
					//_log.info(s);
					throw(be);
				}
				Util.sleep(SLEEPTIME);
			}
		}
	}
}

class SoftPortCalculator {
	protected final Log _log = new Log(this);

	protected static final int MINPORT = 1025;
	protected static final int MAXPORT = 65535;

	protected int _port;
	protected int _seed = 0; // TODO: make this final

	public SoftPortCalculator(String seed) {
		try { _seed = Util.atoi(seed); }
		catch(BadValueException bve) { }

		Properties props = Config.get().getProperties();
		String lrrp = props.getProperty("LocalRMIRegistryPort");

		int baseport = 0;
		if(null != lrrp) {
			try { baseport = 2 + Util.atoi(lrrp); }
			catch(BadValueException bve) { }
		}

		String s = "SoftPortCalculator(";
		s += "seed=" + _seed + ",";
		s += "baseport=" + baseport + ")";
		_log.info(s);

		_port = baseport + _seed;

		if(_port < MINPORT) { _port = MINPORT; }
		if(_port > MAXPORT) { _port = MINPORT; }
	}

	public int calculate() throws BadValueException {
		try {
			if(_port > MAXPORT) {
				String s = "port " + _port + " exceeds maximum value for seed " + _seed;
				throw(new BadValueException(s));
			}
			return(_port);
		}
		finally {
			++_port;
		}
	}
}

