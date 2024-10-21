package com.thiago;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

public class DownCallingStrLen {
    static long invokeStrlen(String s) throws Throwable {

        try (Arena arena = Arena.openConfined()) {

            // 1. Allocate off-heap memory, and
            // 2. Dereference off-heap memory
            MemorySegment nativeString = arena.allocateUtf8String(s);
            // Nova versão: MemorySegment nativeString = arena.allocateFrom(s);

            // 3. Link and call C function

            // 3a. Obtain an instance of the native linker
            Linker linker = Linker.nativeLinker();

            // 3b. Locate the address of the C function
            SymbolLookup libc = SymbolLookup.libraryLookup("msvcrt", arena.scope());
            MemorySegment strlen_addr = libc.find("strlen").get();

            // 3c. Create a description of the native function signature
            // OBS: platform specific
            FunctionDescriptor strlen_sig = FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS);

            // 3d. Create a downcall handle for the C function
            MethodHandle strlen = linker.downcallHandle(strlen_addr, strlen_sig);

            // 3e. Call the C function directly from Java
            return (long) strlen.invokeExact(nativeString);

            // Nota:
            /**
             * A classe Linker possui métodos restritos, que podem causar crashes na JVM.
             * Por exemplo, se a função downcallHandle() for chamada com um endereço inválido (a função não está
             * carregada na memória) ou com um descritor de função inválido, a JVM vai falhar.
             */
        }
    }

    public static void main(String[] args) {
        try {
            final String name = "Thiago";
            final long nameLen = invokeStrlen(name);
            System.out.printf("'%s' have %d letters", name, nameLen);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
