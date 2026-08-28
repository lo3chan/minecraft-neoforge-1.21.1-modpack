/*
 * Decompiled with CFR 0.152.
 */
package physx.extensions;

import physx.NativeObject;

public class PxSpring
extends NativeObject {
    public static final int SIZEOF = PxSpring.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxSpring() {
    }

    private static native int __sizeOf();

    public static PxSpring wrapPointer(long address) {
        return address != 0L ? new PxSpring(address) : null;
    }

    public static PxSpring arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxSpring.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxSpring(long address) {
        super(address);
    }

    public PxSpring(float stiffness, float damping) {
        this.address = PxSpring._PxSpring(stiffness, damping);
    }

    private static native long _PxSpring(float var0, float var1);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxSpring._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public float getStiffness() {
        this.checkNotNull();
        return PxSpring._getStiffness(this.address);
    }

    private static native float _getStiffness(long var0);

    public void setStiffness(float value) {
        this.checkNotNull();
        PxSpring._setStiffness(this.address, value);
    }

    private static native void _setStiffness(long var0, float var2);

    public float getDamping() {
        this.checkNotNull();
        return PxSpring._getDamping(this.address);
    }

    private static native float _getDamping(long var0);

    public void setDamping(float value) {
        this.checkNotNull();
        PxSpring._setDamping(this.address, value);
    }

    private static native void _setDamping(long var0, float var2);
}

