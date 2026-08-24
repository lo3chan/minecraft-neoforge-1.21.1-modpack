package net.cibernet.alchemancy.mixin;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.mixin.accessors.LivingEntityAccessor;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ServerPlayer.class})
public class ServerPlayerMixin {
   @Inject(
      method = {"setPlayerInput"},
      at = {@At("HEAD")}
   )
   public void setPlayerInput(float strafe, float forward, boolean jumping, boolean sneaking, CallbackInfo ci) {
      ((LivingEntityAccessor)this).setJumping(jumping);
   }

   @ModifyArg(
      method = {"checkMovementStatistics"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V"
      )
   )
   public float athleticReduceMovementExhaustion(float par1) {
      ServerPlayer self = (ServerPlayer)this;
      float mult = 1.0F;

      for (EquipmentSlot slot : EquipmentSlot.values()) {
         if (slot.isArmor() && InfusedPropertiesHelper.hasProperty(self.getItemBySlot(slot), AlchemancyProperties.ATHLETIC)) {
            mult -= 0.1F;
         }
      }

      return par1 * mult;
   }
}
