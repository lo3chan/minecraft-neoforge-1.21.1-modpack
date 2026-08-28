/*
 * Decompiled with CFR 0.152.
 */
package physx.common;

import physx.NativeObject;
import physx.PlatformChecks;

public class PxgDynamicsMemoryConfig
extends NativeObject {
    public static final int SIZEOF;
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxgDynamicsMemoryConfig wrapPointer(long address) {
        return address != 0L ? new PxgDynamicsMemoryConfig(address) : null;
    }

    public static PxgDynamicsMemoryConfig arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxgDynamicsMemoryConfig.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxgDynamicsMemoryConfig(long address) {
        super(address);
    }

    public PxgDynamicsMemoryConfig() {
        this.address = PxgDynamicsMemoryConfig._PxgDynamicsMemoryConfig();
    }

    private static native long _PxgDynamicsMemoryConfig();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxgDynamicsMemoryConfig._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public int getTempBufferCapacity() {
        this.checkNotNull();
        return PxgDynamicsMemoryConfig._getTempBufferCapacity(this.address);
    }

    private static native int _getTempBufferCapacity(long var0);

    public void setTempBufferCapacity(int value) {
        this.checkNotNull();
        PxgDynamicsMemoryConfig._setTempBufferCapacity(this.address, value);
    }

    private static native void _setTempBufferCapacity(long var0, int var2);

    public int getMaxRigidContactCount() {
        this.checkNotNull();
        return PxgDynamicsMemoryConfig._getMaxRigidContactCount(this.address);
    }

    private static native int _getMaxRigidContactCount(long var0);

    public void setMaxRigidContactCount(int value) {
        this.checkNotNull();
        PxgDynamicsMemoryConfig._setMaxRigidContactCount(this.address, value);
    }

    private static native void _setMaxRigidContactCount(long var0, int var2);

    public int getMaxRigidPatchCount() {
        this.checkNotNull();
        return PxgDynamicsMemoryConfig._getMaxRigidPatchCount(this.address);
    }

    private static native int _getMaxRigidPatchCount(long var0);

    public void setMaxRigidPatchCount(int value) {
        this.checkNotNull();
        PxgDynamicsMemoryConfig._setMaxRigidPatchCount(this.address, value);
    }

    private static native void _setMaxRigidPatchCount(long var0, int var2);

    public int getHeapCapacity() {
        this.checkNotNull();
        return PxgDynamicsMemoryConfig._getHeapCapacity(this.address);
    }

    private static native int _getHeapCapacity(long var0);

    public void setHeapCapacity(int value) {
        this.checkNotNull();
        PxgDynamicsMemoryConfig._setHeapCapacity(this.address, value);
    }

    private static native void _setHeapCapacity(long var0, int var2);

    public int getFoundLostPairsCapacity() {
        this.checkNotNull();
        return PxgDynamicsMemoryConfig._getFoundLostPairsCapacity(this.address);
    }

    private static native int _getFoundLostPairsCapacity(long var0);

    public void setFoundLostPairsCapacity(int value) {
        this.checkNotNull();
        PxgDynamicsMemoryConfig._setFoundLostPairsCapacity(this.address, value);
    }

    private static native void _setFoundLostPairsCapacity(long var0, int var2);

    public int getFoundLostAggregatePairsCapacity() {
        this.checkNotNull();
        return PxgDynamicsMemoryConfig._getFoundLostAggregatePairsCapacity(this.address);
    }

    private static native int _getFoundLostAggregatePairsCapacity(long var0);

    public void setFoundLostAggregatePairsCapacity(int value) {
        this.checkNotNull();
        PxgDynamicsMemoryConfig._setFoundLostAggregatePairsCapacity(this.address, value);
    }

    private static native void _setFoundLostAggregatePairsCapacity(long var0, int var2);

    public int getTotalAggregatePairsCapacity() {
        this.checkNotNull();
        return PxgDynamicsMemoryConfig._getTotalAggregatePairsCapacity(this.address);
    }

    private static native int _getTotalAggregatePairsCapacity(long var0);

    public void setTotalAggregatePairsCapacity(int value) {
        this.checkNotNull();
        PxgDynamicsMemoryConfig._setTotalAggregatePairsCapacity(this.address, value);
    }

    private static native void _setTotalAggregatePairsCapacity(long var0, int var2);

    public int getMaxSoftBodyContacts() {
        this.checkNotNull();
        return PxgDynamicsMemoryConfig._getMaxSoftBodyContacts(this.address);
    }

    private static native int _getMaxSoftBodyContacts(long var0);

    public void setMaxSoftBodyContacts(int value) {
        this.checkNotNull();
        PxgDynamicsMemoryConfig._setMaxSoftBodyContacts(this.address, value);
    }

    private static native void _setMaxSoftBodyContacts(long var0, int var2);

    public int getMaxFemClothContacts() {
        this.checkNotNull();
        return PxgDynamicsMemoryConfig._getMaxFemClothContacts(this.address);
    }

    private static native int _getMaxFemClothContacts(long var0);

    public void setMaxFemClothContacts(int value) {
        this.checkNotNull();
        PxgDynamicsMemoryConfig._setMaxFemClothContacts(this.address, value);
    }

    private static native void _setMaxFemClothContacts(long var0, int var2);

    public int getMaxParticleContacts() {
        this.checkNotNull();
        return PxgDynamicsMemoryConfig._getMaxParticleContacts(this.address);
    }

    private static native int _getMaxParticleContacts(long var0);

    public void setMaxParticleContacts(int value) {
        this.checkNotNull();
        PxgDynamicsMemoryConfig._setMaxParticleContacts(this.address, value);
    }

    private static native void _setMaxParticleContacts(long var0, int var2);

    public int getCollisionStackSize() {
        this.checkNotNull();
        return PxgDynamicsMemoryConfig._getCollisionStackSize(this.address);
    }

    private static native int _getCollisionStackSize(long var0);

    public void setCollisionStackSize(int value) {
        this.checkNotNull();
        PxgDynamicsMemoryConfig._setCollisionStackSize(this.address, value);
    }

    private static native void _setCollisionStackSize(long var0, int var2);

    public int getMaxHairContacts() {
        this.checkNotNull();
        return PxgDynamicsMemoryConfig._getMaxHairContacts(this.address);
    }

    private static native int _getMaxHairContacts(long var0);

    public void setMaxHairContacts(int value) {
        this.checkNotNull();
        PxgDynamicsMemoryConfig._setMaxHairContacts(this.address, value);
    }

    private static native void _setMaxHairContacts(long var0, int var2);

    public boolean isValid() {
        this.checkNotNull();
        return PxgDynamicsMemoryConfig._isValid(this.address);
    }

    private static native boolean _isValid(long var0);

    static {
        PlatformChecks.requirePlatform(3, "physx.common.PxgDynamicsMemoryConfig");
        SIZEOF = PxgDynamicsMemoryConfig.__sizeOf();
    }
}

