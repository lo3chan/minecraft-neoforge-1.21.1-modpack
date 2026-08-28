/*
 * Decompiled with CFR 0.152.
 */
package physx.common;

import physx.NativeObject;

public class PxFoundation
extends NativeObject {
    public static final int SIZEOF = PxFoundation.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxFoundation() {
    }

    private static native int __sizeOf();

    public static PxFoundation wrapPointer(long address) {
        return address != 0L ? new PxFoundation(address) : null;
    }

    public static PxFoundation arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxFoundation.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxFoundation(long address) {
        super(address);
    }

    public void release() {
        this.checkNotNull();
        PxFoundation._release(this.address);
    }

    private static native void _release(long var0);
}

