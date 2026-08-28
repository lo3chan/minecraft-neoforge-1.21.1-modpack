/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.NativeObject;
import physx.common.PxVec3;

public class PxVehicleSuspensionForce
extends NativeObject {
    public static final int SIZEOF = PxVehicleSuspensionForce.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxVehicleSuspensionForce wrapPointer(long address) {
        return address != 0L ? new PxVehicleSuspensionForce(address) : null;
    }

    public static PxVehicleSuspensionForce arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxVehicleSuspensionForce.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxVehicleSuspensionForce(long address) {
        super(address);
    }

    public PxVehicleSuspensionForce() {
        this.address = PxVehicleSuspensionForce._PxVehicleSuspensionForce();
    }

    private static native long _PxVehicleSuspensionForce();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxVehicleSuspensionForce._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxVec3 getForce() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxVehicleSuspensionForce._getForce(this.address));
    }

    private static native long _getForce(long var0);

    public void setForce(PxVec3 value) {
        this.checkNotNull();
        PxVehicleSuspensionForce._setForce(this.address, value.getAddress());
    }

    private static native void _setForce(long var0, long var2);

    public PxVec3 getTorque() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxVehicleSuspensionForce._getTorque(this.address));
    }

    private static native long _getTorque(long var0);

    public void setTorque(PxVec3 value) {
        this.checkNotNull();
        PxVehicleSuspensionForce._setTorque(this.address, value.getAddress());
    }

    private static native void _setTorque(long var0, long var2);

    public float getNormalForce() {
        this.checkNotNull();
        return PxVehicleSuspensionForce._getNormalForce(this.address);
    }

    private static native float _getNormalForce(long var0);

    public void setNormalForce(float value) {
        this.checkNotNull();
        PxVehicleSuspensionForce._setNormalForce(this.address, value);
    }

    private static native void _setNormalForce(long var0, float var2);

    public void setToDefault() {
        this.checkNotNull();
        PxVehicleSuspensionForce._setToDefault(this.address);
    }

    private static native void _setToDefault(long var0);
}

