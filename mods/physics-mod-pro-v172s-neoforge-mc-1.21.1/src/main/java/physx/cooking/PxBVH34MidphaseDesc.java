/*
 * Decompiled with CFR 0.152.
 */
package physx.cooking;

import physx.NativeObject;

public class PxBVH34MidphaseDesc
extends NativeObject {
    public static final int SIZEOF = PxBVH34MidphaseDesc.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxBVH34MidphaseDesc() {
    }

    private static native int __sizeOf();

    public static PxBVH34MidphaseDesc wrapPointer(long address) {
        return address != 0L ? new PxBVH34MidphaseDesc(address) : null;
    }

    public static PxBVH34MidphaseDesc arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxBVH34MidphaseDesc.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxBVH34MidphaseDesc(long address) {
        super(address);
    }

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxBVH34MidphaseDesc._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public int getNumPrimsPerLeaf() {
        this.checkNotNull();
        return PxBVH34MidphaseDesc._getNumPrimsPerLeaf(this.address);
    }

    private static native int _getNumPrimsPerLeaf(long var0);

    public void setNumPrimsPerLeaf(int value) {
        this.checkNotNull();
        PxBVH34MidphaseDesc._setNumPrimsPerLeaf(this.address, value);
    }

    private static native void _setNumPrimsPerLeaf(long var0, int var2);

    public void setToDefault() {
        this.checkNotNull();
        PxBVH34MidphaseDesc._setToDefault(this.address);
    }

    private static native void _setToDefault(long var0);

    public boolean isValid() {
        this.checkNotNull();
        return PxBVH34MidphaseDesc._isValid(this.address);
    }

    private static native boolean _isValid(long var0);
}

