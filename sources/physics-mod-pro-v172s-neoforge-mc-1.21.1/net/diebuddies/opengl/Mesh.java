package net.diebuddies.opengl;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.diebuddies.physics.snow.math.AABB3D;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.lwjgl.system.MemoryUtil;

public class Mesh {
   public static final Map<Integer, VAO> uniqueMeshes = new Object2ObjectOpenHashMap();
   private Map<Data, Buffer> preallocatedBuffers;
   public Integer hashCode;
   private DataStorage storage;

   public Mesh(Mesh mesh) {
      this.storage = new DataStorage(mesh.storage);
   }

   public Mesh() {
      this.storage = new DataStorage();
   }

   public boolean hasCached() {
      VAO vao = null;
      if (this.hashCode != null) {
         vao = uniqueMeshes.get(this.hashCode);
         if (vao != null && vao.isDestroyed()) {
            vao = null;
         }
      }

      return vao != null;
   }

   public void preallocateBuffers() {
      this.preallocatedBuffers = new Object2ObjectOpenHashMap();

      for (Entry<Data, Object> entry : this.storage.getData().entrySet()) {
         Data type = entry.getKey();
         Object dataArray = entry.getValue();
         if (dataArray != null) {
            Buffer buffer = null;
            int size = this.storage.size(type);
            if (dataArray instanceof float[] data) {
               buffer = MemoryUtil.memAllocFloat(size);
               ((FloatBuffer)buffer).put(0, data, 0, size);
            } else if (dataArray instanceof int[] data) {
               buffer = MemoryUtil.memAllocInt(size);
               ((IntBuffer)buffer).put(0, data, 0, size);
            } else if (dataArray instanceof short[] data) {
               buffer = MemoryUtil.memAllocShort(size);
               ((ShortBuffer)buffer).put(0, data, 0, size);
            } else if (dataArray instanceof byte[] data) {
               buffer = MemoryUtil.memAlloc(size);
               ((ByteBuffer)buffer).put(0, data, 0, size);
            }

            this.preallocatedBuffers.put(type, buffer);
         }
      }
   }

   public void freePreallocatedBuffers() {
      for (Buffer dataArray : this.preallocatedBuffers.values()) {
         if (dataArray != null) {
            MemoryUtil.memFree(dataArray);
         }
      }
   }

   public VAO constructVAO(Usage usage) {
      VAO vao = null;
      if (this.hashCode != null) {
         vao = uniqueMeshes.get(this.hashCode);
         if (vao != null && vao.isDestroyed()) {
            vao = null;
         }
      }

      if (vao == null) {
         vao = new VAO(usage);
         if (this.preallocatedBuffers != null) {
            for (Entry<Data, Buffer> entry : this.preallocatedBuffers.entrySet()) {
               Data type = entry.getKey();
               Buffer dataArray = entry.getValue();
               if (type != Data.INDEX && dataArray != null) {
                  vao.attachAttribute(type, dataArray);
                  MemoryUtil.memFree(dataArray);
               }
            }

            IntBuffer indexBuffer = (IntBuffer)this.preallocatedBuffers.get(Data.INDEX);
            vao.finish(indexBuffer, this.storage.size(Data.INDEX));
            if (indexBuffer != null) {
               MemoryUtil.memFree(indexBuffer);
            }
         } else {
            for (Entry<Data, Object> entryx : this.storage.getData().entrySet()) {
               Data type = entryx.getKey();
               Object dataArray = entryx.getValue();
               if (type != Data.INDEX && dataArray != null) {
                  if (dataArray instanceof float[]) {
                     vao.attachAttribute(type, (float[])dataArray, this.storage.size(type));
                  } else if (dataArray instanceof int[]) {
                     vao.attachAttribute(type, (int[])dataArray, this.storage.size(type));
                  } else if (dataArray instanceof short[]) {
                     vao.attachAttribute(type, (short[])dataArray, this.storage.size(type));
                  } else if (dataArray instanceof byte[]) {
                     vao.attachAttribute(type, (byte[])dataArray, this.storage.size(type));
                  }
               }
            }

            vao.finish((int[])this.storage.getNative(Data.INDEX), this.storage.size(Data.INDEX));
         }

         if (this.hashCode != null) {
            uniqueMeshes.put(this.hashCode, vao);
         }
      } else {
         vao.increaseReferenceCounter();
      }

      return vao;
   }

   public AABB3D constructBoundingBox() {
      AABB3D result = new AABB3D(new Vector3d(1.7976931348623157E308), new Vector3d(-1.7976931348623157E308));
      Vector3d tmp = new Vector3d();
      float[] positions = (float[])this.storage.getNative(Data.POSITION);

      for (int i = 0; i < positions.length / 3; i++) {
         tmp.set(positions[i * 3], positions[i * 3 + 1], positions[i * 3 + 2]);
         result.start.min(tmp);
         result.end.max(tmp);
      }

      return result;
   }

   public static Vector3d calculateCenter(List<Mesh> meshes, DoubleList volumes) {
      Vector3d center = new Vector3d();
      double totalVolume = 0.0;

      for (int i = 0; i < volumes.size(); i++) {
         totalVolume += volumes.getDouble(i);
      }

      for (int i = 0; i < meshes.size(); i++) {
         Mesh mesh = meshes.get(i);
         int count = 0;
         float[] positions = (float[])mesh.getNative(Data.POSITION);
         Vector3d meshCenter = new Vector3d();
         if (positions != null) {
            for (int j = 0; j < positions.length / 3; j++) {
               meshCenter.add(positions[j * 3], positions[j * 3 + 1], positions[j * 3 + 2]);
               count++;
            }
         }

         meshCenter.mul(1.0 / count);
         center.add(meshCenter.mul(volumes.getDouble(i) / totalVolume));
      }

      return center;
   }

   public static Vector3d calculateCenter(List<Mesh> meshes) {
      Vector3d center = new Vector3d();
      int count = 0;

      for (Mesh mesh : meshes) {
         float[] positions = (float[])mesh.getNative(Data.POSITION);
         if (positions != null) {
            for (int i = 0; i < positions.length / 3; i++) {
               center.add(positions[i * 3], positions[i * 3 + 1], positions[i * 3 + 2]);
               count++;
            }
         }
      }

      return center.mul(1.0 / count);
   }

   public void transform(Matrix4d transformation) {
      float[] positions = (float[])this.getNative(Data.POSITION);
      Vector3d tmp = new Vector3d();
      if (positions != null) {
         for (int i = 0; i < positions.length / 3; i++) {
            transformation.transformPosition(positions[i * 3], positions[i * 3 + 1], positions[i * 3 + 2], tmp);
            positions[i * 3] = (float)tmp.x;
            positions[i * 3 + 1] = (float)tmp.y;
            positions[i * 3 + 2] = (float)tmp.z;
         }
      }
   }

   public VAO constructVAO() {
      return this.constructVAO(Usage.STATIC);
   }

   public int size(Data type) {
      return this.storage.size(type);
   }

   public void setSize(Data type, int size) {
      this.storage.setSize(type, size);
   }

   public Object getNative(Data type) {
      return this.storage.getNative(type);
   }

   public void set(byte[] data, Data type) {
      if (data != null && data.length != 0) {
         this.storage.set(data, type);
      }
   }

   public void set(short[] data, Data type) {
      if (data != null && data.length != 0) {
         this.storage.set(data, type);
      }
   }

   public void set(float[] data, Data type) {
      if (data != null && data.length != 0) {
         this.storage.set(data, type);
      }
   }

   public void set(double[] data, Data type) {
      if (data != null && data.length != 0) {
         this.storage.set(data, type);
      }
   }

   public void set(int[] data, Data type) {
      if (data != null && data.length != 0) {
         this.storage.set(data, type);
      }
   }

   public DataStorage getStorage() {
      return this.storage;
   }

   public static void destroyStoredVAOs() {
      for (VAO vao : uniqueMeshes.values()) {
         vao.destroy();
      }

      uniqueMeshes.clear();
   }
}
