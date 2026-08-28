/*
 * Decompiled with CFR 0.152.
 */
package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.common.PxBase;
import physx.common.PxVec4;
import physx.particles.PxParticleBufferFlagEnum;
import physx.particles.PxParticleRigidAttachment;
import physx.particles.PxParticleRigidFilterPair;
import physx.particles.PxParticleVolume;

public class PxParticleBuffer
extends PxBase {
    public static final int SIZEOF;
    public static final int ALIGNOF = 8;

    protected PxParticleBuffer() {
    }

    private static native int __sizeOf();

    public static PxParticleBuffer wrapPointer(long address) {
        return address != 0L ? new PxParticleBuffer(address) : null;
    }

    public static PxParticleBuffer arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxParticleBuffer.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxParticleBuffer(long address) {
        super(address);
    }

    public int getBufferIndex() {
        this.checkNotNull();
        return PxParticleBuffer._getBufferIndex(this.address);
    }

    private static native int _getBufferIndex(long var0);

    public int getBufferUniqueId() {
        this.checkNotNull();
        return PxParticleBuffer._getBufferUniqueId(this.address);
    }

    private static native int _getBufferUniqueId(long var0);

    public PxVec4 getPositionInvMasses() {
        this.checkNotNull();
        return PxVec4.wrapPointer(PxParticleBuffer._getPositionInvMasses(this.address));
    }

    private static native long _getPositionInvMasses(long var0);

    public PxVec4 getVelocities() {
        this.checkNotNull();
        return PxVec4.wrapPointer(PxParticleBuffer._getVelocities(this.address));
    }

    private static native long _getVelocities(long var0);

    public NativeObject getPhases() {
        this.checkNotNull();
        return NativeObject.wrapPointer(PxParticleBuffer._getPhases(this.address));
    }

    private static native long _getPhases(long var0);

    public PxParticleVolume getParticleVolumes() {
        this.checkNotNull();
        return PxParticleVolume.wrapPointer(PxParticleBuffer._getParticleVolumes(this.address));
    }

    private static native long _getParticleVolumes(long var0);

    public void setNbActiveParticles(int nbActiveParticles) {
        this.checkNotNull();
        PxParticleBuffer._setNbActiveParticles(this.address, nbActiveParticles);
    }

    private static native void _setNbActiveParticles(long var0, int var2);

    public int getNbActiveParticles() {
        this.checkNotNull();
        return PxParticleBuffer._getNbActiveParticles(this.address);
    }

    private static native int _getNbActiveParticles(long var0);

    public int getMaxParticles() {
        this.checkNotNull();
        return PxParticleBuffer._getMaxParticles(this.address);
    }

    private static native int _getMaxParticles(long var0);

    public int getNbParticleVolumes() {
        this.checkNotNull();
        return PxParticleBuffer._getNbParticleVolumes(this.address);
    }

    private static native int _getNbParticleVolumes(long var0);

    public void setNbParticleVolumes(int nbParticleVolumes) {
        this.checkNotNull();
        PxParticleBuffer._setNbParticleVolumes(this.address, nbParticleVolumes);
    }

    private static native void _setNbParticleVolumes(long var0, int var2);

    public int getMaxParticleVolumes() {
        this.checkNotNull();
        return PxParticleBuffer._getMaxParticleVolumes(this.address);
    }

    private static native int _getMaxParticleVolumes(long var0);

    public void setRigidFilters(PxParticleRigidFilterPair filters, int nbFilters) {
        this.checkNotNull();
        PxParticleBuffer._setRigidFilters(this.address, filters.getAddress(), nbFilters);
    }

    private static native void _setRigidFilters(long var0, long var2, int var4);

    public void setRigidAttachments(PxParticleRigidAttachment attachments, int nbAttachments) {
        this.checkNotNull();
        PxParticleBuffer._setRigidAttachments(this.address, attachments.getAddress(), nbAttachments);
    }

    private static native void _setRigidAttachments(long var0, long var2, int var4);

    public int getFlatListStartIndex() {
        this.checkNotNull();
        return PxParticleBuffer._getFlatListStartIndex(this.address);
    }

    private static native int _getFlatListStartIndex(long var0);

    public void raiseFlags(PxParticleBufferFlagEnum flags) {
        this.checkNotNull();
        PxParticleBuffer._raiseFlags(this.address, flags.value);
    }

    private static native void _raiseFlags(long var0, int var2);

    @Override
    public void release() {
        this.checkNotNull();
        PxParticleBuffer._release(this.address);
    }

    private static native void _release(long var0);

    static {
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticleBuffer");
        SIZEOF = PxParticleBuffer.__sizeOf();
    }
}

