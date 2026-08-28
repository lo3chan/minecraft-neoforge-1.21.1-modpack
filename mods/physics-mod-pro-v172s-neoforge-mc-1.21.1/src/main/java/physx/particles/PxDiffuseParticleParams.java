/*
 * Decompiled with CFR 0.152.
 */
package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;

public class PxDiffuseParticleParams
extends NativeObject {
    public static final int SIZEOF;
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxDiffuseParticleParams wrapPointer(long address) {
        return address != 0L ? new PxDiffuseParticleParams(address) : null;
    }

    public static PxDiffuseParticleParams arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxDiffuseParticleParams.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxDiffuseParticleParams(long address) {
        super(address);
    }

    public static PxDiffuseParticleParams createAt(long address) {
        PxDiffuseParticleParams.__placement_new_PxDiffuseParticleParams(address);
        PxDiffuseParticleParams createdObj = PxDiffuseParticleParams.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxDiffuseParticleParams createAt(T allocator, NativeObject.Allocator<T> allocate) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxDiffuseParticleParams.__placement_new_PxDiffuseParticleParams(address);
        PxDiffuseParticleParams createdObj = PxDiffuseParticleParams.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxDiffuseParticleParams(long var0);

    public PxDiffuseParticleParams() {
        this.address = PxDiffuseParticleParams._PxDiffuseParticleParams();
    }

    private static native long _PxDiffuseParticleParams();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxDiffuseParticleParams._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public float getThreshold() {
        this.checkNotNull();
        return PxDiffuseParticleParams._getThreshold(this.address);
    }

    private static native float _getThreshold(long var0);

    public void setThreshold(float value) {
        this.checkNotNull();
        PxDiffuseParticleParams._setThreshold(this.address, value);
    }

    private static native void _setThreshold(long var0, float var2);

    public float getLifetime() {
        this.checkNotNull();
        return PxDiffuseParticleParams._getLifetime(this.address);
    }

    private static native float _getLifetime(long var0);

    public void setLifetime(float value) {
        this.checkNotNull();
        PxDiffuseParticleParams._setLifetime(this.address, value);
    }

    private static native void _setLifetime(long var0, float var2);

    public float getAirDrag() {
        this.checkNotNull();
        return PxDiffuseParticleParams._getAirDrag(this.address);
    }

    private static native float _getAirDrag(long var0);

    public void setAirDrag(float value) {
        this.checkNotNull();
        PxDiffuseParticleParams._setAirDrag(this.address, value);
    }

    private static native void _setAirDrag(long var0, float var2);

    public float getBubbleDrag() {
        this.checkNotNull();
        return PxDiffuseParticleParams._getBubbleDrag(this.address);
    }

    private static native float _getBubbleDrag(long var0);

    public void setBubbleDrag(float value) {
        this.checkNotNull();
        PxDiffuseParticleParams._setBubbleDrag(this.address, value);
    }

    private static native void _setBubbleDrag(long var0, float var2);

    public float getBuoyancy() {
        this.checkNotNull();
        return PxDiffuseParticleParams._getBuoyancy(this.address);
    }

    private static native float _getBuoyancy(long var0);

    public void setBuoyancy(float value) {
        this.checkNotNull();
        PxDiffuseParticleParams._setBuoyancy(this.address, value);
    }

    private static native void _setBuoyancy(long var0, float var2);

    public float getKineticEnergyWeight() {
        this.checkNotNull();
        return PxDiffuseParticleParams._getKineticEnergyWeight(this.address);
    }

    private static native float _getKineticEnergyWeight(long var0);

    public void setKineticEnergyWeight(float value) {
        this.checkNotNull();
        PxDiffuseParticleParams._setKineticEnergyWeight(this.address, value);
    }

    private static native void _setKineticEnergyWeight(long var0, float var2);

    public float getPressureWeight() {
        this.checkNotNull();
        return PxDiffuseParticleParams._getPressureWeight(this.address);
    }

    private static native float _getPressureWeight(long var0);

    public void setPressureWeight(float value) {
        this.checkNotNull();
        PxDiffuseParticleParams._setPressureWeight(this.address, value);
    }

    private static native void _setPressureWeight(long var0, float var2);

    public float getDivergenceWeight() {
        this.checkNotNull();
        return PxDiffuseParticleParams._getDivergenceWeight(this.address);
    }

    private static native float _getDivergenceWeight(long var0);

    public void setDivergenceWeight(float value) {
        this.checkNotNull();
        PxDiffuseParticleParams._setDivergenceWeight(this.address, value);
    }

    private static native void _setDivergenceWeight(long var0, float var2);

    public float getCollisionDecay() {
        this.checkNotNull();
        return PxDiffuseParticleParams._getCollisionDecay(this.address);
    }

    private static native float _getCollisionDecay(long var0);

    public void setCollisionDecay(float value) {
        this.checkNotNull();
        PxDiffuseParticleParams._setCollisionDecay(this.address, value);
    }

    private static native void _setCollisionDecay(long var0, float var2);

    public boolean getUseAccurateVelocity() {
        this.checkNotNull();
        return PxDiffuseParticleParams._getUseAccurateVelocity(this.address);
    }

    private static native boolean _getUseAccurateVelocity(long var0);

    public void setUseAccurateVelocity(boolean value) {
        this.checkNotNull();
        PxDiffuseParticleParams._setUseAccurateVelocity(this.address, value);
    }

    private static native void _setUseAccurateVelocity(long var0, boolean var2);

    public void setToDefault() {
        this.checkNotNull();
        PxDiffuseParticleParams._setToDefault(this.address);
    }

    private static native void _setToDefault(long var0);

    static {
        PlatformChecks.requirePlatform(3, "physx.particles.PxDiffuseParticleParams");
        SIZEOF = PxDiffuseParticleParams.__sizeOf();
    }
}

