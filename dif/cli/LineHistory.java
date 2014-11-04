package com.lumenare.dif.cli;

public class LineHistory {
        // TODO: think about synchronization
        protected LineHistoryBlock block;

        public LineHistory() {
                block = new LineHistoryBlock();
        }

        // TODO: place some hard limits on history size

        public synchronized void add(Line line) {
                block.add(line);
        }

        public synchronized LineHistoryBlock get() {
                LineHistoryBlock oldBlock = block;
                block = new LineHistoryBlock();
                return(oldBlock);
        }

        public synchronized int size() {
                return(block.size());
        }
}
