/*
 * Decompiled with CFR 0.152.
 */
package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.particles.PxParticleRigidFilterPair;

@Deprecated
public class Vector_PxParticleRigidFilterPair
extends NativeObject {
    public static final int SIZEOF;
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static Vector_PxParticleRigidFilterPair wrapPointer(long address) {
        return address != 0L ? new Vector_PxParticleRigidFilterPair(address) : null;
    }

    public static Vector_PxParticleRigidFilterPair arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return Vector_PxParticleRigidFilterPair.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected Vector_PxParticleRigidFilterPair(long address) {
        super(address);
    }

    public Vector_PxParticleRigidFilterPair() {
        this.address = Vector_PxParticleRigidFilterPair._Vector_PxParticleRigidFilterPair();
    }

    private static native long _Vector_PxParticleRigidFilterPair();

    public Vector_PxParticleRigidFilterPair(int size) {
        this.address = Vector_PxParticleRigidFilterPair._Vector_PxParticleRigidFilterPair(size);
    }

    private static native long _Vector_PxParticleRigidFilterPair(int var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        Vector_PxParticleRigidFilterPair._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxParticleRigidFilterPair at(int index) {
        this.checkNotNull();
        return PxParticleRigidFilterPair.wrapPointer(Vector_PxParticleRigidFilterPair._at(this.address, index));
    }

    private static native long _at(long var0, int var2);

    public PxParticleRigidFilterPair data() {
        this.checkNotNull();
        return PxParticleRigidFilterPair.wrapPointer(Vector_PxParticleRigidFilterPair._data(this.address));
    }

    private static native long _data(long var0);

    public int size() {
        this.checkNotNull();
        return Vector_PxParticleRigidFilterPair._size(this.address);
    }

    private static native int _size(long var0);

    public void push_back(PxParticleRigidFilterPair value) {
        this.checkNotNull();
        Vector_PxParticleRigidFilterPair._push_back(this.address, value.getAddress());
    }

    private static native void _push_back(long var0, long var2);

    public void clear() {
        this.checkNotNull();
        Vector_PxParticleRigidFilterPair._clear(this.address);
    }

    private static native void _clear(long var0);

    static {
        PlatformChecks.requirePlatform(3, "physx.particles.Vector_PxParticleRigidFilterPair");
        SIZEOF = Vector_PxParticleRigidFilterPair.__sizeOf();
    }
}

