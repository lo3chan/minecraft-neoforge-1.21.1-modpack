package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.init.UndeadRevamp2ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelAccessor;

public class SomnolencespawnsBlockDestroyedByPlayerProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (Math.random()
               < 0.01
                  + (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY)
                        .getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE))
                     / 10
            && world instanceof ServerLevel _level) {
            ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack((ItemLike)UndeadRevamp2ModItems.THE_SOMNOLENCEEXTRACT.get()));
            entityToSpawn.setPickUpDelay(10);
            _level.addFreshEntity(entityToSpawn);
         }

         if (world instanceof ServerLevel _level) {
            _level.addFreshEntity(new ExperienceOrb(_level, x, y, z, 3));
         }
      }
   }
}
