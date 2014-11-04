package com.lumenare.dif.language;

import java.util.StringTokenizer;
import java.io.IOException;

import com.lumenare.dif.cli.LineHistoryBlock;
import com.lumenare.dif.cli.TimeoutException;

public class Operation_writeln extends DDSOperation {
	protected final String _text;
	protected final int _getPromptWithin;
	protected final String _failOnReceiptOf;
	protected final String _expectResponse;
	protected final int _delayBeforeRead;
	protected final String _logOnFail;

	protected final boolean __needToParse;

	public Operation_writeln(String text,int getPromptWithin,
			String failOnReceiptOf,String expectResponse,
			int delayBeforeRead,
			String logOnFail) {

		_text = text;
		_getPromptWithin = getPromptWithin;
		_failOnReceiptOf = failOnReceiptOf;
		_expectResponse = expectResponse;
		_delayBeforeRead = delayBeforeRead;
		_logOnFail = logOnFail;

		__needToParse = ( (null != _expectResponse) || (null != _failOnReceiptOf) );
	}

	public String toString() {
		String s = "Operation_writeln(";
		s += _text + ",";
		s += _getPromptWithin + ",";
		s += _failOnReceiptOf + ",";
		s += _expectResponse + ",";
		s += _delayBeforeRead + ",";
		s += _logOnFail + ")";
		return(s);
	}

	protected String fetchValue(String varName,ExecutionContext ec) {
		String method = "fetchAccessor(" + varName + "," + ec + ")";

		// See if this is a well known variable.
		Accessor accessor = InventoryAccessorFactory._locateAccessor(varName);
		if(null != accessor) {
			String value = accessor.fetchValue(ec);
			log.info(method,"FOUNDINVENTORYACCESSOR:" + varName + "='" + value + "'");
			return(value);
		}

		// The variable is not well known.
		// See if it is declared within this dds.

		SymbolTable symtab = ec.getSymbolTable();
		if(symtab.hasBeenDeclared(varName)) {
			String value = symtab.getValue(varName);
			log.info(method,"FOUNDDECLAREDVARIABLE:" + varName + "='" + value + "'");
			return(value);
		}

		log.info(method,"NOACCESSOR(" + varName + ")");
		return(null);
	}

	protected String nextToken(StringTokenizer st) {
		if(st.hasMoreTokens()) { return(st.nextToken()); }
		return(null);
	}

	protected String realText(String in,ExecutionContext ec) {
		String delims = "$()";
		StringBuffer out = new StringBuffer(in.length());

		String s0,s1,s2,s3;

		StringTokenizer st = new StringTokenizer(in,delims,true);

		parseLoop: while(st.hasMoreTokens()) {
			s0 = st.nextToken();
			if(s0.equals("$")) {
				if(null != (s1=nextToken(st))) {
					if(s1.equals("(")) {
						if(null != (s2=nextToken(st))) {
							if(null != (s3=nextToken(st))) {
								if(s3.equals(")")) {
									String value = fetchValue(s2,ec);

									if(null == value) {
										out.append(s0);
										out.append(s1);
										out.append(s2);
										out.append(s3);
										continue parseLoop;
									}

									out.append(value);
									continue parseLoop;
								}
								out.append(s0);
								out.append(s1);
								out.append(s2);
								out.append(s3);
								continue parseLoop;
							}
							out.append(s0);
							out.append(s1);
							out.append(s2);
							continue parseLoop;
						}
						out.append(s0);
						out.append(s1);
						continue parseLoop;
					}
					out.append(s0);
					out.append(s1);
					continue parseLoop;
				}
			}
			out.append(s0);
			continue parseLoop;
		}
		return(out.toString());
	}

	protected LineHistoryBlock doWriteAndRead(String realText,CommandLine cl)
			throws IOException,TimeoutException {

		// _delayBeforeRead and _getPromptWithin are mutually exclusive (!?!?!?)

		if(_getPromptWithin < 0) {
			cl.write(realText);

			if(_delayBeforeRead > 0) {
				return(cl.readFully(_delayBeforeRead));
			}
			else {
				return(cl.readFully());
			}
		}
		else {
			return(cl.writeAndRead(realText,_getPromptWithin));
		}
	}

	protected void execute(ExecutionContext ec) throws DDSExecutionException {
		String method = "execute(" + ec + ")";
		log.invoke(method,ec);

		Console console = ec.currentConsole();
		if(null == console) {
			String s = "NOCURRENTCONSOLE";
			log.warn(method,s);
			throw(new DDSExecutionException(s));
		}

		CommandLine cl = console.commandLine();

		String realText = realText(_text,ec);

		LineHistoryBlock lhb;
		try {
			ec.forgetResponse();
			lhb = doWriteAndRead(realText,cl);
			ec.rememberResponse(lhb);
		}
		catch(IOException ioe) {
			// FIXME: chain this exception
			String s = toString() + "." + method + "," + cl + "," + _text + ":";
			throw(new DDSExecutionException(s));
		}
		catch(TimeoutException te) {
			// FIXME: chain this exception
			String s = toString() + "." + method + "," + cl + "," + _text + ":";
			throw(new DDSExecutionException(s));
		}

		if(!__needToParse) {
			// If both _expectResponse and _failOnReceiptOf are unspecified
			// then we can leave the theater before the credits roll.
			// So like yesterday we saw Spider-Man and everybody left as
			// soon as the closing credits started.  What's up with that?
			return;
		}

		// FIXME:  add logOnFail

		if(null != _expectResponse) {
			if(lhb.contains(_expectResponse)) {
				log.info(method,"CONTAINSEXPECTED(" + _expectResponse + ")");
			}
			else {
				String s = "DOESNOTCONTAINEXPECTED(" + _expectResponse + ")";
				log.info(method,s);
				throw(new DDSExecutionException(s));
			}
		}

		if(null != _failOnReceiptOf) {
			if(lhb.contains(_failOnReceiptOf )) {
				String s = "CONTAINSFAIL(" + _failOnReceiptOf + ")";
				log.info(method,s);
				throw(new DDSExecutionException(s));
			}
			else {
				log.info(method,"DOESNOTCONTAINFAIL(" + _failOnReceiptOf + ")");
			}
		}

		log.ret(method,ec);
	}
}

