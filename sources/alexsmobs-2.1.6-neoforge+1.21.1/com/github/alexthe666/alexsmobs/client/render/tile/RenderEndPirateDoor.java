package com.github.alexthe666.alexsmobs.client.render.tile;

import com.github.alexthe666.alexsmobs.block.BlockEndPirateDoor;
import com.github.alexthe666.alexsmobs.client.model.ModelEndPirateDoor;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateDoor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;

public class RenderEndPirateDoor<T extends TileEntityEndPirateDoor> implements BlockEntityRenderer<T> {
   private static final ResourceLocation TEXTURE = AMCompat.rl("alexsmobs:textures/entity/end_pirate/door.png");
   private static final ModelEndPirateDoor DOOR_MODEL = new ModelEndPirateDoor();

   public RenderEndPirateDoor(Context rendererDispatcherIn) {
   }

   public void render(T tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
      matrixStackIn.pushPose();
      Direction dir = (Direction)tileEntityIn.getBlockState().getValue(BlockEndPirateDoor.HORIZONTAL_FACING);
      switch (dir) {
         case NORTH:
            matrixStackIn.translate(0.5, 0.5, -0.5);
            break;
         case EAST:
            matrixStackIn.translate(1.5F, 0.5F, 0.5F);
            break;
         case SOUTH:
            matrixStackIn.translate(0.5, 0.5, 1.5);
            break;
         case WEST:
            matrixStackIn.translate(-0.5F, 0.5F, 0.5F);
      }

      matrixStackIn.mulPose(dir.getOpposite().getRotation());
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0F, 1.0F, -1.0F);
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F));
      matrixStackIn.scale(0.999F, 0.999F, 0.999F);
      DOOR_MODEL.renderDoor(tileEntityIn, partialTicks, tileEntityIn.getBlockState().getValue(BlockEndPirateDoor.HINGE) == DoorHingeSide.LEFT);
      DOOR_MODEL.renderToBuffer(
         matrixStackIn, bufferIn.getBuffer(RenderType.entityTranslucent(TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F
      );
      matrixStackIn.popPose();
      matrixStackIn.popPose();
   }

   public int getViewDistance() {
      return 128;
   }
}
