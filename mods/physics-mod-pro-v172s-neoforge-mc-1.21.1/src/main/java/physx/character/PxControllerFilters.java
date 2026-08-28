/*
 * Decompiled with CFR 0.152.
 */
package physx.character;

import physx.NativeObject;
import physx.character.PxControllerFilterCallback;
import physx.physics.PxFilterData;
import physx.physics.PxQueryFilterCallback;
import physx.physics.PxQueryFlags;

public class PxControllerFilters
extends NativeObject {
    public static final int SIZEOF = PxControllerFilters.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxControllerFilters wrapPointer(long address) {
        return address != 0L ? new PxControllerFilters(address) : null;
    }

    public static PxControllerFilters arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxControllerFilters.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxControllerFilters(long address) {
        super(address);
    }

    public PxControllerFilters() {
        this.address = PxControllerFilters._PxControllerFilters();
    }

    private static native long _PxControllerFilters();

    public PxControllerFilters(PxFilterData filterData) {
        this.address = PxControllerFilters._PxControllerFilters(filterData.getAddress());
    }

    private static native long _PxControllerFilters(long var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxControllerFilters._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxFilterData getMFilterData() {
        this.checkNotNull();
        return PxFilterData.wrapPointer(PxControllerFilters._getMFilterData(this.address));
    }

    private static native long _getMFilterData(long var0);

    public void setMFilterData(PxFilterData value) {
        this.checkNotNull();
        PxControllerFilters._setMFilterData(this.address, value.getAddress());
    }

    private static native void _setMFilterData(long var0, long var2);

    public PxQueryFilterCallback getMFilterCallback() {
        this.checkNotNull();
        return PxQueryFilterCallback.wrapPointer(PxControllerFilters._getMFilterCallback(this.address));
    }

    private static native long _getMFilterCallback(long var0);

    public void setMFilterCallback(PxQueryFilterCallback value) {
        this.checkNotNull();
        PxControllerFilters._setMFilterCallback(this.address, value.getAddress());
    }

    private static native void _setMFilterCallback(long var0, long var2);

    public PxQueryFlags getMFilterFlags() {
        this.checkNotNull();
        return PxQueryFlags.wrapPointer(PxControllerFilters._getMFilterFlags(this.address));
    }

    private static native long _getMFilterFlags(long var0);

    public void setMFilterFlags(PxQueryFlags value) {
        this.checkNotNull();
        PxControllerFilters._setMFilterFlags(this.address, value.getAddress());
    }

    private static native void _setMFilterFlags(long var0, long var2);

    public PxControllerFilterCallback getMCCTFilterCallback() {
        this.checkNotNull();
        return PxControllerFilterCallback.wrapPointer(PxControllerFilters._getMCCTFilterCallback(this.address));
    }

    private static native long _getMCCTFilterCallback(long var0);

    public void setMCCTFilterCallback(PxControllerFilterCallback value) {
        this.checkNotNull();
        PxControllerFilters._setMCCTFilterCallback(this.address, value.getAddress());
    }

    private static native void _setMCCTFilterCallback(long var0, long var2);
}

