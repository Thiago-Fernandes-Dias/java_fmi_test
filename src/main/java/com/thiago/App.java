package com.thiago;

import java.lang.foreign.Arena;
import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;

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
        AllocateAndPrintString.allocateAndPrintCharArray("Hello, World!");
        System.out.println("--");

        System.out.println("Search for a dll file:");
        Arena arena = Arena.openConfined();
        SymbolLookup dll = SymbolLookup.libraryLookup("auditcse.dll", arena.scope());
        // A DLL está na pasta C:\Windows\System32
        System.out.println("Found auditcse!");
        arena.close();
    }
}
