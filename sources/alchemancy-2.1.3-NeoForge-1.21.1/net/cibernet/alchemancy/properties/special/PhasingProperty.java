package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.properties.Property;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;

public class PhasingProperty extends Property {
   @Override
   public void onProjectileImpact(ItemStack stack, Projectile projectile, HitResult rayTraceResult, ProjectileImpactEvent event) {
      if (rayTraceResult.getType() == Type.BLOCK) {
         event.setCanceled(true);
      }
   }

   @Override
   public void onProjectileTick(ItemStack stack, Projectile projectile) {
      if (projectile instanceof AbstractArrow arrow) {
         arrow.pickup = Pickup.DISALLOWED;
      } else {
         projectile.noPhysics = true;
      }
   }

   @Override
   public TriState allowArrowClipBlocks(AbstractArrow arrow, ItemStack stack) {
      return TriState.TRUE;
   }

   @Override
   public int getColor(ItemStack stack) {
      return 6369965;
   }
}
