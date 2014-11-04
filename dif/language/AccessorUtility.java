package com.lumenare.dif.language;

import java.util.List;

import java.util.Enumeration;
import java.util.Hashtable;

import com.lumenare.datastore.attribute.LmAttributeVo;
import com.lumenare.datastore.device.LmDeviceVo;
import com.lumenare.datastore.device.LmManagementInterfaceVo;

public class AccessorUtility {
	protected static final Log log = new Log(new AccessorUtility());

	public static LmManagementInterfaceVo getManagementInterfaceVo(LmDeviceVo ldv) {
		String method = "getManagementInterfaceVo(" + formatLdv(ldv) + ")";

		if(null == ldv) {
			log.warn(method,"LmDeviceVo is null");
			return(null);
		}

                LmManagementInterfaceVo[] locations = ldv.getAllManagementPorts(false);

		if(null == locations) { 
			log.warn(method,"no mgmtInterface found A");
			return(null);
		}

		if(0 == locations.length) {
			log.warn(method,"no mgmtInterface found B");
			return(null);
		}

		if(locations.length > 1) {
			String s = "more than 1 mgmtInterface found";
			log.warn(method,s);
		}

                return locations[0];
	}

	public static String mgmtIfIpAddress(LmDeviceVo ldv) {
		LmManagementInterfaceVo lmiv = AccessorUtility.getManagementInterfaceVo(ldv);
		if(null == lmiv) { return(null); }
		return(lmiv.getIpAddress());
	}

	public static String mgmtIfSubnetMask(LmDeviceVo ldv) {
		LmManagementInterfaceVo lmiv = AccessorUtility.getManagementInterfaceVo(ldv);
		if(null == lmiv) { return(null); }
		return(lmiv.getSubnetMask());
	}

	public static String mgmtIfLocation(LmDeviceVo ldv) {
		LmManagementInterfaceVo lmiv = AccessorUtility.getManagementInterfaceVo(ldv);
		if(null == lmiv) { return(null); }
		return(lmiv.getLocation());
	}

	public static String mgmtIfGateway(LmDeviceVo ldv) {
		LmManagementInterfaceVo lmiv = AccessorUtility.getManagementInterfaceVo(ldv);
		if(null == lmiv) { return(null); }
		return(lmiv.getGateway());
	}

	public String toString() {
		return("AccessorUtility()");
	}

	public static String formatLdv(LmDeviceVo ldv) {
		if(null == ldv) { return("LDVISNULL"); }

		String s = "LmDeviceVo(";
		s += "assettag(" + ldv.getAssetTag() + "),";
		s += "manufacturer(" + ldv.getManufacturer() + "),";
		s += "model(" + ldv.getModel() + "),";
		s += "devicetypeversion(" + ldv.getDeviceTypeVersionId() + "),";
		s += "devicerole(" + ldv.getDeviceRole() + "),";
		s += "issharable(" + ldv.isSharable() + "),";
		s += "ismanaged(" + ldv.isManaged() + "),";
		s += "subdevices(" + ldv.getAllSubDevices(false) + "),";

		s += "name(" + ldv.getName() + "),";
		s += "primarykey(" + ldv.getPrimaryKey() + "),";
		s += "description(" + ldv.getDescription() + "),";
		s += "status(" + ldv.getStatus() + "),";
		s += "location(" + ldv.getLocation() + "),";
		s += "parentdevice(" + ldv.getParentDevice() + "),";
		s += "lab(" + ldv.getLabName() + "),";
		s += "enterprise(" + ldv.getEnterpriseName() + "),";

		s += "attributes(" + ldv.getAllAttributes() + "),";
                LmAttributeVo[] attrs = ldv.getAllAttributes();
                if (attrs!=null) {
                    for (int i=0; i<attrs.length; i++) {
                        s += attrs[i];
                    }
                }
                s += ")";

		return(s);
	}
}

/*
getAssetTag()
getConsoles()
getDeviceRole()
getDeviceTypeVersionId()
getDisplayName()
getFilesystems()
getInterfaces()
getInventoryAttribute(String
getInventoryAttributeNames()
getInventoryAttributes()
getManagementPortLocations()
getManufacturer()
getModel()
getPowerSupplies()
getSubDevice(String
getSubDevices()

getAttribute(String
getAttributeNames()
getAttributes()
getDescription()
getEnterprise()
getLab()
getLocation()
getName()
getParentDevice()
getPrimaryKey()
getStatus()
*/
