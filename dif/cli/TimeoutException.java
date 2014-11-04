package com.lumenare.dif.cli;

public class TimeoutException extends Exception {
        protected long millis;

        public TimeoutException(String s) {
                super("TimeoutException()" + s);
                millis = -1;
        }

        public TimeoutException(long ms) {
                super("TimeoutException(" + ms + ")");
                millis = ms;
        }

        public long getMillis() {
                return(millis);
        }
}
