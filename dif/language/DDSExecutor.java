package com.lumenare.dif.language;

import java.net.UnknownHostException;
import java.util.Iterator;

import com.avulet.element.ConsoleLocation;

import com.lumenare.common.domain.attribute.AttributeCollection;
import com.lumenare.common.domain.attribute.adapter.AttributeCollectionImpl;
import com.lumenare.common.domain.attribute.adapter.AttributeUtil;
import com.lumenare.datastore.device.LmDeviceVo;

import com.lumenare.dif.manager.MissingFunctionException;

public class DDSExecutor {
	protected final Log log = new Log(this);

	protected final ExecutionContext _ec;

	public DDSExecutor(DDSTree ddst,LmDeviceVo ldv,SystemContext sc)
			throws java.io.IOException,
			DDSExecutionException {

		SymbolTable symtab = ddst.generateSymbolTable();

		ConsoleTable contab = generateConsoleTable(ddst,sc,ldv);

		_ec = new ExecutionContext(ddst,symtab,ldv,sc,contab);
	}

	public String toString() {
		String s = "DDSExecutor(" + _ec + ")";
		return(s);
	}

	public void describe() {
		log.startDescribe();
		_ec.describe();
		log.endDescribe();
	}

	// TODO: Hmm.  Something's not quite right here.
	// TODO: declare and assign are now redundant.
	// TODO: the sc and ldv are only here to validate console data
	public ConsoleTable generateConsoleTable(DDSTree ddst,
			SystemContext sc,
			LmDeviceVo ldv)
			throws DDSExecutionException {

		ConsoleTable contab = new ConsoleTable();

		Iterator i = ddst.consoleMapValuesIterator();
		while(i.hasNext()) {
			DDS_console ddsConsole = (DDS_console)i.next();

			// FIXME: this sucks
			try {
			// sanity check the accessor for this console
			ConsoleLocation consoleLocation = ddsConsole.consoleLocation(sc,ldv);
			if(null == consoleLocation) {
				// FIXME: add visibility into what device this is
				String s = "NULLCONSOLELOCATION:";
				s += ddsConsole + ":";
				s += sc + ":";
				s += AccessorUtility.formatLdv(ldv) + ":";
				log.info(s);
				throw(new DDSExecutionException(s));
			}
			}
			catch(UnknownHostException uhe) {
				// FIXME: add visibility into what device this is
				String s = uhe.getMessage() + ":";
				s += ddsConsole + ":";
				s += sc + ":";
				s += AccessorUtility.formatLdv(ldv) + ":";
				log.info(s);
				throw(new DDSExecutionException(s));
			}

			Console console = new Console(ddsConsole);

			contab.declare(console);
			contab.assign(console);
		}

		return(contab);
	}

	public void close() {
		_ec.close();
	}

	protected void finalize() {
		close();
	}

	public void setInputArgs(AttributeCollection ac) {
		_ec.setInputArgs(ac);
	}

	public AttributeCollection execute(String functionName)
			throws
			MissingFunctionException,
			DDSExecutionException {

		String method = "execute(" + functionName + ")";
		log.invoke(method);

		DDSTree ddst = _ec.getDDSTree();
		DDS_function f = ddst.locateFunction(functionName);

		if(null == f) {
			log.info(method,"COULDNOTFINDFUNCTION(" + functionName + ")");
			throw(new MissingFunctionException(functionName));
		}

		log.info(method,"FOUNDFUNCTION");
		f.describe();

		try {
			_ec.stackReset();
			_ec.clearOutputValues();
			f.execute(_ec);
		}
		catch(DDSExecutionException ddsee) {
			log.info(method,ddsee);
			throw(ddsee);
		}

		AttributeCollection ac = _ec.fetchOutputValues();
		log.ret(method + ":" + AttributeUtil.describe(ac));
		return(ac);
	}

	public boolean supportsFunction(String functionName) {
		DDSTree ddst = _ec.getDDSTree();
		DDS_function f = ddst.locateFunction(functionName);
		if(null == f) { return(false); }
		return(true);
	}

	public String deviceRoleName() {
		LmDeviceVo ldv = _ec.getLdv();
		return(ldv.getDeviceRole().toString());
	}

	public String[] functionNames() {
		DDSTree ddst = _ec.getDDSTree();
		return(ddst.functionNames());
	}

	public void describeFunction(String functionName) {
		DDSTree ddst = _ec.getDDSTree();
		DDS_function f = ddst.locateFunction(functionName);
		if(null == f) { log.info("NOSUCHFUNCTION(" + functionName + ")"); }
		f.describe();
	}

	public ConsoleLocation consoleLocation() {
		ConsoleTable contab = _ec.getConsoleTable();

		Console console = contab.getOne();
		if(null == console) { return(null); }

		return(console.consoleLocation());
	}

	public LmDeviceVo ldvHack() {
		return(_ec.getLdv());
	}
}
