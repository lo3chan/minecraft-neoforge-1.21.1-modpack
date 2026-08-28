/*
 * Decompiled with CFR 0.152.
 */
package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.support.PxU32Ptr;

public class PxGpuParticleSystem
extends NativeObject {
    public static final int SIZEOF;
    public static final int ALIGNOF = 8;

    protected PxGpuParticleSystem() {
    }

    private static native int __sizeOf();

    public static PxGpuParticleSystem wrapPointer(long address) {
        return address != 0L ? new PxGpuParticleSystem(address) : null;
    }

    public static PxGpuParticleSystem arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxGpuParticleSystem.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxGpuParticleSystem(long address) {
        super(address);
    }

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxGpuParticleSystem._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxU32Ptr getMUnsortedPhaseArray() {
        this.checkNotNull();
        return PxU32Ptr.wrapPointer(PxGpuParticleSystem._getMUnsortedPhaseArray(this.address));
    }

    private static native long _getMUnsortedPhaseArray(long var0);

    public void setMUnsortedPhaseArray(PxU32Ptr value) {
        this.checkNotNull();
        PxGpuParticleSystem._setMUnsortedPhaseArray(this.address, value.getAddress());
    }

    private static native void _setMUnsortedPhaseArray(long var0, long var2);

    public PxU32Ptr getMSortedPhaseArray() {
        this.checkNotNull();
        return PxU32Ptr.wrapPointer(PxGpuParticleSystem._getMSortedPhaseArray(this.address));
    }

    private static native long _getMSortedPhaseArray(long var0);

    public void setMSortedPhaseArray(PxU32Ptr value) {
        this.checkNotNull();
        PxGpuParticleSystem._setMSortedPhaseArray(this.address, value.getAddress());
    }

    private static native void _setMSortedPhaseArray(long var0, long var2);

    public PxU32Ptr getMUnsortedToSortedMapping() {
        this.checkNotNull();
        return PxU32Ptr.wrapPointer(PxGpuParticleSystem._getMUnsortedToSortedMapping(this.address));
    }

    private static native long _getMUnsortedToSortedMapping(long var0);

    public void setMUnsortedToSortedMapping(PxU32Ptr value) {
        this.checkNotNull();
        PxGpuParticleSystem._setMUnsortedToSortedMapping(this.address, value.getAddress());
    }

    private static native void _setMUnsortedToSortedMapping(long var0, long var2);

    public PxU32Ptr getMSortedToUnsortedMapping() {
        this.checkNotNull();
        return PxU32Ptr.wrapPointer(PxGpuParticleSystem._getMSortedToUnsortedMapping(this.address));
    }

    private static native long _getMSortedToUnsortedMapping(long var0);

    public void setMSortedToUnsortedMapping(PxU32Ptr value) {
        this.checkNotNull();
        PxGpuParticleSystem._setMSortedToUnsortedMapping(this.address, value.getAddress());
    }

    private static native void _setMSortedToUnsortedMapping(long var0, long var2);

    public PxU32Ptr getMParticleSelfCollisionCount() {
        this.checkNotNull();
        return PxU32Ptr.wrapPointer(PxGpuParticleSystem._getMParticleSelfCollisionCount(this.address));
    }

    private static native long _getMParticleSelfCollisionCount(long var0);

    public void setMParticleSelfCollisionCount(PxU32Ptr value) {
        this.checkNotNull();
        PxGpuParticleSystem._setMParticleSelfCollisionCount(this.address, value.getAddress());
    }

    private static native void _setMParticleSelfCollisionCount(long var0, long var2);

    public PxU32Ptr getMCollisionIndex() {
        this.checkNotNull();
        return PxU32Ptr.wrapPointer(PxGpuParticleSystem._getMCollisionIndex(this.address));
    }

    private static native long _getMCollisionIndex(long var0);

    public void setMCollisionIndex(PxU32Ptr value) {
        this.checkNotNull();
        PxGpuParticleSystem._setMCollisionIndex(this.address, value.getAddress());
    }

    private static native void _setMCollisionIndex(long var0, long var2);

    public int getNumCells() {
        this.checkNotNull();
        return PxGpuParticleSystem._getNumCells(this.address);
    }

    private static native int _getNumCells(long var0);

    static {
        PlatformChecks.requirePlatform(3, "physx.particles.PxGpuParticleSystem");
        SIZEOF = PxGpuParticleSystem.__sizeOf();
    }
}

