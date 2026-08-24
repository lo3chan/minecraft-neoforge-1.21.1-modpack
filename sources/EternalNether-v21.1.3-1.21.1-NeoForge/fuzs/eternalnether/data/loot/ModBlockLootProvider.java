package fuzs.eternalnether.data.loot;

import fuzs.eternalnether.init.ModBlocks;
import fuzs.puzzleslib.api.data.v2.AbstractLootProvider.Blocks;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import java.util.stream.Stream;
import net.minecraft.core.Holder.Reference;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public class ModBlockLootProvider extends Blocks {
   public ModBlockLootProvider(DataProviderContext context) {
      super(context);
   }

   public void addLootTables() {
      this.add(
         net.minecraft.world.level.block.Blocks.BLACKSTONE,
         block -> this.createSingleItemTableWithSilkTouch(block, (ItemLike)ModBlocks.COBBLED_BLACKSTONE.value())
      );
      this.dropSelf((Block)ModBlocks.COBBLED_BLACKSTONE.value());
      this.dropSelf((Block)ModBlocks.WITHERED_BLACKSTONE.value());
      this.dropSelf((Block)ModBlocks.WITHERED_BLACKSTONE_STAIRS.value());
      this.add((Block)ModBlocks.WITHERED_BLACKSTONE_SLAB.value(), this::createSlabItemTable);
      this.dropSelf((Block)ModBlocks.WITHERED_BLACKSTONE_WALL.value());
      this.dropSelf((Block)ModBlocks.CRACKED_WITHERED_BLACKSTONE.value());
      this.dropSelf((Block)ModBlocks.CRACKED_WITHERED_BLACKSTONE_STAIRS.value());
      this.add((Block)ModBlocks.CRACKED_WITHERED_BLACKSTONE_SLAB.value(), this::createSlabItemTable);
      this.dropSelf((Block)ModBlocks.CRACKED_WITHERED_BLACKSTONE_WALL.value());
      this.dropSelf((Block)ModBlocks.CHISELED_WITHERED_BLACKSTONE.value());
      this.dropSelf((Block)ModBlocks.WITHERED_BASALT.value());
      this.dropSelf((Block)ModBlocks.WITHERED_COAL_BLOCK.value());
      this.dropSelf((Block)ModBlocks.WITHERED_QUARTZ_BLOCK.value());
      this.dropSelf((Block)ModBlocks.WITHERED_DEBRIS.value());
      this.dropSelf((Block)ModBlocks.SOUL_STONE.value());
      this.dropSelf((Block)ModBlocks.WITHERED_BONE_BLOCK.value());
      this.dropSelf((Block)ModBlocks.WARPED_NETHER_BRICKS.value());
      this.dropSelf((Block)ModBlocks.WARPED_NETHER_BRICK_STAIRS.value());
      this.add((Block)ModBlocks.WARPED_NETHER_BRICK_SLAB.value(), this::createSlabItemTable);
      this.dropSelf((Block)ModBlocks.WARPED_NETHER_BRICK_WALL.value());
      this.dropSelf((Block)ModBlocks.CHISELED_WARPED_NETHER_BRICKS.value());
      this.dropSelf((Block)ModBlocks.NETHERITE_BELL.value());
   }

   protected Stream<Reference<Block>> getRegistryEntries() {
      return Stream.concat(super.getRegistryEntries(), Stream.of(net.minecraft.world.level.block.Blocks.BLACKSTONE.builtInRegistryHolder()));
   }
}
