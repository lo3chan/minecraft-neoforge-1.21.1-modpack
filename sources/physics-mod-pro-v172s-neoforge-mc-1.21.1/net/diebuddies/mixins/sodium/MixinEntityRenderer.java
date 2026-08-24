package net.diebuddies.mixins.sodium;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import net.caffeinemc.mods.sodium.api.util.ColorABGR;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.caffeinemc.mods.sodium.client.render.immediate.model.ModelCuboid;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(
   targets = {"net.caffeinemc.mods.sodium.client.render.immediate.model.EntityRenderer"},
   remap = false
)
public class MixinEntityRenderer {
   @Unique
   private static ModelPart sodiumPart;

   @Inject(
      at = {@At("HEAD")},
      method = {"renderCuboids"},
      remap = false
   )
   private static void physicsmod$renderCuboids(
      Pose matrices, VertexBufferWriter writer, ModelCuboid[] cuboids, int light, int overlay, int color, CallbackInfo info
   ) {
      if (PhysicsMod.getCurrentInstance() != null && PhysicsMod.getCurrentInstance().blockify && sodiumPart != null) {
         PhysicsMod.createParticlesFromCuboids(
            matrices,
            PhysicsMod.getCurrentInstance().localPivotMatrix,
            sodiumPart.cubes,
            PhysicsMod.getCurrentInstance().cubifyEntity,
            PhysicsMod.getCurrentInstance().cubifyEntityRenderer,
            PhysicsMod.getCurrentInstance().blockifyFeature,
            overlay,
            ColorABGR.unpackRed(color) / 255.0F,
            ColorABGR.unpackGreen(color) / 255.0F,
            ColorABGR.unpackBlue(color) / 255.0F
         );
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"render"},
      remap = false
   )
   private static void physicsmod$renderHead(
      PoseStack matrixStack, VertexBufferWriter writer, ModelPart part, int light, int overlay, int color, CallbackInfo info
   ) {
      if (PhysicsMod.getCurrentInstance() != null && PhysicsMod.getCurrentInstance().blockify && part.visible) {
         sodiumPart = part;
         PhysicsMod.getCurrentInstance().localPivotMatrix.pushPose();
         part.translateAndRotate(PhysicsMod.getCurrentInstance().localPivotMatrix);
      }
   }

   @Inject(
      at = {@At("RETURN")},
      method = {"render"},
      remap = false
   )
   private static void physicsmod$renderTail(
      PoseStack matrixStack, VertexBufferWriter writer, ModelPart part, int light, int overlay, int color, CallbackInfo info
   ) {
      if (PhysicsMod.getCurrentInstance() != null && PhysicsMod.getCurrentInstance().blockify && part.visible) {
         PhysicsMod.getCurrentInstance().localPivotMatrix.popPose();
         sodiumPart = null;
      }
   }
}
