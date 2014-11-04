package com.lumenare.dif.language;

public class Operation_sleep extends DDSOperation {
	protected int _millis;

	public Operation_sleep(int millis) {
		_millis = millis;
	}

	public String toString() {
		String s = "Operation_sleep(" + _millis + ")";
		return(s);
	}

	public void describe() {
		log.describe();
	}

	protected void execute(ExecutionContext ec) throws DDSExecutionException {
		String method = "execute(" + ec + ")";
		log.invoke(method,ec);
		try {
			Thread.sleep(_millis);
		}
		catch(InterruptedException ie) {
			log.info(method,ie);
		}
		log.ret(method,ec);
	}
}


