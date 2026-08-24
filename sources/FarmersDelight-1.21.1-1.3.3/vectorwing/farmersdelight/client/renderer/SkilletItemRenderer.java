package vectorwing.farmersdelight.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.IArmPoseTransformer;
import vectorwing.farmersdelight.common.item.component.ItemStackWrapper;
import vectorwing.farmersdelight.common.registry.ModDataComponents;

public class SkilletItemRenderer extends BlockEntityWithoutLevelRenderer {
   public SkilletItemRenderer() {
      super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
   }

   public void renderByItem(
      ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay
   ) {
      BlockItem item = (BlockItem)stack.getItem();
      BlockState state = item.getBlock().defaultBlockState();
      Minecraft mc = Minecraft.getInstance();
      ItemStackWrapper stackWrapper = (ItemStackWrapper)stack.getOrDefault(
         (DataComponentType)ModDataComponents.SKILLET_INGREDIENT.get(), ItemStackWrapper.EMPTY
      );
      ItemStack ingredientStack = stackWrapper.getStack();
      float animation = 0.0F;
      if (!ingredientStack.isEmpty()) {
         poseStack.pushPose();
         poseStack.translate(0.5, 0.0625, 0.5);
         long gameTime = mc.level.getGameTime();
         if (stack.has((DataComponentType)ModDataComponents.SKILLET_FLIP_TIMESTAMP.get()) && displayContext != ItemDisplayContext.GUI) {
            long time = (Long)stack.get((DataComponentType)ModDataComponents.SKILLET_FLIP_TIMESTAMP.get());
            float partialTicks = mc.getTimer().getGameTimeDeltaPartialTick(false);
            float var20 = ((float)(gameTime - time) + partialTicks) / 12.0F;
            animation = Mth.clamp(var20, 0.0F, 1.0F);
            float maxH = 0.4F;
            poseStack.translate(0.0F, maxH * Mth.sin(animation * 3.1415927F), 0.0F);
            float rotationAnimation = stack.getOrDefault((DataComponentType)ModDataComponents.SKILLET_FLIPPED.get(), false) ? animation + 1.0F : animation;
            poseStack.mulPose(Axis.XP.rotationDegrees(180.0F * rotationAnimation));
         } else {
            poseStack.mulPose(Axis.XP.rotationDegrees(stack.getOrDefault((DataComponentType)ModDataComponents.SKILLET_FLIPPED.get(), false) ? 180.0F : 0.0F));
         }

         poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
         poseStack.scale(0.5F, 0.5F, 0.5F);
         if (displayContext != ItemDisplayContext.GUI) {
            ItemRenderer itemRenderer = mc.getItemRenderer();
            itemRenderer.renderStatic(ingredientStack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, buffer, null, 0);
         }

         poseStack.popPose();
      }

      poseStack.pushPose();
      if (animation != 0.0F && displayContext.firstPerson()) {
         poseStack.translate(0.0F, 0.0F, 1.0F);
         poseStack.mulPose(Axis.XN.rotationDegrees(Mth.sin(animation * 6.2831855F) * 15.0F));
         poseStack.translate(0.0F, 0.0F, -1.0F);
         poseStack.translate(0.0, 0.0, -Mth.sin(animation * 3.1415927F) * 0.2);
      }

      mc.getBlockRenderer().renderSingleBlock(state, poseStack, buffer, packedLight, packedOverlay);
      poseStack.popPose();
   }

   public static class ArmPoseTransformer implements IArmPoseTransformer {
      public void applyTransform(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm) {
         ItemStack stack = entity.getUseItem();
         if (stack.has((DataComponentType)ModDataComponents.SKILLET_FLIP_TIMESTAMP.get())) {
            long time = (Long)stack.get((DataComponentType)ModDataComponents.SKILLET_FLIP_TIMESTAMP.get());
            float partialTicks = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
            float animation = ((float)(entity.level().getGameTime() - time) + partialTicks) / 12.0F;
            animation = Mth.clamp(animation, 0.0F, 1.0F);
            if (arm == HumanoidArm.LEFT) {
               model.leftArm.xRot = (-Mth.sin(animation * 6.2831855F) * 15.0F - 20.0F) * 0.017453292F;
            } else {
               model.rightArm.xRot = (-Mth.sin(animation * 6.2831855F) * 15.0F - 20.0F) * 0.017453292F;
            }
         }
      }
   }
}
