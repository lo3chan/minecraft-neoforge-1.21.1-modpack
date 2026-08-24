package net.diebuddies.mixins.cloth;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import java.util.Map.Entry;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.config.ConfigCloth;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.verlet.Cloth;
import net.diebuddies.physics.verlet.PhysicsClothLayer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({HumanoidArmorLayer.class})
public class MixinHumanoidArmorLayer {
   @Inject(
      at = {@At("HEAD")},
      method = {"renderArmorPiece"},
      cancellable = true
   )
   private void renderArmorPiece(
      PoseStack poseStack,
      MultiBufferSource multiBufferSource,
      LivingEntity entity,
      EquipmentSlot equipmentSlot,
      int light,
      HumanoidModel humanoidModel,
      CallbackInfo info
   ) {
      if (ConfigClient.capePhysics && !PhysicsMod.hudRendering && !ConfigClient.clothForceArmor) {
         ItemStack itemStack = entity.getItemBySlot(equipmentSlot);
         if (!(itemStack.getItem() instanceof ArmorItem)) {
            return;
         }

         ArmorItem armorItem = (ArmorItem)itemStack.getItem();
         if (armorItem.getEquipmentSlot() != equipmentSlot) {
            return;
         }

         Map<String, ConfigCloth.ClothList> customizationParts = ConfigCloth.getCustomizationParts(entity);
         if (customizationParts == null || entity.isInvisible()) {
            return;
         }

         for (Entry<String, ConfigCloth.ClothList> customizationPart : customizationParts.entrySet()) {
            ConfigCloth.ClothList clothList = customizationPart.getValue();

            for (String clothPiece : clothList.getClothPieces()) {
               Cloth cloth = PhysicsClothLayer.getCloth(clothPiece);
               if (cloth != null && cloth.rules.getHiddenArmorPieces().contains(equipmentSlot.getName())) {
                  info.cancel();
                  return;
               }
            }
         }
      }
   }
}
