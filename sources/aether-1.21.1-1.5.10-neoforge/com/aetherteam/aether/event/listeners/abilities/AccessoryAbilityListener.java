package com.aetherteam.aether.event.listeners.abilities;

import com.aetherteam.aether.event.hooks.AbilityHooks;
import com.aetherteam.aether.item.accessories.abilities.ShieldOfRepulsionAccessory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent.LivingVisibilityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.neoforged.neoforge.event.level.BlockEvent.BreakEvent;

public class AccessoryAbilityListener {
   public static void listen(IEventBus bus) {
      bus.addListener(AccessoryAbilityListener::onBlockBreak);
      bus.addListener(AccessoryAbilityListener::onMiningSpeed);
      bus.addListener(AccessoryAbilityListener::onTargetSet);
      bus.addListener(AccessoryAbilityListener::onProjectileImpact);
      bus.addListener(AccessoryAbilityListener::onEntityHurt);
   }

   public static void onBlockBreak(BreakEvent event) {
      Player player = event.getPlayer();
      LevelAccessor level = event.getLevel();
      BlockState state = event.getState();
      BlockPos pos = event.getPos();
      if (!event.isCanceled()) {
         AbilityHooks.AccessoryHooks.damageZaniteRing(player, level, state, pos);
         AbilityHooks.AccessoryHooks.damageZanitePendant(player, level, state, pos);
      }
   }

   public static void onMiningSpeed(BreakSpeed event) {
      Player player = event.getEntity();
      if (!event.isCanceled()) {
         event.setNewSpeed(AbilityHooks.AccessoryHooks.handleZaniteRingAbility(player, event.getNewSpeed()));
         event.setNewSpeed(AbilityHooks.AccessoryHooks.handleZanitePendantAbility(player, event.getNewSpeed()));
      }
   }

   public static void onTargetSet(LivingVisibilityEvent event) {
      LivingEntity livingEntity = event.getEntity();
      Entity lookingEntity = event.getLookingEntity();
      if (AbilityHooks.AccessoryHooks.preventTargeting(livingEntity, lookingEntity)
         && !AbilityHooks.AccessoryHooks.recentlyAttackedWithInvisibility(livingEntity, lookingEntity)) {
         event.modifyVisibility(0.0);
      }

      if (AbilityHooks.AccessoryHooks.recentlyAttackedWithInvisibility(livingEntity, lookingEntity)) {
         event.modifyVisibility(1.0);
      }
   }

   public static void onProjectileImpact(ProjectileImpactEvent event) {
      HitResult hitResult = event.getRayTraceResult();
      Projectile projectile = event.getProjectile();
      ShieldOfRepulsionAccessory.deflectProjectile(event, hitResult, projectile);
   }

   public static void onEntityHurt(LivingIncomingDamageEvent event) {
      LivingEntity livingEntity = event.getEntity();
      DamageSource damageSource = event.getSource();
      AbilityHooks.AccessoryHooks.setAttack(event.getSource());
      if (AbilityHooks.AccessoryHooks.preventMagmaDamage(livingEntity, damageSource)) {
         event.setCanceled(true);
      }
   }
}
