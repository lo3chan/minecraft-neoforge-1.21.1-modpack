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
 *  org.joml.GeometryUtils
 *  org.joml.Math
 *  org.joml.Vector2f
 *  org.joml.Vector2fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4i
 */
package net.diebuddies.model;

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
import net.diebuddies.model.XmlNode;
import net.diebuddies.opengl.Data;
import net.diebuddies.opengl.Mesh;
import net.diebuddies.opengl.Pack;
import net.diebuddies.opengl.Usage;
import net.diebuddies.opengl.VAO;
import net.diebuddies.physics.StarterClient;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.GeometryUtils;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4i;

public class ColladaMesh {
    public List<Vector3f> positions;
    public List<Vector3f> normals;
    public List<Vector2f> texCoords;
    public List<Vector3f> colors;
    public List<Vector4i> indices;
    public List<Integer> lineIndices;
    public byte[] polyCount;

    public static ColladaMesh parseMesh(XmlNode geometryNode) {
        XmlNode meshNode = geometryNode.getChild("mesh");
        ColladaMesh mesh = new ColladaMesh();
        mesh.parsePositions(meshNode);
        mesh.parseNormals(meshNode);
        mesh.parseTextureCoordinates(meshNode);
        mesh.parseColors(meshNode);
        mesh.parseIndices(meshNode);
        return mesh;
    }

    private void parsePositions(XmlNode mesh) {
        String id = mesh.getChild("vertices").getChild("input").getAttribute("source").substring(1);
        XmlNode source = mesh.getChildWithAttribute("source", "id", id);
        XmlNode floatArray = source.getChild("float_array");
        this.positions = new ObjectArrayList();
        String[] data = floatArray.getData().split(" ");
        for (int i = 0; i < data.length / 3; ++i) {
            this.positions.add(new Vector3f(Float.parseFloat(data[i * 3]), Float.parseFloat(data[i * 3 + 1]), Float.parseFloat(data[i * 3 + 2])));
        }
    }

    private void parseIndices(XmlNode mesh) {
        int i;
        boolean onlyTriangles = false;
        XmlNode triangles = mesh.getChild("polylist");
        if (triangles == null) {
            triangles = mesh.getChild("triangles");
            onlyTriangles = true;
        }
        XmlNode indicesArray = triangles.getChild("p");
        int vertexOffset = Integer.parseInt(triangles.getChildWithAttribute("input", "semantic", "VERTEX").getAttribute("offset"));
        int normalOffset = Integer.parseInt(triangles.getChildWithAttribute("input", "semantic", "NORMAL").getAttribute("offset"));
        int texCoordOffset = Integer.parseInt(triangles.getChildWithAttribute("input", "semantic", "TEXCOORD").getAttribute("offset"));
        int colorOffset = Integer.parseInt(triangles.getChildWithAttribute("input", "semantic", "COLOR").getAttribute("offset"));
        this.indices = new ObjectArrayList();
        this.lineIndices = new ObjectArrayList();
        XmlNode lines = mesh.getChild("lines");
        if (lines != null) {
            XmlNode lineIndicesArray = lines.getChild("p");
            String[] lineData = lineIndicesArray.getData().split(" ");
            for (i = 0; i < lineData.length; ++i) {
                this.lineIndices.add(Integer.parseInt(lineData[i]));
            }
        }
        String[] data = indicesArray.getData().split(" ");
        if (onlyTriangles) {
            this.polyCount = new byte[data.length / 12];
            for (int i2 = 0; i2 < this.polyCount.length; ++i2) {
                this.polyCount[i2] = 3;
            }
        } else {
            String[] vcountData = triangles.getChild("vcount").getData().split(" ");
            this.polyCount = new byte[vcountData.length];
            for (i = 0; i < vcountData.length; ++i) {
                this.polyCount[i] = Byte.parseByte(vcountData[i]);
            }
        }
        for (int i3 = 0; i3 < data.length / 4; ++i3) {
            int[] indices = new int[]{Integer.parseInt(data[i3 * 4]), Integer.parseInt(data[i3 * 4 + 1]), Integer.parseInt(data[i3 * 4 + 2]), Integer.parseInt(data[i3 * 4 + 3])};
            this.indices.add(new Vector4i(indices[vertexOffset], indices[normalOffset], indices[texCoordOffset], indices[colorOffset]));
        }
    }

    private void parseNormals(XmlNode mesh) {
        XmlNode triangles = mesh.getChild("polylist");
        if (triangles == null) {
            triangles = mesh.getChild("triangles");
        }
        String id = triangles.getChildWithAttribute("input", "semantic", "NORMAL").getAttribute("source").substring(1);
        XmlNode source = mesh.getChildWithAttribute("source", "id", id);
        XmlNode floatArray = source.getChild("float_array");
        this.normals = new ObjectArrayList();
        String[] data = floatArray.getData().split(" ");
        for (int i = 0; i < data.length / 3; ++i) {
            this.normals.add(new Vector3f(Float.parseFloat(data[i * 3]), Float.parseFloat(data[i * 3 + 1]), Float.parseFloat(data[i * 3 + 2])));
        }
    }

    private void parseTextureCoordinates(XmlNode mesh) {
        XmlNode texCoordNode;
        XmlNode triangles = mesh.getChild("polylist");
        if (triangles == null) {
            triangles = mesh.getChild("triangles");
        }
        if ((texCoordNode = triangles.getChildWithAttribute("input", "semantic", "TEXCOORD")) == null) {
            return;
        }
        String id = texCoordNode.getAttribute("source").substring(1);
        XmlNode source = mesh.getChildWithAttribute("source", "id", id);
        XmlNode floatArray = source.getChild("float_array");
        this.texCoords = new ObjectArrayList();
        String[] data = floatArray.getData().split(" ");
        for (int i = 0; i < data.length / 2; ++i) {
            this.texCoords.add(new Vector2f(Float.parseFloat(data[i * 2]), Float.parseFloat(data[i * 2 + 1])));
        }
    }

    private void parseColors(XmlNode mesh) {
        XmlNode color;
        XmlNode triangles = mesh.getChild("polylist");
        if (triangles == null) {
            triangles = mesh.getChild("triangles");
        }
        if ((color = triangles.getChildWithAttribute("input", "semantic", "COLOR")) == null) {
            return;
        }
        String id = color.getAttribute("source").substring(1);
        XmlNode source = mesh.getChildWithAttribute("source", "id", id);
        XmlNode floatArray = source.getChild("float_array");
        int stride = Integer.parseInt(source.getChild("technique_common").getChild("accessor").getAttribute("stride"));
        this.colors = new ObjectArrayList();
        String[] data = floatArray.getData().split(" ");
        for (int i = 0; i < data.length / stride; ++i) {
            this.colors.add(new Vector3f(Float.parseFloat(data[i * stride]), Float.parseFloat(data[i * stride + 1]), Float.parseFloat(data[i * stride + 2])));
        }
    }

    public VAO createVAO(boolean flatShading) {
        int indexCount;
        ObjectArrayList tangents = null;
        float[] mposition = new float[this.indices.size() * 3];
        float[] muv = new float[this.indices.size() * 2];
        int[] mnormal = new int[this.indices.size()];
        int[] mtangent = null;
        if (StarterClient.iris) {
            int i;
            mtangent = new int[this.indices.size()];
            tangents = new ObjectArrayList(this.normals.size());
            indexCount = 0;
            Vector3f tmpTangent = new Vector3f();
            for (i = 0; i < this.normals.size(); ++i) {
                tangents.add(new Vector3f());
            }
            for (i = 0; i < this.polyCount.length; ++i) {
                int polyCount = this.polyCount[i];
                if (polyCount > 2) {
                    int pindex1 = this.indices.get((int)indexCount).x;
                    int tindex1 = this.indices.get((int)indexCount).z;
                    int pindex2 = this.indices.get((int)(indexCount + 1)).x;
                    int tindex2 = this.indices.get((int)(indexCount + 1)).z;
                    int pindex3 = this.indices.get((int)(indexCount + 2)).x;
                    int tindex3 = this.indices.get((int)(indexCount + 2)).z;
                    GeometryUtils.tangent((Vector3fc)((Vector3fc)this.positions.get(pindex1)), (Vector2fc)((Vector2fc)this.texCoords.get(tindex1)), (Vector3fc)((Vector3fc)this.positions.get(pindex2)), (Vector2fc)((Vector2fc)this.texCoords.get(tindex2)), (Vector3fc)((Vector3fc)this.positions.get(pindex3)), (Vector2fc)((Vector2fc)this.texCoords.get(tindex3)), (Vector3f)tmpTangent);
                    for (int j = 0; j < polyCount; ++j) {
                        int nindex = this.indices.get((int)(indexCount + j)).y;
                        ((Vector3f)tangents.get(nindex)).add((Vector3fc)tmpTangent);
                    }
                } else {
                    for (int j = 0; j < polyCount; ++j) {
                        int nindex = this.indices.get((int)(indexCount + j)).y;
                        Vector3f normal = this.normals.get(nindex);
                        ((Vector3f)tangents.get(nindex)).add(normal.z, -normal.x, normal.y);
                    }
                }
                indexCount += polyCount;
            }
            for (i = 0; i < this.normals.size(); ++i) {
                Vector3f tangent = (Vector3f)tangents.get(i);
                float lengthSquared = tangent.lengthSquared();
                if (lengthSquared > 0.0f) {
                    tangent.div(Math.sqrt((float)lengthSquared));
                    continue;
                }
                tangent.set(0.0f, 1.0f, 0.0f);
            }
        }
        indexCount = 0;
        IntArrayList mindices = new IntArrayList();
        Vector3f flatNormal = new Vector3f(0.0f, 1.0f, 0.0f);
        for (int i = 0; i < this.polyCount.length; ++i) {
            int polyCount = this.polyCount[i];
            if (polyCount == 4) {
                mindices.add(indexCount + 2);
                mindices.add(indexCount + 1);
                mindices.add(indexCount);
                mindices.add(indexCount);
                mindices.add(indexCount + 3);
                mindices.add(indexCount + 2);
                if (flatShading) {
                    GeometryUtils.normal((Vector3fc)((Vector3fc)this.positions.get(this.indices.get((int)indexCount).x)), (Vector3fc)((Vector3fc)this.positions.get(this.indices.get((int)(indexCount + 1)).x)), (Vector3fc)((Vector3fc)this.positions.get(this.indices.get((int)(indexCount + 2)).x)), (Vector3f)flatNormal);
                }
            } else if (polyCount == 3) {
                mindices.add(indexCount + 2);
                mindices.add(indexCount + 1);
                mindices.add(indexCount);
                if (flatShading) {
                    GeometryUtils.normal((Vector3fc)((Vector3fc)this.positions.get(this.indices.get((int)indexCount).x)), (Vector3fc)((Vector3fc)this.positions.get(this.indices.get((int)(indexCount + 1)).x)), (Vector3fc)((Vector3fc)this.positions.get(this.indices.get((int)(indexCount + 2)).x)), (Vector3f)flatNormal);
                }
            }
            for (int j = 0; j < polyCount; ++j) {
                int pindex = this.indices.get((int)indexCount).x;
                int nindex = this.indices.get((int)indexCount).y;
                int tindex = this.indices.get((int)indexCount).z;
                Vector3f position = this.positions.get(pindex);
                Vector3f normal = this.normals.get(nindex);
                Vector2f uv = this.texCoords.get(tindex);
                mposition[indexCount * 3] = position.x;
                mposition[indexCount * 3 + 1] = position.y;
                mposition[indexCount * 3 + 2] = position.z;
                muv[indexCount * 2] = uv.x;
                muv[indexCount * 2 + 1] = uv.y;
                mnormal[indexCount] = flatShading ? Pack.normal(flatNormal.x, flatNormal.y, flatNormal.z) : Pack.normal(normal.x, normal.y, normal.z);
                if (StarterClient.iris) {
                    Vector3f tangent = (Vector3f)tangents.get(nindex);
                    mtangent[i] = flatShading ? Pack.normal(flatNormal.z, flatNormal.y, flatNormal.z, 1.0f) : Pack.normal(tangent.z, tangent.y, tangent.z, 1.0f);
                }
                ++indexCount;
            }
        }
        Mesh mesh = new Mesh();
        int[] finalIndices = mindices.toArray(new int[mindices.size()]);
        mesh.set(mposition, Data.POSITION);
        mesh.set(mnormal, Data.NORMAL);
        mesh.set(muv, Data.TEX_COORD);
        if (StarterClient.iris) {
            mesh.set(mtangent, Data.TANGENT_SHADER);
        } else if (StarterClient.optifabric) {
            mesh.set(mtangent, Data.TANGENT_OPTIFINE);
        }
        mesh.set(finalIndices, Data.INDEX);
        return mesh.constructVAO(Usage.STATIC);
    }

    public void flipUVs() {
        for (Vector2f uv : this.texCoords) {
            uv.y = 1.0f - uv.y;
        }
    }

    public void renderSlow(int brightness, boolean smoothShading) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.NEW_ENTITY);
        Vector3f normal = new Vector3f();
        int indexCount = 0;
        for (int i = 0; i < this.polyCount.length; ++i) {
            byte polyCount = this.polyCount[i];
            if (smoothShading) {
                if (polyCount == 4) {
                    this.bufferVertex((VertexConsumer)bufferbuilder, indexCount + 2, brightness);
                    this.bufferVertex((VertexConsumer)bufferbuilder, indexCount + 1, brightness);
                    this.bufferVertex((VertexConsumer)bufferbuilder, indexCount, brightness);
                    this.bufferVertex((VertexConsumer)bufferbuilder, indexCount, brightness);
                    this.bufferVertex((VertexConsumer)bufferbuilder, indexCount + 3, brightness);
                    this.bufferVertex((VertexConsumer)bufferbuilder, indexCount + 2, brightness);
                } else if (polyCount == 3) {
                    this.bufferVertex((VertexConsumer)bufferbuilder, indexCount, brightness);
                    this.bufferVertex((VertexConsumer)bufferbuilder, indexCount + 1, brightness);
                    this.bufferVertex((VertexConsumer)bufferbuilder, indexCount + 2, brightness);
                }
            } else if (polyCount == 4) {
                GeometryUtils.normal((Vector3fc)((Vector3fc)this.positions.get(this.indices.get((int)indexCount).x)), (Vector3fc)((Vector3fc)this.positions.get(this.indices.get((int)(indexCount + 1)).x)), (Vector3fc)((Vector3fc)this.positions.get(this.indices.get((int)(indexCount + 2)).x)), (Vector3f)normal);
                this.bufferVertex((VertexConsumer)bufferbuilder, indexCount + 2, normal, brightness);
                this.bufferVertex((VertexConsumer)bufferbuilder, indexCount + 1, normal, brightness);
                this.bufferVertex((VertexConsumer)bufferbuilder, indexCount, normal, brightness);
                this.bufferVertex((VertexConsumer)bufferbuilder, indexCount, normal, brightness);
                this.bufferVertex((VertexConsumer)bufferbuilder, indexCount + 3, normal, brightness);
                this.bufferVertex((VertexConsumer)bufferbuilder, indexCount + 2, normal, brightness);
            } else if (polyCount == 3) {
                GeometryUtils.normal((Vector3fc)((Vector3fc)this.positions.get(this.indices.get((int)indexCount).x)), (Vector3fc)((Vector3fc)this.positions.get(this.indices.get((int)(indexCount + 1)).x)), (Vector3fc)((Vector3fc)this.positions.get(this.indices.get((int)(indexCount + 2)).x)), (Vector3f)normal);
                this.bufferVertex((VertexConsumer)bufferbuilder, indexCount, normal, brightness);
                this.bufferVertex((VertexConsumer)bufferbuilder, indexCount + 1, normal, brightness);
                this.bufferVertex((VertexConsumer)bufferbuilder, indexCount + 2, normal, brightness);
            }
            indexCount += polyCount;
        }
        BufferUploader.drawWithShader((MeshData)bufferbuilder.build());
    }

    private void bufferVertex(VertexConsumer bufferbuilder, int index, int brightness) {
        int pindex = this.indices.get((int)index).x;
        int nindex = this.indices.get((int)index).y;
        int tindex = this.indices.get((int)index).z;
        Vector3f position = this.positions.get(pindex);
        Vector3f normal = this.normals.get(nindex);
        Vector2f uv = this.texCoords.get(tindex);
        bufferbuilder.addVertex(position.x, position.y, position.z).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(uv.x, uv.y).setOverlay(OverlayTexture.NO_OVERLAY).setLight(brightness).setNormal(normal.x, normal.y, normal.z);
    }

    private void bufferVertex(VertexConsumer bufferbuilder, int index, Vector3f normal, int brightness) {
        int pindex = this.indices.get((int)index).x;
        int tindex = this.indices.get((int)index).z;
        Vector3f position = this.positions.get(pindex);
        Vector2f uv = this.texCoords.get(tindex);
        bufferbuilder.addVertex(position.x, position.y, position.z).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(uv.x, uv.y).setOverlay(OverlayTexture.NO_OVERLAY).setLight(brightness).setNormal(normal.x, normal.y, normal.z);
    }
}

