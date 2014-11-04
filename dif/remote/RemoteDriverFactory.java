package com.lumenare.dif.remote;

import java.util.Map;

import weblogic.rmi.Remote;
import weblogic.rmi.RemoteException;

import com.avulet.db.Key;

import com.lumenare.datastore.common.DeviceRole;
import com.lumenare.datastore.device.LmDeviceVo;
import com.lumenare.dif.BadValueException;
import com.lumenare.dif.language.SystemContext;

public interface RemoteDriverFactory extends Remote {
	public void create(byte[] ddsText,LmDeviceVo disVo,SystemContext sc)
		throws RemoteException,
		NoSuchDriverException,
		BadArgumentException,
		java.io.IOException,
		org.xml.sax.SAXException,
		com.lumenare.dif.manager.DeviceRoleException,
		BadValueException,
		com.lumenare.dif.language.DDSExecutionException;

	public void destroy(Key deviceKey)
		throws RemoteException,
		NoSuchDriverException,BadArgumentException;

	public Map enumerateHack();

	public Map enumerateHack(DeviceRole role);
}
