/*
 * Decompiled with CFR 0.152.
 */
package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.particles.PxParticleFlagEnum;

public class PxParticleFlags
extends NativeObject {
    public static final int SIZEOF;
    public static final int ALIGNOF = 8;

    protected PxParticleFlags() {
    }

    private static native int __sizeOf();

    public static PxParticleFlags wrapPointer(long address) {
        return address != 0L ? new PxParticleFlags(address) : null;
    }

    public static PxParticleFlags arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxParticleFlags.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxParticleFlags(long address) {
        super(address);
    }

    public static PxParticleFlags createAt(long address, int flags) {
        PxParticleFlags.__placement_new_PxParticleFlags(address, flags);
        PxParticleFlags createdObj = PxParticleFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxParticleFlags createAt(T allocator, NativeObject.Allocator<T> allocate, int flags) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxParticleFlags.__placement_new_PxParticleFlags(address, flags);
        PxParticleFlags createdObj = PxParticleFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxParticleFlags(long var0, int var2);

    public PxParticleFlags(int flags) {
        this.address = PxParticleFlags._PxParticleFlags(flags);
    }

    private static native long _PxParticleFlags(int var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxParticleFlags._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public boolean isSet(PxParticleFlagEnum flag) {
        this.checkNotNull();
        return PxParticleFlags._isSet(this.address, flag.value);
    }

    private static native boolean _isSet(long var0, int var2);

    public void raise(PxParticleFlagEnum flag) {
        this.checkNotNull();
        PxParticleFlags._raise(this.address, flag.value);
    }

    private static native void _raise(long var0, int var2);

    public void clear(PxParticleFlagEnum flag) {
        this.checkNotNull();
        PxParticleFlags._clear(this.address, flag.value);
    }

    private static native void _clear(long var0, int var2);

    static {
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticleFlags");
        SIZEOF = PxParticleFlags.__sizeOf();
    }
}

