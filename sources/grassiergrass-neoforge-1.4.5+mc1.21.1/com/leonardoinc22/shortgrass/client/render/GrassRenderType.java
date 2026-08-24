package com.leonardoinc22.shortgrass.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

public final class GrassRenderType extends RenderType {
   private static final ResourceLocation BLADE_TEXTURE = ResourceLocation.fromNamespaceAndPath("grassiergrass", "textures/block/blade.png");
   private static ShaderInstance grassShader;
   private static ShaderInstance plantShader;
   private static final ShaderStateShard GRASS_SHADER_SHARD = new ShaderStateShard(GrassRenderType::getGrassShader);
   private static final ShaderStateShard PLANT_SHADER_SHARD = new ShaderStateShard(GrassRenderType::getPlantShader);
   private static final RenderType GRASS_BLADES = create(
      "grassiergrass_blades",
      DefaultVertexFormat.BLOCK,
      Mode.QUADS,
      1536,
      false,
      false,
      CompositeState.builder()
         .setShaderState(GRASS_SHADER_SHARD)
         .setTextureState(new TextureStateShard(BLADE_TEXTURE, false, false))
         .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
         .setCullState(NO_CULL)
         .setLightmapState(LIGHTMAP)
         .createCompositeState(true)
   );
   private static final RenderType IRIS_TERRAIN_BLADES = create(
      "grassiergrass_iris_terrain_blades",
      DefaultVertexFormat.BLOCK,
      Mode.QUADS,
      1536,
      true,
      false,
      CompositeState.builder()
         .setShaderState(RENDERTYPE_CUTOUT_SHADER)
         .setTextureState(new TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, false))
         .setCullState(NO_CULL)
         .setLightmapState(LIGHTMAP)
         .createCompositeState(true)
   );
   private static final RenderType IRIS_TERRAIN_PLANTS = create(
      "grassiergrass_iris_terrain_plants",
      DefaultVertexFormat.BLOCK,
      Mode.QUADS,
      1536,
      true,
      false,
      CompositeState.builder()
         .setShaderState(RENDERTYPE_CUTOUT_SHADER)
         .setTextureState(new TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, false))
         .setLightmapState(LIGHTMAP)
         .createCompositeState(true)
   );
   private static final RenderType GRASS_PLANTS = create(
      "grassiergrass_plants",
      DefaultVertexFormat.BLOCK,
      Mode.QUADS,
      1536,
      false,
      false,
      CompositeState.builder()
         .setShaderState(PLANT_SHADER_SHARD)
         .setTextureState(new TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, false))
         .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
         .setLightmapState(LIGHTMAP)
         .createCompositeState(true)
   );

   private GrassRenderType(
      String name, VertexFormat format, Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState
   ) {
      super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
   }

   public static RenderType grassBlades() {
      return GRASS_BLADES;
   }

   public static RenderType grassPlants() {
      return GRASS_PLANTS;
   }

   public static RenderType irisTerrainBlades() {
      return IRIS_TERRAIN_BLADES;
   }

   public static RenderType irisTerrainPlants() {
      return IRIS_TERRAIN_PLANTS;
   }

   public static void setGrassShader(ShaderInstance shader) {
      grassShader = shader;
   }

   public static ShaderInstance getGrassShader() {
      return grassShader;
   }

   public static void setPlantShader(ShaderInstance shader) {
      plantShader = shader;
   }

   public static ShaderInstance getPlantShader() {
      return plantShader;
   }
}
