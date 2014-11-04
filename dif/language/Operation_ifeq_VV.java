package com.lumenare.dif.language;

public class Operation_ifeq_VV extends Operation_binaryconditional_SS {
	public Operation_ifeq_VV(String left,String right) {
		super(left,right);
	}

	protected boolean condition(ExecutionContext ec) throws DDSExecutionException {
		String method = "condition()";

		String left = symtabGetValue(method,ec,_left);
		String right = symtabGetValue(method,ec,_right);

		log.info(method,_left + "=" + left + "," + _right + "=" + right);

		if((null == left) || (null == right)) { // ShouldNotHappen
			return(false);
		}

		return(left.equals(right));
	}
}
