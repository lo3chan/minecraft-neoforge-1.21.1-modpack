/*
 * Decompiled with CFR 0.152.
 */
package physx.support;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.support.OmniPvdFileWriteStream;
import physx.support.OmniPvdWriter;

public class PxOmniPvd
extends NativeObject {
    public static final int SIZEOF = PxOmniPvd.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxOmniPvd() {
    }

    private static native int __sizeOf();

    public static PxOmniPvd wrapPointer(long address) {
        return address != 0L ? new PxOmniPvd(address) : null;
    }

    public static PxOmniPvd arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxOmniPvd.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxOmniPvd(long address) {
        super(address);
    }

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxOmniPvd._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public OmniPvdWriter getWriter() {
        this.checkNotNull();
        PlatformChecks.requirePlatform(7, "physx.support.PxOmniPvd");
        return OmniPvdWriter.wrapPointer(PxOmniPvd._getWriter(this.address));
    }

    private static native long _getWriter(long var0);

    public OmniPvdFileWriteStream getFileWriteStream() {
        this.checkNotNull();
        PlatformChecks.requirePlatform(7, "physx.support.PxOmniPvd");
        return OmniPvdFileWriteStream.wrapPointer(PxOmniPvd._getFileWriteStream(this.address));
    }

    private static native long _getFileWriteStream(long var0);

    public boolean startSampling() {
        this.checkNotNull();
        return PxOmniPvd._startSampling(this.address);
    }

    private static native boolean _startSampling(long var0);

    public void release() {
        this.checkNotNull();
        PxOmniPvd._release(this.address);
    }

    private static native void _release(long var0);
}

