package jeresources.util;

import java.util.List;
import jeresources.api.drop.PlantDrop;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;

public class PlantHelper {
   public static List<PlantDrop> getSeeds() {
      return LootTableHelper.toDrops(Blocks.GRASS_BLOCK.getLootTable())
         .stream()
         .map(lootDrop -> new PlantDrop(lootDrop.item, lootDrop.minDrop, lootDrop.maxDrop))
         .toList();
   }

   public static BlockState getPlant(BushBlock bushBlock, BlockGetter world, BlockPos pos) {
      BlockState state = world.getBlockState(pos);
      return state.getBlock() != bushBlock ? bushBlock.defaultBlockState() : state;
   }
}
