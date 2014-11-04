package com.lumenare.dif.language;

public class Operation_ifne_VV extends  Operation_ifeq_VV {
	public Operation_ifne_VV(String left,String right) {
		super(left,right);
	}

	protected boolean condition(ExecutionContext ec) throws DDSExecutionException {
		return(!super.condition(ec));
	}
}
