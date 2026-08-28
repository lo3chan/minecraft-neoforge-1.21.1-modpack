/*
 * Decompiled with CFR 0.152.
 */
package physx.support;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.support.OmniPvdWriteStream;

public class OmniPvdWriter
extends NativeObject {
    public static final int SIZEOF;
    public static final int ALIGNOF = 8;

    protected OmniPvdWriter() {
    }

    private static native int __sizeOf();

    public static OmniPvdWriter wrapPointer(long address) {
        return address != 0L ? new OmniPvdWriter(address) : null;
    }

    public static OmniPvdWriter arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return OmniPvdWriter.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected OmniPvdWriter(long address) {
        super(address);
    }

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        OmniPvdWriter._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public void setWriteStream(OmniPvdWriteStream writeStream) {
        this.checkNotNull();
        OmniPvdWriter._setWriteStream(this.address, writeStream.getAddress());
    }

    private static native void _setWriteStream(long var0, long var2);

    static {
        PlatformChecks.requirePlatform(7, "physx.support.OmniPvdWriter");
        SIZEOF = OmniPvdWriter.__sizeOf();
    }
}

