/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import physx.physics.PxLocationHit;

public class PxGeomRaycastHit
extends PxLocationHit {
    public static final int SIZEOF = PxGeomRaycastHit.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxGeomRaycastHit() {
    }

    private static native int __sizeOf();

    public static PxGeomRaycastHit wrapPointer(long address) {
        return address != 0L ? new PxGeomRaycastHit(address) : null;
    }

    public static PxGeomRaycastHit arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxGeomRaycastHit.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxGeomRaycastHit(long address) {
        super(address);
    }

    @Override
    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxGeomRaycastHit._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public float getU() {
        this.checkNotNull();
        return PxGeomRaycastHit._getU(this.address);
    }

    private static native float _getU(long var0);

    public void setU(float value) {
        this.checkNotNull();
        PxGeomRaycastHit._setU(this.address, value);
    }

    private static native void _setU(long var0, float var2);

    public float getV() {
        this.checkNotNull();
        return PxGeomRaycastHit._getV(this.address);
    }

    private static native float _getV(long var0);

    public void setV(float value) {
        this.checkNotNull();
        PxGeomRaycastHit._setV(this.address, value);
    }

    private static native void _setV(long var0, float var2);

    public boolean hadInitialOverlap() {
        this.checkNotNull();
        return PxGeomRaycastHit._hadInitialOverlap(this.address);
    }

    private static native boolean _hadInitialOverlap(long var0);
}

