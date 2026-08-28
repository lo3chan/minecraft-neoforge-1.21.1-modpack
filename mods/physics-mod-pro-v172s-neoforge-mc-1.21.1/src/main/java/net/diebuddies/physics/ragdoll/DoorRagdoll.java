/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.DoorBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.DoorHingeSide
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.joml.Quaternionf
 *  org.lwjgl.system.MemoryStack
 */
package net.diebuddies.physics.ragdoll;

import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.ragdoll.DynamicRagdoll;
import net.diebuddies.physics.sound.ContactSimulationCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.Property;
import org.joml.Quaternionf;
import org.lwjgl.system.MemoryStack;
import physx.PxTopLevelFunctions;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.extensions.PxD6AxisEnum;
import physx.extensions.PxD6DriveEnum;
import physx.extensions.PxD6Joint;
import physx.extensions.PxD6JointDrive;
import physx.extensions.PxD6MotionEnum;
import physx.physics.PxFilterData;
import physx.physics.PxForceModeEnum;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidDynamic;

public class DoorRagdoll
extends DynamicRagdoll {
    private static final int DOOR_CLOSE_TIMER = 80;
    public float stiffness = 10.0f;
    public float damping = 60.0f;
    public float currentTarget = 0.0f;
    public long tickDelay = 0L;
    public int switchDir = 80;
    public float direction = 0.0f;

    @Override
    public void updatePhysics(PhysicsWorld physics) {
        super.updatePhysics(physics);
        if (this.hookJoint != null && !this.isFrozen()) {
            float currentAngle = (float)Math.toDegrees(this.hookJoint.getSwingYAngle());
            if (Math.abs(currentAngle - this.currentTarget) > 35.0f && this.btBodies.size() >= 2 && this.tickDelay <= 0L) {
                IRigidBody body = (IRigidBody)this.btBodies.get(1);
                PxRigidDynamic dynamicBody = (PxRigidDynamic)body.getRigidBody();
                if (this.tickDelay == 0L) {
                    this.direction = Math.signum(this.currentTarget - currentAngle);
                }
                try (MemoryStack mem = MemoryStack.stackPush();){
                    dynamicBody.addTorque(PxVec3.createAt(mem, MemoryStack::nmalloc, 0.0f, this.direction * 20.0f, 0.0f), PxForceModeEnum.eVELOCITY_CHANGE);
                }
                --this.switchDir;
                if (this.switchDir == 0) {
                    this.switchDir = 80;
                    this.direction = -this.direction;
                }
            } else {
                this.switchDir = 80;
            }
            --this.tickDelay;
        }
    }

    @Override
    public boolean blockUpdate(PhysicsWorld physics, BlockPos pos, BlockState state) {
        if (this.bodiesPos.size() == 0) {
            return false;
        }
        BlockPos start = (BlockPos)this.bodiesPos.get(0);
        if (start.getX() != pos.getX() || start.getZ() != pos.getZ() || start.getY() != pos.getY()) {
            return false;
        }
        if (state.getBlock() instanceof DoorBlock) {
            try (MemoryStack mem = MemoryStack.stackPush();){
                BlockState initial = (BlockState)this.bodiesState.get(0);
                boolean wasOpen = (Boolean)initial.getValue((Property)DoorBlock.OPEN);
                boolean isOpen = (Boolean)state.getValue((Property)DoorBlock.OPEN);
                if (wasOpen == isOpen) {
                    this.setTargetAngle(0.0f);
                } else {
                    boolean hinge;
                    float degree = 90.0f;
                    boolean bl = hinge = ((BlockState)this.bodiesState.get(0)).getValue((Property)DoorBlock.HINGE) == DoorHingeSide.RIGHT;
                    if (wasOpen) {
                        degree = -degree;
                    }
                    if (hinge) {
                        degree = -degree;
                    }
                    this.setTargetAngle(degree);
                }
                for (IRigidBody body : this.btBodies) {
                    PxRigidActor actor = body.getRigidBody();
                    if (!(actor instanceof PxRigidDynamic)) continue;
                    ((PxRigidDynamic)actor).wakeUp();
                }
            }
            return true;
        }
        this.pxJoints.remove(this.hookJoint);
        this.hookJoint.release();
        this.hookJoint = null;
        physics.getDynamicsWorld().removeActor(this.hookBody.getRigidBody());
        this.btBodies.remove(this.hookBody);
        this.hookBody.destroy();
        this.hookBody = null;
        for (IRigidBody body : this.btBodies) {
            PhysicsEntity entity = body.getEntity();
            entity.time = PhysicsWorld.calculateLifetime(entity);
            if (!this.linkedPhysics) {
                entity.time = 0.0f;
            }
            if (!this.collision) {
                try (MemoryStack stack = MemoryStack.stackPush();){
                    PxFilterData filterData = PxFilterData.createAt(stack, MemoryStack::nmalloc, 2, 23, ContactSimulationCallback.REPORT_CONTACT_FLAGS, 0);
                    body.getShape().setSimulationFilterData(filterData);
                }
            }
            entity.type = PhysicsEntity.Type.BLOCK;
            body.applyRandomSpawnForces();
        }
        this.setFrozen(false);
        this.bodiesPos.clear();
        return false;
    }

    private void setTargetAngle(float degree) {
        Quaternionf rot = new Quaternionf();
        this.currentTarget = degree;
        this.tickDelay = 20L;
        this.switchDir = 80;
        rot.rotateLocalY((float)Math.toRadians(degree));
        try (MemoryStack mem = MemoryStack.stackPush();){
            this.hookJoint.setDrivePosition(PxTransform.createAt(mem, MemoryStack::nmalloc, PxVec3.createAt(mem, MemoryStack::nmalloc, 0.0f, 0.0f, 0.0f), PxQuat.createAt(mem, MemoryStack::nmalloc, rot.x, rot.y, rot.z, rot.w)));
        }
    }

    @Override
    protected PxD6Joint createJoint(PxRigidActor rigidBody1, PxTransform localPose1, PxRigidActor rigidBody2, PxTransform localPose2) {
        PxD6Joint joint = null;
        try (MemoryStack mem = MemoryStack.stackPush();){
            joint = PxTopLevelFunctions.D6JointCreate(StarterClient.physics, rigidBody1, localPose1, rigidBody2, localPose2);
            PxD6JointDrive drive = new PxD6JointDrive(this.stiffness, this.damping, 100000.0f, true);
            joint.setDrivePosition(PxTransform.createAt(mem, MemoryStack::nmalloc, PxVec3.createAt(mem, MemoryStack::nmalloc, 0.0f, 0.0f, 0.0f), PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0f, 0.0f, 0.0f, 1.0f)));
            joint.setDriveVelocity(PxVec3.createAt(mem, MemoryStack::nmalloc, 0.0f, 0.0f, 0.0f), PxVec3.createAt(mem, MemoryStack::nmalloc, 0.0f, 0.0f, 0.0f));
            joint.setMotion(PxD6AxisEnum.eTWIST, PxD6MotionEnum.eLOCKED);
            joint.setMotion(PxD6AxisEnum.eSWING1, PxD6MotionEnum.eFREE);
            joint.setMotion(PxD6AxisEnum.eSWING2, PxD6MotionEnum.eLOCKED);
            joint.setDrive(PxD6DriveEnum.eSWING, drive);
            drive.destroy();
        }
        return joint;
    }
}

