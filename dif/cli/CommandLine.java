package com.lumenare.dif.cli;

import java.io.IOException;
import java.net.InetAddress;

import com.avulet.element.ConsoleLocation;

import com.lumenare.util.logit.LogIt;


public class CommandLine {
	protected CommandLineHelper _commandLine;
	//protected InetAddress _inetAddress;
	//protected int _port;

	private static LogIt _log = LogIt.get(LogIt.TAG_DIF_CLI);

	protected String _description;

	public CommandLine(ConsoleLocation cl) throws IOException {
		this(cl.getInetAddress(),cl.getPort());
	}

	// FIXME: What to throw... What to throw...

	public CommandLine(InetAddress inetAddress,int port) throws IOException {
		_description = "CommandLine(";
		_description += inetAddress.getHostAddress() + ",";
		_description += port + ")";

		_log.trace(_description);

		_commandLine = new CommandLineHelper(inetAddress,port);

		// Save these for toString()
		// in lieu of decent ConsoleLocation.toString()
		//_inetAddress = inetAddress;
		//_port = port;
	}

	public void setPromptDetector(PromptDetector pd) {
		_commandLine.setPromptDetector(pd);
	}

	public void close() {
		String logmsg = toString() + ".close()";
		_log.trace(logmsg);

		if(null == _commandLine) {
			_log.warn(logmsg + ":ALREADYCLOSED");
			return;
		}

		_commandLine.close();
		_commandLine = null;

		// Hopefully nobody will try to read or write after calling close!
	}

	protected void finalize() {
		String logmsg = toString() + ".finalize()";
		_log.trace(logmsg);

		if(null != _commandLine) {
			_log.info(logmsg + ":" + _commandLine);
			close();
		}
	}

	public String toString() {
		return(_description);
	}

	public void sleep(int ms) {
		try {
			Thread.sleep(ms);
		}
		catch(InterruptedException ie) {
			String logmsg = toString() + ".sleep(" + ms + ")";
			_log.warn(logmsg + ":" + ie.toString());
		}
	}

	public void crlf() throws IOException {
		String logmsg = toString() + ".crlf()";
		_log.trace(logmsg);

		_commandLine.write(Ascii.CR);
		_commandLine.write(Ascii.LF);
	}

	public LineHistoryBlock writeAndRead(String s,int ms) throws IOException,TimeoutException {
		String logmsg = toString() + ".writeAndRead(" + s + "," + ms + ")";
		_log.trace(logmsg);

		_commandLine.lazyStartWaitCycle();

		try {
			_commandLine.write(s);

			// FIXME: Do we need both CR and LF ?
			_commandLine.write(Ascii.CR);
			_commandLine.write(Ascii.LF);
		}
		catch(IOException ioe) {
			_log.error(logmsg,ioe);
			throw(ioe);
		}

		try {
			LineHistoryBlock response = _commandLine.lazyReadTilPrompt(ms);
			dumpResponse(response,logmsg);
			_log.trace(logmsg);
			return(response);
		}
		catch(TimeoutException te) {
			_log.debug(logmsg + ":" + te);
			throw(te);
		}
	}

	// FIXME: I wonder how far we should let exceptions like this
	// FIXME: percolate.

	// FIXME: when we log strings we send to a console let's pretty
	// FIXME: them up so we get nice multiline output and everything's printable

	public void write(String s) throws IOException {
		String logmsg = toString() + ".write(" + s + ")";
		_log.trace(logmsg);

		try {
			_commandLine.write(s);

			// FIXME: Do we need both CR and LF ?
			_commandLine.write(Ascii.CR);
			_commandLine.write(Ascii.LF);
		}
		catch(IOException ioe) {
			_log.error(logmsg,ioe);
			throw(ioe);
		}
	}

	public void write(byte b) throws IOException {
		String logmsg = toString() + ".write(";
		logmsg += Ascii.byteToString(b);
		logmsg += ")";
		_log.trace(logmsg);

		try {
			_commandLine.write(b);
		}
		catch(IOException ioe) {
			_log.error(logmsg,ioe);
		}
	}

	public void write(byte b[]) throws IOException {
		String logmsg = toString() + ".write(";
		logmsg += new String(b);
		logmsg += ")";
		_log.trace(logmsg);

		try {
			_commandLine.write(b);
		}
		catch(IOException ioe) {
			_log.error(logmsg,ioe);
		}
	}

	/*
	public LineHistoryBlock read(int ms) throws TimeoutException {
		String logmsg = toString() + ".read(" + ms + ")";
		_log.trace(logmsg);

		try {
			LineHistoryBlock response = _commandLine.readTilPrompt(ms);
			dumpResponse(response,logmsg);
			_log.trace(logmsg);
			return(response);
		}
		catch(TimeoutException te) {
			//_log.error("read(" + ms + ")",te);
			_log.debug(logmsg + ":" + te);
			throw(te);
		}
	}
	*/

	public LineHistoryBlock readFully() {
		String logmsg = toString() + ".readFully()";
		_log.trace(logmsg);

		LineHistoryBlock response = _commandLine.readFully();
		dumpResponse(response,logmsg);

		_log.trace(logmsg);
		return(response);
	}

	public LineHistoryBlock readFully(int ms) {
		String logmsg = toString() + ".readFully(" + ms + ")";
		_log.trace(logmsg);

		sleep(ms);
		LineHistoryBlock response = _commandLine.readFully();
		dumpResponse(response,logmsg);

		_log.trace(logmsg);
		return(response);
	}

	public LineHistoryBlock readFullyWithCurrentLine() {
		String logmsg = toString() + ".readFullyWithCurrentLine()";
		_log.trace(logmsg);

		LineHistoryBlock response = _commandLine.readFully();
		dumpResponse(response,logmsg);

		_log.trace(logmsg);
		return(response);
	}

	public void dumpResponse(LineHistoryBlock response,String msg) {
		for(int j=0;j<response.size();j++) {
			String s = msg + ":";
			s += response.get(j);
			_log.debug(s);
		}
	}

	/*
	public Line getCurrentLine() {
		return(_commandLine.getCurrentLine());
	}
	*/

	/*
	public boolean currentLineContains(String expected) {
		// TODO: move this method to CommandLine.

		// TODO: Hmm.  It feels like this has something in common
		// TODO: with PromptDetector.

		String logmsg = toString() + ".currentLineContains(" + expected + ")";

		Line line = _commandLine.getCurrentLine();
		String actual = line.toString();

		logmsg += ":actual(" + actual + "):";

		int idx = actual.indexOf(expected);
		if(idx >= 0) {
			_log.info(logmsg + "TRUE");
			return(true);
		}
		_log.info(logmsg + "FALSE");
		return(false);
	}
	*/

	// Return on success or throw on failure

	/*
	public void pollForAPrompt(int thisManyTimes,int thisLongBetweenCycles)
	throws TimeoutException,IOException {

		String logmsg = toString() + ".pollForAPrompt("
						+ thisManyTimes + ","
						+ thisLongBetweenCycles + ")";

		_log.trace(logmsg);
		readFully(1000);

		for(int i=0;i<thisManyTimes;i++) {
			crlf();

			try {
				read(thisLongBetweenCycles);

				_log.trace(logmsg);

				// success
				return;
			}
			catch(TimeoutException te) {
				_log.info(logmsg + i);
			}

			readFully(1000);
		}

		_log.trace(logmsg + ":TIMEOUT");

		// if we get to here we've failed
		throw(new TimeoutException(logmsg));
	}
	*/
}

/*
	// Only CR is accepted at 'Press RETURN to get started.'
	// ETX and LF are ignored.
*/


class CommandLineHelper implements Runnable {
	protected CommandLineSocket sock;

	protected final LineHistory history = new LineHistory();

	protected static final LogIt _log = LogIt.get(LogIt.TAG_DIF_CLI);

	protected String _description;

	protected static int _threadIndex = 0;

	public CommandLineHelper(InetAddress addr,int port) throws IOException {
		_description = "CommandLineHelper(";
		_description += addr.getHostAddress();
		_description += ",";
		_description += port;
		_description += ")";

		sock = new CommandLineSocket(addr,port);

		String name = _description + "-" + _threadIndex++;
		Thread reader = new Thread(this,name);
		reader.start();
	}

	public String toString() {
		return(_description);
	}

	public void write(byte b) throws IOException {
		sock.write(b);
	}

	public void write(byte[] b) throws IOException {
		sock.write(b);
	}

	public void write(String s) throws IOException {
		write(s.getBytes());
	}

	public synchronized void close() {
		String logmsg = toString() + ".close()";
		_log.trace(logmsg);

		if(null == sock) {
			_log.warn(logmsg + ":SOCKETALREADYCLOSED");
			return;
		}

		try {
			sock.close();
		}
		catch(Exception ee) {
			_log.info(logmsg,ee.toString());
			_log.error(logmsg,ee);
			//ee.printStackTrace();
		}

		sock = null;
	}

	// kinda redundant with CommandLineSocket.finalize()
	protected void finalize() {
		close();
	}


	protected volatile PromptDetector promptDetector = null;

	public void setPromptDetector(PromptDetector pd) {
		promptDetector = pd;
	}


	protected final DLLatch promptXid = new DLLatch();

	/*
	protected volatile Line currentLine = new Line();

	protected void setCurrentLine(Line line) {
		currentLine = line;
	}

	public Line getCurrentLine() {
		return(currentLine);
	}
	*/


	protected volatile boolean _stripNul = true;

	public void stripNul(boolean b) {
		_stripNul = b;
	}


	protected volatile Line _currentLine = new Line();

	protected final Object _advanceCurrentLineLock = new Object();

	protected void advanceCurrentLine() {
		synchronized(_advanceCurrentLineLock) {
			// TODO: maybe can remove sync from LineHistory()
			history.add(_currentLine);
			_currentLine = new Line();
		}
	}

	public void run() {
		int i;

		try {
			// TODO: think about adding filter strategy here (discard binary)
			while((i = sock.read()) != -1) {
				switch (i) {
				case(10):
				case(13):
					if(_currentLine.isEmpty()) { break; }

					advanceCurrentLine();

					break;
				case(0):
					if(_stripNul) { break; }
				default:
					_currentLine.add((byte)i);

					if(promptDetector != null) {
						if(promptDetector.lineIsPrompt(_currentLine)) {
							promptXid.inc();
						}
					}

					break;
				}
			}
		}
		catch(Exception ee) {
			String logmsg = toString() + ".run()";
			_log.warn(logmsg + ":" + ee.getMessage());
			close();
		}
	}

	/*
	public LineHistoryBlock readTilPrompt(long ms) throws TimeoutException {
		boolean success = promptXid.waitForInc(ms);

		if(!success) {
			throw(new TimeoutException(ms));
		}

		return(history.get());
	}
	*/

	protected void lazyStartWaitCycle() throws TimeoutException {
		promptXid.lazyStartWaitCycle();
	}

	protected LineHistoryBlock lazyReadTilPrompt(long ms) throws TimeoutException {
		boolean success = promptXid.lazyWaitForInc(ms);

		if(!success) {
			throw(new TimeoutException(ms));
		}

		return(readFully());
	}

	public LineHistoryBlock readFully() {
		advanceCurrentLine();
		return(history.get());
	}
}


// SEE ALSO: util.concurrent.Latch
// SEE ALSO: Lea, CPiJSE p187
// TODO: rename and place this class in it's own file (different package too)
// TODO: revisit synch
class DLLatch {
	protected long count_ = 0;
	protected long lastCount_ = 0;

	public synchronized boolean waitForInc(long ms) {
		lastCount_ = count_;

		long waitTime = ms;
		long startTime = System.currentTimeMillis();

		while(waitTime > 0) {
			try {
				wait(waitTime);
			}
			catch(InterruptedException ie) {
				ie.printStackTrace();
			}

			if(count_ > lastCount_) {
				return(true);
			}

			long elapsedTime =
			System.currentTimeMillis() - startTime;
			waitTime = ms - elapsedTime;
		}

		return(false);
	}

	public synchronized void lazyStartWaitCycle() {
		lastCount_ = count_;
	}

	public synchronized boolean lazyWaitForInc(long ms) {
		long waitTime = ms;
		long startTime = System.currentTimeMillis();

		while(waitTime > 0) {
			try {
				wait(waitTime);
			}
			catch(InterruptedException ie) {
				ie.printStackTrace();
			}

			if(count_ > lastCount_) {
				return(true);
			}

			long elapsedTime =
			System.currentTimeMillis() - startTime;
			waitTime = ms - elapsedTime;
		}

		return(false);
	}

	public synchronized void inc() {
		//System.out.println("DLLatch.inc():count_="+count_);
		++count_;
		notifyAll();
	}
}
