package com.lumenare.dif;

import java.io.Serializable;

import com.avulet.db.Key;

import com.lumenare.common.domain.attribute.AttributeCollection;

/**
 * Packages a configuration (encoded as an AttributeCollection)
 * with the device it is to be applied to.
 *
 */

public class TargetedConfiguration implements Serializable {
    protected AttributeCollection _configuration;
    protected Key _deviceKey;

    public TargetedConfiguration(AttributeCollection deviceConfiguration, Key deviceKey)
    {
        _configuration = deviceConfiguration;
        _deviceKey = deviceKey;
    }

    public AttributeCollection getConfiguration() {
        return _configuration;
    }

    public Key getDeviceKey() {
        return _deviceKey;
    }

}
