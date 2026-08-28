/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.util.Tuple
 *  org.lwjgl.system.MemoryStack
 */
package net.diebuddies.physics.ragdoll;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.diebuddies.math.Math;
import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.ragdoll.Ragdoll;
import net.diebuddies.physics.ragdoll.RagdollJoint;
import net.minecraft.util.Tuple;
import org.lwjgl.system.MemoryStack;
import physx.PxTopLevelFunctions;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.extensions.PxD6AxisEnum;
import physx.extensions.PxD6Joint;
import physx.extensions.PxD6MotionEnum;
import physx.extensions.PxJoint;
import physx.extensions.PxJointAngularLimitPair;
import physx.extensions.PxJointLimitCone;
import physx.extensions.PxSpring;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidDynamic;

public class BreakableRagdoll
extends Ragdoll {
    private float breakForce;
    public List<PxJoint> pxJoints = new ObjectArrayList();

    public BreakableRagdoll(float breakForce) {
        this.breakForce = breakForce;
    }

    @Override
    public void add(PhysicsWorld physics) {
        float rnd = -1.0f;
        List<Ragdoll.Node> tree = this.generateTree();
        for (Ragdoll.Node root : tree) {
            for (int i = 0; i < root.children.size(); ++i) {
                this.createChildLinkPrePass(root, root.children.get(i));
            }
        }
        for (Ragdoll.Node root : tree) {
            PhysicsEntity particle = (PhysicsEntity)this.bodies.get(root.index);
            if (particle.noVolume) continue;
            if ((double)rnd < 0.0) {
                rnd = PhysicsWorld.calculateLifetime(particle);
            }
            if (physics.getBodies().size() == 0 && physics.getChunkBodies().size() == 0) {
                particle.getTransformation().getTranslation(physics.getOffset());
            }
            IRigidBody rigidBody = physics.addBlockParticle(particle);
            particle.time = rnd;
            rigidBody.separateController = true;
            this.btBodies.add(rigidBody);
            for (int i = 0; i < root.children.size(); ++i) {
                this.createChildLink(physics, rigidBody, root, root.children.get(i), rnd);
            }
        }
        if (this.velocity != null && this.velocity.lengthSquared() > 0.01) {
            for (IRigidBody body : this.btBodies) {
                PxRigidActor rigidBody = body.getRigidBody();
                if (!(rigidBody instanceof PxRigidDynamic)) continue;
                PxRigidDynamic rigidBody2 = (PxRigidDynamic)rigidBody;
                float angularForce = 10.0f;
                PxVec3 v = rigidBody2.getLinearVelocity();
                v.setX((float)this.velocity.x);
                v.setY((float)this.velocity.y);
                v.setZ((float)this.velocity.z);
                rigidBody2.setLinearVelocity(v, true);
                PxVec3 a = rigidBody2.getAngularVelocity();
                a.setX((Math.random() - 0.5f) * angularForce);
                a.setY((Math.random() - 0.5f) * angularForce);
                a.setZ((Math.random() - 0.5f) * angularForce);
                rigidBody2.setAngularVelocity(a);
            }
            this.velocity = null;
        }
    }

    private void createChildLink(PhysicsWorld physics, IRigidBody rootLink, Ragdoll.Node parent, Ragdoll.Node root, float rnd) {
        PhysicsEntity particle = (PhysicsEntity)this.bodies.get(root.index);
        if (!particle.noVolume) {
            RagdollJoint rjoint = (RagdollJoint)this.joints.get(root.jointIndex);
            if (rjoint.fixed) {
                return;
            }
            if (rjoint.stopCollision) {
                particle.physicsGroup = (byte)2;
                particle.physicsMask = (byte)21;
            }
            IRigidBody rigidBody = physics.addBlockParticle(particle);
            particle.time = rnd;
            rigidBody.separateController = true;
            this.btBodies.add(rigidBody);
            try (MemoryStack mem = MemoryStack.stackPush();){
                PxTransform parentPose = PxTransform.createAt(mem, MemoryStack::nmalloc, PxVec3.createAt(mem, MemoryStack::nmalloc, (float)rjoint.point1.x, (float)rjoint.point1.y, (float)rjoint.point1.z), PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0f, 0.0f, 0.0f, 1.0f));
                PxTransform childPose = PxTransform.createAt(mem, MemoryStack::nmalloc, PxVec3.createAt(mem, MemoryStack::nmalloc, (float)rjoint.point2.x, (float)rjoint.point2.y, (float)rjoint.point2.z), PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0f, 0.0f, 0.0f, 1.0f));
                if (rjoint.index1 == root.index) {
                    PxTransform tmp = parentPose;
                    parentPose = childPose;
                    childPose = tmp;
                }
                PxD6Joint joint = this.createJoint(rootLink.getRigidBody(), parentPose, rigidBody.getRigidBody(), childPose);
                this.pxJoints.add(joint);
                physics.addJointParents(joint, (Tuple<IRigidBody, IRigidBody>)new Tuple((Object)rootLink, (Object)rigidBody));
            }
            for (int i = 0; i < root.children.size(); ++i) {
                this.createChildLink(physics, rigidBody, root, root.children.get(i), rnd);
            }
        }
    }

    protected PxD6Joint createJoint(PxRigidActor rigidBody1, PxTransform localPose1, PxRigidActor rigidBody2, PxTransform localPose2) {
        PxD6Joint joint = null;
        try (MemoryStack mem = MemoryStack.stackPush();){
            joint = PxTopLevelFunctions.D6JointCreate(StarterClient.physics, rigidBody1, localPose1, rigidBody2, localPose2);
            joint.setMotion(PxD6AxisEnum.eTWIST, PxD6MotionEnum.eLIMITED);
            joint.setMotion(PxD6AxisEnum.eSWING1, PxD6MotionEnum.eLIMITED);
            joint.setMotion(PxD6AxisEnum.eSWING2, PxD6MotionEnum.eLIMITED);
            if (this.breakForce > 0.0f) {
                joint.setBreakForce(this.breakForce, this.breakForce);
            }
            PxSpring spring = new PxSpring(1.0f, 1.0f);
            PxJointLimitCone coneLimit = new PxJointLimitCone((float)java.lang.Math.toRadians(90.0), (float)java.lang.Math.toRadians(90.0), spring);
            joint.setSwingLimit(coneLimit);
            PxJointAngularLimitPair angularLimit = new PxJointAngularLimitPair((float)java.lang.Math.toRadians(-90.0), (float)java.lang.Math.toRadians(90.0), spring);
            joint.setTwistLimit(angularLimit);
            spring.destroy();
            coneLimit.destroy();
            angularLimit.destroy();
        }
        return joint;
    }

    @Override
    public void remove(PhysicsWorld physicsWorld) {
        super.remove(physicsWorld);
        for (PxJoint joint : this.pxJoints) {
            physicsWorld.removeJointParents(joint);
        }
        for (IRigidBody body : this.btBodies) {
            physicsWorld.getDynamicsWorld().removeActor(body.getRigidBody());
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        for (IRigidBody body : this.btBodies) {
            body.destroy();
        }
        for (PxJoint joint : this.pxJoints) {
            joint.release();
        }
        this.btBodies.clear();
        this.pxJoints.clear();
    }
}

