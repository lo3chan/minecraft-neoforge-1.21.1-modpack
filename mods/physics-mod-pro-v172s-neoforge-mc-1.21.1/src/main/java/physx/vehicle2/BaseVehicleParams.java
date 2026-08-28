/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.NativeObject;
import physx.vehicle2.PxVehicleAckermannParams;
import physx.vehicle2.PxVehicleAntiRollForceParams;
import physx.vehicle2.PxVehicleAxleDescription;
import physx.vehicle2.PxVehicleBrakeCommandResponseParams;
import physx.vehicle2.PxVehicleFrame;
import physx.vehicle2.PxVehicleRigidBodyParams;
import physx.vehicle2.PxVehicleScale;
import physx.vehicle2.PxVehicleSteerCommandResponseParams;
import physx.vehicle2.PxVehicleSuspensionComplianceParams;
import physx.vehicle2.PxVehicleSuspensionForceParams;
import physx.vehicle2.PxVehicleSuspensionParams;
import physx.vehicle2.PxVehicleSuspensionStateCalculationParams;
import physx.vehicle2.PxVehicleTireForceParams;
import physx.vehicle2.PxVehicleWheelParams;

public class BaseVehicleParams
extends NativeObject {
    public static final int SIZEOF = BaseVehicleParams.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static BaseVehicleParams wrapPointer(long address) {
        return address != 0L ? new BaseVehicleParams(address) : null;
    }

    public static BaseVehicleParams arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return BaseVehicleParams.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected BaseVehicleParams(long address) {
        super(address);
    }

    public BaseVehicleParams() {
        this.address = BaseVehicleParams._BaseVehicleParams();
    }

    private static native long _BaseVehicleParams();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        BaseVehicleParams._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxVehicleAxleDescription getAxleDescription() {
        this.checkNotNull();
        return PxVehicleAxleDescription.wrapPointer(BaseVehicleParams._getAxleDescription(this.address));
    }

    private static native long _getAxleDescription(long var0);

    public void setAxleDescription(PxVehicleAxleDescription value) {
        this.checkNotNull();
        BaseVehicleParams._setAxleDescription(this.address, value.getAddress());
    }

    private static native void _setAxleDescription(long var0, long var2);

    public PxVehicleFrame getFrame() {
        this.checkNotNull();
        return PxVehicleFrame.wrapPointer(BaseVehicleParams._getFrame(this.address));
    }

    private static native long _getFrame(long var0);

    public void setFrame(PxVehicleFrame value) {
        this.checkNotNull();
        BaseVehicleParams._setFrame(this.address, value.getAddress());
    }

    private static native void _setFrame(long var0, long var2);

    public PxVehicleScale getScale() {
        this.checkNotNull();
        return PxVehicleScale.wrapPointer(BaseVehicleParams._getScale(this.address));
    }

    private static native long _getScale(long var0);

    public void setScale(PxVehicleScale value) {
        this.checkNotNull();
        BaseVehicleParams._setScale(this.address, value.getAddress());
    }

    private static native void _setScale(long var0, long var2);

    public PxVehicleSuspensionStateCalculationParams getSuspensionStateCalculationParams() {
        this.checkNotNull();
        return PxVehicleSuspensionStateCalculationParams.wrapPointer(BaseVehicleParams._getSuspensionStateCalculationParams(this.address));
    }

    private static native long _getSuspensionStateCalculationParams(long var0);

    public void setSuspensionStateCalculationParams(PxVehicleSuspensionStateCalculationParams value) {
        this.checkNotNull();
        BaseVehicleParams._setSuspensionStateCalculationParams(this.address, value.getAddress());
    }

    private static native void _setSuspensionStateCalculationParams(long var0, long var2);

    public PxVehicleBrakeCommandResponseParams getBrakeResponseParams(int index) {
        this.checkNotNull();
        return PxVehicleBrakeCommandResponseParams.wrapPointer(BaseVehicleParams._getBrakeResponseParams(this.address, index));
    }

    private static native long _getBrakeResponseParams(long var0, int var2);

    public void setBrakeResponseParams(int index, PxVehicleBrakeCommandResponseParams value) {
        this.checkNotNull();
        BaseVehicleParams._setBrakeResponseParams(this.address, index, value.getAddress());
    }

    private static native void _setBrakeResponseParams(long var0, int var2, long var3);

    public PxVehicleSteerCommandResponseParams getSteerResponseParams() {
        this.checkNotNull();
        return PxVehicleSteerCommandResponseParams.wrapPointer(BaseVehicleParams._getSteerResponseParams(this.address));
    }

    private static native long _getSteerResponseParams(long var0);

    public void setSteerResponseParams(PxVehicleSteerCommandResponseParams value) {
        this.checkNotNull();
        BaseVehicleParams._setSteerResponseParams(this.address, value.getAddress());
    }

    private static native void _setSteerResponseParams(long var0, long var2);

    public PxVehicleAckermannParams getAckermannParams(int index) {
        this.checkNotNull();
        return PxVehicleAckermannParams.wrapPointer(BaseVehicleParams._getAckermannParams(this.address, index));
    }

    private static native long _getAckermannParams(long var0, int var2);

    public void setAckermannParams(int index, PxVehicleAckermannParams value) {
        this.checkNotNull();
        BaseVehicleParams._setAckermannParams(this.address, index, value.getAddress());
    }

    private static native void _setAckermannParams(long var0, int var2, long var3);

    public PxVehicleSuspensionParams getSuspensionParams(int index) {
        this.checkNotNull();
        return PxVehicleSuspensionParams.wrapPointer(BaseVehicleParams._getSuspensionParams(this.address, index));
    }

    private static native long _getSuspensionParams(long var0, int var2);

    public void setSuspensionParams(int index, PxVehicleSuspensionParams value) {
        this.checkNotNull();
        BaseVehicleParams._setSuspensionParams(this.address, index, value.getAddress());
    }

    private static native void _setSuspensionParams(long var0, int var2, long var3);

    public PxVehicleSuspensionComplianceParams getSuspensionComplianceParams(int index) {
        this.checkNotNull();
        return PxVehicleSuspensionComplianceParams.wrapPointer(BaseVehicleParams._getSuspensionComplianceParams(this.address, index));
    }

    private static native long _getSuspensionComplianceParams(long var0, int var2);

    public void setSuspensionComplianceParams(int index, PxVehicleSuspensionComplianceParams value) {
        this.checkNotNull();
        BaseVehicleParams._setSuspensionComplianceParams(this.address, index, value.getAddress());
    }

    private static native void _setSuspensionComplianceParams(long var0, int var2, long var3);

    public PxVehicleSuspensionForceParams getSuspensionForceParams(int index) {
        this.checkNotNull();
        return PxVehicleSuspensionForceParams.wrapPointer(BaseVehicleParams._getSuspensionForceParams(this.address, index));
    }

    private static native long _getSuspensionForceParams(long var0, int var2);

    public void setSuspensionForceParams(int index, PxVehicleSuspensionForceParams value) {
        this.checkNotNull();
        BaseVehicleParams._setSuspensionForceParams(this.address, index, value.getAddress());
    }

    private static native void _setSuspensionForceParams(long var0, int var2, long var3);

    public PxVehicleAntiRollForceParams getAntiRollForceParams(int index) {
        this.checkNotNull();
        return PxVehicleAntiRollForceParams.wrapPointer(BaseVehicleParams._getAntiRollForceParams(this.address, index));
    }

    private static native long _getAntiRollForceParams(long var0, int var2);

    public void setAntiRollForceParams(int index, PxVehicleAntiRollForceParams value) {
        this.checkNotNull();
        BaseVehicleParams._setAntiRollForceParams(this.address, index, value.getAddress());
    }

    private static native void _setAntiRollForceParams(long var0, int var2, long var3);

    public int getNbAntiRollForceParams() {
        this.checkNotNull();
        return BaseVehicleParams._getNbAntiRollForceParams(this.address);
    }

    private static native int _getNbAntiRollForceParams(long var0);

    public void setNbAntiRollForceParams(int value) {
        this.checkNotNull();
        BaseVehicleParams._setNbAntiRollForceParams(this.address, value);
    }

    private static native void _setNbAntiRollForceParams(long var0, int var2);

    public PxVehicleTireForceParams getTireForceParams(int index) {
        this.checkNotNull();
        return PxVehicleTireForceParams.wrapPointer(BaseVehicleParams._getTireForceParams(this.address, index));
    }

    private static native long _getTireForceParams(long var0, int var2);

    public void setTireForceParams(int index, PxVehicleTireForceParams value) {
        this.checkNotNull();
        BaseVehicleParams._setTireForceParams(this.address, index, value.getAddress());
    }

    private static native void _setTireForceParams(long var0, int var2, long var3);

    public PxVehicleWheelParams getWheelParams(int index) {
        this.checkNotNull();
        return PxVehicleWheelParams.wrapPointer(BaseVehicleParams._getWheelParams(this.address, index));
    }

    private static native long _getWheelParams(long var0, int var2);

    public void setWheelParams(int index, PxVehicleWheelParams value) {
        this.checkNotNull();
        BaseVehicleParams._setWheelParams(this.address, index, value.getAddress());
    }

    private static native void _setWheelParams(long var0, int var2, long var3);

    public PxVehicleRigidBodyParams getRigidBodyParams() {
        this.checkNotNull();
        return PxVehicleRigidBodyParams.wrapPointer(BaseVehicleParams._getRigidBodyParams(this.address));
    }

    private static native long _getRigidBodyParams(long var0);

    public void setRigidBodyParams(PxVehicleRigidBodyParams value) {
        this.checkNotNull();
        BaseVehicleParams._setRigidBodyParams(this.address, value.getAddress());
    }

    private static native void _setRigidBodyParams(long var0, long var2);

    public BaseVehicleParams transformAndScale(PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale) {
        this.checkNotNull();
        return BaseVehicleParams.wrapPointer(BaseVehicleParams._transformAndScale(this.address, srcFrame.getAddress(), trgFrame.getAddress(), srcScale.getAddress(), trgScale.getAddress()));
    }

    private static native long _transformAndScale(long var0, long var2, long var4, long var6, long var8);

    public boolean isValid() {
        this.checkNotNull();
        return BaseVehicleParams._isValid(this.address);
    }

    private static native boolean _isValid(long var0);
}

