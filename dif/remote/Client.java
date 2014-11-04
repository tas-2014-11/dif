package com.lumenare.dif.remote;

import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Properties;

import weblogic.rmi.Naming;
import weblogic.rmi.Remote;

import com.lumenare.datastore.device.LmDeviceVo;
import com.lumenare.dif.DifApplication;
import com.lumenare.dif.Util;
import com.lumenare.dif.language.SystemContext;

import com.avulet.db.Key;
import com.avulet.db.KeyImpl;

import com.lumenare.common.domain.attribute.AttributeCollection;
import com.lumenare.common.domain.attribute.adapter.AttributeUtil;

public class Client {
	public static void main(String args[]) {
		Log.init();
		try {
			Log.info("HELLO");

			for(int i=0;i<args.length;i++) {
				Log.info(i + "=" + args[i]);
			}

			new Client(args);
		}
		catch(Exception e) {
			Log.info("WRONGTHING:" + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
		finally {
			Log.info("GOODBYE");
		}
	}

	public static final String _qualifiedRegistryName = "//localhost:41010/" +
		com.lumenare.service.lookup.ServiceLookupCache.DIF_SERVER_RMI;

	public Client(String[] args) throws Exception {
		// FIXME: want to use this, but port number doesn't carry through
		//Remote remote = Naming.lookup(Server._qualifiedRegistryName);
		Remote remote = Naming.lookup(Client._qualifiedRegistryName);

		switch(args.length) {
			case(3):
				Client_base.dir(args[2]);
			case(2):
				if(args[0].equals("call"))	{ new Client_executeByName(remote,args[1]); }
				Client_base.dir(args[1]);
			case(1):
				String s = args[0];
				if(s.equals("create"))		{ new Client_create(remote); }
				if(s.equals("connect"))		{ new Client_connect(remote); }
				if(s.equals("sanitycheck"))	{ new Client_sanitycheck(remote); }

				if(s.equals("batchconnect"))	{ new Client_batchconnect(remote); }
				if(s.equals("batchdisconnect"))	{ new Client_batchdisconnect(remote); }

				if(s.equals("configure"))	{ new Client_configure(remote); }
				break;
			default:
				throw(new Exception("BADARGS"));
		}
	}
}

class Client_base {
	static protected String _dir = "";

	static public void dir(String dir) {
		_dir = dir;
		Log.info("Client_base._dir=" + _dir);
	}

	static protected byte[] fetchDds() throws Exception {
		return(Util.file2bytes(_dir + "dds.xml"));
	}

	static protected LmDeviceVo fetchDis() throws Exception {
		LmDeviceVo dis = DifApplication.fetchDis(_dir + "dis.xml");
		dis.setPrimaryKey(dis.getAssetTag());
		dis.setEnterpriseName("enterprise");
		dis.setLabName("lab");
		return(dis);
	}

	static protected SystemContext fetchSc() throws Exception {
		return(DifApplication.initSystemContext(_dir + "sc.txt"));
	}

	static protected Key fetchKey() throws Exception {
		LmDeviceVo dis = fetchDis();
		Key key = new KeyImpl(dis.getAssetTag());
		return(key);
	}

	static protected AttributeCollection fetchArgv() throws Exception {
		AttributeCollection ac = DifApplication.doReadArgs(_dir + "arg.txt");
		return(ac);
	}

	static protected String fetchArg(AttributeCollection argv,String name) {
		String value = AttributeUtil.getStringAttributeStringValue(argv,name);
		return(value);
	}
}

class Client_create extends Client_base {
	public Client_create(Remote remote) throws Exception {
		byte[] dds = fetchDds();

		LmDeviceVo dis = fetchDis();

		SystemContext sc = fetchSc();

		RemoteDriverFactory rdf = (RemoteDriverFactory)remote;
		rdf.create(dds,dis,sc);
	}
}

class Client_connect extends Client_base {
	public Client_connect(Remote remote) throws Exception {
		Key key = fetchKey();

		AttributeCollection argv = fetchArgv();

		String left = fetchArg(argv,"left");
		String right = fetchArg(argv,"right");

		Link link = new Link(left,right);

		String lmstftploc = fetchArg(argv,"lmstftploc");
		if(null != lmstftploc) {
			link.decorate("lmstftploc",lmstftploc);
		}

		TargetedLink[] tl = new TargetedLink[1];
		tl[0] = new TargetedLink(key,link);

		RemoteMatrixSwitch rms = (RemoteMatrixSwitch)remote;
		rms.connect(tl);
	}
}

class Client_configure extends Client_base {
	public Client_configure(Remote remote) throws Exception {
		TargetedAttributeCollection[] tac = new TargetedAttributeCollection[1];

		Key key = fetchKey();
		AttributeCollection ac = fetchArgv();
		tac[0] = new TargetedAttributeCollection(key,ac);

		RemoteManagedDevice rmd = (RemoteManagedDevice)remote;
		rmd.configure(tac);
	}
}

class Client_executeByName extends Client_base {
	public Client_executeByName(Remote remote,String functionName) throws Exception {
		Log.info("Client_executeByName(" + remote + "," + functionName + ")");
		Key key = fetchKey();
		AttributeCollection ac = fetchArgv();
		RemoteDriver rd = (RemoteDriver)remote;
		rd.executeByName(key,functionName,ac);
	}
}

class Client_batchconnect extends Client_base {
	public Client_batchconnect(Remote remote) throws Exception {
		TargetedLink[] tl = LinkjobFactory.readLinkjob();
		RemoteMatrixSwitch rms = (RemoteMatrixSwitch)remote;
		rms.connect(tl);
	}
}

class Client_batchdisconnect extends Client_base {
	public Client_batchdisconnect(Remote remote) throws Exception {
		TargetedLink[] tl = LinkjobFactory.readLinkjob();
		RemoteMatrixSwitch rms = (RemoteMatrixSwitch)remote;
		rms.disconnect(tl);
	}
}

class Client_sanitycheck extends Client_base {
	public Client_sanitycheck(Remote remote) throws Exception {
		Key key = fetchKey();

		RemoteDriver rd = (RemoteDriver)remote;
		rd.sanityCheck(key);
	}
}

class LinkjobFactory {
	protected static TargetedLink[] readLinkjob() throws Exception {
		Properties props = new Properties();
		props.load(new java.io.FileInputStream("linkjob.txt"));

		Collection values = props.values();

		int j = 0;
		TargetedLink tl[] = new TargetedLink[values.size()];

		for(Iterator i = values.iterator();i.hasNext();) {
			String value = (String)i.next();
			tl[j++] = parseOneLinkjob(value);
		}

		return(tl);
	}

	protected static TargetedLink parseOneLinkjob(String s) throws Exception {
		String[] tokens = tokenizeOneLinkjob(s);

		Key key = new KeyImpl(tokens[0]);

		String left = tokens[1];
		String right = tokens[2];
		Link link = new Link(left,right);

		if(tokens.length > 3) {
			String name = tokens[3];
			String value = tokens[4];
			link.decorate(name,value);
		}

		return(new TargetedLink(key,link));
	}

	protected static String[] tokenizeOneLinkjob(String s) throws Exception {
		StringTokenizer st = new StringTokenizer(s,".");
		int ntokens = st.countTokens();

		if((ntokens != 3) && (ntokens != 5)) {
			throw(new Exception("PARSEERROR(" + s + ")"));
		}

		String tokens[] = new String[ntokens];
		int i = 0;
		while(st.hasMoreTokens()) {
			tokens[i++] = st.nextToken();
		}
		return(tokens);
	}
}

/*
1       =       6509.3/36.3/48.lmstftploc,3/48
2       =       6509.3/37.3/48.lmstftploc,3/48

3       =       6509.3/15.3/16.lmstftploc,3/48
4       =       6509.3/17.3/18.lmstftploc,3/48
5       =       6509.3/19.3/20.lmstftploc.3/48
6       =       6509.3/21.3/22.lmstftploc.3/48
7       =       6509.3/23.3/24.lmstftploc.3/48

8       =       2850.0104C11.0104C12
9       =       2850.0104C13.0104C14
A       =       2850.0104C15.0104C16
B       =       2850.0104C17.0104C18
C       =       2850.0104C19.0104C20
*/
