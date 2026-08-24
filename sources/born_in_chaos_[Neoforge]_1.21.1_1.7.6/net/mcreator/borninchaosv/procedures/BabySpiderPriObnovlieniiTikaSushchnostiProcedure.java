package net.mcreator.borninchaosv.procedures;

import java.util.ArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.level.LevelAccessor;

public class BabySpiderPriObnovlieniiTikaSushchnostiProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null) {
         if (entity.isVehicle()) {
            for (Entity entityiterator : new ArrayList(entity.getPassengers())) {
               if (entityiterator instanceof Skeleton && !entityiterator.level().isClientSide()) {
                  entityiterator.discard();
               }
            }
         }
      }
   }
}
