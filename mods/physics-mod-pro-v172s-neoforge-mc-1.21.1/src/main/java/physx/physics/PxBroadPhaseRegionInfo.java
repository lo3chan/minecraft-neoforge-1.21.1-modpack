/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import physx.NativeObject;
import physx.physics.PxBroadPhaseRegion;

public class PxBroadPhaseRegionInfo
extends NativeObject {
    public static final int SIZEOF = PxBroadPhaseRegionInfo.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxBroadPhaseRegionInfo wrapPointer(long address) {
        return address != 0L ? new PxBroadPhaseRegionInfo(address) : null;
    }

    public static PxBroadPhaseRegionInfo arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxBroadPhaseRegionInfo.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxBroadPhaseRegionInfo(long address) {
        super(address);
    }

    public PxBroadPhaseRegionInfo() {
        this.address = PxBroadPhaseRegionInfo._PxBroadPhaseRegionInfo();
    }

    private static native long _PxBroadPhaseRegionInfo();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxBroadPhaseRegionInfo._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxBroadPhaseRegion getMRegion() {
        this.checkNotNull();
        return PxBroadPhaseRegion.wrapPointer(PxBroadPhaseRegionInfo._getMRegion(this.address));
    }

    private static native long _getMRegion(long var0);

    public void setMRegion(PxBroadPhaseRegion value) {
        this.checkNotNull();
        PxBroadPhaseRegionInfo._setMRegion(this.address, value.getAddress());
    }

    private static native void _setMRegion(long var0, long var2);

    public int getMNbStaticObjects() {
        this.checkNotNull();
        return PxBroadPhaseRegionInfo._getMNbStaticObjects(this.address);
    }

    private static native int _getMNbStaticObjects(long var0);

    public void setMNbStaticObjects(int value) {
        this.checkNotNull();
        PxBroadPhaseRegionInfo._setMNbStaticObjects(this.address, value);
    }

    private static native void _setMNbStaticObjects(long var0, int var2);

    public int getMNbDynamicObjects() {
        this.checkNotNull();
        return PxBroadPhaseRegionInfo._getMNbDynamicObjects(this.address);
    }

    private static native int _getMNbDynamicObjects(long var0);

    public void setMNbDynamicObjects(int value) {
        this.checkNotNull();
        PxBroadPhaseRegionInfo._setMNbDynamicObjects(this.address, value);
    }

    private static native void _setMNbDynamicObjects(long var0, int var2);

    public boolean getMActive() {
        this.checkNotNull();
        return PxBroadPhaseRegionInfo._getMActive(this.address);
    }

    private static native boolean _getMActive(long var0);

    public void setMActive(boolean value) {
        this.checkNotNull();
        PxBroadPhaseRegionInfo._setMActive(this.address, value);
    }

    private static native void _setMActive(long var0, boolean var2);

    public boolean getMOverlap() {
        this.checkNotNull();
        return PxBroadPhaseRegionInfo._getMOverlap(this.address);
    }

    private static native boolean _getMOverlap(long var0);

    public void setMOverlap(boolean value) {
        this.checkNotNull();
        PxBroadPhaseRegionInfo._setMOverlap(this.address, value);
    }

    private static native void _setMOverlap(long var0, boolean var2);
}

