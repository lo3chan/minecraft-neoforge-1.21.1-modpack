/*
 * Decompiled with CFR 0.152.
 */
package physx.support;

import physx.NativeObject;
import physx.support.PxPvdTransport;

public class SimplePvdTransport
extends PxPvdTransport {
    public static final int SIZEOF = SimplePvdTransport.__sizeOf();
    public static final int ALIGNOF = 8;

    protected SimplePvdTransport() {
    }

    private static native int __sizeOf();

    public static SimplePvdTransport wrapPointer(long address) {
        return address != 0L ? new SimplePvdTransport(address) : null;
    }

    public static SimplePvdTransport arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return SimplePvdTransport.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected SimplePvdTransport(long address) {
        super(address);
    }

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        SimplePvdTransport._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public void send(NativeObject inBytes, int inLength) {
        this.checkNotNull();
        SimplePvdTransport._send(this.address, inBytes.getAddress(), inLength);
    }

    private static native void _send(long var0, long var2, int var4);
}

