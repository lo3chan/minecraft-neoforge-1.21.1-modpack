/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  org.lwjgl.system.MemoryStack
 */
package net.diebuddies.physics;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.diebuddies.physics.StarterClient;
import org.lwjgl.system.MemoryStack;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.geometry.PxBoxGeometry;
import physx.physics.PxFilterData;
import physx.physics.PxRigidActor;
import physx.physics.PxShape;
import physx.physics.PxShapeFlagEnum;
import physx.physics.PxShapeFlags;

public class ChunkRigidBody {
    private PxRigidActor chunk;
    private List<PxShape> blocks = new ObjectArrayList();
    private PxShapeFlags shapeFlags;
    private PxFilterData filterData;
    private boolean destroyed;

    public ChunkRigidBody(double x, double y, double z) {
        this.shapeFlags = new PxShapeFlags((byte)PxShapeFlagEnum.eSIMULATION_SHAPE.value);
        this.filterData = new PxFilterData(1, 23, 0, 0);
        try (MemoryStack mem = MemoryStack.stackPush();){
            PxVec3 tmpVec = PxVec3.createAt(mem, MemoryStack::nmalloc, (float)x, (float)y, (float)z);
            PxQuat tmpQuat = PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0f, 0.0f, 0.0f, 1.0f);
            PxTransform tmpPose = PxTransform.createAt(mem, MemoryStack::nmalloc, tmpVec, tmpQuat);
            this.chunk = StarterClient.physics.createRigidStatic(tmpPose);
        }
    }

    public void attachBox(float x, float y, float z, float width, float height, float depth) {
        try (MemoryStack mem = MemoryStack.stackPush();){
            PxBoxGeometry boxGeometry = PxBoxGeometry.createAt(mem, MemoryStack::nmalloc, width * 0.5f, height * 0.5f, depth * 0.5f);
            PxShape boxShape = StarterClient.physics.createShape(boxGeometry, StarterClient.defaultMaterial, true, this.shapeFlags);
            boxShape.setLocalPose(PxTransform.createAt(mem, MemoryStack::nmalloc, PxVec3.createAt(mem, MemoryStack::nmalloc, x, y, z), PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0f, 0.0f, 0.0f, 1.0f)));
            boxShape.setSimulationFilterData(this.filterData);
            this.chunk.attachShape(boxShape);
            this.blocks.add(boxShape);
        }
    }

    public PxRigidActor getActor() {
        return this.chunk;
    }

    public void destroy() {
        if (!this.destroyed) {
            for (PxShape shape : this.blocks) {
                shape.release();
            }
            this.chunk.release();
            this.shapeFlags.destroy();
            this.filterData.destroy();
            this.destroyed = true;
        }
    }
}

