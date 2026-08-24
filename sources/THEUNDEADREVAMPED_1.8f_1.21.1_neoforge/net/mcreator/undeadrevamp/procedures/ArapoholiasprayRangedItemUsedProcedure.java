package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ArapoholiasprayRangedItemUsedProcedure {
   public static void execute(Entity entity, ItemStack itemstack) {
      if (entity != null) {
         if (entity instanceof Player _player) {
            _player.getCooldowns().addCooldown(itemstack.getItem(), 150);
         }

         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(UndeadRevamp2ModMobEffects.HONEYSPLAT);
         }

         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(UndeadRevamp2ModMobEffects.MOONFLOWERSSCENT);
         }
      }
   }
}
