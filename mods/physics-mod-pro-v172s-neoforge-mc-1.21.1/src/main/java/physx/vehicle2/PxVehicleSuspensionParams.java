/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.NativeObject;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.vehicle2.PxVehicleFrame;
import physx.vehicle2.PxVehicleScale;

public class PxVehicleSuspensionParams
extends NativeObject {
    public static final int SIZEOF = PxVehicleSuspensionParams.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxVehicleSuspensionParams wrapPointer(long address) {
        return address != 0L ? new PxVehicleSuspensionParams(address) : null;
    }

    public static PxVehicleSuspensionParams arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxVehicleSuspensionParams.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxVehicleSuspensionParams(long address) {
        super(address);
    }

    public PxVehicleSuspensionParams() {
        this.address = PxVehicleSuspensionParams._PxVehicleSuspensionParams();
    }

    private static native long _PxVehicleSuspensionParams();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxVehicleSuspensionParams._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxTransform getSuspensionAttachment() {
        this.checkNotNull();
        return PxTransform.wrapPointer(PxVehicleSuspensionParams._getSuspensionAttachment(this.address));
    }

    private static native long _getSuspensionAttachment(long var0);

    public void setSuspensionAttachment(PxTransform value) {
        this.checkNotNull();
        PxVehicleSuspensionParams._setSuspensionAttachment(this.address, value.getAddress());
    }

    private static native void _setSuspensionAttachment(long var0, long var2);

    public PxVec3 getSuspensionTravelDir() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxVehicleSuspensionParams._getSuspensionTravelDir(this.address));
    }

    private static native long _getSuspensionTravelDir(long var0);

    public void setSuspensionTravelDir(PxVec3 value) {
        this.checkNotNull();
        PxVehicleSuspensionParams._setSuspensionTravelDir(this.address, value.getAddress());
    }

    private static native void _setSuspensionTravelDir(long var0, long var2);

    public float getSuspensionTravelDist() {
        this.checkNotNull();
        return PxVehicleSuspensionParams._getSuspensionTravelDist(this.address);
    }

    private static native float _getSuspensionTravelDist(long var0);

    public void setSuspensionTravelDist(float value) {
        this.checkNotNull();
        PxVehicleSuspensionParams._setSuspensionTravelDist(this.address, value);
    }

    private static native void _setSuspensionTravelDist(long var0, float var2);

    public PxTransform getWheelAttachment() {
        this.checkNotNull();
        return PxTransform.wrapPointer(PxVehicleSuspensionParams._getWheelAttachment(this.address));
    }

    private static native long _getWheelAttachment(long var0);

    public void setWheelAttachment(PxTransform value) {
        this.checkNotNull();
        PxVehicleSuspensionParams._setWheelAttachment(this.address, value.getAddress());
    }

    private static native void _setWheelAttachment(long var0, long var2);

    public PxVehicleSuspensionParams transformAndScale(PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale) {
        this.checkNotNull();
        return PxVehicleSuspensionParams.wrapPointer(PxVehicleSuspensionParams._transformAndScale(this.address, srcFrame.getAddress(), trgFrame.getAddress(), srcScale.getAddress(), trgScale.getAddress()));
    }

    private static native long _transformAndScale(long var0, long var2, long var4, long var6, long var8);

    public boolean isValid() {
        this.checkNotNull();
        return PxVehicleSuspensionParams._isValid(this.address);
    }

    private static native boolean _isValid(long var0);
}

