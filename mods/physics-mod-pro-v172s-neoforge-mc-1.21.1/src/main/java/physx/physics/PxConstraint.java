/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import physx.common.PxBase;
import physx.common.PxVec3;
import physx.physics.PxConstraintFlagEnum;
import physx.physics.PxConstraintFlags;
import physx.physics.PxRigidActor;
import physx.physics.PxScene;

public class PxConstraint
extends PxBase {
    public static final int SIZEOF = PxConstraint.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxConstraint() {
    }

    private static native int __sizeOf();

    public static PxConstraint wrapPointer(long address) {
        return address != 0L ? new PxConstraint(address) : null;
    }

    public static PxConstraint arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxConstraint.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxConstraint(long address) {
        super(address);
    }

    public PxScene getScene() {
        this.checkNotNull();
        return PxScene.wrapPointer(PxConstraint._getScene(this.address));
    }

    private static native long _getScene(long var0);

    public void setActors(PxRigidActor actor0, PxRigidActor actor1) {
        this.checkNotNull();
        PxConstraint._setActors(this.address, actor0.getAddress(), actor1.getAddress());
    }

    private static native void _setActors(long var0, long var2, long var4);

    public void markDirty() {
        this.checkNotNull();
        PxConstraint._markDirty(this.address);
    }

    private static native void _markDirty(long var0);

    public void setFlags(PxConstraintFlags flags) {
        this.checkNotNull();
        PxConstraint._setFlags(this.address, flags.getAddress());
    }

    private static native void _setFlags(long var0, long var2);

    public PxConstraintFlags getFlags() {
        this.checkNotNull();
        return PxConstraintFlags.wrapPointer(PxConstraint._getFlags(this.address));
    }

    private static native long _getFlags(long var0);

    public void setFlag(PxConstraintFlagEnum flag, boolean value) {
        this.checkNotNull();
        PxConstraint._setFlag(this.address, flag.value, value);
    }

    private static native void _setFlag(long var0, int var2, boolean var3);

    public void getForce(PxVec3 linear, PxVec3 angular) {
        this.checkNotNull();
        PxConstraint._getForce(this.address, linear.getAddress(), angular.getAddress());
    }

    private static native void _getForce(long var0, long var2, long var4);

    public boolean isValid() {
        this.checkNotNull();
        return PxConstraint._isValid(this.address);
    }

    private static native boolean _isValid(long var0);

    public void setBreakForce(float linear, float angular) {
        this.checkNotNull();
        PxConstraint._setBreakForce(this.address, linear, angular);
    }

    private static native void _setBreakForce(long var0, float var2, float var3);

    public void setMinResponseThreshold(float threshold) {
        this.checkNotNull();
        PxConstraint._setMinResponseThreshold(this.address, threshold);
    }

    private static native void _setMinResponseThreshold(long var0, float var2);

    public float getMinResponseThreshold() {
        this.checkNotNull();
        return PxConstraint._getMinResponseThreshold(this.address);
    }

    private static native float _getMinResponseThreshold(long var0);
}

