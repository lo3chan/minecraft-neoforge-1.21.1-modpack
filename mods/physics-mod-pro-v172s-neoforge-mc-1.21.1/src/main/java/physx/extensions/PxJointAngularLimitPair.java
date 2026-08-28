/*
 * Decompiled with CFR 0.152.
 */
package physx.extensions;

import physx.extensions.PxJointLimitParameters;
import physx.extensions.PxSpring;

public class PxJointAngularLimitPair
extends PxJointLimitParameters {
    public static final int SIZEOF = PxJointAngularLimitPair.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxJointAngularLimitPair() {
    }

    private static native int __sizeOf();

    public static PxJointAngularLimitPair wrapPointer(long address) {
        return address != 0L ? new PxJointAngularLimitPair(address) : null;
    }

    public static PxJointAngularLimitPair arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxJointAngularLimitPair.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxJointAngularLimitPair(long address) {
        super(address);
    }

    public PxJointAngularLimitPair(float lowerLimit, float upperLimit, PxSpring spring) {
        this.address = PxJointAngularLimitPair._PxJointAngularLimitPair(lowerLimit, upperLimit, spring.getAddress());
    }

    private static native long _PxJointAngularLimitPair(float var0, float var1, long var2);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxJointAngularLimitPair._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public float getUpper() {
        this.checkNotNull();
        return PxJointAngularLimitPair._getUpper(this.address);
    }

    private static native float _getUpper(long var0);

    public void setUpper(float value) {
        this.checkNotNull();
        PxJointAngularLimitPair._setUpper(this.address, value);
    }

    private static native void _setUpper(long var0, float var2);

    public float getLower() {
        this.checkNotNull();
        return PxJointAngularLimitPair._getLower(this.address);
    }

    private static native float _getLower(long var0);

    public void setLower(float value) {
        this.checkNotNull();
        PxJointAngularLimitPair._setLower(this.address, value);
    }

    private static native void _setLower(long var0, float var2);
}

