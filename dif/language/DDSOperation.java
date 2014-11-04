package com.lumenare.dif.language;

public abstract class DDSOperation extends DDSElement {
	public DDSOperation() {
		super();
	}

	public String toString() {
		return("DDSOperation()");
	}

	protected abstract void execute(ExecutionContext ec)
		throws DDSExecutionException;

	protected void fail(String method,String message) throws DDSExecutionException {
		log.info(method,message);
		throw(new DDSExecutionException(message));
	}

	protected String symtabGetValue(String method,ExecutionContext ec,String variable) {
		String value = ec.getSymbolTable().getValue(variable); // May Demeter forgive me!
		if(null == value) {
			log.warn(method,":VARIABLEISNULL(" + variable + "):SHOULDNOTHAPPEN");
		}
		return(value);
	}

	protected int symtabGetValueInt(String method,ExecutionContext ec,String variable)
			throws DDSExecutionException {

		String value = symtabGetValue(method,ec,variable);
		return(atoi(method,value,variable));
	}

	public int atoi(String method,String candidate,String variable) throws DDSExecutionException {
		try {
			return(Integer.parseInt(candidate));
		}
		catch(NumberFormatException nfe) {
			String s = "NUMBERFORMATEXCEPTION(";
			s += variable + "=";
			s += candidate + ")";

			log.info(method,s);
			log.info(method,nfe);

			throw(new DDSExecutionException(s));
		}
	}
}
