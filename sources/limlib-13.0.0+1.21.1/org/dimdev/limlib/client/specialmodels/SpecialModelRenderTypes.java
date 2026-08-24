package org.dimdev.limlib.client.specialmodels;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard.LayeringStateShard;
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.limlib.client.specialmodels.compat.iris.IrisCompat;
import org.jetbrains.annotations.Nullable;

public final class SpecialModelRenderTypes {
   private static final LayeringStateShard SPECIAL_MODEL_LAYERING = new LayeringStateShard("limlib_special_model_layering", () -> {
      RenderSystem.polygonOffset(1.0F, 10.0F);
      RenderSystem.enablePolygonOffset();
   }, () -> {
      RenderSystem.polygonOffset(0.0F, 0.0F);
      RenderSystem.disablePolygonOffset();
   });
   private static final Map<ResourceLocation, RenderType> specialModelRenderTypes = new LinkedHashMap<>();
   private static volatile List<RenderType> chunkBufferLayers = List.of();

   public static synchronized void clearSpecialModelRenderTypes() {
      specialModelRenderTypes.clear();
      chunkBufferLayers = List.of();
   }

   @Nullable
   public static synchronized RenderType getOrCreateSpecialModelRenderType(ResourceLocation id) {
      RenderType existing = specialModelRenderTypes.get(id);
      if (existing != null) {
         return existing;
      } else if (!SpecialModelShaderRegistry.isRegistered(id)) {
         return null;
      } else {
         RenderType created = createSpecialModelRenderType(id);
         specialModelRenderTypes.put(id, created);
         chunkBufferLayers = List.copyOf(specialModelRenderTypes.values());
         return created;
      }
   }

   public static boolean isSpecialModelRenderType(RenderType renderType) {
      return !IrisCompat.shouldDisableSpecialModelRenderTypes() && isKnownSpecialModelRenderType(renderType);
   }

   public static boolean isKnownSpecialModelRenderType(RenderType renderType) {
      return chunkBufferLayers.contains(renderType);
   }

   public static List<RenderType> chunkBufferLayers() {
      return IrisCompat.shouldDisableSpecialModelRenderTypes() ? List.of() : chunkBufferLayers;
   }

   private static RenderType createSpecialModelRenderType(ResourceLocation rendererId) {
      ShaderStateShard shader = new ShaderStateShard(() -> SpecialModelShaderRegistry.getShader(rendererId));
      VertexFormat vertexFormat = SpecialModelShaderRegistry.getVertexFormat(rendererId);
      return RenderType.create(
         "limlib_special_model_" + rendererId.toString().replace(':', '_').replace('/', '_'),
         vertexFormat,
         Mode.QUADS,
         4194304,
         true,
         true,
         CompositeState.builder()
            .setLightmapState(RenderStateShard.LIGHTMAP)
            .setShaderState(shader)
            .setTextureState(RenderStateShard.BLOCK_SHEET_MIPPED)
            .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
            .setOutputState(RenderStateShard.MAIN_TARGET)
            .setCullState(RenderStateShard.NO_CULL)
            .setLayeringState(SPECIAL_MODEL_LAYERING)
            .createCompositeState(true)
      );
   }

   private SpecialModelRenderTypes() {
   }
}
