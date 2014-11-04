package com.lumenare.dif.core;

import com.avulet.db.Key;

import com.avulet.db.DataDock;
import com.avulet.db.DbSqlException;

import com.lumenare.common.domain.element.Device;


// FIXME: Attribute names should be an enumerated type.

// TODO: Create for each attribute a class like this.
// TODO: class DA_deviceName extends State { }
// TODO: And then derive from the class name the key we send to getProperty().
// TODO: This may seem academic but it gives a nice concrete method of adding
// TODO: attributes to the system (in a typesafe way).
// TODO: And it wouldn't extend State, but something like it.

// FIXME: Move Model up to SimpleDeviceAttributes.  Maybe.

public class DeviceAttributes {
        protected Key _deviceKey;
        protected Model _model;

        public DeviceAttributes(Key deviceKey) throws AttributeNotFoundException {
                _deviceKey = deviceKey;
                _model = fetchModel(_deviceKey);
        }

        protected Model fetchModel(Key deviceKey) throws AttributeNotFoundException {
                Attribute a = new Attribute_modelName(deviceKey);
                String modelName = a.getValue();
                return(new Model(modelName));
        }

        public Key getDeviceKey() {
                return(_deviceKey);
        }

        public Model getModel() {
                return(_model);
        }
}

class Attribute_modelName extends Attribute {
        protected Key _deviceKey;

        public Attribute_modelName(Key deviceKey) throws AttributeNotFoundException {
                super();
                _deviceKey = deviceKey;
                _value = fetchValue();
        }

        protected String fetchFromDatadock() throws AttributeNotFoundException {
                try {
                        Device device = DataDock.get().getDevice(_deviceKey);
                        String s = device.getModel();
                        return(s);
                }
                catch(DbSqlException dse) {
                        String s = "Key(" + _deviceKey.getKey() + ")";
                        s += " ";
                        s += dse.getMessage();
                        throw(new AttributeNotFoundException(s));
                }
        }

        protected String calculateKey() {
                String key = getAttributeName();
                key += ".";
                key += _deviceKey.getKey();
                return(key);
        }
}
