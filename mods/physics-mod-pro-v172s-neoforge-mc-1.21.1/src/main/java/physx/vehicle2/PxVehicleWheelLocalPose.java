/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.NativeObject;
import physx.common.PxTransform;

public class PxVehicleWheelLocalPose
extends NativeObject {
    public static final int SIZEOF = PxVehicleWheelLocalPose.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxVehicleWheelLocalPose wrapPointer(long address) {
        return address != 0L ? new PxVehicleWheelLocalPose(address) : null;
    }

    public static PxVehicleWheelLocalPose arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxVehicleWheelLocalPose.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxVehicleWheelLocalPose(long address) {
        super(address);
    }

    public PxVehicleWheelLocalPose() {
        this.address = PxVehicleWheelLocalPose._PxVehicleWheelLocalPose();
    }

    private static native long _PxVehicleWheelLocalPose();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxVehicleWheelLocalPose._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxTransform getLocalPose() {
        this.checkNotNull();
        return PxTransform.wrapPointer(PxVehicleWheelLocalPose._getLocalPose(this.address));
    }

    private static native long _getLocalPose(long var0);

    public void setLocalPose(PxTransform value) {
        this.checkNotNull();
        PxVehicleWheelLocalPose._setLocalPose(this.address, value.getAddress());
    }

    private static native void _setLocalPose(long var0, long var2);

    public void setToDefault() {
        this.checkNotNull();
        PxVehicleWheelLocalPose._setToDefault(this.address);
    }

    private static native void _setToDefault(long var0);
}

