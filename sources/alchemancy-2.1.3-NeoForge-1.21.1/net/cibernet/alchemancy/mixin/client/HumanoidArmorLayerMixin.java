package net.cibernet.alchemancy.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.DisguisedProperty;
import net.cibernet.alchemancy.properties.ResizedProperty;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({HumanoidArmorLayer.class})
public class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> {
   @WrapMethod(
      method = {"renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;FFFFFF)V"}
   )
   public void renderArmorPiece(
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      T livingEntity,
      EquipmentSlot slot,
      int packedLight,
      A p_model,
      float limbSwing,
      float limbSwingAmount,
      float partialTick,
      float ageInTicks,
      float netHeadYaw,
      float headPitch,
      Operation<Void> original
   ) {
      ItemStack stack = livingEntity.getItemBySlot(slot);
      if (!InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.CONCEALED)) {
         poseStack.pushPose();
         float scale = ((ResizedProperty)AlchemancyProperties.RESIZED.value()).getData(stack);
         if (scale != 1.0F) {
            poseStack.scale(scale, scale, scale);
         }

         if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.GLOWING_AURA)) {
            packedLight = 15728880;
         }

         original.call(
            new Object[]{
               poseStack, bufferSource, livingEntity, slot, packedLight, p_model, limbSwing, limbSwingAmount, partialTick, ageInTicks, netHeadYaw, headPitch
            }
         );
         poseStack.popPose();
      }
   }

   @WrapOperation(
      method = {"renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;FFFFFF)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
      )}
   )
   public ItemStack modifyArmorItem(LivingEntity instance, EquipmentSlot equipmentSlot, Operation<ItemStack> original) {
      ItemStack stack = (ItemStack)original.call(new Object[]{instance, equipmentSlot});
      ItemStack disguise = InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.DISGUISED)
         ? ((DisguisedProperty)AlchemancyProperties.DISGUISED.get()).getData(stack)
         : ItemStack.EMPTY;
      return disguise.isEmpty() ? stack : disguise;
   }
}
