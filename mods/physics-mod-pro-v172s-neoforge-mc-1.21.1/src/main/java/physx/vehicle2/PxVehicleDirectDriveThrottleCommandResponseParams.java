/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.vehicle2.PxVehicleAxleDescription;
import physx.vehicle2.PxVehicleCommandResponseParams;
import physx.vehicle2.PxVehicleFrame;
import physx.vehicle2.PxVehicleScale;

public class PxVehicleDirectDriveThrottleCommandResponseParams
extends PxVehicleCommandResponseParams {
    public static final int SIZEOF = PxVehicleDirectDriveThrottleCommandResponseParams.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxVehicleDirectDriveThrottleCommandResponseParams wrapPointer(long address) {
        return address != 0L ? new PxVehicleDirectDriveThrottleCommandResponseParams(address) : null;
    }

    public static PxVehicleDirectDriveThrottleCommandResponseParams arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxVehicleDirectDriveThrottleCommandResponseParams.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxVehicleDirectDriveThrottleCommandResponseParams(long address) {
        super(address);
    }

    public PxVehicleDirectDriveThrottleCommandResponseParams() {
        this.address = PxVehicleDirectDriveThrottleCommandResponseParams._PxVehicleDirectDriveThrottleCommandResponseParams();
    }

    private static native long _PxVehicleDirectDriveThrottleCommandResponseParams();

    @Override
    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxVehicleDirectDriveThrottleCommandResponseParams._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxVehicleDirectDriveThrottleCommandResponseParams transformAndScale(PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale) {
        this.checkNotNull();
        return PxVehicleDirectDriveThrottleCommandResponseParams.wrapPointer(PxVehicleDirectDriveThrottleCommandResponseParams._transformAndScale(this.address, srcFrame.getAddress(), trgFrame.getAddress(), srcScale.getAddress(), trgScale.getAddress()));
    }

    private static native long _transformAndScale(long var0, long var2, long var4, long var6, long var8);

    public boolean isValid(PxVehicleAxleDescription axleDesc) {
        this.checkNotNull();
        return PxVehicleDirectDriveThrottleCommandResponseParams._isValid(this.address, axleDesc.getAddress());
    }

    private static native boolean _isValid(long var0, long var2);
}

