package com.thiago;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public class PrintfWithJNA {
 // This is the standard, stable way of mapping, which supports extensive
    // customization and mapping of Java to native types.

    public interface CLibrary extends Library {
        CLibrary INSTANCE = (CLibrary)
            Native.load((Platform.isWindows() ? "msvcrt" : "c"),
                                CLibrary.class);

        void printf(String format, Object... args);
    }

    public static void invokePrintf(String s) {
        CLibrary.INSTANCE.printf(s);
    }   
}
