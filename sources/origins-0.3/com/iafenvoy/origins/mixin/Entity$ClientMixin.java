package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.power.builtin.regular.EntityGlowPower;
import com.iafenvoy.origins.data.power.builtin.regular.SelfGlowPower;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin({Entity.class})
public class Entity$ClientMixin {
   @Unique
   private Entity origins$self() {
      return (Entity)this;
   }

   @Inject(
      method = {"getTeamColor"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void handleGlowColor(CallbackInfoReturnable<Integer> cir) {
      Player player = Minecraft.getInstance().player;
      if (player != null) {
         Entity entity = this.origins$self();
         Stream.concat(PowerHelper.get(player).streamActive(EntityGlowPower.class), PowerHelper.get(entity).streamActive(SelfGlowPower.class))
            .filter(power -> !power.shouldUseTeam() && power.canGlow(player, entity))
            .mapToInt(rec$ -> rec$.getColor())
            .forEach(cir::setReturnValue);
      }
   }

   @Inject(
      method = {"isCurrentlyGlowing"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void handleGlowing(CallbackInfoReturnable<Boolean> cir) {
      Entity entity = this.origins$self();
      if (entity.level().isClientSide) {
         Player player = Minecraft.getInstance().player;
         if (player != null) {
            if (Stream.concat(PowerHelper.get(player).streamActive(EntityGlowPower.class), PowerHelper.get(entity).streamActive(SelfGlowPower.class))
               .anyMatch(power -> power.canGlow(player, entity))) {
               cir.setReturnValue(true);
            }
         }
      }
   }
}
