/*
 * Decompiled with CFR 0.152.
 */
package physx.common;

import physx.NativeObject;
import physx.common.PxVec3;

public class PxVec4
extends NativeObject {
    public static final int SIZEOF = PxVec4.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxVec4 wrapPointer(long address) {
        return address != 0L ? new PxVec4(address) : null;
    }

    public static PxVec4 arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxVec4.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxVec4(long address) {
        super(address);
    }

    public static PxVec4 createAt(long address) {
        PxVec4.__placement_new_PxVec4(address);
        PxVec4 createdObj = PxVec4.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxVec4 createAt(T allocator, NativeObject.Allocator<T> allocate) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxVec4.__placement_new_PxVec4(address);
        PxVec4 createdObj = PxVec4.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxVec4(long var0);

    public static PxVec4 createAt(long address, float x, float y, float z, float w) {
        PxVec4.__placement_new_PxVec4(address, x, y, z, w);
        PxVec4 createdObj = PxVec4.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxVec4 createAt(T allocator, NativeObject.Allocator<T> allocate, float x, float y, float z, float w) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxVec4.__placement_new_PxVec4(address, x, y, z, w);
        PxVec4 createdObj = PxVec4.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxVec4(long var0, float var2, float var3, float var4, float var5);

    public PxVec4() {
        this.address = PxVec4._PxVec4();
    }

    private static native long _PxVec4();

    public PxVec4(float x, float y, float z, float w) {
        this.address = PxVec4._PxVec4(x, y, z, w);
    }

    private static native long _PxVec4(float var0, float var1, float var2, float var3);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxVec4._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public float getX() {
        this.checkNotNull();
        return PxVec4._getX(this.address);
    }

    private static native float _getX(long var0);

    public void setX(float value) {
        this.checkNotNull();
        PxVec4._setX(this.address, value);
    }

    private static native void _setX(long var0, float var2);

    public float getY() {
        this.checkNotNull();
        return PxVec4._getY(this.address);
    }

    private static native float _getY(long var0);

    public void setY(float value) {
        this.checkNotNull();
        PxVec4._setY(this.address, value);
    }

    private static native void _setY(long var0, float var2);

    public float getZ() {
        this.checkNotNull();
        return PxVec4._getZ(this.address);
    }

    private static native float _getZ(long var0);

    public void setZ(float value) {
        this.checkNotNull();
        PxVec4._setZ(this.address, value);
    }

    private static native void _setZ(long var0, float var2);

    public float getW() {
        this.checkNotNull();
        return PxVec4._getW(this.address);
    }

    private static native float _getW(long var0);

    public void setW(float value) {
        this.checkNotNull();
        PxVec4._setW(this.address, value);
    }

    private static native void _setW(long var0, float var2);

    public boolean isZero() {
        this.checkNotNull();
        return PxVec4._isZero(this.address);
    }

    private static native boolean _isZero(long var0);

    public boolean isFinite() {
        this.checkNotNull();
        return PxVec4._isFinite(this.address);
    }

    private static native boolean _isFinite(long var0);

    public boolean isNormalized() {
        this.checkNotNull();
        return PxVec4._isNormalized(this.address);
    }

    private static native boolean _isNormalized(long var0);

    public float magnitudeSquared() {
        this.checkNotNull();
        return PxVec4._magnitudeSquared(this.address);
    }

    private static native float _magnitudeSquared(long var0);

    public float magnitude() {
        this.checkNotNull();
        return PxVec4._magnitude(this.address);
    }

    private static native float _magnitude(long var0);

    public float dot(PxVec4 v) {
        this.checkNotNull();
        return PxVec4._dot(this.address, v.getAddress());
    }

    private static native float _dot(long var0, long var2);

    public PxVec4 getNormalized() {
        this.checkNotNull();
        return PxVec4.wrapPointer(PxVec4._getNormalized(this.address));
    }

    private static native long _getNormalized(long var0);

    public float normalize() {
        this.checkNotNull();
        return PxVec4._normalize(this.address);
    }

    private static native float _normalize(long var0);

    public PxVec4 multiply(PxVec4 a) {
        this.checkNotNull();
        return PxVec4.wrapPointer(PxVec4._multiply(this.address, a.getAddress()));
    }

    private static native long _multiply(long var0, long var2);

    public PxVec4 minimum(PxVec4 v) {
        this.checkNotNull();
        return PxVec4.wrapPointer(PxVec4._minimum(this.address, v.getAddress()));
    }

    private static native long _minimum(long var0, long var2);

    public PxVec4 maximum(PxVec4 v) {
        this.checkNotNull();
        return PxVec4.wrapPointer(PxVec4._maximum(this.address, v.getAddress()));
    }

    private static native long _maximum(long var0, long var2);

    public PxVec3 getXYZ() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxVec4._getXYZ(this.address));
    }

    private static native long _getXYZ(long var0);
}

