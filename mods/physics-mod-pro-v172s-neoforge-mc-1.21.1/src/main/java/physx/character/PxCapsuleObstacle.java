/*
 * Decompiled with CFR 0.152.
 */
package physx.character;

import physx.character.PxObstacle;

public class PxCapsuleObstacle
extends PxObstacle {
    public static final int SIZEOF = PxCapsuleObstacle.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxCapsuleObstacle wrapPointer(long address) {
        return address != 0L ? new PxCapsuleObstacle(address) : null;
    }

    public static PxCapsuleObstacle arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxCapsuleObstacle.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxCapsuleObstacle(long address) {
        super(address);
    }

    public PxCapsuleObstacle() {
        this.address = PxCapsuleObstacle._PxCapsuleObstacle();
    }

    private static native long _PxCapsuleObstacle();

    @Override
    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxCapsuleObstacle._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public float getMHalfHeight() {
        this.checkNotNull();
        return PxCapsuleObstacle._getMHalfHeight(this.address);
    }

    private static native float _getMHalfHeight(long var0);

    public void setMHalfHeight(float value) {
        this.checkNotNull();
        PxCapsuleObstacle._setMHalfHeight(this.address, value);
    }

    private static native void _setMHalfHeight(long var0, float var2);

    public float getMRadius() {
        this.checkNotNull();
        return PxCapsuleObstacle._getMRadius(this.address);
    }

    private static native float _getMRadius(long var0);

    public void setMRadius(float value) {
        this.checkNotNull();
        PxCapsuleObstacle._setMRadius(this.address, value);
    }

    private static native void _setMRadius(long var0, float var2);
}

