package com.thiago;

import java.lang.foreign.*;

public class AllocateAndPrintString {
    static void allocateAndPrintCharArray(String s) {
        final Arena arena = Arena.openConfined();

        // Allocate off-heap memory
        MemorySegment nativeText = arena.allocateUtf8String(s);
        // Nova versão:
        // MemorySegment nativeText = arena.allocateFrom(s);

        // Access off-heap memory
        for (int i = 0; i < s.length(); i++) {
            System.out.print((char) nativeText.get(ValueLayout.JAVA_BYTE, i));
        }

        arena.close(); // off-heap memory released
    }
}