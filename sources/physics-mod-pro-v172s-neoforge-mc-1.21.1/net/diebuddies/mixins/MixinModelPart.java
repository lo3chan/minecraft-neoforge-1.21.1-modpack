package net.diebuddies.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import java.util.List;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.util.FastColor.ARGB32;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {ModelPart.class},
   priority = 500
)
public class MixinModelPart {
   @Shadow
   @Final
   public List<Cube> cubes;

   @Inject(
      at = {@At("HEAD")},
      method = {"compile"},
      cancellable = true
   )
   private void renderCuboids(Pose matrices, VertexConsumer vertexConsumer, int light, int overlay, int color, CallbackInfo ci) {
      if (PhysicsMod.getCurrentInstance() != null && PhysicsMod.getCurrentInstance().blockify && ((ModelPart)this).visible) {
         ((ModelPart)this).translateAndRotate(PhysicsMod.getCurrentInstance().localPivotMatrix);
         PhysicsMod.createParticlesFromCuboids(
            matrices,
            PhysicsMod.getCurrentInstance().localPivotMatrix,
            this.cubes,
            PhysicsMod.getCurrentInstance().cubifyEntity,
            PhysicsMod.getCurrentInstance().cubifyEntityRenderer,
            PhysicsMod.getCurrentInstance().blockifyFeature,
            overlay,
            ARGB32.red(color) / 255.0F,
            ARGB32.green(color) / 255.0F,
            ARGB32.blue(color) / 255.0F
         );
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"}
   )
   public void renderHead(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color, CallbackInfo ci) {
      if (PhysicsMod.getCurrentInstance() != null && PhysicsMod.getCurrentInstance().blockify && ((ModelPart)this).visible) {
         PhysicsMod.getCurrentInstance().localPivotMatrix.pushPose();
      }
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"}
   )
   public void renderTail(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color, CallbackInfo ci) {
      if (PhysicsMod.getCurrentInstance() != null && PhysicsMod.getCurrentInstance().blockify && ((ModelPart)this).visible) {
         PhysicsMod.getCurrentInstance().localPivotMatrix.popPose();
      }
   }
}
