package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class NonlethalProperty extends Property {
   @Override
   public void onIncomingAttack(Entity user, ItemStack weapon, LivingEntity target, LivingIncomingDamageEvent event) {
      event.setCanceled(true);
   }

   @Override
   public void onProjectileImpact(ItemStack stack, Projectile projectile, HitResult rayTraceResult, ProjectileImpactEvent event) {
      if (rayTraceResult.getType() == Type.ENTITY
         && rayTraceResult instanceof EntityHitResult entityHitResult
         && entityHitResult.getEntity() instanceof LivingEntity target) {
         InfusedPropertiesHelper.forEachProperty(
            stack,
            propertyHolder -> ((Property)propertyHolder.value())
               .onAttack(projectile, stack, activationDamageSource(projectile.level(), projectile, projectile.position()), target)
         );
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 16777215;
   }

   @Override
   public int getPriority() {
      return -100;
   }
}
