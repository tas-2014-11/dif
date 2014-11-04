package com.lumenare.dif.language;

import java.util.StringTokenizer;

import com.lumenare.dif.cli.LineHistoryBlock;

public class Operation_bindonfirstmatch extends DDSOperation {
	protected final String _variable;
	protected final String _text;
	protected final int _field;

	public Operation_bindonfirstmatch(String variable,String text,int field) throws DDSParseException {
		_variable = checknull(this,variable,"variable");
		_text = checklength(this,text,"text");
		_field = field;
	}

	public String toString() {
		String s = "Operation_bindonfirstmatch(";
		s += _variable + ",";
		s += _text + ",";
		s += _field + ")";
		return(s);
	}

	public void describe() {
		log.describe();
	}

	protected void execute(ExecutionContext ec) throws DDSExecutionException {
		String method = "execute(" + ec + ")";
		log.invoke(method);

		SymbolTable symtab = ec.getSymbolTable();

		LineHistoryBlock lhb = ec.getResponse();
		if(null == lhb) {
			// FIXME: is this the Right Thing to do???
			//fail(method,"NOLINEHISTORY");

			log.warn(method,"NOLINEHISTORY");

			symtab.assign(_variable,"");
			log.ret(method);
			return;
		}

		String line = lhb.getLineContaining(_text);
		if(null == line) {
			// FIXME: is this the Right Thing to do???
			//fail(method,"NOMATCHINGLINE");

			log.warn(method,"NOMATCHINGLINE");

			symtab.assign(_variable,"");
			log.ret(method);
			return;
		}

		int tokenCount = 0;
		StringTokenizer st = new StringTokenizer(line);
		while(st.hasMoreTokens()) {
			String token = st.nextToken();
			if(tokenCount == _field) {
				symtab.assign(_variable,token);
				log.ret(method);
				return;
			}
			++tokenCount;
		}

		log.warn(method,"NOTENOUGHTOKENS(" + line + "):" +  tokenCount + "<=" + _field);

		// FIXME: is this the Right Thing to do???
		symtab.assign(_variable,"");
		log.ret(method);
	}
}
