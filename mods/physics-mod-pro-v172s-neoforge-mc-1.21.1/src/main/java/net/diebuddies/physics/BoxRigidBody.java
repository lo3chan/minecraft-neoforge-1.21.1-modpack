/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Quaterniond
 *  org.joml.Vector3d
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.lwjgl.system.MemoryStack
 */
package net.diebuddies.physics;

import java.util.List;
import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.Mesh;
import net.diebuddies.physics.Model;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.sound.ContactSimulationCallback;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.system.MemoryStack;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.extensions.PxRigidBodyExt;
import physx.geometry.PxBoxGeometry;
import physx.geometry.PxCapsuleGeometry;
import physx.physics.PxFilterData;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidBody;
import physx.physics.PxRigidDynamic;
import physx.physics.PxShape;
import physx.physics.PxShapeFlagEnum;
import physx.physics.PxShapeFlags;

public class BoxRigidBody
extends IRigidBody {
    public float width;
    public float height;
    public float depth;

    private BoxRigidBody() {
    }

    public static BoxRigidBody create(PhysicsEntity entity, float width, float height, float depth, float offsetx, float offsety, float offsetz, boolean dynamic) {
        BoxRigidBody boxRigidBody = new BoxRigidBody();
        Vector3d scale = entity.getTransformation().getScale(new Vector3d());
        Vector3d translation = entity.getTransformation().getTranslation(new Vector3d());
        boxRigidBody.entity = entity;
        Quaterniond rotation = new Quaterniond();
        entity.getTransformation().getNormalizedRotation(rotation);
        try (MemoryStack mem = MemoryStack.stackPush();){
            PxShapeFlags shapeFlags = PxShapeFlags.createAt(mem, MemoryStack::nmalloc, (byte)PxShapeFlagEnum.eSIMULATION_SHAPE.value);
            PxVec3 tmpVec = PxVec3.createAt(mem, MemoryStack::nmalloc, (float)translation.x, (float)translation.y, (float)translation.z);
            PxQuat tmpQuat = PxQuat.createAt(mem, MemoryStack::nmalloc, (float)rotation.x, (float)rotation.y, (float)rotation.z, (float)rotation.w);
            PxTransform tmpPose = PxTransform.createAt(mem, MemoryStack::nmalloc, tmpVec, tmpQuat);
            PxFilterData tmpFilterData = null;
            tmpFilterData = PxFilterData.createAt(mem, MemoryStack::nmalloc, entity.physicsGroup, entity.physicsMask, dynamic ? ContactSimulationCallback.REPORT_CONTACT_FLAGS : 0, 0);
            PxBoxGeometry boxGeometry = PxBoxGeometry.createAt(mem, MemoryStack::nmalloc, width * 0.5f * (float)Math.abs(scale.x) * entity.enlargeHitbox.x * entity.scale, height * 0.5f * (float)Math.abs(scale.y) * entity.enlargeHitbox.y * entity.scale, depth * 0.5f * (float)Math.abs(scale.z) * entity.enlargeHitbox.z * entity.scale);
            PxShape boxShape = StarterClient.physics.createShape(boxGeometry, StarterClient.defaultMaterial, true, shapeFlags);
            if (offsetx != 0.0f || offsety != 0.0f || offsetz != 0.0f) {
                boxShape.setLocalPose(PxTransform.createAt(mem, MemoryStack::nmalloc, PxVec3.createAt(mem, MemoryStack::nmalloc, offsetx, offsety, offsetz), PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0f, 0.0f, 0.0f, 1.0f)));
            }
            PxRigidActor box = null;
            if (dynamic) {
                box = StarterClient.physics.createRigidDynamic(tmpPose);
                boxRigidBody.setMass(((PxRigidBody)box).getMass());
            } else {
                box = StarterClient.physics.createRigidStatic(tmpPose);
            }
            boxRigidBody.shape = boxShape;
            boxRigidBody.rigidBody = box;
            boxShape.setSimulationFilterData(tmpFilterData);
            box.attachShape(boxShape);
            if (dynamic) {
                PxRigidBodyExt.updateMassAndInertia((PxRigidBody)box, 0.1f);
                boxRigidBody.setMass(((PxRigidBody)box).getMass());
                ((PxRigidDynamic)boxRigidBody.rigidBody).setContactReportThreshold(0.25f);
                PxRigidDynamic d = (PxRigidDynamic)boxRigidBody.rigidBody;
                d.setContactReportThreshold(0.25f);
                ((PxRigidBody)boxRigidBody.rigidBody).setMaxDepenetrationVelocity(2.5f);
            }
        }
        boxRigidBody.width = width;
        boxRigidBody.height = height;
        boxRigidBody.depth = depth;
        return boxRigidBody;
    }

    public static BoxRigidBody create(PhysicsEntity entity, boolean dynamic) {
        BoxRigidBody boxRigidBody = new BoxRigidBody();
        Vector3d scale = entity.getTransformation().getScale(new Vector3d());
        Vector3d translation = entity.getTransformation().getTranslation(new Vector3d());
        boxRigidBody.entity = entity;
        Quaterniond rotation = new Quaterniond();
        entity.getTransformation().getNormalizedRotation(rotation);
        try (MemoryStack mem = MemoryStack.stackPush();){
            PxShapeFlags shapeFlags = PxShapeFlags.createAt(mem, MemoryStack::nmalloc, (byte)PxShapeFlagEnum.eSIMULATION_SHAPE.value);
            PxVec3 tmpVec = PxVec3.createAt(mem, MemoryStack::nmalloc, (float)translation.x, (float)translation.y, (float)translation.z);
            PxQuat tmpQuat = PxQuat.createAt(mem, MemoryStack::nmalloc, (float)rotation.x, (float)rotation.y, (float)rotation.z, (float)rotation.w);
            PxTransform tmpPose = PxTransform.createAt(mem, MemoryStack::nmalloc, tmpVec, tmpQuat);
            PxFilterData tmpFilterData = null;
            tmpFilterData = PxFilterData.createAt(mem, MemoryStack::nmalloc, entity.physicsGroup, entity.physicsMask, dynamic ? ContactSimulationCallback.REPORT_CONTACT_FLAGS : 0, 0);
            int numPoints = 0;
            float scaleX = entity.scale * entity.enlargeHitbox.x;
            float scaleY = entity.scale * entity.enlargeHitbox.y;
            float scaleZ = entity.scale * entity.enlargeHitbox.z;
            List<Model> models = entity.models;
            Vector3f min = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
            Vector3f max = new Vector3f(-3.4028235E38f, -3.4028235E38f, -3.4028235E38f);
            Vector3f tmp = new Vector3f();
            for (int i = 0; i < models.size(); ++i) {
                Model model = models.get(i);
                if (model.onlyVisual) continue;
                Mesh mesh = model.mesh;
                if (model.physicsMesh != null) {
                    mesh = model.physicsMesh;
                }
                List<Vector3f> positions = mesh.positions;
                for (int j = 0; j < positions.size(); ++j) {
                    Vector3f position = positions.get(j);
                    tmp.set((Vector3fc)position).mul(scaleX, scaleY, scaleZ);
                    min.min((Vector3fc)tmp);
                    max.max((Vector3fc)tmp);
                    ++numPoints;
                }
            }
            if (numPoints <= 1 || min.x == max.x || min.y == max.y || min.z == max.z) {
                min.set(-0.5f);
                max.set(0.5f);
            }
            float width = max.x - min.x;
            float height = max.y - min.y;
            float depth = max.z - min.z;
            PxBoxGeometry boxGeometry = PxBoxGeometry.createAt(mem, MemoryStack::nmalloc, width * 0.5f * (float)Math.abs(scale.x) * entity.enlargeHitbox.x * entity.scale, height * 0.5f * (float)Math.abs(scale.y) * entity.enlargeHitbox.y * entity.scale, depth * 0.5f * (float)Math.abs(scale.z) * entity.enlargeHitbox.z * entity.scale);
            PxShape boxShape = StarterClient.physics.createShape(boxGeometry, StarterClient.defaultMaterial, true, shapeFlags);
            PxRigidActor box = null;
            if (dynamic) {
                box = StarterClient.physics.createRigidDynamic(tmpPose);
                boxRigidBody.setMass(((PxRigidBody)box).getMass());
            } else {
                box = StarterClient.physics.createRigidStatic(tmpPose);
            }
            boxRigidBody.shape = boxShape;
            boxRigidBody.rigidBody = box;
            boxShape.setSimulationFilterData(tmpFilterData);
            box.attachShape(boxShape);
            if (dynamic) {
                PxRigidBodyExt.updateMassAndInertia((PxRigidBody)box, 0.1f);
                boxRigidBody.setMass(((PxRigidBody)box).getMass());
                ((PxRigidDynamic)boxRigidBody.rigidBody).setContactReportThreshold(0.25f);
                PxRigidDynamic d = (PxRigidDynamic)boxRigidBody.rigidBody;
                d.setContactReportThreshold(0.25f);
                ((PxRigidBody)boxRigidBody.rigidBody).setMaxDepenetrationVelocity(2.5f);
            }
            boxRigidBody.width = width;
            boxRigidBody.height = height;
            boxRigidBody.depth = depth;
        }
        return boxRigidBody;
    }

    public static BoxRigidBody createFromConvexWithOffset(PhysicsEntity entity, boolean dynamic) {
        BoxRigidBody boxRigidBody = new BoxRigidBody();
        Vector3d scale = entity.getTransformation().getScale(new Vector3d());
        Vector3d translation = entity.getTransformation().getTranslation(new Vector3d());
        boxRigidBody.entity = entity;
        Quaterniond rotation = new Quaterniond();
        entity.getTransformation().getNormalizedRotation(rotation);
        try (MemoryStack mem = MemoryStack.stackPush();){
            PxShapeFlags shapeFlags = PxShapeFlags.createAt(mem, MemoryStack::nmalloc, (byte)PxShapeFlagEnum.eSIMULATION_SHAPE.value);
            PxVec3 tmpVec = PxVec3.createAt(mem, MemoryStack::nmalloc, (float)translation.x, (float)translation.y, (float)translation.z);
            PxQuat tmpQuat = PxQuat.createAt(mem, MemoryStack::nmalloc, (float)rotation.x, (float)rotation.y, (float)rotation.z, (float)rotation.w);
            PxTransform tmpPose = PxTransform.createAt(mem, MemoryStack::nmalloc, tmpVec, tmpQuat);
            PxFilterData tmpFilterData = null;
            tmpFilterData = PxFilterData.createAt(mem, MemoryStack::nmalloc, entity.physicsGroup, entity.physicsMask, dynamic ? ContactSimulationCallback.REPORT_CONTACT_FLAGS : 0, 0);
            int numPoints = 0;
            float scaleX = entity.scale * entity.enlargeHitbox.x;
            float scaleY = entity.scale * entity.enlargeHitbox.y;
            float scaleZ = entity.scale * entity.enlargeHitbox.z;
            List<Model> models = entity.models;
            Vector3f min = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
            Vector3f max = new Vector3f(-3.4028235E38f, -3.4028235E38f, -3.4028235E38f);
            Vector3f tmp = new Vector3f();
            for (int i = 0; i < models.size(); ++i) {
                Model model = models.get(i);
                if (model.onlyVisual) continue;
                Mesh mesh = model.mesh;
                if (model.physicsMesh != null) {
                    mesh = model.physicsMesh;
                }
                List<Vector3f> positions = mesh.positions;
                for (int j = 0; j < positions.size(); ++j) {
                    Vector3f position = positions.get(j);
                    tmp.set((Vector3fc)position).mul(scaleX, scaleY, scaleZ);
                    min.min((Vector3fc)tmp);
                    max.max((Vector3fc)tmp);
                    ++numPoints;
                }
            }
            if (numPoints <= 0) {
                min.set(-0.5f);
                max.set(0.5f);
            }
            float threshold = 0.02f;
            if (Math.abs(max.x - min.x) < threshold) {
                min.x -= threshold;
                max.x += threshold;
            }
            if (Math.abs(max.y - min.y) < threshold) {
                min.y -= threshold;
                max.y += threshold;
            }
            if (Math.abs(max.z - min.z) < threshold) {
                min.z -= threshold;
                max.z += threshold;
            }
            float width = max.x - min.x;
            float height = max.y - min.y;
            float depth = max.z - min.z;
            float offsetx = (max.x + min.x) * 0.5f;
            float offsety = (max.y + min.y) * 0.5f;
            float offsetz = (max.z + min.z) * 0.5f;
            PxBoxGeometry boxGeometry = PxBoxGeometry.createAt(mem, MemoryStack::nmalloc, width * 0.5f * (float)Math.abs(scale.x) * entity.enlargeHitbox.x * entity.scale, height * 0.5f * (float)Math.abs(scale.y) * entity.enlargeHitbox.y * entity.scale, depth * 0.5f * (float)Math.abs(scale.z) * entity.enlargeHitbox.z * entity.scale);
            PxShape boxShape = StarterClient.physics.createShape(boxGeometry, StarterClient.defaultMaterial, true, shapeFlags);
            boxShape.setLocalPose(PxTransform.createAt(mem, MemoryStack::nmalloc, PxVec3.createAt(mem, MemoryStack::nmalloc, offsetx, offsety, offsetz), PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0f, 0.0f, 0.0f, 1.0f)));
            PxRigidActor box = null;
            if (dynamic) {
                box = StarterClient.physics.createRigidDynamic(tmpPose);
                boxRigidBody.setMass(((PxRigidBody)box).getMass());
            } else {
                box = StarterClient.physics.createRigidStatic(tmpPose);
            }
            boxRigidBody.shape = boxShape;
            boxRigidBody.rigidBody = box;
            boxShape.setSimulationFilterData(tmpFilterData);
            box.attachShape(boxShape);
            if (dynamic) {
                PxRigidBodyExt.updateMassAndInertia((PxRigidBody)box, 0.1f);
                boxRigidBody.setMass(((PxRigidBody)box).getMass());
                ((PxRigidDynamic)boxRigidBody.rigidBody).setContactReportThreshold(0.25f);
                PxRigidDynamic d = (PxRigidDynamic)boxRigidBody.rigidBody;
                d.setContactReportThreshold(0.25f);
                ((PxRigidBody)boxRigidBody.rigidBody).setMaxDepenetrationVelocity(2.5f);
            }
            boxRigidBody.width = width;
            boxRigidBody.height = height;
            boxRigidBody.depth = depth;
        }
        return boxRigidBody;
    }

    public static BoxRigidBody createPlayer(PhysicsEntity entity, float width, float height, float depth, boolean dynamic) {
        BoxRigidBody boxRigidBody = new BoxRigidBody();
        Vector3d scale = entity.getTransformation().getScale(new Vector3d());
        Vector3d translation = entity.getTransformation().getTranslation(new Vector3d());
        boxRigidBody.entity = entity;
        Quaterniond rotation = new Quaterniond();
        entity.getTransformation().getNormalizedRotation(rotation);
        try (MemoryStack mem = MemoryStack.stackPush();){
            PxShapeFlags shapeFlags = PxShapeFlags.createAt(mem, MemoryStack::nmalloc, (byte)PxShapeFlagEnum.eSIMULATION_SHAPE.value);
            PxVec3 tmpVec = PxVec3.createAt(mem, MemoryStack::nmalloc, (float)translation.x, (float)translation.y, (float)translation.z);
            PxQuat tmpQuat = PxQuat.createAt(mem, MemoryStack::nmalloc, (float)rotation.x, (float)rotation.y, (float)rotation.z, (float)rotation.w);
            PxTransform tmpPose = PxTransform.createAt(mem, MemoryStack::nmalloc, tmpVec, tmpQuat);
            PxFilterData tmpFilterData = null;
            tmpFilterData = PxFilterData.createAt(mem, MemoryStack::nmalloc, entity.physicsGroup, entity.physicsMask, dynamic ? ContactSimulationCallback.REPORT_CONTACT_FLAGS : 0, 0);
            float radius = depth * 0.5f * (float)Math.abs(scale.x) * entity.enlargeHitbox.x * entity.scale;
            float halfHeight = height * 0.5f * (float)Math.abs(scale.y) * entity.enlargeHitbox.y * entity.scale;
            PxCapsuleGeometry capsuleGeometry = PxCapsuleGeometry.createAt(mem, MemoryStack::nmalloc, radius, halfHeight);
            PxShape capsuleShape = StarterClient.physics.createShape(capsuleGeometry, StarterClient.defaultMaterial, true, shapeFlags);
            float a = 0.7853982f;
            float s = (float)Math.sin(a);
            capsuleShape.setLocalPose(PxTransform.createAt(mem, MemoryStack::nmalloc, PxVec3.createAt(mem, MemoryStack::nmalloc, 0.0f, 0.0f, 0.0f), PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0f, 0.0f, s, (float)Math.cos(a))));
            PxRigidActor actor = null;
            if (dynamic) {
                actor = StarterClient.physics.createRigidDynamic(tmpPose);
                boxRigidBody.setMass(((PxRigidBody)actor).getMass());
            } else {
                actor = StarterClient.physics.createRigidStatic(tmpPose);
            }
            boxRigidBody.shape = capsuleShape;
            boxRigidBody.rigidBody = actor;
            capsuleShape.setSimulationFilterData(tmpFilterData);
            actor.attachShape(capsuleShape);
            if (dynamic) {
                PxRigidBodyExt.updateMassAndInertia((PxRigidBody)actor, 0.1f);
                boxRigidBody.setMass(((PxRigidBody)actor).getMass());
                ((PxRigidDynamic)boxRigidBody.rigidBody).setContactReportThreshold(0.25f);
                ((PxRigidBody)boxRigidBody.rigidBody).setMaxDepenetrationVelocity(2.5f);
            }
        }
        boxRigidBody.width = width;
        boxRigidBody.height = height;
        boxRigidBody.depth = depth;
        return boxRigidBody;
    }
}

