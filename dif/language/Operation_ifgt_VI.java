package com.lumenare.dif.language;

public class Operation_ifgt_VI extends Operation_binaryconditional_SI {
	public Operation_ifgt_VI(String left,int right) {
		super(left,right);
	}

	protected boolean condition(ExecutionContext ec) throws DDSExecutionException {
		String method = "condition()";

		String leftString = symtabGetValue(method,ec,_left);
		log.info(method,_left + "=" + leftString + "," + _right);

		if(null == leftString) { // ShouldNotHappen
			return(false);
		}

		int left = atoi(method,leftString,_left);

		return(left > _right);
	}
}

