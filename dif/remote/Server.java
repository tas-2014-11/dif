package com.lumenare.dif.remote;

import EDU.oswego.cs.dl.util.concurrent.*;

import weblogic.rmi.Naming;
import weblogic.rmi.Remote;
import weblogic.rmi.RemoteException;

import java.io.IOException;

import java.lang.reflect.InvocationTargetException;

import java.net.InetAddress;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.xml.sax.SAXException;

import com.avulet.db.Key;
import com.avulet.db.KeyImpl;
import com.avulet.element.ConsoleLocation;

import com.lumenare.common.domain.attribute.AttributeCollection;
import com.lumenare.common.domain.attribute.adapter.AttributeCollectionImpl;
import com.lumenare.common.domain.attribute.adapter.AttributeUtil;

import com.lumenare.datastore.device.LmDeviceVo;
import com.lumenare.datastore.common.DeviceRole;

import com.lumenare.dif.BadValueException;
import com.lumenare.dif.Util;
import com.lumenare.dif.language.AccessorUtility;
import com.lumenare.dif.language.DDSExecutionException;
import com.lumenare.dif.language.SystemContext;
import com.lumenare.dif.manager.DeviceDriver;
import com.lumenare.dif.manager.DeviceDriverFactory;
import com.lumenare.dif.manager.DeviceRoleException;
import com.lumenare.dif.manager.MissingFunctionException;

import com.lumenare.dif.manager.ManagedDevice;
import com.lumenare.dif.manager.MatrixSwitch;
import com.lumenare.dif.manager.PowerController;

// TODO: after destroy, peek and pre-fail

public class Server implements
		RemoteDriver,
		RemoteDriverFactory,
		RemoteMatrixSwitch,
		RemoteManagedDevice,
		RemotePowerController {

	public static final String _qualifiedRegistryName = "//localhost/" +
		com.lumenare.service.lookup.ServiceLookupCache.DIF_SERVER_RMI;

	public static void main(String args[]) throws Exception {
		System.out.println("Server.main()");
		Remote r = new Server();
		Naming.rebind(_qualifiedRegistryName,r);
	}

	public Server() throws RemoteException {
		String method = toString();
		Log.call(method);
	}

	public String toString() {
		return("Server()");
	}

	protected void finalize() throws Throwable {
		ThreadTree.dumpThreadTree();
	}

	// TODO: add arg to specify console name
	public ConsoleLocation getConsoleLocation(Key key)
			throws
			NoSuchDriverException,BadArgumentException {

		String method = toString() + ".getConsoleLocation(" + key + ")";
		Log.call(method);

		synchronized(_machine) {
			DeviceDriver d = _machine.locateDriverByKey(key);
			ConsoleLocation cl = d.consoleLocation();

			Log.ret(method + "=" + cl);
			return(cl);
		}
	}

	protected final DIFMachine _machine = new DIFMachine();

	public void create(byte[] dds,LmDeviceVo dis,SystemContext sc)
			throws
			NoSuchDriverException,BadArgumentException,
			IOException,SAXException,DeviceRoleException,
			BadValueException,
			DDSExecutionException {

		String method = "create(" + dds.length;
		method += "," + AccessorUtility.formatLdv(dis);
		method += "," + sc + ")";
		Log.call(method);

		FutureResultStuff.create(_machine,dds,dis,sc);

		Log.ret(method);
	}

	public void destroy(Key key)
			throws
			NoSuchDriverException,BadArgumentException {

		String method = "destroy(" + key + ")";
		Log.call(method);

		FutureResultStuff.destroy(_machine,key);

		Log.ret(method);
	}

	public Map enumerateHack() {
		String method = "enumerate()";
		Log.call(method);
		return(_machine.enumerateHack());
	}

	public Map enumerateHack(DeviceRole role) {
		String method = "enumerate(" + role + ")";
		Log.call(method);
		return(_machine.enumerateHack(role));
	}

	public AttributeCollection executeByName(Key key,String function,AttributeCollection ac)
			throws
			NoSuchDriverException,BadArgumentException,
			MissingFunctionException,DDSExecutionException {

		String method = "executeByName(";
		method += key + ",";
		method += function + ",";
		method += AttributeUtil.describe(ac) + ")";
		Log.call(method);

		Callable callable = new Callable_executeByName(_machine,key,function,ac);

		AttributeCollection ret =
			(AttributeCollection)FutureResultStuff.setupAndExecute(_machine,key,callable);

		Log.ret(method + ":" + ret);
		return(ret);
	}

	public void sanityCheck(Key key)
			throws
			NoSuchDriverException,BadArgumentException,
			MissingFunctionException,DDSExecutionException {

		String method = "sanityCheck(" + key + ")";
		Log.call(method);

		FutureResultStuff.setupAndExecute(_machine,key,new Callable_sanitycheck(_machine,key));

		Log.ret(method);
	}

	public void connect(TargetedLink[] links)
			throws
			BadArgumentException,NoSuchDriverException,
			DeviceRoleException,
			DDSExecutionException {

		String method = "connect(" + links.length + ")";
		method += ":" + Util.describe(links);
		Log.call(method);

		FutureResultStuff.setupAndExecute(_machine,links,new Factory_connect());

		Log.ret(method);
	}

	public void disconnect(TargetedLink[] links)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException,MissingFunctionException,
			DDSExecutionException {

		String method = "disconnect(" + links.length + ")";
		method += ":" + Util.describe(links);
		Log.call(method);

		FutureResultStuff.setupAndExecute(_machine,links,new Factory_disconnect());

		Log.ret(method);
	}

	protected void notImplemented(String method) throws NoSuchDriverException {
		String s = "ISNOTIMPLEMENTED:" + method;
		throw(new NoSuchDriverException(s));
	}

	public boolean isBreakable(TargetedLink link) throws NoSuchDriverException {
		String method = "isBreakable(" + link + ")";
		notImplemented(method);
		return(false); // not reached
	}

	public boolean isMakeable(TargetedLink link) throws NoSuchDriverException {
		String method = "isMakeable(" + link + ")";
		notImplemented(method);
		return(false); // not reached
	}

	public void configure(TargetedAttributeCollection[] configs)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException,MissingFunctionException,
			DDSExecutionException {

		String method = "configure(" + configs.length + ")";
		method += ":" + Util.describe(configs);
		Log.call(method);

		FutureResultStuff.setupAndExecute(_machine,configs,new Factory_configure());

		Log.ret(method);
	}

	public TargetedAttributeCollection[] extractConfiguration(Key[] deviceKeys)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException,MissingFunctionException,
			DDSExecutionException {

		String method = "extractConfiguration(" + deviceKeys.length + ")";
		method += ":" + Util.describe(deviceKeys);
		Log.call(method);

		TargetedAttributeCollection[] tac =
			(TargetedAttributeCollection[])FutureResultStuff.setupAndExecute(_machine,
				deviceKeys,new Factory_extractConfiguration());

		Log.ret(method + ":" + Util.describe(tac));
		return(tac);
	}

	public TargetedAttributeCollection[] measureUtilization(Key[] deviceKeys)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException,MissingFunctionException,
			DDSExecutionException {

		String method = "measureUtilization(" + deviceKeys.length + ")";
		method += ":" + Util.describe(deviceKeys);
		Log.call(method);

		TargetedAttributeCollection[] tac =
			(TargetedAttributeCollection[])FutureResultStuff.setupAndExecute(_machine,
				deviceKeys,new Factory_measureUtilization());

		Log.ret(method + ":" + Util.describe(tac));
		return(tac);
	}

	public void turnPowerOn(TargetedString[] locations)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException,MissingFunctionException,
			DDSExecutionException {

		String method = "turnPowerOn(" + locations.length + ")";
		method += ":" + Util.describe(locations);
		Log.call(method);

		FutureResultStuff.setupAndExecute(_machine,locations,new Factory_turnPowerOn());

		Log.ret(method);
	}

	public void turnPowerOff(TargetedString[] locations)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException,MissingFunctionException,
			DDSExecutionException {

		String method = "turnPowerOff(" + locations.length + ")";
		method += ":" + Util.describe(locations);
		Log.call(method);

		FutureResultStuff.setupAndExecute(_machine,locations,new Factory_turnPowerOff());

		Log.ret(method);
	}

	public boolean powerIsOn(TargetedString location)
			throws
			NoSuchDriverException,BadArgumentException {

		String method = "powerIsOn(" + location + ")";
		notImplemented(method);
		return(false); // not reached
	}
}

class DIFMachine {
	public DIFMachine() {
	}

	public String toString() {
		String s = "DIFMachine()";
		s += ":" + _drivers;
		s += ":" + _executors;
		return(s);
	}

	// FIXME: there is no destroy
	// FIXME: if there were, it would be synchronized

	public synchronized void addDriverAndExecutor(Key key,DeviceDriver dd,Executor executor) {
		_drivers.put(key,dd);
		_executors.put(key,executor);
	}



	public static void baeIfNull(Object o,String method,String var) throws BadArgumentException {
		if(null == o) {
			String s = method + ":ISNULL:" + var;
			BadArgumentException bae = new BadArgumentException(s);
			bae.printStackTrace();
			throw(bae);
		}
	}

	protected final HashMap _drivers = new HashMap();

	public synchronized DeviceDriver locateDriverByKey(Key key)
			throws
			NoSuchDriverException,BadArgumentException {

		String method = "locateDriverByKey(" + key + ")";
		Log.call(method);

		baeIfNull(key,method,"key");
		if(!_drivers.containsKey(key)) {
			throw(new NoSuchDriverException(key));
		}

		DeviceDriver driver = (DeviceDriver)_drivers.get(key);
		if(null == driver) {
			throw(new NoSuchDriverException(key));
		}
		return(driver);
	}

	protected synchronized DeviceDriver locateDriverByKeyAndRole(Key k,Class c)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException {

		DeviceDriver driver = locateDriverByKey(k);

		if(!(driver.getClass() == c)) {
			String s = "DRIVERISNOTA(" + c.getName() + ")";
			s += ":" + driver;
			throw(new DeviceRoleException(s));
		}

		return(driver);
	}

	public ManagedDevice locateManagedDevice(Key k)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException {

		return((ManagedDevice)locateDriverByKeyAndRole(k,ManagedDevice.class));
	}

	public MatrixSwitch locateMatrixSwitch(Key k)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException {

		return((MatrixSwitch)locateDriverByKeyAndRole(k,MatrixSwitch.class));
	}

	public PowerController locatePowerController(Key k)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException {

		return((PowerController)locateDriverByKeyAndRole(k,PowerController.class));
	}

	public DeviceDriver locateDeviceDriver(Key k)
			throws
			NoSuchDriverException,BadArgumentException {

		return(locateDriverByKey(k));
	}

	public synchronized boolean driverExists(Key key) {
		return(_drivers.containsKey(key));
	}

	public synchronized Map enumerateHack() {
		HashMap map = new HashMap(_drivers.size());
		Collection values = _drivers.values();
		for(Iterator i = values.iterator();i.hasNext();) {
			DeviceDriver dd = (DeviceDriver)i.next();
			map.put(dd.ldvHack().getAssetTag(),dd.ldvHack().getDeviceRole());
		}
		return(map);
	}

	public synchronized Map enumerateHack(DeviceRole role) {
		HashMap map = new HashMap(_drivers.size());
		Collection values = _drivers.values();
		for(Iterator i = values.iterator();i.hasNext();) {
			DeviceDriver dd = (DeviceDriver)i.next();

			if(role == dd.ldvHack().getDeviceRole()) {
				map.put(dd.ldvHack().getAssetTag(),dd.ldvHack().getDeviceRole());
			}
		}
		return(map);
	}

	protected final HashMap _executors = new HashMap();

	protected synchronized QueuedExecutor locateExecutor(Key key)
			throws
			BadArgumentException,NoSuchDriverException {

		String method = "locateExecutor(" + key + ")";
		Log.call(method);

		baeIfNull(key,method,"key");
		if(!_executors.containsKey(key)) {
			throw(new NoSuchDriverException(key));
		}

		QueuedExecutor executor = (QueuedExecutor)_executors.get(key);
		if(null == executor) {
			Log.info("EXECUTORNOTFOUND"); // should not happen
			throw(new NoSuchDriverException(key));
		}
		return(executor);
	}

	public void executeOn(Key key,Runnable runner)
			throws
			BadArgumentException,NoSuchDriverException,
			InterruptedException {

		Executor executor = locateExecutor(key);
		executor.execute(runner);
	}
}

class VOID {
	private VOID() { }
	public static final VOID VOID = new VOID();
	public String toString() { return("VOID()"); }
}

class Callable_create implements Callable {
	protected final byte[] _dds;
	protected final LmDeviceVo _dis;
	protected final SystemContext _sc;
	protected final String _toString;

	public Callable_create(byte[] dds,LmDeviceVo dis,SystemContext sc) {
		_toString = "Callable_create(" + dds.length
			+ "," + AccessorUtility.formatLdv(dis)
			+ "," + sc + ")";

		_dds = dds;
		_dis = dis;
		_sc = sc;
	}

	public Object call()
			throws
			NoSuchDriverException,BadArgumentException,
			IOException,SAXException,DeviceRoleException,
			BadValueException,
			DDSExecutionException {

		return(DeviceDriverFactory.create(_dds,_dis,_sc));
	}

	public String toString() {
		return(_toString);
	}
}

class FutureResultStuff {
	public static void create(DIFMachine dm,byte[] dds,LmDeviceVo dis,SystemContext sc)
			throws
			BadArgumentException,
			NoSuchDriverException,
			DDSExecutionException {

		String method = "create(" + dds.length;
		method += "," + AccessorUtility.formatLdv(dis);
		method += "," + sc + ")";
		Log.call(method);

		DIFMachine.baeIfNull(dds,method,"dds");
		DIFMachine.baeIfNull(dis,method,"dis");
		DIFMachine.baeIfNull(sc,method,"sc");

		String keyValue = dis.getPrimaryKey();
		DIFMachine.baeIfNull(keyValue,method,"keyValue");
		Key key = new KeyImpl(keyValue);

		if(dm.driverExists(key)) {
			String s = "DRIVERALREADYCREATED:" + method;
			throw(new NoSuchDriverException(s));
		}

		FutureResult futureCreate = new FutureResult();
		Callable_create cc = new Callable_create(dds,dis,sc);
		Runnable runner = futureCreate.setter(cc);

		QueuedExecutor executor = new QueuedExecutor(new LinkedQueue());

		try {
			executor.execute(runner);
			DeviceDriver dd = (DeviceDriver)futureCreate.get();
			dm.addDriverAndExecutor(key,dd,executor);
		}
		catch(Exception e) {
			executor.shutdownNow();
			Log.stack(method,e);
			throw(new DDSExecutionException(e.toString()));
		}
	}

	public static void destroy(DIFMachine dm,Key key) {
		Log.call("destroy(" + dm + "," + key + ")");

		String method = "destroy(" + key + ")";

		try {
			DeviceDriver dd = dm.locateDriverByKey(key);
			dd.close();
			dd = null;
		}
		catch(Exception e1) {
			Log.info(method + ":" + e1);
			Log.stack(method,e1);
		}

		try {
			QueuedExecutor executor = dm.locateExecutor(key);
			executor.shutdownNow();
		}
		catch(Exception e2) {
			Log.info(method + ":" + e2);
			Log.stack(method,e2);
		}

		Log.ret("destroy(" + dm + "," + key + ")");
	}

	// TODO: make the return value a bit more type-safe
	public static Object setupAndExecute(DIFMachine dm,Key key,Callable callable)
			throws
			NoSuchDriverException,BadArgumentException,
			MissingFunctionException,DDSExecutionException {

		String method = "setupAndExecute(" + dm + "," + key + "," + callable + ")";
		Log.call(method);

		FutureResult future = new FutureResult();
		Runnable runner = future.setter(callable);

		try {
			dm.executeOn(key,runner);
		}
		catch(InterruptedException ie) {
			Log.info(method + ":UNEXPECTEDEXCEPTION:" + ie);
			Log.stack(method,ie);
		}

		String s;
		try {
			Object result = future.get();
			s = "SUCCESS(" + key + "," + result + ")";
			Log.ret(method + ":" + s);
			return(result);
		}
		catch(InvocationTargetException ite) {
			s = "FAIL(" + key + "," + ite.getTargetException() + ")";
			Log.ret(method + ":" + s);
			throw(new DDSExecutionException(s));
		}
		catch(InterruptedException ie) {
			s = "FAIL(" + key + "," + ie + ")";
			Log.ret(method + ":" + s);
			throw(new DDSExecutionException(s));
		}
	}

	// TODO: think about wrapping FutureResult to add type-safety
	// TODO: we always know the return types and exceptions

	private static Object[] gatherResults(FutureResult[] fr,Object[] o,
			String shortMethod,String longMethod) throws DDSExecutionException {

		Object results[] = new Object[fr.length];

		int nfail = 0;
		String failmessage = "";
		for(int i=0;i<fr.length;i++) {
			String s;
			try {
				Object result = fr[i].get();
				s = "SUCCESS(" + o[i] + "," + result + ")";

				// all our callables return either VOID or a TAC
				// if it's not a TAC then return null in its slot

				if(null == result) { }
				else if(VOID.VOID == result) { }
				else { results[i] = result; }
			}
			catch(InvocationTargetException ite) {
				s = "FAIL(" + o[i] + "," + ite.getTargetException() + ")";

				if(nfail > 0) { failmessage += ","; }
				failmessage += s;
				++nfail;
			}
			catch(InterruptedException ie) {
				s = "FAIL(" + o[i] + "," + ie + ")";

				if(nfail > 0) { failmessage += ","; }
				failmessage += s;
				++nfail;
			}

			Log.info(shortMethod + ":" + s);
		}

		Log.ret(longMethod + ": " + nfail + " failures");

		if(nfail > 0) {
			throw(new DDSExecutionException(failmessage));
		}

		return(results);
	}

	// TODO: think about merging the two private setupAndExecute() methods

	private static Object[] setupAndExecute(DIFMachine dm,Key[] keys,CallableFactory_dm_k cf,
				String shortMethod,String longMethod)
			throws
			BadArgumentException,NoSuchDriverException,
			DeviceRoleException,
			DDSExecutionException {

		Log.call(longMethod);

		FutureResult future[] = new FutureResult[keys.length];

		// FIXME: if one driver is missing should the other jobs still execute ???

		for(int i=0;i<keys.length;i++) {
			future[i] = new FutureResult();
			Key key = keys[i];
			Callable callable = cf.create(dm,key);
			Runnable runner = future[i].setter(callable);

			try {
				dm.executeOn(key,runner);
			}
			catch(InterruptedException ie) {
				Log.info("UNEXPECTEDEXCEPTION:" + ie);
				Log.stack(shortMethod,ie);
				// TODO: is this fatal???
			}
		}

		return(gatherResults(future,keys,shortMethod,longMethod));
	}

	public static Object[] setupAndExecute(DIFMachine dm,Key[] keys,CallableFactory_dm_k cf)
			throws
			BadArgumentException,NoSuchDriverException,
			DeviceRoleException,
			DDSExecutionException {

		String shortMethod = cf.operationName() + "(" + keys.length + ")";
		String longMethod = shortMethod +  ":" + Util.describe(keys);

		return(setupAndExecute(dm,keys,cf,shortMethod,longMethod));
	}

	private static Object[] setupAndExecute(DIFMachine dm,TargetedObject[] toa,CallableFactory cf,
				String shortMethod,String longMethod)
			throws
			BadArgumentException,NoSuchDriverException,
			DeviceRoleException,
			DDSExecutionException {

		Log.call(longMethod);

		FutureResult future[] = new FutureResult[toa.length];

		// FIXME: if one driver is missing should the other jobs still execute ???

		for(int i=0;i<toa.length;i++) {
			future[i] = new FutureResult();
			TargetedObject to = toa[i];
			Callable callable = cf.create(dm,to);
			Runnable runner = future[i].setter(callable);

			try {
				dm.executeOn(to.key(),runner);
			}
			catch(InterruptedException ie) {
				Log.info("UNEXPECTEDEXCEPTION:" + ie);
				Log.stack(shortMethod,ie);
				// TODO: is this fatal???
			}
		}

		return(gatherResults(future,toa,shortMethod,longMethod));
	}

	// there is a good reason to make this method private
	// even though everybody else just delegates to it.
	// we must enforce that the TargetedObject array matches
	// the type of the factory that it's paired with.

	private static Object[] setupAndExecute(DIFMachine dm,TargetedObject[] to,CallableFactory_dm_to cf)
			throws
			BadArgumentException,NoSuchDriverException,
			DeviceRoleException,
			DDSExecutionException {

		String shortMethod = cf.operationName() + "(" + to.length + ")";
		String longMethod = shortMethod +  ":" + TargetedObject.describe(to);

		return(setupAndExecute(dm,to,cf,shortMethod,longMethod));
	}

	public static Object[] setupAndExecute(DIFMachine dm,TargetedAttributeCollection[] tac,
				CallableFactory_dm_tac cf)

			throws
			BadArgumentException,NoSuchDriverException,
			DeviceRoleException,
			DDSExecutionException {

		return(setupAndExecute(dm,(TargetedObject[])tac,cf));
	}

	public static Object[] setupAndExecute(DIFMachine dm,TargetedLink[] tl,CallableFactory_dm_tl cf)
			throws
			BadArgumentException,NoSuchDriverException,
			DeviceRoleException,
			DDSExecutionException {

		return(setupAndExecute(dm,(TargetedObject[])tl,cf));
	}

	public static Object[] setupAndExecute(DIFMachine dm,TargetedString[] ts,CallableFactory_dm_ts cf)
			throws
			BadArgumentException,NoSuchDriverException,
			DeviceRoleException,
			DDSExecutionException {

		return(setupAndExecute(dm,(TargetedObject[])ts,cf));
	}
}

abstract class CallableFactory {
	public abstract Callable create(DIFMachine dm,Object o)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException;

	public abstract String operationName();
}

abstract class CallableFactory_dm_k extends CallableFactory {
	public abstract Callable create(DIFMachine dm,Key k)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException;

	public Callable create(DIFMachine dm,Object o)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException {

		// FIXME: what if the O is not a K ???
		return(create(dm,(Key)o));
	}
}

class Factory_extractConfiguration extends CallableFactory_dm_k {
	public Callable create(DIFMachine dm,Key k)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException {

		return(new Callable_extractConfiguration(dm,k));
	}

	public String operationName() {
		return("extractConfiguration");
	}
}

class Factory_measureUtilization extends CallableFactory_dm_k {
	public Callable create(DIFMachine dm,Key k)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException {

		return(new Callable_measureUtilization(dm,k));
	}

	public String operationName() {
		return("measureUtilization");
	}
}

abstract class CallableFactory_dm_to extends CallableFactory {
	public abstract Callable create(DIFMachine dm,TargetedObject to)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException;

	public Callable create(DIFMachine dm,Object o)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException {

		// FIXME: what if the O is not a TO ???
		return(create(dm,(TargetedObject)o));
	}
}

// FIXME: wtf am i really trying to do here ???

abstract class CallableFactory_dm_tl extends CallableFactory_dm_to {
	public abstract Callable create(DIFMachine dm,TargetedLink tl)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException;

	public Callable create(DIFMachine dm,TargetedObject to)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException {

		// FIXME: what if the TO is not a TL ???
		return(create(dm,(TargetedLink)to));
	}

}

class Factory_connect extends CallableFactory_dm_tl {
	public Callable create(DIFMachine dm,TargetedLink tl)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException {

		return(new Callable_connect(dm,tl));
	}

	public String operationName() {
		return("connect");
	}
}

class Factory_disconnect extends CallableFactory_dm_tl {
	public Callable create(DIFMachine dm,TargetedLink tl)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException {

		return(new Callable_disconnect(dm,tl));
	}

	public String operationName() {
		return("disconnect");
	}
}

abstract class CallableFactory_dm_tac extends CallableFactory_dm_to {
	public abstract Callable create(DIFMachine dm,TargetedAttributeCollection tac)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException;

	public Callable create(DIFMachine dm,TargetedObject to)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException {

		// FIXME: what if the TO is not a TAC ???
		return(create(dm,(TargetedAttributeCollection)to));
	}
}

class Factory_configure extends CallableFactory_dm_tac {
	public Callable create(DIFMachine dm,TargetedAttributeCollection tac)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException {

		return(new Callable_configure(dm,tac));
	}

	public String operationName() {
		return("configure");
	}
}

abstract class CallableFactory_dm_ts extends CallableFactory_dm_to {
	public abstract Callable create(DIFMachine dm,TargetedString ts)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException;

	public Callable create(DIFMachine dm,TargetedObject to)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException {

		// FIXME: what if the TO is not a TS ???
		return(create(dm,(TargetedString)to));
	}
}

class Factory_turnPowerOn extends CallableFactory_dm_ts {
	public Callable create(DIFMachine dm,TargetedString ts)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException {

		return(new Callable_turnPowerOn(dm,ts));
	}

	public String operationName() {
		return("turnPowerOn");
	}
}

class Factory_turnPowerOff extends CallableFactory_dm_ts {
	public Callable create(DIFMachine dm,TargetedString ts)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException {

		return(new Callable_turnPowerOff(dm,ts));
	}

	public String operationName() {
		return("turnPowerOff");
	}
}

class Callable_executeByName implements Callable {
	protected final DeviceDriver _dd;
	protected final Key _key;
	protected final String _function;
	protected final AttributeCollection _ac;

	public Callable_executeByName(DIFMachine dm,Key key,String function,AttributeCollection ac)
			throws
			NoSuchDriverException,BadArgumentException {

		_dd = dm.locateDeviceDriver(key);
		_key = key;
		_function = function;
		_ac = ac;
	}

	public String toString() {
		return("Callable_executeByName(" + _dd + "," + _key + "," + _function + "," + _ac + ")");
	}

	public Object call()
			throws
                	NoSuchDriverException,BadArgumentException,
                	MissingFunctionException,DDSExecutionException {

		AttributeCollection ac = _dd.executeByName(_function,_ac);
		return(ac);
	}
}

class Callable_sanitycheck implements Callable {
	protected final DeviceDriver _dd;
	protected final Key _key;

	public Callable_sanitycheck(DIFMachine dm,Key key)
			throws
			NoSuchDriverException,BadArgumentException {

		_dd = dm.locateDeviceDriver(key);
		_key = key;
	}

	public String toString() {
		return("Callable_sanitycheck(" + _dd + "," + _key + ")");
	}

	public Object call() throws MissingFunctionException,DDSExecutionException {
		_dd.sanityCheck();
		return(VOID.VOID);
	}
}

abstract class Callable_connectOrDisconnect implements Callable {
	protected final MatrixSwitch _ms;
	protected final Link _link;
	protected final String _toString;

	public Callable_connectOrDisconnect(DIFMachine dm,TargetedLink tl)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException {

		_ms = dm.locateMatrixSwitch(tl.key());
		_link = tl.link();
		_toString = Util.basename4class(getClass()) + "(" + _ms + "," + _link + ")";
	}

	public String toString() {
		return(_toString);
	}
}

class Callable_connect extends Callable_connectOrDisconnect {
	public Callable_connect(DIFMachine dm,TargetedLink tl)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException {

		super(dm,tl);
	}

	public Object call() throws MissingFunctionException,DDSExecutionException {
		_ms.connect(_link.left(),_link.right(),_link.name(),_link.value());
		return(VOID.VOID);
	}
}

class Callable_disconnect extends Callable_connectOrDisconnect {
	public Callable_disconnect(DIFMachine dm,TargetedLink tl)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException {

		super(dm,tl);
	}

	public Object call() throws MissingFunctionException,DDSExecutionException {
		_ms.disconnect(_link.left(),_link.right(),_link.name(),_link.value());
		return(VOID.VOID);
	}
}

class Callable_configure implements Callable {
	protected final ManagedDevice _md;
	protected final AttributeCollection _ac;

	public Callable_configure(DIFMachine dm,TargetedAttributeCollection tac)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException {

		_md = dm.locateManagedDevice(tac.key());
		_ac = tac.attributeCollection();
	}

	public String toString() {
		return("Callable_configure(" + _md + "," + _ac + ")");
	}

	public Object call() throws MissingFunctionException,DDSExecutionException {
		_md.configure(_ac);
		return(VOID.VOID);
	}
}

class Callable_extractConfiguration implements Callable {
	protected final ManagedDevice _md;
	protected final Key _key;

	public Callable_extractConfiguration(DIFMachine dm,Key key)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException {

		_md = dm.locateManagedDevice(key);
		_key = key;
	}

	public String toString() {
		return("Callable_extractConfiguration(" + _md + "," + _key + ")");
	}

	public Object call() throws MissingFunctionException,DDSExecutionException {
		AttributeCollection ac = _md.extractConfiguration();
		TargetedAttributeCollection tac = new TargetedAttributeCollection(_key,ac);
		return(tac);
	}
}

class Callable_measureUtilization implements Callable {
	protected final ManagedDevice _md;
	protected final Key _key;

	public Callable_measureUtilization(DIFMachine dm,Key key)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException {

		_md = dm.locateManagedDevice(key);
		_key = key;
	}

	public String toString() {
		return("Callable_measureUtilization(" + _md + "," + _key + ")");
	}

	public Object call() {
		String method = toString();
		try {
			// it's ok if one fails. we just won't pass back any data for it.
			AttributeCollection ac = _md.measureUtilization();
			TargetedAttributeCollection tac = new TargetedAttributeCollection(_key,ac);
			return(tac);
		}
		catch(MissingFunctionException mfe) {
			Log.stack(method,mfe);
		}
		catch(DeviceRoleException dre) {
			Log.stack(method,dre);
		}
		catch(DDSExecutionException ddsee) {
			Log.stack(method,ddsee);
		}

		AttributeCollection ac = new AttributeCollectionImpl();
		TargetedAttributeCollection tac = new TargetedAttributeCollection(_key,ac);
		return(tac);
	}
}

class Callable_turnPowerOn implements Callable {
	protected final PowerController _pc;
	protected final String _location;

	public Callable_turnPowerOn(DIFMachine dm,TargetedString ts)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException {

		_pc = dm.locatePowerController(ts.key());
		_location = ts.string();
	}

	public String toString() {
		return("Callable_turnPowerOn(" + _pc + "," + _location + ")");
	}

	public Object call() throws MissingFunctionException,DDSExecutionException {
		_pc.turnpoweron(_location);
		return(VOID.VOID);
	}
}

class Callable_turnPowerOff implements Callable {
	protected final PowerController _pc;
	protected final String _location;

	public Callable_turnPowerOff(DIFMachine dm,TargetedString ts)
			throws
			NoSuchDriverException,BadArgumentException,
			DeviceRoleException {

		_pc = dm.locatePowerController(ts.key());
		_location = ts.string();
	}

	public String toString() {
		return("Callable_turnPowerOff(" + _pc + "," + _location + ")");
	}

	public Object call() throws MissingFunctionException,DDSExecutionException {
		_pc.turnpoweron(_location);
		return(VOID.VOID);
	}
}
