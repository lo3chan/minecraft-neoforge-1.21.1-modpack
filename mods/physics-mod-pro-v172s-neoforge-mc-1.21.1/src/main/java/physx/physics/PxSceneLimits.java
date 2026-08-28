/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import physx.NativeObject;

public class PxSceneLimits
extends NativeObject {
    public static final int SIZEOF = PxSceneLimits.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxSceneLimits wrapPointer(long address) {
        return address != 0L ? new PxSceneLimits(address) : null;
    }

    public static PxSceneLimits arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxSceneLimits.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxSceneLimits(long address) {
        super(address);
    }

    public static PxSceneLimits createAt(long address) {
        PxSceneLimits.__placement_new_PxSceneLimits(address);
        PxSceneLimits createdObj = PxSceneLimits.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxSceneLimits createAt(T allocator, NativeObject.Allocator<T> allocate) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxSceneLimits.__placement_new_PxSceneLimits(address);
        PxSceneLimits createdObj = PxSceneLimits.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxSceneLimits(long var0);

    public PxSceneLimits() {
        this.address = PxSceneLimits._PxSceneLimits();
    }

    private static native long _PxSceneLimits();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxSceneLimits._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public int getMaxNbActors() {
        this.checkNotNull();
        return PxSceneLimits._getMaxNbActors(this.address);
    }

    private static native int _getMaxNbActors(long var0);

    public void setMaxNbActors(int value) {
        this.checkNotNull();
        PxSceneLimits._setMaxNbActors(this.address, value);
    }

    private static native void _setMaxNbActors(long var0, int var2);

    public int getMaxNbBodies() {
        this.checkNotNull();
        return PxSceneLimits._getMaxNbBodies(this.address);
    }

    private static native int _getMaxNbBodies(long var0);

    public void setMaxNbBodies(int value) {
        this.checkNotNull();
        PxSceneLimits._setMaxNbBodies(this.address, value);
    }

    private static native void _setMaxNbBodies(long var0, int var2);

    public int getMaxNbStaticShapes() {
        this.checkNotNull();
        return PxSceneLimits._getMaxNbStaticShapes(this.address);
    }

    private static native int _getMaxNbStaticShapes(long var0);

    public void setMaxNbStaticShapes(int value) {
        this.checkNotNull();
        PxSceneLimits._setMaxNbStaticShapes(this.address, value);
    }

    private static native void _setMaxNbStaticShapes(long var0, int var2);

    public int getMaxNbDynamicShapes() {
        this.checkNotNull();
        return PxSceneLimits._getMaxNbDynamicShapes(this.address);
    }

    private static native int _getMaxNbDynamicShapes(long var0);

    public void setMaxNbDynamicShapes(int value) {
        this.checkNotNull();
        PxSceneLimits._setMaxNbDynamicShapes(this.address, value);
    }

    private static native void _setMaxNbDynamicShapes(long var0, int var2);

    public int getMaxNbAggregates() {
        this.checkNotNull();
        return PxSceneLimits._getMaxNbAggregates(this.address);
    }

    private static native int _getMaxNbAggregates(long var0);

    public void setMaxNbAggregates(int value) {
        this.checkNotNull();
        PxSceneLimits._setMaxNbAggregates(this.address, value);
    }

    private static native void _setMaxNbAggregates(long var0, int var2);

    public int getMaxNbConstraints() {
        this.checkNotNull();
        return PxSceneLimits._getMaxNbConstraints(this.address);
    }

    private static native int _getMaxNbConstraints(long var0);

    public void setMaxNbConstraints(int value) {
        this.checkNotNull();
        PxSceneLimits._setMaxNbConstraints(this.address, value);
    }

    private static native void _setMaxNbConstraints(long var0, int var2);

    public int getMaxNbRegions() {
        this.checkNotNull();
        return PxSceneLimits._getMaxNbRegions(this.address);
    }

    private static native int _getMaxNbRegions(long var0);

    public void setMaxNbRegions(int value) {
        this.checkNotNull();
        PxSceneLimits._setMaxNbRegions(this.address, value);
    }

    private static native void _setMaxNbRegions(long var0, int var2);

    public int getMaxNbBroadPhaseOverlaps() {
        this.checkNotNull();
        return PxSceneLimits._getMaxNbBroadPhaseOverlaps(this.address);
    }

    private static native int _getMaxNbBroadPhaseOverlaps(long var0);

    public void setMaxNbBroadPhaseOverlaps(int value) {
        this.checkNotNull();
        PxSceneLimits._setMaxNbBroadPhaseOverlaps(this.address, value);
    }

    private static native void _setMaxNbBroadPhaseOverlaps(long var0, int var2);

    public void setToDefault() {
        this.checkNotNull();
        PxSceneLimits._setToDefault(this.address);
    }

    private static native void _setToDefault(long var0);

    public boolean isValid() {
        this.checkNotNull();
        return PxSceneLimits._isValid(this.address);
    }

    private static native boolean _isValid(long var0);
}

