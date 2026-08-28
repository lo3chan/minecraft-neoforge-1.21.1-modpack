/*
 * Decompiled with CFR 0.152.
 */
package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.particles.PxParticlePhaseFlagEnum;

public class PxParticlePhaseFlags
extends NativeObject {
    public static final int SIZEOF;
    public static final int ALIGNOF = 8;

    protected PxParticlePhaseFlags() {
    }

    private static native int __sizeOf();

    public static PxParticlePhaseFlags wrapPointer(long address) {
        return address != 0L ? new PxParticlePhaseFlags(address) : null;
    }

    public static PxParticlePhaseFlags arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxParticlePhaseFlags.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxParticlePhaseFlags(long address) {
        super(address);
    }

    public static PxParticlePhaseFlags createAt(long address, int flags) {
        PxParticlePhaseFlags.__placement_new_PxParticlePhaseFlags(address, flags);
        PxParticlePhaseFlags createdObj = PxParticlePhaseFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxParticlePhaseFlags createAt(T allocator, NativeObject.Allocator<T> allocate, int flags) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxParticlePhaseFlags.__placement_new_PxParticlePhaseFlags(address, flags);
        PxParticlePhaseFlags createdObj = PxParticlePhaseFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxParticlePhaseFlags(long var0, int var2);

    public PxParticlePhaseFlags(int flags) {
        this.address = PxParticlePhaseFlags._PxParticlePhaseFlags(flags);
    }

    private static native long _PxParticlePhaseFlags(int var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxParticlePhaseFlags._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public boolean isSet(PxParticlePhaseFlagEnum flag) {
        this.checkNotNull();
        return PxParticlePhaseFlags._isSet(this.address, flag.value);
    }

    private static native boolean _isSet(long var0, int var2);

    public void raise(PxParticlePhaseFlagEnum flag) {
        this.checkNotNull();
        PxParticlePhaseFlags._raise(this.address, flag.value);
    }

    private static native void _raise(long var0, int var2);

    public void clear(PxParticlePhaseFlagEnum flag) {
        this.checkNotNull();
        PxParticlePhaseFlags._clear(this.address, flag.value);
    }

    private static native void _clear(long var0, int var2);

    static {
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticlePhaseFlags");
        SIZEOF = PxParticlePhaseFlags.__sizeOf();
    }
}

