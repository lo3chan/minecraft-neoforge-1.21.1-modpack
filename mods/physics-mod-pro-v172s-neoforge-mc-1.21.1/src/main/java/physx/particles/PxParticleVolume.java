/*
 * Decompiled with CFR 0.152.
 */
package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.common.PxBounds3;

public class PxParticleVolume
extends NativeObject {
    public static final int SIZEOF;
    public static final int ALIGNOF = 8;

    protected PxParticleVolume() {
    }

    private static native int __sizeOf();

    public static PxParticleVolume wrapPointer(long address) {
        return address != 0L ? new PxParticleVolume(address) : null;
    }

    public static PxParticleVolume arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxParticleVolume.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxParticleVolume(long address) {
        super(address);
    }

    public PxBounds3 getBound() {
        this.checkNotNull();
        return PxBounds3.wrapPointer(PxParticleVolume._getBound(this.address));
    }

    private static native long _getBound(long var0);

    public void setBound(PxBounds3 value) {
        this.checkNotNull();
        PxParticleVolume._setBound(this.address, value.getAddress());
    }

    private static native void _setBound(long var0, long var2);

    public int getParticleIndicesOffset() {
        this.checkNotNull();
        return PxParticleVolume._getParticleIndicesOffset(this.address);
    }

    private static native int _getParticleIndicesOffset(long var0);

    public void setParticleIndicesOffset(int value) {
        this.checkNotNull();
        PxParticleVolume._setParticleIndicesOffset(this.address, value);
    }

    private static native void _setParticleIndicesOffset(long var0, int var2);

    public int getNumParticles() {
        this.checkNotNull();
        return PxParticleVolume._getNumParticles(this.address);
    }

    private static native int _getNumParticles(long var0);

    public void setNumParticles(int value) {
        this.checkNotNull();
        PxParticleVolume._setNumParticles(this.address, value);
    }

    private static native void _setNumParticles(long var0, int var2);

    static {
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticleVolume");
        SIZEOF = PxParticleVolume.__sizeOf();
    }
}

