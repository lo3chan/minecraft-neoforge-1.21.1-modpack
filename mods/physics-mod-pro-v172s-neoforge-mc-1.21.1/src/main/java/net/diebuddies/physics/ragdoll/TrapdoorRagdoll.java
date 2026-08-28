/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.TrapDoorBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Half
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
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
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
import physx.extensions.PxJointLinearLimit;
import physx.extensions.PxSpring;
import physx.physics.PxFilterData;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidDynamic;

public class TrapdoorRagdoll
extends DynamicRagdoll {
    public float stiffness = 10.0f;
    public float damping = 60.0f;
    public boolean west = false;

    @Override
    public boolean blockUpdate(PhysicsWorld physics, BlockPos pos, BlockState state) {
        if (this.bodiesPos.size() == 0) {
            return false;
        }
        BlockPos start = (BlockPos)this.bodiesPos.get(0);
        if (start.getX() != pos.getX() || start.getZ() != pos.getZ() || start.getY() != pos.getY()) {
            return false;
        }
        if (state.getBlock() instanceof TrapDoorBlock) {
            try (MemoryStack mem = MemoryStack.stackPush();){
                boolean isTop;
                BlockState initial = (BlockState)this.bodiesState.get(0);
                boolean wasOpen = (Boolean)initial.getValue((Property)TrapDoorBlock.OPEN);
                boolean isOpen = (Boolean)state.getValue((Property)TrapDoorBlock.OPEN);
                boolean bl = isTop = initial.getValue((Property)TrapDoorBlock.HALF) == Half.TOP;
                if (wasOpen == isOpen) {
                    this.hookJoint.setDrivePosition(PxTransform.createAt(mem, MemoryStack::nmalloc, PxVec3.createAt(mem, MemoryStack::nmalloc, 0.0f, 0.0f, 0.0f), PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0f, 0.0f, 0.0f, 1.0f)));
                } else {
                    float degree;
                    Quaternionf rot = new Quaternionf();
                    float f = degree = isTop ? -90.0f : 90.0f;
                    if (wasOpen) {
                        degree = -degree;
                    }
                    switch ((Direction)initial.getValue((Property)TrapDoorBlock.FACING)) {
                        default: {
                            rot.rotateLocalX((float)Math.toRadians(degree));
                            break;
                        }
                        case SOUTH: {
                            rot.rotateLocalX((float)Math.toRadians(-degree));
                            break;
                        }
                        case WEST: {
                            rot.rotateLocalZ((float)Math.toRadians(-degree));
                            break;
                        }
                        case EAST: {
                            rot.rotateLocalZ((float)Math.toRadians(degree));
                        }
                    }
                    this.hookJoint.setDrivePosition(PxTransform.createAt(mem, MemoryStack::nmalloc, PxVec3.createAt(mem, MemoryStack::nmalloc, 0.0f, 0.0f, 0.0f), PxQuat.createAt(mem, MemoryStack::nmalloc, rot.x, rot.y, rot.z, rot.w)));
                }
                for (IRigidBody body : this.btBodies) {
                    PxRigidActor actor = body.getRigidBody();
                    if (!(actor instanceof PxRigidDynamic)) continue;
                    ((PxRigidDynamic)actor).wakeUp();
                }
            }
            return true;
        }
        this.hookJoint.release();
        this.hookJoint = null;
        this.pxJoints.clear();
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

    @Override
    protected PxD6Joint createJoint(PxRigidActor rigidBody1, PxTransform localPose1, PxRigidActor rigidBody2, PxTransform localPose2) {
        PxD6Joint joint = null;
        try (MemoryStack mem = MemoryStack.stackPush();){
            joint = PxTopLevelFunctions.D6JointCreate(StarterClient.physics, rigidBody1, localPose1, rigidBody2, localPose2);
            PxD6JointDrive drive = new PxD6JointDrive(this.stiffness, this.damping, 100000.0f, true);
            joint.setDrivePosition(PxTransform.createAt(mem, MemoryStack::nmalloc, PxVec3.createAt(mem, MemoryStack::nmalloc, 0.0f, 0.0f, 0.0f), PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0f, 0.0f, 0.0f, 1.0f)));
            joint.setDriveVelocity(PxVec3.createAt(mem, MemoryStack::nmalloc, 0.0f, 0.0f, 0.0f), PxVec3.createAt(mem, MemoryStack::nmalloc, 0.0f, 0.0f, 0.0f));
            if (this.west) {
                joint.setMotion(PxD6AxisEnum.eTWIST, PxD6MotionEnum.eLOCKED);
                joint.setMotion(PxD6AxisEnum.eSWING1, PxD6MotionEnum.eLOCKED);
                joint.setMotion(PxD6AxisEnum.eSWING2, PxD6MotionEnum.eFREE);
                joint.setDrive(PxD6DriveEnum.eSWING, drive);
            } else {
                joint.setMotion(PxD6AxisEnum.eTWIST, PxD6MotionEnum.eFREE);
                joint.setMotion(PxD6AxisEnum.eSWING1, PxD6MotionEnum.eLOCKED);
                joint.setMotion(PxD6AxisEnum.eSWING2, PxD6MotionEnum.eLOCKED);
                joint.setDrive(PxD6DriveEnum.eTWIST, drive);
            }
            PxSpring spring = new PxSpring(900.0f, 20.0f);
            PxJointLinearLimit distanceLimit = new PxJointLinearLimit(0.003f, spring);
            joint.setDistanceLimit(distanceLimit);
            drive.destroy();
            spring.destroy();
            distanceLimit.destroy();
        }
        return joint;
    }
}

