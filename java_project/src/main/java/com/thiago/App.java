package com.thiago;

import java.lang.foreign.*;
import java.lang.invoke.*;

import com.thiago.helloworld.HelloWorldDotNet;
import com.thiago.helpers.Point;

public class App {
    public static void main(String[] args) {
        System.out.println("Down call to 'strlen' from Java:");
        try {
            final String name = "Thiago";
            final long nameLen = DownCallingStrLen.invokeStrlen(name);
            System.out.printf("'%s' have %d letters\n", name, nameLen);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println("--");

        System.out.print(
                """
                        Down call to 'qsort' and upcall to 'InvokeQsort.Qsort.qsortCompare' from Java:
                        """);
        try {
            int[] sortedArray = InvokeQsort.qsortTest(
                    new int[] { 0, 9, 3, 4, 6, 5, 1, 8, 2, 7 });
            System.out.print("Sorted array:");
            for (int num : sortedArray) {
                System.out.print(num + " ");
            }
            System.out.print("\n");
        } catch (Throwable t) {
            t.printStackTrace();
        }
        System.out.println("--");

        System.out.println("Allocating point structs from Java:");
        Point[] points = new Point[] { new Point(1, 2), new Point(3, 4) };
        CStruct.setNativePointsArray1(points);
        CStruct.setNativePointsArray2(points);
        CStruct.setNativePoint(points[0]);
        System.out.println("Done!");
        System.out.println("--");

        System.out.println("Allocating and printing a string from Java:");
        AllocateAndPrintString.allocateAndPrintCharArray("Hello, World!\n");
        System.out.println("--");

        Arena arena = Arena.openConfined();
        System.out.println("Search for a dll file on the System32 folder:");
        Linker linker = Linker.nativeLinker();
        SymbolLookup dll = SymbolLookup.libraryLookup("crypt32", arena.scope());
        System.out.println("Found!");
        System.out.println("Calling a method from a C# class library");
        System.out.println(HelloWorldDotNet.add(1, 2));
        MemorySegment nativeSeg = arena.allocateUtf8String("Thiago");
        var greet = HelloWorldDotNet.greet(nativeSeg);
        System.out.println(greet.getUtf8String(0));
        arena.close();
    }
}
