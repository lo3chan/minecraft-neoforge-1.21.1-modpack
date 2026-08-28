/*
 * Decompiled with CFR 0.152.
 */
package physx.extensions;

import physx.NativeObject;
import physx.common.PxVec3;

public class PxGjkQuerySweepResult
extends NativeObject {
    public static final int SIZEOF = PxGjkQuerySweepResult.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxGjkQuerySweepResult wrapPointer(long address) {
        return address != 0L ? new PxGjkQuerySweepResult(address) : null;
    }

    public static PxGjkQuerySweepResult arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxGjkQuerySweepResult.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxGjkQuerySweepResult(long address) {
        super(address);
    }

    public static PxGjkQuerySweepResult createAt(long address) {
        PxGjkQuerySweepResult.__placement_new_PxGjkQuerySweepResult(address);
        PxGjkQuerySweepResult createdObj = PxGjkQuerySweepResult.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxGjkQuerySweepResult createAt(T allocator, NativeObject.Allocator<T> allocate) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxGjkQuerySweepResult.__placement_new_PxGjkQuerySweepResult(address);
        PxGjkQuerySweepResult createdObj = PxGjkQuerySweepResult.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxGjkQuerySweepResult(long var0);

    public PxGjkQuerySweepResult() {
        this.address = PxGjkQuerySweepResult._PxGjkQuerySweepResult();
    }

    private static native long _PxGjkQuerySweepResult();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxGjkQuerySweepResult._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public boolean getSuccess() {
        this.checkNotNull();
        return PxGjkQuerySweepResult._getSuccess(this.address);
    }

    private static native boolean _getSuccess(long var0);

    public void setSuccess(boolean value) {
        this.checkNotNull();
        PxGjkQuerySweepResult._setSuccess(this.address, value);
    }

    private static native void _setSuccess(long var0, boolean var2);

    public float getT() {
        this.checkNotNull();
        return PxGjkQuerySweepResult._getT(this.address);
    }

    private static native float _getT(long var0);

    public void setT(float value) {
        this.checkNotNull();
        PxGjkQuerySweepResult._setT(this.address, value);
    }

    private static native void _setT(long var0, float var2);

    public PxVec3 getN() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxGjkQuerySweepResult._getN(this.address));
    }

    private static native long _getN(long var0);

    public void setN(PxVec3 value) {
        this.checkNotNull();
        PxGjkQuerySweepResult._setN(this.address, value.getAddress());
    }

    private static native void _setN(long var0, long var2);

    public PxVec3 getP() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxGjkQuerySweepResult._getP(this.address));
    }

    private static native long _getP(long var0);

    public void setP(PxVec3 value) {
        this.checkNotNull();
        PxGjkQuerySweepResult._setP(this.address, value.getAddress());
    }

    private static native void _setP(long var0, long var2);
}

