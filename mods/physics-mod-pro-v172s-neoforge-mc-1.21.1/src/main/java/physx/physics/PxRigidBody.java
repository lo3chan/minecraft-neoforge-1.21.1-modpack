/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.physics.PxForceModeEnum;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidBodyFlagEnum;
import physx.physics.PxRigidBodyFlags;

public class PxRigidBody
extends PxRigidActor {
    public static final int SIZEOF = PxRigidBody.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxRigidBody() {
    }

    private static native int __sizeOf();

    public static PxRigidBody wrapPointer(long address) {
        return address != 0L ? new PxRigidBody(address) : null;
    }

    public static PxRigidBody arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxRigidBody.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxRigidBody(long address) {
        super(address);
    }

    public void setCMassLocalPose(PxTransform pose) {
        this.checkNotNull();
        PxRigidBody._setCMassLocalPose(this.address, pose.getAddress());
    }

    private static native void _setCMassLocalPose(long var0, long var2);

    public PxTransform getCMassLocalPose() {
        this.checkNotNull();
        return PxTransform.wrapPointer(PxRigidBody._getCMassLocalPose(this.address));
    }

    private static native long _getCMassLocalPose(long var0);

    public void setMass(float mass) {
        this.checkNotNull();
        PxRigidBody._setMass(this.address, mass);
    }

    private static native void _setMass(long var0, float var2);

    public float getMass() {
        this.checkNotNull();
        return PxRigidBody._getMass(this.address);
    }

    private static native float _getMass(long var0);

    public float getInvMass() {
        this.checkNotNull();
        return PxRigidBody._getInvMass(this.address);
    }

    private static native float _getInvMass(long var0);

    public void setMassSpaceInertiaTensor(PxVec3 m) {
        this.checkNotNull();
        PxRigidBody._setMassSpaceInertiaTensor(this.address, m.getAddress());
    }

    private static native void _setMassSpaceInertiaTensor(long var0, long var2);

    public PxVec3 getMassSpaceInertiaTensor() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxRigidBody._getMassSpaceInertiaTensor(this.address));
    }

    private static native long _getMassSpaceInertiaTensor(long var0);

    public PxVec3 getMassSpaceInvInertiaTensor() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxRigidBody._getMassSpaceInvInertiaTensor(this.address));
    }

    private static native long _getMassSpaceInvInertiaTensor(long var0);

    public void setLinearDamping(float linDamp) {
        this.checkNotNull();
        PxRigidBody._setLinearDamping(this.address, linDamp);
    }

    private static native void _setLinearDamping(long var0, float var2);

    public float getLinearDamping() {
        this.checkNotNull();
        return PxRigidBody._getLinearDamping(this.address);
    }

    private static native float _getLinearDamping(long var0);

    public void setAngularDamping(float angDamp) {
        this.checkNotNull();
        PxRigidBody._setAngularDamping(this.address, angDamp);
    }

    private static native void _setAngularDamping(long var0, float var2);

    public float getAngularDamping() {
        this.checkNotNull();
        return PxRigidBody._getAngularDamping(this.address);
    }

    private static native float _getAngularDamping(long var0);

    public PxVec3 getLinearVelocity() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxRigidBody._getLinearVelocity(this.address));
    }

    private static native long _getLinearVelocity(long var0);

    public PxVec3 getAngularVelocity() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxRigidBody._getAngularVelocity(this.address));
    }

    private static native long _getAngularVelocity(long var0);

    public void setMaxLinearVelocity(float maxLinVel) {
        this.checkNotNull();
        PxRigidBody._setMaxLinearVelocity(this.address, maxLinVel);
    }

    private static native void _setMaxLinearVelocity(long var0, float var2);

    public float getMaxLinearVelocity() {
        this.checkNotNull();
        return PxRigidBody._getMaxLinearVelocity(this.address);
    }

    private static native float _getMaxLinearVelocity(long var0);

    public void setMaxAngularVelocity(float maxAngVel) {
        this.checkNotNull();
        PxRigidBody._setMaxAngularVelocity(this.address, maxAngVel);
    }

    private static native void _setMaxAngularVelocity(long var0, float var2);

    public float getMaxAngularVelocity() {
        this.checkNotNull();
        return PxRigidBody._getMaxAngularVelocity(this.address);
    }

    private static native float _getMaxAngularVelocity(long var0);

    public void addForce(PxVec3 force) {
        this.checkNotNull();
        PxRigidBody._addForce(this.address, force.getAddress());
    }

    private static native void _addForce(long var0, long var2);

    public void addForce(PxVec3 force, PxForceModeEnum mode) {
        this.checkNotNull();
        PxRigidBody._addForce(this.address, force.getAddress(), mode.value);
    }

    private static native void _addForce(long var0, long var2, int var4);

    public void addForce(PxVec3 force, PxForceModeEnum mode, boolean autowake) {
        this.checkNotNull();
        PxRigidBody._addForce(this.address, force.getAddress(), mode.value, autowake);
    }

    private static native void _addForce(long var0, long var2, int var4, boolean var5);

    public void addTorque(PxVec3 torque) {
        this.checkNotNull();
        PxRigidBody._addTorque(this.address, torque.getAddress());
    }

    private static native void _addTorque(long var0, long var2);

    public void addTorque(PxVec3 torque, PxForceModeEnum mode) {
        this.checkNotNull();
        PxRigidBody._addTorque(this.address, torque.getAddress(), mode.value);
    }

    private static native void _addTorque(long var0, long var2, int var4);

    public void addTorque(PxVec3 torque, PxForceModeEnum mode, boolean autowake) {
        this.checkNotNull();
        PxRigidBody._addTorque(this.address, torque.getAddress(), mode.value, autowake);
    }

    private static native void _addTorque(long var0, long var2, int var4, boolean var5);

    public void clearForce(PxForceModeEnum mode) {
        this.checkNotNull();
        PxRigidBody._clearForce(this.address, mode.value);
    }

    private static native void _clearForce(long var0, int var2);

    public void clearTorque(PxForceModeEnum mode) {
        this.checkNotNull();
        PxRigidBody._clearTorque(this.address, mode.value);
    }

    private static native void _clearTorque(long var0, int var2);

    public void setForceAndTorque(PxVec3 force, PxVec3 torque) {
        this.checkNotNull();
        PxRigidBody._setForceAndTorque(this.address, force.getAddress(), torque.getAddress());
    }

    private static native void _setForceAndTorque(long var0, long var2, long var4);

    public void setForceAndTorque(PxVec3 force, PxVec3 torque, PxForceModeEnum mode) {
        this.checkNotNull();
        PxRigidBody._setForceAndTorque(this.address, force.getAddress(), torque.getAddress(), mode.value);
    }

    private static native void _setForceAndTorque(long var0, long var2, long var4, int var6);

    public void setRigidBodyFlag(PxRigidBodyFlagEnum flag, boolean value) {
        this.checkNotNull();
        PxRigidBody._setRigidBodyFlag(this.address, flag.value, value);
    }

    private static native void _setRigidBodyFlag(long var0, int var2, boolean var3);

    public void setRigidBodyFlags(PxRigidBodyFlags inFlags) {
        this.checkNotNull();
        PxRigidBody._setRigidBodyFlags(this.address, inFlags.getAddress());
    }

    private static native void _setRigidBodyFlags(long var0, long var2);

    public PxRigidBodyFlags getRigidBodyFlags() {
        this.checkNotNull();
        return PxRigidBodyFlags.wrapPointer(PxRigidBody._getRigidBodyFlags(this.address));
    }

    private static native long _getRigidBodyFlags(long var0);

    public void setMinCCDAdvanceCoefficient(float advanceCoefficient) {
        this.checkNotNull();
        PxRigidBody._setMinCCDAdvanceCoefficient(this.address, advanceCoefficient);
    }

    private static native void _setMinCCDAdvanceCoefficient(long var0, float var2);

    public float getMinCCDAdvanceCoefficient() {
        this.checkNotNull();
        return PxRigidBody._getMinCCDAdvanceCoefficient(this.address);
    }

    private static native float _getMinCCDAdvanceCoefficient(long var0);

    public void setMaxDepenetrationVelocity(float biasClamp) {
        this.checkNotNull();
        PxRigidBody._setMaxDepenetrationVelocity(this.address, biasClamp);
    }

    private static native void _setMaxDepenetrationVelocity(long var0, float var2);

    public float getMaxDepenetrationVelocity() {
        this.checkNotNull();
        return PxRigidBody._getMaxDepenetrationVelocity(this.address);
    }

    private static native float _getMaxDepenetrationVelocity(long var0);

    public void setMaxContactImpulse(float maxImpulse) {
        this.checkNotNull();
        PxRigidBody._setMaxContactImpulse(this.address, maxImpulse);
    }

    private static native void _setMaxContactImpulse(long var0, float var2);

    public float getMaxContactImpulse() {
        this.checkNotNull();
        return PxRigidBody._getMaxContactImpulse(this.address);
    }

    private static native float _getMaxContactImpulse(long var0);

    public void setContactSlopCoefficient(float slopCoefficient) {
        this.checkNotNull();
        PxRigidBody._setContactSlopCoefficient(this.address, slopCoefficient);
    }

    private static native void _setContactSlopCoefficient(long var0, float var2);

    public float getContactSlopCoefficient() {
        this.checkNotNull();
        return PxRigidBody._getContactSlopCoefficient(this.address);
    }

    private static native float _getContactSlopCoefficient(long var0);
}

