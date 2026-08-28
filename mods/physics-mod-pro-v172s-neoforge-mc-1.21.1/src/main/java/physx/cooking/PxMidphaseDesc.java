/*
 * Decompiled with CFR 0.152.
 */
package physx.cooking;

import physx.NativeObject;
import physx.cooking.PxBVH33MidphaseDesc;
import physx.cooking.PxBVH34MidphaseDesc;
import physx.cooking.PxMeshMidPhaseEnum;

public class PxMidphaseDesc
extends NativeObject {
    public static final int SIZEOF = PxMidphaseDesc.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxMidphaseDesc wrapPointer(long address) {
        return address != 0L ? new PxMidphaseDesc(address) : null;
    }

    public static PxMidphaseDesc arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxMidphaseDesc.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxMidphaseDesc(long address) {
        super(address);
    }

    public PxMidphaseDesc() {
        this.address = PxMidphaseDesc._PxMidphaseDesc();
    }

    private static native long _PxMidphaseDesc();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxMidphaseDesc._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxBVH33MidphaseDesc getMBVH33Desc() {
        this.checkNotNull();
        return PxBVH33MidphaseDesc.wrapPointer(PxMidphaseDesc._getMBVH33Desc(this.address));
    }

    private static native long _getMBVH33Desc(long var0);

    public void setMBVH33Desc(PxBVH33MidphaseDesc value) {
        this.checkNotNull();
        PxMidphaseDesc._setMBVH33Desc(this.address, value.getAddress());
    }

    private static native void _setMBVH33Desc(long var0, long var2);

    public PxBVH34MidphaseDesc getMBVH34Desc() {
        this.checkNotNull();
        return PxBVH34MidphaseDesc.wrapPointer(PxMidphaseDesc._getMBVH34Desc(this.address));
    }

    private static native long _getMBVH34Desc(long var0);

    public void setMBVH34Desc(PxBVH34MidphaseDesc value) {
        this.checkNotNull();
        PxMidphaseDesc._setMBVH34Desc(this.address, value.getAddress());
    }

    private static native void _setMBVH34Desc(long var0, long var2);

    public PxMeshMidPhaseEnum getType() {
        this.checkNotNull();
        return PxMeshMidPhaseEnum.forValue(PxMidphaseDesc._getType(this.address));
    }

    private static native int _getType(long var0);

    public void setToDefault(PxMeshMidPhaseEnum type) {
        this.checkNotNull();
        PxMidphaseDesc._setToDefault(this.address, type.value);
    }

    private static native void _setToDefault(long var0, int var2);

    public boolean isValid() {
        this.checkNotNull();
        return PxMidphaseDesc._isValid(this.address);
    }

    private static native boolean _isValid(long var0);
}

