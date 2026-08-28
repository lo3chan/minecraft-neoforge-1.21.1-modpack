/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import physx.common.PxBase;
import physx.physics.PxArticulationReducedCoordinate;

public class PxArticulationTendon
extends PxBase {
    public static final int SIZEOF = PxArticulationTendon.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxArticulationTendon() {
    }

    private static native int __sizeOf();

    public static PxArticulationTendon wrapPointer(long address) {
        return address != 0L ? new PxArticulationTendon(address) : null;
    }

    public static PxArticulationTendon arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxArticulationTendon.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxArticulationTendon(long address) {
        super(address);
    }

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxArticulationTendon._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public void setStiffness(float stiffness) {
        this.checkNotNull();
        PxArticulationTendon._setStiffness(this.address, stiffness);
    }

    private static native void _setStiffness(long var0, float var2);

    public float getStiffness() {
        this.checkNotNull();
        return PxArticulationTendon._getStiffness(this.address);
    }

    private static native float _getStiffness(long var0);

    public void setDamping(float damping) {
        this.checkNotNull();
        PxArticulationTendon._setDamping(this.address, damping);
    }

    private static native void _setDamping(long var0, float var2);

    public float getDamping() {
        this.checkNotNull();
        return PxArticulationTendon._getDamping(this.address);
    }

    private static native float _getDamping(long var0);

    public void setLimitStiffness(float stiffness) {
        this.checkNotNull();
        PxArticulationTendon._setLimitStiffness(this.address, stiffness);
    }

    private static native void _setLimitStiffness(long var0, float var2);

    public float getLimitStiffness() {
        this.checkNotNull();
        return PxArticulationTendon._getLimitStiffness(this.address);
    }

    private static native float _getLimitStiffness(long var0);

    public void setOffset(float offset) {
        this.checkNotNull();
        PxArticulationTendon._setOffset(this.address, offset);
    }

    private static native void _setOffset(long var0, float var2);

    public void setOffset(float offset, boolean autowake) {
        this.checkNotNull();
        PxArticulationTendon._setOffset(this.address, offset, autowake);
    }

    private static native void _setOffset(long var0, float var2, boolean var3);

    public float getOffset() {
        this.checkNotNull();
        return PxArticulationTendon._getOffset(this.address);
    }

    private static native float _getOffset(long var0);

    public PxArticulationReducedCoordinate getArticulation() {
        this.checkNotNull();
        return PxArticulationReducedCoordinate.wrapPointer(PxArticulationTendon._getArticulation(this.address));
    }

    private static native long _getArticulation(long var0);
}

