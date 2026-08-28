/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.MeshData
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  org.joml.Math
 *  org.joml.Matrix4fStack
 *  org.joml.Vector2d
 *  org.joml.Vector2f
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector4f
 */
package net.diebuddies.physics.verlet.constraints;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.diebuddies.compat.Iris;
import net.diebuddies.compat.Optifine;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.SimplePoolVector3d;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.verlet.VerletLine;
import net.diebuddies.physics.verlet.VerletPoint;
import net.diebuddies.physics.verlet.VerletQuad;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.diebuddies.physics.verlet.VerletTriangle;
import net.diebuddies.physics.verlet.constraints.VerletConstraint;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Math;
import org.joml.Matrix4fStack;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector4f;

public class RenderConstraint
implements VerletConstraint {
    private static final Vector3d UP_VECTOR = new Vector3d(0.0, 1.0, 0.0);
    private static Vector3d tmp1 = new Vector3d();
    private static Vector3d tmp2 = new Vector3d();
    private static Vector3d tmp3 = new Vector3d();
    private static SimplePoolVector3d vectorPool;
    private static ObjectArrayList<Vector3d> vertices;
    private static IntArrayList lineData;
    private static IntArrayList indices;

    @Override
    public boolean initAsyncData(PhysicsWorld world, VerletSimulation simulation) {
        return false;
    }

    @Override
    public void updateBefore(double delta, VerletSimulation simulation) {
    }

    @Override
    public void subStep(double percent, VerletSimulation simulation) {
    }

    @Override
    public void updateAfter(double delta, VerletSimulation simulation) {
    }

    @Override
    public void renderBefore(Matrix4fStack matrixStack, double delta, VerletSimulation simulation) {
    }

    @Override
    public void render(Matrix4fStack matrixStack, double renderPercent, VerletSimulation simulation) {
        int brightness = simulation.brightness;
        List<VerletQuad> quads = simulation.getQuads();
        List<VerletTriangle> triangles = simulation.getTriangles();
        List<VerletLine> lines = simulation.getLines();
        List<VerletPoint> points = simulation.getPoints();
        for (int i = 0; i < points.size(); ++i) {
            points.get(i).updateRenderPosition(renderPercent);
        }
        if (simulation.getQuads().size() > 0 || simulation.getTriangles().size() > 0) {
            if (StarterClient.iris && Iris.isExtending() || StarterClient.optifabric && Optifine.isUsingShadersNoInternal()) {
                int i;
                BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.NEW_ENTITY);
                for (i = 0; i < quads.size(); ++i) {
                    VerletQuad quad = quads.get(i);
                    if (ConfigClient.clothSmoothShading) {
                        this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, quad.point1.renderPosition, quad.point1.uv, quad.point1.bufferNormal, brightness, quad.point1.rgba);
                        this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, quad.point2.renderPosition, quad.point2.uv, quad.point2.bufferNormal, brightness, quad.point2.rgba);
                        this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, quad.point3.renderPosition, quad.point3.uv, quad.point3.bufferNormal, brightness, quad.point3.rgba);
                        this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, quad.point4.renderPosition, quad.point4.uv, quad.point4.bufferNormal, brightness, quad.point4.rgba);
                        continue;
                    }
                    this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, quad.point1.renderPosition, quad.point1.uv, quad.bufferNormal, brightness, quad.point1.rgba);
                    this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, quad.point2.renderPosition, quad.point2.uv, quad.bufferNormal, brightness, quad.point2.rgba);
                    this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, quad.point3.renderPosition, quad.point3.uv, quad.bufferNormal, brightness, quad.point3.rgba);
                    this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, quad.point4.renderPosition, quad.point4.uv, quad.bufferNormal, brightness, quad.point4.rgba);
                }
                for (i = 0; i < triangles.size(); ++i) {
                    VerletTriangle triangle = triangles.get(i);
                    if (ConfigClient.clothSmoothShading) {
                        this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, triangle.point1.renderPosition, triangle.point1.uv, triangle.point1.bufferNormal, brightness, triangle.point1.rgba);
                        this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, triangle.point2.renderPosition, triangle.point2.uv, triangle.point2.bufferNormal, brightness, triangle.point2.rgba);
                        this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, triangle.point3.renderPosition, triangle.point3.uv, triangle.point3.bufferNormal, brightness, triangle.point3.rgba);
                        this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, triangle.point3.renderPosition, triangle.point3.uv, triangle.point3.bufferNormal, brightness, triangle.point3.rgba);
                        continue;
                    }
                    this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, triangle.point1.renderPosition, triangle.point1.uv, triangle.bufferNormal, brightness, triangle.point1.rgba);
                    this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, triangle.point2.renderPosition, triangle.point2.uv, triangle.bufferNormal, brightness, triangle.point2.rgba);
                    this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, triangle.point3.renderPosition, triangle.point3.uv, triangle.bufferNormal, brightness, triangle.point3.rgba);
                    this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, triangle.point3.renderPosition, triangle.point3.uv, triangle.bufferNormal, brightness, triangle.point3.rgba);
                }
                BufferUploader.drawWithShader((MeshData)bufferbuilder.build());
            } else {
                int i;
                BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.NEW_ENTITY);
                for (i = 0; i < quads.size(); ++i) {
                    VerletQuad quad = quads.get(i);
                    if (ConfigClient.clothSmoothShading) {
                        this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, quad.point4.renderPosition, quad.point4.uv, quad.point4.bufferNormal, brightness, quad.point4.rgba);
                        this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, quad.point3.renderPosition, quad.point3.uv, quad.point3.bufferNormal, brightness, quad.point3.rgba);
                        this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, quad.point2.renderPosition, quad.point2.uv, quad.point2.bufferNormal, brightness, quad.point2.rgba);
                        this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, quad.point1.renderPosition, quad.point1.uv, quad.point1.bufferNormal, brightness, quad.point1.rgba);
                        this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, quad.point4.renderPosition, quad.point4.uv, quad.point4.bufferNormal, brightness, quad.point4.rgba);
                        this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, quad.point2.renderPosition, quad.point2.uv, quad.point2.bufferNormal, brightness, quad.point2.rgba);
                        continue;
                    }
                    this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, quad.point4.renderPosition, quad.point4.uv, quad.bufferNormal, brightness, quad.point4.rgba);
                    this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, quad.point3.renderPosition, quad.point3.uv, quad.bufferNormal, brightness, quad.point3.rgba);
                    this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, quad.point2.renderPosition, quad.point2.uv, quad.bufferNormal, brightness, quad.point2.rgba);
                    this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, quad.point1.renderPosition, quad.point1.uv, quad.bufferNormal, brightness, quad.point1.rgba);
                    this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, quad.point4.renderPosition, quad.point4.uv, quad.bufferNormal, brightness, quad.point4.rgba);
                    this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, quad.point2.renderPosition, quad.point2.uv, quad.bufferNormal, brightness, quad.point2.rgba);
                }
                for (i = 0; i < triangles.size(); ++i) {
                    VerletTriangle triangle = triangles.get(i);
                    if (ConfigClient.clothSmoothShading) {
                        this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, triangle.point3.renderPosition, triangle.point3.uv, triangle.point3.bufferNormal, brightness, triangle.point3.rgba);
                        this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, triangle.point2.renderPosition, triangle.point2.uv, triangle.point2.bufferNormal, brightness, triangle.point2.rgba);
                        this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, triangle.point1.renderPosition, triangle.point1.uv, triangle.point1.bufferNormal, brightness, triangle.point1.rgba);
                        continue;
                    }
                    this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, triangle.point3.renderPosition, triangle.point3.uv, triangle.bufferNormal, brightness, triangle.point3.rgba);
                    this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, triangle.point2.renderPosition, triangle.point2.uv, triangle.bufferNormal, brightness, triangle.point2.rgba);
                    this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, triangle.point1.renderPosition, triangle.point1.uv, triangle.bufferNormal, brightness, triangle.point1.rgba);
                }
                BufferUploader.drawWithShader((MeshData)bufferbuilder.build());
            }
        }
        if (lines.size() > 0) {
            if (vectorPool == null) {
                vectorPool = new SimplePoolVector3d(128);
            }
            this.renderFromFrenetFrame(lines, this.circleShape(5), brightness, 0.02, renderPercent);
            vectorPool.reset();
        }
    }

    private void debugRenderClothNormals(VerletSimulation simulation) {
        for (VerletPoint point : simulation.getPoints()) {
            Tesselator tessellator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
            float length = 0.175f;
            Vector3d p = point.renderPosition;
            Vector3d normal = point.bufferNormal;
            bufferbuilder.addVertex((float)p.x, (float)p.y, (float)p.z).setUv(point.uv.x, point.uv.y).setColor(1.0f, 0.0f, 0.0f, 1.0f);
            bufferbuilder.addVertex((float)(p.x + normal.x * (double)length), (float)(p.y + normal.y * (double)length), (float)(p.z + normal.z * (double)length)).setUv(point.uv.x, point.uv.y).setColor(1.0f, 0.0f, 0.0f, 1.0f);
            BufferUploader.drawWithShader((MeshData)bufferbuilder.build());
        }
    }

    private void renderFromFrenetFrame(List<VerletLine> spline, List<Vector2d> shape, int brightness, double radius, double renderPercent) {
        int j;
        vertices.clear();
        lineData.clear();
        indices.clear();
        int vertexSegments = shape.size();
        int size = spline.size();
        Vector3d tangent = vectorPool.get();
        Vector3d binormal = vectorPool.get();
        Vector3d normal = vectorPool.get();
        for (int i = 0; i <= size; ++i) {
            Vector3d nextPoint;
            Vector3d point;
            VerletLine line = spline.get(Math.min((int)i, (int)(size - 1)));
            if (size == i) {
                point = tmp1.set((Vector3dc)line.point1.renderPosition);
                nextPoint = tmp2.set((Vector3dc)line.point2.renderPosition);
                Vector3d dir = nextPoint.sub((Vector3dc)point, tmp3);
                point.set((Vector3dc)nextPoint);
                nextPoint.add((Vector3dc)dir);
            } else {
                point = tmp1.set((Vector3dc)line.point1.renderPosition);
                nextPoint = tmp2.set((Vector3dc)line.point2.renderPosition);
            }
            this.normalize(nextPoint.sub((Vector3dc)point, tangent));
            this.normalize(tangent.cross((Vector3dc)UP_VECTOR, binormal));
            this.normalize(binormal.cross((Vector3dc)tangent, normal));
            for (j = vertexSegments - 1; j >= 0; --j) {
                Vector2d shapePos = shape.get(j);
                vertices.add((Object)vectorPool.get(point.x + (normal.x * shapePos.x + binormal.x * shapePos.y) * radius, point.y + (normal.y * shapePos.x + binormal.y * shapePos.y) * radius, point.z + (normal.z * shapePos.x + binormal.z * shapePos.y) * radius));
                lineData.add(i);
            }
        }
        int indexOffset = 0;
        int iterations = vertices.size() / vertexSegments - 1;
        boolean closeEnds = true;
        for (int i = 0; i < iterations; ++i) {
            for (j = 0; j < vertexSegments; ++j) {
                int index1 = i * vertexSegments + j + indexOffset;
                int index2 = i * vertexSegments + (j + 1) % vertexSegments + indexOffset;
                indices.add(index1);
                indices.add(index1 + vertexSegments);
                indices.add(index2);
                indices.add(index2);
                indices.add(index1 + vertexSegments);
                indices.add(index2 + vertexSegments);
            }
        }
        if (closeEnds) {
            VerletLine startLine = spline.get(0);
            VerletLine endLine = spline.get(spline.size() - 1);
            Vector3d start = startLine.point1.renderPosition;
            Vector3d end = endLine.point2.renderPosition;
            if (vertices.size() % shape.size() == 0) {
                vertices.add((Object)vectorPool.get(start.x, start.y, start.z));
                vertices.add((Object)vectorPool.get(end.x, end.y, end.z));
                lineData.add(0);
                lineData.add(spline.size() - 1);
            } else {
                ((Vector3d)vertices.get(vertices.size() - 2)).set(start.x, start.y, start.z);
                ((Vector3d)vertices.get(vertices.size() - 1)).set(end.x, end.y, end.z);
                lineData.add(0);
                lineData.add(spline.size() - 1);
            }
            int indexStart = vertices.size() - 2;
            int indexEnd = vertices.size() - 1;
            int startEnd = vertices.size() - 2 - vertexSegments;
            for (int j2 = 0; j2 < vertexSegments; ++j2) {
                indices.add(j2);
                indices.add((j2 + 1) % vertexSegments);
                indices.add(indexStart);
                indices.add(startEnd + j2);
                indices.add(indexEnd);
                indices.add(startEnd + (j2 + 1) % vertexSegments);
            }
        }
        BufferBuilder bufferbuilder = null;
        bufferbuilder = StarterClient.iris && Iris.isExtending() || StarterClient.optifabric && Optifine.isUsingShadersNoInternal() ? Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.NEW_ENTITY) : Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.NEW_ENTITY);
        for (int i = 0; i < indices.size(); ++i) {
            int index = indices.getInt(i);
            Vector3d position = (Vector3d)vertices.get(index);
            index = indices.getInt(i / 3 * 3);
            int lineIndex = lineData.getInt(index);
            VerletLine line = spline.get(Math.min((int)lineIndex, (int)(size - 1)));
            VerletPoint point = lineIndex == size ? line.point2 : line.point1;
            this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, position, point.uv, point.bufferNormal, brightness, point.rgba);
            if (!StarterClient.iris || !Iris.isExtending() || i % 3 != 0) continue;
            this.bufferVertex((VertexConsumer)bufferbuilder, renderPercent, position, point.uv, point.bufferNormal, brightness, point.rgba);
        }
        BufferUploader.drawWithShader((MeshData)bufferbuilder.build());
    }

    private void bufferVertex(VertexConsumer bufferbuilder, double renderPercent, Vector3d position, Vector2f uv, Vector3d normal, int brightness, Vector4f rgba) {
        bufferbuilder.addVertex((float)position.x, (float)position.y, (float)position.z).setColor(rgba.x, rgba.y, rgba.z, rgba.w).setUv(uv.x, uv.y).setOverlay(OverlayTexture.NO_OVERLAY).setLight(brightness).setNormal((float)normal.x, (float)normal.y, (float)normal.z);
    }

    private void normalize(Vector3d v) {
        double lengthSquared = v.x * v.x + v.y * v.y + v.z * v.z;
        if (lengthSquared != 0.0) {
            double invLength = Math.invsqrt((double)lengthSquared);
            v.x *= invLength;
            v.y *= invLength;
            v.z *= invLength;
        }
    }

    public List<Vector2d> circleShape(int segments) {
        ObjectArrayList circle = new ObjectArrayList();
        for (int i = 0; i < segments; ++i) {
            double angle = java.lang.Math.PI * 2 * (double)i / (double)segments;
            Vector2d circlePoint = new Vector2d(Math.cos((double)angle), Math.sin((double)angle));
            circle.add(circlePoint);
        }
        return circle;
    }

    public List<Vector2d> starShape(double spikeRadius, int segments) {
        ObjectArrayList circle = new ObjectArrayList();
        double radius = 1.0 - spikeRadius;
        for (int i = 0; i < segments; ++i) {
            radius = i % 2 == 0 ? 1.0 + spikeRadius : 1.0 - spikeRadius;
            double angle = java.lang.Math.PI * 2 * (double)i / (double)segments;
            Vector2d circlePoint = new Vector2d(Math.cos((double)angle) * radius, Math.sin((double)angle) * radius);
            circle.add(circlePoint);
        }
        return circle;
    }

    @Override
    public void renderAfter(Matrix4fStack matrixStack, double delta, VerletSimulation simulation) {
    }

    static {
        vertices = new ObjectArrayList();
        lineData = new IntArrayList();
        indices = new IntArrayList();
    }
}

