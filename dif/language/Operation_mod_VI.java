package com.lumenare.dif.language;

public class Operation_mod_VI extends Operation_binaryarithmeticoperator_VI {
	public Operation_mod_VI(String variable,String left,int right)
			throws DDSParseException {

		super(variable,left,right);
	}

	protected int evaluate(int left) {
		return(left % _right);
	}
}
