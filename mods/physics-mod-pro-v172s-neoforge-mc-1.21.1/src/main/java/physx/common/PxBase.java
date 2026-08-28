/*
 * Decompiled with CFR 0.152.
 */
package physx.common;

import physx.NativeObject;
import physx.common.PxBaseFlagEnum;
import physx.common.PxBaseFlags;

public class PxBase
extends NativeObject {
    public static final int SIZEOF = PxBase.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxBase() {
    }

    private static native int __sizeOf();

    public static PxBase wrapPointer(long address) {
        return address != 0L ? new PxBase(address) : null;
    }

    public static PxBase arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxBase.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxBase(long address) {
        super(address);
    }

    public void release() {
        this.checkNotNull();
        PxBase._release(this.address);
    }

    private static native void _release(long var0);

    public String getConcreteTypeName() {
        this.checkNotNull();
        return PxBase._getConcreteTypeName(this.address);
    }

    private static native String _getConcreteTypeName(long var0);

    public int getConcreteType() {
        this.checkNotNull();
        return PxBase._getConcreteType(this.address);
    }

    private static native int _getConcreteType(long var0);

    public void setBaseFlag(PxBaseFlagEnum flag, boolean value) {
        this.checkNotNull();
        PxBase._setBaseFlag(this.address, flag.value, value);
    }

    private static native void _setBaseFlag(long var0, int var2, boolean var3);

    public void setBaseFlags(PxBaseFlags inFlags) {
        this.checkNotNull();
        PxBase._setBaseFlags(this.address, inFlags.getAddress());
    }

    private static native void _setBaseFlags(long var0, long var2);

    public PxBaseFlags getBaseFlags() {
        this.checkNotNull();
        return PxBaseFlags.wrapPointer(PxBase._getBaseFlags(this.address));
    }

    private static native long _getBaseFlags(long var0);

    public boolean isReleasable() {
        this.checkNotNull();
        return PxBase._isReleasable(this.address);
    }

    private static native boolean _isReleasable(long var0);
}

