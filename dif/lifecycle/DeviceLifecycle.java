package com.lumenare.dif.lifecycle;

import com.lumenare.common.domain.attribute.AttributeCollection;

import com.avulet.db.Key;
import com.lumenare.dif.core.BehaviorNotFoundException;
import com.lumenare.dif.core.DeviceBehavior;
import com.lumenare.dif.core.DeviceBehaviorException;
import com.lumenare.dif.util.Log;
import com.lumenare.common.domain.attribute.AttributeCollection;

// HERE: The next thing to do is decouple the state machine from the
// behaviors it manages.  Build a messaging layer and a protocol for
// passing data, status, and errors.

// FIXME: refactor to separate states from transitions
// FIXME: refactor to allow for async message passing between the machine and its states

// FIXME: should the methods exposed through these interfaces all synchronize
// FIXME: on a common monitor?  Or should I just break down and stick a queueing
// FIXME: between the transaction requestors and the machine core?
// FIXME: How does that affect internal transitions?  (We'll already hold the object lock.)

// FIXME: If I did decouple the machine from the callers, the setState() call would
// FIXME: send a message to the machine core.  All the calls into DeviceBehavior
// FIXME: would sit on the other side of the wall.

// FIXME: But if we allow multiple participants to modify this machine, do all
// FIXME: need to listen for changes?  Do they care or is that why all transitions throw?

// TODO:  I'll probably wind up passing the DeviceBehavior object into all states.

// FIXME: All these states are conceptually private to this class.  Make them so.

class State_NULL                    extends State { }
class State_ACQUIRE_CONTROL         extends State { }
class State_IDLE                    extends State { }
class State_CONFIGURE               extends State { }
class State_RANDOM_EVENTS_OCCUR		extends State { }
class State_EXTRACT_CONFIGURATION	extends State { }
class State_MEASURE_UTILIZATION		extends State { }
class State_DEAD                    extends State { }

public class DeviceLifecycle implements DLCAutoTransitions,DLCSessionTransitions,DLCMaintTransitions {

    protected DeviceBehavior _db;
    protected Key            _deviceKey;

    // FIXME: make these static or final or something like that
    // TODO: think real hard about strong typing
    // TODO: flyweight?

    protected static final State _stateNull			        = new State_NULL();
    protected static final State _stateAcquireControl	    = new State_ACQUIRE_CONTROL();
    protected static final State _stateIdle			        = new State_IDLE();
    protected static final State _stateConfigure		    = new State_CONFIGURE();
    protected static final State _stateRandomEventsOccur	= new State_RANDOM_EVENTS_OCCUR();
    protected static final State _stateExtractConfiguration	= new State_EXTRACT_CONFIGURATION();
    protected static final State _stateMeasureUtilization	= new State_MEASURE_UTILIZATION();
    protected static final State _stateDead			        = new State_DEAD();

    protected State _state = _stateNull;
    protected void setState(State s) {
        // FIXME: println
        // System.out.println("DeviceLifecycle.setState(" + s + ")");
        _state = s;
    }

    protected State getState() {
        return(_state);
    }

    // TODO: should this throw IllegalTransitionException or something
    // TODO: domain relevant instead?

    // TODO: Should the DeviceBehaviorLocator be global or static?
    // TODO: If it is how to manage database changes? (wrt hot-deploy)

    // TODO: May want to re-locate at each state change.
    // TODO: May need to get a new DBL for each locate.  Depends on concurrency needs.

    // TODO: What if the DBL fails to locate a valid behavior?

    /*
     * This is the constructor you'll usually want to use.
     */

    // FIXME: Fix these Exceptions.


    public DeviceLifecycle(Key deviceKey) throws IllegalTransitionException,  BehaviorNotFoundException, DeviceBehaviorException {

        Log.trace(this.getClass().getName() + "(" + deviceKey + ")");

        // We want to hang onto the deviceKey in case we have to throw.
        _deviceKey = deviceKey;

        DeviceBehaviorLocator dbl = new DeviceBehaviorLocator();
        _db = dbl.locate(_deviceKey);
        XdeviceBirth();
    }

    /*
     * This constructor lets you specify a custom DeviceBehaviorLocator.
     * It's probably only useful for unittest.
     */

    public DeviceLifecycle(Key d,DeviceBehaviorLocator dbl) throws IllegalTransitionException, BehaviorNotFoundException, DeviceBehaviorException {
        Log.trace(this.getClass().getName() + "(" + d + ")");
        _db = dbl.locate(d);
        XdeviceBirth();
    }

    // FIXME: all this "check for valid transition" crap could be driven by data
    // FIXME: but this will make my unit test pass

    public void XdeviceBirth() throws IllegalTransitionException, DeviceBehaviorException {
        Log.trace(toString() + ".XdeviceBirth()");
        validateTransition(_stateNull,_stateAcquireControl);
        ACQUIRE_CONTROL();
    }

    // FIXME: Since any action *could* kick any number of internal (or external?) transitions
    // FIXME: should all actions be prepared to throw IllegalTransitionException.
    // FIXME: Or does the need for this go away when we decouple activities from
    // FIXME: state transitions?

    // FIXME: Would it be useful to have getEstimatedTimeToCompletion()

    // FIXME: A DeviceBehavior is somebody else's code so be smart.

    // FIXME: What is the proper response if a DeviceBehavior method
    // FIXME: screws up (by throwing, for example)?  Should we catch
    // FIXME: and log and carry on, or reinitialize the DeviceBehavior,
    // FIXME: or call the DeviceBehaviorLocator again?

    // FIXME: So what's the difference between a duck?
    // FIXME: Also, what's the difference between IllegalTransitionException
    // FIXME: and DeviceBehaviorException?

    // FIXME: What if some misbehaving device driver has spawned a bunch of
    // FIXME: threads and then it throws unexpectedly.  How do we clean that up?

    // FIXME: Take a look at the runProtected() stuff in JUnit.

    // FIXME: This should throw something different

    protected void ACQUIRE_CONTROL()  throws IllegalTransitionException,DeviceBehaviorException {
        String logmsg = toString() + ".ACQUIRE_CONTROL()";
        Log.trace(logmsg);

        setState(_stateAcquireControl);

        // FIXME: If we can't open a socket to the console
        // FIXME: we get this on the first write.
        // IOSDeviceBehavior(IOSDeviceAttributes()->DeviceAttributes(com.avulet.db.Key(z892)),com.lumenare.dif.ios.Atoms(IOSCommandLine(com.lumenare.lms.tftp.cli.CommandLine@126861f))).acquireControl():java.io.IOException: Broken pipe
        // FIXME: Think about this.
        try {
            _db.acquireControl();
        } catch(DeviceBehaviorException dbe) {
            // These should be all the unusual conditions we've anticipated.
            // FIXME: This doesn't necessarily mean it's misbehaving?

            Log.error(logmsg,"Misbehaving device driver:" + _db,dbe);
            throw(dbe);
        } catch(Throwable t) {
            // If we get one of these that probably means
            // that some device driver has freaked out and
            // may be unstable.

            Log.error(logmsg,"Misbehaving device driver:" + _db,t);

            throw(new DeviceBehaviorException(t));
        }
        XgoToSleep();
    }

    public void XgoToSleep() throws IllegalTransitionException {
        String logmsg = toString() + ".XgoToSleep()";
        Log.trace(logmsg);
        validateTransition(_stateAcquireControl,_stateIdle);
        IDLE();
    }

    protected void IDLE() {
        String logmsg = toString() + ".IDLE()";
        Log.trace(logmsg);
        setState(_stateIdle);
    }

    // TODO: I wonder if this wants to know about the session
    public void XgrabDeviceForSession(AttributeCollection c) throws IllegalTransitionException,DeviceBehaviorException {

        String logmsg = toString() + ".XgrabDeviceForSession(" + c + ")";
        Log.trace(logmsg);
        validateTransition(_stateIdle,_stateConfigure);

        // FIXME: Don't do this!

        
            CONFIGURE(c);
       
        

        // After the device is configured we want to release it
        // for users to interact with.
        XdeviceIsReadyForEvilUsers();
    }

    // FIXME: See discussion around ACQUIRE_CONTROL()

    protected void CONFIGURE(AttributeCollection c) throws DeviceBehaviorException {
        String logmsg = toString() + ".CONFIGURE(" + c + ")";
        Log.trace(logmsg);

        setState(_stateConfigure);

        // FIXME: Think about this.
        try {
            _db.configure(c);
        }
        catch(DeviceBehaviorException dbe) {
            // FIXME: This doesn't necessarily mean it's misbehaving?
            Log.error(logmsg,"Misbehaving device driver:" + _db,dbe);
            throw(dbe);
        }
        catch(Throwable t) {
            Log.error(logmsg,"Misbehaving device driver:" + _db,t);
            throw(new DeviceBehaviorException(t));
        }
    }

    public AttributeCollection XextractConfiguration() throws IllegalTransitionException, DeviceBehaviorException {

        String logmsg = toString() + ".XextractConfiguration()";
        Log.trace(logmsg);

        validateTransition(_stateRandomEventsOccur,_stateExtractConfiguration);

        try {
            AttributeCollection attrCollection;
            attrCollection = EXTRACT_CONFIGURATION();
            return(attrCollection);
        }
        finally {
            // force sane state
            RANDOM_EVENTS_OCCUR();
        }
    }


    protected AttributeCollection EXTRACT_CONFIGURATION() throws DeviceBehaviorException {

        String logmsg = toString() + ".EXTRACT_CONFIGURATION()";
        Log.trace(logmsg);
        setState(_stateExtractConfiguration);

        try {
            AttributeCollection attrCollection;
            attrCollection = _db.extractConfiguration();
            return(attrCollection);
        }
        catch(DeviceBehaviorException dbe) {
            Log.error(logmsg,"Misbehaving device driver:" + _db,dbe);
            throw(new DeviceBehaviorException(dbe));
        }
        catch(Throwable t) {
            Log.error(logmsg,"Misbehaving device driver:" + _db,t);
            throw(new DeviceBehaviorException(t));
        }
    }


    public boolean XmeasureUtilization() throws IllegalTransitionException,DeviceBehaviorException {

        boolean utilized = false;
        String logmsg = toString() + ".XmeasureUtilization()";
        Log.trace(logmsg);

        try {
            validateTransition(_stateRandomEventsOccur,_stateMeasureUtilization);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }

        try {
            utilized = MEASURE_UTILIZATION();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        } finally {
            // force sane state
            RANDOM_EVENTS_OCCUR();
        }
        return utilized;
    }

    protected boolean MEASURE_UTILIZATION() throws DeviceBehaviorException {

        String logmsg = toString() + ".MEASURE_UTILIZATION()";
        Log.trace(logmsg);
        setState(_stateMeasureUtilization);

        try {
            boolean isInUse;
            isInUse = _db.measureUtilization();
            return(isInUse);
        }
        catch(DeviceBehaviorException dbe) {
            // FIXME: This doesn't necessarily mean it's misbehaving?
            Log.error(logmsg,"Misbehaving device driver:" + _db,dbe);
            throw(dbe);
        }
        catch(Throwable t) {
            Log.error(logmsg,"Misbehaving device driver:" + _db,t);
            throw(new DeviceBehaviorException(t));
        }
    }

    public void XdeviceIsReadyForEvilUsers() throws IllegalTransitionException {
        String logmsg = toString() + ".XdeviceIsReadyForEvilUsers()";
        Log.trace(logmsg);
        validateTransition(_stateConfigure,_stateRandomEventsOccur);
        RANDOM_EVENTS_OCCUR();
    }

    protected void RANDOM_EVENTS_OCCUR() {
        String logmsg = toString() + ".RANDOM_EVENTS_OCCUR()";
        Log.trace(logmsg);
        setState(_stateRandomEventsOccur);
    }

    public void XreleaseDeviceFromSession() throws IllegalTransitionException,DeviceBehaviorException {
        String logmsg = toString() + ".XreleaseDeviceFromSession()";
        Log.trace(logmsg);
        validateTransition(_stateRandomEventsOccur,_stateIdle);

        try {
                _db.acquireControl();
                Log.trace(toString() + ".acquireControl()");
        } catch(DeviceBehaviorException dbe) {
            Log.error(logmsg,"Misbehaving device driver:" + _db,dbe);
            throw( new DeviceBehaviorException(dbe));
        }
        IDLE();
    }

    public void XdeviceDeath() throws IllegalTransitionException {
        String logmsg = toString() + ".XdeviceDeath()";
        Log.trace(logmsg);
        validateTransition(_stateIdle,_stateDead);
        DEAD();
    }

    protected void DEAD() {
        String logmsg = toString() + ".DEAD()";
        Log.trace(logmsg);
        setState(_stateDead);
    }
    /******************************************************************/


    // TODO: I think nulls are taken care of by State.equals()
    protected void validateTransition(State from,State to) throws IllegalTransitionException {
        if(!getState().equals(from)) {
            throw(new IllegalTransitionException(getState(),to,_deviceKey));
        }
    }

    public Key getDeviceKey() {
        return(_deviceKey);
    }

    public String toString() {
        String s = "DeviceLifeCycle(";
        s += getState();
        s += ",";
        s += _db;
        s += ")";
        return(s);
    }
}
