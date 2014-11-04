package com.lumenare.dif.language;

import com.lumenare.datastore.device.LmDeviceVo;

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
