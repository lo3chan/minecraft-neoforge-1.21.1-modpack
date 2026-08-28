/*
 * Decompiled with CFR 0.152.
 */
package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.common.PxVec4;
import physx.particles.PxParticleCloth;
import physx.particles.PxParticleClothDesc;
import physx.particles.PxParticleSpring;
import physx.support.PxU32Ptr;

public class PxParticleClothBufferHelper
extends NativeObject {
    public static final int SIZEOF;
    public static final int ALIGNOF = 8;

    protected PxParticleClothBufferHelper() {
    }

    private static native int __sizeOf();

    public static PxParticleClothBufferHelper wrapPointer(long address) {
        return address != 0L ? new PxParticleClothBufferHelper(address) : null;
    }

    public static PxParticleClothBufferHelper arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxParticleClothBufferHelper.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxParticleClothBufferHelper(long address) {
        super(address);
    }

    public void release() {
        this.checkNotNull();
        PxParticleClothBufferHelper._release(this.address);
    }

    private static native void _release(long var0);

    public int getMaxCloths() {
        this.checkNotNull();
        return PxParticleClothBufferHelper._getMaxCloths(this.address);
    }

    private static native int _getMaxCloths(long var0);

    public int getNumCloths() {
        this.checkNotNull();
        return PxParticleClothBufferHelper._getNumCloths(this.address);
    }

    private static native int _getNumCloths(long var0);

    public int getMaxSprings() {
        this.checkNotNull();
        return PxParticleClothBufferHelper._getMaxSprings(this.address);
    }

    private static native int _getMaxSprings(long var0);

    public int getNumSprings() {
        this.checkNotNull();
        return PxParticleClothBufferHelper._getNumSprings(this.address);
    }

    private static native int _getNumSprings(long var0);

    public int getMaxTriangles() {
        this.checkNotNull();
        return PxParticleClothBufferHelper._getMaxTriangles(this.address);
    }

    private static native int _getMaxTriangles(long var0);

    public int getNumTriangles() {
        this.checkNotNull();
        return PxParticleClothBufferHelper._getNumTriangles(this.address);
    }

    private static native int _getNumTriangles(long var0);

    public int getMaxParticles() {
        this.checkNotNull();
        return PxParticleClothBufferHelper._getMaxParticles(this.address);
    }

    private static native int _getMaxParticles(long var0);

    public int getNumParticles() {
        this.checkNotNull();
        return PxParticleClothBufferHelper._getNumParticles(this.address);
    }

    private static native int _getNumParticles(long var0);

    public void addCloth(PxParticleCloth particleCloth, PxU32Ptr triangles, int numTriangles, PxParticleSpring springs, int numSprings, PxVec4 restPositions, int numParticles) {
        this.checkNotNull();
        PxParticleClothBufferHelper._addCloth(this.address, particleCloth.getAddress(), triangles.getAddress(), numTriangles, springs.getAddress(), numSprings, restPositions.getAddress(), numParticles);
    }

    private static native void _addCloth(long var0, long var2, long var4, int var6, long var7, int var9, long var10, int var12);

    public void addCloth(float blendScale, float restVolume, float pressure, PxU32Ptr triangles, int numTriangles, PxParticleSpring springs, int numSprings, PxVec4 restPositions, int numParticles) {
        this.checkNotNull();
        PxParticleClothBufferHelper._addCloth(this.address, blendScale, restVolume, pressure, triangles.getAddress(), numTriangles, springs.getAddress(), numSprings, restPositions.getAddress(), numParticles);
    }

    private static native void _addCloth(long var0, float var2, float var3, float var4, long var5, int var7, long var8, int var10, long var11, int var13);

    public PxParticleClothDesc getParticleClothDesc() {
        this.checkNotNull();
        return PxParticleClothDesc.wrapPointer(PxParticleClothBufferHelper._getParticleClothDesc(this.address));
    }

    private static native long _getParticleClothDesc(long var0);

    static {
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticleClothBufferHelper");
        SIZEOF = PxParticleClothBufferHelper.__sizeOf();
    }
}

