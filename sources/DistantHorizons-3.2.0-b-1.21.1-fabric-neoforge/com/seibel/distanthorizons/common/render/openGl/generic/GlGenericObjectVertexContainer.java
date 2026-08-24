package com.seibel.distanthorizons.common.render.openGl.generic;

import com.seibel.distanthorizons.api.objects.render.DhApiRenderableBox;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftGLWrapper;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.util.LodUtil;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.objects.IDhGenericObjectVertexBufferContainer;
import java.awt.Color;
import java.util.List;
import org.lwjgl.opengl.GL33;

public class GlGenericObjectVertexContainer implements IDhGenericObjectVertexBufferContainer {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final MinecraftGLWrapper GLMC = MinecraftGLWrapper.INSTANCE;
   public int chunkPos = 0;
   public int subChunkPos = 0;
   public int scale = 0;
   public int color = 0;
   public int material = 0;
   public int[] chunkPosData = new int[0];
   public float[] subChunkPosData = new float[0];
   public float[] scalingData = new float[0];
   public float[] colorData = new float[0];
   public int[] materialData = new int[0];
   public int uploadedBoxCount = 0;
   private IDhGenericObjectVertexBufferContainer.EState state = IDhGenericObjectVertexBufferContainer.EState.NEW;

   @Override
   public IDhGenericObjectVertexBufferContainer.EState getState() {
      return this.state;
   }

   @Override
   public void setState(IDhGenericObjectVertexBufferContainer.EState state) {
      this.state = state;
   }

   @Override
   public void updateVertexData(List<DhApiRenderableBox> uploadBoxList) {
      int boxCount = uploadBoxList.size();
      if (this.uploadedBoxCount != boxCount) {
         this.uploadedBoxCount = boxCount;
         this.chunkPosData = new int[boxCount * 3];
         this.subChunkPosData = new float[boxCount * 3];
         this.scalingData = new float[boxCount * 3];
         this.colorData = new float[boxCount * 4];
         this.materialData = new int[boxCount];
      }

      for (int i = 0; i < boxCount; i++) {
         DhApiRenderableBox box = uploadBoxList.get(i);
         int dataIndex = i * 3;
         this.chunkPosData[dataIndex] = LodUtil.getChunkPosFromDouble(box.minPos.x);
         this.chunkPosData[dataIndex + 1] = LodUtil.getChunkPosFromDouble(box.minPos.y);
         this.chunkPosData[dataIndex + 2] = LodUtil.getChunkPosFromDouble(box.minPos.z);
         this.subChunkPosData[dataIndex] = LodUtil.getSubChunkPosFromDouble(box.minPos.x);
         this.subChunkPosData[dataIndex + 1] = LodUtil.getSubChunkPosFromDouble(box.minPos.y);
         this.subChunkPosData[dataIndex + 2] = LodUtil.getSubChunkPosFromDouble(box.minPos.z);
         this.scalingData[dataIndex] = (float)(box.maxPos.x - box.minPos.x);
         this.scalingData[dataIndex + 1] = (float)(box.maxPos.y - box.minPos.y);
         this.scalingData[dataIndex + 2] = (float)(box.maxPos.z - box.minPos.z);
      }

      for (int i = 0; i < boxCount; i++) {
         DhApiRenderableBox box = uploadBoxList.get(i);
         Color color = box.color;
         int colorIndex = i * 4;
         this.colorData[colorIndex] = color.getRed() / 255.0F;
         this.colorData[colorIndex + 1] = color.getGreen() / 255.0F;
         this.colorData[colorIndex + 2] = color.getBlue() / 255.0F;
         this.colorData[colorIndex + 3] = color.getAlpha() / 255.0F;
         this.materialData[i] = box.material;
      }
   }

   @Override
   public void uploadDataToGpu() {
      this.tryCreateBuffers();
      GL33.glBindBuffer(34962, this.chunkPos);
      GL33.glBufferData(34962, this.chunkPosData, 35048);
      GL33.glBindBuffer(34962, this.subChunkPos);
      GL33.glBufferData(34962, this.subChunkPosData, 35048);
      GL33.glBindBuffer(34962, this.scale);
      GL33.glBufferData(34962, this.scalingData, 35048);
      GL33.glBindBuffer(34962, this.color);
      GL33.glBufferData(34962, this.colorData, 35048);
      GL33.glBindBuffer(34962, this.material);
      GL33.glBufferData(34962, this.materialData, 35048);
   }

   private void tryCreateBuffers() {
      if (this.chunkPos == 0) {
         this.chunkPos = GLMC.glGenBuffers();
         this.subChunkPos = GLMC.glGenBuffers();
         this.scale = GLMC.glGenBuffers();
         this.color = GLMC.glGenBuffers();
         this.material = GLMC.glGenBuffers();
      }
   }

   @Override
   public void close() {
      tryDeleteBuffer(this.chunkPos);
      tryDeleteBuffer(this.subChunkPos);
      tryDeleteBuffer(this.scale);
      tryDeleteBuffer(this.color);
      tryDeleteBuffer(this.material);
   }

   private static void tryDeleteBuffer(int bufferId) {
      if (bufferId != 0) {
         GLMC.glDeleteBuffers(bufferId);
      }
   }
}
