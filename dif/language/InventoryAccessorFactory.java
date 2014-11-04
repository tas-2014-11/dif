package com.lumenare.dif.language;

import java.util.List;

import com.lumenare.datastore.device.LmDeviceVo;
import com.lumenare.datastore.device.LmManagementInterfaceVo;

public class InventoryAccessorFactory extends AccessorFactory {
	protected static InventoryAccessor _accessorList[] = {
		new InventoryAccessor_preferredImageName(),
		new InventoryAccessor_ETX(),
		new InventoryAccessor_date(),
		new InventoryAccessor_systemTftpServerMgmtIfIpAddr(),
		new InventoryAccessor_mgmtIfIpAddr(),
		new InventoryAccessor_mgmtIfSubnetMask(),
		new InventoryAccessor_mgmtIfLocation(),
		new InventoryAccessor_mgmtIfGateway(),
		new InventoryAccessor_currentTimeMillis(),
		new InventoryAccessor_defaultImage()
	};

	public InventoryAccessorFactory(String path) throws DDSException {
		super(path);
	}

	// TODO: Fix bogosearch!!!
	public static Accessor _locateAccessor(String path) {
		String method = "locateAccessor(" + path + ")";

		for(int i=0;i<_accessorList.length;i++) {
			InventoryAccessor acc = _accessorList[i];
			if(path.equals(acc.fieldName())) {
				return(acc);
			}
		}
		return(null);
	}

	protected Accessor locateAccessor(String path) {
		return(_locateAccessor(path));
	}

	public static String[] enumerateInventoryAccessors() {
		int n = _accessorList.length;
		String s[] = new String[n];
		for(int i=0;i<n;i++) {
			InventoryAccessor acc = _accessorList[i];
			s[i] = acc.fieldName();
		}
		return(s);
	}
}

/*
abstract class InventoryAccessor extends Accessor {
	public InventoryAccessor() {
		_fieldName = calculateFieldName();
	}

	protected String calculateFieldName() {
		String className = this.getClass().getName();
		String packageName = this.getClass().getPackage().getName();
		int i = packageName.length();
		i += 19;  // strlen("InventoryAccessor_");
		// FIXME:  If somebody mis-names their class then what???  ArrayOutOfBoundsException???
		String baseName = className.substring(i);
		return(baseName);
	}
}
*/

class InventoryAccessor_preferredImageName extends InventoryAccessor {
	public String fetchValue(ExecutionContext ec) {
		String method = "fetchValue(" + ec + ")";

		String preferredImageName = null;
		return preferredImageName;
	}
}

/*
class InventoryAccessor_fileSystems extends InventoryAccessor {
	public String[] fetchValue(ExecutionContext ec) {
		String method = "fetchValue(" + ec + ")";

		if(ec != null) {
			Map fileSystems = ec.getLdv().getFilesystems();
		}
	}
}
*/

class InventoryAccessor_currentTimeMillis extends InventoryAccessor {
	public String fetchValue(ExecutionContext ec) {
		long ms = System.currentTimeMillis();
		return("" + ms);
	}
}

class InventoryAccessor_ETX extends InventoryAccessor {
	public String fetchValue(ExecutionContext ec) {
		return("\3");
	}
}

class InventoryAccessor_date extends InventoryAccessor {
	public String fetchValue(ExecutionContext ec) {
		java.util.Date d = new java.util.Date();
		String s = d.toString();
		return(s);
	}
}

class InventoryAccessor_systemTftpServerMgmtIfIpAddr extends InventoryAccessor {
	public String fetchValue(ExecutionContext ec) {
		SystemContext sc = ec.getSystemContext();
		String s = sc.systemTftpServerMgmtIfIpAddr();
		return(s);
	}
}

/*
class InventoryAccessor_mgmtIfIpAddr extends InventoryAccessor {
	public String fetchValue(ExecutionContext ec) {
		if(null == ec) { return(null); }
		LmDeviceVo ldv = ec.getLdv();
		return(fetchValue(ldv));
	}

	public String fetchValue(LmDeviceVo ldv) {
		return(AccessorUtility.mgmtIfIpAddress(ldv));
	}
}
*/

class InventoryAccessor_mgmtIfSubnetMask extends InventoryAccessor {
	public String fetchValue(ExecutionContext ec) {
		if(null == ec) { return(null); }
		LmDeviceVo ldv = ec.getLdv();
		return(AccessorUtility.mgmtIfSubnetMask(ldv));
	}
}

class InventoryAccessor_mgmtIfLocation extends InventoryAccessor {
	public String fetchValue(ExecutionContext ec) {
		if(null == ec) { return(null); }
		LmDeviceVo ldv = ec.getLdv();
		return(AccessorUtility.mgmtIfLocation(ldv));
	}
}

class InventoryAccessor_mgmtIfGateway extends InventoryAccessor {
	public String fetchValue(ExecutionContext ec) {
		if(null == ec) { return(null); }
		LmDeviceVo ldv = ec.getLdv();
		return(AccessorUtility.mgmtIfGateway(ldv));
	}
}


class InventoryAccessor_defaultImage extends InventoryAccessor {
	public String fetchValue(ExecutionContext ec) {
		// FIXME: Write this!!!
		String s = "c2600-i-mz.120-7.T.bin";
		return(s);
	}
}
