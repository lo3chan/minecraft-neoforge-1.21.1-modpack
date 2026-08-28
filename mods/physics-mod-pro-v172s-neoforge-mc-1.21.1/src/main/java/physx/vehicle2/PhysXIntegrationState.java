/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.NativeObject;
import physx.cooking.PxCookingParams;
import physx.physics.PxMaterial;
import physx.physics.PxPhysics;
import physx.vehicle2.BaseVehicleParams;
import physx.vehicle2.PhysXIntegrationParams;
import physx.vehicle2.PxVehiclePhysXActor;
import physx.vehicle2.PxVehiclePhysXConstraints;
import physx.vehicle2.PxVehiclePhysXSteerState;

public class PhysXIntegrationState
extends NativeObject {
    public static final int SIZEOF = PhysXIntegrationState.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PhysXIntegrationState wrapPointer(long address) {
        return address != 0L ? new PhysXIntegrationState(address) : null;
    }

    public static PhysXIntegrationState arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PhysXIntegrationState.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PhysXIntegrationState(long address) {
        super(address);
    }

    public PhysXIntegrationState() {
        this.address = PhysXIntegrationState._PhysXIntegrationState();
    }

    private static native long _PhysXIntegrationState();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PhysXIntegrationState._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxVehiclePhysXActor getPhysxActor() {
        this.checkNotNull();
        return PxVehiclePhysXActor.wrapPointer(PhysXIntegrationState._getPhysxActor(this.address));
    }

    private static native long _getPhysxActor(long var0);

    public void setPhysxActor(PxVehiclePhysXActor value) {
        this.checkNotNull();
        PhysXIntegrationState._setPhysxActor(this.address, value.getAddress());
    }

    private static native void _setPhysxActor(long var0, long var2);

    public PxVehiclePhysXSteerState getPhysxSteerState() {
        this.checkNotNull();
        return PxVehiclePhysXSteerState.wrapPointer(PhysXIntegrationState._getPhysxSteerState(this.address));
    }

    private static native long _getPhysxSteerState(long var0);

    public void setPhysxSteerState(PxVehiclePhysXSteerState value) {
        this.checkNotNull();
        PhysXIntegrationState._setPhysxSteerState(this.address, value.getAddress());
    }

    private static native void _setPhysxSteerState(long var0, long var2);

    public PxVehiclePhysXConstraints getPhysxConstraints() {
        this.checkNotNull();
        return PxVehiclePhysXConstraints.wrapPointer(PhysXIntegrationState._getPhysxConstraints(this.address));
    }

    private static native long _getPhysxConstraints(long var0);

    public void setPhysxConstraints(PxVehiclePhysXConstraints value) {
        this.checkNotNull();
        PhysXIntegrationState._setPhysxConstraints(this.address, value.getAddress());
    }

    private static native void _setPhysxConstraints(long var0, long var2);

    public void destroyState() {
        this.checkNotNull();
        PhysXIntegrationState._destroyState(this.address);
    }

    private static native void _destroyState(long var0);

    public void setToDefault() {
        this.checkNotNull();
        PhysXIntegrationState._setToDefault(this.address);
    }

    private static native void _setToDefault(long var0);

    public void create(BaseVehicleParams baseParams, PhysXIntegrationParams physxParams, PxPhysics physics, PxCookingParams params, PxMaterial defaultMaterial) {
        this.checkNotNull();
        PhysXIntegrationState._create(this.address, baseParams.getAddress(), physxParams.getAddress(), physics.getAddress(), params.getAddress(), defaultMaterial.getAddress());
    }

    private static native void _create(long var0, long var2, long var4, long var6, long var8, long var10);
}

