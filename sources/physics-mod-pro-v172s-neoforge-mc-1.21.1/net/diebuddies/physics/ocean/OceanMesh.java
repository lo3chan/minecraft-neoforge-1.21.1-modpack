package net.diebuddies.physics.ocean;

import javax.annotation.Nullable;
import net.diebuddies.opengl.ArenaBuffer;
import net.diebuddies.opengl.RawMesh;
import net.diebuddies.opengl.Texture;
import net.diebuddies.physics.snow.math.AABB3D;
import org.joml.Matrix4d;
import org.joml.Vector3d;

public class OceanMesh {
   @Nullable
   public RawMesh mesh;
   @Nullable
   public ArenaBuffer.MemorySegment vertexSegment;
   @Nullable
   public ArenaBuffer.MemorySegment indexSegment;
   @Nullable
   public Matrix4d transformation;
   @Nullable
   public byte[] textureData;
   public int width;
   public int height;
   public int offsetX;
   public int offsetZ;
   public int textureOffsetX;
   public int textureOffsetZ;
   @Nullable
   public Texture texture;
   @Nullable
   public AABB3D aabb;
   public float maxInfluence;

   public OceanMesh(
      RawMesh mesh,
      Matrix4d transformation,
      byte[] texture,
      float maxInfluence,
      int width,
      int height,
      int offsetX,
      int offsetZ,
      int textureOffsetX,
      int textureOffsetZ
   ) {
      this.mesh = mesh;
      this.transformation = transformation;
      this.textureData = texture;
      this.width = width;
      this.height = height;
      this.offsetX = offsetX;
      this.offsetZ = offsetZ;
      this.textureOffsetX = textureOffsetX;
      this.textureOffsetZ = textureOffsetZ;
      this.maxInfluence = maxInfluence;
      Vector3d start = new Vector3d(textureOffsetX, 0.0, textureOffsetZ);
      this.aabb = new AABB3D(start, new Vector3d(width + 1, 0.0, height + 1).add(start));
      this.transformation.transformAab(this.aabb.getMin(), this.aabb.getMax(), this.aabb.getMin(), this.aabb.getMax());
   }

   public OceanMesh() {
   }

   public void createGLObjects(OceanWorld oceanWorld, OceanMesh oldMesh) {
      if (oldMesh != null) {
         if (oldMesh.vertexSegment != null) {
            oldMesh.vertexSegment.free();
            oldMesh.indexSegment.free();
         }

         this.vertexSegment = oceanWorld.getOceanVertexData().uploadData(this.mesh.data);
         this.indexSegment = oceanWorld.getOceanIndexData().uploadData(this.mesh.indexData);
         this.mesh.destroy();
         if (this.textureData.length == oldMesh.textureData.length && this.width == oldMesh.width && this.height == oldMesh.height) {
            this.texture = oldMesh.texture;
            if (this.textureData.length != 1) {
               this.texture.updateTexture(this.textureData);
            }
         } else {
            oldMesh.destroyTexture();
            if (this.textureData.length == 1) {
               this.texture = Texture.createColoredTexture(this.textureData, 1, 1, Texture.FILTER_LOAD_NO_MIPMAP_2D_TEXTURE);
            } else {
               this.texture = Texture.createColoredTexture(this.textureData, this.width, this.height, Texture.FILTER_LOAD_NO_MIPMAP_2D_TEXTURE);
            }
         }
      } else {
         this.vertexSegment = oceanWorld.getOceanVertexData().uploadData(this.mesh.data);
         this.indexSegment = oceanWorld.getOceanIndexData().uploadData(this.mesh.indexData);
         this.mesh.destroy();
         if (this.textureData.length == 1) {
            this.texture = Texture.createColoredTexture(this.textureData, 1, 1, Texture.FILTER_LOAD_NO_MIPMAP_2D_TEXTURE);
         } else {
            this.texture = Texture.createColoredTexture(this.textureData, this.width, this.height, Texture.FILTER_LOAD_NO_MIPMAP_2D_TEXTURE);
         }
      }
   }

   public void destroy(OceanWorld oceanWorld) {
      if (this.vertexSegment != null) {
         this.vertexSegment.free();
      }

      if (this.indexSegment != null) {
         this.indexSegment.free();
      }

      if (this.mesh != null) {
         this.mesh.destroy();
      }

      this.destroyTexture();
   }

   public void destroyTexture() {
      if (this.texture != null) {
         this.texture.destroy();
      }
   }
}
