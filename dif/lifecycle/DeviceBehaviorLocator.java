package com.lumenare.dif.lifecycle;

// FIXME: This class sucks bad.  Lots of null & exceptions.

// Should this have a hook which inserts an externally defined
// database?  So our unittest can handcraft a Properties and
// pass it in and know what to expect instead of relying on
// a properties file?  The answer is yes.
// And maybe that means that a DeviceBehaviorLocator gets
// handed the database against which it performs lookups.
// Then that means we can externally drive database updates
// to support on-the-fly device integration.

// When does new support get added?  How dynamic must this be?

import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import java.lang.reflect.Modifier;
import java.lang.reflect.Field;

import com.avulet.db.Key;
import com.avulet.db.DataDock;
import com.avulet.system.Config;
import com.lumenare.dif.core.AttributeNotFoundException;
import com.lumenare.dif.core.BehaviorNotFoundException;
import com.lumenare.dif.core.DeviceAttributes;
import com.lumenare.dif.core.DeviceBehavior;
import com.lumenare.dif.core.Model;
import com.lumenare.dif.util.Log;

public class DeviceBehaviorLocator {
        public DeviceBehaviorLocator() {
                String logmsg = "DeviceBehaviorLocator()";
                Log.trace(logmsg);
        }

        /*
         * Method for unittest.  This lets us specify a data set to use.
         * Probably not useful in the wild.
         */

        // TODO: inventorProps provides context for this method.
        // TODO: I feel there must be a cleaner way to do this.
        // TODO: Is this version of locate still used?

        public DeviceBehavior locate(Key deviceKey,Properties inventoryProps)
        throws BehaviorNotFoundException {

                String logmsg = toString() + ".locate(" + deviceKey + ")";

                Log.trace(logmsg);
                Log.debug("inventoryProps",inventoryProps);

                DeviceBehavior db = lookupBehavior(deviceKey);

                return(db);
        }

// TODO: when should we read properties?
// TODO: Every locate?  Why not just build a new DeviceBehaviorLocator then.
// TODO: 'Course that might be a lot of database hits.
// TODO: Only in constructor?  That screws you for hot-deploy.
// TODO: Unless hot-deploy can instruct the Czar to re-construct or re-locate.

// TODO: Maybe I should lost this version of locate() and just use the unittest one.

        public DeviceBehavior locate(Key deviceKey)
                        throws BehaviorNotFoundException {

                DeviceBehavior db = lookupBehavior(deviceKey);
                return(db);
        }

        protected Model getModelForKey(Key deviceKey) throws AttributeNotFoundException {
                DeviceAttributes da = new DeviceAttributes(deviceKey);
                return(da.getModel());
        }

        /**
         *
         * Determines the class name which describes device behavior.
         *
         * The selection criteria differ based on whether we
         * are in a virtual lab or a real lab.
         *
         */

        protected String getClassName(Model model) {
                String logmsg = toString() + ".getClassName(" + model + ")";
                Log.trace(logmsg);

                String theClassName;

                if(isVirtualLab()) {
                        theClassName = getClassNameForVirtual();
                        Log.debug(logmsg + ":theClassName=" + theClassName);
                        return(theClassName);
                }

                theClassName = getClassNameForModel(model);
                Log.debug(logmsg + ":theClassName=" + theClassName);

                return(theClassName);
        }

        /**
         *
         * Return class name which describes device behavior
         * for virtual lab.
         *
         * The class name is defined by the system property dif.behavior.virtual
         *
         * If this property is not defined then return SimpleDeviceBehavior.
         *
         * @return the name of the class
         *
         */

        protected String getClassNameForVirtual() {
                String logmsg = toString() + ".getClassNameForVirtual()";
                Log.trace(logmsg);

                String theClassName;

                String propertyName = "dif.behavior.virtual";
                theClassName = Config.get().getProperties().getProperty(propertyName);
                Log.debug(logmsg + ":" + propertyName + "=" + theClassName);

                if(theClassName == null) {
                        theClassName =
                                "com.lumenare.dif.simple.SimpleDeviceBehavior";
                        Log.debug(logmsg + ":" + theClassName);
                }

                return(theClassName);
        }

        protected String getClassNameForModel(Model model) {
                String logmsg = toString() + ".getClassNameForModel(" + model + ")";
                Log.trace(logmsg);

                String className;

                String propertyName = "dif.behavior." + model.getName();
                Log.debug(logmsg + ":" + propertyName);

                className = Config.get().getProperties().getProperty(propertyName);
                Log.debug(logmsg + ":" + className);

                return(className);
        }

        protected Class getClassForClassName(String className)
                        throws
                        LinkageError,
                        ExceptionInInitializerError,
                        ClassNotFoundException {

                String logmsg = toString() + ".getClassForClassName(" + className + ")";
                Log.trace(logmsg);

                Class theClass = Class.forName(className);
                Log.debug(logmsg + ":" + theClass);

                return(theClass);
        }

        public String describeConstructor(Constructor c) {
                try {
                        StringBuffer s = new StringBuffer();

                        int mods = c.getModifiers();  // ick, a bitmask
                        if(mods != 0) {
                                s.append(Modifier.toString(mods));
                                s.append(" ");
                        }

                        //s.append(Field.getTypeName(c.getDeclaringClass()));
                        s.append("(");

                        Class[] params = c.getParameterTypes();
                        for(int i=0;i<params.length;i++) {
                                //s.append(Field.getTypeName(params[i]));
                                if(i < (params.length - 1)) {
                                        s.append(",");
                                }
                        }

                        s.append(")");

                        return(s.toString());
                }
                catch(Exception e) {
                        return("<" + e + ">");
                }
        }

        protected Constructor getTheConstructorIWant(Class theClass)
                        throws
                        NoSuchMethodException,
                        SecurityException {

                String logmsg = toString() + ".getTheConstructorIWant(" + theClass + ")";
                Log.trace(logmsg);

                Class[] argVec = new Class[1];
                argVec[0] = Key.class;

                Constructor constructor = theClass.getConstructor(argVec);
                //Log.debug(logmsg + ":" + constructor);
                Log.debug(logmsg + ":" + describeConstructor(constructor));

                return(constructor);
        }

        protected DeviceBehavior okNowInstantiateTheDamnThing(Constructor constructor,Key deviceKey)
                        throws
                        IllegalAccessException,
                        IllegalArgumentException,
                        InstantiationException,
                        InvocationTargetException,
                        ExceptionInInitializerError {

                String logmsg = toString() + ".okNowInstantiateTheDamnThing("
                                                + constructor + "," + deviceKey + ")";
                Log.trace(logmsg);

                // FIXME: How to avoid the noarg constructor?
                Key[] argv = new Key[1];
                argv[0] = deviceKey;
                DeviceBehavior db = (DeviceBehavior)constructor.newInstance(argv);
                return(db);
        }

        // FIXME: Break out each exception (or throwable).
        // FIXME: Is BNFE the RightThing to throw?

        /*
         *
         * Helper methods to lookupBehavior() may return null.
         * null means that a fatal error occurred in the helper.
         *
         * lookupBehavior() must either return a valid DeviceBehavior
         * or throw.
         */

        protected DeviceBehavior lookupBehavior(Key deviceKey)
                        throws BehaviorNotFoundException {

                String logmsg = toString() + ".lookupBehavior(" + deviceKey + ")";
                Log.trace(logmsg);

                Model theModel;
                try {
                        theModel = getModelForKey(deviceKey);
                }
                catch(AttributeNotFoundException anfe) {
                        Log.error(logmsg,"Could not find model for key:" + deviceKey);
                        throw(new BehaviorNotFoundException(deviceKey,anfe));
                }
                Log.debug(logmsg + ":theModel=" + theModel);

                String theClassName = getClassName(theModel);
                if (null == theClassName) {
                        Log.error(logmsg,"Could not find class name for model:" + theModel);
                        throw(new BehaviorNotFoundException(deviceKey));
                } 

                Class theClass;
                try {
                        theClass = getClassForClassName(theClassName);
                } catch (Exception e1) {
                        Log.error(logmsg,
                                          "Could not find class for class name:" + theClassName,
                                          e1);

                        throw(new BehaviorNotFoundException(deviceKey,e1));
                }
                Log.debug(logmsg + ":theClass=" + theClass);


                Constructor theConstructor;
                try {
                        theConstructor = getTheConstructorIWant(theClass);
                } catch (Exception e2) {
                        Log.error(logmsg,
                                          "Could not find constructor for class:" + theClass,
                                          e2);

                        throw(new BehaviorNotFoundException(deviceKey,e2));
                }
                //Log.debug(logmsg + ":theConstructor=" + theConstructor);
                Log.debug(logmsg + ":theConstructor=" + describeConstructor(theConstructor));


                try {
                        DeviceBehavior db =
                                okNowInstantiateTheDamnThing(theConstructor,deviceKey);
                        return(db);
                } catch (InvocationTargetException ite) {
                        String s = "Could not instantiate:" + theConstructor;
                        Log.error(logmsg,ite);

                        Throwable te = ite.getTargetException();
                        Log.error(logmsg,te);

                        throw(new BehaviorNotFoundException(deviceKey,ite));
                } catch (Exception e3) {
                        Log.error(logmsg,
                                          "Could not instantiate:" + theConstructor,
                                          e3);

                        throw(new BehaviorNotFoundException(deviceKey,e3));
                }
                // FIXME: Maybe catch java.lang.VerifyError (or Throwable) here.
                // FIXME: Just in case something really screws up in the device driver.
        }

        public String toString() {
                String s = "DeviceBehaviorLocator()";
                return(s);
        }

        private boolean isVirtualLab() {
                String logmsg = toString() + ".isVirtualLab() ";
                try {
                        //return(DataDock.get().isLabVirtual());
                    throw new Exception(logmsg + " - virual lab not supported");
                }

                // FIXME: Why would we get an Exception here ???
                catch(Exception e) {
                        Log.error(logmsg,e.getMessage());
                        return(true);
                }
        }
}

//Class[] argVec = new Class[1];
//argVec[0] = dif.Key.class;
//Constructor constructor = theClass.getConstructor(argVec);

//Key[] initargs = new Key[1];
//initargs[0] = deviceKey;
//DeviceBehavior db =
//(DeviceBehavior)constructor.newInstance(initargs);




