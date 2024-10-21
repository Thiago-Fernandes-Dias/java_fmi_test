package com.thiago;

import java.lang.foreign.*;
import java.lang.invoke.*;

public class InvokeQsort {
        
    static class Qsort {
        static int qsortCompare(MemorySegment addr1, MemorySegment addr2) {
            return addr1.get(ValueLayout.JAVA_INT, 0) - addr2.get(ValueLayout.JAVA_INT, 0);
        }
    }
    
    // Obtain instance of native linker
    final static Linker linker = Linker.nativeLinker();
    
    // Create downcall handle for qsort
    final static MethodHandle qsort = linker.downcallHandle(
        linker.defaultLookup().find("qsort").get(),
        FunctionDescriptor.ofVoid(
            ValueLayout.ADDRESS,
            ValueLayout.JAVA_LONG,
            ValueLayout.JAVA_LONG,
            ValueLayout.ADDRESS));

    // A Java description of a C function implemented by a Java method
    final static FunctionDescriptor qsortCompareDesc = FunctionDescriptor.of(
        ValueLayout.JAVA_INT,
        ValueLayout.ADDRESS.asUnbounded(),
        ValueLayout.ADDRESS.asUnbounded());
    // Nova versão:
    // final static FunctionDescriptor qsortCompareDesc = FunctionDescriptor.of(
    //     ValueLayout.JAVA_INT,
    //     ValueLayout.ADDRESS.withTargetLayout(MemoryLayout.sequenceLayout(4, ValueLayout.JAVA_BYTE)),
    //     ValueLayout.ADDRESS.withTargetLayout(MemoryLayout.sequenceLayout(4, ValueLayout.JAVA_BYTE)));

    // Create method handle for qsortCompare
    final static MethodHandle compareHandle;
    static {
        try {   
            compareHandle = MethodHandles.lookup().findStatic(
                InvokeQsort.Qsort.class,
                "qsortCompare",
                qsortCompareDesc.toMethodType());
        } catch (Exception e) {
            throw new AssertionError(
                "Problem creating method handle compareHandle", e);
        }
    }    
    
    static int[] qsortTest(int[] unsortedArray) throws Throwable {
        
        int[] sorted = null;        
        
        try (Arena arena = Arena.openConfined()) {                    
        
            // Allocate off-heap memory and store unsortedArray in it                
            MemorySegment array = arena.allocateArray(
                                          ValueLayout.JAVA_INT,
                                          unsortedArray);        
            // Nova versão:
            // MemorySegment array = arena.allocateFrom(
            //                               ValueLayout.JAVA_INT,
            //                               unsortedArray);        
        
            // Create function pointer for qsortCompare
            MemorySegment compareFunc = linker.upcallStub(
                compareHandle,
                qsortCompareDesc,
                arena.scope());
            // Nova versão:
            // MemorySegment compareFunc = linker.upcallStub(
            //     compareHandle,
            //     qsortCompareDesc,
            //     arena);
                    
            // Call qsort        
            qsort.invoke(array, (long)unsortedArray.length,
                ValueLayout.JAVA_INT.byteSize(), compareFunc);
            
            // Access off-heap memory
            sorted = array.toArray(ValueLayout.JAVA_INT);                
        }
        return sorted;
    }        
}