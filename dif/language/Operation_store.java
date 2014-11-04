package com.lumenare.dif.language;

public abstract class Operation_store extends DDSOperation {
	protected final String _path;

	public Operation_store(String path) throws DDSParseException {
		_path = checklength(this,path,"path");
	}

	public String toString() {
		String s = "Operation_store(";
		s += _path + ")";
		return(s);
	}

	protected final void execute(ExecutionContext ec) throws DDSExecutionException {
		String method = "execute(" + ec + ")";
		log.invoke(method);

		String value = fetchValue(ec);

		// TODO: Should we check for duplicates???
		ec.addOutputValue(_path,value);

		log.ret(method);
	}

	protected abstract String fetchValue(ExecutionContext ec);
}
