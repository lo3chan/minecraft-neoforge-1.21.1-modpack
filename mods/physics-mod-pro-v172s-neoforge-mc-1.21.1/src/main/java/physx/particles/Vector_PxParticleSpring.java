/*
 * Decompiled with CFR 0.152.
 */
package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.particles.PxParticleSpring;

@Deprecated
public class Vector_PxParticleSpring
extends NativeObject {
    public static final int SIZEOF;
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static Vector_PxParticleSpring wrapPointer(long address) {
        return address != 0L ? new Vector_PxParticleSpring(address) : null;
    }

    public static Vector_PxParticleSpring arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return Vector_PxParticleSpring.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected Vector_PxParticleSpring(long address) {
        super(address);
    }

    public Vector_PxParticleSpring() {
        this.address = Vector_PxParticleSpring._Vector_PxParticleSpring();
    }

    private static native long _Vector_PxParticleSpring();

    public Vector_PxParticleSpring(int size) {
        this.address = Vector_PxParticleSpring._Vector_PxParticleSpring(size);
    }

    private static native long _Vector_PxParticleSpring(int var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        Vector_PxParticleSpring._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxParticleSpring at(int index) {
        this.checkNotNull();
        return PxParticleSpring.wrapPointer(Vector_PxParticleSpring._at(this.address, index));
    }

    private static native long _at(long var0, int var2);

    public PxParticleSpring data() {
        this.checkNotNull();
        return PxParticleSpring.wrapPointer(Vector_PxParticleSpring._data(this.address));
    }

    private static native long _data(long var0);

    public int size() {
        this.checkNotNull();
        return Vector_PxParticleSpring._size(this.address);
    }

    private static native int _size(long var0);

    public void push_back(PxParticleSpring value) {
        this.checkNotNull();
        Vector_PxParticleSpring._push_back(this.address, value.getAddress());
    }

    private static native void _push_back(long var0, long var2);

    public void clear() {
        this.checkNotNull();
        Vector_PxParticleSpring._clear(this.address);
    }

    private static native void _clear(long var0);

    static {
        PlatformChecks.requirePlatform(3, "physx.particles.Vector_PxParticleSpring");
        SIZEOF = Vector_PxParticleSpring.__sizeOf();
    }
}

