package com.leonardoinc22.shortgrass.client.render.iris;

import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class IrisCompat {
   private static final String[] API_CLASS_NAMES = new String[]{"net.irisshaders.iris.api.v0.IrisApi", "net.coderbot.iris.api.v0.IrisApi"};
   private static boolean initialized;
   private static Object apiInstance;
   private static Method isShaderPackInUseMethod;
   private static Method isRenderingShadowPassMethod;
   private static boolean terrainMetadataInitialized;
   private static Class<?> blockSensitiveBufferBuilderClass;
   private static Method beginBlockMethod;
   private static Method endBlockMethod;
   private static Object worldRenderingSettingsInstance;
   private static Method getBlockStateIdsMethod;

   private IrisCompat() {
   }

   public static boolean isShaderPackInUse() {
      return invokeBool(isShaderPackInUseMethod);
   }

   public static boolean isRenderingShadowPass() {
      return invokeBool(isRenderingShadowPassMethod);
   }

   public static void beginTerrainBlock(VertexConsumer consumer, BlockState state, int localX, int localY, int localZ) {
      initializeTerrainMetadata();
      if (blockSensitiveBufferBuilderClass != null && blockSensitiveBufferBuilderClass.isInstance(consumer)) {
         try {
            beginBlockMethod.invoke(consumer, terrainBlockId(state), (byte)0, (byte)state.getLightEmission(), localX, localY, localZ);
         } catch (Throwable var6) {
            disableTerrainMetadata();
         }
      }
   }

   public static void endTerrainBlock(VertexConsumer consumer) {
      initializeTerrainMetadata();
      if (blockSensitiveBufferBuilderClass != null && blockSensitiveBufferBuilderClass.isInstance(consumer)) {
         try {
            endBlockMethod.invoke(consumer);
         } catch (Throwable var2) {
            disableTerrainMetadata();
         }
      }
   }

   private static boolean invokeBool(Method method) {
      if (!initialized) {
         initialize();
      }

      if (apiInstance != null && method != null) {
         try {
            return (Boolean)method.invoke(apiInstance);
         } catch (Throwable var2) {
            return false;
         }
      } else {
         return false;
      }
   }

   private static void initialize() {
      initialized = true;

      for (String className : API_CLASS_NAMES) {
         try {
            Class<?> apiClass = Class.forName(className);
            apiInstance = apiClass.getMethod("getInstance").invoke(null);
            isShaderPackInUseMethod = apiClass.getMethod("isShaderPackInUse");

            try {
               isRenderingShadowPassMethod = apiClass.getMethod("isRenderingShadowPass");
            } catch (Throwable var6) {
               isRenderingShadowPassMethod = null;
            }

            return;
         } catch (Throwable var7) {
         }
      }
   }

   private static void initializeTerrainMetadata() {
      if (!terrainMetadataInitialized) {
         terrainMetadataInitialized = true;

         try {
            blockSensitiveBufferBuilderClass = Class.forName("net.irisshaders.iris.vertices.BlockSensitiveBufferBuilder");
            beginBlockMethod = blockSensitiveBufferBuilderClass.getMethod("beginBlock", int.class, byte.class, byte.class, int.class, int.class, int.class);
            endBlockMethod = blockSensitiveBufferBuilderClass.getMethod("endBlock");
         } catch (Throwable var3) {
            disableTerrainMetadata();
            return;
         }

         try {
            Class<?> settingsClass = Class.forName("net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings");
            Field instanceField = settingsClass.getField("INSTANCE");
            worldRenderingSettingsInstance = instanceField.get(null);
            getBlockStateIdsMethod = settingsClass.getMethod("getBlockStateIds");
         } catch (Throwable var2) {
            worldRenderingSettingsInstance = null;
            getBlockStateIdsMethod = null;
         }
      }
   }

   private static int terrainBlockId(BlockState state) {
      if (worldRenderingSettingsInstance != null && getBlockStateIdsMethod != null) {
         try {
            if (getBlockStateIdsMethod.invoke(worldRenderingSettingsInstance) instanceof Object2IntMap<?> idMap) {
               int id = idMap.getOrDefault(state, -1);
               if (id != -1) {
                  return id;
               }
            }
         } catch (Throwable var4) {
            worldRenderingSettingsInstance = null;
            getBlockStateIdsMethod = null;
         }
      }

      return legacyFoliageBlockId(state);
   }

   private static int legacyFoliageBlockId(BlockState state) {
      if (state.is(Blocks.SHORT_GRASS) || state.is(Blocks.FERN)) {
         return 31;
      } else {
         return !state.is(Blocks.TALL_GRASS) && !state.is(Blocks.LARGE_FERN) ? -1 : 175;
      }
   }

   private static void disableTerrainMetadata() {
      blockSensitiveBufferBuilderClass = null;
      beginBlockMethod = null;
      endBlockMethod = null;
      worldRenderingSettingsInstance = null;
      getBlockStateIdsMethod = null;
   }
}
