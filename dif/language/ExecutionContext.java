package com.lumenare.dif.language;

/**
 *
 * This is a facade for all variables which might be needed
 * by a driver a runtime.
 *
 */

import com.lumenare.common.domain.attribute.Attribute;
import com.lumenare.common.domain.attribute.AttributeCollection;
import com.lumenare.common.domain.attribute.AttributeFactory;
import com.lumenare.common.domain.attribute.StringAttribute;
import com.lumenare.common.domain.attribute.adapter.AttributeCollectionImpl;
import com.lumenare.common.domain.attribute.adapter.AttributeUtil;

import com.lumenare.datastore.device.LmDeviceVo;

import com.lumenare.dif.cli.LineHistoryBlock;

public class ExecutionContext {
	protected final Log log = new Log(this);

	protected final DDSTree _ddst;
	protected final SymbolTable _symtab;
	protected final LmDeviceVo _ldv;
	protected final SystemContext _sc;
	protected final ConsoleTable _contab;

	public ExecutionContext(DDSTree ddst,SymbolTable symtab,
			LmDeviceVo ldv,
			SystemContext sc,
			ConsoleTable contab) {

		_ddst = ddst;
		_symtab = symtab;
		_ldv = ldv;
		_sc = sc;
		_contab = contab;
	}

	public String toString() {
		/*
		String s = "ExecutionContext(";
		s += _ddst + ",";
		s += _symtab + ",";
		s += _ldv + ",";
		s += _sc + ")";
		return(s);
		*/

		return("EC");
	}

	public void describe() {
		log.startDescribe();
		_ddst.describe();
		_symtab.describe();

		// TODO: Write LmDeviceVo.describe()
		String s = AccessorUtility.formatLdv(_ldv);
		log.describe(s);

		/*
		// TODO: Write CommandLine.describe()
		if(null != _cl) {
			log.describe(_cl.toString());
		}
		else {
			log.describe("COMMANDLINEISNULL");
		}
		*/

		_sc.describe();
		_contab.describe();

		log.endDescribe();
	}


	// TODO: Make this return an immutable!

	public DDSTree getDDSTree() {
		return(_ddst);
	}

	public SymbolTable getSymbolTable() {
		return(_symtab);
	}

	public LmDeviceVo getLdv() {
		return(_ldv);
	}

	public SystemContext getSystemContext() {
		return(_sc);
	}

	public ConsoleTable getConsoleTable() {
		return(_contab);
	}


	// TODO: should close be synchronized ???
	public void close() {
		_contab.close();
	}


	protected AttributeCollection _inputArgs = new AttributeCollectionImpl();

	public void setInputArgs(AttributeCollection ac) {
		_inputArgs = ac;
	}

	public String fetchInputArgValue(String name) {
		String value = AttributeUtil.getStringAttributeStringValue(_inputArgs,name);
		return(value);
	}


	protected int _stackDepth = 0;
 
	public void push() {
		_stackDepth++;
	}
 
	public void pop() {
		_stackDepth--;
		if(_stackDepth < 0) {
			log.warn("pop()","STACKUNDERRUN:" + _stackDepth);
			_stackDepth = 0;
		}
	}
 
	public int stackDepth() {
		return(_stackDepth);
	}
 
	public void stackReset() {
		_stackDepth = 0;
	}


	protected LineHistoryBlock _lhb;

	public void forgetResponse() {
		_lhb = null;
	}

	public void rememberResponse(LineHistoryBlock lhb) {
		_lhb = lhb;
	}

	public LineHistoryBlock getResponse() {
		return(_lhb);
	}


	protected Console _currentConsole;

	public void currentConsole(Console console) {
		_currentConsole = console;
	}

	public Console currentConsole() {
		return(_currentConsole);
	}


	protected AttributeCollection _outputValues = new AttributeCollectionImpl();

	public void clearOutputValues() {
		_outputValues = new AttributeCollectionImpl();
	}

	/*
	public void addOutputValue(Attribute a) {
		_outputValues.addAttribute(a);
	}
	*/

	public void addOutputValue(String name,String value) {
		StringAttribute sa = AttributeFactory.createString(name,value);
		_outputValues.addAttribute(sa);
	}

	public AttributeCollection fetchOutputValues() {
		return(_outputValues);
	}
}
