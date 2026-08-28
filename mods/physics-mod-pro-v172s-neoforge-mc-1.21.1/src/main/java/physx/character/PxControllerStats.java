/*
 * Decompiled with CFR 0.152.
 */
package physx.character;

import physx.NativeObject;

public class PxControllerStats
extends NativeObject {
    public static final int SIZEOF = PxControllerStats.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxControllerStats() {
    }

    private static native int __sizeOf();

    public static PxControllerStats wrapPointer(long address) {
        return address != 0L ? new PxControllerStats(address) : null;
    }

    public static PxControllerStats arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxControllerStats.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxControllerStats(long address) {
        super(address);
    }

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxControllerStats._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public short getNbIterations() {
        this.checkNotNull();
        return PxControllerStats._getNbIterations(this.address);
    }

    private static native short _getNbIterations(long var0);

    public void setNbIterations(short value) {
        this.checkNotNull();
        PxControllerStats._setNbIterations(this.address, value);
    }

    private static native void _setNbIterations(long var0, short var2);

    public short getNbFullUpdates() {
        this.checkNotNull();
        return PxControllerStats._getNbFullUpdates(this.address);
    }

    private static native short _getNbFullUpdates(long var0);

    public void setNbFullUpdates(short value) {
        this.checkNotNull();
        PxControllerStats._setNbFullUpdates(this.address, value);
    }

    private static native void _setNbFullUpdates(long var0, short var2);

    public short getNbPartialUpdates() {
        this.checkNotNull();
        return PxControllerStats._getNbPartialUpdates(this.address);
    }

    private static native short _getNbPartialUpdates(long var0);

    public void setNbPartialUpdates(short value) {
        this.checkNotNull();
        PxControllerStats._setNbPartialUpdates(this.address, value);
    }

    private static native void _setNbPartialUpdates(long var0, short var2);

    public short getNbTessellation() {
        this.checkNotNull();
        return PxControllerStats._getNbTessellation(this.address);
    }

    private static native short _getNbTessellation(long var0);

    public void setNbTessellation(short value) {
        this.checkNotNull();
        PxControllerStats._setNbTessellation(this.address, value);
    }

    private static native void _setNbTessellation(long var0, short var2);
}

