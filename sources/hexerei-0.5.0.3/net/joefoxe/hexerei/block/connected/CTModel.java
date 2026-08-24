package net.joefoxe.hexerei.block.connected;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.client.model.data.ModelData.Builder;

public class CTModel extends BakedModelWrapperWithData {
   private static final ModelProperty<CTModel.CTData> CT_PROPERTY = new ModelProperty();
   private final ConnectedTextureBehaviour behaviour;

   public CTModel(BakedModel originalModel, ConnectedTextureBehaviour behaviour) {
      super(originalModel);
      this.behaviour = behaviour;
   }

   @Override
   protected Builder gatherModelData(Builder builder, BlockAndTintGetter world, BlockPos pos, BlockState state, ModelData blockEntityData) {
      return builder.with(CT_PROPERTY, this.createCTData(world, pos, state));
   }

   protected CTModel.CTData createCTData(BlockAndTintGetter world, BlockPos pos, BlockState state) {
      CTModel.CTData data = new CTModel.CTData();
      MutableBlockPos mutablePos = new MutableBlockPos();

      for (Direction face : Direction.values()) {
         BlockState actualState = world.getBlockState(pos);
         if (this.behaviour.buildContextForOccludedDirections() || Block.shouldRenderFace(state, world, pos, face, mutablePos.setWithOffset(pos, face))) {
            CTType dataType = this.behaviour.getDataType(world, pos, state, face);
            if (dataType != null) {
               ConnectedTextureBehaviour.CTContext context = this.behaviour.buildContext(world, pos, state, face, dataType.getContextRequirement());
               data.put(face, dataType.getTextureIndex(context));
            }
         }
      }

      return data;
   }

   public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData extraData, RenderType renderType) {
      List<BakedQuad> quads = super.getQuads(state, side, rand, extraData, renderType);
      if (!extraData.has(CT_PROPERTY)) {
         return quads;
      } else {
         CTModel.CTData data = (CTModel.CTData)extraData.get(CT_PROPERTY);
         List<BakedQuad> var18 = new ArrayList<>(quads);

         for (int i = 0; i < var18.size(); i++) {
            BakedQuad quad = (BakedQuad)var18.get(i);
            int index = data.get(quad.getDirection());
            if (index != -1) {
               CTSpriteShiftEntry spriteShift = this.behaviour.getShift(state, quad.getDirection(), quad.getSprite());
               boolean hasTransparent = this.behaviour instanceof ConnectedTextureTransparentLayer;
               if (spriteShift != null && (quad.getSprite() == spriteShift.getOriginal() || hasTransparent)) {
                  if (this.behaviour instanceof ConnectedTextureTransparentLayer transparentLayer) {
                     CTSpriteShiftEntry transparentShift = transparentLayer.getTransparentShift(state, quad.getDirection(), quad.getSprite());
                     if (transparentShift == null) {
                        continue;
                     }

                     if (quad.getSprite() == transparentShift.getOriginal()) {
                        spriteShift = transparentShift;
                     } else if (quad.getSprite() != spriteShift.getOriginal()) {
                        continue;
                     }
                  }

                  BakedQuad newQuad = BakedQuadHelper.clone(quad);
                  int[] vertexData = newQuad.getVertices();

                  for (int vertex = 0; vertex < 4; vertex++) {
                     float u = BakedQuadHelper.getU(vertexData, vertex);
                     float v = BakedQuadHelper.getV(vertexData, vertex);
                     BakedQuadHelper.setU(vertexData, vertex, spriteShift.getTargetU(u, index));
                     BakedQuadHelper.setV(vertexData, vertex, spriteShift.getTargetV(v, index));
                  }

                  var18.set(i, newQuad);
               }
            }
         }

         return var18;
      }
   }

   private static class CTData {
      private final int[] indices = new int[6];

      public CTData() {
         Arrays.fill(this.indices, -1);
      }

      public void put(Direction face, int texture) {
         this.indices[face.get3DDataValue()] = texture;
      }

      public int get(Direction face) {
         return this.indices[face.get3DDataValue()];
      }
   }
}
