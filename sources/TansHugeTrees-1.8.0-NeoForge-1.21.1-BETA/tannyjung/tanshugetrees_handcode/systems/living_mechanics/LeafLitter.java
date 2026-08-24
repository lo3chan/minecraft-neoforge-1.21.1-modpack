package tannyjung.tanshugetrees_handcode.systems.living_mechanics;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import tannyjung.tanshugetrees_core.game.GameUtils;
import tannyjung.tanshugetrees_core.outside.CacheManager;
import tannyjung.tanshugetrees_core.outside.TXTFunction;
import tannyjung.tanshugetrees_handcode.Handcode;

public class LeafLitter {
   public static void create(LevelAccessor level_accessor, ServerLevel level_server, BlockPos pos, BlockState block, boolean remove) {
      String function_id = "leaf_litter/" + GameUtils.Tile.toText(block)[0].replace(":", "-");
      String[] data = CacheManager.getFunction(function_id);
      if (data.length != 0 && !Handcode.Config.leaf_litter_classic_only) {
         TXTFunction.run(level_accessor, level_server, pos, function_id, true);
      } else if (Handcode.Config.leaf_litter_classic) {
         if (!remove) {
            if (level_accessor.getBlockState(pos).canBeReplaced()) {
               if (level_accessor.isWaterAt(pos.below())) {
                  pos = pos.below();
               }

               GameUtils.Tile.set(level_accessor, pos, block, false);
            }
         } else if (level_accessor.getBlockState(pos).getBlock() == block.getBlock()) {
            GameUtils.Tile.remove(level_accessor, level_server, pos, false);
         }
      }
   }
}
