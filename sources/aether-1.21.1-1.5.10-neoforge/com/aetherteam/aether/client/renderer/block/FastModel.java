package com.aetherteam.aether.client.renderer.block;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

public class FastModel extends BakedModelWrapper<BakedModel> {
   public FastModel(BakedModel originalModel) {
      super(originalModel);
   }

   public List<BakedQuad> getQuads(
      @Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType
   ) {
      return super.getQuads(state, side, rand, extraData, null);
   }

   public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
      return ChunkRenderTypeSet.of(new RenderType[]{Minecraft.useFancyGraphics() ? RenderType.cutoutMipped() : RenderType.solid()});
   }
}
