package io.github.razordevs.deep_aether.item.gear;

import com.aetherteam.aether.event.hooks.AbilityHooks.ArmorHooks;
import io.github.razordevs.deep_aether.item.gear.stratus.StratusAbility;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent.LivingJumpEvent;

@EventBusSubscriber(
   modid = "deep_aether"
)
public class ArmorAbilityListener {
   @SubscribeEvent
   public static void onEntityJump(LivingJumpEvent event) {
      LivingEntity livingEntity = event.getEntity();
      StratusAbility.moreBoostedJump(livingEntity);
   }

   @SubscribeEvent
   public static void onEntityFall(LivingFallEvent event) {
      LivingEntity livingEntity = event.getEntity();
      if (!event.isCanceled()) {
         event.setCanceled(ArmorHooks.fallCancellation(livingEntity));
      }
   }
}
