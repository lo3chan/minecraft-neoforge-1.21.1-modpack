/*
 * Decompiled with CFR 0.152.
 */
package physx.character;

import physx.character.PxController;
import physx.character.PxControllerHit;

public class PxControllersHit
extends PxControllerHit {
    public static final int SIZEOF = PxControllersHit.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxControllersHit() {
    }

    private static native int __sizeOf();

    public static PxControllersHit wrapPointer(long address) {
        return address != 0L ? new PxControllersHit(address) : null;
    }

    public static PxControllersHit arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxControllersHit.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxControllersHit(long address) {
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
        PxControllersHit._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxController getOther() {
        this.checkNotNull();
        return PxController.wrapPointer(PxControllersHit._getOther(this.address));
    }

    private static native long _getOther(long var0);

    public void setOther(PxController value) {
        this.checkNotNull();
        PxControllersHit._setOther(this.address, value.getAddress());
    }

    private static native void _setOther(long var0, long var2);
}

