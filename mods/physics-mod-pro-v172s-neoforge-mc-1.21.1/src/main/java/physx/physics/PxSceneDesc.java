/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.common.PxBounds3;
import physx.common.PxCpuDispatcher;
import physx.common.PxCudaContextManager;
import physx.common.PxTolerancesScale;
import physx.common.PxVec3;
import physx.common.PxgDynamicsMemoryConfig;
import physx.physics.PxBVHBuildStrategyEnum;
import physx.physics.PxBroadPhaseTypeEnum;
import physx.physics.PxDynamicTreeSecondaryPrunerEnum;
import physx.physics.PxFrictionTypeEnum;
import physx.physics.PxPairFilteringModeEnum;
import physx.physics.PxPruningStructureTypeEnum;
import physx.physics.PxSceneFlags;
import physx.physics.PxSceneLimits;
import physx.physics.PxSceneQueryUpdateModeEnum;
import physx.physics.PxSimulationEventCallback;
import physx.physics.PxSimulationFilterShader;
import physx.physics.PxSolverTypeEnum;

public class PxSceneDesc
extends NativeObject {
    public static final int SIZEOF = PxSceneDesc.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxSceneDesc() {
    }

    private static native int __sizeOf();

    public static PxSceneDesc wrapPointer(long address) {
        return address != 0L ? new PxSceneDesc(address) : null;
    }

    public static PxSceneDesc arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxSceneDesc.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxSceneDesc(long address) {
        super(address);
    }

    public static PxSceneDesc createAt(long address, PxTolerancesScale scale) {
        PxSceneDesc.__placement_new_PxSceneDesc(address, scale.getAddress());
        PxSceneDesc createdObj = PxSceneDesc.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxSceneDesc createAt(T allocator, NativeObject.Allocator<T> allocate, PxTolerancesScale scale) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxSceneDesc.__placement_new_PxSceneDesc(address, scale.getAddress());
        PxSceneDesc createdObj = PxSceneDesc.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxSceneDesc(long var0, long var2);

    public PxSceneDesc(PxTolerancesScale scale) {
        this.address = PxSceneDesc._PxSceneDesc(scale.getAddress());
    }

    private static native long _PxSceneDesc(long var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxSceneDesc._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxVec3 getGravity() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxSceneDesc._getGravity(this.address));
    }

    private static native long _getGravity(long var0);

    public void setGravity(PxVec3 value) {
        this.checkNotNull();
        PxSceneDesc._setGravity(this.address, value.getAddress());
    }

    private static native void _setGravity(long var0, long var2);

    public PxSimulationEventCallback getSimulationEventCallback() {
        this.checkNotNull();
        return PxSimulationEventCallback.wrapPointer(PxSceneDesc._getSimulationEventCallback(this.address));
    }

    private static native long _getSimulationEventCallback(long var0);

    public void setSimulationEventCallback(PxSimulationEventCallback value) {
        this.checkNotNull();
        PxSceneDesc._setSimulationEventCallback(this.address, value.getAddress());
    }

    private static native void _setSimulationEventCallback(long var0, long var2);

    public NativeObject getFilterShaderData() {
        this.checkNotNull();
        return NativeObject.wrapPointer(PxSceneDesc._getFilterShaderData(this.address));
    }

    private static native long _getFilterShaderData(long var0);

    public void setFilterShaderData(NativeObject value) {
        this.checkNotNull();
        PxSceneDesc._setFilterShaderData(this.address, value.getAddress());
    }

    private static native void _setFilterShaderData(long var0, long var2);

    public int getFilterShaderDataSize() {
        this.checkNotNull();
        return PxSceneDesc._getFilterShaderDataSize(this.address);
    }

    private static native int _getFilterShaderDataSize(long var0);

    public void setFilterShaderDataSize(int value) {
        this.checkNotNull();
        PxSceneDesc._setFilterShaderDataSize(this.address, value);
    }

    private static native void _setFilterShaderDataSize(long var0, int var2);

    public PxSimulationFilterShader getFilterShader() {
        this.checkNotNull();
        return PxSimulationFilterShader.wrapPointer(PxSceneDesc._getFilterShader(this.address));
    }

    private static native long _getFilterShader(long var0);

    public void setFilterShader(PxSimulationFilterShader value) {
        this.checkNotNull();
        PxSceneDesc._setFilterShader(this.address, value.getAddress());
    }

    private static native void _setFilterShader(long var0, long var2);

    public PxPairFilteringModeEnum getKineKineFilteringMode() {
        this.checkNotNull();
        return PxPairFilteringModeEnum.forValue(PxSceneDesc._getKineKineFilteringMode(this.address));
    }

    private static native int _getKineKineFilteringMode(long var0);

    public void setKineKineFilteringMode(PxPairFilteringModeEnum value) {
        this.checkNotNull();
        PxSceneDesc._setKineKineFilteringMode(this.address, value.value);
    }

    private static native void _setKineKineFilteringMode(long var0, int var2);

    public PxPairFilteringModeEnum getStaticKineFilteringMode() {
        this.checkNotNull();
        return PxPairFilteringModeEnum.forValue(PxSceneDesc._getStaticKineFilteringMode(this.address));
    }

    private static native int _getStaticKineFilteringMode(long var0);

    public void setStaticKineFilteringMode(PxPairFilteringModeEnum value) {
        this.checkNotNull();
        PxSceneDesc._setStaticKineFilteringMode(this.address, value.value);
    }

    private static native void _setStaticKineFilteringMode(long var0, int var2);

    public PxBroadPhaseTypeEnum getBroadPhaseType() {
        this.checkNotNull();
        return PxBroadPhaseTypeEnum.forValue(PxSceneDesc._getBroadPhaseType(this.address));
    }

    private static native int _getBroadPhaseType(long var0);

    public void setBroadPhaseType(PxBroadPhaseTypeEnum value) {
        this.checkNotNull();
        PxSceneDesc._setBroadPhaseType(this.address, value.value);
    }

    private static native void _setBroadPhaseType(long var0, int var2);

    public PxSceneLimits getLimits() {
        this.checkNotNull();
        return PxSceneLimits.wrapPointer(PxSceneDesc._getLimits(this.address));
    }

    private static native long _getLimits(long var0);

    public void setLimits(PxSceneLimits value) {
        this.checkNotNull();
        PxSceneDesc._setLimits(this.address, value.getAddress());
    }

    private static native void _setLimits(long var0, long var2);

    public PxFrictionTypeEnum getFrictionType() {
        this.checkNotNull();
        return PxFrictionTypeEnum.forValue(PxSceneDesc._getFrictionType(this.address));
    }

    private static native int _getFrictionType(long var0);

    public void setFrictionType(PxFrictionTypeEnum value) {
        this.checkNotNull();
        PxSceneDesc._setFrictionType(this.address, value.value);
    }

    private static native void _setFrictionType(long var0, int var2);

    public PxSolverTypeEnum getSolverType() {
        this.checkNotNull();
        return PxSolverTypeEnum.forValue(PxSceneDesc._getSolverType(this.address));
    }

    private static native int _getSolverType(long var0);

    public void setSolverType(PxSolverTypeEnum value) {
        this.checkNotNull();
        PxSceneDesc._setSolverType(this.address, value.value);
    }

    private static native void _setSolverType(long var0, int var2);

    public float getBounceThresholdVelocity() {
        this.checkNotNull();
        return PxSceneDesc._getBounceThresholdVelocity(this.address);
    }

    private static native float _getBounceThresholdVelocity(long var0);

    public void setBounceThresholdVelocity(float value) {
        this.checkNotNull();
        PxSceneDesc._setBounceThresholdVelocity(this.address, value);
    }

    private static native void _setBounceThresholdVelocity(long var0, float var2);

    public float getFrictionOffsetThreshold() {
        this.checkNotNull();
        return PxSceneDesc._getFrictionOffsetThreshold(this.address);
    }

    private static native float _getFrictionOffsetThreshold(long var0);

    public void setFrictionOffsetThreshold(float value) {
        this.checkNotNull();
        PxSceneDesc._setFrictionOffsetThreshold(this.address, value);
    }

    private static native void _setFrictionOffsetThreshold(long var0, float var2);

    public float getFrictionCorrelationDistance() {
        this.checkNotNull();
        return PxSceneDesc._getFrictionCorrelationDistance(this.address);
    }

    private static native float _getFrictionCorrelationDistance(long var0);

    public void setFrictionCorrelationDistance(float value) {
        this.checkNotNull();
        PxSceneDesc._setFrictionCorrelationDistance(this.address, value);
    }

    private static native void _setFrictionCorrelationDistance(long var0, float var2);

    public PxSceneFlags getFlags() {
        this.checkNotNull();
        return PxSceneFlags.wrapPointer(PxSceneDesc._getFlags(this.address));
    }

    private static native long _getFlags(long var0);

    public void setFlags(PxSceneFlags value) {
        this.checkNotNull();
        PxSceneDesc._setFlags(this.address, value.getAddress());
    }

    private static native void _setFlags(long var0, long var2);

    public PxCpuDispatcher getCpuDispatcher() {
        this.checkNotNull();
        return PxCpuDispatcher.wrapPointer(PxSceneDesc._getCpuDispatcher(this.address));
    }

    private static native long _getCpuDispatcher(long var0);

    public void setCpuDispatcher(PxCpuDispatcher value) {
        this.checkNotNull();
        PxSceneDesc._setCpuDispatcher(this.address, value.getAddress());
    }

    private static native void _setCpuDispatcher(long var0, long var2);

    public PxCudaContextManager getCudaContextManager() {
        this.checkNotNull();
        PlatformChecks.requirePlatform(3, "physx.physics.PxSceneDesc");
        return PxCudaContextManager.wrapPointer(PxSceneDesc._getCudaContextManager(this.address));
    }

    private static native long _getCudaContextManager(long var0);

    public void setCudaContextManager(PxCudaContextManager value) {
        this.checkNotNull();
        PlatformChecks.requirePlatform(3, "physx.physics.PxSceneDesc");
        PxSceneDesc._setCudaContextManager(this.address, value.getAddress());
    }

    private static native void _setCudaContextManager(long var0, long var2);

    public NativeObject getUserData() {
        this.checkNotNull();
        return NativeObject.wrapPointer(PxSceneDesc._getUserData(this.address));
    }

    private static native long _getUserData(long var0);

    public void setUserData(NativeObject value) {
        this.checkNotNull();
        PxSceneDesc._setUserData(this.address, value.getAddress());
    }

    private static native void _setUserData(long var0, long var2);

    public int getSolverBatchSize() {
        this.checkNotNull();
        return PxSceneDesc._getSolverBatchSize(this.address);
    }

    private static native int _getSolverBatchSize(long var0);

    public void setSolverBatchSize(int value) {
        this.checkNotNull();
        PxSceneDesc._setSolverBatchSize(this.address, value);
    }

    private static native void _setSolverBatchSize(long var0, int var2);

    public int getSolverArticulationBatchSize() {
        this.checkNotNull();
        return PxSceneDesc._getSolverArticulationBatchSize(this.address);
    }

    private static native int _getSolverArticulationBatchSize(long var0);

    public void setSolverArticulationBatchSize(int value) {
        this.checkNotNull();
        PxSceneDesc._setSolverArticulationBatchSize(this.address, value);
    }

    private static native void _setSolverArticulationBatchSize(long var0, int var2);

    public int getNbContactDataBlocks() {
        this.checkNotNull();
        return PxSceneDesc._getNbContactDataBlocks(this.address);
    }

    private static native int _getNbContactDataBlocks(long var0);

    public void setNbContactDataBlocks(int value) {
        this.checkNotNull();
        PxSceneDesc._setNbContactDataBlocks(this.address, value);
    }

    private static native void _setNbContactDataBlocks(long var0, int var2);

    public int getMaxNbContactDataBlocks() {
        this.checkNotNull();
        return PxSceneDesc._getMaxNbContactDataBlocks(this.address);
    }

    private static native int _getMaxNbContactDataBlocks(long var0);

    public void setMaxNbContactDataBlocks(int value) {
        this.checkNotNull();
        PxSceneDesc._setMaxNbContactDataBlocks(this.address, value);
    }

    private static native void _setMaxNbContactDataBlocks(long var0, int var2);

    public float getMaxBiasCoefficient() {
        this.checkNotNull();
        return PxSceneDesc._getMaxBiasCoefficient(this.address);
    }

    private static native float _getMaxBiasCoefficient(long var0);

    public void setMaxBiasCoefficient(float value) {
        this.checkNotNull();
        PxSceneDesc._setMaxBiasCoefficient(this.address, value);
    }

    private static native void _setMaxBiasCoefficient(long var0, float var2);

    public int getContactReportStreamBufferSize() {
        this.checkNotNull();
        return PxSceneDesc._getContactReportStreamBufferSize(this.address);
    }

    private static native int _getContactReportStreamBufferSize(long var0);

    public void setContactReportStreamBufferSize(int value) {
        this.checkNotNull();
        PxSceneDesc._setContactReportStreamBufferSize(this.address, value);
    }

    private static native void _setContactReportStreamBufferSize(long var0, int var2);

    public int getCcdMaxPasses() {
        this.checkNotNull();
        return PxSceneDesc._getCcdMaxPasses(this.address);
    }

    private static native int _getCcdMaxPasses(long var0);

    public void setCcdMaxPasses(int value) {
        this.checkNotNull();
        PxSceneDesc._setCcdMaxPasses(this.address, value);
    }

    private static native void _setCcdMaxPasses(long var0, int var2);

    public float getCcdThreshold() {
        this.checkNotNull();
        return PxSceneDesc._getCcdThreshold(this.address);
    }

    private static native float _getCcdThreshold(long var0);

    public void setCcdThreshold(float value) {
        this.checkNotNull();
        PxSceneDesc._setCcdThreshold(this.address, value);
    }

    private static native void _setCcdThreshold(long var0, float var2);

    public float getCcdMaxSeparation() {
        this.checkNotNull();
        return PxSceneDesc._getCcdMaxSeparation(this.address);
    }

    private static native float _getCcdMaxSeparation(long var0);

    public void setCcdMaxSeparation(float value) {
        this.checkNotNull();
        PxSceneDesc._setCcdMaxSeparation(this.address, value);
    }

    private static native void _setCcdMaxSeparation(long var0, float var2);

    public float getWakeCounterResetValue() {
        this.checkNotNull();
        return PxSceneDesc._getWakeCounterResetValue(this.address);
    }

    private static native float _getWakeCounterResetValue(long var0);

    public void setWakeCounterResetValue(float value) {
        this.checkNotNull();
        PxSceneDesc._setWakeCounterResetValue(this.address, value);
    }

    private static native void _setWakeCounterResetValue(long var0, float var2);

    public PxBounds3 getSanityBounds() {
        this.checkNotNull();
        return PxBounds3.wrapPointer(PxSceneDesc._getSanityBounds(this.address));
    }

    private static native long _getSanityBounds(long var0);

    public void setSanityBounds(PxBounds3 value) {
        this.checkNotNull();
        PxSceneDesc._setSanityBounds(this.address, value.getAddress());
    }

    private static native void _setSanityBounds(long var0, long var2);

    public PxgDynamicsMemoryConfig getGpuDynamicsConfig() {
        this.checkNotNull();
        PlatformChecks.requirePlatform(3, "physx.physics.PxSceneDesc");
        return PxgDynamicsMemoryConfig.wrapPointer(PxSceneDesc._getGpuDynamicsConfig(this.address));
    }

    private static native long _getGpuDynamicsConfig(long var0);

    public void setGpuDynamicsConfig(PxgDynamicsMemoryConfig value) {
        this.checkNotNull();
        PlatformChecks.requirePlatform(3, "physx.physics.PxSceneDesc");
        PxSceneDesc._setGpuDynamicsConfig(this.address, value.getAddress());
    }

    private static native void _setGpuDynamicsConfig(long var0, long var2);

    public int getGpuMaxNumPartitions() {
        this.checkNotNull();
        return PxSceneDesc._getGpuMaxNumPartitions(this.address);
    }

    private static native int _getGpuMaxNumPartitions(long var0);

    public void setGpuMaxNumPartitions(int value) {
        this.checkNotNull();
        PxSceneDesc._setGpuMaxNumPartitions(this.address, value);
    }

    private static native void _setGpuMaxNumPartitions(long var0, int var2);

    public int getGpuMaxNumStaticPartitions() {
        this.checkNotNull();
        return PxSceneDesc._getGpuMaxNumStaticPartitions(this.address);
    }

    private static native int _getGpuMaxNumStaticPartitions(long var0);

    public void setGpuMaxNumStaticPartitions(int value) {
        this.checkNotNull();
        PxSceneDesc._setGpuMaxNumStaticPartitions(this.address, value);
    }

    private static native void _setGpuMaxNumStaticPartitions(long var0, int var2);

    public int getGpuComputeVersion() {
        this.checkNotNull();
        return PxSceneDesc._getGpuComputeVersion(this.address);
    }

    private static native int _getGpuComputeVersion(long var0);

    public void setGpuComputeVersion(int value) {
        this.checkNotNull();
        PxSceneDesc._setGpuComputeVersion(this.address, value);
    }

    private static native void _setGpuComputeVersion(long var0, int var2);

    public int getContactPairSlabSize() {
        this.checkNotNull();
        return PxSceneDesc._getContactPairSlabSize(this.address);
    }

    private static native int _getContactPairSlabSize(long var0);

    public void setContactPairSlabSize(int value) {
        this.checkNotNull();
        PxSceneDesc._setContactPairSlabSize(this.address, value);
    }

    private static native void _setContactPairSlabSize(long var0, int var2);

    public PxPruningStructureTypeEnum getStaticStructure() {
        this.checkNotNull();
        return PxPruningStructureTypeEnum.forValue(PxSceneDesc._getStaticStructure(this.address));
    }

    private static native int _getStaticStructure(long var0);

    public void setStaticStructure(PxPruningStructureTypeEnum value) {
        this.checkNotNull();
        PxSceneDesc._setStaticStructure(this.address, value.value);
    }

    private static native void _setStaticStructure(long var0, int var2);

    public PxPruningStructureTypeEnum getDynamicStructure() {
        this.checkNotNull();
        return PxPruningStructureTypeEnum.forValue(PxSceneDesc._getDynamicStructure(this.address));
    }

    private static native int _getDynamicStructure(long var0);

    public void setDynamicStructure(PxPruningStructureTypeEnum value) {
        this.checkNotNull();
        PxSceneDesc._setDynamicStructure(this.address, value.value);
    }

    private static native void _setDynamicStructure(long var0, int var2);

    public int getDynamicTreeRebuildRateHint() {
        this.checkNotNull();
        return PxSceneDesc._getDynamicTreeRebuildRateHint(this.address);
    }

    private static native int _getDynamicTreeRebuildRateHint(long var0);

    public void setDynamicTreeRebuildRateHint(int value) {
        this.checkNotNull();
        PxSceneDesc._setDynamicTreeRebuildRateHint(this.address, value);
    }

    private static native void _setDynamicTreeRebuildRateHint(long var0, int var2);

    public PxDynamicTreeSecondaryPrunerEnum getDynamicTreeSecondaryPruner() {
        this.checkNotNull();
        return PxDynamicTreeSecondaryPrunerEnum.forValue(PxSceneDesc._getDynamicTreeSecondaryPruner(this.address));
    }

    private static native int _getDynamicTreeSecondaryPruner(long var0);

    public void setDynamicTreeSecondaryPruner(PxDynamicTreeSecondaryPrunerEnum value) {
        this.checkNotNull();
        PxSceneDesc._setDynamicTreeSecondaryPruner(this.address, value.value);
    }

    private static native void _setDynamicTreeSecondaryPruner(long var0, int var2);

    public PxBVHBuildStrategyEnum getStaticBVHBuildStrategy() {
        this.checkNotNull();
        return PxBVHBuildStrategyEnum.forValue(PxSceneDesc._getStaticBVHBuildStrategy(this.address));
    }

    private static native int _getStaticBVHBuildStrategy(long var0);

    public void setStaticBVHBuildStrategy(PxBVHBuildStrategyEnum value) {
        this.checkNotNull();
        PxSceneDesc._setStaticBVHBuildStrategy(this.address, value.value);
    }

    private static native void _setStaticBVHBuildStrategy(long var0, int var2);

    public PxBVHBuildStrategyEnum getDynamicBVHBuildStrategy() {
        this.checkNotNull();
        return PxBVHBuildStrategyEnum.forValue(PxSceneDesc._getDynamicBVHBuildStrategy(this.address));
    }

    private static native int _getDynamicBVHBuildStrategy(long var0);

    public void setDynamicBVHBuildStrategy(PxBVHBuildStrategyEnum value) {
        this.checkNotNull();
        PxSceneDesc._setDynamicBVHBuildStrategy(this.address, value.value);
    }

    private static native void _setDynamicBVHBuildStrategy(long var0, int var2);

    public int getStaticNbObjectsPerNode() {
        this.checkNotNull();
        return PxSceneDesc._getStaticNbObjectsPerNode(this.address);
    }

    private static native int _getStaticNbObjectsPerNode(long var0);

    public void setStaticNbObjectsPerNode(int value) {
        this.checkNotNull();
        PxSceneDesc._setStaticNbObjectsPerNode(this.address, value);
    }

    private static native void _setStaticNbObjectsPerNode(long var0, int var2);

    public int getDynamicNbObjectsPerNode() {
        this.checkNotNull();
        return PxSceneDesc._getDynamicNbObjectsPerNode(this.address);
    }

    private static native int _getDynamicNbObjectsPerNode(long var0);

    public void setDynamicNbObjectsPerNode(int value) {
        this.checkNotNull();
        PxSceneDesc._setDynamicNbObjectsPerNode(this.address, value);
    }

    private static native void _setDynamicNbObjectsPerNode(long var0, int var2);

    public PxSceneQueryUpdateModeEnum getSceneQueryUpdateMode() {
        this.checkNotNull();
        return PxSceneQueryUpdateModeEnum.forValue(PxSceneDesc._getSceneQueryUpdateMode(this.address));
    }

    private static native int _getSceneQueryUpdateMode(long var0);

    public void setSceneQueryUpdateMode(PxSceneQueryUpdateModeEnum value) {
        this.checkNotNull();
        PxSceneDesc._setSceneQueryUpdateMode(this.address, value.value);
    }

    private static native void _setSceneQueryUpdateMode(long var0, int var2);

    public void setToDefault(PxTolerancesScale scale) {
        this.checkNotNull();
        PxSceneDesc._setToDefault(this.address, scale.getAddress());
    }

    private static native void _setToDefault(long var0, long var2);

    public boolean isValid() {
        this.checkNotNull();
        return PxSceneDesc._isValid(this.address);
    }

    private static native boolean _isValid(long var0);
}

