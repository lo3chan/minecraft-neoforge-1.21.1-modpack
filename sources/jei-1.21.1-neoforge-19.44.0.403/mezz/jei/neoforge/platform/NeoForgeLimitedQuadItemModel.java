package mezz.jei.neoforge.platform;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.common.util.QuadUtil;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

public class NeoForgeLimitedQuadItemModel extends BakedModelWrapper<BakedModel> {
   @Nullable
   private List<BakedQuad> quads;

   public static BakedModel wrap(BakedModel model) {
      return (BakedModel)(!(model instanceof IDynamicBakedModel) && !(model instanceof NeoForgeLimitedQuadItemModel)
         ? new NeoForgeLimitedQuadItemModel(model)
         : model);
   }

   private NeoForgeLimitedQuadItemModel(BakedModel originalModel) {
      super(originalModel);
   }

   @Deprecated
   public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource) {
      if (direction == null) {
         if (this.quads == null) {
            List<BakedQuad> originalQuads = this.originalModel.getQuads(blockState, null, randomSource);
            PoseStack poseStack = new PoseStack();
            BakedModel bakedModel = this.originalModel.applyTransform(ItemDisplayContext.GUI, poseStack, false);
            if (bakedModel != this.originalModel) {
               return originalQuads;
            }

            this.quads = QuadUtil.getQuadsFacingDirection(originalQuads, poseStack, Direction.SOUTH);
            if (this.quads.isEmpty()) {
               this.quads = originalQuads;
            }
         }

         return this.quads;
      } else {
         return this.originalModel.getQuads(blockState, direction, randomSource);
      }
   }

   public List<BakedQuad> getQuads(
      @Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource, ModelData extraData, @Nullable RenderType renderType
   ) {
      if (direction == null) {
         if (this.quads == null) {
            List<BakedQuad> originalQuads = this.originalModel.getQuads(blockState, null, randomSource, extraData, renderType);
            PoseStack poseStack = new PoseStack();
            BakedModel bakedModel = this.originalModel.applyTransform(ItemDisplayContext.GUI, poseStack, false);
            if (bakedModel != this.originalModel) {
               return originalQuads;
            }

            this.quads = QuadUtil.getQuadsFacingDirection(originalQuads, poseStack, Direction.SOUTH);
            if (this.quads.isEmpty()) {
               this.quads = originalQuads;
            }
         }

         return this.quads;
      } else {
         return List.of();
      }
   }

   public BakedModel applyTransform(ItemDisplayContext cameraTransformType, PoseStack poseStack, boolean applyLeftHandTransform) {
      BakedModel model = super.applyTransform(cameraTransformType, poseStack, applyLeftHandTransform);
      return (BakedModel)(model == this.originalModel ? this : model);
   }

   public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous) {
      List<BakedModel> renderPasses = super.getRenderPasses(itemStack, fabulous);
      List<BakedModel> result = new ArrayList<>(renderPasses.size());

      for (BakedModel bakedModel : renderPasses) {
         if (bakedModel == this.originalModel) {
            bakedModel = this;
         }

         result.add(bakedModel);
      }

      return result;
   }
}
