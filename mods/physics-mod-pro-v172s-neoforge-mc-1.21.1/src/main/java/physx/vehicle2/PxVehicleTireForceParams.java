/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.NativeObject;
import physx.vehicle2.PxVehicleFrame;
import physx.vehicle2.PxVehicleScale;

public class PxVehicleTireForceParams
extends NativeObject {
    public static final int SIZEOF = PxVehicleTireForceParams.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxVehicleTireForceParams wrapPointer(long address) {
        return address != 0L ? new PxVehicleTireForceParams(address) : null;
    }

    public static PxVehicleTireForceParams arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxVehicleTireForceParams.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxVehicleTireForceParams(long address) {
        super(address);
    }

    public PxVehicleTireForceParams() {
        this.address = PxVehicleTireForceParams._PxVehicleTireForceParams();
    }

    private static native long _PxVehicleTireForceParams();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxVehicleTireForceParams._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public float getLatStiffX() {
        this.checkNotNull();
        return PxVehicleTireForceParams._getLatStiffX(this.address);
    }

    private static native float _getLatStiffX(long var0);

    public void setLatStiffX(float value) {
        this.checkNotNull();
        PxVehicleTireForceParams._setLatStiffX(this.address, value);
    }

    private static native void _setLatStiffX(long var0, float var2);

    public float getLatStiffY() {
        this.checkNotNull();
        return PxVehicleTireForceParams._getLatStiffY(this.address);
    }

    private static native float _getLatStiffY(long var0);

    public void setLatStiffY(float value) {
        this.checkNotNull();
        PxVehicleTireForceParams._setLatStiffY(this.address, value);
    }

    private static native void _setLatStiffY(long var0, float var2);

    public float getLongStiff() {
        this.checkNotNull();
        return PxVehicleTireForceParams._getLongStiff(this.address);
    }

    private static native float _getLongStiff(long var0);

    public void setLongStiff(float value) {
        this.checkNotNull();
        PxVehicleTireForceParams._setLongStiff(this.address, value);
    }

    private static native void _setLongStiff(long var0, float var2);

    public float getCamberStiff() {
        this.checkNotNull();
        return PxVehicleTireForceParams._getCamberStiff(this.address);
    }

    private static native float _getCamberStiff(long var0);

    public void setCamberStiff(float value) {
        this.checkNotNull();
        PxVehicleTireForceParams._setCamberStiff(this.address, value);
    }

    private static native void _setCamberStiff(long var0, float var2);

    public float getRestLoad() {
        this.checkNotNull();
        return PxVehicleTireForceParams._getRestLoad(this.address);
    }

    private static native float _getRestLoad(long var0);

    public void setRestLoad(float value) {
        this.checkNotNull();
        PxVehicleTireForceParams._setRestLoad(this.address, value);
    }

    private static native void _setRestLoad(long var0, float var2);

    public PxVehicleTireForceParams transformAndScale(PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale) {
        this.checkNotNull();
        return PxVehicleTireForceParams.wrapPointer(PxVehicleTireForceParams._transformAndScale(this.address, srcFrame.getAddress(), trgFrame.getAddress(), srcScale.getAddress(), trgScale.getAddress()));
    }

    private static native long _transformAndScale(long var0, long var2, long var4, long var6, long var8);

    public boolean isValid() {
        this.checkNotNull();
        return PxVehicleTireForceParams._isValid(this.address);
    }

    private static native boolean _isValid(long var0);
}

