package at.petrak.hexcasting.common.lib;

import at.petrak.hexcasting.common.blocks.BlockQuenchedAllay;
import at.petrak.hexcasting.common.blocks.akashic.BlockEntityAkashicBookshelf;
import at.petrak.hexcasting.common.blocks.circles.BlockEntitySlate;
import at.petrak.hexcasting.common.blocks.circles.impetuses.BlockEntityLookingImpetus;
import at.petrak.hexcasting.common.blocks.circles.impetuses.BlockEntityRedstoneImpetus;
import at.petrak.hexcasting.common.blocks.circles.impetuses.BlockEntityRightClickImpetus;
import at.petrak.hexcasting.common.blocks.entity.BlockEntityConjured;
import at.petrak.hexcasting.common.blocks.entity.BlockEntityQuenchedAllay;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class HexBlockEntities {
   private static final Map<ResourceLocation, BlockEntityType<?>> BLOCK_ENTITIES = new LinkedHashMap<>();
   public static final BlockEntityType<BlockEntityConjured> CONJURED_TILE = register(
      "conjured", BlockEntityConjured::new, HexBlocks.CONJURED_LIGHT, HexBlocks.CONJURED_BLOCK
   );
   public static final BlockEntityType<BlockEntityAkashicBookshelf> AKASHIC_BOOKSHELF_TILE = register(
      "akashic_bookshelf", BlockEntityAkashicBookshelf::new, HexBlocks.AKASHIC_BOOKSHELF
   );
   public static final BlockEntityType<BlockEntityRedstoneImpetus> IMPETUS_REDSTONE_TILE = register(
      "impetus/redstone", BlockEntityRedstoneImpetus::new, HexBlocks.IMPETUS_REDSTONE
   );
   public static final BlockEntityType<BlockEntityLookingImpetus> IMPETUS_LOOK_TILE = register(
      "impetus/look", BlockEntityLookingImpetus::new, HexBlocks.IMPETUS_LOOK
   );
   public static final BlockEntityType<BlockEntityRightClickImpetus> IMPETUS_RIGHTCLICK_TILE = register(
      "impetus/rightclick", BlockEntityRightClickImpetus::new, HexBlocks.IMPETUS_RIGHTCLICK
   );
   public static final BlockEntityType<BlockEntitySlate> SLATE_TILE = register("slate", BlockEntitySlate::new, HexBlocks.SLATE);
   public static final BlockEntityType<BlockEntityQuenchedAllay> QUENCHED_ALLAY_TILE = register(
      "quenched_allay", BlockEntityQuenchedAllay.fromKnownBlock(HexBlocks.QUENCHED_ALLAY), HexBlocks.QUENCHED_ALLAY
   );
   public static final BlockEntityType<BlockEntityQuenchedAllay> QUENCHED_ALLAY_TILES_TILE = register(
      "quenched_allay_tiles", BlockEntityQuenchedAllay.fromKnownBlock(HexBlocks.QUENCHED_ALLAY_TILES), HexBlocks.QUENCHED_ALLAY_TILES
   );
   public static final BlockEntityType<BlockEntityQuenchedAllay> QUENCHED_ALLAY_BRICKS_TILE = register(
      "quenched_allay_bricks", BlockEntityQuenchedAllay.fromKnownBlock(HexBlocks.QUENCHED_ALLAY_BRICKS), HexBlocks.QUENCHED_ALLAY_BRICKS
   );
   public static final BlockEntityType<BlockEntityQuenchedAllay> QUENCHED_ALLAY_BRICKS_SMALL_TILE = register(
      "quenched_allay_bricks_small", BlockEntityQuenchedAllay.fromKnownBlock(HexBlocks.QUENCHED_ALLAY_BRICKS_SMALL), HexBlocks.QUENCHED_ALLAY_BRICKS_SMALL
   );

   public static void registerTiles(BiConsumer<BlockEntityType<?>, ResourceLocation> r) {
      for (Entry<ResourceLocation, BlockEntityType<?>> e : BLOCK_ENTITIES.entrySet()) {
         r.accept(e.getValue(), e.getKey());
      }
   }

   public static BlockEntityType<BlockEntityQuenchedAllay> typeForQuenchedAllay(BlockQuenchedAllay block) {
      if (block == HexBlocks.QUENCHED_ALLAY) {
         return QUENCHED_ALLAY_TILE;
      } else if (block == HexBlocks.QUENCHED_ALLAY_TILES) {
         return QUENCHED_ALLAY_TILES_TILE;
      } else if (block == HexBlocks.QUENCHED_ALLAY_BRICKS) {
         return QUENCHED_ALLAY_BRICKS_TILE;
      } else {
         return block == HexBlocks.QUENCHED_ALLAY_BRICKS_SMALL ? QUENCHED_ALLAY_BRICKS_SMALL_TILE : null;
      }
   }

   private static <T extends BlockEntity> BlockEntityType<T> register(String id, BiFunction<BlockPos, BlockState, T> func, Block... blocks) {
      BlockEntityType<T> ret = IXplatAbstractions.INSTANCE.createBlockEntityType(func, blocks);
      BlockEntityType<?> old = BLOCK_ENTITIES.put(ResourceLocation.fromNamespaceAndPath("hexcasting", id), ret);
      if (old != null) {
         throw new IllegalArgumentException("Duplicate id " + id);
      } else {
         return ret;
      }
   }
}
