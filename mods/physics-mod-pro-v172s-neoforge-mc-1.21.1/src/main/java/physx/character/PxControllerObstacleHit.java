/*
 * Decompiled with CFR 0.152.
 */
package physx.character;

import physx.NativeObject;
import physx.character.PxControllerHit;

public class PxControllerObstacleHit
extends PxControllerHit {
    public static final int SIZEOF = PxControllerObstacleHit.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxControllerObstacleHit() {
    }

    private static native int __sizeOf();

    public static PxControllerObstacleHit wrapPointer(long address) {
        return address != 0L ? new PxControllerObstacleHit(address) : null;
    }

    public static PxControllerObstacleHit arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxControllerObstacleHit.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxControllerObstacleHit(long address) {
        super(address);
    }

    @Override
    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxControllerObstacleHit._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public NativeObject getUserData() {
        this.checkNotNull();
        return NativeObject.wrapPointer(PxControllerObstacleHit._getUserData(this.address));
    }

    private static native long _getUserData(long var0);

    public void setUserData(NativeObject value) {
        this.checkNotNull();
        PxControllerObstacleHit._setUserData(this.address, value.getAddress());
    }

    private static native void _setUserData(long var0, long var2);
}

