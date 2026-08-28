/*
 * Decompiled with CFR 0.152.
 */
package physx.geometry;

import physx.PlatformChecks;
import physx.geometry.PxGeometry;
import physx.geometry.SimpleCustomGeometryCallbacks;

public class PxCustomGeometry
extends PxGeometry {
    public static final int SIZEOF;
    public static final int ALIGNOF = 8;

    protected PxCustomGeometry() {
    }

    private static native int __sizeOf();

    public static PxCustomGeometry wrapPointer(long address) {
        return address != 0L ? new PxCustomGeometry(address) : null;
    }

    public static PxCustomGeometry arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxCustomGeometry.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxCustomGeometry(long address) {
        super(address);
    }

    public PxCustomGeometry(SimpleCustomGeometryCallbacks callbacks) {
        this.address = PxCustomGeometry._PxCustomGeometry(callbacks.getAddress());
    }

    private static native long _PxCustomGeometry(long var0);

    @Override
    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxCustomGeometry._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public boolean isValid() {
        this.checkNotNull();
        return PxCustomGeometry._isValid(this.address);
    }

    private static native boolean _isValid(long var0);

    static {
        PlatformChecks.requirePlatform(15, "physx.geometry.PxCustomGeometry");
        SIZEOF = PxCustomGeometry.__sizeOf();
    }
}

