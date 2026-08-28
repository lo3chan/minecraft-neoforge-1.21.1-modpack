/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.Util
 *  org.joml.Matrix4d
 *  org.joml.Matrix4dc
 *  org.joml.Matrix4f
 *  org.joml.Vector2f
 *  org.joml.Vector2fc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3f
 *  org.joml.Vector4i
 */
package net.diebuddies.physics.verlet.test;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import net.diebuddies.math.Math;
import net.diebuddies.math.MatrixUtil;
import net.diebuddies.model.ColladaMesh;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.verlet.VerletLine;
import net.diebuddies.physics.verlet.VerletPoint;
import net.diebuddies.physics.verlet.VerletQuad;
import net.diebuddies.physics.verlet.VerletSimulationData;
import net.diebuddies.physics.verlet.VerletStick;
import net.diebuddies.physics.verlet.VerletTriangle;
import net.diebuddies.physics.verlet.constraints.VerletConstraint;
import net.diebuddies.physics.verlet.test.VerletTestConstraint;
import net.minecraft.Util;
import org.joml.Matrix4d;
import org.joml.Matrix4dc;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector4i;

public class VerletSimulationTest
implements Runnable {
    public static ExecutorService asynchronousWorker = Executors.newFixedThreadPool(1);
    private Vector3d gravity;
    private Vector3d windDirection;
    private float windStrength;
    private double friction;
    private VerletSimulationData data;
    private List<VerletTestConstraint> constraints;
    public boolean active = true;
    public volatile boolean destroyed = false;
    public int textureID;
    public int brightness;
    public ColladaMesh mesh;
    public boolean fetchInstantly = false;
    public boolean alwaysFetchInstantly = false;
    public Future<?> task;
    private long lastUpdate;
    private volatile double updateDelta;
    private volatile int iterations;
    public Matrix4d offsetTransform;
    public Matrix4d lastOffsetTransform;
    private static Matrix4f mojangTransform = new Matrix4f();
    private static Matrix4d tmpMove = new Matrix4d();
    private static Matrix4f identity = new Matrix4f();
    private static Vector3f shaderLight0 = new Vector3f();
    private static Vector3f shaderLight1 = new Vector3f();
    private double lastRenderPercent = 0.0;

    public VerletSimulationTest(Vector3d gravity, int iterations, double friction, Vector3d offset) {
        this.gravity = new Vector3d((Vector3dc)gravity);
        this.windDirection = new Vector3d();
        this.data = new VerletSimulationData(offset);
        this.constraints = new ObjectArrayList();
        this.lastUpdate = Util.getNanos();
        this.friction = friction;
        this.iterations = iterations;
    }

    public VerletSimulationTest(Vector3d gravity, int iterations, double friction) {
        this(gravity, iterations, friction, null);
    }

    public void update(PhysicsWorld world, double delta) {
        if (this.task != null) {
            this.finishTask();
        }
        this.downloadData();
        this.fetchInstantly = this.alwaysFetchInstantly;
        this.updateDelta = delta;
        this.iterations = this.iterations;
        this.lastUpdate = Util.getNanos();
        this.getWindForces(world);
        for (int i = 0; i < this.constraints.size(); ++i) {
            this.fetchInstantly |= this.constraints.get(i).initAsyncData(this);
        }
        this.updateOffsets();
        this.task = asynchronousWorker.submit(this);
        this.active = false;
    }

    private void getWindForces(PhysicsWorld world) {
        if (world != null) {
            this.windDirection.set(0.0, 0.0, 0.0);
            this.windStrength = 0.0f;
        }
    }

    private void fetchData() {
        if (this.task != null && this.fetchInstantly) {
            this.finishTask();
            this.downloadData();
            if (this.offsetTransform == null) {
                this.offsetTransform = new Matrix4d();
            } else {
                this.offsetTransform.identity();
            }
            if (this.lastOffsetTransform == null) {
                this.lastOffsetTransform = new Matrix4d();
            } else {
                this.lastOffsetTransform.identity();
            }
        }
    }

    private void finishTask() {
        try {
            this.task.get();
            this.task = null;
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void downloadData() {
        for (int i = 0; i < this.data.points.size(); ++i) {
            VerletPoint point = this.data.points.get(i);
            point.bufferPosition.set((Vector3dc)point.position);
            point.bufferPrevPosition.set((Vector3dc)point.prevPosition);
            point.bufferNormal.set((Vector3dc)point.normal);
        }
        if (this.data.offset == null) {
            this.data.bufferOffset = null;
        } else {
            this.data.bufferOffset.set((Vector3dc)this.data.offset);
        }
        this.data.bufferTransformation.set((Matrix4dc)this.data.transformation);
    }

    public void updateOffsets() {
        if (this.lastOffsetTransform != null) {
            this.lastOffsetTransform.set((Matrix4dc)this.offsetTransform);
        }
        this.offsetTransform = new Matrix4d((Matrix4dc)this.data.transformation);
        this.offsetTransform.mul((Matrix4dc)this.data.bufferTransformation.invert(new Matrix4d()));
        if (this.lastOffsetTransform == null) {
            this.lastOffsetTransform = new Matrix4d();
            this.lastOffsetTransform.set((Matrix4dc)this.offsetTransform);
        }
    }

    @Override
    public void run() {
        int i;
        int i2;
        for (i2 = 0; i2 < this.data.points.size(); ++i2) {
            VerletPoint p = this.data.points.get(i2);
            if (!p.locked) continue;
            p.prevPosition.set((Vector3dc)p.position);
        }
        for (i2 = 0; i2 < this.constraints.size(); ++i2) {
            this.constraints.get(i2).updateBefore(this.updateDelta, this);
        }
        double timeSquared = this.updateDelta * this.updateDelta;
        double gx = this.gravity.x * timeSquared;
        double gy = this.gravity.y * timeSquared;
        double gz = this.gravity.z * timeSquared;
        for (i = 0; i < this.data.points.size(); ++i) {
            VerletPoint p = this.data.points.get(i);
            if (p.locked) continue;
            double vx = p.position.x - p.prevPosition.x;
            double vy = p.position.y - p.prevPosition.y;
            double vz = p.position.z - p.prevPosition.z;
            p.prevPosition.set((Vector3dc)p.position);
            p.force.x = (gx + vx * this.friction) * (1.0 / (double)this.iterations);
            p.force.y = (gy + vy * this.friction) * (1.0 / (double)this.iterations);
            p.force.z = (gz + vz * this.friction) * (1.0 / (double)this.iterations);
        }
        for (i = 0; i < this.iterations; ++i) {
            int j;
            for (j = 0; j < this.data.points.size(); ++j) {
                VerletPoint p = this.data.points.get(j);
                if (p.locked) continue;
                p.position.x += p.force.x;
                p.position.y += p.force.y;
                p.position.z += p.force.z;
            }
            for (j = 0; j < this.data.sticks.size(); ++j) {
                VerletStick stick = this.data.sticks.get(j);
                if (stick.pointA.locked && stick.pointB.locked) continue;
                double stickCenterX = (stick.pointA.position.x + stick.pointB.position.x) * 0.5;
                double stickCenterY = (stick.pointA.position.y + stick.pointB.position.y) * 0.5;
                double stickCenterZ = (stick.pointA.position.z + stick.pointB.position.z) * 0.5;
                double stickDirX = stick.pointA.position.x - stick.pointB.position.x;
                double stickDirY = stick.pointA.position.y - stick.pointB.position.y;
                double stickDirZ = stick.pointA.position.z - stick.pointB.position.z;
                double length = Vector3d.length((double)stickDirX, (double)stickDirY, (double)stickDirZ);
                if (length != 0.0) {
                    double invLength = stick.halfLength / length;
                    stickDirX *= invLength;
                    stickDirY *= invLength;
                    stickDirZ *= invLength;
                } else {
                    stickDirZ = 1.0 * stick.halfLength;
                }
                if (!stick.pointA.locked) {
                    stick.pointA.position.x = stickCenterX + stickDirX;
                    stick.pointA.position.y = stickCenterY + stickDirY;
                    stick.pointA.position.z = stickCenterZ + stickDirZ;
                }
                if (stick.pointB.locked) continue;
                stick.pointB.position.x = stickCenterX - stickDirX;
                stick.pointB.position.y = stickCenterY - stickDirY;
                stick.pointB.position.z = stickCenterZ - stickDirZ;
            }
            double percent = (double)i / (double)this.iterations + 1.0 / (double)this.iterations;
            for (int z = 0; z < this.constraints.size(); ++z) {
                this.constraints.get(z).subStep(percent, this);
            }
        }
        for (i = 0; i < this.constraints.size(); ++i) {
            this.constraints.get(i).updateAfter(this.updateDelta, this);
        }
        this.calculateNormals();
    }

    public void setTransformation(Matrix4d transformation) {
        this.data.transformation.set((Matrix4dc)transformation);
    }

    public void setBufferTransformation(Matrix4d transformation) {
        this.data.bufferTransformation.set((Matrix4dc)transformation);
    }

    public void addMesh(ColladaMesh mesh, Matrix4d transformation, boolean flipUV) {
        int i;
        for (Vector3f position : mesh.positions) {
            Vector3d pointPosition = new Vector3d((double)position.x, (double)(-position.y), (double)position.z);
            if (transformation != null) {
                transformation.transformPosition(pointPosition);
            }
            VerletPoint point = new VerletPoint(pointPosition);
            this.addPoint(point);
        }
        List<VerletPoint> points = this.getPoints();
        for (int i2 = 0; i2 < mesh.indices.size(); ++i2) {
            int pindex = mesh.indices.get((int)i2).x;
            int cindex = mesh.indices.get((int)i2).w;
            int tindex = mesh.indices.get((int)i2).z;
            VerletPoint point = points.get(pindex);
            point.locked = mesh.colors.get((int)cindex).x < 0.99f;
            point.uv.set((Vector2fc)mesh.texCoords.get(tindex));
            if (!flipUV) continue;
            point.uv.y = 1.0f - point.uv.y;
        }
        int offset = 0;
        for (i = 0; i < mesh.lineIndices.size() / 2; ++i) {
            int index1 = mesh.lineIndices.get(i * 2);
            int index2 = mesh.lineIndices.get(i * 2 + 1);
            this.addStick(new VerletStick(points.get(index1), points.get(index2)));
        }
        for (i = 0; i < mesh.polyCount.length; ++i) {
            byte polyCount = mesh.polyCount[i];
            if (polyCount == 4) {
                index1 = mesh.indices.get(offset);
                index2 = mesh.indices.get(offset + 1);
                index3 = mesh.indices.get(offset + 2);
                Vector4i index4 = mesh.indices.get(offset + 3);
                VerletPoint point1 = points.get(index1.x);
                VerletPoint point2 = points.get(index2.x);
                VerletPoint point3 = points.get(index3.x);
                VerletPoint point4 = points.get(index4.x);
                this.addStick(new VerletStick(point1, point2));
                this.addStick(new VerletStick(point2, point3));
                this.addStick(new VerletStick(point3, point4));
                this.addStick(new VerletStick(point4, point1));
                this.addStick(new VerletStick(point1, point3));
                this.addStick(new VerletStick(point2, point4));
                this.addQuad(new VerletQuad(point1, point2, point3, point4));
            } else if (polyCount == 3) {
                index1 = mesh.indices.get(offset);
                index2 = mesh.indices.get(offset + 1);
                index3 = mesh.indices.get(offset + 2);
                VerletPoint point1 = points.get(index1.x);
                VerletPoint point2 = points.get(index2.x);
                VerletPoint point3 = points.get(index3.x);
                this.addStick(new VerletStick(point1, point2));
                this.addStick(new VerletStick(point2, point3));
                this.addStick(new VerletStick(point3, point1));
                this.addTriangle(new VerletTriangle(point1, point2, point3, new Vector2f((Vector2fc)mesh.texCoords.get(index1.z)), new Vector2f((Vector2fc)mesh.texCoords.get(index2.z)), new Vector2f((Vector2fc)mesh.texCoords.get(index3.z)), false));
            }
            offset += polyCount;
        }
    }

    public void calculateNormals() {
    }

    public void addPoint(VerletPoint point) {
        if (this.data.offset == null) {
            this.data.offset = new Vector3d((Vector3dc)point.position);
            this.data.bufferOffset = new Vector3d((Vector3dc)point.position);
        }
        point.position.sub((Vector3dc)this.data.offset);
        point.prevPosition.set((Vector3dc)point.position);
        point.bufferPosition.set((Vector3dc)point.position);
        point.bufferPrevPosition.set((Vector3dc)point.position);
        this.data.points.add(point);
    }

    public void addStick(VerletStick stick) {
        this.data.sticks.add(stick);
    }

    public void addQuad(VerletQuad quad) {
        this.data.quads.add(quad);
    }

    public void addTriangle(VerletTriangle triangle) {
        this.data.triangles.add(triangle);
    }

    public void addLine(VerletLine line) {
        this.data.lines.add(line);
    }

    public void addConstraint(VerletTestConstraint constraint) {
        this.constraints.add(constraint);
    }

    public void removePoint(VerletPoint point) {
        this.data.points.remove(point);
    }

    public void removeStick(VerletStick stick) {
        this.data.sticks.remove(stick);
    }

    public void removeQuad(VerletQuad quad) {
        this.data.quads.remove(quad);
    }

    public void removeTriangle(VerletTriangle triangle) {
        this.data.triangles.remove(triangle);
    }

    public void removeLine(VerletLine line) {
        this.data.lines.remove(line);
    }

    public void removeConstraint(VerletConstraint constraint) {
        this.constraints.remove(constraint);
    }

    public List<VerletStick> getSticks() {
        return this.data.sticks;
    }

    public List<VerletPoint> getPoints() {
        return this.data.points;
    }

    public List<VerletQuad> getQuads() {
        return this.data.quads;
    }

    public List<VerletTriangle> getTriangles() {
        return this.data.triangles;
    }

    public List<VerletLine> getLines() {
        return this.data.lines;
    }

    public List<VerletTestConstraint> getConstraints() {
        return this.constraints;
    }

    public Vector3d getGravity() {
        return this.gravity;
    }

    public void setGravity(Vector3d gravity) {
        this.gravity.set((Vector3dc)gravity);
    }

    public Vector3d getOffset() {
        return this.data.offset;
    }

    public void setOffset(Vector3d offset, boolean changePoints) {
        if (changePoints) {
            double dx = -offset.x + this.data.offset.x;
            double dy = -offset.y + this.data.offset.y;
            double dz = -offset.z + this.data.offset.z;
            for (VerletPoint point : this.data.points) {
                point.position.x += dx;
                point.position.y += dy;
                point.position.z += dz;
                point.prevPosition.x += dx;
                point.prevPosition.y += dy;
                point.prevPosition.z += dz;
            }
        }
        this.data.offset.set((Vector3dc)offset);
    }

    public void setOffset(Vector3d offset) {
        this.setOffset(offset, true);
    }

    public void render(PoseStack matrixStack) {
        long time = Util.getNanos();
        double diff = (double)(time - this.lastUpdate) / 1.0E9;
        double renderPercent = Math.clamp(diff / this.updateDelta, 0.0, 1.0);
        this.render(matrixStack, renderPercent);
    }

    public void render(PoseStack matrixStack, double renderPercent) {
        matrixStack.pushPose();
        this.fetchData();
        for (VerletTestConstraint constraint : this.constraints) {
            constraint.renderBefore(matrixStack, renderPercent, this);
        }
        if (this.offsetTransform != null) {
            Matrix4d moveTransform = this.offsetTransform;
            if (this.lastOffsetTransform != null) {
                moveTransform = MatrixUtil.lerp(this.lastOffsetTransform, this.offsetTransform, renderPercent, tmpMove);
            }
            mojangTransform.set((Matrix4dc)moveTransform);
            matrixStack.mulPose(mojangTransform);
        }
        RenderSystem.applyModelViewMatrix();
        for (VerletTestConstraint constraint : this.constraints) {
            constraint.render(matrixStack, renderPercent, this);
        }
        matrixStack.popPose();
        for (VerletTestConstraint constraint : this.constraints) {
            constraint.renderAfter(matrixStack, renderPercent, this);
        }
    }

    public VerletSimulationData getData() {
        return this.data;
    }
}

