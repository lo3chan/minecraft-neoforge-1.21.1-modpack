/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import physx.NativeObject;
import physx.physics.PxConstraint;

public class PxConstraintInfo
extends NativeObject {
    public static final int SIZEOF = PxConstraintInfo.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxConstraintInfo() {
    }

    private static native int __sizeOf();

    public static PxConstraintInfo wrapPointer(long address) {
        return address != 0L ? new PxConstraintInfo(address) : null;
    }

    public static PxConstraintInfo arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxConstraintInfo.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxConstraintInfo(long address) {
        super(address);
    }

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxConstraintInfo._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxConstraint getConstraint() {
        this.checkNotNull();
        return PxConstraint.wrapPointer(PxConstraintInfo._getConstraint(this.address));
    }

    private static native long _getConstraint(long var0);

    public void setConstraint(PxConstraint value) {
        this.checkNotNull();
        PxConstraintInfo._setConstraint(this.address, value.getAddress());
    }

    private static native void _setConstraint(long var0, long var2);

    public NativeObject getExternalReference() {
        this.checkNotNull();
        return NativeObject.wrapPointer(PxConstraintInfo._getExternalReference(this.address));
    }

    private static native long _getExternalReference(long var0);

    public void setExternalReference(NativeObject value) {
        this.checkNotNull();
        PxConstraintInfo._setExternalReference(this.address, value.getAddress());
    }

    private static native void _setExternalReference(long var0, long var2);

    public int getType() {
        this.checkNotNull();
        return PxConstraintInfo._getType(this.address);
    }

    private static native int _getType(long var0);

    public void setType(int value) {
        this.checkNotNull();
        PxConstraintInfo._setType(this.address, value);
    }

    private static native void _setType(long var0, int var2);
}

