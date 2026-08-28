/*
 * Decompiled with CFR 0.152.
 */
package physx.vhacd;

import physx.NativeObject;
import physx.PlatformChecks;

public class VHACDTriangle
extends NativeObject {
    public static final int SIZEOF;
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static VHACDTriangle wrapPointer(long address) {
        return address != 0L ? new VHACDTriangle(address) : null;
    }

    public static VHACDTriangle arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return VHACDTriangle.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected VHACDTriangle(long address) {
        super(address);
    }

    public VHACDTriangle() {
        this.address = VHACDTriangle._VHACDTriangle();
    }

    private static native long _VHACDTriangle();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        VHACDTriangle._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public int getMI0() {
        this.checkNotNull();
        return VHACDTriangle._getMI0(this.address);
    }

    private static native int _getMI0(long var0);

    public void setMI0(int value) {
        this.checkNotNull();
        VHACDTriangle._setMI0(this.address, value);
    }

    private static native void _setMI0(long var0, int var2);

    public int getMI1() {
        this.checkNotNull();
        return VHACDTriangle._getMI1(this.address);
    }

    private static native int _getMI1(long var0);

    public void setMI1(int value) {
        this.checkNotNull();
        VHACDTriangle._setMI1(this.address, value);
    }

    private static native void _setMI1(long var0, int var2);

    public int getMI2() {
        this.checkNotNull();
        return VHACDTriangle._getMI2(this.address);
    }

    private static native int _getMI2(long var0);

    public void setMI2(int value) {
        this.checkNotNull();
        VHACDTriangle._setMI2(this.address, value);
    }

    private static native void _setMI2(long var0, int var2);

    static {
        PlatformChecks.requirePlatform(15, "physx.vhacd.VHACDTriangle");
        SIZEOF = VHACDTriangle.__sizeOf();
    }
}

