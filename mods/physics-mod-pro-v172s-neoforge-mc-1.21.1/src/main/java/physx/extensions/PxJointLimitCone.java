/*
 * Decompiled with CFR 0.152.
 */
package physx.extensions;

import physx.extensions.PxJointLimitParameters;
import physx.extensions.PxSpring;

public class PxJointLimitCone
extends PxJointLimitParameters {
    public static final int SIZEOF = PxJointLimitCone.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxJointLimitCone() {
    }

    private static native int __sizeOf();

    public static PxJointLimitCone wrapPointer(long address) {
        return address != 0L ? new PxJointLimitCone(address) : null;
    }

    public static PxJointLimitCone arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxJointLimitCone.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxJointLimitCone(long address) {
        super(address);
    }

    public PxJointLimitCone(float yLimitAngle, float zLimitAngle, PxSpring spring) {
        this.address = PxJointLimitCone._PxJointLimitCone(yLimitAngle, zLimitAngle, spring.getAddress());
    }

    private static native long _PxJointLimitCone(float var0, float var1, long var2);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxJointLimitCone._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public float getYAngle() {
        this.checkNotNull();
        return PxJointLimitCone._getYAngle(this.address);
    }

    private static native float _getYAngle(long var0);

    public void setYAngle(float value) {
        this.checkNotNull();
        PxJointLimitCone._setYAngle(this.address, value);
    }

    private static native void _setYAngle(long var0, float var2);

    public float getZAngle() {
        this.checkNotNull();
        return PxJointLimitCone._getZAngle(this.address);
    }

    private static native float _getZAngle(long var0);

    public void setZAngle(float value) {
        this.checkNotNull();
        PxJointLimitCone._setZAngle(this.address, value);
    }

    private static native void _setZAngle(long var0, float var2);
}

