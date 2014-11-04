package com.lumenare.dif.language;

import com.lumenare.dif.BadValueException;

public class Operation_open extends DDSOperation {
	protected final String _name;

	public Operation_open(String name) {
		_name = name;
	}

	public String toString() {
		String s = "Operation_open(" + _name + ")";
		return(s);
	}

	protected void execute(ExecutionContext ec) throws DDSExecutionException {
		String method = "execute(" + ec + ")";
		log.invoke(method,ec);

		ConsoleTable contab = ec.getConsoleTable();
		Console console = contab.getConsole(_name);

		if(null == console) {
			String s = toString() + "." + method + ":NOSUCHCONSOLE(" + _name + ")";
			throw(new DDSExecutionException(s));
		}

		try {
			console.open(ec);
			ec.currentConsole(console);
		}
		catch(BadValueException bve) {
			// FIXME: chain this exception
			log.info(method,bve);
			String s = toString() + "." + method + ":" + bve + ":";
			throw(new DDSExecutionException(s));
		}
		catch(java.io.IOException ioe) {
			// FIXME: chain this exception
			log.info(method,ioe);
			String s = toString() + "." + method + ":" + ioe + ":";
			throw(new DDSExecutionException(s));
		}
	}
}

