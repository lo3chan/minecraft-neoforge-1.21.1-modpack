/*
 * Decompiled with CFR 0.152.
 */
package physx.common;

import physx.NativeObject;
import physx.common.PxIDENTITYEnum;
import physx.common.PxVec3;

public class PxQuat
extends NativeObject {
    public static final int SIZEOF = PxQuat.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxQuat wrapPointer(long address) {
        return address != 0L ? new PxQuat(address) : null;
    }

    public static PxQuat arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxQuat.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxQuat(long address) {
        super(address);
    }

    public static PxQuat createAt(long address) {
        PxQuat.__placement_new_PxQuat(address);
        PxQuat createdObj = PxQuat.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxQuat createAt(T allocator, NativeObject.Allocator<T> allocate) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxQuat.__placement_new_PxQuat(address);
        PxQuat createdObj = PxQuat.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxQuat(long var0);

    public static PxQuat createAt(long address, PxIDENTITYEnum r) {
        PxQuat.__placement_new_PxQuat(address, r.value);
        PxQuat createdObj = PxQuat.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxQuat createAt(T allocator, NativeObject.Allocator<T> allocate, PxIDENTITYEnum r) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxQuat.__placement_new_PxQuat(address, r.value);
        PxQuat createdObj = PxQuat.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxQuat(long var0, int var2);

    public static PxQuat createAt(long address, float x, float y, float z, float w) {
        PxQuat.__placement_new_PxQuat(address, x, y, z, w);
        PxQuat createdObj = PxQuat.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxQuat createAt(T allocator, NativeObject.Allocator<T> allocate, float x, float y, float z, float w) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxQuat.__placement_new_PxQuat(address, x, y, z, w);
        PxQuat createdObj = PxQuat.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxQuat(long var0, float var2, float var3, float var4, float var5);

    public PxQuat() {
        this.address = PxQuat._PxQuat();
    }

    private static native long _PxQuat();

    public PxQuat(PxIDENTITYEnum r) {
        this.address = PxQuat._PxQuat(r.value);
    }

    private static native long _PxQuat(int var0);

    public PxQuat(float x, float y, float z, float w) {
        this.address = PxQuat._PxQuat(x, y, z, w);
    }

    private static native long _PxQuat(float var0, float var1, float var2, float var3);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxQuat._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public float getX() {
        this.checkNotNull();
        return PxQuat._getX(this.address);
    }

    private static native float _getX(long var0);

    public void setX(float value) {
        this.checkNotNull();
        PxQuat._setX(this.address, value);
    }

    private static native void _setX(long var0, float var2);

    public float getY() {
        this.checkNotNull();
        return PxQuat._getY(this.address);
    }

    private static native float _getY(long var0);

    public void setY(float value) {
        this.checkNotNull();
        PxQuat._setY(this.address, value);
    }

    private static native void _setY(long var0, float var2);

    public float getZ() {
        this.checkNotNull();
        return PxQuat._getZ(this.address);
    }

    private static native float _getZ(long var0);

    public void setZ(float value) {
        this.checkNotNull();
        PxQuat._setZ(this.address, value);
    }

    private static native void _setZ(long var0, float var2);

    public float getW() {
        this.checkNotNull();
        return PxQuat._getW(this.address);
    }

    private static native float _getW(long var0);

    public void setW(float value) {
        this.checkNotNull();
        PxQuat._setW(this.address, value);
    }

    private static native void _setW(long var0, float var2);

    public boolean isIdentity() {
        this.checkNotNull();
        return PxQuat._isIdentity(this.address);
    }

    private static native boolean _isIdentity(long var0);

    public boolean isFinite() {
        this.checkNotNull();
        return PxQuat._isFinite(this.address);
    }

    private static native boolean _isFinite(long var0);

    public boolean isUnit() {
        this.checkNotNull();
        return PxQuat._isUnit(this.address);
    }

    private static native boolean _isUnit(long var0);

    public boolean isSane() {
        this.checkNotNull();
        return PxQuat._isSane(this.address);
    }

    private static native boolean _isSane(long var0);

    public float getAngle() {
        this.checkNotNull();
        return PxQuat._getAngle(this.address);
    }

    private static native float _getAngle(long var0);

    public float getAngle(PxQuat q) {
        this.checkNotNull();
        return PxQuat._getAngle(this.address, q.getAddress());
    }

    private static native float _getAngle(long var0, long var2);

    public float magnitudeSquared() {
        this.checkNotNull();
        return PxQuat._magnitudeSquared(this.address);
    }

    private static native float _magnitudeSquared(long var0);

    public float dot(PxQuat q) {
        this.checkNotNull();
        return PxQuat._dot(this.address, q.getAddress());
    }

    private static native float _dot(long var0, long var2);

    public PxQuat getNormalized() {
        this.checkNotNull();
        return PxQuat.wrapPointer(PxQuat._getNormalized(this.address));
    }

    private static native long _getNormalized(long var0);

    public float magnitude() {
        this.checkNotNull();
        return PxQuat._magnitude(this.address);
    }

    private static native float _magnitude(long var0);

    public float normalize() {
        this.checkNotNull();
        return PxQuat._normalize(this.address);
    }

    private static native float _normalize(long var0);

    public PxQuat getConjugate() {
        this.checkNotNull();
        return PxQuat.wrapPointer(PxQuat._getConjugate(this.address));
    }

    private static native long _getConjugate(long var0);

    public PxVec3 getImaginaryPart() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxQuat._getImaginaryPart(this.address));
    }

    private static native long _getImaginaryPart(long var0);

    public PxVec3 getBasisVector0() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxQuat._getBasisVector0(this.address));
    }

    private static native long _getBasisVector0(long var0);

    public PxVec3 getBasisVector1() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxQuat._getBasisVector1(this.address));
    }

    private static native long _getBasisVector1(long var0);

    public PxVec3 getBasisVector2() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxQuat._getBasisVector2(this.address));
    }

    private static native long _getBasisVector2(long var0);

    public PxVec3 rotate(PxVec3 v) {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxQuat._rotate(this.address, v.getAddress()));
    }

    private static native long _rotate(long var0, long var2);

    public PxVec3 rotateInv(PxVec3 v) {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxQuat._rotateInv(this.address, v.getAddress()));
    }

    private static native long _rotateInv(long var0, long var2);
}

