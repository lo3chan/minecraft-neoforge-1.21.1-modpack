/*
 * Decompiled with CFR 0.152.
 */
package physx.character;

import physx.NativeObject;
import physx.character.PxControllerBehaviorCallback;
import physx.character.PxControllerNonWalkableModeEnum;
import physx.character.PxControllerShapeTypeEnum;
import physx.character.PxExtendedVec3;
import physx.character.PxUserControllerHitReport;
import physx.common.PxVec3;
import physx.physics.PxMaterial;

public class PxControllerDesc
extends NativeObject {
    public static final int SIZEOF = PxControllerDesc.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxControllerDesc() {
    }

    private static native int __sizeOf();

    public static PxControllerDesc wrapPointer(long address) {
        return address != 0L ? new PxControllerDesc(address) : null;
    }

    public static PxControllerDesc arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxControllerDesc.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxControllerDesc(long address) {
        super(address);
    }

    public PxExtendedVec3 getPosition() {
        this.checkNotNull();
        return PxExtendedVec3.wrapPointer(PxControllerDesc._getPosition(this.address));
    }

    private static native long _getPosition(long var0);

    public void setPosition(PxExtendedVec3 value) {
        this.checkNotNull();
        PxControllerDesc._setPosition(this.address, value.getAddress());
    }

    private static native void _setPosition(long var0, long var2);

    public PxVec3 getUpDirection() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxControllerDesc._getUpDirection(this.address));
    }

    private static native long _getUpDirection(long var0);

    public void setUpDirection(PxVec3 value) {
        this.checkNotNull();
        PxControllerDesc._setUpDirection(this.address, value.getAddress());
    }

    private static native void _setUpDirection(long var0, long var2);

    public float getSlopeLimit() {
        this.checkNotNull();
        return PxControllerDesc._getSlopeLimit(this.address);
    }

    private static native float _getSlopeLimit(long var0);

    public void setSlopeLimit(float value) {
        this.checkNotNull();
        PxControllerDesc._setSlopeLimit(this.address, value);
    }

    private static native void _setSlopeLimit(long var0, float var2);

    public float getInvisibleWallHeight() {
        this.checkNotNull();
        return PxControllerDesc._getInvisibleWallHeight(this.address);
    }

    private static native float _getInvisibleWallHeight(long var0);

    public void setInvisibleWallHeight(float value) {
        this.checkNotNull();
        PxControllerDesc._setInvisibleWallHeight(this.address, value);
    }

    private static native void _setInvisibleWallHeight(long var0, float var2);

    public float getMaxJumpHeight() {
        this.checkNotNull();
        return PxControllerDesc._getMaxJumpHeight(this.address);
    }

    private static native float _getMaxJumpHeight(long var0);

    public void setMaxJumpHeight(float value) {
        this.checkNotNull();
        PxControllerDesc._setMaxJumpHeight(this.address, value);
    }

    private static native void _setMaxJumpHeight(long var0, float var2);

    public float getContactOffset() {
        this.checkNotNull();
        return PxControllerDesc._getContactOffset(this.address);
    }

    private static native float _getContactOffset(long var0);

    public void setContactOffset(float value) {
        this.checkNotNull();
        PxControllerDesc._setContactOffset(this.address, value);
    }

    private static native void _setContactOffset(long var0, float var2);

    public float getStepOffset() {
        this.checkNotNull();
        return PxControllerDesc._getStepOffset(this.address);
    }

    private static native float _getStepOffset(long var0);

    public void setStepOffset(float value) {
        this.checkNotNull();
        PxControllerDesc._setStepOffset(this.address, value);
    }

    private static native void _setStepOffset(long var0, float var2);

    public float getDensity() {
        this.checkNotNull();
        return PxControllerDesc._getDensity(this.address);
    }

    private static native float _getDensity(long var0);

    public void setDensity(float value) {
        this.checkNotNull();
        PxControllerDesc._setDensity(this.address, value);
    }

    private static native void _setDensity(long var0, float var2);

    public float getScaleCoeff() {
        this.checkNotNull();
        return PxControllerDesc._getScaleCoeff(this.address);
    }

    private static native float _getScaleCoeff(long var0);

    public void setScaleCoeff(float value) {
        this.checkNotNull();
        PxControllerDesc._setScaleCoeff(this.address, value);
    }

    private static native void _setScaleCoeff(long var0, float var2);

    public float getVolumeGrowth() {
        this.checkNotNull();
        return PxControllerDesc._getVolumeGrowth(this.address);
    }

    private static native float _getVolumeGrowth(long var0);

    public void setVolumeGrowth(float value) {
        this.checkNotNull();
        PxControllerDesc._setVolumeGrowth(this.address, value);
    }

    private static native void _setVolumeGrowth(long var0, float var2);

    public PxUserControllerHitReport getReportCallback() {
        this.checkNotNull();
        return PxUserControllerHitReport.wrapPointer(PxControllerDesc._getReportCallback(this.address));
    }

    private static native long _getReportCallback(long var0);

    public void setReportCallback(PxUserControllerHitReport value) {
        this.checkNotNull();
        PxControllerDesc._setReportCallback(this.address, value.getAddress());
    }

    private static native void _setReportCallback(long var0, long var2);

    public PxControllerBehaviorCallback getBehaviorCallback() {
        this.checkNotNull();
        return PxControllerBehaviorCallback.wrapPointer(PxControllerDesc._getBehaviorCallback(this.address));
    }

    private static native long _getBehaviorCallback(long var0);

    public void setBehaviorCallback(PxControllerBehaviorCallback value) {
        this.checkNotNull();
        PxControllerDesc._setBehaviorCallback(this.address, value.getAddress());
    }

    private static native void _setBehaviorCallback(long var0, long var2);

    public PxControllerNonWalkableModeEnum getNonWalkableMode() {
        this.checkNotNull();
        return PxControllerNonWalkableModeEnum.forValue(PxControllerDesc._getNonWalkableMode(this.address));
    }

    private static native int _getNonWalkableMode(long var0);

    public void setNonWalkableMode(PxControllerNonWalkableModeEnum value) {
        this.checkNotNull();
        PxControllerDesc._setNonWalkableMode(this.address, value.value);
    }

    private static native void _setNonWalkableMode(long var0, int var2);

    public PxMaterial getMaterial() {
        this.checkNotNull();
        return PxMaterial.wrapPointer(PxControllerDesc._getMaterial(this.address));
    }

    private static native long _getMaterial(long var0);

    public void setMaterial(PxMaterial value) {
        this.checkNotNull();
        PxControllerDesc._setMaterial(this.address, value.getAddress());
    }

    private static native void _setMaterial(long var0, long var2);

    public boolean getRegisterDeletionListener() {
        this.checkNotNull();
        return PxControllerDesc._getRegisterDeletionListener(this.address);
    }

    private static native boolean _getRegisterDeletionListener(long var0);

    public void setRegisterDeletionListener(boolean value) {
        this.checkNotNull();
        PxControllerDesc._setRegisterDeletionListener(this.address, value);
    }

    private static native void _setRegisterDeletionListener(long var0, boolean var2);

    public NativeObject getUserData() {
        this.checkNotNull();
        return NativeObject.wrapPointer(PxControllerDesc._getUserData(this.address));
    }

    private static native long _getUserData(long var0);

    public void setUserData(NativeObject value) {
        this.checkNotNull();
        PxControllerDesc._setUserData(this.address, value.getAddress());
    }

    private static native void _setUserData(long var0, long var2);

    public boolean isValid() {
        this.checkNotNull();
        return PxControllerDesc._isValid(this.address);
    }

    private static native boolean _isValid(long var0);

    public PxControllerShapeTypeEnum getType() {
        this.checkNotNull();
        return PxControllerShapeTypeEnum.forValue(PxControllerDesc._getType(this.address));
    }

    private static native int _getType(long var0);
}

