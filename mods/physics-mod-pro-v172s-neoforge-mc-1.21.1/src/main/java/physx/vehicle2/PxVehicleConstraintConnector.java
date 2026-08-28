/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.physics.PxConstraintConnector;
import physx.vehicle2.PxVehiclePhysXConstraintState;

public class PxVehicleConstraintConnector
extends PxConstraintConnector {
    public static final int SIZEOF = PxVehicleConstraintConnector.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxVehicleConstraintConnector wrapPointer(long address) {
        return address != 0L ? new PxVehicleConstraintConnector(address) : null;
    }

    public static PxVehicleConstraintConnector arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxVehicleConstraintConnector.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxVehicleConstraintConnector(long address) {
        super(address);
    }

    public PxVehicleConstraintConnector() {
        this.address = PxVehicleConstraintConnector._PxVehicleConstraintConnector();
    }

    private static native long _PxVehicleConstraintConnector();

    public PxVehicleConstraintConnector(PxVehiclePhysXConstraintState vehicleConstraintState) {
        this.address = PxVehicleConstraintConnector._PxVehicleConstraintConnector(vehicleConstraintState.getAddress());
    }

    private static native long _PxVehicleConstraintConnector(long var0);

    @Override
    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxVehicleConstraintConnector._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public void setConstraintState(PxVehiclePhysXConstraintState constraintState) {
        this.checkNotNull();
        PxVehicleConstraintConnector._setConstraintState(this.address, constraintState.getAddress());
    }

    private static native void _setConstraintState(long var0, long var2);

    @Override
    public void getConstantBlock() {
        this.checkNotNull();
        PxVehicleConstraintConnector._getConstantBlock(this.address);
    }

    private static native void _getConstantBlock(long var0);
}

