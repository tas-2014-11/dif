package com.lumenare.dif.language;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.lumenare.dif.language.DDSException;

public class DDSTreeFactory {
	protected final Log log = new Log(this);
	protected final InputSource _is;

	public DDSTreeFactory(byte[] b) {
		String method = "DDSTreeFactory(" + b + ")";
		//log.invoke(method);

		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		_is = new InputSource(bais);

		//log.ret(method);
	}

	public String toString() {
		return("DDSTreeFactory()");
	}

	protected final DDSParser _ddsp = new DDSParser();

	public DDSTree build() throws IOException,SAXException {
		String method = "build()";
		//log.invoke(method,"_is=" + _is);

		XMLReader xr = new org.apache.xerces.parsers.SAXParser();
		setFeatures(xr);
		getFeatures(xr);

		xr.setContentHandler(_ddsp);
		xr.setErrorHandler(_ddsp);

		xr.parse(_is);

		DDSTree ddst = _ddsp.get();

		// TODO: now sanity check the DDSTree...
		// TODO: 1. make sure all call targets have corresponding functions
		// TODO: 2. check references between bind & declare
		// TODO: 3. check existance of variables embedded in writeln
		// TODO: 4. check for duplicate declare statements

		//log.ret(method + ":" + ddst);
		return(ddst);
	}

	public int countError() {
		return(_ddsp.countError());
	}

	public int countFatalError() {
		return(_ddsp.countFatalError());
	}

	public int countWarning() {
		return(_ddsp.countWarning());
	}

	// I really don't have a clue what all these features do. :/

	static String[] featureName = {
		"http://xml.org/sax/features/validation",
		"http://xml.org/sax/features/namespaces",
		/*
		"http://xml.org/sax/features/namespace-prefixes",
		*/
		"http://apache.org/xml/features/validation/schema",
		"http://apache.org/xml/features/validation/schema-full-checking"
	};

	protected void setFeatures(XMLReader xr) throws SAXException {
		String method = "setFeatures()";
		//log.invoke(method,"xr=" + xr);

		for(int i=0;i<featureName.length;i++) {
			//log.info(method,featureName[i]);

			xr.setFeature(featureName[i],true);

			/*
			try {
				xr.setFeature(featureName[i],true);
			}
			catch(SAXNotSupportedException snse) {
				snse.printStackTrace();
			}
			catch(SAXNotRecognizedException snre) {
				snre.printStackTrace();
			}
			*/
		}

		//log.ret(method);
	}

	//protected void getFeatures(XMLReader xr) throws SAXException {
	protected void getFeatures(XMLReader xr) {
		String method = "getFeatures()";
		for(int i=0;i<featureName.length;i++) {
			String s = featureName[i];

			try {
				boolean b = xr.getFeature(featureName[i]);
				s += "=" + b;
			}
			catch(SAXNotSupportedException snse) {
				s += snse.getMessage();
				//snse.printStackTrace();
			}
			catch(SAXNotRecognizedException snre) {
				s += snre.getMessage();
				//snre.printStackTrace();
			}

			//log.info(method,s);
		}
	}
}

class DDSParser extends DefaultHandler
		implements
		ContentHandler,
		ErrorHandler {

	protected final Log log = new Log(this);

	protected DDSTree _ddst;

	public DDSParser() {
		String method = "DDSParser()";
		//log.invoke(method);
		//log.ret(method);
	}

	public DDSTree get() {
		return(_ddst);
	}

	public String toString() {
		return("DDSParser()");
	}

	public void startDocument() {
		String method = "startDocument()";
		//log.invoke(method);
		if(traceon("startDocument")) { printLocation(method); }
		//log.ret(method);
	}

	public void endDocument() throws SAXException {
		String method = "endDocument()";
		//log.invoke(method);
		if(traceon("endDocument")) { printLocation(method); }

		if(null == _ddst) {
			abort(method,"UNEXPECTED_END_DOCUMENT");
		}

		//log.ret(method);
	}

	public void startPrefixMapping(String prefix,String uri) throws SAXException {
		String method = "startPrefixMapping(";
		method += prefix + "," + uri + ")";
		//log.invoke(method);
		if(traceon("startPrefixMapping")) { printLocation(method); }
		//log.ret(method);
	}

	public void endPrefixMapping(String prefix) throws SAXException {
		String method = "endPrefixMapping(";
		method += prefix + ")";
		//log.invoke(method);
		if(traceon("endPrefixMapping")) { printLocation(method); }
		//log.ret(method);
	}

	// TODO: use a non-Vector based stack
	protected final Stack _aggregatorStack = new Stack();

	protected DDS_console _currentConsole = null;

	public void startElement(String uri,String name,String qName,Attributes attrs)
			throws SAXException {

		String method = "startElement(" + uri + "," + name + "," + qName + ")";

		try {
			doStartElement(uri,name,qName,attrs);
		}
		catch(DDSParseException ddpse) {
			log.info(method,ddpse);
			abort(method,ddpse);
		}
	}

	public void doStartElement(String uri,String name,String qName,Attributes attrs)
			throws SAXException,DDSParseException {

		String method = "doStartElement(";
		//method += uri + "," + name + "," + qName + "," + attrs + ")";
		method += uri + "," + name + "," + qName + ")";
		//log.invoke(method);
		if(traceon("startElement")) { printLocation(method); }

		if(name.equals("devicedriver")) {
			String driverName = getAttributeValue(method,attrs,"name");
			String description = getOptionalAttributeValue(attrs,"description");
			String authorName = getOptionalAttributeValue(attrs,"authorname");
			String authorCompany = getOptionalAttributeValue(attrs,"authorcompany");
			String authorEmail = getOptionalAttributeValue(attrs,"authoremail");
			_ddst = new DDSTree(driverName,description,authorName,authorCompany,authorEmail);
			return;
		}

		if(null == _ddst) {
			abort(method,"NODEVICEDRIVERROOTELEMENT(" + name + ")");
		}

		if(name.equals("declare")) {
			String varName = getAttributeValue(method,attrs,"name");

			// Not allowed to declare variables which are internally defined.
			Accessor accessor = InventoryAccessorFactory._locateAccessor(varName);
			if(accessor != null) {
				abort(method,"DECLAREINTERNALVARIABLE(" + varName + ")");
			}

			try {
				DDS_declare d = new DDS_declare(varName);
				_ddst.add(d);
			}
			catch(DDSParseException ddpse) {
				log.info(method,ddpse);
				abort(method,ddpse);
			}

			return;
		}

		if(name.equals("function")) {
			// FIXME: check for multiple definitions of same function name
			String funcName = getAttributeValue(method,attrs,"name");
			DDS_function f = new DDS_function(funcName);
			_ddst.add(f);
			_aggregatorStack.push(f);
			return;
		}

		if(name.equals("supporteddts")) {
			String manufacturer = getAttributeValue(method,attrs,"manufacturer");
			String model = getAttributeValue(method,attrs,"model");
			DDS_supporteddts s = new DDS_supporteddts(manufacturer,model);
			_ddst.add(s);
			return;
		}

		if(name.equals("console")) {
			String consoleName = getAttributeValue(method,attrs,"name");
			DDS_console c = new DDS_console(consoleName);

			_ddst.add(c);
			_currentConsole = c;

			return;
		}

		if(name.equals("prompt")) {
			String text = getAttributeValue(method,attrs,"text");
			DDS_console_prompt p = new DDS_console_prompt(text);

			if(null == _currentConsole) { abort(method,"NOCURRENTCONSOLE"); }
			_currentConsole.add(p);

			return;
		}

		if(name.equals("direct")) {
			String location = getAttributeValue(method,attrs,"location");
			String port = getAttributeValue(method,attrs,"port");
			int portValue = atoiGE0(method,port);
			DDS_console_direct cd = new DDS_console_direct(location,portValue);

			if(null == _currentConsole) { abort(method,"NOCURRENTCONSOLE"); }
			_currentConsole.add(cd);

			return;
		}

		if(name.equals("terminalserver")) {
			String location = getAttributeValue(method,attrs,"location");
			DDS_console_terminalserver ct = new DDS_console_terminalserver(location);

			if(null == _currentConsole) { abort(method,"NOCURRENTCONSOLE"); }
			_currentConsole.add(ct);

			return;
		}

		// If we get this far we better be inside a function definition.
		// Or maybe we're inside an if block.

		if(_aggregatorStack.empty()) {
			abort(method,"NOCURRENTBLOCK");
		}

		DDSOperation op = null;

		// TODO: I'd love to replace this grotesque chain of if blocks with some reflection.
		if(name.equals("bind")) {
			String varName = getAttributeValue(method,attrs,"name");
			String varSource = getAttributeValue(method,attrs,"source");
			String varPath = getAttributeValue(method,attrs,"path");

			if(!_ddst.variableHasBeenDeclared(varName)) {
				abort(method,"BINDUNDECLAREDVARIABLE(" + varName + ")");
			}

			op = new Operation_bind(varName,varSource,varPath);
			appendOperation(op);

			return;
		}

		if(name.equals("open")) {
			String consoleName = getAttributeValue(method,attrs,"name");

			if(!_ddst.consoleHasBeenDeclared(consoleName)) {
				abort(method,"OPENUNDECLAREDCONSOLE(" + consoleName + ")");
			}

			op = new Operation_open(consoleName);
			appendOperation(op);
			return;
		}

		if(name.equals("call")) {
			String funcName = getAttributeValue(method,attrs,"name");
			// TODO: Check if the function has been defined.
			// TODO: Maybe have to wait until we finish parning the whole doc.
			op = new Operation_call(funcName);
			appendOperation(op);
			return;
		}

		if(name.equals("sleep")) {
			String s = getAttributeValue(method,attrs,"millis");
			int millis = atoiGE0(method,s);
			op = new Operation_sleep(millis);
			appendOperation(op);
			return;
		}

		if(name.equals("fail")) {
			String message = getAttributeValue(method,attrs,"message");
			op = new Operation_fail(message);
			appendOperation(op);
			return;
		}

		if(name.equals("writeln")) {
			String text = getAttributeValue(method,attrs,"text");
			String getPromptWithin = getOptionalAttributeValue(attrs,"getPromptWithin");
			String failOnReceiptOf = getOptionalAttributeValue(attrs,"failOnReceiptOf");
			String expectResponse = getOptionalAttributeValue(attrs,"expectResponse");
			String delayBeforeRead = getOptionalAttributeValue(attrs,"delayBeforeRead");
			String logOnFail = getOptionalAttributeValue(attrs,"logOnFail");

			// getPromptWithin and delayBeforeRead are mutually exclusive
			// that's not enforced anywhere ?!

			int getPromptWithinVal = -1; // Loathe this!
			if(null != getPromptWithin) {
				getPromptWithinVal = atoiGE0(method,getPromptWithin);
			}

			int delayBeforeReadVal = -1; // Loathe this!
			if(null != delayBeforeRead) {
				delayBeforeReadVal = atoiGE0(method,delayBeforeRead);
			}

			op = new Operation_writeln(text,getPromptWithinVal,failOnReceiptOf,
				expectResponse,delayBeforeReadVal,logOnFail);

			appendOperation(op);
			return;
		}

		// <bindonfirstmatch variable="currentpacketsin" text="Processor" field="1"/>
		if(name.equals("bindonfirstmatch")) {
			String variable = getAttributeValue(method,attrs,"variable");
			String text = getAttributeValue(method,attrs,"text");
			String field = getAttributeValue(method,attrs,"field");

			throwOnUndeclaredVariable(method,variable);
			int fieldValue = atoiGE0(method,field);

			op = new Operation_bindonfirstmatch(variable,text,fieldValue);
			appendOperation(op);

			return;
		}

                // <store_V path="current" variable="currentpacketsin"/>
		if(name.equals("store_V")) {
			String path = getAttributeValue(method,attrs,"path");
			String variable = getAttributeValue(method,attrs,"variable");

			throwOnUndeclaredVariable(method,variable);

			op = new Operation_store_V(path,variable);
			appendOperation(op);

			return;
		}

                // <store_S path="isused" value="true"/>
		if(name.equals("store_S")) {
			String path = getAttributeValue(method,attrs,"path");
			String value = getAttributeValue(method,attrs,"value");

			op = new Operation_store_S(path,value);
			appendOperation(op);

			return;
		}

		// <sub_VV variable="deltapacketsin" left="currentpacketsin" right="previouspacketsin"/>
		if(name.equals("sub_VV")) {
			String variable = getAttributeValue(method,attrs,"variable");
			String left = getAttributeValue(method,attrs,"left");
			String right = getAttributeValue(method,attrs,"right");

			throwOnUndeclaredVariable(method,variable);
			throwOnUndeclaredVariable(method,left);
			throwOnUndeclaredVariable(method,right);

			op = new Operation_sub_VV(variable,left,right);
			appendOperation(op);

			return;
		}

		// <sub_VI variable="previouspacketsin" left="currentpacketsin" right="0"/>
		if(name.equals("sub_VI")) {
			String variable = getAttributeValue(method,attrs,"variable");
			String left = getAttributeValue(method,attrs,"left");
			String right = getAttributeValue(method,attrs,"right");

			throwOnUndeclaredVariable(method,variable);
			throwOnUndeclaredVariable(method,left);
			int rightValue = atoi(method,right);

			op = new Operation_sub_VI(variable,left,rightValue);
			appendOperation(op);

			return;
		}


		// TODO: I am brute-forcing the rest of these math operations in.
		// TODO: This would be a good time to push argument parsing into the
		// TODO: operation constructors.
		// TODO: But then how to retain parse context (like _ddst and Locator) ???

		if(name.equals("add_VV")) {
			String variable = getAttributeValue(method,attrs,"variable");
			String left = getAttributeValue(method,attrs,"left");
			String right = getAttributeValue(method,attrs,"right");

			throwOnUndeclaredVariable(method,variable);
			throwOnUndeclaredVariable(method,left);
			throwOnUndeclaredVariable(method,right);

			op = new Operation_add_VV(variable,left,right);
			appendOperation(op);

			return;
		}

		if(name.equals("add_VI")) {
			String variable = getAttributeValue(method,attrs,"variable");
			String left = getAttributeValue(method,attrs,"left");
			String right = getAttributeValue(method,attrs,"right");

			throwOnUndeclaredVariable(method,variable);
			throwOnUndeclaredVariable(method,left);
			int rightValue = atoi(method,right);

			op = new Operation_add_VI(variable,left,rightValue);
			appendOperation(op);

			return;
		}

		if(name.equals("mul_VV")) {
			String variable = getAttributeValue(method,attrs,"variable");
			String left = getAttributeValue(method,attrs,"left");
			String right = getAttributeValue(method,attrs,"right");

			throwOnUndeclaredVariable(method,variable);
			throwOnUndeclaredVariable(method,left);
			throwOnUndeclaredVariable(method,right);

			op = new Operation_mul_VV(variable,left,right);
			appendOperation(op);

			return;
		}

		if(name.equals("mul_VI")) {
			String variable = getAttributeValue(method,attrs,"variable");
			String left = getAttributeValue(method,attrs,"left");
			String right = getAttributeValue(method,attrs,"right");

			throwOnUndeclaredVariable(method,variable);
			throwOnUndeclaredVariable(method,left);
			int rightValue = atoi(method,right);

			op = new Operation_mul_VI(variable,left,rightValue);
			appendOperation(op);

			return;
		}

		if(name.equals("div_VV")) {
			String variable = getAttributeValue(method,attrs,"variable");
			String left = getAttributeValue(method,attrs,"left");
			String right = getAttributeValue(method,attrs,"right");

			throwOnUndeclaredVariable(method,variable);
			throwOnUndeclaredVariable(method,left);
			throwOnUndeclaredVariable(method,right);

			op = new Operation_div_VV(variable,left,right);
			appendOperation(op);

			return;
		}

		if(name.equals("div_VI")) {
			String variable = getAttributeValue(method,attrs,"variable");
			String left = getAttributeValue(method,attrs,"left");
			String right = getAttributeValue(method,attrs,"right");

			throwOnUndeclaredVariable(method,variable);
			throwOnUndeclaredVariable(method,left);
			int rightValue = atoi(method,right);

			op = new Operation_div_VI(variable,left,rightValue);
			appendOperation(op);

			return;
		}

		if(name.equals("mod_VV")) {
			String variable = getAttributeValue(method,attrs,"variable");
			String left = getAttributeValue(method,attrs,"left");
			String right = getAttributeValue(method,attrs,"right");

			throwOnUndeclaredVariable(method,variable);
			throwOnUndeclaredVariable(method,left);
			throwOnUndeclaredVariable(method,right);

			op = new Operation_mod_VV(variable,left,right);
			appendOperation(op);

			return;
		}

		if(name.equals("mod_VI")) {
			String variable = getAttributeValue(method,attrs,"variable");
			String left = getAttributeValue(method,attrs,"left");
			String right = getAttributeValue(method,attrs,"right");

			throwOnUndeclaredVariable(method,variable);
			throwOnUndeclaredVariable(method,left);
			int rightValue = atoi(method,right);

			op = new Operation_mod_VI(variable,left,rightValue);
			appendOperation(op);

			return;
		}


		if(name.equals("ifnull")) {
			String variable = getAttributeValue(method,attrs,"variable");
			throwOnUndeclaredVariable(method,variable);
			op = new Operation_ifnull(variable);
			startBlock(op);
			return;
		}

		if(name.equals("ifnotnull")) {
			String variable = getAttributeValue(method,attrs,"variable");
			throwOnUndeclaredVariable(method,variable);
			op = new Operation_ifnotnull(variable);
			startBlock(op);
			return;
		}

		if(name.equals("ifresponsecontains")) {
			String text = getAttributeValue(method,attrs,"text");
			op = new Operation_ifresponsecontains(text);
			startBlock(op);
			return;
		}

		// <ifgt_VI left="deltapacketsin" right="5">
		if(name.equals("ifgt_VI")) {
			String left = getAttributeValue(method,attrs,"left");
			String right = getAttributeValue(method,attrs,"right");

			throwOnUndeclaredVariable(method,left);

			int rightValue = atoi(method,right);

			op = new Operation_ifgt_VI(left,rightValue);
			startBlock(op);
			return;
		}

		// TODO: again with the brute-force operator parsing.
		// TODO: it sure would be nice to push some of this common
		// TODO: parse logic into the Operation_* constructor.

		if(name.equals("ifeq_VV")) {
			String left = getAttributeValue(method,attrs,"left");
			String right = getAttributeValue(method,attrs,"right");

			throwOnUndeclaredVariable(method,left);
			throwOnUndeclaredVariable(method,right);

			op = new Operation_ifeq_VV(left,right);
			startBlock(op);
			return;
		}

		if(name.equals("ifne_VV")) {
			String left = getAttributeValue(method,attrs,"left");
			String right = getAttributeValue(method,attrs,"right");

			throwOnUndeclaredVariable(method,left);
			throwOnUndeclaredVariable(method,right);

			op = new Operation_ifne_VV(left,right);
			startBlock(op);
			return;
		}

		abort(method,"UNKNOWN_ELEMENT(" + name + ")");

		//log.ret(method);
	}

	// FIXME: handle EmptyStackException !!!
	protected void appendOperation(DDSOperation op) {
		// find the current block (function or if-statement)
		OperationAggregator currentBlock = (OperationAggregator)_aggregatorStack.peek();

		// append this statement to the end of the current block
		currentBlock.append(op);
	}

	protected void startBlock(DDSOperation op) {
		// append this new statement to the end of the currentblock
		appendOperation(op);

		// start appending all following statements to this new block
		_aggregatorStack.push(op);
	}

	protected String getOptionalAttributeValue(Attributes attrs,String attrName) {
		String value = attrs.getValue(attrName);
		return(value);
	}

	protected String getAttributeValue(String method,Attributes attrs,String attrName)
			throws SAXException {

		String value = attrs.getValue(attrName);
		if(null == value) {
			abort(method,"MISSINGREQUIREDATTRIBUTE(" + attrName + ")");
		}
		return(value);
	}

	protected String location() {
		if(null == _locator) {
			return("[?,?]");
		}
		else {
			return("[" + _locator.getLineNumber() + "," + _locator.getColumnNumber() + "]");
		}
	}

	protected void throwOnUndeclaredVariable(String method,String variable) throws SAXException {
		if(!_ddst.variableHasBeenDeclared(variable)) {
			abort(method,"UNDECLAREDVARIABLE(" + variable + ")");
		}
	}

	protected void abort(String method,String message) throws SAXException {
		throw(new SAXException(method + ":" + location() + ":" + message));
	}

	protected void abort(String method,Exception e) throws SAXException {
		throw(new SAXException(method + ":" + location() + ":" + e.toString()));
	}

	protected int atoi(String method,String s) throws SAXException {
		int i;
		try {
			i = Integer.parseInt(s);
			return(i);
		}
		catch(NumberFormatException nfe) {
			abort(method,"NUMBERFORMATEXCEPTION(" + s + "):" + nfe.toString());
			return(0); // unreachable
		}
	}

	protected int atoiGE0(String method,String s) throws SAXException {
		int val = atoi(method,s);
		if(val < 0) { abort(method,"INVALIDVALUE(" + val + ")"); }
		return(val);
	}

	public void endElement(String uri,String name,String qName) throws SAXException {
		String method = "endElement(";
		method += uri + "," + name + "," + qName + ")";
		//log.invoke(method);
		if(traceon("endElement")) { printLocation(method); }

		// FIXME: handle EmptyStackException !!!
		if(name.equals("function")) {
			_aggregatorStack.pop();
			// stack better be empty now
		}
		else if(name.equals("ifnull")) {
			_aggregatorStack.pop();
		}
		else if(name.equals("ifnotnull")) {
			_aggregatorStack.pop();
		}

		// TODO: if(currentOperation implements OperationAggregator)
		else if(name.equals("ifresponsecontains")) {
			_aggregatorStack.pop();
		}

		else if(name.equals("console")) {
			if(_currentConsole.accessorIsNull()) {
				abort(method,"MISSINGCONSOLEACCESSOR");
			}
			_currentConsole = null;
		}

		//log.ret(method);
	}

	public void characters(char ch[],int start,int length) {
		String method = "characters(";
		method += ch + "," + start + "," + length + ")";
		//log.invoke(method);
		if(traceon("characters")) { printLocation(method); }
		//log.ret(method);
	}

	public void ignorableWhitespace(char[] ch,int start,int length) {
		String method = "ignorableWhitespace(";
		method += ch + "," + start + "," + length + ")";
		//log.invoke(method);
		if(traceon("ignorableWhitespace")) { printLocation(method); }
		//log.ret(method);
	}

	public void processingInstruction(Locator locator) {
		String method = "processingInstruction(";
		method += locator + ")";
		//log.invoke(method);
		if(traceon("processingInstruction")) { printLocation(method); }
		//log.ret(method);
	}

	public void skippedEntity(String name) {
		String method = "skippedEntity(";
		method += name + ")";
		log.invoke(method);
		printLocation(method);
		log.ret(method);
	}


	//http://www.ibiblio.org/xml/books/xmljava/chapters/ch06s12.html

	protected Locator _locator;

	public void setDocumentLocator(Locator locator) {
		String method = "setDocumentLocator(" + locator + ")";
		//log.invoke(method);

		_locator = locator;
		DDSElement.setLocator(_locator);

		//log.ret(method);
	}

	protected void printLocation(String tag,String msg,Locator l) {
		if(null == l) {
			log.info(tag + ":NULLLOCATOR:" + msg);
			return;
		}

		int col = l.getColumnNumber();
		int line = l.getLineNumber();
		//String publicId = l.getPublicId();
		//String systemId = l.getSystemId();

		String s = tag + ":";
		s += "(" + line + "," + col + ")";
		//s += ":publicId=" + publicId;
		//s += ":systemId=" + systemId;
		s += ":";
		s += msg;

		log.info(s);
	}

	protected void printLocation(String msg) {
		printLocation("LOCATOR",msg,_locator);
	}

	protected Locator saxpe2locator(SAXParseException saxpe) {
		org.xml.sax.helpers.LocatorImpl l =
			new org.xml.sax.helpers.LocatorImpl();
		l.setColumnNumber(saxpe.getColumnNumber());
		l.setLineNumber(saxpe.getLineNumber());
		l.setPublicId(saxpe.getPublicId());
		l.setSystemId(saxpe.getSystemId());
		return(l);
	}

	/*
	protected void printLocation(SAXParseException saxpe) {
		Locator l = saxpe2locator(saxpe);
		printLocation(saxpe.getMessage(),l);
	}
	*/

	protected void printLocation(String tag,SAXParseException saxpe) {
		Locator l = saxpe2locator(saxpe);
		printLocation(tag,saxpe.getMessage(),l);
	}

	protected int _n_error;
	protected int _n_fatalError;
	protected int _n_warning;
	protected int _n_suppressed_error;

	protected static final boolean _suppressXmlspyDisagreement = true;

	protected static final boolean _suppressAllXMLErrorMessages = true;

	public void error(SAXParseException saxpe) {
		++_n_error;

		if(_suppressXmlspyDisagreement) {
			// FIXME: Fix the error.  Don't suppress the error message.
			if(saxpe.getMessage().startsWith("Identity Constraint error:")) {
				++_n_suppressed_error;
				return;
			}
		}

		if(!_suppressAllXMLErrorMessages) {
			printLocation("ERROR",saxpe);
		}
	}

	public void fatalError(SAXParseException saxpe) {
		++_n_fatalError;
		printLocation("FATALERROR",saxpe);
	}

	public void warning(SAXParseException saxpe) {
		++_n_warning;
		if(!_suppressAllXMLErrorMessages) {
			printLocation("WARNING",saxpe);
		}
	}

	public int countError() {
		return(_n_error);
	}

	public int countFatalError() {
		return(_n_fatalError);
	}

	public int countWarning() {
		return(_n_warning);
	}

	public int countSuppressedError() {
		return(_n_suppressed_error);
	}

	protected boolean traceon(String tag) {
		//if(tag.equals("startElement")) { return(true); }
		return(false);
	}
}
