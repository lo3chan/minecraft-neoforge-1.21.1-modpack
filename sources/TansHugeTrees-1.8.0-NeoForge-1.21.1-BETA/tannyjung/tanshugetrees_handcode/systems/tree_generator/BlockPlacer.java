package tannyjung.tanshugetrees_handcode.systems.tree_generator;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import tannyjung.tanshugetrees_core.Core;
import tannyjung.tanshugetrees_core.game.GameUtils;
import tannyjung.tanshugetrees_core.outside.TXTFunction;

public class BlockPlacer {
   public static void start(LevelAccessor level_accessor, ServerLevel level_server, BlockPos pos) {
      String function = GameUtils.Data.getBlockText(level_accessor, pos, "function");
      if (!GameUtils.Data.getBlockLogic(level_accessor, pos, "delay1")) {
         GameUtils.Data.setBlockLogic(level_accessor, level_server, pos, "delay1", true);
         GameUtils.Tile.setScheduleTick(level_server, pos, 100);
         if (!function.isEmpty()) {
            String[] styles = GameUtils.Data.getBlockText(level_accessor, pos, "function_style").split("/");
            boolean pass = false;

            for (String style : styles) {
               if (style.equals("all")) {
                  pass = true;
               } else if (style.equals("up")) {
                  if (level_accessor.getBlockState(pos.above()).isAir()) {
                     pass = true;
                  }
               } else if (style.equals("down")) {
                  if (level_accessor.getBlockState(pos.below()).isAir()) {
                     pass = true;
                  }
               } else if (style.equals("side")) {
                  if (level_accessor.getBlockState(pos.north()).isAir()) {
                     pass = true;
                  } else if (level_accessor.getBlockState(pos.west()).isAir()) {
                     pass = true;
                  } else if (level_accessor.getBlockState(pos.east()).isAir()) {
                     pass = true;
                  } else if (level_accessor.getBlockState(pos.south()).isAir()) {
                     pass = true;
                  }
               }
            }

            if (!pass) {
               GameUtils.Data.setBlockText(level_accessor, level_server, pos, "function", "");
            }
         }
      } else if (!GameUtils.Data.getBlockLogic(level_accessor, pos, "delay2")) {
         GameUtils.Data.setBlockLogic(level_accessor, level_server, pos, "delay2", true);
         GameUtils.Tile.setScheduleTick(level_server, pos, 100);
      } else {
         GameUtils.Tile.set(level_accessor, pos, GameUtils.Tile.fromText(level_server, GameUtils.Data.getBlockText(level_accessor, pos, "block")), false);
         if (!function.isEmpty()) {
            Core.DelayedWork.create(false, 20, () -> TXTFunction.run(level_accessor, level_server, pos, function, true));
         }
      }
   }
}
