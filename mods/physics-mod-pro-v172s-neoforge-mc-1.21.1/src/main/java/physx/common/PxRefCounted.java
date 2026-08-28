/*
 * Decompiled with CFR 0.152.
 */
package physx.common;

import physx.common.PxBase;

public class PxRefCounted
extends PxBase {
    public static final int SIZEOF = PxRefCounted.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxRefCounted() {
    }

    private static native int __sizeOf();

    public static PxRefCounted wrapPointer(long address) {
        return address != 0L ? new PxRefCounted(address) : null;
    }

    public static PxRefCounted arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxRefCounted.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxRefCounted(long address) {
        super(address);
    }

    public int getReferenceCount() {
        this.checkNotNull();
        return PxRefCounted._getReferenceCount(this.address);
    }

    private static native int _getReferenceCount(long var0);

    public void acquireReference() {
        this.checkNotNull();
        PxRefCounted._acquireReference(this.address);
    }

    private static native void _acquireReference(long var0);
}

