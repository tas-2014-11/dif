package com.lumenare.dif.cli;

import java.util.NoSuchElementException;
import java.util.ArrayList;

import com.lumenare.util.logit.LogIt;

public class LineHistoryBlock {
	protected static LogIt _log = LogIt.get(LogIt.TAG_DIF);

        // TODO: think about synchronization

        protected ArrayList blockData;

        public LineHistoryBlock() {
                blockData = new ArrayList();
        }

        // TODO: place some hard limits on history size

        public synchronized void add(Line line) {
                //System.out.println("LineHistoryBlock.add("+line.toString()+")");
                blockData.add(line);
        }

        public synchronized int size() {
                return(blockData.size());
        }

        // TODO: throw something else
        public synchronized String get(int i) throws IndexOutOfBoundsException {
                String s = "";
                Line l = (Line)blockData.get(i);
                if(l != null) {
                        s = l.toString();
                }
                return(s);
        }

        // TODO: throw something else
        public String toString() throws IndexOutOfBoundsException {
                String s = "";
                for(int i = 0;i < size();i++) {
                        s += get(i);
                        s += "\n";
                }
                return(s);
        }

	/*
	public boolean contains(String expected) {
		for(int i=0;i<size();i++) {
			String actualString = get(i);
			int idx = actualString.indexOf(expected);
			if(idx >= 0) {
				String logmsg = toString() + ".contains(" + expected + ")";
				_log.trace(logmsg +
					":LINE_HISTORY_BLOCK_CONTAINS:" + i + ":" + idx);
				return(true);
			}
		}

		return(false);
	}
	*/

	public boolean contains(String expected) {
		return(null != getLineContaining(expected));
	}

	// TODO: efficientify this with better strcmp().
	public String getLineContaining(String expected) {
		for(int i=0;i<size();i++) {
			String actualString = get(i);
			int idx = actualString.indexOf(expected);
			if(idx >= 0) {
				String logmsg = toString() + ".contains(" + expected + ")";
				_log.trace(logmsg +
					":LINE_HISTORY_BLOCK_CONTAINS:" + i + ":" + idx);
				return(actualString);
			}
		}

		return(null);
	}
}
