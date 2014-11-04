package com.lumenare.dif.cli;

import java.io.IOException;
import java.net.Socket;
import java.net.InetAddress;

public class CommandLineSocket extends Socket {
        public CommandLineSocket(InetAddress addr,int port) throws IOException {
                super(addr,port);
                setSendBufferSize(1);
                setReceiveBufferSize(1);
                setTcpNoDelay(true);
        }

        public int read() throws IOException {
                return(getInputStream().read());
        }

        public void write(int b) throws IOException {
                getOutputStream().write(b);
                flush();
        }

        public void write(byte[] b) throws IOException {
                getOutputStream().write(b);
                flush();
        }

        public void write(String s) throws IOException {
                write(s.getBytes());
        }

        public void flush() throws IOException {
                getOutputStream().flush();
        }

        public void close() throws IOException {
                flush();
                super.close();
        }

        protected void finalize() {
                try {
                        super.close();
                }
                catch(Exception ee) {
                        ee.printStackTrace();
                }
        }
}
