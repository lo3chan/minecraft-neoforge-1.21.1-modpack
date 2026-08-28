/*
 * Decompiled with CFR 0.152.
 */
package physx.particles;

import physx.PlatformChecks;
import physx.common.PxVec3;
import physx.particles.PxParticleSystem;

public class PxPBDParticleSystem
extends PxParticleSystem {
    public static final int SIZEOF;
    public static final int ALIGNOF = 8;

    protected PxPBDParticleSystem() {
    }

    private static native int __sizeOf();

    public static PxPBDParticleSystem wrapPointer(long address) {
        return address != 0L ? new PxPBDParticleSystem(address) : null;
    }

    public static PxPBDParticleSystem arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxPBDParticleSystem.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxPBDParticleSystem(long address) {
        super(address);
    }

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxPBDParticleSystem._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public void setWind(PxVec3 wind) {
        this.checkNotNull();
        PxPBDParticleSystem._setWind(this.address, wind.getAddress());
    }

    private static native void _setWind(long var0, long var2);

    public PxVec3 getWind() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxPBDParticleSystem._getWind(this.address));
    }

    private static native long _getWind(long var0);

    public void setFluidBoundaryDensityScale(float fluidBoundaryDensityScale) {
        this.checkNotNull();
        PxPBDParticleSystem._setFluidBoundaryDensityScale(this.address, fluidBoundaryDensityScale);
    }

    private static native void _setFluidBoundaryDensityScale(long var0, float var2);

    public float getFluidBoundaryDensityScale() {
        this.checkNotNull();
        return PxPBDParticleSystem._getFluidBoundaryDensityScale(this.address);
    }

    private static native float _getFluidBoundaryDensityScale(long var0);

    public void setFluidRestOffset(float fluidRestOffset) {
        this.checkNotNull();
        PxPBDParticleSystem._setFluidRestOffset(this.address, fluidRestOffset);
    }

    private static native void _setFluidRestOffset(long var0, float var2);

    public float getFluidRestOffset() {
        this.checkNotNull();
        return PxPBDParticleSystem._getFluidRestOffset(this.address);
    }

    private static native float _getFluidRestOffset(long var0);

    public void setGridSizeX(int gridSizeX) {
        this.checkNotNull();
        PxPBDParticleSystem._setGridSizeX(this.address, gridSizeX);
    }

    private static native void _setGridSizeX(long var0, int var2);

    public void setGridSizeY(int gridSizeY) {
        this.checkNotNull();
        PxPBDParticleSystem._setGridSizeY(this.address, gridSizeY);
    }

    private static native void _setGridSizeY(long var0, int var2);

    public void setGridSizeZ(int gridSizeZ) {
        this.checkNotNull();
        PxPBDParticleSystem._setGridSizeZ(this.address, gridSizeZ);
    }

    private static native void _setGridSizeZ(long var0, int var2);

    @Override
    public String getConcreteTypeName() {
        this.checkNotNull();
        return PxPBDParticleSystem._getConcreteTypeName(this.address);
    }

    private static native String _getConcreteTypeName(long var0);

    static {
        PlatformChecks.requirePlatform(3, "physx.particles.PxPBDParticleSystem");
        SIZEOF = PxPBDParticleSystem.__sizeOf();
    }
}

