/*
 * Decompiled with CFR 0.152.
 */
package physx.common;

import physx.NativeObject;
import physx.common.PxDebugLine;
import physx.common.PxDebugPoint;
import physx.common.PxDebugTriangle;
import physx.common.PxVec3;

public class PxRenderBuffer
extends NativeObject {
    public static final int SIZEOF = PxRenderBuffer.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxRenderBuffer() {
    }

    private static native int __sizeOf();

    public static PxRenderBuffer wrapPointer(long address) {
        return address != 0L ? new PxRenderBuffer(address) : null;
    }

    public static PxRenderBuffer arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxRenderBuffer.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxRenderBuffer(long address) {
        super(address);
    }

    public int getNbPoints() {
        this.checkNotNull();
        return PxRenderBuffer._getNbPoints(this.address);
    }

    private static native int _getNbPoints(long var0);

    public PxDebugPoint getPoints() {
        this.checkNotNull();
        return PxDebugPoint.wrapPointer(PxRenderBuffer._getPoints(this.address));
    }

    private static native long _getPoints(long var0);

    public void addPoint(PxDebugPoint point) {
        this.checkNotNull();
        PxRenderBuffer._addPoint(this.address, point.getAddress());
    }

    private static native void _addPoint(long var0, long var2);

    public int getNbLines() {
        this.checkNotNull();
        return PxRenderBuffer._getNbLines(this.address);
    }

    private static native int _getNbLines(long var0);

    public PxDebugLine getLines() {
        this.checkNotNull();
        return PxDebugLine.wrapPointer(PxRenderBuffer._getLines(this.address));
    }

    private static native long _getLines(long var0);

    public void addLine(PxDebugLine line) {
        this.checkNotNull();
        PxRenderBuffer._addLine(this.address, line.getAddress());
    }

    private static native void _addLine(long var0, long var2);

    public PxDebugLine reserveLines(int nbLines) {
        this.checkNotNull();
        return PxDebugLine.wrapPointer(PxRenderBuffer._reserveLines(this.address, nbLines));
    }

    private static native long _reserveLines(long var0, int var2);

    public PxDebugPoint reservePoints(int nbLines) {
        this.checkNotNull();
        return PxDebugPoint.wrapPointer(PxRenderBuffer._reservePoints(this.address, nbLines));
    }

    private static native long _reservePoints(long var0, int var2);

    public int getNbTriangles() {
        this.checkNotNull();
        return PxRenderBuffer._getNbTriangles(this.address);
    }

    private static native int _getNbTriangles(long var0);

    public PxDebugTriangle getTriangles() {
        this.checkNotNull();
        return PxDebugTriangle.wrapPointer(PxRenderBuffer._getTriangles(this.address));
    }

    private static native long _getTriangles(long var0);

    public void addTriangle(PxDebugTriangle triangle) {
        this.checkNotNull();
        PxRenderBuffer._addTriangle(this.address, triangle.getAddress());
    }

    private static native void _addTriangle(long var0, long var2);

    public void append(PxRenderBuffer other) {
        this.checkNotNull();
        PxRenderBuffer._append(this.address, other.getAddress());
    }

    private static native void _append(long var0, long var2);

    public void clear() {
        this.checkNotNull();
        PxRenderBuffer._clear(this.address);
    }

    private static native void _clear(long var0);

    public void shift(PxVec3 delta) {
        this.checkNotNull();
        PxRenderBuffer._shift(this.address, delta.getAddress());
    }

    private static native void _shift(long var0, long var2);

    public boolean empty() {
        this.checkNotNull();
        return PxRenderBuffer._empty(this.address);
    }

    private static native boolean _empty(long var0);
}

