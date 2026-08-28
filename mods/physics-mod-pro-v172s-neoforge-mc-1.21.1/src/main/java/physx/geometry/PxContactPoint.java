/*
 * Decompiled with CFR 0.152.
 */
package physx.geometry;

import physx.NativeObject;
import physx.common.PxVec3;

public class PxContactPoint
extends NativeObject {
    public static final int SIZEOF = PxContactPoint.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxContactPoint wrapPointer(long address) {
        return address != 0L ? new PxContactPoint(address) : null;
    }

    public static PxContactPoint arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxContactPoint.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxContactPoint(long address) {
        super(address);
    }

    public PxContactPoint() {
        this.address = PxContactPoint._PxContactPoint();
    }

    private static native long _PxContactPoint();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxContactPoint._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxVec3 getNormal() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxContactPoint._getNormal(this.address));
    }

    private static native long _getNormal(long var0);

    public void setNormal(PxVec3 value) {
        this.checkNotNull();
        PxContactPoint._setNormal(this.address, value.getAddress());
    }

    private static native void _setNormal(long var0, long var2);

    public PxVec3 getPoint() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxContactPoint._getPoint(this.address));
    }

    private static native long _getPoint(long var0);

    public void setPoint(PxVec3 value) {
        this.checkNotNull();
        PxContactPoint._setPoint(this.address, value.getAddress());
    }

    private static native void _setPoint(long var0, long var2);

    public PxVec3 getTargetVel() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxContactPoint._getTargetVel(this.address));
    }

    private static native long _getTargetVel(long var0);

    public void setTargetVel(PxVec3 value) {
        this.checkNotNull();
        PxContactPoint._setTargetVel(this.address, value.getAddress());
    }

    private static native void _setTargetVel(long var0, long var2);

    public float getSeparation() {
        this.checkNotNull();
        return PxContactPoint._getSeparation(this.address);
    }

    private static native float _getSeparation(long var0);

    public void setSeparation(float value) {
        this.checkNotNull();
        PxContactPoint._setSeparation(this.address, value);
    }

    private static native void _setSeparation(long var0, float var2);

    public float getMaxImpulse() {
        this.checkNotNull();
        return PxContactPoint._getMaxImpulse(this.address);
    }

    private static native float _getMaxImpulse(long var0);

    public void setMaxImpulse(float value) {
        this.checkNotNull();
        PxContactPoint._setMaxImpulse(this.address, value);
    }

    private static native void _setMaxImpulse(long var0, float var2);

    public float getStaticFriction() {
        this.checkNotNull();
        return PxContactPoint._getStaticFriction(this.address);
    }

    private static native float _getStaticFriction(long var0);

    public void setStaticFriction(float value) {
        this.checkNotNull();
        PxContactPoint._setStaticFriction(this.address, value);
    }

    private static native void _setStaticFriction(long var0, float var2);

    public byte getMaterialFlags() {
        this.checkNotNull();
        return PxContactPoint._getMaterialFlags(this.address);
    }

    private static native byte _getMaterialFlags(long var0);

    public void setMaterialFlags(byte value) {
        this.checkNotNull();
        PxContactPoint._setMaterialFlags(this.address, value);
    }

    private static native void _setMaterialFlags(long var0, byte var2);

    public int getInternalFaceIndex1() {
        this.checkNotNull();
        return PxContactPoint._getInternalFaceIndex1(this.address);
    }

    private static native int _getInternalFaceIndex1(long var0);

    public void setInternalFaceIndex1(int value) {
        this.checkNotNull();
        PxContactPoint._setInternalFaceIndex1(this.address, value);
    }

    private static native void _setInternalFaceIndex1(long var0, int var2);

    public float getDynamicFriction() {
        this.checkNotNull();
        return PxContactPoint._getDynamicFriction(this.address);
    }

    private static native float _getDynamicFriction(long var0);

    public void setDynamicFriction(float value) {
        this.checkNotNull();
        PxContactPoint._setDynamicFriction(this.address, value);
    }

    private static native void _setDynamicFriction(long var0, float var2);

    public float getRestitution() {
        this.checkNotNull();
        return PxContactPoint._getRestitution(this.address);
    }

    private static native float _getRestitution(long var0);

    public void setRestitution(float value) {
        this.checkNotNull();
        PxContactPoint._setRestitution(this.address, value);
    }

    private static native void _setRestitution(long var0, float var2);

    public float getDamping() {
        this.checkNotNull();
        return PxContactPoint._getDamping(this.address);
    }

    private static native float _getDamping(long var0);

    public void setDamping(float value) {
        this.checkNotNull();
        PxContactPoint._setDamping(this.address, value);
    }

    private static native void _setDamping(long var0, float var2);
}

