package com.lumenare.dif;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import java.util.Iterator;
import java.util.Enumeration;
import java.util.StringTokenizer;

import com.lumenare.datastore.TransientDataModel;
import com.lumenare.datastore.common.DeviceRole;
import com.lumenare.datastore.device.ExtendableLmDeviceVo;
import com.lumenare.datastore.device.LmDeviceVo;
import com.lumenare.datastore.devicetype.LmDeviceTypeVo;
import com.lumenare.datastore.devicetype.ExtendableLmDeviceTypeVo;
import com.lumenare.datastore.enterprise.LmEnterpriseVo;
import com.lumenare.datastore.enterprise.ExtendableLmEnterpriseVo;
import com.lumenare.datastore.lab.ExtendableLmLabVo;
import com.lumenare.datastore.lab.LmLabVo;

import com.lumenare.common.domain.dis.DISParser;
import com.lumenare.common.domain.dts.DTSParser;
import com.lumenare.common.domain.dis.DisBuilderException;
import com.lumenare.common.domain.dts.DtsBuilderException;

import com.lumenare.datastore.validator.SetValidator;
import com.lumenare.datastore.validator.TypeValidator;
import com.lumenare.datastore.validator.TypeValidatee;
import com.lumenare.datastore.validator.InventoryValidator;
import com.lumenare.datastore.validator.InventoryValidatee;
import com.lumenare.datastore.validator.ValidationException;

import com.lumenare.dif.language.AccessorUtility;
import com.lumenare.dif.language.DDSExecutionException;
import com.lumenare.dif.language.DDSTree;
import com.lumenare.dif.language.DDSTreeFactory;
import com.lumenare.dif.language.InventoryAccessorFactory;
import com.lumenare.dif.language.MutableSystemContext;
import com.lumenare.dif.language.SystemContext;
import com.lumenare.dif.manager.DeviceDriver;
import com.lumenare.dif.manager.DeviceDriverFactory;
import com.lumenare.dif.manager.DeviceRoleException;
import com.lumenare.dif.manager.EthernetHub;
import com.lumenare.dif.manager.ManagedDevice;
import com.lumenare.dif.manager.MatrixSwitch;
import com.lumenare.dif.manager.MissingFunctionException;
import com.lumenare.dif.manager.PowerController;
import com.lumenare.dif.manager.TerminalServer;
import com.lumenare.dif.manager.TftpServer;

import com.lumenare.common.domain.attribute.Attribute;
import com.lumenare.common.domain.attribute.AttributeCollection;
import com.lumenare.common.domain.attribute.AttributeFactory;
import com.lumenare.common.domain.attribute.StringAttribute;
import com.lumenare.common.domain.attribute.adapter.AttributeCollectionImpl;
//import com.lumenare.common.domain.attribute.adapter.AttributeUtil;

import java.net.MalformedURLException;

import com.avulet.element.ConsoleLocation;

public class DifApplication {
	public static String DEFAULT_ENTERPRISE = "__DEFAULT_ENTERPRISE__";
	public static String DEFAULT_LAB = "__DEFAULT_LAB__";

	public static void main(String args[]) {
		com.avulet.system.Config.get();
		try {
			DifApplication da = new DifApplication();
		}
		catch(Exception e) {
			System.out.println("unexpected exception in main");
			e.printStackTrace();
			System.exit(1);
		}
		catch(Error e) {
			System.out.println("unexpected error in main");
			e.printStackTrace();
			System.exit(1);
		}
		com.avulet.consoleserver.util.ThreadTree.dumpThreadTree();
		//System.exit(0);
	}


	protected boolean _quit = false;

	public void quit() {
		_quit = true;
	}

	protected final java.io.BufferedWriter _writer =
		new java.io.BufferedWriter(new java.io.OutputStreamWriter(System.out));

	public void write(String s) {
		try {
			_writer.write(s);
		}
		catch(IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void _newLine() {
		try {
			_writer.newLine();
			_writer.flush();
		}
		catch(IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void _flush() {
		try {
			_writer.flush();
		}
		catch(IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void writeln(String s) {
		try {
			_writer.write(s);
			_writer.newLine();
		}
		catch(IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void prompt() {
		write("dif# ");
		_flush();
	}

	ArrayList tokenize(String line) {
		ArrayList tokens = new ArrayList();
		StringTokenizer st = new StringTokenizer(line," ");
		while(st.hasMoreTokens()) {
			tokens.add(st.nextToken());
		}
		return(tokens);
	}

	public void sleep(int millis) {
		try { Thread.sleep(millis); }
		catch(Exception e) { }
	}

	protected DeviceDriver _driver;

	public void setDriver(DeviceDriver driver) {
		_driver = driver;
	}

	public DeviceDriver getDriver() {
		return(_driver);
	}

	public void writeDammit(String s) {
		System.out.println("");
		System.out.println(s);
		System.out.println("");
		System.out.flush();
		sleep(1000);
	}

	public void destroyDriver() {
		String s = "destroyDriver()";
		s += " _driver=" + _driver;
		//writeDammit(s);

		if(null == _driver) { return; }
		_driver.close();
		_driver = null;

		sleep(100);
		System.gc();
		sleep(100);
	}

	public DifApplication() throws Exception {
		new KickstartCommands();

		String filename = "logging.xml";
		org.apache.log4j.xml.DOMConfigurator.configureAndWatch(filename);

		java.io.BufferedReader reader =
			new java.io.BufferedReader(new java.io.InputStreamReader(System.in));

		while(!_quit) {
			prompt();

			String line = reader.readLine();
			if(null == line) { break; }

			ArrayList argv = tokenize(line);
			if(0 == argv.size()) { continue; }

			String commandName = (String)argv.get(0);
			Command command = Command.locate(commandName);

			if(null == command) {
				writeln("Unknown command: '" + commandName + "'");
				continue;
			}

			Environment env = new Environment(argv,this);
			try {
				command.execute(env);
				// if throw on create then destroy tcc
			}
			catch(DifApplicationException dae) {
				writeln(dae.getMessage());
				writeln(command.formatLongHelp());
			}
			finally { }
		}

		destroyDriver();
	}

	public static LmDeviceVo fetchDis(String filename)
			throws DisBuilderException,FileNotFoundException {

		InputSource is = new InputSource(new FileInputStream(filename));
		DISParser parser = new DISParser(is);

		LmDeviceVo disVo = parser.getDisVo();
		disVo.setEnterpriseName(DEFAULT_ENTERPRISE);
		disVo.setLabName(DEFAULT_LAB);
		disVo.setDeviceTypeVersionId(-1);

		return(disVo);
	}

	public static AttributeCollection doReadArgs(String filename)
			throws IOException {

		Properties props = new MyProperties(filename);
		AttributeCollection ac = new AttributeCollectionImpl();

		Enumeration e = props.propertyNames();
		while(e.hasMoreElements()) {
			String key = (String)e.nextElement();
			String value = props.getProperty(key);
			addString(ac,key,value);
		}

		return(ac);
	}

	public static void addString(AttributeCollection ac,String name,String value) {
		StringAttribute sa = AttributeFactory.createString(name,value);
		ac.addAttribute(sa);
	}

	public static SystemContext initSystemContext(String scfile)
			throws IOException,UnknownHostException,
			MissingPropertyException,BadValueException,
			DifApplicationException {

		return(SystemContextFactory.create(scfile));
	}

	public void dumpAttributeCollection(AttributeCollection ac) {
		Map map = ac.getAttributes();
		Collection c = map.values();
		for(Iterator i = c.iterator();i.hasNext();) {
			Attribute a = (Attribute)i.next();
			String s = a.getName() + "=" + a.getValue();
			writeln(s);
		}
	}
}

class SystemContextFactory {
	public static SystemContext create(String scfile)
			throws IOException,UnknownHostException,
			MissingPropertyException,BadValueException,
			DifApplicationException {

		MutableSystemContext msc = new MutableSystemContext();
		MyProperties props = new MyProperties(scfile);

		fetchTftpServerStuff(msc,props);

		gropeForTerminalServers(msc,props);

		return(msc);
	}

	public static void fetchTftpServerStuff(MutableSystemContext msc,MyProperties props)
			throws MissingPropertyException {

		String s = props.getRequiredProperty("systemTftpServerMgmtIfIpAddr");
		msc.systemTftpServerMgmtIfIpAddr(s);
	}

	public static String[] split(String in,String delim) {
		StringTokenizer st = new StringTokenizer(in,delim);
		int nTokens = st.countTokens();
		String[] token = new String[nTokens];
		for(int i=0;i<nTokens;i++) {
			token[i] = st.nextToken();
		}
		return(token);
	}

	public static void countTokens(String[] token,int desiredLength,
			String msg)
			throws DifApplicationException {

		//_env.writeln("name=" + name);
		//_env.writeln("value=" + value);
		//_env.writeln("token.length=" + token.length);
		//_env.writeln("desiredLength=" + desiredLength);

		//System.out.println("msg=[" + msg + "]");

		//int nTokens = token.length;
		//for(int i=0;i<nTokens;i++) {
			//System.out.println("token[" + i + "]=" + token[i]);
		//}

		if(token.length != desiredLength) {
			throw(new DifApplicationException("parse error: " + msg));
		}
	}

	// terminalserver.con=172.16.8.1:2001
	// softport.con=2001

	public static void gropeForTerminalServers(MutableSystemContext msc,MyProperties props)
			throws DifApplicationException,
			BadValueException,
			UnknownHostException {

		Enumeration e = props.propertyNames();
		while(e.hasMoreElements()) {
			String propertyName = (String)e.nextElement();

			if(propertyName.startsWith("terminalserver.")) {
				String consoleName = doTerminalServer(propertyName,props,msc);
				//doSoftport(consoleName,props,msc);
			}
		}
	}

	protected static String doTerminalServer(String propertyName,
			MyProperties props,MutableSystemContext msc)
			throws DifApplicationException,
			UnknownHostException {

		String value = props.getProperty(propertyName);

		String msg = "property '" + propertyName + "=" + value + "' ";
		msg += "must take the form ";
		msg += "terminalserver.<consolelocation>=ipaddress:port";

		// parse lhs to get console name
		String[] token = split(propertyName,".");
		countTokens(token,2,msg);
		String consoleName = token[1];

		// parse rhs to get ip:port
		token = split(value,":");
		countTokens(token,2,msg);

		String address = token[0];

		try {
			int port = Util.atoi(token[1]);
			ConsoleLocation cl = Util.constructConsoleLocation(address,port);
			msc.addTerminalServerConsoleLocation(consoleName,cl);
		}
		catch(BadValueException bve) {
			String s = "parse error: " + msg;
			s += " CANTPARSEINTEGER(" + bve + ")";
			throw(new DifApplicationException(s));
		}

		return(consoleName);
	}

	protected void doSoftportParseError(String consoleName,String comment)
				throws DifApplicationException {

	}

	// TODO: this is really interfacelocation, not consolename
	protected static void doSoftport(String consoleName,
			MyProperties props,MutableSystemContext msc)
			throws DifApplicationException {

		String msg = "must include a property of the form ";
		msg += "softport.<consolelocation>=port ";
		msg += "for consolelocation='" + consoleName + "'";

		String propertyName = "softport." + consoleName;
		String value = props.getProperty(propertyName);

		if(null == value) {
			String s = "parse error: " + msg;
			s += " NOSUCHPROPERTY(" + propertyName + ")";
			throw(new DifApplicationException(s));
		}

		try {
			int port = Util.atoi(value);
			msc.addConsoleSoftport(consoleName,port);
		}
		catch(BadValueException bve) {
			String s = "parse error: " + msg;
			s += " CANTPARSEINTEGER(" + bve + ")";
			throw(new DifApplicationException(s));
		}
	}
}


/*
		}
		else if(command.equalsIgnoreCase(DTS_PRINT)) {
			if(inputlist.size()>=2) {
				_printDTS((String)inputlist.get(0),(String)inputlist.get(1));
			}
			else {
				_printMissingArgumentError(command);
			}

		}
		else if(command.equalsIgnoreCase(DTS_LIST)) {
			boolean sub = false;
			Vector manufacturer = new Vector();
			for(int i=0; i<inputlist.size(); i++) {
				String s = (String)inputlist.get(i);
				if(s.equalsIgnoreCase("-s")) {
					sub = true;
				}
				else if(s.equalsIgnoreCase("-m")) {
					i++;
					if(i+1>inputlist.size()) {
						_printMissingArgumentError(command);
						break;
					}
					else if(!manufacturer.contains((String)inputlist.get(i))) {
						manufacturer.addElement((String)inputlist.get(i));
					}
				}
				else {
					_printUnknownArgumentError(command,s);
					break;
				}
			}

			_listDTS(sub,manufacturer);
		}

		else if(command.equalsIgnoreCase(DIS_LIST)) {
			boolean sub = false;
			Vector manufacturer = new Vector();
			Vector model = new Vector();
			for(int i=0; i<inputlist.size(); i++) {
				String s = (String)inputlist.get(i);
				if(s.equalsIgnoreCase("-s")) {
					sub = true;

				}
				else if(s.equalsIgnoreCase("-m")) {
					i++;
					if(i+1>inputlist.size()) {
						_printMissingArgumentError(command);
						break;
					}
					else if(!manufacturer.contains((String)inputlist.get(i))) {
						manufacturer.addElement((String)inputlist.get(i));
					}
				}
				else if(s.equalsIgnoreCase("-n")) {
					i++;
					if(i+1>inputlist.size()) {
						_printMissingArgumentError(command);
						break;
					}
					else if(!model.contains((String)inputlist.get(i))) {
						model.addElement((String)inputlist.get(i));
					}
				}
				else {
					_printUnknownArgumentError(command,s);
					break;
				}
			}
			_listDIS(sub,manufacturer,model);
		}
	}

	public void _addupdateDTS(String xmlfilepath,boolean update)
			throws ValidationException,XMLServiceException {

		try {
			// Parse dts xml file
			DTSParser parser = new DTSParser(_readXML(xmlfilepath));
			LmDeviceTypeVo dtsVo = parser.getDtsVo();
			dtsVo.setParentEnterpriseName(DEFAULT_ENTERPRISE);
			if(dtsVo.getInternalDeviceTypes()!=null && dtsVo.getInternalDeviceTypes().size()>0) {
				Iterator it = dtsVo.getInternalDeviceTypes().iterator();
				while(it.hasNext()) {
					((LmDeviceTypeVo)it.next()).setParentEnterpriseName(DEFAULT_ENTERPRISE);
				}
			}

			TypeValidator validator = new TypeValidator(dtsVo,(TypeValidatee)s_datamodel);
			validator.validate();

			if(validator.isValid()) {
				s_datamodel.addDeviceType(dtsVo);
			}
			else {
				// SHC - Fix me!!!
				throw new XMLServiceException("Invalid DTS: "+validator.toInvalid());
			}

		}
		catch(DtsBuilderException e) {
			throw new XMLServiceException("DTS syntax error: " + e.getMessage());
		}
	}

	public void _printDTS(String manufacturer,String model) {
		ExtendableLmDeviceTypeVo dt = s_datamodel.getDeviceType(manufacturer,model,DEFAULT_ENTERPRISE);

		if(dt!=null) {
			writeln(dt.toString());
		}
		else {
			writeln("Invalid DTS: manufacturer="+manufacturer+ " model="+model);
		}
	}

	public void _listDTS(boolean sub,List manufacturer) {
		Vector list = new Vector();
		List listDts = s_datamodel.getAllRootDeviceTypes(DEFAULT_ENTERPRISE);
		if(listDts!=null) {
			Iterator it = listDts.iterator();
			while(it.hasNext()) {
				ExtendableLmDeviceTypeVo dt = (ExtendableLmDeviceTypeVo)it.next();
				_listDTSTree(sub,manufacturer,dt,null,list);
			}
		}

		if(list.size()>0) {
			Iterator it = list.iterator();
			while(it.hasNext()) {
				writeln((String)it.next());
			}
		}
		else {
			writeln("-EMPTY -");
		}
	}

	public void _listDTSTree(boolean sub,List manufacturer,ExtendableLmDeviceTypeVo dt,String parent,Vector list)
	{
		String root = dt.getManufacturer()+":"+dt.getModel();
		if(parent!=null && !parent.trim().equals("")) {
			root = parent+"; "+root;
		}

		if(manufacturer==null || manufacturer.isEmpty() || manufacturer.contains(dt.getManufacturer())) {
			list.addElement(root);
		}

		// sub-devices
		if(sub && dt.getInternalDeviceTypes()!=null && dt.getInternalDeviceTypes().size()>0) {
			Iterator it = dt.getInternalDeviceTypes().iterator();
			while(it.hasNext()) {
				_listDTSTree(sub,manufacturer,(ExtendableLmDeviceTypeVo)it.next(),root,list);
			}
		}
	}

	public void _printDIS(String assetTag) {
		ExtendableLmDeviceVo device = s_datamodel.getDeviceInventory(assetTag,DEFAULT_ENTERPRISE,DEFAULT_LAB);
		if(device!=null) {
			writeln(device.toString());
		}
		else {
			writeln("Invalid DIS: "+assetTag);
		}
	}

	public void _listDIS(boolean sub,List manufacturer,List model) {
		Vector list = new Vector();
		List listDev = s_datamodel.getAllDeviceInventory(DEFAULT_ENTERPRISE,DEFAULT_LAB);

		if(listDev!=null && listDev.size()>0) {
			Iterator it = listDev.iterator();
			while(it.hasNext()) {
				ExtendableLmDeviceVo device = (ExtendableLmDeviceVo)it.next();
				_listDISTree(sub,manufacturer,model,device,null,list);
			}
		}

		if(list.size()>0) {
			Iterator it = list.iterator();
			while(it.hasNext()) {
				writeln((String)it.next());
			}
		}
		else {
			writeln("-EMPTY -");
		}
	}

	public void _listDISTree(boolean sub,List manufacturer,List model,ExtendableLmDeviceVo device,
				String parent,Vector list) {

		String root = device.getAssetTag()+"("+device.getManufacturer()+":"+device.getModel()+")";
		if(parent!=null && !parent.trim().equals("")) {
			root = parent+"; "+root;
		}

		// root device
		if(manufacturer==null || manufacturer.isEmpty() || manufacturer.contains(device.getManufacturer())) {
			if(model==null || model.isEmpty() || model.contains(device.getModel())) {
				list.addElement(root);
			}
		}

		// sub-devices
		if(sub && device.getSubDevices()!=null && device.getSubDevices().size()>0) {
			Enumeration enum = device.getSubDevices().elements();
			while(enum.hasMoreElements()) {
				_listDISTree(sub,manufacturer,model,(ExtendableLmDeviceVo)enum.nextElement(),root,list);
			}
		}
	}
*/







abstract class Command {
	protected static HashMap _allCommands = new HashMap();

	protected String _name = "UNKNOWN";
	protected String _shortHelp = "";
	protected String _longHelp = "";

	public Command() {
		_name = calculateName();
		_shortHelp = _name;
		_longHelp = "";
		String lcname = _name.toLowerCase();
		_allCommands.put(lcname,this);
	}

	protected String calculateName() {
		String classname = Util.basename4class(this.getClass());
		int i = "Command_".length();
		String commandname = classname.substring(i);
		return(commandname);
	}

/*
FIXME: Use Util.basename4class
	protected String calculateName() {
		String className = this.getClass().getName();
		String packageName = this.getClass().getPackage().getName();
		int i = packageName.length();
		i += 1 + "Command_".length();
		String baseName = className.substring(i);
		return(baseName);
	}
*/

	public static Command locate(String name) {
		String lcname = name.toLowerCase();
		return((Command)_allCommands.get(lcname));
	}

	public String toString() {
		String s = "Command(" + _name + ")";
		return(s);
	}

	public static Command[] allCommands() {
		Object[] o = _allCommands.values().toArray();
		Command[] c = new Command[_allCommands.size()];

		for(int i=0;i<_allCommands.size();i++) {
			c[i] = (Command)o[i];
		}
		return(c);
	}

	public String formatShortHelp() {
		String s = "  " + _name;
		int i = 16 - _name.length();
		for(;i>0;i--) { s += " "; }
		s +=  _shortHelp;
		return(s);
	}

	public void printShortHelp(Environment env) {
		env.writeln(formatShortHelp());
	}

	public String formatLongHelp() {
		return("Usage: " + _name + " " + _longHelp);
	}

	public void printLongHelp(Environment env) {
		env.writeln(formatLongHelp());
	}

	public void execute(Environment env) throws DifApplicationException {
		try {
			doExecute(env);
		}
		catch(BadArgumentCountException bace) {
			String s = _name + ": bad argument count";
			String s1 = bace.info();
			if(null != s1) { s += " " + s1; }
			env.writeln(s);
			printLongHelp(env);
		}
		catch(NoDriverException nde) {
			env.writeln(_name + ": no driver");
			printLongHelp(env);
		}
	}

	public abstract void doExecute(Environment env) throws DifApplicationException;

	public void countArgs(int expected,Environment env) throws BadArgumentCountException {
		int actual = env.argc();
		if(expected != actual) {
			throw(new BadArgumentCountException(expected,actual));
		}
	}
}

class KickstartCommands {
	public KickstartCommands() {
		new Command_call();
		new Command_create();
		new Command_describe();
		new Command_destroy();
		new Command_help();
		new Command_lsfunctions();
		new Command_quit();
		new Command_validate();

		new Command_c();
		new Command_debug();
		new Command_v();

		new Command_lsvars();
		new Command_questionmark();
		new Command_parse();
		new Command_p();

		new Command_f();
	}
}

class Environment {
	protected ArrayList _argv;
	protected DifApplication _da;

	// Debug constructor
	public Environment(ArrayList argv,Environment e) {
		this(argv,e._da);
	}

	public Environment(ArrayList argv,DifApplication da) {
		_argv = argv;
		_da = da;
	}

	public int argc() {
		return(_argv.size());
	}

	public String argv(int index) {
		return((String)_argv.get(index));
	}

	public void writeln(String s) {
		_da.writeln(s);
	}

	public void quit() {
		_da.quit();
	}

	protected DeviceDriver _driver;

	public void setDriver(DeviceDriver driver) {
		_da.setDriver(driver);
	}

	public DeviceDriver getDriver() {
		return(_da.getDriver());
	}

	public void destroyDriver() throws DifApplicationException {
		_da.destroyDriver();
	}

	public void dumpAttributeCollection(AttributeCollection ac) {
		_da.dumpAttributeCollection(ac);
	}
}

class Command_quit extends Command {
	public Command_quit() {
		super();
		_shortHelp = "End the program";
		_longHelp = "";
	}

	public void doExecute(Environment env) {
		env.quit();
	}
}

class Command_help extends Command {
	public Command_help() {
		super();
		_shortHelp = "Help";
		_longHelp = "[command]";
	}

	public void doExecute(Environment env) {
		if(env.argc() == 2) {
			String commandName = env.argv(1);
			Command c = Command.locate(commandName);
			if(null == c) {
				env.writeln("Unknown command: '" + commandName + "'");
				return;
			}
			c.printLongHelp(env);
		}
		else {
			Command[] c = Command.allCommands();
			for(int i=0;i<c.length;i++) {
				c[i].printShortHelp(env);
			}
		}
	}
}

class Command_describe extends Command {
	public Command_describe() {
		super();
		_shortHelp = "Write a description of the currently loaded driver to the log";
		_longHelp = "";
	}

	public void doExecute(Environment env) throws DifApplicationException {
		DeviceDriver driver = env.getDriver();
		if(null == driver) { throw(new NoDriverException()); }
		driver.describe();
		env.writeln("A description of the currently loaded driver has been written to the log.");
	}
}

/*
class LinkSide {
	String _assettag = "";
	String _supertype = "";
	String _type = "";
	String _location= "";

	public void setassettag(String s) { _assettag = s; }
	public void setsupertype(String s) { _supertype = s; }
	public void settype(String s) { _type = s; }
	public void setlocation(String s) { _location = s; }

	public String toString() {
		String s = "LinkSide(";
		s += _assettag + ",";
		s += _supertype + ",";
		s += _type + ",";
		s += _location + ")";
		return(s);
	}
}

class Link {
	public static final String LINK = "link";
	public static final String LEFT = "left";
	public static final String RIGHT = "right";
	public static final String ASSETTAG = "assettag";
	public static final String SUPERTYPE = "supertype";
	public static final String TYPE = "type";
	public static final String LOCATION = "location";

	protected static HashMap _allLinks = new HashMap();

	protected LinkSide left = new LinkSide();
	protected LinkSide right = new LinkSide();

	public LinkSide right() { return(right); }
	public LinkSide left() { return(left); }

	public Link() {
	}

	public static Link locate(String key) {
		return((Link)_allLinks.get(key));
	}

	public static void clear() {
		_allLinks = new HashMap();
	}

	public static String[] tokenize(String s) throws DifApplicationException {
		String[] token = new String[4];
		int i = 0;
		StringTokenizer st = new StringTokenizer(s,".");
		while(st.hasMoreTokens()) {
			if(i >= 4) {
				throw(new DifApplicationException("parse error: '" + s + "'"));
			}
			token[i++] = st.nextToken();
		}
		return(token);
	}

	public static void parseError(String propkey,String propvalue) throws DifApplicationException {
		String s = "parse error: ";
		s += "'" + propkey + "'";
		s += ",";
		s += "'" + propvalue + "'";
		throw(new DifApplicationException(s));
	}

	public static void add(String propkey,String propvalue) throws DifApplicationException {
		String[] token = tokenize(propkey);

		if(!token[0].equals(LINK)) { parseError(propkey,propvalue); }

		String key = token[1];
		Link link = locate(key);
		if(null == link) {
			link = new Link();
		}

		LinkSide side;
		if(token[2].equals(RIGHT)) {
			side = link.right();
		}
		else if(token[2].equals(LEFT)) {
			side = link.left();
		}
		else { parseError(propkey,propvalue); return; }

		String s = token[3];
		if(s.equals(ASSETTAG)) {
			side.setassettag(propvalue);
		}
		else if(s.equals(SUPERTYPE)) {
			side.setsupertype(propvalue);
		}
		else if(s.equals(TYPE)) {
			side.settype(propvalue);
		}
		else if(s.equals(LOCATION)) {
			side.setlocation(propvalue);
		}
		else { parseError(propkey,propvalue); }

		_allLinks.put(key,link);
	}

	public static void describeAll() {
		Set keys = _allLinks.keySet();
		for(Iterator i=keys.iterator();i.hasNext();) {
			String key = (String)i.next();
			Link link = (Link)_allLinks.get(key);
			link.describe();
		}
	}

	public String toString() {
		String s = "Link(";
		s += "left=" + left;
		s += ",";
		s += "right=" + right;
		s += ")";
		return(s);
	}

	public void describe() {
		System.out.println(this);
	}
}
*/

	/*
	public void fetchLinks(String linkfile) throws DifApplicationException {
		Properties props = new MyProperties(linkfile);

		Enumeration e = props.propertyNames();
		while(e.hasMoreElements()) {
			String name = (String)e.nextElement();
			String value = props.getProperty(name);
			Link.add(name,value);
		}

		Link.describeAll();
	}
	*/

	/*
	1. get console
	2. find link with console
	3. traverse link
	4. query device on other end for port & ip
	*/

	/*
	public SystemContext initSystemContext(String tsfile) throws DifApplicationException {
		LmDeviceVo ldv = DifApplication.fetchDis(tsfile);
		String mgmtIfAddr = AccessorUtility.mgmtIfAddr(ldv);
		int port = 897;
		fetchLinks(linkfile);
		SystemContext sc = new SystemContext(mgmtIfAddr,port);
		return(sc);
	}
	*/


class Command_c extends Command {
	public Command_c() {
		super();
		_shortHelp = "Alias for 'create dds.xml dis.xml sc.txt'";
		_longHelp = "";
	}

	public void doExecute(Environment env) throws DifApplicationException {
		String create = "create";
		String ddsfile = "dds.xml";
		String disfile = "dis.xml";
		String scfile = "sc.txt";

		ArrayList argv = new ArrayList();
		argv.add(create);
		argv.add(ddsfile);
		argv.add(disfile);
		argv.add(scfile);
		Environment e = new Environment(argv,env);

		Command command = Command.locate(create);
		command.execute(e);
	}
}

class Command_create extends Command {
	public Command_create() {
		super();
		_shortHelp = "Create device driver";
		_longHelp = "ddsfile disfile scfile";
	}

	public void doExecute(Environment env) throws DifApplicationException {
		if(env.getDriver() != null) {
			throw(new DifApplicationException("driver already loaded"));
		}

		countArgs(4,env);

		String ddsfile = env.argv(1);
		String disfile = env.argv(2);
		String scfile = env.argv(3);

		SystemContext sc;
		try {
			sc = SystemContextFactory.create(scfile);
		}
		catch(MissingPropertyException mpe) {
			throw(new DifApplicationException(mpe));
		}
		catch(BadValueException bve) {
			throw(new DifApplicationException(bve));
		}
		catch(UnknownHostException uhe) {
			throw(new DifApplicationException(uhe));
		}
		catch(IOException ioe) {
			throw(new DifApplicationException(ioe));
		}

		LmDeviceVo disVo;
		try {
			disVo = DifApplication.fetchDis(disfile);
		}
		catch(DisBuilderException dbe) {
			throw(new DifApplicationException(dbe));
		}
		catch(FileNotFoundException fnfe) {
			throw(new DifApplicationException("file not found: " + disfile));
		}

		try {
			DeviceDriver driver = DeviceDriverFactory.create(ddsfile,disVo,sc);
			env.setDriver(driver);
		}
		catch(FileNotFoundException fnfe) {
			throw(new DifApplicationException("File not found: " + ddsfile));
		}
		catch(IOException ioe) {
			throw(new DifApplicationException(ioe));
		}
		catch(SAXException saxe) {
			throw(new DifApplicationException(saxe));
		}
		catch(DeviceRoleException dre) {
			throw(new DifApplicationException(dre));
		}
		catch(Exception e) {
			throw(new DifApplicationException(e));
		}
		finally {
			DeviceDriver driver = env.getDriver();
			if(null == driver) {
				env.writeln("An error occurred on create.");
				env.writeln("You must quit and restart.");
			}
		}
	}
}

class Command_f extends Command {
	public Command_f() {
		super();
		_shortHelp = "Alias for 'call function arg.txt'";
		_longHelp = "";
	}

	public void doExecute(Environment env) throws DifApplicationException {
		String call = "call";
		String function = "function";
		String argfile = "arg.txt";

		ArrayList argv = new ArrayList();
		argv.add(call);
		argv.add(function);
		argv.add(argfile);
		Environment e = new Environment(argv,env);

		Command command = Command.locate(call);
		command.execute(e);
	}
}

class Command_call extends Command {
	public Command_call() {
		super();
		_shortHelp = "Call a function on the currently loaded driver";
		_longHelp = "function [argumentfile]";
	}

	public void doExecute(Environment env) throws DifApplicationException {
		DeviceDriver driver = env.getDriver();
		if(null == driver) { throw(new NoDriverException()); }

		AttributeCollection args = new AttributeCollectionImpl();
		String functionName;

		switch(env.argc()) {
			case(3):
				String argfile = env.argv(2);
				try {
					args = DifApplication.doReadArgs(argfile);
				}
				catch(IOException ioe) {
					throw(new DifApplicationException(ioe));
				}
			case(2):
				functionName = env.argv(1);
				break;
			default:
				throw(new BadArgumentCountException());
		}

		try {
			AttributeCollection ret = driver.executeByName(functionName,args);

			//AttributeUtil.dumpAttributeCollection(ret);
			env.dumpAttributeCollection(ret);
		}
		catch(MissingFunctionException mfe) {
			throw(new DifApplicationException(mfe));
		}
		catch(DDSExecutionException ddsee) {
			throw(new DifApplicationException(ddsee));
		}
	}
}

class Command_v extends Command {
	public Command_v() {
		super();
		_shortHelp = "Alias for 'validate dis.xml dts.xml'";
		_longHelp = "";
	}

	public void doExecute(Environment env) throws DifApplicationException {
		String validate = "validate";
		String disfile = "dis.xml";
		String dtsfile = "dts.xml";

		ArrayList argv = new ArrayList();
		argv.add(validate);
		argv.add(disfile);
		argv.add(dtsfile);
		Environment e = new Environment(argv,env);

		Command command = Command.locate(validate);
		command.execute(e);
	}
}

class Command_validate extends Command {
	public Command_validate() {
		super();
		_shortHelp = "Validate a dis against a dts";
		_longHelp = "disfile dtsfile";
	}

	public void doExecute(Environment env) throws DifApplicationException {
		countArgs(3,env);
		String disfile = env.argv(1);
		String dtsfile = env.argv(2);

		LmDeviceVo disVo;
		try {
			disVo = DifApplication.fetchDis(disfile);

			/*
			if(disVo.getSubDevices()!=null && disVo.getSubDevices().size()>0) {
				Enumeration enum = disVo.getSubDevices().elements();
				while(enum.hasMoreElements()) {
					LmDeviceVo distemp = (LmDeviceVo)enum.nextElement();
					env.writeln("Subdevice assettag = "+distemp.getAssetTag());
				}
			}
			*/
		}
		catch(DisBuilderException dbe) {
			throw(new DifApplicationException(dbe));
		}
		catch(FileNotFoundException fnfe) {
			throw(new DifApplicationException("file not found: " + disfile));
		}

		InventoryValidatee dts = fetchDts(dtsfile);

		InventoryValidator validator = new InventoryValidator(disVo,dts);
		SetValidator result = null;
		try {
			result = validator.validate();
		}
		catch(ValidationException ve) {
			throw(new DifApplicationException(ve));
		}

		if(result.isValid()) {
			env.writeln(disfile + " conforms to " + dtsfile);
		}
		else {
			env.writeln(disfile + " does not conform to " + dtsfile);
			env.writeln(result.toInvalid());
		}
	}

	public InventoryValidatee fetchDts(String filename) throws DifApplicationException {
		try {
			InputSource is = new InputSource(new FileInputStream(filename));
			DTSParser parser = new DTSParser(is);
			LmDeviceTypeVo dtsVo = parser.getDtsVo();

			dtsVo.setParentEnterpriseName(DifApplication.DEFAULT_ENTERPRISE);
			if(dtsVo.getInternalDeviceTypes()!=null && dtsVo.getInternalDeviceTypes().size()>0) {
				Iterator i = dtsVo.getInternalDeviceTypes().iterator();
				while(i.hasNext()) {
					LmDeviceTypeVo ldtv = (LmDeviceTypeVo)i.next();
					ldtv.setParentEnterpriseName(DifApplication.DEFAULT_ENTERPRISE);
				}
			}

			TransientDataModel datamodel = new TransientDataModel();
			datamodel.addEnterprise(new LmEnterpriseVo(DifApplication.DEFAULT_ENTERPRISE,"default enterprise"));
			datamodel.addLab(new LmLabVo(DifApplication.DEFAULT_LAB,"default lab","",DifApplication.DEFAULT_ENTERPRISE));
			datamodel.addDeviceType(dtsVo);
			return(datamodel);
		}
		catch(DtsBuilderException dbe) {
			throw(new DifApplicationException(dbe));
		}
		catch(FileNotFoundException fnfe) {
			throw(new DifApplicationException("File not found: " + filename));
		}
		catch(ValidationException ve) {
			throw(new DifApplicationException(ve));
		}
	}
}

class Command_destroy extends Command {
	public Command_destroy() {
		super();
		_shortHelp = "Destroy the currently loaded driver";
		_longHelp = "";
	}

	public void doExecute(Environment env) throws DifApplicationException {
		DeviceDriver driver = env.getDriver();
		if(null == driver) { throw(new NoDriverException()); }

		// FIXME: Get around to fixing this someday!!!

		env.writeln("Sorry, but destroy is still broken.");
		env.writeln("If you want to load a new driver you'll need to quit and restart.");

		return;

		/*
		env.destroyDriver();
		env.destroyConsoleController();
		System.gc();
		*/
	}
}

class Command_lsfunctions extends Command {
	public Command_lsfunctions() {
		super();
		_shortHelp = "List functions on the current driver";
		_longHelp = "";
	}

	public void doExecute(Environment env) throws DifApplicationException {
		DeviceDriver driver = env.getDriver();
		if(null == driver) { throw(new NoDriverException()); }

		String s[] = driver.functionNames();

		if(0 == s.length) {
			env.writeln("no functions");
			return;
		}

		for(int i=0;i<s.length;i++) {
			env.writeln("  " + s[i]);
		}
	}
}

class Command_debug extends Command {
	public Command_debug() {
		super();
	}

	public void doExecute(Environment env) {
		com.avulet.consoleserver.util.ThreadTree.dumpThreadTree();
		env.writeln("Some boring debug stuff has been written to the log.");
	}
}

class Command_lsvars extends Command {
	public Command_lsvars() {
		super();
		_shortHelp = "List all inventory variables";
		_longHelp = "";
	}

	public void doExecute(Environment env) {
		String s[] = InventoryAccessorFactory.enumerateInventoryAccessors();
		for(int i=0;i<s.length;i++) {
			env.writeln("  " + s[i]);
		}
	}
}

class Command_questionmark extends Command {
	public Command_questionmark() {
		super();
		_shortHelp = "Help";
		_longHelp = "";
	}

	public void doExecute(Environment env) throws DifApplicationException {
		Command command = Command.locate("help");
		command.execute(env);
	}

	protected String calculateName() {
		return("?");
	}
}

class Command_parse extends Command {
	public Command_parse() {
		super();
		_shortHelp = "Attempt to parse a dds";
		_longHelp = "ddsfile";
	}

	public void doExecute(Environment env) throws DifApplicationException {
		countArgs(2,env);

		String ddsfile = env.argv(1);

		try {
			byte[] ddsxml = Util.file2bytes(ddsfile);
			DDSTreeFactory ddstf = new DDSTreeFactory(ddsxml);
			DDSTree ddst = ddstf.build();

			ddst.describe();

			String s = "successfully parsed " + ddsfile;
			s += " with ";
			s += ddstf.countError() + " error(s), ";
			s += ddstf.countFatalError() + " fatal error(s), ";
			s += ddstf.countWarning() + " warning(s)";
			env.writeln(s);

			env.writeln("a detailed description has been written to the log");
		}
		catch(IOException ioe) {
			throw(new DifApplicationException(ioe));
		}
		catch(SAXException saxe) {
			throw(new DifApplicationException(saxe));
		}
	}
}

class Command_p extends Command {
	public Command_p() {
		super();
		_shortHelp = "Alias for 'parse dds.xml'";
		_longHelp = "";
	}

	public void doExecute(Environment env) throws DifApplicationException {
		String parse = "parse";
		String ddsfile = "dds.xml";

		ArrayList argv = new ArrayList();
		argv.add(parse);
		argv.add(ddsfile);
		Environment e = new Environment(argv,env);

		Command command = Command.locate(parse);
		command.execute(e);
	}
}

class DifApplicationException extends Exception {
	public DifApplicationException(String s) {
		super(s);
	}

	public DifApplicationException(Exception e) {
		this(e.getClass().getName() + ": " + e.getMessage());
	}

	public DifApplicationException(Exception e,String s) {
		this(e.getClass().getName() + ": " + e.getMessage() + ": {" + s + "}");
	}
}

class BadArgumentCountException extends DifApplicationException {
	protected String _info = "";

	public BadArgumentCountException() {
		super("BadArgumentCountException");
	}

	public BadArgumentCountException(int expected,int actual) {
		super("BadArgumentCountException(expected=" + expected + ",actual=" + actual + ")");
		_info = "expected=" + expected + ",actual=" + actual;
	}

	public String info() {
		return(_info);
	}
}

class NoDriverException extends DifApplicationException {
	public NoDriverException() {
		super("NoDriverException");
	}
}

class MyProperties extends Properties {
	protected String _filename;

	public MyProperties(String filename) throws IOException {
		super();

		_filename = filename;

		load(new FileInputStream(_filename));
	}

	public String getRequiredProperty(String name) throws MissingPropertyException {
		String value = getProperty(name);
		if(null == value) {
			throw(new MissingPropertyException(name,_filename));
		}
		return(value);
	}

	public int getRequiredPropertyInt(String name)
			throws MissingPropertyException,BadValueException {

		String value = getRequiredProperty(name);
		try {
			int i = Integer.parseInt(value);
			return(i);
		}
		catch(NumberFormatException nfe) {
			String s = nfe.getMessage();
			s += " for property '" + name + "'";
			s += " in file '" + _filename + "'";
			throw(new BadValueException(s));
		}
	}
}
