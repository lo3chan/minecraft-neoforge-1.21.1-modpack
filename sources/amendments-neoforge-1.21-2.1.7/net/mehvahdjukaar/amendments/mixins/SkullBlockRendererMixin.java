package net.mehvahdjukaar.amendments.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.dragon.DragonHeadModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SkullBlock.Type;
import net.minecraft.world.level.block.SkullBlock.Types;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({SkullBlockRenderer.class})
public abstract class SkullBlockRendererMixin implements BlockEntityRenderer<SkullBlockEntity> {
   @Unique
   private static final ResourceLocation DRAGON_EYES = ResourceLocation.withDefaultNamespace("textures/entity/enderdragon/dragon_eyes.png");

   public AABB getRenderBoundingBox(SkullBlockEntity blockEntity) {
      return new AABB(blockEntity.getBlockPos()).inflate(0.1);
   }

   @Inject(
      method = {"renderSkull"},
      at = {@At(
         value = "INVOKE",
         shift = Shift.AFTER,
         target = "Lnet/minecraft/client/model/SkullModelBase;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"
      )}
   )
   private static void amendments$addDragonEyes(
      Direction direction,
      float yRot,
      float mouthAnimation,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int packedLight,
      SkullModelBase model,
      RenderType renderType,
      CallbackInfo ci
   ) {
      if (model instanceof DragonHeadModel) {
         VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.eyes(DRAGON_EYES));
         poseStack.pushPose();
         model.renderToBuffer(poseStack, vertexConsumer, 15728640, OverlayTexture.NO_OVERLAY);
         poseStack.popPose();
      }
   }

   @ModifyReturnValue(
      method = {"getRenderType"},
      at = {@At("RETURN")}
   )
   private static RenderType amendments$modifyDragonHeadRenderType(RenderType original, @Local ResourceLocation texture, @Local(argsOnly = true) Type type) {
      return type == Types.DRAGON ? RenderType.entityCutoutNoCull(texture) : original;
   }
}
