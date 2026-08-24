package vectorwing.farmersdelight.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.items.ItemStackHandler;
import vectorwing.farmersdelight.common.block.StoveBlock;
import vectorwing.farmersdelight.common.block.entity.AbstractStoveBlockEntity;

public class DefaultStoveRenderer<T extends AbstractStoveBlockEntity> implements BlockEntityRenderer<T> {
   private static final float SIZE = 0.375F;
   private final ItemRenderer itemRenderer;

   public DefaultStoveRenderer(Context context) {
      this.itemRenderer = context.getItemRenderer();
   }

   public void render(T stove, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
      Direction direction = ((Direction)stove.getBlockState().getValue(StoveBlock.FACING)).getOpposite();
      ItemStackHandler items = stove.getItems();
      int posLong = (int)stove.getBlockPos().asLong();

      for (int i = 0; i < items.getSlots(); i++) {
         ItemStack stoveStack = items.getStackInSlot(i);
         if (!stoveStack.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5, 1.02, 0.5);
            float f = -direction.toYRot();
            poseStack.mulPose(Axis.YP.rotationDegrees(f));
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            Vec2 itemOffset = stove.getStoveItemOffset(i);
            poseStack.translate(itemOffset.x, itemOffset.y, 0.0);
            poseStack.scale(0.375F, 0.375F, 0.375F);
            this.itemRenderer
               .renderStatic(
                  stoveStack,
                  ItemDisplayContext.FIXED,
                  LevelRenderer.getLightColor(stove.getLevel(), stove.getBlockPos().above()),
                  packedOverlay,
                  poseStack,
                  buffer,
                  stove.getLevel(),
                  posLong + i
               );
            poseStack.popPose();
         }
      }
   }
}
