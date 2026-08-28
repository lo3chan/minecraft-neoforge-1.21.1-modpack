/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.physics.PxRigidBody;
import physx.physics.PxRigidDynamicLockFlagEnum;
import physx.physics.PxRigidDynamicLockFlags;

public class PxRigidDynamic
extends PxRigidBody {
    public static final int SIZEOF = PxRigidDynamic.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxRigidDynamic() {
    }

    private static native int __sizeOf();

    public static PxRigidDynamic wrapPointer(long address) {
        return address != 0L ? new PxRigidDynamic(address) : null;
    }

    public static PxRigidDynamic arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxRigidDynamic.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxRigidDynamic(long address) {
        super(address);
    }

    public void setKinematicTarget(PxTransform destination) {
        this.checkNotNull();
        PxRigidDynamic._setKinematicTarget(this.address, destination.getAddress());
    }

    private static native void _setKinematicTarget(long var0, long var2);

    public boolean getKinematicTarget(PxTransform target) {
        this.checkNotNull();
        return PxRigidDynamic._getKinematicTarget(this.address, target.getAddress());
    }

    private static native boolean _getKinematicTarget(long var0, long var2);

    public boolean isSleeping() {
        this.checkNotNull();
        return PxRigidDynamic._isSleeping(this.address);
    }

    private static native boolean _isSleeping(long var0);

    public void setSleepThreshold(float threshold) {
        this.checkNotNull();
        PxRigidDynamic._setSleepThreshold(this.address, threshold);
    }

    private static native void _setSleepThreshold(long var0, float var2);

    public float getSleepThreshold() {
        this.checkNotNull();
        return PxRigidDynamic._getSleepThreshold(this.address);
    }

    private static native float _getSleepThreshold(long var0);

    public void setStabilizationThreshold(float threshold) {
        this.checkNotNull();
        PxRigidDynamic._setStabilizationThreshold(this.address, threshold);
    }

    private static native void _setStabilizationThreshold(long var0, float var2);

    public float getStabilizationThreshold() {
        this.checkNotNull();
        return PxRigidDynamic._getStabilizationThreshold(this.address);
    }

    private static native float _getStabilizationThreshold(long var0);

    public PxRigidDynamicLockFlags getRigidDynamicLockFlags() {
        this.checkNotNull();
        return PxRigidDynamicLockFlags.wrapPointer(PxRigidDynamic._getRigidDynamicLockFlags(this.address));
    }

    private static native long _getRigidDynamicLockFlags(long var0);

    public void setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum flag, boolean value) {
        this.checkNotNull();
        PxRigidDynamic._setRigidDynamicLockFlag(this.address, flag.value, value);
    }

    private static native void _setRigidDynamicLockFlag(long var0, int var2, boolean var3);

    public void setRigidDynamicLockFlags(PxRigidDynamicLockFlags flags) {
        this.checkNotNull();
        PxRigidDynamic._setRigidDynamicLockFlags(this.address, flags.getAddress());
    }

    private static native void _setRigidDynamicLockFlags(long var0, long var2);

    public void setLinearVelocity(PxVec3 linVel) {
        this.checkNotNull();
        PxRigidDynamic._setLinearVelocity(this.address, linVel.getAddress());
    }

    private static native void _setLinearVelocity(long var0, long var2);

    public void setLinearVelocity(PxVec3 linVel, boolean autowake) {
        this.checkNotNull();
        PxRigidDynamic._setLinearVelocity(this.address, linVel.getAddress(), autowake);
    }

    private static native void _setLinearVelocity(long var0, long var2, boolean var4);

    public void setAngularVelocity(PxVec3 angVel) {
        this.checkNotNull();
        PxRigidDynamic._setAngularVelocity(this.address, angVel.getAddress());
    }

    private static native void _setAngularVelocity(long var0, long var2);

    public void setAngularVelocity(PxVec3 angVel, boolean autowake) {
        this.checkNotNull();
        PxRigidDynamic._setAngularVelocity(this.address, angVel.getAddress(), autowake);
    }

    private static native void _setAngularVelocity(long var0, long var2, boolean var4);

    public void setWakeCounter(float wakeCounterValue) {
        this.checkNotNull();
        PxRigidDynamic._setWakeCounter(this.address, wakeCounterValue);
    }

    private static native void _setWakeCounter(long var0, float var2);

    public float getWakeCounter() {
        this.checkNotNull();
        return PxRigidDynamic._getWakeCounter(this.address);
    }

    private static native float _getWakeCounter(long var0);

    public void wakeUp() {
        this.checkNotNull();
        PxRigidDynamic._wakeUp(this.address);
    }

    private static native void _wakeUp(long var0);

    public void putToSleep() {
        this.checkNotNull();
        PxRigidDynamic._putToSleep(this.address);
    }

    private static native void _putToSleep(long var0);

    public void setSolverIterationCounts(int minPositionIters) {
        this.checkNotNull();
        PxRigidDynamic._setSolverIterationCounts(this.address, minPositionIters);
    }

    private static native void _setSolverIterationCounts(long var0, int var2);

    public void setSolverIterationCounts(int minPositionIters, int minVelocityIters) {
        this.checkNotNull();
        PxRigidDynamic._setSolverIterationCounts(this.address, minPositionIters, minVelocityIters);
    }

    private static native void _setSolverIterationCounts(long var0, int var2, int var3);

    public float getContactReportThreshold() {
        this.checkNotNull();
        return PxRigidDynamic._getContactReportThreshold(this.address);
    }

    private static native float _getContactReportThreshold(long var0);

    public void setContactReportThreshold(float threshold) {
        this.checkNotNull();
        PxRigidDynamic._setContactReportThreshold(this.address, threshold);
    }

    private static native void _setContactReportThreshold(long var0, float var2);
}

