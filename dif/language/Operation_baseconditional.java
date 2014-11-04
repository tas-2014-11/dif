package com.lumenare.dif.language;

public abstract class Operation_baseconditional
		extends DDSOperation
		implements OperationAggregator {

	public String toString() {
		return("Operation_baseconditional()");
	}

	protected SimpleOperationAggregator _soa = new SimpleOperationAggregator();

	// implement OperationAggregator
	public final void append(DDSOperation op) {
		_soa.append(op);
	}

	public void describe() {
		_soa.describe();
	}

	protected abstract boolean condition(ExecutionContext ec) throws DDSExecutionException;

	protected final void execute(ExecutionContext ec) throws DDSExecutionException {
		boolean condition = condition(ec);
		log.info("condition="+condition);

		try {
			Log.push();

			if(condition) {
				_soa.execute(ec);
			}
		}
		finally {
			Log.pop();
		}
	}
}
