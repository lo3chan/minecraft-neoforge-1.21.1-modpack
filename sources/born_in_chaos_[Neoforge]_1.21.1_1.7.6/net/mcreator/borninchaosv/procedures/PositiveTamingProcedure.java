package net.mcreator.borninchaosv.procedures;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.init.BornInChaosV1ModGameRules;
import net.mcreator.borninchaosv.network.BornInChaosV1ModVariables;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.AnimalTameEvent;

@EventBusSubscriber
public class PositiveTamingProcedure {
   @SubscribeEvent
   public static void onEntityTamed(AnimalTameEvent event) {
      execute(event, event.getAnimal().level(), event.getTamer());
   }

   public static void execute(LevelAccessor world, Entity sourceentity) {
      execute(null, world, sourceentity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, Entity sourceentity) {
      if (sourceentity != null) {
         if (world.getLevelData().getGameRules().getBoolean(BornInChaosV1ModGameRules.NAUGHTINESS_MECHANICS)
            && sourceentity instanceof Player
            && ((BornInChaosV1ModVariables.PlayerVariables)sourceentity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness >= 5.0) {
            BornInChaosV1ModVariables.PlayerVariables _vars = (BornInChaosV1ModVariables.PlayerVariables)sourceentity.getData(
               BornInChaosV1ModVariables.PLAYER_VARIABLES
            );
            _vars.naughtiness = ((BornInChaosV1ModVariables.PlayerVariables)sourceentity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness - 5.0;
            _vars.syncPlayerVariables(sourceentity);
            if (((BornInChaosV1ModVariables.PlayerVariables)sourceentity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness < 35.0) {
               sourceentity.getPersistentData().putBoolean("firstwarning", false);
               sourceentity.getPersistentData().putBoolean("secondwarning", false);
               sourceentity.getPersistentData().putBoolean("finalwarning", false);
            } else if (((BornInChaosV1ModVariables.PlayerVariables)sourceentity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness > 50.0
               && ((BornInChaosV1ModVariables.PlayerVariables)sourceentity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness < 65.0) {
               sourceentity.getPersistentData().putBoolean("secondwarning", false);
               sourceentity.getPersistentData().putBoolean("finalwarning", false);
            } else if (((BornInChaosV1ModVariables.PlayerVariables)sourceentity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness > 75.0
               && ((BornInChaosV1ModVariables.PlayerVariables)sourceentity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness < 85.0) {
               sourceentity.getPersistentData().putBoolean("finalwarning", false);
            }
         }
      }
   }
}
