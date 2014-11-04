package com.lumenare.dif.language;

public class Operation_fail extends DDSOperation {
	protected final String _message;

	public Operation_fail(String message) {
		_message = message;
	}

	public String toString() {
		String s = "Operation_fail(" + _message + ")";
		return(s);
	}

	public void describe() {
		log.describe();
	}

	protected void execute(ExecutionContext ec) throws DDSExecutionException {
		String method = "execute(" + ec + ")";
		log.invoke(method,ec);
		throw(new DDSExecutionException(_message));
	}
}

