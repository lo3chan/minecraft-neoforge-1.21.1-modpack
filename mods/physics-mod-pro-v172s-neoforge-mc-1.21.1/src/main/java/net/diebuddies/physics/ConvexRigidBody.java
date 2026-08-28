/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Quaterniond
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3f
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
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import physx.PxTopLevelFunctions;
import physx.common.PxBoundedData;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.cooking.PxConvexFlagEnum;
import physx.cooking.PxConvexFlags;
import physx.cooking.PxConvexMeshDesc;
import physx.extensions.PxRigidActorExt;
import physx.extensions.PxRigidBodyExt;
import physx.geometry.PxConvexMesh;
import physx.geometry.PxConvexMeshGeometry;
import physx.geometry.PxMeshScale;
import physx.physics.PxFilterData;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidBody;
import physx.physics.PxRigidDynamic;
import physx.physics.PxShapeFlagEnum;
import physx.physics.PxShapeFlags;
import physx.support.Vector_PxVec3;

public class ConvexRigidBody
extends IRigidBody {
    private PxConvexMesh convexMesh;

    private ConvexRigidBody() {
    }

    public static ConvexRigidBody create(PhysicsEntity entity, PxRigidActor baseActor, boolean dynamic) {
        ConvexRigidBody convexRigidBody = new ConvexRigidBody();
        Vector3d scale = entity.getTransformation().getScale(new Vector3d());
        Vector3d translation = entity.getTransformation().getTranslation(new Vector3d());
        convexRigidBody.entity = entity;
        entity.scalePhysics.set((Vector3dc)scale);
        entity.getTransformation().normalize3x3();
        entity.getOldTransformation().normalize3x3();
        Quaterniond rotation = new Quaterniond();
        entity.getTransformation().getNormalizedRotation(rotation);
        try (MemoryStack mem = StarterClient.memoryStack.push();){
            PxShapeFlags shapeFlags = PxShapeFlags.createAt(mem, MemoryStack::nmalloc, (byte)PxShapeFlagEnum.eSIMULATION_SHAPE.value);
            PxVec3 tmpVec = PxVec3.createAt(mem, MemoryStack::nmalloc, (float)translation.x, (float)translation.y, (float)translation.z);
            PxQuat tmpQuat = PxQuat.createAt(mem, MemoryStack::nmalloc, (float)rotation.x, (float)rotation.y, (float)rotation.z, (float)rotation.w);
            PxTransform tmpPose = PxTransform.createAt(mem, MemoryStack::nmalloc, tmpVec, tmpQuat);
            PxFilterData tmpFilterData = null;
            tmpFilterData = PxFilterData.createAt(mem, MemoryStack::nmalloc, entity.physicsGroup, entity.physicsMask, dynamic ? ContactSimulationCallback.REPORT_CONTACT_FLAGS : 0, 0);
            Vector_PxVec3 pointVector = new Vector_PxVec3();
            int numPoints = 0;
            double scaleX = entity.scale * entity.enlargeHitbox.x;
            double scaleY = entity.scale * entity.enlargeHitbox.y;
            double scaleZ = entity.scale * entity.enlargeHitbox.z;
            List<Model> models = entity.models;
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
                    pointVector.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, (float)((double)position.x * scaleX), (float)((double)position.y * scaleY), (float)((double)position.z * scaleZ)));
                    ++numPoints;
                }
            }
            if (numPoints < 4) {
                pointVector.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, 0.5f, 0.5f, 0.5f));
                pointVector.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, 0.5f, 0.5f, -0.5f));
                pointVector.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, 0.5f, -0.5f, 0.5f));
                pointVector.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, 0.5f, -0.5f, -0.5f));
                pointVector.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, -0.5f, 0.5f, 0.5f));
                pointVector.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, -0.5f, 0.5f, -0.5f));
                pointVector.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, -0.5f, -0.5f, 0.5f));
                pointVector.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, -0.5f, -0.5f, -0.5f));
            }
            PxBoundedData points = PxBoundedData.createAt(mem, MemoryStack::nmalloc);
            points.setCount(pointVector.size());
            points.setStride(PxVec3.SIZEOF);
            points.setData(pointVector.data());
            PxConvexMeshDesc convexMeshDesc = PxConvexMeshDesc.createAt(mem, MemoryStack::nmalloc);
            convexMeshDesc.setPoints(points);
            convexMeshDesc.setFlags(PxConvexFlags.createAt(mem, MemoryStack::nmalloc, (short)(PxConvexFlagEnum.eCOMPUTE_CONVEX.value | PxConvexFlagEnum.eDISABLE_MESH_VALIDATION.value | PxConvexFlagEnum.eFAST_INERTIA_COMPUTATION.value)));
            PxConvexMesh convexMesh = PxTopLevelFunctions.CreateConvexMesh(StarterClient.cookingParams, convexMeshDesc);
            if (convexMesh == null) {
                Vector_PxVec3 pointVectorDefault = new Vector_PxVec3();
                pointVectorDefault.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, 0.5f, 0.5f, 0.5f));
                pointVectorDefault.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, 0.5f, 0.5f, -0.5f));
                pointVectorDefault.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, 0.5f, -0.5f, 0.5f));
                pointVectorDefault.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, 0.5f, -0.5f, -0.5f));
                pointVectorDefault.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, -0.5f, 0.5f, 0.5f));
                pointVectorDefault.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, -0.5f, 0.5f, -0.5f));
                pointVectorDefault.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, -0.5f, -0.5f, 0.5f));
                pointVectorDefault.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, -0.5f, -0.5f, -0.5f));
                points = PxBoundedData.createAt(mem, MemoryStack::nmalloc);
                points.setCount(pointVectorDefault.size());
                points.setStride(PxVec3.SIZEOF);
                points.setData(pointVectorDefault.data());
                convexMeshDesc = PxConvexMeshDesc.createAt(mem, MemoryStack::nmalloc);
                convexMeshDesc.setPoints(points);
                convexMeshDesc.setFlags(PxConvexFlags.createAt(mem, MemoryStack::nmalloc, (short)PxConvexFlagEnum.eCOMPUTE_CONVEX.value));
                convexMesh = PxTopLevelFunctions.CreateConvexMesh(StarterClient.cookingParams, convexMeshDesc);
                pointVectorDefault.destroy();
            }
            PxMeshScale meshScale = PxMeshScale.createAt(mem, MemoryStack::nmalloc, PxVec3.createAt(mem, MemoryStack::nmalloc, (float)scale.x, (float)scale.y, (float)scale.z), PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0f, 0.0f, 0.0f, 1.0f));
            PxConvexMeshGeometry geometry = PxConvexMeshGeometry.createAt(mem, MemoryStack::nmalloc, convexMesh, meshScale);
            if (baseActor == null) {
                shape = StarterClient.physics.createShape(geometry, StarterClient.defaultMaterial, true, shapeFlags);
                PxRigidActor actor = null;
                actor = dynamic ? StarterClient.physics.createRigidDynamic(tmpPose) : StarterClient.physics.createRigidStatic(tmpPose);
                convexRigidBody.shape = shape;
                convexRigidBody.rigidBody = actor;
                actor.attachShape(shape);
            } else {
                convexRigidBody.shape = shape = PxRigidActorExt.createExclusiveShape(baseActor, geometry, StarterClient.defaultMaterial, shapeFlags);
                convexRigidBody.rigidBody = baseActor;
            }
            convexRigidBody.shape.setSimulationFilterData(tmpFilterData);
            convexRigidBody.convexMesh = convexMesh;
            if (convexRigidBody.rigidBody instanceof PxRigidBody) {
                ((PxRigidBody)convexRigidBody.rigidBody).setMaxDepenetrationVelocity(2.5f);
            }
            if (convexRigidBody.rigidBody instanceof PxRigidDynamic) {
                ((PxRigidDynamic)convexRigidBody.rigidBody).setContactReportThreshold(0.25f);
            }
            if (dynamic) {
                PxRigidBodyExt.updateMassAndInertia((PxRigidBody)convexRigidBody.rigidBody, 0.1f);
                convexRigidBody.setMass(((PxRigidBody)convexRigidBody.rigidBody).getMass());
            }
            pointVector.destroy();
        }
        return convexRigidBody;
    }

    public static ConvexRigidBody create(PhysicsEntity entity, boolean dynamic) {
        return ConvexRigidBody.create(entity, null, dynamic);
    }

    @Override
    public void destroy() {
        boolean d = this.destroyed;
        super.destroy();
        if (!d) {
            this.convexMesh.release();
        }
    }
}

