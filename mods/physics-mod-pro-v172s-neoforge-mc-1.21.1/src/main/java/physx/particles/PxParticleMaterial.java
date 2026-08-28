/*
 * Decompiled with CFR 0.152.
 */
package physx.particles;

import physx.PlatformChecks;
import physx.physics.PxBaseMaterial;

public class PxParticleMaterial
extends PxBaseMaterial {
    public static final int SIZEOF;
    public static final int ALIGNOF = 8;

    protected PxParticleMaterial() {
    }

    private static native int __sizeOf();

    public static PxParticleMaterial wrapPointer(long address) {
        return address != 0L ? new PxParticleMaterial(address) : null;
    }

    public static PxParticleMaterial arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxParticleMaterial.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxParticleMaterial(long address) {
        super(address);
    }

    public void setFriction(float friction) {
        this.checkNotNull();
        PxParticleMaterial._setFriction(this.address, friction);
    }

    private static native void _setFriction(long var0, float var2);

    public float getFriction() {
        this.checkNotNull();
        return PxParticleMaterial._getFriction(this.address);
    }

    private static native float _getFriction(long var0);

    public void setDamping(float damping) {
        this.checkNotNull();
        PxParticleMaterial._setDamping(this.address, damping);
    }

    private static native void _setDamping(long var0, float var2);

    public float getDamping() {
        this.checkNotNull();
        return PxParticleMaterial._getDamping(this.address);
    }

    private static native float _getDamping(long var0);

    public void setAdhesion(float adhesion) {
        this.checkNotNull();
        PxParticleMaterial._setAdhesion(this.address, adhesion);
    }

    private static native void _setAdhesion(long var0, float var2);

    public float getAdhesion() {
        this.checkNotNull();
        return PxParticleMaterial._getAdhesion(this.address);
    }

    private static native float _getAdhesion(long var0);

    public void setGravityScale(float scale) {
        this.checkNotNull();
        PxParticleMaterial._setGravityScale(this.address, scale);
    }

    private static native void _setGravityScale(long var0, float var2);

    public float getGravityScale() {
        this.checkNotNull();
        return PxParticleMaterial._getGravityScale(this.address);
    }

    private static native float _getGravityScale(long var0);

    public void setAdhesionRadiusScale(float scale) {
        this.checkNotNull();
        PxParticleMaterial._setAdhesionRadiusScale(this.address, scale);
    }

    private static native void _setAdhesionRadiusScale(long var0, float var2);

    public float getAdhesionRadiusScale() {
        this.checkNotNull();
        return PxParticleMaterial._getAdhesionRadiusScale(this.address);
    }

    private static native float _getAdhesionRadiusScale(long var0);

    static {
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticleMaterial");
        SIZEOF = PxParticleMaterial.__sizeOf();
    }
}

