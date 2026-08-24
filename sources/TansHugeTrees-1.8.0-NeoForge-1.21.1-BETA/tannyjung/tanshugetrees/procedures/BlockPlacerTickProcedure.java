package tannyjung.tanshugetrees.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import tannyjung.tanshugetrees_handcode.systems.tree_generator.BlockPlacer;

public class BlockPlacerTickProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      BlockPlacer.start(world, (ServerLevel)world, BlockPos.containing(x, y, z));
   }
}
