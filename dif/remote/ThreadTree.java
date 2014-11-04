package com.lumenare.dif.remote;

import java.lang.Thread;
import java.lang.ThreadGroup;

public class ThreadTree {
	private ThreadTree() { }
	
	public static void dumpThreadTree() {
		int i;
		Thread currentThread = Thread.currentThread();
		ThreadGroup currentThreadGroup = currentThread.getThreadGroup();

		ThreadGroup rootThreadGroup = currentThreadGroup;
		ThreadGroup tg;
		//find root thread group
		while((tg = rootThreadGroup.getParent()) != null)  {
			rootThreadGroup = tg;
		}

		dumpThreadGroup(rootThreadGroup,"    ");
	}

	public static void dumpThreadGroup(ThreadGroup tg1,String _prefix) {
		int i;
		String prefix = new String(_prefix);

		log(prefix + tg1.toString());

		int nThread = tg1.activeCount();
		int nThreadGroup = tg1.activeGroupCount();

		Thread threadList[] = new Thread[nThread];
		ThreadGroup threadGroupList[] = new ThreadGroup[nThreadGroup];

		i = tg1.enumerate(threadList,false);
		if(i != nThread) {
			//Log.log("1:Huh? " + i + "," + nThread);
			nThread = i;
		}

		i = tg1.enumerate(threadGroupList,false);
		if(i != nThreadGroup) {
			//Log.log("2:Huh?" + "," + nThreadGroup);
			nThreadGroup = i;
		}

		//for(i=0;i<threadList.length;i++) {
		for(i=0;i<nThread;i++) {
			//log("  " + prefix + threadList[i].toString());
			Thread t = threadList[i];
			String s = "  " + prefix + t.toString();
			if(t.isDaemon()) { s += " *"; }
			log(s);
		}

		//for(i=0;i<threadGroupList.length;i++) {
		for(i=0;i<nThreadGroup;i++) {
			dumpThreadGroup(threadGroupList[i],prefix+"    ");
		}
	}

	public static void log(String s) {
		System.out.println(s);
	}
}
