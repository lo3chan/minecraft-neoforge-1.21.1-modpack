/*
 * Decompiled with CFR 0.152.
 */
package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.particles.PxParticleBufferFlagEnum;

public class PxParticleBufferFlags
extends NativeObject {
    public static final int SIZEOF;
    public static final int ALIGNOF = 8;

    protected PxParticleBufferFlags() {
    }

    private static native int __sizeOf();

    public static PxParticleBufferFlags wrapPointer(long address) {
        return address != 0L ? new PxParticleBufferFlags(address) : null;
    }

    public static PxParticleBufferFlags arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxParticleBufferFlags.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxParticleBufferFlags(long address) {
        super(address);
    }

    public static PxParticleBufferFlags createAt(long address, int flags) {
        PxParticleBufferFlags.__placement_new_PxParticleBufferFlags(address, flags);
        PxParticleBufferFlags createdObj = PxParticleBufferFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxParticleBufferFlags createAt(T allocator, NativeObject.Allocator<T> allocate, int flags) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxParticleBufferFlags.__placement_new_PxParticleBufferFlags(address, flags);
        PxParticleBufferFlags createdObj = PxParticleBufferFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxParticleBufferFlags(long var0, int var2);

    public PxParticleBufferFlags(int flags) {
        this.address = PxParticleBufferFlags._PxParticleBufferFlags(flags);
    }

    private static native long _PxParticleBufferFlags(int var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxParticleBufferFlags._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public boolean isSet(PxParticleBufferFlagEnum flag) {
        this.checkNotNull();
        return PxParticleBufferFlags._isSet(this.address, flag.value);
    }

    private static native boolean _isSet(long var0, int var2);

    public void raise(PxParticleBufferFlagEnum flag) {
        this.checkNotNull();
        PxParticleBufferFlags._raise(this.address, flag.value);
    }

    private static native void _raise(long var0, int var2);

    public void clear(PxParticleBufferFlagEnum flag) {
        this.checkNotNull();
        PxParticleBufferFlags._clear(this.address, flag.value);
    }

    private static native void _clear(long var0, int var2);

    static {
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticleBufferFlags");
        SIZEOF = PxParticleBufferFlags.__sizeOf();
    }
}

