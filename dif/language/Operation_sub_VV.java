package com.lumenare.dif.language;

public class Operation_sub_VV extends Operation_binaryarithmeticoperator_VV {
	public Operation_sub_VV(String variable,String left,String right)
			throws DDSParseException {

		super(variable,left,right);
	}

	protected int evaluate(int left,int right) {
		return(left - right);
	}
}
