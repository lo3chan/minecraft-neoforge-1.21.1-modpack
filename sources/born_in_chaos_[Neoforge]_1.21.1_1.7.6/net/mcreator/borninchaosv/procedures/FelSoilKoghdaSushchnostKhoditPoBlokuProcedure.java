package net.mcreator.borninchaosv.procedures;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;

public class FelSoilKoghdaSushchnostKhoditPoBlokuProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null) {
         if ((entity instanceof Mob || entity instanceof Monster || entity instanceof Player) && !entity.isShiftKeyDown() && !entity.fireImmune()) {
            entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.HOT_FLOOR)), 1.0F);
         }
      }
   }
}
