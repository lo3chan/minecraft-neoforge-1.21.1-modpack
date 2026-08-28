/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.state.BlockState
 *  org.joml.Matrix4d
 *  org.joml.Matrix4dc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.lwjgl.system.MemoryStack
 */
package net.diebuddies.physics.ragdoll;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.Mesh;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.ragdoll.DynamicRagdoll;
import net.diebuddies.physics.ragdoll.Ragdoll;
import net.diebuddies.physics.ragdoll.RagdollJoint;
import net.diebuddies.physics.vines.VineHelper;
import net.diebuddies.physics.vines.VineSetting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4d;
import org.joml.Matrix4dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
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
import physx.extensions.PxJoint;
import physx.extensions.PxJointAngularLimitPair;
import physx.extensions.PxSpring;
import physx.physics.PxFilterData;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidDynamic;

public class VineRagdoll
extends DynamicRagdoll {
    public List<Connector> connectors = new ObjectArrayList();
    public boolean bottomFixed;
    public float stiffness = 10.0f;
    public float damping = 60.0f;
    private boolean alwaysInWater;

    @Override
    public void updatePhysics(PhysicsWorld physics) {
        super.updatePhysics(physics);
        if (this.alwaysInWater) {
            for (IRigidBody body : this.btBodies) {
                body.setGravity(false);
            }
        }
    }

    @Override
    public boolean blockUpdate(PhysicsWorld physics, BlockPos pos, BlockState state) {
        if (this.bodiesPos.size() == 0) {
            return false;
        }
        BlockPos start = (BlockPos)this.bodiesPos.get(0);
        if (start.getX() != pos.getX() || start.getZ() != pos.getZ()) {
            return false;
        }
        int index = this.bodiesPos.indexOf(pos);
        if (index != -1) {
            VineSetting setting = (VineSetting)VineHelper.getSetting((BlockState)this.bodiesState.get(index));
            if (setting != null && !setting.canLink((BlockState)this.bodiesState.get(index), state)) {
                boolean sideConnection = setting.sideConnection;
                int cutJoint = -1;
                BlockPos connectionDir = this.bottomFixed ? pos.below() : pos.above();
                for (int i = 0; i < this.connectors.size(); ++i) {
                    if (!this.connectors.get(i).connects(pos, connectionDir)) continue;
                    cutJoint = i;
                    break;
                }
                PxJoint releasedJoint = (PxJoint)this.pxJoints.remove(cutJoint);
                releasedJoint.release();
                ObjectArrayList bodiesNew = new ObjectArrayList();
                ObjectArrayList btBodiesNew = new ObjectArrayList();
                ObjectArrayList pxJointsNew = new ObjectArrayList();
                ObjectArrayList connectorsNew = new ObjectArrayList();
                Connector cutConnector = this.connectors.remove(cutJoint);
                BlockPos pos1 = pos;
                BlockPos pos2 = connectionDir;
                ObjectOpenHashSet removedPositions = new ObjectOpenHashSet();
                block11: while (cutConnector != null) {
                    cutConnector = null;
                    pos1 = this.bottomFixed ? pos1.above() : pos1.below();
                    pos2 = this.bottomFixed ? pos2.above() : pos2.below();
                    removedPositions.add(pos1);
                    removedPositions.add(pos2);
                    this.moveBodiesIntoNewRagdoll(pos1, (List<IRigidBody>)btBodiesNew, (List<PhysicsEntity>)bodiesNew);
                    this.moveBodiesIntoNewRagdoll(pos2, (List<IRigidBody>)btBodiesNew, (List<PhysicsEntity>)bodiesNew);
                    for (int i = 0; i < this.connectors.size(); ++i) {
                        Connector connection = this.connectors.get(i);
                        if (!connection.connects(pos1, pos2)) continue;
                        cutConnector = connection;
                        connectorsNew.add(this.connectors.remove(i));
                        pxJointsNew.add((PxJoint)this.pxJoints.remove(i));
                        continue block11;
                    }
                }
                this.validateHitbox();
                removedPositions.remove(pos);
                VineRagdoll vine = new VineRagdoll();
                vine.hitboxScale.set((Vector3fc)this.hitboxScale);
                vine.bottomFixed = this.bottomFixed;
                vine.bodies.addAll(bodiesNew);
                vine.btBodies.addAll(btBodiesNew);
                vine.pxJoints.addAll(pxJointsNew);
                vine.connectors.addAll((Collection<Connector>)connectorsNew);
                vine.initFreeze = false;
                vine.linkedPhysics = this.linkedPhysics;
                ((IRigidBody)vine.btBodies.get(0)).applyRandomSpawnForces();
                if (this.isFrozen()) {
                    vine.setFrozen(false);
                }
                for (Object body : vine.btBodies) {
                    if (((IRigidBody)body).getRigidBody() instanceof PxRigidDynamic) {
                        ((PxRigidDynamic)((IRigidBody)body).getRigidBody()).wakeUp();
                    }
                    if (!((IRigidBody)body).isInWater()) {
                        ((IRigidBody)body).setGravity(true);
                    }
                    MemoryStack mem = MemoryStack.stackPush();
                    try {
                        PxFilterData tmpFilterData = PxFilterData.createAt(mem, MemoryStack::nmalloc, 1, 1, 0, 0);
                        ((IRigidBody)body).getShape().setSimulationFilterData(tmpFilterData);
                    }
                    finally {
                        if (mem == null) continue;
                        mem.close();
                    }
                }
                float rnd = vine.bodies.size() > 0 ? PhysicsWorld.calculateLifetime((PhysicsEntity)vine.bodies.get(0)) : 0.0f;
                for (Object entity : vine.bodies) {
                    ((PhysicsEntity)entity).type = PhysicsEntity.Type.BLOCK;
                    ((PhysicsEntity)entity).time = rnd;
                    if (this.linkedPhysics) continue;
                    ((PhysicsEntity)entity).time = 0.0f;
                }
                if (this.hookJoint == releasedJoint) {
                    physics.getDynamicsWorld().removeActor(this.hookBody.getRigidBody());
                    this.btBodies.remove(this.hookBody);
                    this.hookBody.destroy();
                    this.hookBody = null;
                }
                this.setFrozen(false);
                physics.getRagdolls().add(vine);
                if (sideConnection) {
                    ObjectArrayList sorted = new ObjectArrayList((Collection)removedPositions);
                    Collections.sort(sorted, (a, b) -> -Integer.compare(a.getY(), b.getY()));
                    for (BlockPos removedPosition : sorted) {
                        physics.blockUpdate(removedPosition);
                    }
                }
                return true;
            }
            BlockState before = (BlockState)this.bodiesState.get(index);
            if (before != state) {
                PhysicsEntity entity = PhysicsMod.getInstance(physics.getWorld()).renderBlockIntoEntity(physics.getLevel(), PhysicsEntity.Type.VINE, state, pos, true);
                if (entity == null) {
                    entity = new PhysicsEntity(PhysicsEntity.Type.VINE, state);
                    entity.getTransformation().set((Matrix4dc)new Matrix4d().translate((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5));
                    entity.getOldTransformation().set((Matrix4dc)entity.getTransformation());
                    entity.models.get((int)0).mesh = new Mesh();
                    entity.models.get((int)0).mesh.offset = new Vector3f();
                }
                entity.enlargeHitbox.set((Vector3fc)this.hitboxScale);
                if (((PhysicsEntity)this.bodies.get((int)index)).models.size() > 0) {
                    Vector3f diff = entity.models.get((int)0).mesh.offset.sub((Vector3fc)((PhysicsEntity)this.bodies.get((int)index)).models.get((int)0).mesh.offset, new Vector3f());
                    diff.y %= 1.0f;
                    entity.models.get((int)0).mesh.move(diff);
                    entity.models.get((int)0).mesh.offset.set((Vector3fc)((PhysicsEntity)this.bodies.get((int)index)).models.get((int)0).mesh.offset);
                }
                ((PhysicsEntity)this.bodies.get(index)).destroy();
                ((PhysicsEntity)this.bodies.get((int)index)).models = entity.models;
                this.bodiesState.set(index, state);
                physics.getQueueForModelCreation().add((PhysicsEntity)this.bodies.get(index));
            }
            return true;
        }
        if (this.bodiesPos.size() > 0) {
            int highestY = ((BlockPos)this.bodiesPos.get(0)).getY();
            int highestIndex = 0;
            for (int i = 1; i < this.bodiesPos.size(); ++i) {
                int y = ((BlockPos)this.bodiesPos.get(i)).getY();
                if (this.bottomFixed) {
                    if (y <= highestY) continue;
                    highestY = y;
                    highestIndex = i;
                    continue;
                }
                if (y >= highestY) continue;
                highestY = y;
                highestIndex = i;
            }
            BlockPos check = new BlockPos(pos.getX(), highestY, pos.getZ());
            index = this.bodiesPos.indexOf(check);
            VineSetting setting = (VineSetting)VineHelper.getSetting((BlockState)this.bodiesState.get(index));
            if (setting != null && index != -1 && (this.bottomFixed ? highestY + 1 == pos.getY() : highestY - 1 == pos.getY()) && setting.canLink((BlockState)this.bodiesState.get(index), state)) {
                PhysicsEntity entity;
                IRigidBody appendTo = null;
                PhysicsEntity highestEntity = (PhysicsEntity)this.bodies.get(highestIndex);
                for (int i = 0; i < this.btBodies.size(); ++i) {
                    IRigidBody body = (IRigidBody)this.btBodies.get(i);
                    if (!body.getEntity().equals(highestEntity)) continue;
                    appendTo = body;
                    break;
                }
                if ((entity = PhysicsMod.getInstance(physics.getWorld()).renderBlockIntoEntity(physics.getLevel(), PhysicsEntity.Type.VINE, state, pos, true)) == null) {
                    entity = new PhysicsEntity(PhysicsEntity.Type.VINE, state);
                    entity.getTransformation().set((Matrix4dc)new Matrix4d().translate((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5));
                    entity.getOldTransformation().set((Matrix4dc)entity.getTransformation());
                    entity.models.get((int)0).mesh = new Mesh();
                    entity.models.get((int)0).mesh.offset = new Vector3f();
                }
                entity.enlargeHitbox.set((Vector3fc)this.hitboxScale);
                Connector connector = new Connector(this, check, pos);
                this.connectors.add(connector);
                this.bodiesPos.add(pos);
                this.bodiesState.add(state);
                this.bodies.add(entity);
                this.validateHitbox();
                IRigidBody childLink = physics.addBlockParticleBox(entity);
                entity.time = PhysicsWorld.calculateLifetime(entity);
                childLink.setFrozen(this.frozen);
                childLink.separateController = true;
                this.btBodies.add(childLink);
                try (MemoryStack mem = MemoryStack.stackPush();){
                    Vector3f localPos1 = childLink.getEntity().models.get((int)0).mesh.offset;
                    Vector3f localPos2 = appendTo.getEntity().models.get((int)0).mesh.offset;
                    double childOffY = this.bottomFixed ? -((double)localPos1.y % 1.0) : 1.0 - (double)localPos1.y % 1.0;
                    double parentOffY = this.bottomFixed ? 1.0 - (double)localPos2.y % 1.0 : -((double)localPos2.y % 1.0);
                    PxTransform parentPose = PxTransform.createAt(mem, MemoryStack::nmalloc, PxVec3.createAt(mem, MemoryStack::nmalloc, 0.0f, (float)parentOffY, 0.0f), PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0f, 0.0f, 0.0f, 1.0f));
                    PxTransform childPose = PxTransform.createAt(mem, MemoryStack::nmalloc, PxVec3.createAt(mem, MemoryStack::nmalloc, localPos2.x - localPos1.x, (float)childOffY, localPos2.z - localPos1.z), PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0f, 0.0f, 0.0f, 1.0f));
                    PxD6Joint joint = this.createJoint(appendTo.getRigidBody(), parentPose, childLink.getRigidBody(), childPose);
                    this.pxJoints.add(joint);
                }
                return true;
            }
        }
        return false;
    }

    private void moveBodiesIntoNewRagdoll(BlockPos pos, List<IRigidBody> btBodiesNew, List<PhysicsEntity> bodiesNew) {
        int removeBody = this.bodiesPos.indexOf(pos);
        if (removeBody != -1) {
            this.bodiesPos.remove(removeBody);
            this.bodiesState.remove(removeBody);
            PhysicsEntity entity = (PhysicsEntity)this.bodies.remove(removeBody);
            bodiesNew.add(entity);
            for (int i = 0; i < this.btBodies.size(); ++i) {
                IRigidBody btBody = (IRigidBody)this.btBodies.get(i);
                if (!btBody.getEntity().equals(entity)) continue;
                this.btBodies.remove(btBody);
                btBodiesNew.add(btBody);
                break;
            }
        }
    }

    public void setAlwaysInWater(boolean alwaysInWater) {
        this.alwaysInWater = alwaysInWater;
        for (IRigidBody body : this.btBodies) {
            if (!body.hasGravity()) continue;
            body.setGravity(!alwaysInWater);
        }
    }

    @Override
    protected void createHook(PhysicsWorld physics, PhysicsEntity particle, IRigidBody rigidBody) {
        super.createHook(physics, particle, rigidBody);
        BlockPos pos = (BlockPos)this.bodiesPos.get(this.bodies.indexOf(rigidBody.getEntity()));
        this.connectors.add(new Connector(this, pos, this.bottomFixed ? pos.below() : pos.above()));
    }

    @Override
    protected void createChildLink(PhysicsWorld physics, IRigidBody rootLink, Ragdoll.Node root, double rnd) {
        PhysicsEntity particle = (PhysicsEntity)this.bodies.get(root.index);
        if (!particle.noVolume) {
            RagdollJoint rjoint = (RagdollJoint)this.joints.get(root.jointIndex);
            if (rjoint.fixed) {
                return;
            }
            if (rjoint.stopCollision) {
                particle.physicsGroup = (byte)8;
                particle.physicsMask = 0;
            }
            IRigidBody childLink = physics.addBlockParticleBox(particle);
            childLink.setFrozen(this.frozen);
            childLink.separateController = true;
            this.btBodies.add(childLink);
            try (MemoryStack mem = MemoryStack.stackPush();){
                PxTransform parentPose = PxTransform.createAt(mem, MemoryStack::nmalloc, PxVec3.createAt(mem, MemoryStack::nmalloc, (float)rjoint.point1.x, (float)rjoint.point1.y, (float)rjoint.point1.z), PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0f, 0.0f, 0.0f, 1.0f));
                PxTransform childPose = PxTransform.createAt(mem, MemoryStack::nmalloc, PxVec3.createAt(mem, MemoryStack::nmalloc, (float)rjoint.point2.x, (float)rjoint.point2.y, (float)rjoint.point2.z), PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0f, 0.0f, 0.0f, 1.0f));
                if (rjoint.index1 == root.index) {
                    PxTransform tmp = parentPose;
                    parentPose = childPose;
                    childPose = tmp;
                }
                PxD6Joint joint = this.createJoint(rootLink.getRigidBody(), parentPose, childLink.getRigidBody(), childPose);
                this.pxJoints.add(joint);
                this.connectors.add(new Connector(this, (BlockPos)this.bodiesPos.get(this.bodies.indexOf(rootLink.getEntity())), (BlockPos)this.bodiesPos.get(this.bodies.indexOf(childLink.getEntity()))));
                for (int i = 0; i < root.children.size(); ++i) {
                    this.createChildLink(physics, childLink, root.children.get(i), rnd);
                }
            }
            if (particle.equals(this.hookedEntity)) {
                this.createHook(physics, particle, childLink);
            }
        }
    }

    @Override
    protected PxD6Joint createJoint(PxRigidActor rigidBody1, PxTransform localPose1, PxRigidActor rigidBody2, PxTransform localPose2) {
        PxD6Joint joint = null;
        try (MemoryStack mem = MemoryStack.stackPush();){
            joint = PxTopLevelFunctions.D6JointCreate(StarterClient.physics, rigidBody1, localPose1, rigidBody2, localPose2);
            joint.setMotion(PxD6AxisEnum.eTWIST, PxD6MotionEnum.eLIMITED);
            joint.setMotion(PxD6AxisEnum.eSWING1, PxD6MotionEnum.eLIMITED);
            joint.setMotion(PxD6AxisEnum.eSWING2, PxD6MotionEnum.eLIMITED);
            PxD6JointDrive drive = new PxD6JointDrive(this.stiffness, this.damping, 100000.0f, true);
            joint.setDrive(PxD6DriveEnum.eSWING, drive);
            joint.setDrive(PxD6DriveEnum.eTWIST, drive);
            joint.setDrivePosition(PxTransform.createAt(mem, MemoryStack::nmalloc, PxVec3.createAt(mem, MemoryStack::nmalloc, 0.0f, 0.0f, 0.0f), PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0f, 0.0f, 0.0f, 1.0f)));
            joint.setDriveVelocity(PxVec3.createAt(mem, MemoryStack::nmalloc, 0.0f, 0.0f, 0.0f), PxVec3.createAt(mem, MemoryStack::nmalloc, 0.0f, 0.0f, 0.0f));
            PxSpring spring = new PxSpring(60.0f, 10.0f);
            PxJointAngularLimitPair angularLimit = new PxJointAngularLimitPair(-0.3926991f, 0.3926991f, spring);
            joint.setTwistLimit(angularLimit);
            drive.destroy();
            spring.destroy();
            angularLimit.destroy();
        }
        return joint;
    }

    class Connector {
        BlockPos pos1;
        BlockPos pos2;

        public Connector(VineRagdoll this$0, BlockPos pos1, BlockPos pos2) {
            this.pos1 = pos1;
            this.pos2 = pos2;
        }

        public boolean connects(BlockPos pos1, BlockPos pos2) {
            return this.pos1.equals((Object)pos1) && this.pos2.equals((Object)pos2) || this.pos1.equals((Object)pos2) && this.pos2.equals((Object)pos1);
        }
    }
}

