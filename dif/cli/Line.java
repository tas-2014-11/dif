package com.lumenare.dif.cli;

// TODO: add endsWith() to satisfy IOSPromptDetector

public class Line {
        protected byte elementData[];
        protected int elementCount;

        protected int initialCapacity;
        protected int capacityIncrement;

        public Line() {
                initialCapacity = 64;
                capacityIncrement = 64;
                elementData = new byte[initialCapacity];
                elementCount = 0;
        }

        public Line(String initialData) {
                this();
                add(initialData);
        }

        protected void ensureCapacity(int min) {
                if(elementData.length < min) { grow(); }
        }

        protected void ensureCapacityDelta(int delta) {
                ensureCapacity(elementCount+delta);
        }

        protected void grow() {
                int newCapacity = elementData.length + capacityIncrement;
                capacityIncrement *= 2;

                byte oldData[] = elementData;
                elementData = new byte[newCapacity];
                System.arraycopy(oldData,0,elementData,0,elementCount);

                oldData = null;
        }

        // TODO: think about synchronization
        // TODO: catch ArrayOutOfBoundsException

        public void add(byte b) {
                ensureCapacityDelta(1);
                elementData[elementCount++] = b;
        }

        public void add(byte[] b) {
                ensureCapacityDelta(b.length);
                System.arraycopy(b,0,elementData,elementCount,b.length);
                elementCount += b.length;
        }

        public void add(String str) {
                add(str.getBytes());
        }

        public byte[] get() {
                return(elementData);
        }

        public String toString() {
		// TODO: want a way to freeze Line so we dont have to keep making new strings.
		// TODO: want bytewise compare.
		// TODO: want bytewise contains.
                return(new String(elementData,0,elementCount));
        }

        public int size() {
                return(elementCount);
        }

        public boolean isEmpty() {
                return(size() == 0);
        }

        // Byte-wise compare
        // Is it better to compare Strings or arrays of bytes (or chars) ?
        public boolean equals(String str) {
                if(null == str) { return(false); }

                byte[] b = str.getBytes();
                if(elementCount != b.length) {
                        //System.out.println("equals:elementCount="
                                //+elementCount+":b.length="+b.length);
                        return(false);
                }

                for(int i = 0;i < elementCount;i++) {
                        if(elementData[i] != b[i]) {
                                //System.out.println("equals"+
                                        //":elementData["+i+"]="+elementData[i]+
                                        //":b["+i+"]="+b[i]);
                                return(false);
                        }
                }
                return(true);
        }

        // This is to make JUnit's assertEquals()  happy
        public boolean equals(Object obj) {
                if(obj instanceof String) {
                        return(equals((String)obj));
                }

                return(((Object)this).equals(obj));
        }
}
