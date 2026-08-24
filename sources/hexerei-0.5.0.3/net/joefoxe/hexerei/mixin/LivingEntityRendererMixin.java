package net.joefoxe.hexerei.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.util.HexereiTags;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin({LivingEntityRenderer.class})
public abstract class LivingEntityRendererMixin {
   @Inject(
      method = {"render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V",
         ordinal = 1
      )}
   )
   public void render(
      LivingEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci
   ) {
      if (entity.getVehicle() instanceof BroomEntity broom) {
         boolean hasSeat = broom.getModule(BroomEntity.BroomSlot.SATCHEL).is((Item)ModItems.BROOM_SEAT.get());
         if (broom.deltaMovementOld == null) {
            broom.deltaMovementOld = broom.getDeltaMovement();
         }

         float deltaMovementY = Mth.lerp(partialTicks, (float)broom.deltaMovementOld.y(), (float)broom.getDeltaMovement().y());
         float deltaRotation = Math.clamp(
            Mth.lerp(partialTicks, broom.deltaRotationOld, broom.deltaRotation), -13.0F + broom.deltaRotation / 22.5F, 13.0F + broom.deltaRotation / 22.5F
         );
         matrixStack.translate(0.0F, entity.getBbHeight() / 2.5F, 0.0F);
         int i = broom.getPassengers().indexOf(entity);
         float rotation = 0.0F;
         if (i == 1) {
            rotation = 0.0F;
         } else if (hasSeat && i == 0) {
            rotation = 90.0F;
         }

         if (entity.getType().is(HexereiTags.Entity.CAN_RIDE_BROOM)) {
            rotation = 80.0F;
         }

         if (entity instanceof CrowEntity) {
            rotation = 60.0F;
         }

         if (entity instanceof OwlEntity) {
            rotation = 40.0F;
         }

         if (entity instanceof Cat) {
            rotation = 90.0F;
         }

         if (entity instanceof Cat cat) {
            matrixStack.mulPose(Axis.XP.rotationDegrees(180.0F));
            matrixStack.mulPose(Axis.YP.rotationDegrees(deltaMovementY * 25.0F));
            matrixStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            matrixStack.mulPose(Axis.XP.rotationDegrees(180.0F));
            matrixStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            matrixStack.mulPose(Axis.XP.rotationDegrees(-deltaRotation * 2.0F));
            matrixStack.mulPose(Axis.ZP.rotationDegrees(-deltaRotation * 3.0F));
         } else {
            matrixStack.mulPose(Axis.YP.rotationDegrees(rotation));
            matrixStack.mulPose(Axis.ZP.rotationDegrees(180.0F + deltaMovementY * 25.0F));
            matrixStack.mulPose(Axis.XP.rotationDegrees(180.0F - deltaRotation * 3.0F));
            matrixStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            matrixStack.mulPose(Axis.YP.rotationDegrees(-rotation));
            matrixStack.mulPose(Axis.YP.rotationDegrees(-deltaRotation * 2.0F));
         }

         matrixStack.translate(0.0F, -entity.getBbHeight() / 2.5F, 0.0F);
      }
   }
}
