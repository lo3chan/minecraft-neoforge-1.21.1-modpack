package net.joefoxe.hexerei.events;

import java.util.ArrayList;
import java.util.List;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.custom.SageBurningPlate;
import net.joefoxe.hexerei.config.HexConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent.SpawnPlacementCheck;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent.SpawnPlacementCheck.Result;

public class SageBurningPlateEvent {
   @SubscribeEvent
   public void onEntityJoin(SpawnPlacementCheck e) {
      Level world = e.getLevel().isClientSide() ? null : (e.getLevel() instanceof Level ? (Level)e.getLevel() : null);
      if (world != null) {
         if (e.getSpawnType() == MobSpawnType.NATURAL) {
            if ((Integer)HexConfig.SAGE_BURNING_PLATE_RANGE.get() != 0) {
               boolean isHostile = e.getEntityType().getCategory().equals(MobCategory.MONSTER);
               if (e.getEntityType().getCategory().equals(MobCategory.MONSTER)) {
                  List<BlockPos> nonSagePlatesInList = new ArrayList<>();
                  if (!Hexerei.sageBurningPlateTileList.isEmpty()) {
                     BlockPos burning_plate = null;

                     for (BlockPos nearbySageBurningPlate : Hexerei.sageBurningPlateTileList) {
                        float dist = (float)Math.sqrt(e.getPos().distToCenterSqr(nearbySageBurningPlate.getCenter()));
                        if (dist < (Integer)HexConfig.SAGE_BURNING_PLATE_RANGE.get() + 1) {
                           BlockState burning_platestate = world.getBlockState(nearbySageBurningPlate);
                           Block block = burning_platestate.getBlock();
                           if (!(block instanceof SageBurningPlate)) {
                              nonSagePlatesInList.add(nearbySageBurningPlate);
                           } else if ((Boolean)burning_platestate.getValue(SageBurningPlate.LIT)) {
                              burning_plate = nearbySageBurningPlate.immutable();
                              break;
                           }
                        }
                     }

                     for (BlockPos nonSageBurninPlate : nonSagePlatesInList) {
                        Hexerei.sageBurningPlateTileList.remove(nonSageBurninPlate);
                     }

                     if (burning_plate != null) {
                        e.setResult(Result.FAIL);
                     }
                  }
               }
            }
         }
      }
   }
}
