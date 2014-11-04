package com.lumenare.dif.language;

public class DDS_function
		extends DDSElement
		implements OperationAggregator {

	protected Log log = new Log(this);

	String _name;

	public DDS_function(String name) {
		_name = name;
		String method = "DDS_function(" + name + ")";
	}

	public String toString() {
		String s = "DDS_function(" + _name + ")";
		return(s);
	}

	public String name() {
		return(_name);
	}

	public void describe() {
		log.startDescribe();
		soa.describe();
		log.endDescribe();
	}

	// implement OperationAggregator

	protected SimpleOperationAggregator soa = new SimpleOperationAggregator();

	public void append(DDSOperation op) {
		soa.append(op);
	}

	protected void execute(ExecutionContext ec) throws DDSExecutionException {
		String method = "execute(" + ec + ")";
		log.invoke(method,ec);
		soa.execute(ec);
		log.ret(method,ec);
	}
}
