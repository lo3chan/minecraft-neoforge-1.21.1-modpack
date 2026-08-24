package net.mehvahdjukaar.amendments.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mehvahdjukaar.amendments.client.renderers.SignRendererExtension;
import net.mehvahdjukaar.amendments.configs.ClientConfigs;
import net.mehvahdjukaar.moonlight.api.util.math.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({SignRenderer.class})
public abstract class SignRendererMixin {
   @Unique
   private static Float amendments$signYaw;
   @Unique
   private static Boolean amendments$front;
   @Unique
   private static boolean amendments$rendersPixelConsistent;
   @Unique
   private static final Vec3 OLD_OFFSET = new Vec3(0.0, 0.3333333432674408, 0.046666666865348816);

   @Overwrite
   public static int getDarkColor(SignText signText) {
      int color = signText.getColor().getTextColor();
      if (color == DyeColor.BLACK.getTextColor() && signText.hasGlowingText()) {
         return -988212;
      } else {
         float scale = 0.4F * ClientConfigs.getSignColorMult();
         if (amendments$front != null && amendments$signYaw != null) {
            Vector3f normal = new Vector3f(0.0F, 0.0F, 1.0F);
            normal.rotateY(amendments$signYaw * 0.017453292F * (amendments$front ? 1 : -1));
            amendments$front = null;
            scale *= ColorUtils.getShading(normal);
         }

         return ColorUtils.multiply(color, scale);
      }
   }

   @Inject(
      method = {"translateSign"},
      at = {@At("HEAD")}
   )
   private void amendments$captureYaw(PoseStack poseStack, float yaw, BlockState blockState, CallbackInfo ci) {
      amendments$signYaw = yaw;
   }

   @Inject(
      method = {"renderSignText"},
      at = {@At("HEAD")}
   )
   private void amendments$captureFace(
      BlockPos blockPos, SignText signText, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, int k, boolean face, CallbackInfo ci
   ) {
      amendments$front = face;
   }

   @Inject(
      method = {"renderSignWithText"},
      at = {@At("TAIL")}
   )
   private void amendments$resetYaw(
      SignBlockEntity signBlockEntity,
      PoseStack poseStack,
      MultiBufferSource multiBufferSource,
      int i,
      int j,
      BlockState blockState,
      SignBlock signBlock,
      WoodType woodType,
      Model model,
      CallbackInfo ci
   ) {
      amendments$signYaw = null;
   }

   @Inject(
      method = {"render(Lnet/minecraft/world/level/block/entity/SignBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V"},
      at = {@At("RETURN")}
   )
   private void amendments$resetPixelConsistent(
      SignBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, CallbackInfo ci
   ) {
      amendments$rendersPixelConsistent = false;
   }

   @Inject(
      method = {"render(Lnet/minecraft/world/level/block/entity/SignBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void amendments$setPixelConsistent(
      SignBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, CallbackInfo ci
   ) {
      amendments$rendersPixelConsistent = ClientConfigs.isPixelConsistentSign(blockEntity.getBlockState());
      if (amendments$rendersPixelConsistent) {
         SignText front = blockEntity.getFrontText();
         SignText back = blockEntity.getBackText();
         LocalPlayer player = Minecraft.getInstance().player;
         if (player != null && !front.hasMessage(player) && !back.hasMessage(player)) {
            ci.cancel();
         }
      }
   }

   @Inject(
      method = {"renderSignModel"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void amendments$renderSignModel(PoseStack poseStack, int packedLight, int packedOverlay, Model model, VertexConsumer vertexConsumer, CallbackInfo ci) {
      if (amendments$rendersPixelConsistent) {
         ci.cancel();
      }
   }

   @ModifyReturnValue(
      method = {"getTextOffset"},
      at = {@At("RETURN")}
   )
   private Vec3 amendments$signTextOffset(Vec3 scale) {
      return amendments$rendersPixelConsistent && scale.equals(OLD_OFFSET) ? SignRendererExtension.TEXT_OFFSET : scale;
   }

   @ModifyReturnValue(
      method = {"getSignModelRenderScale"},
      at = {@At("RETURN")}
   )
   private float amendments$signScale(float scale) {
      return amendments$rendersPixelConsistent && scale == 0.6666667F ? 1.0F : scale;
   }

   @Inject(
      method = {"translateSign"},
      at = {@At("TAIL")}
   )
   private void amendments$signTranslate(PoseStack poseStack, float yRot, BlockState state, CallbackInfo ci) {
      if (amendments$rendersPixelConsistent && !(state.getBlock() instanceof StandingSignBlock)) {
         SignRendererExtension.translateWall(poseStack);
      }
   }
}
