/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Lighting
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.VertexSorting
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.Util
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Math
 *  org.joml.Matrix4d
 *  org.joml.Matrix4dc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.joml.Matrix4fc
 *  org.joml.Vector2f
 *  org.joml.Vector2fc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4i
 */
package net.diebuddies.physics.verlet;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexSorting;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.diebuddies.math.MatrixUtil;
import net.diebuddies.model.ColladaMesh;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.snow.math.AABB3D;
import net.diebuddies.physics.verlet.Cloth;
import net.diebuddies.physics.verlet.VerletLine;
import net.diebuddies.physics.verlet.VerletPoint;
import net.diebuddies.physics.verlet.VerletQuad;
import net.diebuddies.physics.verlet.VerletSimulationData;
import net.diebuddies.physics.verlet.VerletStick;
import net.diebuddies.physics.verlet.VerletTriangle;
import net.diebuddies.physics.verlet.constraints.RenderConstraint;
import net.diebuddies.physics.verlet.constraints.VerletConstraint;
import net.diebuddies.physics.wind.WeatherDomain;
import net.diebuddies.util.PerformanceTracker;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4d;
import org.joml.Matrix4dc;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4i;

public class VerletSimulation
implements Runnable {
    public static ExecutorService asynchronousWorker = Executors.newFixedThreadPool(ConfigClient.clothThreads);
    private Vector3d gravity;
    private Vector3d windDirection;
    private float windStrength;
    private double friction;
    private VerletSimulationData data;
    private List<VerletConstraint> constraints;
    public boolean active = true;
    public volatile boolean destroyed = false;
    public int textureID;
    public int brightness;
    public Cloth cloth;
    public boolean fetchInstantly = false;
    public boolean alwaysFetchInstantly = false;
    public Future<?> task;
    private long lastUpdate;
    private volatile double updateDelta;
    private volatile int iterations;
    public Matrix4d offsetTransform;
    public Matrix4d lastOffsetTransform;
    public AABB3D aabb;
    public boolean hide;
    private static Matrix4f mojangTransform = new Matrix4f();
    private static Matrix4d tmpMove = new Matrix4d();
    private static Matrix4f localTransformation = new Matrix4f();
    private static Matrix4f identity = new Matrix4f();
    private double lastRenderPercent = 0.0;

    public VerletSimulation(Vector3d gravity, int iterations, double friction, Vector3d offset) {
        this.gravity = new Vector3d((Vector3dc)gravity);
        this.windDirection = new Vector3d();
        this.data = new VerletSimulationData(offset);
        this.constraints = new ObjectArrayList();
        this.lastUpdate = Util.getNanos();
        this.friction = friction;
        this.iterations = iterations;
        this.constraints.add(new RenderConstraint());
        this.aabb = new AABB3D(new Vector3d(), new Vector3d());
    }

    public VerletSimulation(Vector3d gravity, int iterations, double friction) {
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
            this.fetchInstantly |= this.constraints.get(i).initAsyncData(world, this);
        }
        this.updateOffsets();
        this.task = asynchronousWorker.submit(this);
        this.active = false;
    }

    private void getWindForces(PhysicsWorld world) {
        if (world != null) {
            if (ConfigClient.windPhysics) {
                WeatherDomain domain = world.getWeatherDomain();
                if (this.data.points.size() > 0 && this.getOffset() != null) {
                    VerletPoint point = this.data.points.get(0);
                    int ix = Mth.floor((double)(point.position.x + this.getOffset().x));
                    int iy = Mth.floor((double)(point.position.y + this.getOffset().y));
                    int iz = Mth.floor((double)(point.position.z + this.getOffset().z));
                    this.windDirection.set((Vector3fc)domain.getWindDirection(ix, iy, iz));
                    this.windStrength = domain.getWindStrength(ix, iy, iz);
                } else {
                    this.windDirection.set(0.0, 0.0, 0.0);
                    this.windStrength = 0.0f;
                }
            } else {
                this.windDirection.set(0.0, 0.0, 0.0);
                this.windStrength = 0.0f;
            }
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
        int i;
        for (i = 0; i < this.data.points.size(); ++i) {
            VerletPoint point = this.data.points.get(i);
            point.bufferPosition.set((Vector3dc)point.position);
            point.bufferPrevPosition.set((Vector3dc)point.prevPosition);
            point.bufferNormal.set((Vector3dc)point.normal);
        }
        for (i = 0; i < this.data.quads.size(); ++i) {
            VerletQuad quad = this.data.quads.get(i);
            quad.bufferNormal.set((Vector3dc)quad.normal);
        }
        for (i = 0; i < this.data.triangles.size(); ++i) {
            VerletTriangle triangle = this.data.triangles.get(i);
            triangle.bufferNormal.set((Vector3dc)triangle.normal);
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
            p.force.set(0.0);
            if (!p.locked) continue;
            p.prevPosition.set((Vector3dc)p.position);
        }
        for (i2 = 0; i2 < this.constraints.size(); ++i2) {
            this.constraints.get(i2).updateBefore(this.updateDelta, this);
        }
        double timeSquared = this.updateDelta * this.updateDelta;
        double gx = (this.gravity.x + this.windDirection.x * (double)this.windStrength * 42.0) * timeSquared;
        double gy = this.gravity.y * timeSquared;
        double gz = (this.gravity.z + this.windDirection.z * (double)this.windStrength * 42.0) * timeSquared;
        double fraction = 1.0 / (double)this.iterations;
        for (int i3 = 0; i3 < this.data.points.size(); ++i3) {
            VerletPoint p = this.data.points.get(i3);
            if (p.locked) continue;
            double vx = p.position.x - p.prevPosition.x;
            double vy = p.position.y - p.prevPosition.y;
            double vz = p.position.z - p.prevPosition.z;
            p.prevPosition.set((Vector3dc)p.position);
            p.force.x = p.force.x + gx + vx * this.friction;
            p.force.y = p.force.y + gy + vy * this.friction;
            p.force.z = p.force.z + gz + vz * this.friction;
            if (p.softRestriction != null) {
                double softRestrictionX = p.softRestriction.x - (p.position.x + p.force.x);
                double softRestrictionY = p.softRestriction.y - (p.position.y + p.force.y);
                double softRestrictionZ = p.softRestriction.z - (p.position.z + p.force.z);
                double restriction = p.restriction;
                p.force.x += softRestrictionX * restriction;
                p.force.y += softRestrictionY * restriction;
                p.force.z += softRestrictionZ * restriction;
            }
            p.force.x *= fraction;
            p.force.y *= fraction;
            p.force.z *= fraction;
        }
        List<VerletPoint> points = this.data.points;
        List<VerletStick> sticks = this.data.sticks;
        Vector3d start = this.aabb.start;
        Vector3d end = this.aabb.end;
        if (points.size() > 0) {
            Vector3d pos = points.get((int)0).position;
            start.set((Vector3dc)pos);
            end.set((Vector3dc)pos);
        }
        for (i = 0; i < this.iterations; ++i) {
            int j;
            double percent = (double)i / (double)this.iterations + 1.0 / (double)this.iterations;
            for (j = 0; j < this.constraints.size(); ++j) {
                this.constraints.get(j).preSubStep(percent, this);
            }
            for (j = 0; j < points.size(); ++j) {
                VerletPoint p = points.get(j);
                if (!p.locked) {
                    p.position.x += p.force.x;
                    p.position.y += p.force.y;
                    p.position.z += p.force.z;
                }
                start.min((Vector3dc)p.position);
                end.max((Vector3dc)p.position);
            }
            for (j = 0; j < sticks.size(); ++j) {
                VerletStick stick = sticks.get(j);
                VerletPoint pointA = stick.pointA;
                VerletPoint pointB = stick.pointB;
                if (pointA.locked && pointB.locked) continue;
                Vector3d positionA = pointA.position;
                Vector3d positionB = pointB.position;
                double stickCenterX = (positionA.x + positionB.x) * 0.5;
                double stickCenterY = (positionA.y + positionB.y) * 0.5;
                double stickCenterZ = (positionA.z + positionB.z) * 0.5;
                double stickDirX = positionA.x - positionB.x;
                double stickDirY = positionA.y - positionB.y;
                double stickDirZ = positionA.z - positionB.z;
                double lengthSquared = Vector3d.lengthSquared((double)stickDirX, (double)stickDirY, (double)stickDirZ);
                if (lengthSquared != 0.0) {
                    double invLength = stick.halfLength / java.lang.Math.sqrt(lengthSquared);
                    stickDirX *= invLength;
                    stickDirY *= invLength;
                    stickDirZ *= invLength;
                } else {
                    stickDirZ = 1.0 * stick.halfLength;
                }
                if (!pointA.locked) {
                    positionA.x = stickCenterX + stickDirX;
                    positionA.y = stickCenterY + stickDirY;
                    positionA.z = stickCenterZ + stickDirZ;
                }
                if (pointB.locked) continue;
                positionB.x = stickCenterX - stickDirX;
                positionB.y = stickCenterY - stickDirY;
                positionB.z = stickCenterZ - stickDirZ;
            }
            for (j = 0; j < this.constraints.size(); ++j) {
                this.constraints.get(j).subStep(percent, this);
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

    public void addCloth(Cloth cloth, int textureID, Matrix4d transformation, boolean flipUV) {
        int i;
        this.cloth = cloth;
        this.textureID = textureID;
        ColladaMesh mesh = cloth.mesh;
        for (Vector3f position : mesh.positions) {
            Vector3d pointPosition = new Vector3d((double)position.x, (double)position.y, (double)position.z);
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
            Vector3f color = mesh.colors.get(cindex);
            point.locked = color.x < 0.99f;
            point.uv.set((Vector2fc)mesh.texCoords.get(tindex));
            if (color.y < 0.99f) {
                point.setRestriction(color.y);
            }
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
                this.addQuad(new VerletQuad(point1, point2, point3, point4, new Vector2f((Vector2fc)mesh.texCoords.get(index1.z)), new Vector2f((Vector2fc)mesh.texCoords.get(index2.z)), new Vector2f((Vector2fc)mesh.texCoords.get(index3.z)), new Vector2f((Vector2fc)mesh.texCoords.get(index4.z)), flipUV));
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
                this.addTriangle(new VerletTriangle(point1, point2, point3, new Vector2f((Vector2fc)mesh.texCoords.get(index1.z)), new Vector2f((Vector2fc)mesh.texCoords.get(index2.z)), new Vector2f((Vector2fc)mesh.texCoords.get(index3.z)), flipUV));
            }
            offset += polyCount;
        }
        this.calculateNormals();
    }

    private void improveSimulationQualityBySorting() {
        VerletPoint check;
        ObjectArrayList ordered = new ObjectArrayList();
        ObjectArrayList available = new ObjectArrayList(this.data.sticks);
        ArrayDeque<VerletPoint> toCheck = new ArrayDeque<VerletPoint>();
        Iterator it = available.iterator();
        while (it.hasNext()) {
            VerletStick stick = (VerletStick)it.next();
            if (stick.pointA.locked && !stick.pointB.locked) {
                toCheck.add(stick.pointB);
                ordered.add(stick);
                it.remove();
                continue;
            }
            if (!stick.pointA.locked && stick.pointB.locked) {
                toCheck.add(stick.pointA);
                ordered.add(stick);
                it.remove();
                continue;
            }
            if (!stick.pointA.locked || !stick.pointB.locked) continue;
            ordered.add(stick);
            it.remove();
        }
        while ((check = (VerletPoint)toCheck.poll()) != null) {
            Iterator itA = available.iterator();
            while (itA.hasNext()) {
                VerletStick stick = (VerletStick)itA.next();
                if (stick.pointA == check) {
                    toCheck.add(stick.pointB);
                    ordered.add(stick);
                    itA.remove();
                    continue;
                }
                if (stick.pointB != check) continue;
                toCheck.add(stick.pointA);
                ordered.add(stick);
                itA.remove();
            }
        }
        ordered.addAll(available);
        this.data.sticks = ordered;
    }

    public void calculateNormals() {
        double invLength;
        double nz;
        double ny;
        double nx;
        double bz;
        double by;
        double bx;
        double az;
        double ay;
        double ax;
        if (ConfigClient.clothSmoothShading) {
            for (VerletPoint point : this.data.points) {
                point.normal.zero();
            }
        }
        for (VerletQuad quad : this.data.quads) {
            ax = quad.point2.position.x - quad.point1.position.x;
            ay = quad.point2.position.y - quad.point1.position.y;
            az = quad.point2.position.z - quad.point1.position.z;
            bx = quad.point3.position.x - quad.point1.position.x;
            by = quad.point3.position.y - quad.point1.position.y;
            bz = quad.point3.position.z - quad.point1.position.z;
            nx = ay * bz - az * by;
            ny = az * bx - ax * bz;
            nz = ax * by - ay * bx;
            invLength = org.joml.Math.invsqrt((double)(nx * nx + ny * ny + nz * nz));
            nx *= invLength;
            ny *= invLength;
            nz *= invLength;
            if (ConfigClient.clothSmoothShading) {
                quad.point1.normal.add(nx, ny, nz);
                quad.point2.normal.add(nx, ny, nz);
                quad.point3.normal.add(nx, ny, nz);
                quad.point4.normal.add(nx, ny, nz);
                continue;
            }
            quad.normal.set(nx, ny, nz);
        }
        for (VerletTriangle triangle : this.data.triangles) {
            ax = triangle.point2.position.x - triangle.point1.position.x;
            ay = triangle.point2.position.y - triangle.point1.position.y;
            az = triangle.point2.position.z - triangle.point1.position.z;
            bx = triangle.point3.position.x - triangle.point1.position.x;
            by = triangle.point3.position.y - triangle.point1.position.y;
            bz = triangle.point3.position.z - triangle.point1.position.z;
            nx = ay * bz - az * by;
            ny = az * bx - ax * bz;
            nz = ax * by - ay * bx;
            invLength = org.joml.Math.invsqrt((double)(nx * nx + ny * ny + nz * nz));
            nx *= invLength;
            ny *= invLength;
            nz *= invLength;
            if (ConfigClient.clothSmoothShading) {
                triangle.point1.normal.set(nx, ny, nz);
                triangle.point2.normal.set(nx, ny, nz);
                triangle.point3.normal.set(nx, ny, nz);
                continue;
            }
            triangle.normal.set(nx, ny, nz);
        }
        if (ConfigClient.clothSmoothShading) {
            for (VerletPoint point : this.data.points) {
                double lengthSquared = point.normal.lengthSquared();
                if (lengthSquared != 0.0) {
                    point.normal.div(java.lang.Math.sqrt(lengthSquared));
                    continue;
                }
                point.normal.set(0.0, 1.0, 0.0);
            }
        }
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

    public void addConstraint(VerletConstraint constraint) {
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

    public List<VerletConstraint> getConstraints() {
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

    public void render(Matrix4fStack matrixStack) {
        long time = Util.getNanos();
        double diff = (double)(time - this.lastUpdate) / 1.0E9;
        double renderPercent = Math.clamp(diff / this.updateDelta, 0.0, 1.0);
        this.render(matrixStack, renderPercent);
    }

    public void render(Matrix4fStack matrixStack, double renderPercent) {
        PhysicsMod.clothSmootShadingIrisFix = true;
        matrixStack.pushMatrix();
        this.fetchData();
        for (VerletConstraint constraint : this.constraints) {
            constraint.renderBefore(matrixStack, renderPercent, this);
        }
        if (this.offsetTransform != null) {
            Matrix4d moveTransform = this.offsetTransform;
            if (this.lastOffsetTransform != null) {
                moveTransform = MatrixUtil.lerp(this.lastOffsetTransform, this.offsetTransform, renderPercent, tmpMove);
            }
            mojangTransform.set((Matrix4dc)moveTransform);
            matrixStack.mul((Matrix4fc)mojangTransform);
        }
        RenderSystem.applyModelViewMatrix();
        if (!this.hide) {
            for (VerletConstraint constraint : this.constraints) {
                constraint.render(matrixStack, renderPercent, this);
            }
        }
        matrixStack.popMatrix();
        for (VerletConstraint constraint : this.constraints) {
            constraint.renderAfter(matrixStack, renderPercent, this);
        }
        PhysicsMod.clothSmootShadingIrisFix = false;
    }

    public <T extends VerletConstraint> T getConstraint(Class<T> clzz) {
        for (VerletConstraint constraint : this.constraints) {
            if (clzz != constraint.getClass()) continue;
            return (T)constraint;
        }
        return null;
    }

    public VerletSimulationData getData() {
        return this.data;
    }

    public void setFriction(double friction) {
        this.friction = friction;
    }

    public double getFriction() {
        return this.friction;
    }

    public void renderSlow(Level level) {
        if (this.destroyed) {
            return;
        }
        PerformanceTracker.startNoFlush("cloth_rendering");
        Vec3 view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        ShaderInstance shaderBefore = RenderSystem.getShader();
        Matrix4f projectionBefore = RenderSystem.getProjectionMatrix();
        Matrix4f modelViewBefore = RenderSystem.getModelViewMatrix();
        RenderSystem.setProjectionMatrix((Matrix4f)PhysicsMod.projectionMatrix, (VertexSorting)RenderSystem.getVertexSorting());
        Matrix4fStack matrixStackIn = RenderSystem.getModelViewStack();
        RenderSystem.setShader(GameRenderer::getRendertypeArmorCutoutNoCullShader);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        matrixStackIn.pushMatrix();
        matrixStackIn.set((Matrix4fc)PhysicsMod.viewMatrix);
        Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
        RenderSystem.activeTexture((int)33984);
        RenderSystem.disableCull();
        identity.identity();
        PhysicsMod.storeShaderLightDirections();
        if (Minecraft.getInstance().player != null && (level instanceof ClientLevel ? ((ClientLevel)level).effects().constantAmbientLight() : Minecraft.getInstance().player.clientLevel.effects().constantAmbientLight())) {
            Lighting.setupNetherLevel();
        } else {
            Lighting.setupLevel();
        }
        int glID = this.textureID;
        int oldID = RenderSystem.getShaderTexture((int)0);
        RenderSystem.activeTexture((int)33984);
        RenderSystem.setShaderTexture((int)0, (int)glID);
        RenderSystem.bindTexture((int)glID);
        Vector3d offset = this.getOffset();
        localTransformation.identity().translate((float)(-view.x + offset.x), (float)(-view.y + offset.y), (float)(-view.z + offset.z));
        matrixStackIn.mul((Matrix4fc)localTransformation);
        double renderPercent = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        if (Minecraft.getInstance().isPaused()) {
            renderPercent = this.lastRenderPercent;
        } else {
            this.lastRenderPercent = renderPercent;
        }
        this.render(matrixStackIn, renderPercent);
        matrixStackIn.popMatrix();
        RenderSystem.setShader(() -> shaderBefore);
        RenderSystem.setProjectionMatrix((Matrix4f)projectionBefore, (VertexSorting)RenderSystem.getVertexSorting());
        RenderSystem.getModelViewMatrix().set((Matrix4fc)modelViewBefore);
        RenderSystem.activeTexture((int)33984);
        RenderSystem.enableCull();
        RenderSystem.setShaderTexture((int)0, (int)oldID);
        RenderSystem.bindTexture((int)oldID);
        PhysicsMod.restoreShaderLightDirections();
        PerformanceTracker.end("cloth_rendering");
    }
}

