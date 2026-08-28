/*
 * Decompiled with CFR 0.152.
 */
package physx.support;

import physx.NativeObject;

public class PxPvdTransport
extends NativeObject {
    public static final int SIZEOF = PxPvdTransport.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxPvdTransport() {
    }

    private static native int __sizeOf();

    public static PxPvdTransport wrapPointer(long address) {
        return address != 0L ? new PxPvdTransport(address) : null;
    }

    public static PxPvdTransport arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxPvdTransport.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxPvdTransport(long address) {
        super(address);
    }

    public boolean connect() {
        this.checkNotNull();
        return PxPvdTransport._connect(this.address);
    }

    private static native boolean _connect(long var0);

    public boolean isConnected() {
        this.checkNotNull();
        return PxPvdTransport._isConnected(this.address);
    }

    private static native boolean _isConnected(long var0);

    public void disconnect() {
        this.checkNotNull();
        PxPvdTransport._disconnect(this.address);
    }

    private static native void _disconnect(long var0);

    public void release() {
        this.checkNotNull();
        PxPvdTransport._release(this.address);
    }

    private static native void _release(long var0);

    public void flush() {
        this.checkNotNull();
        PxPvdTransport._flush(this.address);
    }

    private static native void _flush(long var0);
}

