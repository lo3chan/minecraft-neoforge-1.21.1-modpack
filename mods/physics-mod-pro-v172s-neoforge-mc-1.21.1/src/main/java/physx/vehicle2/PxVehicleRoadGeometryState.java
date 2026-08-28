/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.NativeObject;
import physx.common.PxPlane;
import physx.common.PxVec3;

public class PxVehicleRoadGeometryState
extends NativeObject {
    public static final int SIZEOF = PxVehicleRoadGeometryState.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxVehicleRoadGeometryState wrapPointer(long address) {
        return address != 0L ? new PxVehicleRoadGeometryState(address) : null;
    }

    public static PxVehicleRoadGeometryState arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxVehicleRoadGeometryState.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxVehicleRoadGeometryState(long address) {
        super(address);
    }

    public PxVehicleRoadGeometryState() {
        this.address = PxVehicleRoadGeometryState._PxVehicleRoadGeometryState();
    }

    private static native long _PxVehicleRoadGeometryState();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxVehicleRoadGeometryState._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxPlane getPlane() {
        this.checkNotNull();
        return PxPlane.wrapPointer(PxVehicleRoadGeometryState._getPlane(this.address));
    }

    private static native long _getPlane(long var0);

    public void setPlane(PxPlane value) {
        this.checkNotNull();
        PxVehicleRoadGeometryState._setPlane(this.address, value.getAddress());
    }

    private static native void _setPlane(long var0, long var2);

    public float getFriction() {
        this.checkNotNull();
        return PxVehicleRoadGeometryState._getFriction(this.address);
    }

    private static native float _getFriction(long var0);

    public void setFriction(float value) {
        this.checkNotNull();
        PxVehicleRoadGeometryState._setFriction(this.address, value);
    }

    private static native void _setFriction(long var0, float var2);

    public PxVec3 getVelocity() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxVehicleRoadGeometryState._getVelocity(this.address));
    }

    private static native long _getVelocity(long var0);

    public void setVelocity(PxVec3 value) {
        this.checkNotNull();
        PxVehicleRoadGeometryState._setVelocity(this.address, value.getAddress());
    }

    private static native void _setVelocity(long var0, long var2);

    public boolean getHitState() {
        this.checkNotNull();
        return PxVehicleRoadGeometryState._getHitState(this.address);
    }

    private static native boolean _getHitState(long var0);

    public void setHitState(boolean value) {
        this.checkNotNull();
        PxVehicleRoadGeometryState._setHitState(this.address, value);
    }

    private static native void _setHitState(long var0, boolean var2);

    public void setToDefault() {
        this.checkNotNull();
        PxVehicleRoadGeometryState._setToDefault(this.address);
    }

    private static native void _setToDefault(long var0);
}

