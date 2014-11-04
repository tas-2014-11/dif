package com.lumenare.dif.cli;

public class Ascii {
        public static final byte NUL = 0;       // ^@
        public static final byte ETX = 3;       // ^C
        public static final byte BEL = 7;       // ^G
        public static final byte LF = 10;       // \n
        public static final byte CR = 13;       // \r
        public static final byte SP = 32;
        public static String byteToString(byte b) {
                switch(b) {
                        case(NUL):      return("NUL");
                        case(ETX):      return("ETX");
                        case(BEL):      return("BEL");
                        case(LF):       return("LF");
                        case(CR):       return("CR");
                        case(SP):       return("SP");
                }
                return("" + b);
        }
}
