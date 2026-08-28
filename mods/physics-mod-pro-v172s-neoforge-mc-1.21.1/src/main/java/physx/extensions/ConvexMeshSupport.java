/*
 * Decompiled with CFR 0.152.
 */
package physx.extensions;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.common.PxQuat;
import physx.common.PxVec3;
import physx.extensions.Support;
import physx.geometry.PxConvexMesh;

public class ConvexMeshSupport
extends Support {
    public static final int SIZEOF = ConvexMeshSupport.__sizeOf();
    public static final int ALIGNOF = 8;

    protected ConvexMeshSupport() {
    }

    private static native int __sizeOf();

    public static ConvexMeshSupport wrapPointer(long address) {
        return address != 0L ? new ConvexMeshSupport(address) : null;
    }

    public static ConvexMeshSupport arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return ConvexMeshSupport.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected ConvexMeshSupport(long address) {
        super(address);
    }

    public static ConvexMeshSupport createAt(long address, PxConvexMesh convexMesh) {
        ConvexMeshSupport.__placement_new_ConvexMeshSupport(address, convexMesh.getAddress());
        ConvexMeshSupport createdObj = ConvexMeshSupport.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> ConvexMeshSupport createAt(T allocator, NativeObject.Allocator<T> allocate, PxConvexMesh convexMesh) {
        long address = allocate.on(allocator, 8, SIZEOF);
        ConvexMeshSupport.__placement_new_ConvexMeshSupport(address, convexMesh.getAddress());
        ConvexMeshSupport createdObj = ConvexMeshSupport.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_ConvexMeshSupport(long var0, long var2);

    public static ConvexMeshSupport createAt(long address, PxConvexMesh convexMesh, PxVec3 scale) {
        ConvexMeshSupport.__placement_new_ConvexMeshSupport(address, convexMesh.getAddress(), scale.getAddress());
        ConvexMeshSupport createdObj = ConvexMeshSupport.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> ConvexMeshSupport createAt(T allocator, NativeObject.Allocator<T> allocate, PxConvexMesh convexMesh, PxVec3 scale) {
        long address = allocate.on(allocator, 8, SIZEOF);
        ConvexMeshSupport.__placement_new_ConvexMeshSupport(address, convexMesh.getAddress(), scale.getAddress());
        ConvexMeshSupport createdObj = ConvexMeshSupport.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_ConvexMeshSupport(long var0, long var2, long var4);

    public static ConvexMeshSupport createAt(long address, PxConvexMesh convexMesh, PxVec3 scale, PxQuat scaleRotation) {
        ConvexMeshSupport.__placement_new_ConvexMeshSupport(address, convexMesh.getAddress(), scale.getAddress(), scaleRotation.getAddress());
        ConvexMeshSupport createdObj = ConvexMeshSupport.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> ConvexMeshSupport createAt(T allocator, NativeObject.Allocator<T> allocate, PxConvexMesh convexMesh, PxVec3 scale, PxQuat scaleRotation) {
        long address = allocate.on(allocator, 8, SIZEOF);
        ConvexMeshSupport.__placement_new_ConvexMeshSupport(address, convexMesh.getAddress(), scale.getAddress(), scaleRotation.getAddress());
        ConvexMeshSupport createdObj = ConvexMeshSupport.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_ConvexMeshSupport(long var0, long var2, long var4, long var6);

    public static ConvexMeshSupport createAt(long address, PxConvexMesh convexMesh, PxVec3 scale, PxQuat scaleRotation, float margin) {
        ConvexMeshSupport.__placement_new_ConvexMeshSupport(address, convexMesh.getAddress(), scale.getAddress(), scaleRotation.getAddress(), margin);
        ConvexMeshSupport createdObj = ConvexMeshSupport.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> ConvexMeshSupport createAt(T allocator, NativeObject.Allocator<T> allocate, PxConvexMesh convexMesh, PxVec3 scale, PxQuat scaleRotation, float margin) {
        long address = allocate.on(allocator, 8, SIZEOF);
        ConvexMeshSupport.__placement_new_ConvexMeshSupport(address, convexMesh.getAddress(), scale.getAddress(), scaleRotation.getAddress(), margin);
        ConvexMeshSupport createdObj = ConvexMeshSupport.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_ConvexMeshSupport(long var0, long var2, long var4, long var6, float var8);

    public ConvexMeshSupport(PxConvexMesh convexMesh) {
        this.address = ConvexMeshSupport._ConvexMeshSupport(convexMesh.getAddress());
    }

    private static native long _ConvexMeshSupport(long var0);

    public ConvexMeshSupport(PxConvexMesh convexMesh, PxVec3 scale) {
        this.address = ConvexMeshSupport._ConvexMeshSupport(convexMesh.getAddress(), scale.getAddress());
    }

    private static native long _ConvexMeshSupport(long var0, long var2);

    public ConvexMeshSupport(PxConvexMesh convexMesh, PxVec3 scale, PxQuat scaleRotation) {
        this.address = ConvexMeshSupport._ConvexMeshSupport(convexMesh.getAddress(), scale.getAddress(), scaleRotation.getAddress());
    }

    private static native long _ConvexMeshSupport(long var0, long var2, long var4);

    public ConvexMeshSupport(PxConvexMesh convexMesh, PxVec3 scale, PxQuat scaleRotation, float margin) {
        this.address = ConvexMeshSupport._ConvexMeshSupport(convexMesh.getAddress(), scale.getAddress(), scaleRotation.getAddress(), margin);
    }

    private static native long _ConvexMeshSupport(long var0, long var2, long var4, float var6);

    @Override
    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        ConvexMeshSupport._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxConvexMesh getConvexMesh() {
        this.checkNotNull();
        PlatformChecks.requirePlatform(15, "physx.extensions.ConvexMeshSupport");
        return PxConvexMesh.wrapPointer(ConvexMeshSupport._getConvexMesh(this.address));
    }

    private static native long _getConvexMesh(long var0);

    public PxVec3 getScale() {
        this.checkNotNull();
        return PxVec3.wrapPointer(ConvexMeshSupport._getScale(this.address));
    }

    private static native long _getScale(long var0);

    public void setScale(PxVec3 value) {
        this.checkNotNull();
        ConvexMeshSupport._setScale(this.address, value.getAddress());
    }

    private static native void _setScale(long var0, long var2);

    public PxQuat getScaleRotation() {
        this.checkNotNull();
        return PxQuat.wrapPointer(ConvexMeshSupport._getScaleRotation(this.address));
    }

    private static native long _getScaleRotation(long var0);

    public void setScaleRotation(PxQuat value) {
        this.checkNotNull();
        ConvexMeshSupport._setScaleRotation(this.address, value.getAddress());
    }

    private static native void _setScaleRotation(long var0, long var2);

    @Override
    public float getMargin() {
        this.checkNotNull();
        return ConvexMeshSupport._getMargin(this.address);
    }

    private static native float _getMargin(long var0);

    public void setMargin(float value) {
        this.checkNotNull();
        ConvexMeshSupport._setMargin(this.address, value);
    }

    private static native void _setMargin(long var0, float var2);
}

