package net.mcreator.borninchaosv.procedures;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public class SearedSpiritPriObnovlieniiTaktaSushchnostiProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null) {
         if (entity.isInWaterRainOrBubble()) {
            entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC)), 1.0F);
         }
      }
   }
}
