package com.leonardoinc22.shortgrass.neoforge;

import com.leonardoinc22.shortgrass.client.render.HiddenGrass;
import com.leonardoinc22.shortgrass.client.render.HiddenModelScope;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.event.ModelEvent.ModifyBakingResult;
import net.neoforged.neoforge.client.model.data.ModelData;

public final class HiddenBakedModel implements BakedModel {
   private final ResourceLocation blockId;
   private final BakedModel wrapped;

   public HiddenBakedModel(ResourceLocation blockId, BakedModel wrapped) {
      this.blockId = blockId;
      this.wrapped = wrapped;
   }

   public static void onModifyBakingResult(ModifyBakingResult event) {
      HiddenGrass.clearOriginalModels();
      event.getModels().replaceAll((location, model) -> {
         if (HiddenGrass.canBeHidden(location) && !(model instanceof HiddenBakedModel)) {
            HiddenGrass.rememberOriginalModel(location, model);
            return new HiddenBakedModel(location.id(), model);
         } else {
            return (BakedModel)model;
         }
      });
   }

   public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random) {
      return !HiddenModelScope.shouldExposeHiddenModels() && HiddenGrass.shouldHide(this.blockId) ? List.of() : this.wrapped.getQuads(state, side, random);
   }

   public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random, ModelData modelData, RenderType renderType) {
      return !HiddenModelScope.shouldExposeHiddenModels() && HiddenGrass.shouldHide(this.blockId)
         ? List.of()
         : this.wrapped.getQuads(state, side, random, modelData, renderType);
   }

   public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random, ModelData modelData) {
      return !HiddenModelScope.shouldExposeHiddenModels() && HiddenGrass.shouldHide(this.blockId)
         ? ChunkRenderTypeSet.none()
         : this.wrapped.getRenderTypes(state, random, modelData);
   }

   public boolean useAmbientOcclusion() {
      return this.wrapped.useAmbientOcclusion();
   }

   public boolean isGui3d() {
      return this.wrapped.isGui3d();
   }

   public boolean usesBlockLight() {
      return this.wrapped.usesBlockLight();
   }

   public boolean isCustomRenderer() {
      return this.wrapped.isCustomRenderer();
   }

   public TextureAtlasSprite getParticleIcon() {
      return this.wrapped.getParticleIcon();
   }

   public TextureAtlasSprite getParticleIcon(ModelData modelData) {
      return this.wrapped.getParticleIcon(modelData);
   }

   public ItemTransforms getTransforms() {
      return this.wrapped.getTransforms();
   }

   public ItemOverrides getOverrides() {
      return this.wrapped.getOverrides();
   }
}
