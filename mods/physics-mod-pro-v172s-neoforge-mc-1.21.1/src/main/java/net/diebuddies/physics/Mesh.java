/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.bytes.ByteArrayList
 *  it.unimi.dsi.fastutil.bytes.ByteList
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  org.joml.Math
 *  org.joml.Vector2f
 *  org.joml.Vector2fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 */
package net.diebuddies.physics;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.diebuddies.dualcontouring.DualContouring3d;
import net.diebuddies.opengl.Pack;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;

public class Mesh {
    private static final Vector3f[] faceNormals = new Vector3f[]{new Vector3f(0.0f, 0.0f, 1.0f), new Vector3f(1.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, -1.0f), new Vector3f(-1.0f, 0.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(0.0f, -1.0f, 0.0f)};
    public List<Vector3f> positions;
    public List<Vector2f> uvs;
    public List<Vector3f> normals;
    public IntList colors;
    public ByteList sides;
    public List<Vector4f> tangents;
    public Vector2f midcoord;
    public List<Vector2f> midcoords;
    public IntList indices;
    public IntList indicesQuads;
    public Vector3f offset;
    public float radius = -1.0f;
    public int sodiumUVOffset = 0;
    public boolean canDiscard = true;

    public Mesh(boolean canDiscard) {
        this.canDiscard = canDiscard;
        this.positions = new ObjectArrayList();
        this.uvs = new ObjectArrayList();
        this.normals = new ObjectArrayList();
        this.indices = new IntArrayList();
        this.indicesQuads = new IntArrayList();
        this.colors = new IntArrayList();
        this.sides = new ByteArrayList();
    }

    public Mesh() {
        this(true);
    }

    public void calculatePBRData(boolean defaultMidCoord) {
        this.midcoords = new ObjectArrayList();
        this.midcoord = new Vector2f();
        this.tangents = new ObjectArrayList();
        for (int i = 0; i < this.normals.size(); ++i) {
            this.tangents.add(new Vector4f());
        }
        Vector2f uvmin = new Vector2f(Float.MAX_VALUE);
        Vector2f uvmax = new Vector2f(-3.4028235E38f);
        Vector3f tmp1 = new Vector3f();
        Vector3f tmp2 = new Vector3f();
        for (int i = 0; i < this.indices.size(); i += 3) {
            int i0 = this.indices.getInt(i);
            int i1 = this.indices.getInt(i + 1);
            int i2 = this.indices.getInt(i + 2);
            Vector3f v0 = this.positions.get(i0);
            Vector3f v1 = this.positions.get(i1);
            Vector3f v2 = this.positions.get(i2);
            if (this.uvs.size() == 0) continue;
            Vector2f uv0 = this.uvs.get(i0);
            Vector2f uv1 = this.uvs.get(i1);
            Vector2f uv2 = this.uvs.get(i2);
            Vector4f t0 = this.tangents.get(i0);
            Vector4f t1 = this.tangents.get(i1);
            Vector4f t2 = this.tangents.get(i2);
            Vector3f normal = this.normals.get(i0);
            Vector3f edge1 = v1.sub((Vector3fc)v0, tmp1);
            Vector3f edge2 = v2.sub((Vector3fc)v0, tmp2);
            float deltaU1 = uv1.x - uv0.x;
            float deltaV2 = uv2.y - uv0.y;
            float deltaU2 = uv2.x - uv0.x;
            float deltaV1 = uv1.y - uv0.y;
            float fdenom = deltaU1 * deltaV2 - deltaU2 * deltaV1;
            float f = fdenom == 0.0f ? 1.0f : 1.0f / fdenom;
            float tangentx = f * (deltaV2 * edge1.x - deltaV1 * edge2.x);
            float tangenty = f * (deltaV2 * edge1.y - deltaV1 * edge2.y);
            float tangentz = f * (deltaV2 * edge1.z - deltaV1 * edge2.z);
            float tcoeff = DualContouring3d.rsqrt(tangentx * tangentx + tangenty * tangenty + tangentz * tangentz);
            tangentx *= tcoeff;
            tangenty *= tcoeff;
            float bitangentx = f * (-deltaU2 * edge1.x + deltaU1 * edge2.x);
            float bitangenty = f * (-deltaU2 * edge1.y + deltaU1 * edge2.y);
            float bitangentz = f * (-deltaU2 * edge1.z + deltaU1 * edge2.z);
            float bitcoeff = DualContouring3d.rsqrt(bitangentx * bitangentx + bitangenty * bitangenty + bitangentz * bitangentz);
            float pbitangentx = tangenty * normal.z - (tangentz *= tcoeff) * normal.y;
            float pbitangenty = tangentz * normal.x - tangentx * normal.z;
            float pbitangentz = tangentx * normal.y - tangenty * normal.x;
            float dot = (bitangentx *= bitcoeff) * pbitangentx + (bitangenty *= bitcoeff) * pbitangenty + (bitangentz *= bitcoeff) * pbitangentz;
            float tangentw = dot < 0.0f ? -1.0f : 1.0f;
            t0.set(tangentx, tangenty, tangentz, tangentw);
            t1.set(tangentx, tangenty, tangentz, tangentw);
            t2.set(tangentx, tangenty, tangentz, tangentw);
            uvmin.set((Vector2fc)uv0);
            uvmin.min((Vector2fc)uv1);
            uvmin.min((Vector2fc)uv2);
            uvmax.set((Vector2fc)uv0);
            uvmax.max((Vector2fc)uv1);
            uvmax.max((Vector2fc)uv2);
            if (defaultMidCoord) {
                this.midcoords.add(new Vector2f(0.5f));
                continue;
            }
            this.midcoords.add(new Vector2f((Vector2fc)uvmin).add((Vector2fc)uvmax).mul(0.5f));
        }
    }

    public void addColor(float r, float g, float b) {
        this.colors.add(Pack.color(r, g, b));
    }

    public void addColor(float r, float g, float b, float a) {
        this.colors.add(Pack.color(r, g, b, a));
    }

    public void addColor(int r, int g, int b, int a) {
        this.colors.add(Pack.color(r, g, b, a));
    }

    public float getRadius(boolean reculalcate) {
        if (reculalcate) {
            this.radius = -1.0f;
        }
        if (this.radius < 0.0f) {
            for (int i = 0; i < this.positions.size(); ++i) {
                Vector3f position = this.positions.get(i);
                float radiusSquared = position.lengthSquared();
                if (!(radiusSquared > this.radius)) continue;
                this.radius = radiusSquared;
            }
            this.radius = (float)Math.sqrt(this.radius);
        }
        return this.radius;
    }

    public float getRadius() {
        return this.getRadius(false);
    }

    public void move(Vector3f position) {
        for (int i = 0; i < this.positions.size(); ++i) {
            this.positions.get(i).add((Vector3fc)position);
        }
    }

    public void calculateOffset() {
        this.calculateOffset(false);
    }

    public void calculateOffset(boolean needsSides) {
        Mesh.calculateMeshOffsets(this, needsSides);
    }

    public static void calculateMeshOffsets(Mesh mesh, boolean needsSides) {
        Vector3f offset = mesh.offset = new Vector3f();
        for (Vector3f position : mesh.positions) {
            offset.add((Vector3fc)position);
        }
        offset.div((float)mesh.positions.size());
        Mesh.calculateMetaData(mesh, needsSides);
    }

    public static void calculateMeshOffsets(List<Mesh> meshes, boolean needsSides) {
        Mesh mesh;
        int i;
        int meshSize = meshes.size();
        int count = 0;
        Vector3f offset = new Vector3f();
        for (i = 0; i < meshSize; ++i) {
            mesh = meshes.get(i);
            for (Vector3f position : mesh.positions) {
                offset.add((Vector3fc)position);
            }
            count += mesh.positions.size();
        }
        offset.div((float)count);
        for (i = 0; i < meshSize; ++i) {
            mesh = meshes.get(i);
            mesh.offset = new Vector3f((Vector3fc)offset);
            Mesh.calculateMetaData(mesh, needsSides);
        }
    }

    private static void calculateMetaData(Mesh mesh, boolean needsSides) {
        Vector3f offset = mesh.offset;
        for (Vector3f position : mesh.positions) {
            position.sub((Vector3fc)offset);
            float radiusSquared = position.lengthSquared();
            if (!(radiusSquared > mesh.radius)) continue;
            mesh.radius = radiusSquared;
        }
        mesh.radius = (float)Math.sqrt(mesh.radius);
        if (needsSides) {
            for (int j = 0; j < mesh.normals.size(); ++j) {
                boolean hasSide = false;
                Vector3f normal = mesh.normals.get(j);
                for (int i = 0; i < faceNormals.length; ++i) {
                    if (!((double)Math.abs(org.joml.Math.acos((float)normal.dot((Vector3fc)faceNormals[i]))) < 0.01)) continue;
                    mesh.sides.add((byte)i);
                    hasSide = true;
                    break;
                }
                if (hasSide) continue;
                mesh.sides.add((byte)-1);
            }
        }
    }

    public List<Integer> calculateFaceDirections() {
        ObjectArrayList sides = new ObjectArrayList();
        for (int j = 0; j < this.normals.size(); ++j) {
            int maxComp;
            Vector3f normal = this.normals.get(j);
            double max = normal.get(maxComp = normal.maxComponent());
            if (max >= 0.0) {
                if (maxComp == 0) {
                    sides.add(1);
                    continue;
                }
                if (maxComp == 1) {
                    sides.add(4);
                    continue;
                }
                sides.add(0);
                continue;
            }
            if (maxComp == 0) {
                sides.add(3);
                continue;
            }
            if (maxComp == 1) {
                sides.add(5);
                continue;
            }
            sides.add(2);
        }
        return sides;
    }

    public boolean isEmpty() {
        return this.indices.size() == 0;
    }

    public void clearMemory() {
        if (!this.canDiscard) {
            return;
        }
        this.positions = null;
        this.uvs = null;
        this.normals = null;
        this.colors = null;
        this.sides = null;
        this.tangents = null;
        this.midcoord = null;
        this.midcoords = null;
        this.indices = null;
        this.indicesQuads = null;
    }
}

