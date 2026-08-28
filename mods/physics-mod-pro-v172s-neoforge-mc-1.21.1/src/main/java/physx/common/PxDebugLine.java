/*
 * Decompiled with CFR 0.152.
 */
package physx.common;

import physx.NativeObject;
import physx.common.PxVec3;

public class PxDebugLine
extends NativeObject {
    public static final int SIZEOF = PxDebugLine.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxDebugLine() {
    }

    private static native int __sizeOf();

    public static PxDebugLine wrapPointer(long address) {
        return address != 0L ? new PxDebugLine(address) : null;
    }

    public static PxDebugLine arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxDebugLine.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxDebugLine(long address) {
        super(address);
    }

    public PxVec3 getPos0() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxDebugLine._getPos0(this.address));
    }

    private static native long _getPos0(long var0);

    public void setPos0(PxVec3 value) {
        this.checkNotNull();
        PxDebugLine._setPos0(this.address, value.getAddress());
    }

    private static native void _setPos0(long var0, long var2);

    public int getColor0() {
        this.checkNotNull();
        return PxDebugLine._getColor0(this.address);
    }

    private static native int _getColor0(long var0);

    public void setColor0(int value) {
        this.checkNotNull();
        PxDebugLine._setColor0(this.address, value);
    }

    private static native void _setColor0(long var0, int var2);

    public PxVec3 getPos1() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxDebugLine._getPos1(this.address));
    }

    private static native long _getPos1(long var0);

    public void setPos1(PxVec3 value) {
        this.checkNotNull();
        PxDebugLine._setPos1(this.address, value.getAddress());
    }

    private static native void _setPos1(long var0, long var2);

    public int getColor1() {
        this.checkNotNull();
        return PxDebugLine._getColor1(this.address);
    }

    private static native int _getColor1(long var0);

    public void setColor1(int value) {
        this.checkNotNull();
        PxDebugLine._setColor1(this.address, value);
    }

    private static native void _setColor1(long var0, int var2);
}

