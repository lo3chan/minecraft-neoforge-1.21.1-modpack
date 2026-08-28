/*
 * Decompiled with CFR 0.152.
 */
package physx.extensions;

import physx.NativeObject;
import physx.common.PxOutputStream;

public class PxDefaultMemoryOutputStream
extends PxOutputStream {
    public static final int SIZEOF = PxDefaultMemoryOutputStream.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxDefaultMemoryOutputStream wrapPointer(long address) {
        return address != 0L ? new PxDefaultMemoryOutputStream(address) : null;
    }

    public static PxDefaultMemoryOutputStream arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxDefaultMemoryOutputStream.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxDefaultMemoryOutputStream(long address) {
        super(address);
    }

    public PxDefaultMemoryOutputStream() {
        this.address = PxDefaultMemoryOutputStream._PxDefaultMemoryOutputStream();
    }

    private static native long _PxDefaultMemoryOutputStream();

    @Override
    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxDefaultMemoryOutputStream._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public void write(NativeObject src, int count) {
        this.checkNotNull();
        PxDefaultMemoryOutputStream._write(this.address, src.getAddress(), count);
    }

    private static native void _write(long var0, long var2, int var4);

    public int getSize() {
        this.checkNotNull();
        return PxDefaultMemoryOutputStream._getSize(this.address);
    }

    private static native int _getSize(long var0);

    public NativeObject getData() {
        this.checkNotNull();
        return NativeObject.wrapPointer(PxDefaultMemoryOutputStream._getData(this.address));
    }

    private static native long _getData(long var0);
}

