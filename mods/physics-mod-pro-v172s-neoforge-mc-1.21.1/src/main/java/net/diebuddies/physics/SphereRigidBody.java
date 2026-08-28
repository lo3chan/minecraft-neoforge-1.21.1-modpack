/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4d
 *  org.joml.Quaterniond
 *  org.joml.Vector3d
 *  org.lwjgl.system.MemoryStack
 */
package net.diebuddies.physics;

import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.sound.ContactSimulationCallback;
import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.lwjgl.system.MemoryStack;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.extensions.PxRigidBodyExt;
import physx.geometry.PxSphereGeometry;
import physx.physics.PxFilterData;
import physx.physics.PxMaterial;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidBody;
import physx.physics.PxRigidDynamic;
import physx.physics.PxRigidDynamicLockFlagEnum;
import physx.physics.PxShape;
import physx.physics.PxShapeFlagEnum;
import physx.physics.PxShapeFlags;

public class SphereRigidBody
extends IRigidBody {
    public float radius;

    private SphereRigidBody() {
    }

    public static SphereRigidBody createFastSphere(PhysicsEntity entity, float radius, boolean dynamic, float staticFriction, float dynamicFriction, float restitution, float mass) {
        SphereRigidBody sphereRigidBody = new SphereRigidBody();
        Matrix4d transformation = entity.getTransformation();
        sphereRigidBody.entity = entity;
        try (MemoryStack mem = MemoryStack.stackPush();){
            PxShapeFlags shapeFlags = PxShapeFlags.createAt(mem, MemoryStack::nmalloc, (byte)PxShapeFlagEnum.eSIMULATION_SHAPE.value);
            PxVec3 tmpVec = PxVec3.createAt(mem, MemoryStack::nmalloc, (float)transformation.m30(), (float)transformation.m31(), (float)transformation.m32());
            PxQuat tmpQuat = PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0f, 0.0f, 0.0f, 1.0f);
            PxTransform tmpPose = PxTransform.createAt(mem, MemoryStack::nmalloc, tmpVec, tmpQuat);
            PxFilterData tmpFilterData = PxFilterData.createAt(mem, MemoryStack::nmalloc, 1, 1, 0, 0);
            PxSphereGeometry sphereGeometry = PxSphereGeometry.createAt(mem, MemoryStack::nmalloc, radius);
            PxMaterial material = StarterClient.physics.createMaterial(staticFriction, dynamicFriction, restitution);
            PxShape boxShape = StarterClient.physics.createShape(sphereGeometry, material, true, shapeFlags);
            PxRigidActor sphere = null;
            if (dynamic) {
                PxRigidActor dynamicActor = sphere = StarterClient.physics.createRigidDynamic(tmpPose);
                ((PxRigidDynamic)dynamicActor).setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_ANGULAR_X, true);
                ((PxRigidDynamic)dynamicActor).setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_ANGULAR_Y, true);
                ((PxRigidDynamic)dynamicActor).setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_ANGULAR_Z, true);
            } else {
                sphere = StarterClient.physics.createRigidStatic(tmpPose);
            }
            sphereRigidBody.shape = boxShape;
            sphereRigidBody.rigidBody = sphere;
            boxShape.setSimulationFilterData(tmpFilterData);
            sphere.attachShape(boxShape);
            if (dynamic) {
                PxRigidBodyExt.updateMassAndInertia((PxRigidBody)sphere, 0.001f);
                sphereRigidBody.setMass(((PxRigidBody)sphere).getMass());
            }
            material.release();
        }
        sphereRigidBody.radius = radius;
        return sphereRigidBody;
    }

    public static SphereRigidBody create(PhysicsEntity entity, float radius, boolean dynamic, float staticFriction, float dynamicFriction, float restitution) {
        SphereRigidBody sphereRigidBody = new SphereRigidBody();
        Vector3d translation = entity.getTransformation().getTranslation(new Vector3d());
        sphereRigidBody.entity = entity;
        Quaterniond rotation = new Quaterniond();
        entity.getTransformation().getNormalizedRotation(rotation);
        try (MemoryStack mem = MemoryStack.stackPush();){
            PxShapeFlags shapeFlags = PxShapeFlags.createAt(mem, MemoryStack::nmalloc, (byte)PxShapeFlagEnum.eSIMULATION_SHAPE.value);
            PxVec3 tmpVec = PxVec3.createAt(mem, MemoryStack::nmalloc, (float)translation.x, (float)translation.y, (float)translation.z);
            PxQuat tmpQuat = PxQuat.createAt(mem, MemoryStack::nmalloc, (float)rotation.x, (float)rotation.y, (float)rotation.z, (float)rotation.w);
            PxTransform tmpPose = PxTransform.createAt(mem, MemoryStack::nmalloc, tmpVec, tmpQuat);
            PxFilterData tmpFilterData = PxFilterData.createAt(mem, MemoryStack::nmalloc, 1, 1, dynamic ? ContactSimulationCallback.REPORT_CONTACT_FLAGS : 0, 0);
            PxSphereGeometry sphereGeometry = PxSphereGeometry.createAt(mem, MemoryStack::nmalloc, radius);
            PxMaterial material = StarterClient.physics.createMaterial(staticFriction, dynamicFriction, restitution);
            PxShape boxShape = StarterClient.physics.createShape(sphereGeometry, material, true, shapeFlags);
            PxRigidActor sphere = null;
            sphere = dynamic ? StarterClient.physics.createRigidDynamic(tmpPose) : StarterClient.physics.createRigidStatic(tmpPose);
            sphereRigidBody.shape = boxShape;
            sphereRigidBody.rigidBody = sphere;
            boxShape.setSimulationFilterData(tmpFilterData);
            sphere.attachShape(boxShape);
            if (dynamic) {
                PxRigidBodyExt.updateMassAndInertia((PxRigidBody)sphere, 0.1f);
                sphereRigidBody.setMass(((PxRigidBody)sphere).getMass());
                ((PxRigidDynamic)sphereRigidBody.rigidBody).setContactReportThreshold(0.25f);
            }
            material.release();
        }
        sphereRigidBody.radius = radius;
        return sphereRigidBody;
    }

    public static SphereRigidBody create(PhysicsEntity entity, float radius, boolean dynamic) {
        return SphereRigidBody.create(entity, radius, dynamic, 1.0f, 1.0f, 0.75f);
    }
}

