package net.diebuddies.model;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
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

      for (int i = 0; i < data.length / 3; i++) {
         this.positions.add(new Vector3f(Float.parseFloat(data[i * 3]), Float.parseFloat(data[i * 3 + 1]), Float.parseFloat(data[i * 3 + 2])));
      }
   }

   private void parseIndices(XmlNode mesh) {
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

         for (int i = 0; i < lineData.length; i++) {
            this.lineIndices.add(Integer.parseInt(lineData[i]));
         }
      }

      String[] data = indicesArray.getData().split(" ");
      if (onlyTriangles) {
         this.polyCount = new byte[data.length / 12];

         for (int i = 0; i < this.polyCount.length; i++) {
            this.polyCount[i] = 3;
         }
      } else {
         String[] vcountData = triangles.getChild("vcount").getData().split(" ");
         this.polyCount = new byte[vcountData.length];

         for (int i = 0; i < vcountData.length; i++) {
            this.polyCount[i] = Byte.parseByte(vcountData[i]);
         }
      }

      for (int i = 0; i < data.length / 4; i++) {
         int[] indices = new int[]{
            Integer.parseInt(data[i * 4]), Integer.parseInt(data[i * 4 + 1]), Integer.parseInt(data[i * 4 + 2]), Integer.parseInt(data[i * 4 + 3])
         };
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

      for (int i = 0; i < data.length / 3; i++) {
         this.normals.add(new Vector3f(Float.parseFloat(data[i * 3]), Float.parseFloat(data[i * 3 + 1]), Float.parseFloat(data[i * 3 + 2])));
      }
   }

   private void parseTextureCoordinates(XmlNode mesh) {
      XmlNode triangles = mesh.getChild("polylist");
      if (triangles == null) {
         triangles = mesh.getChild("triangles");
      }

      XmlNode texCoordNode = triangles.getChildWithAttribute("input", "semantic", "TEXCOORD");
      if (texCoordNode != null) {
         String id = texCoordNode.getAttribute("source").substring(1);
         XmlNode source = mesh.getChildWithAttribute("source", "id", id);
         XmlNode floatArray = source.getChild("float_array");
         this.texCoords = new ObjectArrayList();
         String[] data = floatArray.getData().split(" ");

         for (int i = 0; i < data.length / 2; i++) {
            this.texCoords.add(new Vector2f(Float.parseFloat(data[i * 2]), Float.parseFloat(data[i * 2 + 1])));
         }
      }
   }

   private void parseColors(XmlNode mesh) {
      XmlNode triangles = mesh.getChild("polylist");
      if (triangles == null) {
         triangles = mesh.getChild("triangles");
      }

      XmlNode color = triangles.getChildWithAttribute("input", "semantic", "COLOR");
      if (color != null) {
         String id = color.getAttribute("source").substring(1);
         XmlNode source = mesh.getChildWithAttribute("source", "id", id);
         XmlNode floatArray = source.getChild("float_array");
         int stride = Integer.parseInt(source.getChild("technique_common").getChild("accessor").getAttribute("stride"));
         this.colors = new ObjectArrayList();
         String[] data = floatArray.getData().split(" ");

         for (int i = 0; i < data.length / stride; i++) {
            this.colors.add(new Vector3f(Float.parseFloat(data[i * stride]), Float.parseFloat(data[i * stride + 1]), Float.parseFloat(data[i * stride + 2])));
         }
      }
   }

   public VAO createVAO(boolean flatShading) {
      List<Vector3f> tangents = null;
      float[] mposition = new float[this.indices.size() * 3];
      float[] muv = new float[this.indices.size() * 2];
      int[] mnormal = new int[this.indices.size()];
      int[] mtangent = null;
      if (StarterClient.iris) {
         mtangent = new int[this.indices.size()];
         tangents = new ObjectArrayList(this.normals.size());
         int indexCount = 0;
         Vector3f tmpTangent = new Vector3f();

         for (int i = 0; i < this.normals.size(); i++) {
            tangents.add(new Vector3f());
         }

         for (int i = 0; i < this.polyCount.length; i++) {
            byte polyCount = this.polyCount[i];
            if (polyCount > 2) {
               int pindex1 = this.indices.get(indexCount).x;
               int tindex1 = this.indices.get(indexCount).z;
               int pindex2 = this.indices.get(indexCount + 1).x;
               int tindex2 = this.indices.get(indexCount + 1).z;
               int pindex3 = this.indices.get(indexCount + 2).x;
               int tindex3 = this.indices.get(indexCount + 2).z;
               GeometryUtils.tangent(
                  (Vector3fc)this.positions.get(pindex1),
                  (Vector2fc)this.texCoords.get(tindex1),
                  (Vector3fc)this.positions.get(pindex2),
                  (Vector2fc)this.texCoords.get(tindex2),
                  (Vector3fc)this.positions.get(pindex3),
                  (Vector2fc)this.texCoords.get(tindex3),
                  tmpTangent
               );

               for (int j = 0; j < polyCount; j++) {
                  int nindex = this.indices.get(indexCount + j).y;
                  tangents.get(nindex).add(tmpTangent);
               }
            } else {
               for (int j = 0; j < polyCount; j++) {
                  int nindex = this.indices.get(indexCount + j).y;
                  Vector3f normal = this.normals.get(nindex);
                  tangents.get(nindex).add(normal.z, -normal.x, normal.y);
               }
            }

            indexCount += polyCount;
         }

         for (int i = 0; i < this.normals.size(); i++) {
            Vector3f tangent = tangents.get(i);
            float lengthSquared = tangent.lengthSquared();
            if (lengthSquared > 0.0F) {
               tangent.div(Math.sqrt(lengthSquared));
            } else {
               tangent.set(0.0F, 1.0F, 0.0F);
            }
         }
      }

      int indexCount = 0;
      IntList mindices = new IntArrayList();
      Vector3f flatNormal = new Vector3f(0.0F, 1.0F, 0.0F);

      for (int ix = 0; ix < this.polyCount.length; ix++) {
         byte polyCount = this.polyCount[ix];
         if (polyCount == 4) {
            mindices.add(indexCount + 2);
            mindices.add(indexCount + 1);
            mindices.add(indexCount);
            mindices.add(indexCount);
            mindices.add(indexCount + 3);
            mindices.add(indexCount + 2);
            if (flatShading) {
               GeometryUtils.normal(
                  (Vector3fc)this.positions.get(this.indices.get(indexCount).x),
                  (Vector3fc)this.positions.get(this.indices.get(indexCount + 1).x),
                  (Vector3fc)this.positions.get(this.indices.get(indexCount + 2).x),
                  flatNormal
               );
            }
         } else if (polyCount == 3) {
            mindices.add(indexCount + 2);
            mindices.add(indexCount + 1);
            mindices.add(indexCount);
            if (flatShading) {
               GeometryUtils.normal(
                  (Vector3fc)this.positions.get(this.indices.get(indexCount).x),
                  (Vector3fc)this.positions.get(this.indices.get(indexCount + 1).x),
                  (Vector3fc)this.positions.get(this.indices.get(indexCount + 2).x),
                  flatNormal
               );
            }
         }

         for (int j = 0; j < polyCount; j++) {
            int pindex = this.indices.get(indexCount).x;
            int nindex = this.indices.get(indexCount).y;
            int tindex = this.indices.get(indexCount).z;
            Vector3f position = this.positions.get(pindex);
            Vector3f normal = this.normals.get(nindex);
            Vector2f uv = this.texCoords.get(tindex);
            mposition[indexCount * 3] = position.x;
            mposition[indexCount * 3 + 1] = position.y;
            mposition[indexCount * 3 + 2] = position.z;
            muv[indexCount * 2] = uv.x;
            muv[indexCount * 2 + 1] = uv.y;
            if (flatShading) {
               mnormal[indexCount] = Pack.normal(flatNormal.x, flatNormal.y, flatNormal.z);
            } else {
               mnormal[indexCount] = Pack.normal(normal.x, normal.y, normal.z);
            }

            if (StarterClient.iris) {
               Vector3f tangent = tangents.get(nindex);
               if (flatShading) {
                  mtangent[ix] = Pack.normal(flatNormal.z, flatNormal.y, flatNormal.z, 1.0F);
               } else {
                  mtangent[ix] = Pack.normal(tangent.z, tangent.y, tangent.z, 1.0F);
               }
            }

            indexCount++;
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
         uv.y = 1.0F - uv.y;
      }
   }

   public void renderSlow(int brightness, boolean smoothShading) {
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tesselator.begin(Mode.TRIANGLES, DefaultVertexFormat.NEW_ENTITY);
      Vector3f normal = new Vector3f();
      int indexCount = 0;

      for (int i = 0; i < this.polyCount.length; i++) {
         byte polyCount = this.polyCount[i];
         if (smoothShading) {
            if (polyCount == 4) {
               this.bufferVertex(bufferbuilder, indexCount + 2, brightness);
               this.bufferVertex(bufferbuilder, indexCount + 1, brightness);
               this.bufferVertex(bufferbuilder, indexCount, brightness);
               this.bufferVertex(bufferbuilder, indexCount, brightness);
               this.bufferVertex(bufferbuilder, indexCount + 3, brightness);
               this.bufferVertex(bufferbuilder, indexCount + 2, brightness);
            } else if (polyCount == 3) {
               this.bufferVertex(bufferbuilder, indexCount, brightness);
               this.bufferVertex(bufferbuilder, indexCount + 1, brightness);
               this.bufferVertex(bufferbuilder, indexCount + 2, brightness);
            }
         } else if (polyCount == 4) {
            GeometryUtils.normal(
               (Vector3fc)this.positions.get(this.indices.get(indexCount).x),
               (Vector3fc)this.positions.get(this.indices.get(indexCount + 1).x),
               (Vector3fc)this.positions.get(this.indices.get(indexCount + 2).x),
               normal
            );
            this.bufferVertex(bufferbuilder, indexCount + 2, normal, brightness);
            this.bufferVertex(bufferbuilder, indexCount + 1, normal, brightness);
            this.bufferVertex(bufferbuilder, indexCount, normal, brightness);
            this.bufferVertex(bufferbuilder, indexCount, normal, brightness);
            this.bufferVertex(bufferbuilder, indexCount + 3, normal, brightness);
            this.bufferVertex(bufferbuilder, indexCount + 2, normal, brightness);
         } else if (polyCount == 3) {
            GeometryUtils.normal(
               (Vector3fc)this.positions.get(this.indices.get(indexCount).x),
               (Vector3fc)this.positions.get(this.indices.get(indexCount + 1).x),
               (Vector3fc)this.positions.get(this.indices.get(indexCount + 2).x),
               normal
            );
            this.bufferVertex(bufferbuilder, indexCount, normal, brightness);
            this.bufferVertex(bufferbuilder, indexCount + 1, normal, brightness);
            this.bufferVertex(bufferbuilder, indexCount + 2, normal, brightness);
         }

         indexCount += polyCount;
      }

      BufferUploader.drawWithShader(bufferbuilder.build());
   }

   private void bufferVertex(VertexConsumer bufferbuilder, int index, int brightness) {
      int pindex = this.indices.get(index).x;
      int nindex = this.indices.get(index).y;
      int tindex = this.indices.get(index).z;
      Vector3f position = this.positions.get(pindex);
      Vector3f normal = this.normals.get(nindex);
      Vector2f uv = this.texCoords.get(tindex);
      bufferbuilder.addVertex(position.x, position.y, position.z)
         .setColor(1.0F, 1.0F, 1.0F, 1.0F)
         .setUv(uv.x, uv.y)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(brightness)
         .setNormal(normal.x, normal.y, normal.z);
   }

   private void bufferVertex(VertexConsumer bufferbuilder, int index, Vector3f normal, int brightness) {
      int pindex = this.indices.get(index).x;
      int tindex = this.indices.get(index).z;
      Vector3f position = this.positions.get(pindex);
      Vector2f uv = this.texCoords.get(tindex);
      bufferbuilder.addVertex(position.x, position.y, position.z)
         .setColor(1.0F, 1.0F, 1.0F, 1.0F)
         .setUv(uv.x, uv.y)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(brightness)
         .setNormal(normal.x, normal.y, normal.z);
   }
}
