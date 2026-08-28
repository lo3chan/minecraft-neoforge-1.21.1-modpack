/*
 * Decompiled with CFR 0.152.
 */
package physx.cooking;

import physx.NativeObject;
import physx.common.PxTolerancesScale;
import physx.cooking.PxConvexMeshCookingTypeEnum;
import physx.cooking.PxMeshPreprocessingFlags;
import physx.cooking.PxMidphaseDesc;

public class PxCookingParams
extends NativeObject {
    public static final int SIZEOF = PxCookingParams.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxCookingParams() {
    }

    private static native int __sizeOf();

    public static PxCookingParams wrapPointer(long address) {
        return address != 0L ? new PxCookingParams(address) : null;
    }

    public static PxCookingParams arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxCookingParams.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxCookingParams(long address) {
        super(address);
    }

    public PxCookingParams(PxTolerancesScale sc) {
        this.address = PxCookingParams._PxCookingParams(sc.getAddress());
    }

    private static native long _PxCookingParams(long var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxCookingParams._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public float getAreaTestEpsilon() {
        this.checkNotNull();
        return PxCookingParams._getAreaTestEpsilon(this.address);
    }

    private static native float _getAreaTestEpsilon(long var0);

    public void setAreaTestEpsilon(float value) {
        this.checkNotNull();
        PxCookingParams._setAreaTestEpsilon(this.address, value);
    }

    private static native void _setAreaTestEpsilon(long var0, float var2);

    public float getPlaneTolerance() {
        this.checkNotNull();
        return PxCookingParams._getPlaneTolerance(this.address);
    }

    private static native float _getPlaneTolerance(long var0);

    public void setPlaneTolerance(float value) {
        this.checkNotNull();
        PxCookingParams._setPlaneTolerance(this.address, value);
    }

    private static native void _setPlaneTolerance(long var0, float var2);

    public PxConvexMeshCookingTypeEnum getConvexMeshCookingType() {
        this.checkNotNull();
        return PxConvexMeshCookingTypeEnum.forValue(PxCookingParams._getConvexMeshCookingType(this.address));
    }

    private static native int _getConvexMeshCookingType(long var0);

    public void setConvexMeshCookingType(PxConvexMeshCookingTypeEnum value) {
        this.checkNotNull();
        PxCookingParams._setConvexMeshCookingType(this.address, value.value);
    }

    private static native void _setConvexMeshCookingType(long var0, int var2);

    public boolean getSuppressTriangleMeshRemapTable() {
        this.checkNotNull();
        return PxCookingParams._getSuppressTriangleMeshRemapTable(this.address);
    }

    private static native boolean _getSuppressTriangleMeshRemapTable(long var0);

    public void setSuppressTriangleMeshRemapTable(boolean value) {
        this.checkNotNull();
        PxCookingParams._setSuppressTriangleMeshRemapTable(this.address, value);
    }

    private static native void _setSuppressTriangleMeshRemapTable(long var0, boolean var2);

    public boolean getBuildTriangleAdjacencies() {
        this.checkNotNull();
        return PxCookingParams._getBuildTriangleAdjacencies(this.address);
    }

    private static native boolean _getBuildTriangleAdjacencies(long var0);

    public void setBuildTriangleAdjacencies(boolean value) {
        this.checkNotNull();
        PxCookingParams._setBuildTriangleAdjacencies(this.address, value);
    }

    private static native void _setBuildTriangleAdjacencies(long var0, boolean var2);

    public boolean getBuildGPUData() {
        this.checkNotNull();
        return PxCookingParams._getBuildGPUData(this.address);
    }

    private static native boolean _getBuildGPUData(long var0);

    public void setBuildGPUData(boolean value) {
        this.checkNotNull();
        PxCookingParams._setBuildGPUData(this.address, value);
    }

    private static native void _setBuildGPUData(long var0, boolean var2);

    public PxTolerancesScale getScale() {
        this.checkNotNull();
        return PxTolerancesScale.wrapPointer(PxCookingParams._getScale(this.address));
    }

    private static native long _getScale(long var0);

    public void setScale(PxTolerancesScale value) {
        this.checkNotNull();
        PxCookingParams._setScale(this.address, value.getAddress());
    }

    private static native void _setScale(long var0, long var2);

    public PxMeshPreprocessingFlags getMeshPreprocessParams() {
        this.checkNotNull();
        return PxMeshPreprocessingFlags.wrapPointer(PxCookingParams._getMeshPreprocessParams(this.address));
    }

    private static native long _getMeshPreprocessParams(long var0);

    public void setMeshPreprocessParams(PxMeshPreprocessingFlags value) {
        this.checkNotNull();
        PxCookingParams._setMeshPreprocessParams(this.address, value.getAddress());
    }

    private static native void _setMeshPreprocessParams(long var0, long var2);

    public float getMeshWeldTolerance() {
        this.checkNotNull();
        return PxCookingParams._getMeshWeldTolerance(this.address);
    }

    private static native float _getMeshWeldTolerance(long var0);

    public void setMeshWeldTolerance(float value) {
        this.checkNotNull();
        PxCookingParams._setMeshWeldTolerance(this.address, value);
    }

    private static native void _setMeshWeldTolerance(long var0, float var2);

    public PxMidphaseDesc getMidphaseDesc() {
        this.checkNotNull();
        return PxMidphaseDesc.wrapPointer(PxCookingParams._getMidphaseDesc(this.address));
    }

    private static native long _getMidphaseDesc(long var0);

    public void setMidphaseDesc(PxMidphaseDesc value) {
        this.checkNotNull();
        PxCookingParams._setMidphaseDesc(this.address, value.getAddress());
    }

    private static native void _setMidphaseDesc(long var0, long var2);

    public int getGaussMapLimit() {
        this.checkNotNull();
        return PxCookingParams._getGaussMapLimit(this.address);
    }

    private static native int _getGaussMapLimit(long var0);

    public void setGaussMapLimit(int value) {
        this.checkNotNull();
        PxCookingParams._setGaussMapLimit(this.address, value);
    }

    private static native void _setGaussMapLimit(long var0, int var2);
}

