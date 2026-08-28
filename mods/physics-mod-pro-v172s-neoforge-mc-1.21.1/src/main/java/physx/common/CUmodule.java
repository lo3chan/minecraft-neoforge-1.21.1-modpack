/*
 * Decompiled with CFR 0.152.
 */
package physx.common;

import physx.NativeObject;
import physx.PlatformChecks;

public class CUmodule
extends NativeObject {
    public static final int SIZEOF;
    public static final int ALIGNOF = 8;

    protected CUmodule() {
    }

    private static native int __sizeOf();

    public static CUmodule wrapPointer(long address) {
        return address != 0L ? new CUmodule(address) : null;
    }

    public static CUmodule arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return CUmodule.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected CUmodule(long address) {
        super(address);
    }

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        CUmodule._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    static {
        PlatformChecks.requirePlatform(3, "physx.common.CUmodule");
        SIZEOF = CUmodule.__sizeOf();
    }
}

