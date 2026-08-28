/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.NativeObject;
import physx.physics.PxQueryFilterCallback;
import physx.physics.PxQueryFilterData;
import physx.vehicle2.PxVehicleFrame;
import physx.vehicle2.PxVehiclePhysXRoadGeometryQueryTypeEnum;
import physx.vehicle2.PxVehicleScale;

public class PxVehiclePhysXRoadGeometryQueryParams
extends NativeObject {
    public static final int SIZEOF = PxVehiclePhysXRoadGeometryQueryParams.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxVehiclePhysXRoadGeometryQueryParams() {
    }

    private static native int __sizeOf();

    public static PxVehiclePhysXRoadGeometryQueryParams wrapPointer(long address) {
        return address != 0L ? new PxVehiclePhysXRoadGeometryQueryParams(address) : null;
    }

    public static PxVehiclePhysXRoadGeometryQueryParams arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxVehiclePhysXRoadGeometryQueryParams.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxVehiclePhysXRoadGeometryQueryParams(long address) {
        super(address);
    }

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxVehiclePhysXRoadGeometryQueryParams._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxVehiclePhysXRoadGeometryQueryTypeEnum getRoadGeometryQueryType() {
        this.checkNotNull();
        return PxVehiclePhysXRoadGeometryQueryTypeEnum.forValue(PxVehiclePhysXRoadGeometryQueryParams._getRoadGeometryQueryType(this.address));
    }

    private static native int _getRoadGeometryQueryType(long var0);

    public void setRoadGeometryQueryType(PxVehiclePhysXRoadGeometryQueryTypeEnum value) {
        this.checkNotNull();
        PxVehiclePhysXRoadGeometryQueryParams._setRoadGeometryQueryType(this.address, value.value);
    }

    private static native void _setRoadGeometryQueryType(long var0, int var2);

    public PxQueryFilterData getDefaultFilterData() {
        this.checkNotNull();
        return PxQueryFilterData.wrapPointer(PxVehiclePhysXRoadGeometryQueryParams._getDefaultFilterData(this.address));
    }

    private static native long _getDefaultFilterData(long var0);

    public void setDefaultFilterData(PxQueryFilterData value) {
        this.checkNotNull();
        PxVehiclePhysXRoadGeometryQueryParams._setDefaultFilterData(this.address, value.getAddress());
    }

    private static native void _setDefaultFilterData(long var0, long var2);

    public PxQueryFilterData getFilterDataEntries() {
        this.checkNotNull();
        return PxQueryFilterData.wrapPointer(PxVehiclePhysXRoadGeometryQueryParams._getFilterDataEntries(this.address));
    }

    private static native long _getFilterDataEntries(long var0);

    public void setFilterDataEntries(PxQueryFilterData value) {
        this.checkNotNull();
        PxVehiclePhysXRoadGeometryQueryParams._setFilterDataEntries(this.address, value != null ? value.getAddress() : 0L);
    }

    private static native void _setFilterDataEntries(long var0, long var2);

    public PxQueryFilterCallback getFilterCallback() {
        this.checkNotNull();
        return PxQueryFilterCallback.wrapPointer(PxVehiclePhysXRoadGeometryQueryParams._getFilterCallback(this.address));
    }

    private static native long _getFilterCallback(long var0);

    public void setFilterCallback(PxQueryFilterCallback value) {
        this.checkNotNull();
        PxVehiclePhysXRoadGeometryQueryParams._setFilterCallback(this.address, value.getAddress());
    }

    private static native void _setFilterCallback(long var0, long var2);

    public PxVehiclePhysXRoadGeometryQueryParams transformAndScale(PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale) {
        this.checkNotNull();
        return PxVehiclePhysXRoadGeometryQueryParams.wrapPointer(PxVehiclePhysXRoadGeometryQueryParams._transformAndScale(this.address, srcFrame.getAddress(), trgFrame.getAddress(), srcScale.getAddress(), trgScale.getAddress()));
    }

    private static native long _transformAndScale(long var0, long var2, long var4, long var6, long var8);

    public boolean isValid() {
        this.checkNotNull();
        return PxVehiclePhysXRoadGeometryQueryParams._isValid(this.address);
    }

    private static native boolean _isValid(long var0);
}

