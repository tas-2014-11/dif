package com.lumenare.dif.manager;

import java.io.IOException;

import org.xml.sax.SAXException;

import com.lumenare.datastore.common.DeviceRole;
import com.lumenare.datastore.device.LmDeviceVo;

import com.lumenare.dif.Util;
import com.lumenare.dif.language.AccessorUtility;
import com.lumenare.dif.language.DDSExecutionException;
import com.lumenare.dif.language.DDSExecutor;
import com.lumenare.dif.language.DDSTree;
import com.lumenare.dif.language.DDSTreeFactory;
import com.lumenare.dif.language.SystemContext;

public class DeviceDriverFactory {
	protected final Log log = new Log(this);

	public DeviceDriverFactory() {
	}

	// FIXME: What to throw...
	protected DDSExecutor createDeviceDriver(byte[] ddsxml,LmDeviceVo ldv,
			SystemContext sc)
			throws IOException,SAXException,
			DDSExecutionException {

		String method = "createDeviceDriver(";
		method += ddsxml + ",";
		method += AccessorUtility.formatLdv(ldv) + ",";
		method += sc + ")";
		log.invoke(method);

		DDSTreeFactory ddstf = new DDSTreeFactory(ddsxml);
		DDSTree ddst = ddstf.build();

		DDSExecutor ddse = new DDSExecutor(ddst,ldv,sc);
		ddse.describe();

		log.ret(method);
		return(ddse);
	}

	// FIXME: What to throw...
	public ManagedDevice createManagedDeviceDriver(byte[] ddsxml,LmDeviceVo ldv,
			SystemContext sc)
			throws
			IOException,SAXException,DeviceRoleException,
			DDSExecutionException {

		String method = "createManagedDeviceDriver()";
		log.invoke(method);

		DDSExecutor ddse = createDeviceDriver(ddsxml,ldv,sc);
		ManagedDevice md = new ManagedDevice(ddse);

		log.ret(method);
		return(md);
	}

	// FIXME: What to throw...
	public MatrixSwitch createMatrixSwitchDriver(byte[] ddsxml,LmDeviceVo ldv,
			SystemContext sc)
			throws
			IOException,SAXException,DeviceRoleException,
			DDSExecutionException {

		String method = "createMatrixSwitchDriver()";
		log.invoke(method);

		DDSExecutor ddse = createDeviceDriver(ddsxml,ldv,sc);
		MatrixSwitch ms = new MatrixSwitch(ddse);

		log.ret(method);
		return(ms);
	}

	// FIXME: What to throw...
	public PowerController createPowerControllerDriver(byte[] ddsxml,LmDeviceVo ldv,
			SystemContext sc)
			throws
			IOException,SAXException,DeviceRoleException,
			DDSExecutionException {

		String method = "createPowerControllerDriver()";
		log.invoke(method);

		DDSExecutor ddse = createDeviceDriver(ddsxml,ldv,sc);
		PowerController pc = new PowerController(ddse);

		log.ret(method);
		return(pc);
	}

	// FIXME: What to throw...
	public TerminalServer createTerminalServerDriver(byte[] ddsxml,LmDeviceVo ldv,
			SystemContext sc)
			throws
			IOException,SAXException,DeviceRoleException,
			DDSExecutionException {

		String method = "createTerminalServerDriver()";
		log.invoke(method);

		DDSExecutor ddse = createDeviceDriver(ddsxml,ldv,sc);
		TerminalServer ts = new TerminalServer(ddse);

		log.ret(method);
		return(ts);
	}

	// FIXME: What to throw...
	public TftpServer createTftpServerDriver(byte[] ddsxml,LmDeviceVo ldv,
			SystemContext sc)
			throws
			IOException,SAXException,DeviceRoleException,
			DDSExecutionException {

		String method = "createTftpServerDriver()";
		log.invoke(method);

		DDSExecutor ddse = createDeviceDriver(ddsxml,ldv,sc);
		TftpServer ts = new TftpServer(ddse);

		log.ret(method);
		return(ts);
	}

	// FIXME: What to throw...
	public EthernetHub createEthernetHubDriver(byte[] ddsxml,LmDeviceVo ldv,
			SystemContext sc)
			throws
			IOException,SAXException,DeviceRoleException,
			DDSExecutionException {

		String method = "createEthernetHubDriver()";
		log.invoke(method);

		DDSExecutor ddse = createDeviceDriver(ddsxml,ldv,sc);
		EthernetHub eh = new EthernetHub(ddse);

		log.ret(method);
		return(eh);
	}

	public String toString() {
		String s = "DeviceDriverFactory()";
		return(s);
	}

	public void describe() {
		log.describe();
	}

	public static DeviceDriver create(String ddsfilename,LmDeviceVo disVo,SystemContext sc)
			throws IOException,SAXException,DeviceRoleException,
			DDSExecutionException {

		byte[] ddsxml = Util.file2bytes(ddsfilename);
		return(create(ddsxml,disVo,sc));
	}

	public static DeviceDriver create(byte[] ddsxml,LmDeviceVo disVo,SystemContext sc)
			throws IOException,SAXException,DeviceRoleException,
			DDSExecutionException {

		DeviceDriverFactory ddf = new DeviceDriverFactory();
		DeviceRole role = disVo.getDeviceRole();

		if(role == DeviceRole.MATRIX_SWITCH) {
        		return(ddf.createMatrixSwitchDriver(ddsxml,disVo,sc));
		}
		if(role == DeviceRole.MANAGED_DEVICE) {
        		return(ddf.createManagedDeviceDriver(ddsxml,disVo,sc));
		}
		if(role == DeviceRole.POWER_CONTROLLER) {
        		return(ddf.createPowerControllerDriver(ddsxml,disVo,sc));
		}
		if(role == DeviceRole.TFTP_SERVER) {
        		return(ddf.createTftpServerDriver(ddsxml,disVo,sc));
		}
		if(role == DeviceRole.TERMINAL_SERVER) {
        		return(ddf.createTerminalServerDriver(ddsxml,disVo,sc));
		}
		if(role == DeviceRole.ETHERNET_HUB) {
        		return(ddf.createEthernetHubDriver(ddsxml,disVo,sc));
		}

		String s = "unknown role '" + role + "'";
		throw(new DeviceRoleException(s));
	}
}
