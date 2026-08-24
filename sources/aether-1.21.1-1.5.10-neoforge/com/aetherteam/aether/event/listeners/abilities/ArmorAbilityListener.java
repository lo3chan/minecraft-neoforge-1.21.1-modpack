package com.aetherteam.aether.event.listeners.abilities;

import com.aetherteam.aether.event.hooks.AbilityHooks;
import com.aetherteam.aether.item.combat.abilities.armor.GravititeArmor;
import com.aetherteam.aether.item.combat.abilities.armor.NeptuneArmor;
import com.aetherteam.aether.item.combat.abilities.armor.PhoenixArmor;
import com.aetherteam.aether.item.combat.abilities.armor.ValkyrieArmor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;

public class ArmorAbilityListener {
   public static void listen(IEventBus bus) {
      bus.addListener(ArmorAbilityListener::onEntityUpdate);
      bus.addListener(ArmorAbilityListener::onEntityJump);
      bus.addListener(ArmorAbilityListener::onEntityFall);
      bus.addListener(ArmorAbilityListener::onEntityAttack);
   }

   public static void onEntityUpdate(Post event) {
      if (event.getEntity() instanceof LivingEntity livingEntity) {
         ValkyrieArmor.handleFlight(livingEntity);
         NeptuneArmor.boostWaterSwimming(livingEntity);
         PhoenixArmor.boostLavaSwimming(livingEntity);
         PhoenixArmor.damageArmor(livingEntity);
      }
   }

   public static void onEntityJump(LivingJumpEvent event) {
      LivingEntity livingEntity = event.getEntity();
      GravititeArmor.boostedJump(livingEntity);
   }

   public static void onEntityFall(LivingFallEvent event) {
      LivingEntity livingEntity = event.getEntity();
      if (!event.isCanceled()) {
         event.setCanceled(AbilityHooks.ArmorHooks.fallCancellation(livingEntity));
      }
   }

   public static void onEntityAttack(LivingIncomingDamageEvent event) {
      LivingEntity livingEntity = event.getEntity();
      DamageSource damageSource = event.getSource();
      if (!event.isCanceled()) {
         event.setCanceled(PhoenixArmor.extinguishUser(livingEntity, damageSource));
      }
   }
}
