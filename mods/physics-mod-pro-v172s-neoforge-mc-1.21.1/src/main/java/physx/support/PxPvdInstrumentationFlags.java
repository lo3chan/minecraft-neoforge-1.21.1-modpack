/*
 * Decompiled with CFR 0.152.
 */
package physx.support;

import physx.NativeObject;
import physx.support.PxPvdInstrumentationFlagEnum;

public class PxPvdInstrumentationFlags
extends NativeObject {
    public static final int SIZEOF = PxPvdInstrumentationFlags.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxPvdInstrumentationFlags() {
    }

    private static native int __sizeOf();

    public static PxPvdInstrumentationFlags wrapPointer(long address) {
        return address != 0L ? new PxPvdInstrumentationFlags(address) : null;
    }

    public static PxPvdInstrumentationFlags arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxPvdInstrumentationFlags.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxPvdInstrumentationFlags(long address) {
        super(address);
    }

    public PxPvdInstrumentationFlags(byte flags) {
        this.address = PxPvdInstrumentationFlags._PxPvdInstrumentationFlags(flags);
    }

    private static native long _PxPvdInstrumentationFlags(byte var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxPvdInstrumentationFlags._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public boolean isSet(PxPvdInstrumentationFlagEnum flag) {
        this.checkNotNull();
        return PxPvdInstrumentationFlags._isSet(this.address, flag.value);
    }

    private static native boolean _isSet(long var0, int var2);

    public void raise(PxPvdInstrumentationFlagEnum flag) {
        this.checkNotNull();
        PxPvdInstrumentationFlags._raise(this.address, flag.value);
    }

    private static native void _raise(long var0, int var2);

    public void clear(PxPvdInstrumentationFlagEnum flag) {
        this.checkNotNull();
        PxPvdInstrumentationFlags._clear(this.address, flag.value);
    }

    private static native void _clear(long var0, int var2);
}

