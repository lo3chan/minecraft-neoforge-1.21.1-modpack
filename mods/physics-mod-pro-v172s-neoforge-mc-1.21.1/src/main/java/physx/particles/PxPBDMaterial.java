/*
 * Decompiled with CFR 0.152.
 */
package physx.particles;

import physx.PlatformChecks;
import physx.particles.PxParticleMaterial;

public class PxPBDMaterial
extends PxParticleMaterial {
    public static final int SIZEOF;
    public static final int ALIGNOF = 8;

    protected PxPBDMaterial() {
    }

    private static native int __sizeOf();

    public static PxPBDMaterial wrapPointer(long address) {
        return address != 0L ? new PxPBDMaterial(address) : null;
    }

    public static PxPBDMaterial arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxPBDMaterial.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxPBDMaterial(long address) {
        super(address);
    }

    public void setViscosity(float viscosity) {
        this.checkNotNull();
        PxPBDMaterial._setViscosity(this.address, viscosity);
    }

    private static native void _setViscosity(long var0, float var2);

    public float getViscosity() {
        this.checkNotNull();
        return PxPBDMaterial._getViscosity(this.address);
    }

    private static native float _getViscosity(long var0);

    public void setVorticityConfinement(float vorticityConfinement) {
        this.checkNotNull();
        PxPBDMaterial._setVorticityConfinement(this.address, vorticityConfinement);
    }

    private static native void _setVorticityConfinement(long var0, float var2);

    public float getVorticityConfinement() {
        this.checkNotNull();
        return PxPBDMaterial._getVorticityConfinement(this.address);
    }

    private static native float _getVorticityConfinement(long var0);

    public void setSurfaceTension(float surfaceTension) {
        this.checkNotNull();
        PxPBDMaterial._setSurfaceTension(this.address, surfaceTension);
    }

    private static native void _setSurfaceTension(long var0, float var2);

    public float getSurfaceTension() {
        this.checkNotNull();
        return PxPBDMaterial._getSurfaceTension(this.address);
    }

    private static native float _getSurfaceTension(long var0);

    public void setCohesion(float cohesion) {
        this.checkNotNull();
        PxPBDMaterial._setCohesion(this.address, cohesion);
    }

    private static native void _setCohesion(long var0, float var2);

    public float getCohesion() {
        this.checkNotNull();
        return PxPBDMaterial._getCohesion(this.address);
    }

    private static native float _getCohesion(long var0);

    public void setLift(float lift) {
        this.checkNotNull();
        PxPBDMaterial._setLift(this.address, lift);
    }

    private static native void _setLift(long var0, float var2);

    public float getLift() {
        this.checkNotNull();
        return PxPBDMaterial._getLift(this.address);
    }

    private static native float _getLift(long var0);

    public void setDrag(float drag) {
        this.checkNotNull();
        PxPBDMaterial._setDrag(this.address, drag);
    }

    private static native void _setDrag(long var0, float var2);

    public float getDrag() {
        this.checkNotNull();
        return PxPBDMaterial._getDrag(this.address);
    }

    private static native float _getDrag(long var0);

    public void setCFLCoefficient(float coefficient) {
        this.checkNotNull();
        PxPBDMaterial._setCFLCoefficient(this.address, coefficient);
    }

    private static native void _setCFLCoefficient(long var0, float var2);

    public float getCFLCoefficient() {
        this.checkNotNull();
        return PxPBDMaterial._getCFLCoefficient(this.address);
    }

    private static native float _getCFLCoefficient(long var0);

    public void setParticleFrictionScale(float scale) {
        this.checkNotNull();
        PxPBDMaterial._setParticleFrictionScale(this.address, scale);
    }

    private static native void _setParticleFrictionScale(long var0, float var2);

    public float getParticleFrictionScale() {
        this.checkNotNull();
        return PxPBDMaterial._getParticleFrictionScale(this.address);
    }

    private static native float _getParticleFrictionScale(long var0);

    public void setParticleAdhesionScale(float adhesion) {
        this.checkNotNull();
        PxPBDMaterial._setParticleAdhesionScale(this.address, adhesion);
    }

    private static native void _setParticleAdhesionScale(long var0, float var2);

    public float getParticleAdhesionScale() {
        this.checkNotNull();
        return PxPBDMaterial._getParticleAdhesionScale(this.address);
    }

    private static native float _getParticleAdhesionScale(long var0);

    @Override
    public String getConcreteTypeName() {
        this.checkNotNull();
        return PxPBDMaterial._getConcreteTypeName(this.address);
    }

    private static native String _getConcreteTypeName(long var0);

    static {
        PlatformChecks.requirePlatform(3, "physx.particles.PxPBDMaterial");
        SIZEOF = PxPBDMaterial.__sizeOf();
    }
}

