package fuzs.eternalnether.init;

import java.util.stream.Stream;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.level.block.Block;

public final class ModBlockFamilies {
   public static final BlockFamily WITHERED_BLACKSTONE_FAMILY = BlockFamilies.familyBuilder((Block)ModBlocks.WITHERED_BLACKSTONE.value())
      .slab((Block)ModBlocks.WITHERED_BLACKSTONE_SLAB.value())
      .stairs((Block)ModBlocks.WITHERED_BLACKSTONE_STAIRS.value())
      .wall((Block)ModBlocks.WITHERED_BLACKSTONE_WALL.value())
      .cracked((Block)ModBlocks.CRACKED_WITHERED_BLACKSTONE.value())
      .chiseled((Block)ModBlocks.CHISELED_WITHERED_BLACKSTONE.value())
      .dontGenerateModel()
      .getFamily();
   public static final BlockFamily CRACKED_WITHERED_BLACKSTONE_FAMILY = BlockFamilies.familyBuilder((Block)ModBlocks.CRACKED_WITHERED_BLACKSTONE.value())
      .slab((Block)ModBlocks.CRACKED_WITHERED_BLACKSTONE_SLAB.value())
      .stairs((Block)ModBlocks.CRACKED_WITHERED_BLACKSTONE_STAIRS.value())
      .wall((Block)ModBlocks.CRACKED_WITHERED_BLACKSTONE_WALL.value())
      .getFamily();
   public static final BlockFamily WARPED_NETHER_BRICKS_FAMILY = BlockFamilies.familyBuilder((Block)ModBlocks.WARPED_NETHER_BRICKS.value())
      .slab((Block)ModBlocks.WARPED_NETHER_BRICK_SLAB.value())
      .stairs((Block)ModBlocks.WARPED_NETHER_BRICK_STAIRS.value())
      .wall((Block)ModBlocks.WARPED_NETHER_BRICK_WALL.value())
      .chiseled((Block)ModBlocks.CHISELED_WARPED_NETHER_BRICKS.value())
      .getFamily();

   public static Stream<BlockFamily> getAllFamilies() {
      return Stream.of(WITHERED_BLACKSTONE_FAMILY, CRACKED_WITHERED_BLACKSTONE_FAMILY, WARPED_NETHER_BRICKS_FAMILY);
   }
}
