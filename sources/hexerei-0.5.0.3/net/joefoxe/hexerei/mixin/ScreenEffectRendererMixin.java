package net.joefoxe.hexerei.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.joefoxe.hexerei.tileentity.MixingCauldronTile;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin({ScreenEffectRenderer.class})
public abstract class ScreenEffectRendererMixin {
   @OnlyIn(Dist.CLIENT)
   @Inject(
      method = {"renderScreenEffect"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void renderScreenEffect(Minecraft pMinecraft, PoseStack pPoseStack, CallbackInfo ci) {
      Player player = pMinecraft.player;
      Camera camera = pMinecraft.gameRenderer.getMainCamera();
      double d0 = camera.getPosition().y() - 0.1111111119389534;
      BlockPos blockpos = new BlockPos((int)camera.getPosition().x(), (int)d0, (int)camera.getPosition().z());
      if (player.level().getBlockEntity(blockpos) instanceof MixingCauldronTile tile && tile.renderedFluid != null) {
         double d1 = blockpos.getY() + tile.renderedFluid.getAmount() / 2000.0F;
         if (d1 > d0 && !tile.renderedFluid.getFluid().isSame(Fluids.WATER)) {
            IClientFluidTypeExtensions.of(tile.renderedFluid.getFluid()).renderOverlay(pMinecraft, pPoseStack);
         }

         ci.cancel();
      }
   }
}
