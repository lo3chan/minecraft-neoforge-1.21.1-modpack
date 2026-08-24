package net.astralya.hexalia.neoforge.event;

import net.astralya.hexalia.util.ArmorBehaviorHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent.LivingTargetType;

public final class NeoForgeArmorEvents {
   private NeoForgeArmorEvents() {
   }

   public static void register() {
      IEventBus bus = NeoForge.EVENT_BUS;
      bus.addListener(NeoForgeArmorEvents::onIncomingDamage);
      bus.addListener(NeoForgeArmorEvents::onKnockback);
      bus.addListener(NeoForgeArmorEvents::onChangeTarget);
   }

   private static void onIncomingDamage(LivingIncomingDamageEvent event) {
      LivingEntity entity = event.getEntity();
      if (!entity.level().isClientSide()) {
         event.setAmount(ArmorBehaviorHelper.adjustedIncomingDamage(entity, event.getSource(), event.getAmount()));
         if (event.getSource().getEntity() instanceof LivingEntity attacker && ArmorBehaviorHelper.shouldReflectBloomwrapDamage(entity, attacker)) {
            attacker.hurt(entity.level().damageSources().thorns(entity), ArmorBehaviorHelper.bloomwrapReflectionDamage(event.getAmount()));
         }
      }
   }

   private static void onKnockback(LivingKnockBackEvent event) {
      event.setStrength(ArmorBehaviorHelper.adjustedKnockbackStrength(event.getEntity(), event.getStrength()));
   }

   private static void onChangeTarget(LivingChangeTargetEvent event) {
      if (event.getEntity() instanceof Mob mob) {
         if (!mob.level().isClientSide() && event.getTargetType() == LivingTargetType.MOB_TARGET && event.getNewAboutToBeSetTarget() instanceof Player player) {
            if (ArmorBehaviorHelper.shouldGhostveilClearTarget(mob, player)) {
               event.setNewAboutToBeSetTarget(null);
            }
         }
      }
   }
}
