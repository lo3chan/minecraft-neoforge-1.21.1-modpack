/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.NativeObject;
import physx.vehicle2.PxVehicleFixedSizeLookupTableFloat_3;
import physx.vehicle2.PxVehicleFixedSizeLookupTableVec3_3;
import physx.vehicle2.PxVehicleFrame;
import physx.vehicle2.PxVehicleScale;

public class PxVehicleSuspensionComplianceParams
extends NativeObject {
    public static final int SIZEOF = PxVehicleSuspensionComplianceParams.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxVehicleSuspensionComplianceParams wrapPointer(long address) {
        return address != 0L ? new PxVehicleSuspensionComplianceParams(address) : null;
    }

    public static PxVehicleSuspensionComplianceParams arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxVehicleSuspensionComplianceParams.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxVehicleSuspensionComplianceParams(long address) {
        super(address);
    }

    public PxVehicleSuspensionComplianceParams() {
        this.address = PxVehicleSuspensionComplianceParams._PxVehicleSuspensionComplianceParams();
    }

    private static native long _PxVehicleSuspensionComplianceParams();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxVehicleSuspensionComplianceParams._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxVehicleFixedSizeLookupTableFloat_3 getWheelToeAngle() {
        this.checkNotNull();
        return PxVehicleFixedSizeLookupTableFloat_3.wrapPointer(PxVehicleSuspensionComplianceParams._getWheelToeAngle(this.address));
    }

    private static native long _getWheelToeAngle(long var0);

    public void setWheelToeAngle(PxVehicleFixedSizeLookupTableFloat_3 value) {
        this.checkNotNull();
        PxVehicleSuspensionComplianceParams._setWheelToeAngle(this.address, value.getAddress());
    }

    private static native void _setWheelToeAngle(long var0, long var2);

    public PxVehicleFixedSizeLookupTableFloat_3 getWheelCamberAngle() {
        this.checkNotNull();
        return PxVehicleFixedSizeLookupTableFloat_3.wrapPointer(PxVehicleSuspensionComplianceParams._getWheelCamberAngle(this.address));
    }

    private static native long _getWheelCamberAngle(long var0);

    public void setWheelCamberAngle(PxVehicleFixedSizeLookupTableFloat_3 value) {
        this.checkNotNull();
        PxVehicleSuspensionComplianceParams._setWheelCamberAngle(this.address, value.getAddress());
    }

    private static native void _setWheelCamberAngle(long var0, long var2);

    public PxVehicleFixedSizeLookupTableVec3_3 getSuspForceAppPoint() {
        this.checkNotNull();
        return PxVehicleFixedSizeLookupTableVec3_3.wrapPointer(PxVehicleSuspensionComplianceParams._getSuspForceAppPoint(this.address));
    }

    private static native long _getSuspForceAppPoint(long var0);

    public void setSuspForceAppPoint(PxVehicleFixedSizeLookupTableVec3_3 value) {
        this.checkNotNull();
        PxVehicleSuspensionComplianceParams._setSuspForceAppPoint(this.address, value.getAddress());
    }

    private static native void _setSuspForceAppPoint(long var0, long var2);

    public PxVehicleFixedSizeLookupTableVec3_3 getTireForceAppPoint() {
        this.checkNotNull();
        return PxVehicleFixedSizeLookupTableVec3_3.wrapPointer(PxVehicleSuspensionComplianceParams._getTireForceAppPoint(this.address));
    }

    private static native long _getTireForceAppPoint(long var0);

    public void setTireForceAppPoint(PxVehicleFixedSizeLookupTableVec3_3 value) {
        this.checkNotNull();
        PxVehicleSuspensionComplianceParams._setTireForceAppPoint(this.address, value.getAddress());
    }

    private static native void _setTireForceAppPoint(long var0, long var2);

    public PxVehicleSuspensionComplianceParams transformAndScale(PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale) {
        this.checkNotNull();
        return PxVehicleSuspensionComplianceParams.wrapPointer(PxVehicleSuspensionComplianceParams._transformAndScale(this.address, srcFrame.getAddress(), trgFrame.getAddress(), srcScale.getAddress(), trgScale.getAddress()));
    }

    private static native long _transformAndScale(long var0, long var2, long var4, long var6, long var8);

    public boolean isValid() {
        this.checkNotNull();
        return PxVehicleSuspensionComplianceParams._isValid(this.address);
    }

    private static native boolean _isValid(long var0);
}

