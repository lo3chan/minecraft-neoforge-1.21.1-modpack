/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.NativeObject;
import physx.physics.PxConstraint;
import physx.vehicle2.PxVehicleConstraintConnector;
import physx.vehicle2.PxVehiclePhysXConstraintState;

public class PxVehiclePhysXConstraints
extends NativeObject {
    public static final int SIZEOF = PxVehiclePhysXConstraints.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxVehiclePhysXConstraints() {
    }

    private static native int __sizeOf();

    public static PxVehiclePhysXConstraints wrapPointer(long address) {
        return address != 0L ? new PxVehiclePhysXConstraints(address) : null;
    }

    public static PxVehiclePhysXConstraints arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxVehiclePhysXConstraints.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxVehiclePhysXConstraints(long address) {
        super(address);
    }

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxVehiclePhysXConstraints._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxVehiclePhysXConstraintState getConstraintStates(int index) {
        this.checkNotNull();
        return PxVehiclePhysXConstraintState.wrapPointer(PxVehiclePhysXConstraints._getConstraintStates(this.address, index));
    }

    private static native long _getConstraintStates(long var0, int var2);

    public void setConstraintStates(int index, PxVehiclePhysXConstraintState value) {
        this.checkNotNull();
        PxVehiclePhysXConstraints._setConstraintStates(this.address, index, value.getAddress());
    }

    private static native void _setConstraintStates(long var0, int var2, long var3);

    public PxConstraint getConstraints(int index) {
        this.checkNotNull();
        return PxConstraint.wrapPointer(PxVehiclePhysXConstraints._getConstraints(this.address, index));
    }

    private static native long _getConstraints(long var0, int var2);

    public void setConstraints(int index, PxConstraint value) {
        this.checkNotNull();
        PxVehiclePhysXConstraints._setConstraints(this.address, index, value.getAddress());
    }

    private static native void _setConstraints(long var0, int var2, long var3);

    public PxVehicleConstraintConnector getConstraintConnectors(int index) {
        this.checkNotNull();
        return PxVehicleConstraintConnector.wrapPointer(PxVehiclePhysXConstraints._getConstraintConnectors(this.address, index));
    }

    private static native long _getConstraintConnectors(long var0, int var2);

    public void setConstraintConnectors(int index, PxVehicleConstraintConnector value) {
        this.checkNotNull();
        PxVehiclePhysXConstraints._setConstraintConnectors(this.address, index, value.getAddress());
    }

    private static native void _setConstraintConnectors(long var0, int var2, long var3);

    public void setToDefault() {
        this.checkNotNull();
        PxVehiclePhysXConstraints._setToDefault(this.address);
    }

    private static native void _setToDefault(long var0);
}

