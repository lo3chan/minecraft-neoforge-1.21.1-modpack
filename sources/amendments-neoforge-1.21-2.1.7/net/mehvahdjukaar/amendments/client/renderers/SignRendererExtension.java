package net.mehvahdjukaar.amendments.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mehvahdjukaar.moonlight.api.client.util.RotHlpr;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SignRendererExtension {
   public static final Vec3 TEXT_OFFSET = new Vec3(0.0, -0.0416666679084301, 0.0635);

   public static void renderSignBlockModelInGui(GuiGraphics guiGraphics, boolean isWall, BlockState state, boolean flipped) {
      if (isWall) {
         state = (BlockState)state.setValue(WallHangingSignBlock.FACING, Direction.SOUTH);
      } else {
         state = (BlockState)state.setValue(StandingSignBlock.ROTATION, 0);
      }

      PoseStack pose = guiGraphics.pose();
      pose.scale(93.75F, -93.75F, 93.75F);
      pose.translate(0.0, 0.0, -0.125);
      if (flipped) {
         pose.mulPose(RotHlpr.Y180);
      }

      pose.translate(-0.5F, -0.71875F + (isWall ? 0.21875F : 0.0F), -0.5F);
      Minecraft.getInstance()
         .getBlockRenderer()
         .getModelRenderer()
         .renderModel(
            pose.last(),
            guiGraphics.bufferSource().getBuffer(RenderType.cutout()),
            state,
            Minecraft.getInstance().getBlockRenderer().getBlockModel(state),
            1.0F,
            1.0F,
            1.0F,
            15728880,
            OverlayTexture.NO_OVERLAY
         );
   }

   public static void translateWall(PoseStack poseStack) {
      poseStack.translate(0.0F, 0.125F, 0.0F);
   }
}
