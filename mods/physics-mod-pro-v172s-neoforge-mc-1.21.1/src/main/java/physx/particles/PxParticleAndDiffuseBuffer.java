/*
 * Decompiled with CFR 0.152.
 */
package physx.particles;

import physx.PlatformChecks;
import physx.common.PxVec4;
import physx.particles.PxDiffuseParticleParams;
import physx.particles.PxParticleBuffer;

public class PxParticleAndDiffuseBuffer
extends PxParticleBuffer {
    public static final int SIZEOF;
    public static final int ALIGNOF = 8;

    protected PxParticleAndDiffuseBuffer() {
    }

    private static native int __sizeOf();

    public static PxParticleAndDiffuseBuffer wrapPointer(long address) {
        return address != 0L ? new PxParticleAndDiffuseBuffer(address) : null;
    }

    public static PxParticleAndDiffuseBuffer arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxParticleAndDiffuseBuffer.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxParticleAndDiffuseBuffer(long address) {
        super(address);
    }

    public PxVec4 getDiffusePositionLifeTime() {
        this.checkNotNull();
        return PxVec4.wrapPointer(PxParticleAndDiffuseBuffer._getDiffusePositionLifeTime(this.address));
    }

    private static native long _getDiffusePositionLifeTime(long var0);

    public int getNbActiveDiffuseParticles() {
        this.checkNotNull();
        return PxParticleAndDiffuseBuffer._getNbActiveDiffuseParticles(this.address);
    }

    private static native int _getNbActiveDiffuseParticles(long var0);

    public void setMaxActiveDiffuseParticles(int maxActiveDiffuseParticles) {
        this.checkNotNull();
        PxParticleAndDiffuseBuffer._setMaxActiveDiffuseParticles(this.address, maxActiveDiffuseParticles);
    }

    private static native void _setMaxActiveDiffuseParticles(long var0, int var2);

    public int getMaxDiffuseParticles() {
        this.checkNotNull();
        return PxParticleAndDiffuseBuffer._getMaxDiffuseParticles(this.address);
    }

    private static native int _getMaxDiffuseParticles(long var0);

    public void setDiffuseParticleParams(PxDiffuseParticleParams params) {
        this.checkNotNull();
        PxParticleAndDiffuseBuffer._setDiffuseParticleParams(this.address, params.getAddress());
    }

    private static native void _setDiffuseParticleParams(long var0, long var2);

    public PxDiffuseParticleParams getDiffuseParticleParams() {
        this.checkNotNull();
        return PxDiffuseParticleParams.wrapPointer(PxParticleAndDiffuseBuffer._getDiffuseParticleParams(this.address));
    }

    private static native long _getDiffuseParticleParams(long var0);

    static {
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticleAndDiffuseBuffer");
        SIZEOF = PxParticleAndDiffuseBuffer.__sizeOf();
    }
}

