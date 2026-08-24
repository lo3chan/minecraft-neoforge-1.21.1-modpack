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
import net.neoforged.neoforge.event.entity.player.BonemealEvent;

@EventBusSubscriber
public class PositiveFertilizerProcedure {
   @SubscribeEvent
   public static void onBonemeal(BonemealEvent event) {
      execute(event, event.getLevel(), event.getPlayer());
   }

   public static void execute(LevelAccessor world, Entity entity) {
      execute(null, world, entity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, Entity entity) {
      if (entity != null) {
         if (world.getLevelData().getGameRules().getBoolean(BornInChaosV1ModGameRules.NAUGHTINESS_MECHANICS)
            && entity instanceof Player
            && ((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness > 0.0) {
            BornInChaosV1ModVariables.PlayerVariables _vars = (BornInChaosV1ModVariables.PlayerVariables)entity.getData(
               BornInChaosV1ModVariables.PLAYER_VARIABLES
            );
            _vars.naughtiness = ((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness - 1.0;
            _vars.syncPlayerVariables(entity);
            if (((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness < 35.0) {
               entity.getPersistentData().putBoolean("firstwarning", false);
               entity.getPersistentData().putBoolean("secondwarning", false);
               entity.getPersistentData().putBoolean("finalwarning", false);
            } else if (((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness > 50.0
               && ((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness < 65.0) {
               entity.getPersistentData().putBoolean("secondwarning", false);
               entity.getPersistentData().putBoolean("finalwarning", false);
            } else if (((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness > 75.0
               && ((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness < 85.0) {
               entity.getPersistentData().putBoolean("finalwarning", false);
            }
         }
      }
   }
}
