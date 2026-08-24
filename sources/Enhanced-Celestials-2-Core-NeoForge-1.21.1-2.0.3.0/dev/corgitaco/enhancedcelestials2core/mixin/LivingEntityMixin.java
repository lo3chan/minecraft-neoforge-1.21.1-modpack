package dev.corgitaco.enhancedcelestials2core.mixin;

import dev.corgitaco.enhancedcelestials2core.EnhancedCelestials;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LivingEntity.class})
public abstract class LivingEntityMixin extends Entity {
   public LivingEntityMixin(EntityType<?> entityTypeIn, Level worldIn) {
      super(entityTypeIn, worldIn);
   }

   @Inject(
      method = {"tick"},
      at = {@At("HEAD")}
   )
   private void lunarEntityTick(CallbackInfo ci) {
      EnhancedCelestials.lunarForecastWorldData(this.level()).ifPresent(data -> {
         Holder<LunarEvent> currentEvent = data.currentLunarEventHolder();
         ((LunarEvent)currentEvent.value()).livingEntityTick((LivingEntity)this);
         if (this instanceof Mob mob) {
            ((LunarEvent)currentEvent.value()).equipExistingMob(mob, currentEvent);
         }
      });
   }

   @Inject(
      method = {"checkBedExists"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void blockSleeping(CallbackInfoReturnable<Boolean> cir) {
      EnhancedCelestials.lunarForecastWorldData(this.level()).ifPresent(data -> {
         if (data.currentLunarEvent().blockSleeping((LivingEntity)this)) {
            if ((LivingEntity)this instanceof ServerPlayer) {
               ((ServerPlayer)this).displayClientMessage(Component.translatable("enhancedcelestials2core.sleep.fail").withStyle(ChatFormatting.RED), true);
            }

            cir.setReturnValue(false);
         }
      });
   }
}
