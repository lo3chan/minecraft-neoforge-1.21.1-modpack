/*
 * Decompiled with CFR 0.152.
 */
package physx.vhacd;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.support.PxRealConstPtr;
import physx.support.PxU32ConstPtr;
import physx.vhacd.VHACDConvexHull;
import physx.vhacd.VHACDParameters;

public class VHACDImpl
extends NativeObject {
    public static final int SIZEOF;
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static VHACDImpl wrapPointer(long address) {
        return address != 0L ? new VHACDImpl(address) : null;
    }

    public static VHACDImpl arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return VHACDImpl.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected VHACDImpl(long address) {
        super(address);
    }

    public VHACDImpl() {
        this.address = VHACDImpl._VHACDImpl();
    }

    private static native long _VHACDImpl();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        VHACDImpl._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public boolean Compute(PxRealConstPtr points, int countPoints, PxU32ConstPtr triangles, int countTriangles, VHACDParameters params) {
        this.checkNotNull();
        return VHACDImpl._Compute(this.address, points.getAddress(), countPoints, triangles.getAddress(), countTriangles, params.getAddress());
    }

    private static native boolean _Compute(long var0, long var2, int var4, long var5, int var7, long var8);

    public int GetNConvexHulls() {
        this.checkNotNull();
        return VHACDImpl._GetNConvexHulls(this.address);
    }

    private static native int _GetNConvexHulls(long var0);

    public boolean GetConvexHull(int index, VHACDConvexHull ch) {
        this.checkNotNull();
        return VHACDImpl._GetConvexHull(this.address, index, ch.getAddress());
    }

    private static native boolean _GetConvexHull(long var0, int var2, long var3);

    static {
        PlatformChecks.requirePlatform(15, "physx.vhacd.VHACDImpl");
        SIZEOF = VHACDImpl.__sizeOf();
    }
}

