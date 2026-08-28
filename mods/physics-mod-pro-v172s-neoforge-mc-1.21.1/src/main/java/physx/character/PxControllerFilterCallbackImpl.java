/*
 * Decompiled with CFR 0.152.
 */
package physx.character;

import physx.character.PxController;
import physx.character.PxControllerFilterCallback;

public class PxControllerFilterCallbackImpl
extends PxControllerFilterCallback {
    public static final int SIZEOF = PxControllerFilterCallbackImpl.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxControllerFilterCallbackImpl wrapPointer(long address) {
        return address != 0L ? new PxControllerFilterCallbackImpl(address) : null;
    }

    public static PxControllerFilterCallbackImpl arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxControllerFilterCallbackImpl.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxControllerFilterCallbackImpl(long address) {
        super(address);
    }

    protected PxControllerFilterCallbackImpl() {
        this.address = this._PxControllerFilterCallbackImpl();
    }

    private native long _PxControllerFilterCallbackImpl();

    @Override
    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxControllerFilterCallbackImpl._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    private boolean _filter(long a, long b) {
        return this.filter(PxController.wrapPointer(a), PxController.wrapPointer(b));
    }

    @Override
    public boolean filter(PxController a, PxController b) {
        return false;
    }
}

