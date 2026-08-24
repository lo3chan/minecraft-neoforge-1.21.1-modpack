package net.joefoxe.hexerei.item.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import net.joefoxe.hexerei.client.renderer.entity.render.BroomRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class BroomItemRenderer extends CustomItemRenderer {
   private BroomRenderer renderer;
   private static final Minecraft minecraft = Minecraft.getInstance();

   @Override
   public void renderByItem(ItemStack itemStack, ItemDisplayContext displayContext, PoseStack stack, MultiBufferSource source, int light, int overlay) {
      if (this.renderer == null) {
         this.renderer = new BroomRenderer(
            new Context(
               minecraft.getEntityRenderDispatcher(),
               minecraft.getItemRenderer(),
               minecraft.getBlockRenderer(),
               minecraft.gameRenderer.itemInHandRenderer,
               minecraft.getResourceManager(),
               minecraft.getEntityModels(),
               minecraft.font
            )
         );
      }

      stack.pushPose();
      this.renderer.render(((BroomItem)itemStack.getItem()).getBroomFast(minecraft.level, itemStack), 0.0F, 1.0F, stack, source, light);
      stack.popPose();
   }
}
