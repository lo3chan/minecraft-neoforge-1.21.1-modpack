/*
 * Decompiled with CFR 0.152.
 */
package physx;

import de.fabmax.physxjni.Loader;

public class NativeObject {
    public static final int SIZEOF_POINTER;
    public static final int SIZEOF_BYTE = 1;
    public static final int SIZEOF_SHORT = 2;
    public static final int SIZEOF_INT = 4;
    public static final int SIZEOF_LONG = 8;
    public static final int SIZEOF_FLOAT = 4;
    public static final int SIZEOF_DOUBLE = 8;
    protected long address = 0L;
    protected boolean isExternallyAllocated = false;

    protected NativeObject() {
    }

    private static native int __sizeOfPointer();

    protected NativeObject(long address) {
        this.address = address;
    }

    public static NativeObject wrapPointer(long address) {
        return new NativeObject(address);
    }

    protected void checkNotNull() {
        if (this.address == 0L) {
            throw new NullPointerException("Native address of " + this + " is 0");
        }
    }

    public long getAddress() {
        return this.address;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NativeObject)) {
            return false;
        }
        NativeObject that = (NativeObject)o;
        return this.address == that.address;
    }

    public int hashCode() {
        return (int)(this.address ^ this.address >>> 32);
    }

    static {
        Loader.load();
        SIZEOF_POINTER = NativeObject.__sizeOfPointer();
    }

    @FunctionalInterface
    public static interface Allocator<T> {
        public long on(T var1, int var2, int var3);
    }
}

