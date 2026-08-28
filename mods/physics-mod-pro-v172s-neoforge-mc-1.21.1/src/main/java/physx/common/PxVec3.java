/*
 * Decompiled with CFR 0.152.
 */
package physx.common;

import physx.NativeObject;

public class PxVec3
extends NativeObject {
    public static final int SIZEOF = PxVec3.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxVec3 wrapPointer(long address) {
        return address != 0L ? new PxVec3(address) : null;
    }

    public static PxVec3 arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxVec3.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxVec3(long address) {
        super(address);
    }

    public static PxVec3 createAt(long address) {
        PxVec3.__placement_new_PxVec3(address);
        PxVec3 createdObj = PxVec3.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxVec3 createAt(T allocator, NativeObject.Allocator<T> allocate) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxVec3.__placement_new_PxVec3(address);
        PxVec3 createdObj = PxVec3.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxVec3(long var0);

    public static PxVec3 createAt(long address, float x, float y, float z) {
        PxVec3.__placement_new_PxVec3(address, x, y, z);
        PxVec3 createdObj = PxVec3.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxVec3 createAt(T allocator, NativeObject.Allocator<T> allocate, float x, float y, float z) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxVec3.__placement_new_PxVec3(address, x, y, z);
        PxVec3 createdObj = PxVec3.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxVec3(long var0, float var2, float var3, float var4);

    public PxVec3() {
        this.address = PxVec3._PxVec3();
    }

    private static native long _PxVec3();

    public PxVec3(float x, float y, float z) {
        this.address = PxVec3._PxVec3(x, y, z);
    }

    private static native long _PxVec3(float var0, float var1, float var2);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxVec3._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public float getX() {
        this.checkNotNull();
        return PxVec3._getX(this.address);
    }

    private static native float _getX(long var0);

    public void setX(float value) {
        this.checkNotNull();
        PxVec3._setX(this.address, value);
    }

    private static native void _setX(long var0, float var2);

    public float getY() {
        this.checkNotNull();
        return PxVec3._getY(this.address);
    }

    private static native float _getY(long var0);

    public void setY(float value) {
        this.checkNotNull();
        PxVec3._setY(this.address, value);
    }

    private static native void _setY(long var0, float var2);

    public float getZ() {
        this.checkNotNull();
        return PxVec3._getZ(this.address);
    }

    private static native float _getZ(long var0);

    public void setZ(float value) {
        this.checkNotNull();
        PxVec3._setZ(this.address, value);
    }

    private static native void _setZ(long var0, float var2);

    public boolean isZero() {
        this.checkNotNull();
        return PxVec3._isZero(this.address);
    }

    private static native boolean _isZero(long var0);

    public boolean isFinite() {
        this.checkNotNull();
        return PxVec3._isFinite(this.address);
    }

    private static native boolean _isFinite(long var0);

    public boolean isNormalized() {
        this.checkNotNull();
        return PxVec3._isNormalized(this.address);
    }

    private static native boolean _isNormalized(long var0);

    public float magnitudeSquared() {
        this.checkNotNull();
        return PxVec3._magnitudeSquared(this.address);
    }

    private static native float _magnitudeSquared(long var0);

    public float magnitude() {
        this.checkNotNull();
        return PxVec3._magnitude(this.address);
    }

    private static native float _magnitude(long var0);

    public float dot(PxVec3 v) {
        this.checkNotNull();
        return PxVec3._dot(this.address, v.getAddress());
    }

    private static native float _dot(long var0, long var2);

    public PxVec3 cross(PxVec3 v) {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxVec3._cross(this.address, v.getAddress()));
    }

    private static native long _cross(long var0, long var2);

    public PxVec3 getNormalized() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxVec3._getNormalized(this.address));
    }

    private static native long _getNormalized(long var0);

    public float normalize() {
        this.checkNotNull();
        return PxVec3._normalize(this.address);
    }

    private static native float _normalize(long var0);

    public float normalizeSafe() {
        this.checkNotNull();
        return PxVec3._normalizeSafe(this.address);
    }

    private static native float _normalizeSafe(long var0);

    public float normalizeFast() {
        this.checkNotNull();
        return PxVec3._normalizeFast(this.address);
    }

    private static native float _normalizeFast(long var0);

    public PxVec3 multiply(PxVec3 a) {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxVec3._multiply(this.address, a.getAddress()));
    }

    private static native long _multiply(long var0, long var2);

    public PxVec3 minimum(PxVec3 v) {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxVec3._minimum(this.address, v.getAddress()));
    }

    private static native long _minimum(long var0, long var2);

    public float minElement() {
        this.checkNotNull();
        return PxVec3._minElement(this.address);
    }

    private static native float _minElement(long var0);

    public PxVec3 maximum(PxVec3 v) {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxVec3._maximum(this.address, v.getAddress()));
    }

    private static native long _maximum(long var0, long var2);

    public float maxElement() {
        this.checkNotNull();
        return PxVec3._maxElement(this.address);
    }

    private static native float _maxElement(long var0);

    public PxVec3 abs() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxVec3._abs(this.address));
    }

    private static native long _abs(long var0);
}

