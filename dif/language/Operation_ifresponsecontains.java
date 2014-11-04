package com.lumenare.dif.language;

import com.lumenare.dif.cli.LineHistoryBlock;

public class Operation_ifresponsecontains extends Operation_baseconditional {

	protected final String _text;

	public Operation_ifresponsecontains(String text) {
		_text = text;
	}

	public String toString() {
		return("Operation_ifresponsecontains(" + _text + ")");
	}

	public void describe() {
		log.describe();
		super.describe();
	}

	protected boolean condition(ExecutionContext ec) {
		String method = "condition()";
		log.invoke(method);

		LineHistoryBlock lhb = ec.getResponse();

		if(lhb.contains(_text)) {
			log.info("CONTAINS");
			return(true);
		}

		log.ret(method + ":DOESNOTCONTAIN");
		return(false);
	}
}

