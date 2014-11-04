package com.lumenare.dif.remote;

import weblogic.rmi.Remote;
import weblogic.rmi.RemoteException;

import com.avulet.db.Key;
import com.avulet.element.ConsoleLocation;

import com.lumenare.common.domain.attribute.AttributeCollection;

import com.lumenare.dif.language.DDSExecutionException;
import com.lumenare.dif.manager.MissingFunctionException;

public interface RemoteDriver extends Remote {
	public ConsoleLocation getConsoleLocation(Key deviceKey)
		throws
		NoSuchDriverException,BadArgumentException;

	public void sanityCheck(Key deviceKey)
		throws
		NoSuchDriverException,BadArgumentException,
		MissingFunctionException,DDSExecutionException;

	public AttributeCollection executeByName(Key deviceKey,String functionName,AttributeCollection ac)
		throws
		NoSuchDriverException,BadArgumentException,
		MissingFunctionException,DDSExecutionException;
}
