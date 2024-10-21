package com.thiago;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SequenceLayout;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

import com.thiago.helpers.Point;

public class CStruct {

    static void setNativePointsArray1(Point[] points) {
        final Arena arena = Arena.openConfined();
        MemorySegment segment = arena.allocate((long) points.length * 8, 1);
        for (int i = 0; i < points.length; i++) {
            segment.setAtIndex(ValueLayout.JAVA_INT, i * 2, (int) points[i].getX());
            segment.setAtIndex(ValueLayout.JAVA_INT, i * 2 + 1, (int) points[i].getY());
        }
        arena.close();
    }

    static void setNativePointsArray2(Point[] points) {
        final Arena arena = Arena.openConfined();

        StructLayout structLayout = MemoryLayout.structLayout(
                ValueLayout.JAVA_INT.withName("x"),
                ValueLayout.JAVA_INT.withName("y"));
        SequenceLayout ptsLayout = MemoryLayout.sequenceLayout(points.length, structLayout);

        PathElement xGroupElement = PathElement.groupElement("x");
        PathElement yGroupElement = PathElement.groupElement("y");
        PathElement seqElement = PathElement.sequenceElement();

        VarHandle xHandle = ptsLayout.varHandle(seqElement, xGroupElement);
        VarHandle yHandle = ptsLayout.varHandle(seqElement, yGroupElement);

        MemorySegment segment = arena.allocate(ptsLayout);

        for (int i = 0; i < ptsLayout.elementCount(); i++) {
            xHandle.set(segment, (long) i, i);
            yHandle.set(segment, (long) i, i);
        }

        arena.close();
    }

    static void setNativePoint(Point point) {
        final Arena arena = Arena.openConfined();
        StructLayout pointStructLayout = MemoryLayout.structLayout(
                ValueLayout.JAVA_INT.withName("x"),
                ValueLayout.JAVA_INT.withName("y"));

        PathElement xGroupElement = PathElement.groupElement("x");
        PathElement yGroupElement = PathElement.groupElement("y");

        VarHandle xHandle = pointStructLayout.varHandle(xGroupElement);
        VarHandle yHandle = pointStructLayout.varHandle(yGroupElement);

        MemorySegment segment = arena.allocate(pointStructLayout);

        xHandle.set(segment, (int) point.getX());
        yHandle.set(segment, (int) point.getY());

        arena.close();
    }
}
