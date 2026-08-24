package net.mehvahdjukaar.amendments.mixins.neoforge;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mehvahdjukaar.amendments.client.renderers.SignRendererExtension;
import net.mehvahdjukaar.amendments.configs.ClientConfigs;
import net.mehvahdjukaar.moonlight.api.util.math.ColorUtils;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vectorwing.farmersdelight.client.renderer.CanvasSignRenderer;

@Pseudo
@Mixin({CanvasSignRenderer.class})
public abstract class CompatFarmersDelightSignRendererMixin extends SignRenderer {
   @Unique
   private static Float amendments$canvasSignYaw;
   @Unique
   private static Boolean amendments$canvasFront;

   public CompatFarmersDelightSignRendererMixin(Context context) {
      super(context);
   }

   @Overwrite(
      remap = false
   )
   public static int getDarkColor(SignText signText, boolean isOutlineVisible) {
      int color = signText.getColor().getTextColor();
      if (color == DyeColor.BLACK.getTextColor() && signText.hasGlowingText()) {
         return -988212;
      } else {
         float brightness = isOutlineVisible ? 0.4F : 0.6F;
         float scale = brightness * ClientConfigs.getSignColorMult();
         if (amendments$canvasFront != null && amendments$canvasSignYaw != null) {
            Vector3f normal = new Vector3f(0.0F, 0.0F, 1.0F);
            normal.rotateY(amendments$canvasSignYaw * 0.017453292F * (amendments$canvasFront ? 1 : -1));
            amendments$canvasFront = null;
            scale *= ColorUtils.getShading(normal);
         }

         return ColorUtils.multiply(color, scale);
      }
   }

   @Inject(
      method = {"translateSign"},
      at = {@At("HEAD")}
   )
   private void captureYaw(PoseStack poseStack, float yaw, BlockState blockState, CallbackInfo ci) {
      amendments$canvasSignYaw = yaw;
   }

   @Inject(
      method = {"renderSignText"},
      at = {@At("HEAD")}
   )
   private void captureFace(
      BlockPos blockPos, SignText signText, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, int k, boolean face, CallbackInfo ci
   ) {
      amendments$canvasFront = face;
   }

   @Inject(
      method = {"renderSignWithText"},
      at = {@At("TAIL")},
      remap = false
   )
   private void resetYaw(
      SignBlockEntity signBlockEntity,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int packedLight,
      int packedOverlay,
      BlockState state,
      SignBlock block,
      DyeColor dye,
      Model model,
      CallbackInfo ci
   ) {
      amendments$canvasSignYaw = null;
   }

   public float getSignModelRenderScale() {
      return ClientConfigs.PIXEL_CONSISTENT_SIGNS.get() ? 1.0F : super.getSignModelRenderScale();
   }

   @ModifyReturnValue(
      method = {"getTextOffset"},
      at = {@At("RETURN")}
   )
   private Vec3 amendments$signTextOffset(Vec3 scale) {
      return ClientConfigs.PIXEL_CONSISTENT_SIGNS.get() ? SignRendererExtension.TEXT_OFFSET : scale;
   }

   @Inject(
      method = {"renderSignModel"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void amendments$renderSignModel(PoseStack poseStack, int packedLight, int packedOverlay, Model model, VertexConsumer vertexConsumer, CallbackInfo ci) {
      if (ClientConfigs.PIXEL_CONSISTENT_SIGNS.get()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"translateSign"},
      at = {@At("TAIL")}
   )
   private void amendments$signTranslate(PoseStack poseStack, float yRot, BlockState state, CallbackInfo ci) {
      if (ClientConfigs.PIXEL_CONSISTENT_SIGNS.get() && !(state.getBlock() instanceof StandingSignBlock)) {
         SignRendererExtension.translateWall(poseStack);
      }
   }
}
