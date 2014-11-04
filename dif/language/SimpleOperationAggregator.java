package com.lumenare.dif.language;

import java.util.ArrayList;

// TODO: should this extend (or encapsulate) ElementAggregator

public class SimpleOperationAggregator implements OperationAggregator {
	protected Log log = new Log(this);

	protected ArrayList operationList = new ArrayList();

	public void append(DDSOperation op) {
		// FIXME: Handle null!!!
		operationList.add(op);
	}

	public void describe() {
		for(int i=0;i<operationList.size();i++) {
			DDSOperation op = (DDSOperation)operationList.get(i);
			op.describe();
		}
	}

	protected void execute(ExecutionContext ec) throws DDSExecutionException {
		Log.push();
		ec.push();

		try {
			for(int i=0;i<operationList.size();i++) {
				DDSOperation op = (DDSOperation)operationList.get(i);
				op.execute(ec);
			}
		}
		finally {
			ec.pop();
			Log.pop();
		}
	}
}
