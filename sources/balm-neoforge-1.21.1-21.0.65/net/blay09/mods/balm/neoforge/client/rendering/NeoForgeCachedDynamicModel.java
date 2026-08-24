package net.blay09.mods.balm.neoforge.client.rendering;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.blay09.mods.balm.common.client.rendering.AbstractCachedDynamicModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.ModelBakery.TextureGetter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class NeoForgeCachedDynamicModel extends AbstractCachedDynamicModel {
   private final List<RenderType> renderTypes;
   private ChunkRenderTypeSet cachedChunkRenderTypeSet;

   public NeoForgeCachedDynamicModel(
      ModelBakery modelBakery,
      Map<ModelResourceLocation, UnbakedModel> models,
      Function<BlockState, ModelResourceLocation> baseModelFunction,
      @Nullable List<Pair<Predicate<BlockState>, BakedModel>> parts,
      @Nullable Function<BlockState, Map<String, String>> textureMapFunction,
      @Nullable BiConsumer<BlockState, Matrix4f> transformFunction,
      List<RenderType> renderTypes,
      ModelResourceLocation location,
      TextureGetter textureGetter
   ) {
      super(modelBakery, models, baseModelFunction, parts, textureMapFunction, transformFunction, renderTypes, location, textureGetter);
      this.renderTypes = renderTypes;
   }

   @Override
   public List<RenderType> getItemRenderTypes(ItemStack itemStack, boolean fabulous) {
      return this.renderTypes;
   }

   public List<RenderType> getRenderTypes(ItemStack itemStack, boolean fabulous) {
      List<RenderType> result = this.getItemRenderTypes(itemStack, fabulous);
      return result.isEmpty() ? super.getRenderTypes(itemStack, fabulous) : result;
   }

   @Override
   public List<RenderType> getBlockRenderTypes(BlockState state, RandomSource rand) {
      return this.renderTypes;
   }

   public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
      if (this.cachedChunkRenderTypeSet == null) {
         List<RenderType> result = this.getBlockRenderTypes(state, rand);
         if (!result.isEmpty()) {
            this.cachedChunkRenderTypeSet = ChunkRenderTypeSet.of(result);
         } else {
            this.cachedChunkRenderTypeSet = super.getRenderTypes(state, rand, data);
         }
      }

      return this.cachedChunkRenderTypeSet;
   }
}
