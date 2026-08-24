package tannyjung.tanshugetrees.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import tannyjung.tanshugetrees_handcode.systems.tree_generator.Sapling;

public class SaplingTickProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      Sapling.tick(world, BlockPos.containing(x, y, z));
   }
}
