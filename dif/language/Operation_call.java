package com.lumenare.dif.language;

public class Operation_call extends DDSOperation {
	protected String _funcName;

	public Operation_call(String funcName) {
		_funcName = funcName;
	}

	public String toString() {
		String s = "Operation_call(" + _funcName + ")";
		return(s);
	}

	public void describe() {
		log.describe();
	}

	protected void execute(ExecutionContext ec) throws DDSExecutionException {
		String method = "execute(" + ec + ")";
		log.invoke(method,ec);

		DDSTree ddst = ec.getDDSTree();
		DDS_function f = ddst.locateFunction(_funcName);

		if(null == f) {
			log.info(method,"COULDNOTFINDFUNCTION");
			throw(new DDSExecutionException(method));
		}

		ec.push();
		f.execute(ec);
		ec.pop();

		log.ret(method,ec);
	}
}

