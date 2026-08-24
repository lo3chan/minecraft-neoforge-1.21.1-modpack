package tannyjung.tanshugetrees.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import tannyjung.tanshugetrees_handcode.systems.tree_generator.Sapling;

public class SaplingRightClickProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         Sapling.click(world, entity, BlockPos.containing(x, y, z));
      }
   }
}
