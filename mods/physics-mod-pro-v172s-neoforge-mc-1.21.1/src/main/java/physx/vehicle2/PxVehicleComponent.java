/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleComponent
extends NativeObject {
    public static final int SIZEOF = PxVehicleComponent.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxVehicleComponent() {
    }

    private static native int __sizeOf();

    public static PxVehicleComponent wrapPointer(long address) {
        return address != 0L ? new PxVehicleComponent(address) : null;
    }

    public static PxVehicleComponent arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxVehicleComponent.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxVehicleComponent(long address) {
        super(address);
    }

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxVehicleComponent._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);
}

