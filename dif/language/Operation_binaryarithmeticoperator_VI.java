package com.lumenare.dif.language;

// TODO: this class and it's VV sibling share a common superclass.

public abstract class Operation_binaryarithmeticoperator_VI extends DDSOperation {
	protected final String _variable;
	protected final String _left;
	protected final int _right;

	public Operation_binaryarithmeticoperator_VI(String variable,String left,int right)
			throws DDSParseException {

		_variable = checknull(this,variable,"variable");
		_left = checknull(this,left,"left");
		_right = right;
	}

	public String toString() {
		String s = _basename4class + "(";
		s += _variable + ",";
		s += _left + ",";
		s += _right + ")";
		return(s);
	}

	protected final void execute(ExecutionContext ec) throws DDSExecutionException {
		String method = "execute(" + ec + ")";
		log.invoke(method);

		int left = symtabGetValueInt(method,ec,_left);

		int value = evaluate(left);

		SymbolTable symtab = ec.getSymbolTable();
		symtab.assign(_variable,value);

		log.ret(method);
	}

	protected abstract int evaluate(int left);
}
