/*
 * Decompiled with CFR 0.152.
 */
package physx.extensions;

import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.extensions.PxD6AxisEnum;
import physx.extensions.PxD6DriveEnum;
import physx.extensions.PxD6JointDrive;
import physx.extensions.PxD6MotionEnum;
import physx.extensions.PxJoint;
import physx.extensions.PxJointAngularLimitPair;
import physx.extensions.PxJointLimitCone;
import physx.extensions.PxJointLimitPyramid;
import physx.extensions.PxJointLinearLimit;
import physx.extensions.PxJointLinearLimitPair;

public class PxD6Joint
extends PxJoint {
    public static final int SIZEOF = PxD6Joint.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxD6Joint() {
    }

    private static native int __sizeOf();

    public static PxD6Joint wrapPointer(long address) {
        return address != 0L ? new PxD6Joint(address) : null;
    }

    public static PxD6Joint arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxD6Joint.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxD6Joint(long address) {
        super(address);
    }

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxD6Joint._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public void setMotion(PxD6AxisEnum axis, PxD6MotionEnum type) {
        this.checkNotNull();
        PxD6Joint._setMotion(this.address, axis.value, type.value);
    }

    private static native void _setMotion(long var0, int var2, int var3);

    public PxD6MotionEnum getMotion(PxD6AxisEnum axis) {
        this.checkNotNull();
        return PxD6MotionEnum.forValue(PxD6Joint._getMotion(this.address, axis.value));
    }

    private static native int _getMotion(long var0, int var2);

    public float getTwistAngle() {
        this.checkNotNull();
        return PxD6Joint._getTwistAngle(this.address);
    }

    private static native float _getTwistAngle(long var0);

    public float getSwingYAngle() {
        this.checkNotNull();
        return PxD6Joint._getSwingYAngle(this.address);
    }

    private static native float _getSwingYAngle(long var0);

    public float getSwingZAngle() {
        this.checkNotNull();
        return PxD6Joint._getSwingZAngle(this.address);
    }

    private static native float _getSwingZAngle(long var0);

    public void setDistanceLimit(PxJointLinearLimit limit) {
        this.checkNotNull();
        PxD6Joint._setDistanceLimit(this.address, limit.getAddress());
    }

    private static native void _setDistanceLimit(long var0, long var2);

    public void setLinearLimit(PxD6AxisEnum axis, PxJointLinearLimitPair limit) {
        this.checkNotNull();
        PxD6Joint._setLinearLimit(this.address, axis.value, limit.getAddress());
    }

    private static native void _setLinearLimit(long var0, int var2, long var3);

    public void setTwistLimit(PxJointAngularLimitPair limit) {
        this.checkNotNull();
        PxD6Joint._setTwistLimit(this.address, limit.getAddress());
    }

    private static native void _setTwistLimit(long var0, long var2);

    public void setSwingLimit(PxJointLimitCone limit) {
        this.checkNotNull();
        PxD6Joint._setSwingLimit(this.address, limit.getAddress());
    }

    private static native void _setSwingLimit(long var0, long var2);

    public void setPyramidSwingLimit(PxJointLimitPyramid limit) {
        this.checkNotNull();
        PxD6Joint._setPyramidSwingLimit(this.address, limit.getAddress());
    }

    private static native void _setPyramidSwingLimit(long var0, long var2);

    public void setDrive(PxD6DriveEnum index, PxD6JointDrive drive) {
        this.checkNotNull();
        PxD6Joint._setDrive(this.address, index.value, drive.getAddress());
    }

    private static native void _setDrive(long var0, int var2, long var3);

    public PxD6JointDrive getDrive(PxD6DriveEnum index) {
        this.checkNotNull();
        return PxD6JointDrive.wrapPointer(PxD6Joint._getDrive(this.address, index.value));
    }

    private static native long _getDrive(long var0, int var2);

    public void setDrivePosition(PxTransform pose) {
        this.checkNotNull();
        PxD6Joint._setDrivePosition(this.address, pose.getAddress());
    }

    private static native void _setDrivePosition(long var0, long var2);

    public void setDrivePosition(PxTransform pose, boolean autowake) {
        this.checkNotNull();
        PxD6Joint._setDrivePosition(this.address, pose.getAddress(), autowake);
    }

    private static native void _setDrivePosition(long var0, long var2, boolean var4);

    public PxTransform getDrivePosition() {
        this.checkNotNull();
        return PxTransform.wrapPointer(PxD6Joint._getDrivePosition(this.address));
    }

    private static native long _getDrivePosition(long var0);

    public void setDriveVelocity(PxVec3 linear, PxVec3 angular) {
        this.checkNotNull();
        PxD6Joint._setDriveVelocity(this.address, linear.getAddress(), angular.getAddress());
    }

    private static native void _setDriveVelocity(long var0, long var2, long var4);

    public void getDriveVelocity(PxVec3 linear, PxVec3 angular) {
        this.checkNotNull();
        PxD6Joint._getDriveVelocity(this.address, linear.getAddress(), angular.getAddress());
    }

    private static native void _getDriveVelocity(long var0, long var2, long var4);
}

