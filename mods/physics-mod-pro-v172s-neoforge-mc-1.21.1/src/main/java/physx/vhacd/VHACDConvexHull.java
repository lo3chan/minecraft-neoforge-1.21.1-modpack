/*
 * Decompiled with CFR 0.152.
 */
package physx.vhacd;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.vhacd.Vector_VHACDTriangle;
import physx.vhacd.Vector_VHACDVertex;

public class VHACDConvexHull
extends NativeObject {
    public static final int SIZEOF;
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static VHACDConvexHull wrapPointer(long address) {
        return address != 0L ? new VHACDConvexHull(address) : null;
    }

    public static VHACDConvexHull arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return VHACDConvexHull.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected VHACDConvexHull(long address) {
        super(address);
    }

    public static VHACDConvexHull createAt(long address) {
        VHACDConvexHull.__placement_new_VHACDConvexHull(address);
        VHACDConvexHull createdObj = VHACDConvexHull.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> VHACDConvexHull createAt(T allocator, NativeObject.Allocator<T> allocate) {
        long address = allocate.on(allocator, 8, SIZEOF);
        VHACDConvexHull.__placement_new_VHACDConvexHull(address);
        VHACDConvexHull createdObj = VHACDConvexHull.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_VHACDConvexHull(long var0);

    public VHACDConvexHull() {
        this.address = VHACDConvexHull._VHACDConvexHull();
    }

    private static native long _VHACDConvexHull();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        VHACDConvexHull._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public Vector_VHACDVertex getM_points() {
        this.checkNotNull();
        return Vector_VHACDVertex.wrapPointer(VHACDConvexHull._getM_points(this.address));
    }

    private static native long _getM_points(long var0);

    public void setM_points(Vector_VHACDVertex value) {
        this.checkNotNull();
        VHACDConvexHull._setM_points(this.address, value.getAddress());
    }

    private static native void _setM_points(long var0, long var2);

    public Vector_VHACDTriangle getM_triangles() {
        this.checkNotNull();
        return Vector_VHACDTriangle.wrapPointer(VHACDConvexHull._getM_triangles(this.address));
    }

    private static native long _getM_triangles(long var0);

    public void setM_triangles(Vector_VHACDTriangle value) {
        this.checkNotNull();
        VHACDConvexHull._setM_triangles(this.address, value.getAddress());
    }

    private static native void _setM_triangles(long var0, long var2);

    public double getM_volume() {
        this.checkNotNull();
        return VHACDConvexHull._getM_volume(this.address);
    }

    private static native double _getM_volume(long var0);

    public void setM_volume(double value) {
        this.checkNotNull();
        VHACDConvexHull._setM_volume(this.address, value);
    }

    private static native void _setM_volume(long var0, double var2);

    public int getM_meshId() {
        this.checkNotNull();
        return VHACDConvexHull._getM_meshId(this.address);
    }

    private static native int _getM_meshId(long var0);

    public void setM_meshId(int value) {
        this.checkNotNull();
        VHACDConvexHull._setM_meshId(this.address, value);
    }

    private static native void _setM_meshId(long var0, int var2);

    static {
        PlatformChecks.requirePlatform(15, "physx.vhacd.VHACDConvexHull");
        SIZEOF = VHACDConvexHull.__sizeOf();
    }
}

