/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.NativeObject;
import physx.common.PxVec3;
import physx.vehicle2.PxVehicleFrame;
import physx.vehicle2.PxVehicleScale;

public class PxVehicleRigidBodyParams
extends NativeObject {
    public static final int SIZEOF = PxVehicleRigidBodyParams.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxVehicleRigidBodyParams wrapPointer(long address) {
        return address != 0L ? new PxVehicleRigidBodyParams(address) : null;
    }

    public static PxVehicleRigidBodyParams arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxVehicleRigidBodyParams.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxVehicleRigidBodyParams(long address) {
        super(address);
    }

    public PxVehicleRigidBodyParams() {
        this.address = PxVehicleRigidBodyParams._PxVehicleRigidBodyParams();
    }

    private static native long _PxVehicleRigidBodyParams();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxVehicleRigidBodyParams._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public float getMass() {
        this.checkNotNull();
        return PxVehicleRigidBodyParams._getMass(this.address);
    }

    private static native float _getMass(long var0);

    public void setMass(float value) {
        this.checkNotNull();
        PxVehicleRigidBodyParams._setMass(this.address, value);
    }

    private static native void _setMass(long var0, float var2);

    public PxVec3 getMoi() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxVehicleRigidBodyParams._getMoi(this.address));
    }

    private static native long _getMoi(long var0);

    public void setMoi(PxVec3 value) {
        this.checkNotNull();
        PxVehicleRigidBodyParams._setMoi(this.address, value.getAddress());
    }

    private static native void _setMoi(long var0, long var2);

    public PxVehicleRigidBodyParams transformAndScale(PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale) {
        this.checkNotNull();
        return PxVehicleRigidBodyParams.wrapPointer(PxVehicleRigidBodyParams._transformAndScale(this.address, srcFrame.getAddress(), trgFrame.getAddress(), srcScale.getAddress(), trgScale.getAddress()));
    }

    private static native long _transformAndScale(long var0, long var2, long var4, long var6, long var8);

    public boolean isValid() {
        this.checkNotNull();
        return PxVehicleRigidBodyParams._isValid(this.address);
    }

    private static native boolean _isValid(long var0);
}

