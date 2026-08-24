package io.github.razordevs.deep_aether.datagen.tags;

import com.aetherteam.aether.AetherTags.Blocks;
import com.aetherteam.aether.block.AetherBlocks;
import io.github.razordevs.deep_aether.init.DABlocks;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider.IntrinsicTagAppender;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

public class DABlockTagData extends BlockTagsProvider {
   public DABlockTagData(PackOutput output, CompletableFuture<Provider> registries, @Nullable ExistingFileHelper helper) {
      super(output, registries, "deep_aether", helper);
   }

   @Nonnull
   public String getName() {
      return "Deep Aether Block Tags";
   }

   protected void addTags(@NotNull Provider provider) {
      IntrinsicTagAppender<Block> aether_block_tag = this.tag(Blocks.TREATED_AS_AETHER_BLOCK);

      for (DeferredHolder<Block, ? extends Block> block : DABlocks.BLOCKS.getEntries()) {
         aether_block_tag.add((Block)block.get());
      }

      this.tag(BlockTags.MANGROVE_LOGS_CAN_GROW_THROUGH)
         .add(
            new Block[]{
               (Block)DABlocks.AETHER_MOSS_BLOCK.get(),
               (Block)DABlocks.AETHER_MOSS_CARPET.get(),
               (Block)DABlocks.CLOUDBLOOM_CARPET.get(),
               (Block)DABlocks.YAGROOT_LOG.get(),
               (Block)DABlocks.AETHER_MUD.get(),
               (Block)DABlocks.MUDDY_YAGROOT_ROOTS.get(),
               (Block)DABlocks.YAGROOT_ROOTS.get(),
               (Block)DABlocks.TALL_AETHER_CATTAILS.get(),
               (Block)DABlocks.TALL_GLOWING_GRASS.get(),
               (Block)DABlocks.AETHER_CATTAILS.get()
            }
         );
      this.tag(BlockTags.MANGROVE_ROOTS_CAN_GROW_THROUGH)
         .add(
            new Block[]{
               (Block)DABlocks.AETHER_MOSS_BLOCK.get(),
               (Block)DABlocks.AETHER_MOSS_CARPET.get(),
               (Block)DABlocks.CLOUDBLOOM_CARPET.get(),
               (Block)DABlocks.YAGROOT_ROOTS.get()
            }
         );
      this.tag(BlockTags.SCULK_REPLACEABLE).add(new Block[]{(Block)DABlocks.ASETERITE.get(), (Block)DABlocks.RAW_CLORITE.get()});
      this.tag(BlockTags.REPLACEABLE_BY_TREES)
         .add(
            new Block[]{
               (Block)DABlocks.MINI_GOLDEN_GRASS.get(),
               (Block)DABlocks.MEDIUM_GOLDEN_GRASS.get(),
               (Block)DABlocks.SHORT_GOLDEN_GRASS.get(),
               (Block)DABlocks.TALL_GOLDEN_GRASS.get(),
               (Block)DABlocks.GOLDEN_ASPESS.get(),
               (Block)DABlocks.GOLDEN_FLOWER.get(),
               (Block)DABlocks.GOLDEN_VINES_PLANT.get(),
               (Block)DABlocks.GOLDEN_VINES.get(),
               (Block)DABlocks.ENCHANTED_BLOSSOM.get(),
               (Block)DABlocks.RADIANT_ORCHID.get(),
               (Block)DABlocks.SKY_TULIPS.get(),
               (Block)DABlocks.IASPOVE.get(),
               (Block)DABlocks.FEATHER_GRASS.get(),
               (Block)DABlocks.TALL_FEATHER_GRASS.get(),
               (Block)DABlocks.TALL_GLOWING_GRASS.get()
            }
         );
      this.tag(Blocks.AETHER_ANIMALS_SPAWNABLE_ON).add(new Block[]{(Block)DABlocks.GOLDEN_GRASS_BLOCK.get(), (Block)DABlocks.AERCLOUD_GRASS_BLOCK.get()});
      this.tag(DATags.Blocks.CAN_GOLDEN_VINES_SURVIVE_ON).add(new Block[]{(Block)AetherBlocks.QUICKSOIL.get(), net.minecraft.world.level.block.Blocks.SAND});
      this.tag(Blocks.ORES_IN_GROUND_HOLYSTONE).add((Block)DABlocks.SKYJADE_ORE.get());
      this.tag(DATags.Blocks.ROSEROOT_LOGS)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_LOG.get(),
               (Block)DABlocks.ROSEROOT_WOOD.get(),
               (Block)DABlocks.STRIPPED_ROSEROOT_LOG.get(),
               (Block)DABlocks.STRIPPED_ROSEROOT_WOOD.get()
            }
         );
      this.tag(DATags.Blocks.CRUDEROOT_LOGS)
         .add(
            new Block[]{
               (Block)DABlocks.CRUDEROOT_LOG.get(),
               (Block)DABlocks.CRUDEROOT_WOOD.get(),
               (Block)DABlocks.STRIPPED_CRUDEROOT_LOG.get(),
               (Block)DABlocks.STRIPPED_CRUDEROOT_WOOD.get()
            }
         );
      this.tag(DATags.Blocks.YAGROOT_LOGS)
         .add(
            new Block[]{
               (Block)DABlocks.YAGROOT_LOG.get(),
               (Block)DABlocks.YAGROOT_WOOD.get(),
               (Block)DABlocks.STRIPPED_YAGROOT_LOG.get(),
               (Block)DABlocks.STRIPPED_YAGROOT_WOOD.get()
            }
         );
      this.tag(DATags.Blocks.CONBERRY_LOGS)
         .add(
            new Block[]{
               (Block)DABlocks.CONBERRY_LOG.get(),
               (Block)DABlocks.CONBERRY_WOOD.get(),
               (Block)DABlocks.STRIPPED_CONBERRY_LOG.get(),
               (Block)DABlocks.STRIPPED_CONBERRY_WOOD.get()
            }
         );
      this.tag(DATags.Blocks.SUNROOT_LOGS)
         .add(
            new Block[]{
               (Block)DABlocks.SUNROOT_LOG.get(),
               (Block)DABlocks.SUNROOT_WOOD.get(),
               (Block)DABlocks.STRIPPED_SUNROOT_LOG.get(),
               (Block)DABlocks.STRIPPED_SUNROOT_WOOD.get()
            }
         );
      this.tag(BlockTags.LOGS)
         .addTags(
            new TagKey[]{
               DATags.Blocks.ROSEROOT_LOGS, DATags.Blocks.YAGROOT_LOGS, DATags.Blocks.CRUDEROOT_LOGS, DATags.Blocks.CONBERRY_LOGS, DATags.Blocks.SUNROOT_LOGS
            }
         );
      this.tag(BlockTags.LOGS_THAT_BURN)
         .addTags(
            new TagKey[]{
               DATags.Blocks.ROSEROOT_LOGS, DATags.Blocks.YAGROOT_LOGS, DATags.Blocks.CRUDEROOT_LOGS, DATags.Blocks.CONBERRY_LOGS, DATags.Blocks.SUNROOT_LOGS
            }
         );
      this.tag(DATags.Blocks.NIMBUS_BLOCKS)
         .add(
            new Block[]{
               (Block)DABlocks.NIMBUS_STONE.get(),
               (Block)DABlocks.LOCKED_NIMBUS_STONE.get(),
               (Block)DABlocks.TRAPPED_NIMBUS_STONE.get(),
               (Block)DABlocks.BOSS_DOORWAY_NIMBUS_STONE.get(),
               (Block)DABlocks.TREASURE_DOORWAY_NIMBUS_STONE.get(),
               (Block)DABlocks.NIMBUS_STAIRS.get(),
               (Block)DABlocks.NIMBUS_SLAB.get(),
               (Block)DABlocks.NIMBUS_WALL.get(),
               (Block)DABlocks.LIGHT_NIMBUS_STONE.get(),
               (Block)DABlocks.LOCKED_LIGHT_NIMBUS_STONE.get(),
               (Block)DABlocks.TRAPPED_LIGHT_NIMBUS_STONE.get(),
               (Block)DABlocks.BOSS_DOORWAY_LIGHT_NIMBUS_STONE.get(),
               (Block)DABlocks.TREASURE_DOORWAY_LIGHT_NIMBUS_STONE.get(),
               (Block)DABlocks.NIMBUS_PILLAR.get(),
               (Block)DABlocks.LOCKED_NIMBUS_PILLAR.get(),
               (Block)DABlocks.TRAPPED_NIMBUS_PILLAR.get(),
               (Block)DABlocks.BOSS_DOORWAY_NIMBUS_PILLAR.get(),
               (Block)DABlocks.TREASURE_DOORWAY_NIMBUS_PILLAR.get(),
               (Block)DABlocks.LIGHT_NIMBUS_PILLAR.get(),
               (Block)DABlocks.LOCKED_LIGHT_NIMBUS_PILLAR.get(),
               (Block)DABlocks.TRAPPED_LIGHT_NIMBUS_PILLAR.get(),
               (Block)DABlocks.BOSS_DOORWAY_LIGHT_NIMBUS_PILLAR.get(),
               (Block)DABlocks.TREASURE_DOORWAY_LIGHT_NIMBUS_PILLAR.get()
            }
         );
      this.tag(Blocks.DUNGEON_BLOCKS)
         .add(
            new Block[]{
               (Block)DABlocks.NIMBUS_STONE.get(),
               (Block)DABlocks.LIGHT_NIMBUS_STONE.get(),
               (Block)DABlocks.NIMBUS_PILLAR.get(),
               (Block)DABlocks.LIGHT_NIMBUS_PILLAR.get()
            }
         );
      this.tag(Blocks.TRAPPED_DUNGEON_BLOCKS)
         .add(
            new Block[]{
               (Block)DABlocks.TRAPPED_NIMBUS_STONE.get(),
               (Block)DABlocks.TRAPPED_LIGHT_NIMBUS_STONE.get(),
               (Block)DABlocks.TRAPPED_NIMBUS_PILLAR.get(),
               (Block)DABlocks.TRAPPED_LIGHT_NIMBUS_PILLAR.get(),
               (Block)DABlocks.TRAPPED_SKYROOT_PLANKS.get()
            }
         );
      this.tag(Blocks.LOCKED_DUNGEON_BLOCKS)
         .add(
            new Block[]{
               (Block)DABlocks.LOCKED_NIMBUS_STONE.get(),
               (Block)DABlocks.LOCKED_LIGHT_NIMBUS_STONE.get(),
               (Block)DABlocks.LOCKED_NIMBUS_PILLAR.get(),
               (Block)DABlocks.LOCKED_LIGHT_NIMBUS_PILLAR.get(),
               (Block)DABlocks.LOCKED_SKYROOT_PLANKS.get()
            }
         );
      this.tag(Blocks.BOSS_DOORWAY_DUNGEON_BLOCKS)
         .add(
            new Block[]{
               (Block)DABlocks.BOSS_DOORWAY_NIMBUS_STONE.get(),
               (Block)DABlocks.BOSS_DOORWAY_LIGHT_NIMBUS_STONE.get(),
               (Block)DABlocks.BOSS_DOORWAY_NIMBUS_PILLAR.get(),
               (Block)DABlocks.BOSS_DOORWAY_LIGHT_NIMBUS_PILLAR.get()
            }
         );
      this.tag(Blocks.TREASURE_DOORWAY_DUNGEON_BLOCKS)
         .add(
            new Block[]{
               (Block)DABlocks.TREASURE_DOORWAY_NIMBUS_STONE.get(),
               (Block)DABlocks.TREASURE_DOORWAY_LIGHT_NIMBUS_STONE.get(),
               (Block)DABlocks.TREASURE_DOORWAY_NIMBUS_PILLAR.get(),
               (Block)DABlocks.TREASURE_DOORWAY_LIGHT_NIMBUS_PILLAR.get()
            }
         );
      this.tag(BlockTags.ALL_SIGNS)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_SIGN.get(),
               (Block)DABlocks.YAGROOT_SIGN.get(),
               (Block)DABlocks.CRUDEROOT_SIGN.get(),
               (Block)DABlocks.CONBERRY_SIGN.get(),
               (Block)DABlocks.SUNROOT_SIGN.get(),
               (Block)DABlocks.ROSEROOT_WALL_SIGN.get(),
               (Block)DABlocks.YAGROOT_WALL_SIGN.get(),
               (Block)DABlocks.CRUDEROOT_WALL_SIGN.get(),
               (Block)DABlocks.CONBERRY_WALL_SIGN.get(),
               (Block)DABlocks.SUNROOT_WALL_SIGN.get()
            }
         );
      this.tag(BlockTags.WALL_SIGNS)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_WALL_SIGN.get(),
               (Block)DABlocks.YAGROOT_WALL_SIGN.get(),
               (Block)DABlocks.CRUDEROOT_WALL_SIGN.get(),
               (Block)DABlocks.CONBERRY_WALL_SIGN.get(),
               (Block)DABlocks.SUNROOT_WALL_SIGN.get()
            }
         );
      this.tag(BlockTags.STANDING_SIGNS)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_SIGN.get(),
               (Block)DABlocks.YAGROOT_SIGN.get(),
               (Block)DABlocks.CRUDEROOT_SIGN.get(),
               (Block)DABlocks.CONBERRY_SIGN.get(),
               (Block)DABlocks.SUNROOT_SIGN.get()
            }
         );
      this.tag(BlockTags.CEILING_HANGING_SIGNS)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_HANGING_SIGN.get(),
               (Block)DABlocks.YAGROOT_HANGING_SIGN.get(),
               (Block)DABlocks.CRUDEROOT_HANGING_SIGN.get(),
               (Block)DABlocks.CONBERRY_HANGING_SIGN.get(),
               (Block)DABlocks.SUNROOT_HANGING_SIGN.get()
            }
         );
      this.tag(BlockTags.WALL_HANGING_SIGNS)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_WALL_HANGING_SIGN.get(),
               (Block)DABlocks.YAGROOT_WALL_HANGING_SIGN.get(),
               (Block)DABlocks.CRUDEROOT_WALL_HANGING_SIGN.get(),
               (Block)DABlocks.CONBERRY_WALL_HANGING_SIGN.get(),
               (Block)DABlocks.SUNROOT_WALL_HANGING_SIGN.get()
            }
         );
      this.tag(BlockTags.ALL_HANGING_SIGNS)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_WALL_HANGING_SIGN.get(),
               (Block)DABlocks.YAGROOT_WALL_HANGING_SIGN.get(),
               (Block)DABlocks.CRUDEROOT_WALL_HANGING_SIGN.get(),
               (Block)DABlocks.CONBERRY_WALL_HANGING_SIGN.get(),
               (Block)DABlocks.SUNROOT_WALL_HANGING_SIGN.get(),
               (Block)DABlocks.ROSEROOT_HANGING_SIGN.get(),
               (Block)DABlocks.YAGROOT_HANGING_SIGN.get(),
               (Block)DABlocks.CRUDEROOT_HANGING_SIGN.get(),
               (Block)DABlocks.CONBERRY_HANGING_SIGN.get(),
               (Block)DABlocks.SUNROOT_HANGING_SIGN.get()
            }
         );
      this.tag(BlockTags.WOODEN_STAIRS)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_STAIRS.get(),
               (Block)DABlocks.YAGROOT_STAIRS.get(),
               (Block)DABlocks.CRUDEROOT_STAIRS.get(),
               (Block)DABlocks.CONBERRY_STAIRS.get(),
               (Block)DABlocks.SUNROOT_STAIRS.get()
            }
         );
      this.tag(BlockTags.STAIRS)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_STAIRS.get(),
               (Block)DABlocks.YAGROOT_STAIRS.get(),
               (Block)DABlocks.CRUDEROOT_STAIRS.get(),
               (Block)DABlocks.CONBERRY_STAIRS.get(),
               (Block)DABlocks.SUNROOT_STAIRS.get(),
               (Block)DABlocks.RAW_CLORITE_STAIRS.get(),
               (Block)DABlocks.CLORITE_STAIRS.get(),
               (Block)DABlocks.POLISHED_CLORITE_STAIRS.get(),
               (Block)DABlocks.COBBLED_ASETERITE_STAIRS.get(),
               (Block)DABlocks.ASETERITE_STAIRS.get(),
               (Block)DABlocks.POLISHED_ASETERITE_STAIRS.get(),
               (Block)DABlocks.ASETERITE_BRICKS_STAIRS.get(),
               (Block)DABlocks.BIG_HOLYSTONE_BRICKS_STAIRS.get(),
               (Block)DABlocks.AETHER_MUD_BRICKS_STAIRS.get(),
               (Block)DABlocks.HOLYSTONE_TILE_STAIRS.get(),
               (Block)DABlocks.MOSSY_HOLYSTONE_BRICK_STAIRS.get(),
               (Block)DABlocks.MOSSY_HOLYSTONE_TILE_STAIRS.get(),
               (Block)DABlocks.GILDED_HOLYSTONE_BRICK_STAIRS.get(),
               (Block)DABlocks.GILDED_HOLYSTONE_TILE_STAIRS.get(),
               (Block)DABlocks.BLIGHTMOSS_HOLYSTONE_BRICK_STAIRS.get(),
               (Block)DABlocks.BLIGHTMOSS_HOLYSTONE_TILE_STAIRS.get(),
               (Block)DABlocks.NIMBUS_STAIRS.get()
            }
         );
      this.tag(BlockTags.WOODEN_SLABS)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_SLAB.get(),
               (Block)DABlocks.YAGROOT_SLAB.get(),
               (Block)DABlocks.CRUDEROOT_SLAB.get(),
               (Block)DABlocks.CONBERRY_SLAB.get(),
               (Block)DABlocks.SUNROOT_SLAB.get()
            }
         );
      this.tag(BlockTags.SLABS)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_SLAB.get(),
               (Block)DABlocks.YAGROOT_SLAB.get(),
               (Block)DABlocks.CRUDEROOT_SLAB.get(),
               (Block)DABlocks.CONBERRY_SLAB.get(),
               (Block)DABlocks.SUNROOT_SLAB.get(),
               (Block)DABlocks.RAW_CLORITE_SLAB.get(),
               (Block)DABlocks.CLORITE_SLAB.get(),
               (Block)DABlocks.POLISHED_CLORITE_SLAB.get(),
               (Block)DABlocks.COBBLED_ASETERITE_SLAB.get(),
               (Block)DABlocks.ASETERITE_SLAB.get(),
               (Block)DABlocks.POLISHED_ASETERITE_SLAB.get(),
               (Block)DABlocks.ASETERITE_BRICKS_SLAB.get(),
               (Block)DABlocks.BIG_HOLYSTONE_BRICKS_SLAB.get(),
               (Block)DABlocks.AETHER_MUD_BRICKS_SLAB.get(),
               (Block)DABlocks.HOLYSTONE_TILE_SLAB.get(),
               (Block)DABlocks.MOSSY_HOLYSTONE_BRICK_SLAB.get(),
               (Block)DABlocks.MOSSY_HOLYSTONE_TILE_SLAB.get(),
               (Block)DABlocks.GILDED_HOLYSTONE_BRICK_SLAB.get(),
               (Block)DABlocks.GILDED_HOLYSTONE_TILE_SLAB.get(),
               (Block)DABlocks.BLIGHTMOSS_HOLYSTONE_BRICK_SLAB.get(),
               (Block)DABlocks.BLIGHTMOSS_HOLYSTONE_TILE_SLAB.get(),
               (Block)DABlocks.NIMBUS_SLAB.get()
            }
         );
      this.tag(BlockTags.FENCES)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_FENCE.get(),
               (Block)DABlocks.YAGROOT_FENCE.get(),
               (Block)DABlocks.CRUDEROOT_FENCE.get(),
               (Block)DABlocks.CONBERRY_FENCE.get(),
               (Block)DABlocks.SUNROOT_FENCE.get()
            }
         );
      this.tag(BlockTags.WOODEN_FENCES)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_FENCE.get(),
               (Block)DABlocks.YAGROOT_FENCE.get(),
               (Block)DABlocks.CRUDEROOT_FENCE.get(),
               (Block)DABlocks.CONBERRY_FENCE.get(),
               (Block)DABlocks.SUNROOT_FENCE.get()
            }
         );
      this.tag(net.neoforged.neoforge.common.Tags.Blocks.FENCES)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_FENCE.get(),
               (Block)DABlocks.YAGROOT_FENCE.get(),
               (Block)DABlocks.CRUDEROOT_FENCE.get(),
               (Block)DABlocks.CONBERRY_FENCE.get(),
               (Block)DABlocks.SUNROOT_FENCE.get()
            }
         );
      this.tag(net.neoforged.neoforge.common.Tags.Blocks.FENCES_WOODEN)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_FENCE.get(),
               (Block)DABlocks.YAGROOT_FENCE.get(),
               (Block)DABlocks.CRUDEROOT_FENCE.get(),
               (Block)DABlocks.CONBERRY_FENCE.get(),
               (Block)DABlocks.SUNROOT_FENCE.get()
            }
         );
      this.tag(BlockTags.FENCE_GATES)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_FENCE_GATE.get(),
               (Block)DABlocks.YAGROOT_FENCE_GATE.get(),
               (Block)DABlocks.CRUDEROOT_FENCE_GATE.get(),
               (Block)DABlocks.CONBERRY_FENCE_GATE.get(),
               (Block)DABlocks.SUNROOT_FENCE_GATE.get()
            }
         );
      this.tag(net.neoforged.neoforge.common.Tags.Blocks.FENCE_GATES)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_FENCE_GATE.get(),
               (Block)DABlocks.YAGROOT_FENCE_GATE.get(),
               (Block)DABlocks.CRUDEROOT_FENCE_GATE.get(),
               (Block)DABlocks.CONBERRY_FENCE_GATE.get(),
               (Block)DABlocks.SUNROOT_FENCE_GATE.get()
            }
         );
      this.tag(net.neoforged.neoforge.common.Tags.Blocks.FENCE_GATES_WOODEN)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_FENCE_GATE.get(),
               (Block)DABlocks.YAGROOT_FENCE_GATE.get(),
               (Block)DABlocks.CRUDEROOT_FENCE_GATE.get(),
               (Block)DABlocks.CONBERRY_FENCE_GATE.get(),
               (Block)DABlocks.SUNROOT_FENCE_GATE.get()
            }
         );
      this.tag(BlockTags.DOORS)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_DOOR.get(),
               (Block)DABlocks.YAGROOT_DOOR.get(),
               (Block)DABlocks.CRUDEROOT_DOOR.get(),
               (Block)DABlocks.CONBERRY_DOOR.get(),
               (Block)DABlocks.SUNROOT_DOOR.get()
            }
         );
      this.tag(BlockTags.WOODEN_DOORS)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_DOOR.get(),
               (Block)DABlocks.YAGROOT_DOOR.get(),
               (Block)DABlocks.CRUDEROOT_DOOR.get(),
               (Block)DABlocks.CONBERRY_DOOR.get(),
               (Block)DABlocks.SUNROOT_DOOR.get()
            }
         );
      this.tag(BlockTags.MOB_INTERACTABLE_DOORS)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_DOOR.get(),
               (Block)DABlocks.YAGROOT_DOOR.get(),
               (Block)DABlocks.CRUDEROOT_DOOR.get(),
               (Block)DABlocks.CONBERRY_DOOR.get(),
               (Block)DABlocks.SUNROOT_DOOR.get()
            }
         );
      this.tag(BlockTags.TRAPDOORS)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_TRAPDOOR.get(),
               (Block)DABlocks.YAGROOT_TRAPDOOR.get(),
               (Block)DABlocks.CRUDEROOT_TRAPDOOR.get(),
               (Block)DABlocks.CONBERRY_TRAPDOOR.get(),
               (Block)DABlocks.SUNROOT_TRAPDOOR.get()
            }
         );
      this.tag(BlockTags.WOODEN_TRAPDOORS)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_TRAPDOOR.get(),
               (Block)DABlocks.YAGROOT_TRAPDOOR.get(),
               (Block)DABlocks.CRUDEROOT_TRAPDOOR.get(),
               (Block)DABlocks.CONBERRY_TRAPDOOR.get(),
               (Block)DABlocks.SUNROOT_TRAPDOOR.get()
            }
         );
      this.tag(BlockTags.PRESSURE_PLATES)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_PRESSURE_PLATE.get(),
               (Block)DABlocks.YAGROOT_PRESSURE_PLATE.get(),
               (Block)DABlocks.CRUDEROOT_PRESSURE_PLATE.get(),
               (Block)DABlocks.CONBERRY_PRESSURE_PLATE.get(),
               (Block)DABlocks.SUNROOT_PRESSURE_PLATE.get()
            }
         );
      this.tag(BlockTags.WOODEN_PRESSURE_PLATES)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_PRESSURE_PLATE.get(),
               (Block)DABlocks.YAGROOT_PRESSURE_PLATE.get(),
               (Block)DABlocks.CRUDEROOT_PRESSURE_PLATE.get(),
               (Block)DABlocks.CONBERRY_PRESSURE_PLATE.get(),
               (Block)DABlocks.SUNROOT_PRESSURE_PLATE.get()
            }
         );
      this.tag(BlockTags.BUTTONS)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_BUTTON.get(),
               (Block)DABlocks.YAGROOT_BUTTON.get(),
               (Block)DABlocks.CRUDEROOT_BUTTON.get(),
               (Block)DABlocks.CONBERRY_BUTTON.get(),
               (Block)DABlocks.SUNROOT_BUTTON.get()
            }
         );
      this.tag(BlockTags.WOODEN_BUTTONS)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_BUTTON.get(),
               (Block)DABlocks.YAGROOT_BUTTON.get(),
               (Block)DABlocks.CRUDEROOT_BUTTON.get(),
               (Block)DABlocks.CONBERRY_BUTTON.get(),
               (Block)DABlocks.SUNROOT_BUTTON.get()
            }
         );
      this.tag(BlockTags.WALLS)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_WOOD_WALL.get(),
               (Block)DABlocks.STRIPPED_ROSEROOT_WOOD_WALL.get(),
               (Block)DABlocks.YAGROOT_WOOD_WALL.get(),
               (Block)DABlocks.STRIPPED_YAGROOT_WOOD_WALL.get(),
               (Block)DABlocks.CRUDEROOT_WOOD_WALL.get(),
               (Block)DABlocks.STRIPPED_CRUDEROOT_WOOD_WALL.get(),
               (Block)DABlocks.CONBERRY_WOOD_WALL.get(),
               (Block)DABlocks.STRIPPED_CONBERRY_WOOD_WALL.get(),
               (Block)DABlocks.SUNROOT_WOOD_WALL.get(),
               (Block)DABlocks.STRIPPED_SUNROOT_WOOD_WALL.get(),
               (Block)DABlocks.CLORITE_WALL.get(),
               (Block)DABlocks.RAW_CLORITE_WALL.get(),
               (Block)DABlocks.POLISHED_CLORITE_WALL.get(),
               (Block)DABlocks.ASETERITE_WALL.get(),
               (Block)DABlocks.COBBLED_ASETERITE_WALL.get(),
               (Block)DABlocks.POLISHED_ASETERITE_WALL.get(),
               (Block)DABlocks.ASETERITE_BRICKS_WALL.get(),
               (Block)DABlocks.BIG_HOLYSTONE_BRICKS_WALL.get(),
               (Block)DABlocks.AETHER_MUD_BRICKS_WALL.get(),
               (Block)DABlocks.HOLYSTONE_TILE_WALL.get(),
               (Block)DABlocks.MOSSY_HOLYSTONE_BRICK_WALL.get(),
               (Block)DABlocks.MOSSY_HOLYSTONE_TILE_WALL.get(),
               (Block)DABlocks.GILDED_HOLYSTONE_BRICK_WALL.get(),
               (Block)DABlocks.GILDED_HOLYSTONE_TILE_WALL.get(),
               (Block)DABlocks.BLIGHTMOSS_HOLYSTONE_BRICK_WALL.get(),
               (Block)DABlocks.BLIGHTMOSS_HOLYSTONE_TILE_WALL.get(),
               (Block)DABlocks.ROSEROOT_LOG_WALL.get(),
               (Block)DABlocks.STRIPPED_ROSEROOT_LOG_WALL.get(),
               (Block)DABlocks.YAGROOT_LOG_WALL.get(),
               (Block)DABlocks.STRIPPED_YAGROOT_LOG_WALL.get(),
               (Block)DABlocks.CRUDEROOT_LOG_WALL.get(),
               (Block)DABlocks.STRIPPED_CRUDEROOT_LOG_WALL.get(),
               (Block)DABlocks.CONBERRY_LOG_WALL.get(),
               (Block)DABlocks.STRIPPED_CONBERRY_LOG_WALL.get(),
               (Block)DABlocks.SUNROOT_LOG_WALL.get(),
               (Block)DABlocks.STRIPPED_SUNROOT_LOG_WALL.get(),
               (Block)DABlocks.NIMBUS_WALL.get()
            }
         );
      this.tag(BlockTags.LEAVES)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_LEAVES.get(),
               (Block)DABlocks.BLUE_ROSEROOT_LEAVES.get(),
               (Block)DABlocks.FLOWERING_ROSEROOT_LEAVES.get(),
               (Block)DABlocks.FLOWERING_BLUE_ROSEROOT_LEAVES.get(),
               (Block)DABlocks.YAGROOT_LEAVES.get(),
               (Block)DABlocks.CRUDEROOT_LEAVES.get(),
               (Block)DABlocks.CONBERRY_LEAVES.get(),
               (Block)DABlocks.SUNROOT_LEAVES.get()
            }
         );
      this.tag(BlockTags.SAPLINGS)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_SAPLING.get(),
               (Block)DABlocks.BLUE_ROSEROOT_SAPLING.get(),
               (Block)DABlocks.YAGROOT_SAPLING.get(),
               (Block)DABlocks.CONBERRY_SAPLING.get(),
               (Block)DABlocks.CRUDEROOT_SAPLING.get(),
               (Block)DABlocks.SUNROOT_SAPLING.get()
            }
         );
      this.tag(BlockTags.FLOWERS)
         .add(
            new Block[]{
               (Block)DABlocks.FLOWERING_ROSEROOT_LEAVES.get(),
               (Block)DABlocks.FLOWERING_BLUE_ROSEROOT_LEAVES.get(),
               (Block)DABlocks.AERLAVENDER.get(),
               (Block)DABlocks.TALL_AERLAVENDER.get(),
               (Block)DABlocks.AETHER_CATTAILS.get(),
               (Block)DABlocks.TALL_AETHER_CATTAILS.get(),
               (Block)DABlocks.GOLDEN_FLOWER.get(),
               (Block)DABlocks.RADIANT_ORCHID.get(),
               (Block)DABlocks.ENCHANTED_BLOSSOM.get(),
               (Block)DABlocks.SKY_TULIPS.get(),
               (Block)DABlocks.IASPOVE.get(),
               (Block)DABlocks.GOLDEN_ASPESS.get(),
               (Block)DABlocks.ECHAISY.get(),
               (Block)DABlocks.TALL_ALIEN_PLANT.get()
            }
         );
      this.tag(BlockTags.SMALL_FLOWERS)
         .add(
            new Block[]{
               (Block)DABlocks.AERLAVENDER.get(),
               (Block)DABlocks.AETHER_CATTAILS.get(),
               (Block)DABlocks.GOLDEN_FLOWER.get(),
               (Block)DABlocks.RADIANT_ORCHID.get(),
               (Block)DABlocks.ENCHANTED_BLOSSOM.get(),
               (Block)DABlocks.SKY_TULIPS.get(),
               (Block)DABlocks.IASPOVE.get(),
               (Block)DABlocks.GOLDEN_ASPESS.get(),
               (Block)DABlocks.ECHAISY.get()
            }
         );
      this.tag(BlockTags.TALL_FLOWERS)
         .add(new Block[]{(Block)DABlocks.TALL_AERLAVENDER.get(), (Block)DABlocks.TALL_AETHER_CATTAILS.get(), (Block)DABlocks.TALL_ALIEN_PLANT.get()});
      this.tag(BlockTags.MINEABLE_WITH_SHOVEL)
         .add(
            new Block[]{
               (Block)DABlocks.AETHER_MUD.get(),
               (Block)DABlocks.MUDDY_YAGROOT_ROOTS.get(),
               (Block)DABlocks.GOLDEN_GRASS_BLOCK.get(),
               (Block)DABlocks.GOLDEN_DIRT_PATH.get(),
               (Block)DABlocks.AETHER_COARSE_DIRT.get()
            }
         );
      this.tag(BlockTags.MINEABLE_WITH_HOE)
         .add(
            new Block[]{
               (Block)DABlocks.YAGROOT_LEAVES.get(),
               (Block)DABlocks.FLOWERING_ROSEROOT_LEAVES.get(),
               (Block)DABlocks.ROSEROOT_LEAVES.get(),
               (Block)DABlocks.FLOWERING_BLUE_ROSEROOT_LEAVES.get(),
               (Block)DABlocks.BLUE_ROSEROOT_LEAVES.get(),
               (Block)DABlocks.CRUDEROOT_LEAVES.get(),
               (Block)DABlocks.AETHER_MOSS_BLOCK.get(),
               (Block)DABlocks.AETHER_MOSS_CARPET.get(),
               (Block)DABlocks.CLOUDBLOOM_CARPET.get(),
               (Block)DABlocks.AERGLOW_BLOSSOM_BLOCK.get(),
               (Block)DABlocks.CONBERRY_LEAVES.get(),
               (Block)DABlocks.SUNROOT_LEAVES.get(),
               (Block)DABlocks.SUNROOT_HANGER.get(),
               (Block)DABlocks.CHROMATIC_AERCLOUD.get(),
               (Block)DABlocks.STERLING_AERCLOUD.get(),
               (Block)DABlocks.AERSMOG.get(),
               (Block)DABlocks.AERCLOUD_GRASS_BLOCK.get(),
               (Block)DABlocks.RAIN_AERCLOUD.get()
            }
         );
      this.tag(BlockTags.MINEABLE_WITH_AXE)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_LOG.get(),
               (Block)DABlocks.ROSEROOT_WOOD.get(),
               (Block)DABlocks.STRIPPED_ROSEROOT_LOG.get(),
               (Block)DABlocks.STRIPPED_ROSEROOT_WOOD.get(),
               (Block)DABlocks.ROSEROOT_PLANKS.get(),
               (Block)DABlocks.ROSEROOT_STAIRS.get(),
               (Block)DABlocks.ROSEROOT_SLAB.get(),
               (Block)DABlocks.ROSEROOT_FENCE.get(),
               (Block)DABlocks.ROSEROOT_FENCE_GATE.get(),
               (Block)DABlocks.ROSEROOT_DOOR.get(),
               (Block)DABlocks.ROSEROOT_TRAPDOOR.get(),
               (Block)DABlocks.ROSEROOT_PRESSURE_PLATE.get(),
               (Block)DABlocks.ROSEROOT_BUTTON.get(),
               (Block)DABlocks.ROSEROOT_WOOD_WALL.get(),
               (Block)DABlocks.STRIPPED_ROSEROOT_WOOD_WALL.get(),
               (Block)DABlocks.ROSEROOT_LOG_WALL.get(),
               (Block)DABlocks.STRIPPED_ROSEROOT_LOG_WALL.get(),
               (Block)DABlocks.ROSEROOT_SAPLING.get(),
               (Block)DABlocks.ROSEROOT_SIGN.get(),
               (Block)DABlocks.ROSEROOT_WALL_SIGN.get(),
               (Block)DABlocks.ROSEROOT_HANGING_SIGN.get(),
               (Block)DABlocks.ROSEROOT_WALL_HANGING_SIGN.get(),
               (Block)DABlocks.YAGROOT_LOG.get(),
               (Block)DABlocks.YAGROOT_WOOD.get(),
               (Block)DABlocks.STRIPPED_YAGROOT_LOG.get(),
               (Block)DABlocks.STRIPPED_YAGROOT_WOOD.get(),
               (Block)DABlocks.YAGROOT_PLANKS.get(),
               (Block)DABlocks.YAGROOT_STAIRS.get(),
               (Block)DABlocks.YAGROOT_SLAB.get(),
               (Block)DABlocks.YAGROOT_FENCE.get(),
               (Block)DABlocks.YAGROOT_FENCE_GATE.get(),
               (Block)DABlocks.YAGROOT_DOOR.get(),
               (Block)DABlocks.YAGROOT_TRAPDOOR.get(),
               (Block)DABlocks.YAGROOT_PRESSURE_PLATE.get(),
               (Block)DABlocks.YAGROOT_BUTTON.get(),
               (Block)DABlocks.YAGROOT_WOOD_WALL.get(),
               (Block)DABlocks.STRIPPED_YAGROOT_WOOD_WALL.get(),
               (Block)DABlocks.YAGROOT_LOG_WALL.get(),
               (Block)DABlocks.STRIPPED_YAGROOT_LOG_WALL.get(),
               (Block)DABlocks.YAGROOT_SAPLING.get(),
               (Block)DABlocks.YAGROOT_SIGN.get(),
               (Block)DABlocks.YAGROOT_WALL_SIGN.get(),
               (Block)DABlocks.YAGROOT_HANGING_SIGN.get(),
               (Block)DABlocks.YAGROOT_WALL_HANGING_SIGN.get(),
               (Block)DABlocks.CRUDEROOT_LOG.get(),
               (Block)DABlocks.CRUDEROOT_WOOD.get(),
               (Block)DABlocks.STRIPPED_CRUDEROOT_LOG.get(),
               (Block)DABlocks.STRIPPED_CRUDEROOT_WOOD.get(),
               (Block)DABlocks.CRUDEROOT_PLANKS.get(),
               (Block)DABlocks.CRUDEROOT_STAIRS.get(),
               (Block)DABlocks.CRUDEROOT_SLAB.get(),
               (Block)DABlocks.CRUDEROOT_FENCE.get(),
               (Block)DABlocks.CRUDEROOT_FENCE_GATE.get(),
               (Block)DABlocks.CRUDEROOT_DOOR.get(),
               (Block)DABlocks.CRUDEROOT_TRAPDOOR.get(),
               (Block)DABlocks.CRUDEROOT_PRESSURE_PLATE.get(),
               (Block)DABlocks.CRUDEROOT_BUTTON.get(),
               (Block)DABlocks.CRUDEROOT_WOOD_WALL.get(),
               (Block)DABlocks.STRIPPED_CRUDEROOT_WOOD_WALL.get(),
               (Block)DABlocks.CRUDEROOT_LOG_WALL.get(),
               (Block)DABlocks.STRIPPED_CRUDEROOT_LOG_WALL.get(),
               (Block)DABlocks.CRUDEROOT_SAPLING.get(),
               (Block)DABlocks.CRUDEROOT_SIGN.get(),
               (Block)DABlocks.CRUDEROOT_WALL_SIGN.get(),
               (Block)DABlocks.CRUDEROOT_HANGING_SIGN.get(),
               (Block)DABlocks.CRUDEROOT_WALL_HANGING_SIGN.get(),
               (Block)DABlocks.CONBERRY_LOG.get(),
               (Block)DABlocks.CONBERRY_WOOD.get(),
               (Block)DABlocks.STRIPPED_CONBERRY_LOG.get(),
               (Block)DABlocks.STRIPPED_CONBERRY_WOOD.get(),
               (Block)DABlocks.CONBERRY_PLANKS.get(),
               (Block)DABlocks.CONBERRY_STAIRS.get(),
               (Block)DABlocks.CONBERRY_SLAB.get(),
               (Block)DABlocks.CONBERRY_FENCE.get(),
               (Block)DABlocks.CONBERRY_FENCE_GATE.get(),
               (Block)DABlocks.CONBERRY_DOOR.get(),
               (Block)DABlocks.CONBERRY_TRAPDOOR.get(),
               (Block)DABlocks.CONBERRY_PRESSURE_PLATE.get(),
               (Block)DABlocks.CONBERRY_BUTTON.get(),
               (Block)DABlocks.CONBERRY_WOOD_WALL.get(),
               (Block)DABlocks.STRIPPED_CONBERRY_WOOD_WALL.get(),
               (Block)DABlocks.CONBERRY_LOG_WALL.get(),
               (Block)DABlocks.STRIPPED_CONBERRY_LOG_WALL.get(),
               (Block)DABlocks.CONBERRY_SAPLING.get(),
               (Block)DABlocks.CONBERRY_SIGN.get(),
               (Block)DABlocks.CONBERRY_WALL_SIGN.get(),
               (Block)DABlocks.CONBERRY_HANGING_SIGN.get(),
               (Block)DABlocks.CONBERRY_WALL_HANGING_SIGN.get(),
               (Block)DABlocks.SUNROOT_LOG.get(),
               (Block)DABlocks.SUNROOT_WOOD.get(),
               (Block)DABlocks.STRIPPED_SUNROOT_LOG.get(),
               (Block)DABlocks.STRIPPED_SUNROOT_WOOD.get(),
               (Block)DABlocks.SUNROOT_PLANKS.get(),
               (Block)DABlocks.SUNROOT_STAIRS.get(),
               (Block)DABlocks.SUNROOT_SLAB.get(),
               (Block)DABlocks.SUNROOT_FENCE.get(),
               (Block)DABlocks.SUNROOT_FENCE_GATE.get(),
               (Block)DABlocks.SUNROOT_DOOR.get(),
               (Block)DABlocks.SUNROOT_TRAPDOOR.get(),
               (Block)DABlocks.SUNROOT_PRESSURE_PLATE.get(),
               (Block)DABlocks.SUNROOT_BUTTON.get(),
               (Block)DABlocks.SUNROOT_WOOD_WALL.get(),
               (Block)DABlocks.STRIPPED_SUNROOT_WOOD_WALL.get(),
               (Block)DABlocks.SUNROOT_LOG_WALL.get(),
               (Block)DABlocks.STRIPPED_SUNROOT_LOG_WALL.get(),
               (Block)DABlocks.SUNROOT_SAPLING.get(),
               (Block)DABlocks.SUNROOT_SIGN.get(),
               (Block)DABlocks.SUNROOT_WALL_SIGN.get(),
               (Block)DABlocks.SUNROOT_HANGING_SIGN.get(),
               (Block)DABlocks.SUNROOT_WALL_HANGING_SIGN.get(),
               (Block)DABlocks.ROTTEN_ROSEROOT_LOG.get(),
               (Block)DABlocks.YAGROOT_ROOTS.get(),
               (Block)DABlocks.BLUE_SQUASH.get(),
               (Block)DABlocks.GREEN_SQUASH.get(),
               (Block)DABlocks.PURPLE_SQUASH.get(),
               (Block)DABlocks.CARVED_BLUE_SQUASH.get(),
               (Block)DABlocks.CARVED_GREEN_SQUASH.get(),
               (Block)DABlocks.CARVED_PURPLE_SQUASH.get(),
               (Block)DABlocks.LIGHTCAP_MUSHROOMS.get(),
               (Block)DABlocks.LIGHTCAP_MUSHROOM_BLOCK.get()
            }
         );
      this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
         .add(
            new Block[]{
               (Block)DABlocks.COBBLED_ASETERITE.get(),
               (Block)DABlocks.COBBLED_ASETERITE_SLAB.get(),
               (Block)DABlocks.COBBLED_ASETERITE_STAIRS.get(),
               (Block)DABlocks.COBBLED_ASETERITE_WALL.get(),
               (Block)DABlocks.ASETERITE.get(),
               (Block)DABlocks.ASETERITE_SLAB.get(),
               (Block)DABlocks.ASETERITE_STAIRS.get(),
               (Block)DABlocks.ASETERITE_WALL.get(),
               (Block)DABlocks.POLISHED_ASETERITE.get(),
               (Block)DABlocks.POLISHED_ASETERITE_STAIRS.get(),
               (Block)DABlocks.POLISHED_ASETERITE_SLAB.get(),
               (Block)DABlocks.POLISHED_ASETERITE_WALL.get(),
               (Block)DABlocks.ASETERITE_BRICKS.get(),
               (Block)DABlocks.ASETERITE_BRICKS_SLAB.get(),
               (Block)DABlocks.ASETERITE_BRICKS_STAIRS.get(),
               (Block)DABlocks.ASETERITE_BRICKS_WALL.get(),
               (Block)DABlocks.RAW_CLORITE.get(),
               (Block)DABlocks.RAW_CLORITE_SLAB.get(),
               (Block)DABlocks.RAW_CLORITE_STAIRS.get(),
               (Block)DABlocks.RAW_CLORITE_WALL.get(),
               (Block)DABlocks.CLORITE.get(),
               (Block)DABlocks.CLORITE_STAIRS.get(),
               (Block)DABlocks.CLORITE_SLAB.get(),
               (Block)DABlocks.CLORITE_WALL.get(),
               (Block)DABlocks.POLISHED_CLORITE.get(),
               (Block)DABlocks.POLISHED_CLORITE_STAIRS.get(),
               (Block)DABlocks.POLISHED_CLORITE_SLAB.get(),
               (Block)DABlocks.POLISHED_CLORITE_WALL.get(),
               (Block)DABlocks.CLORITE_PILLAR.get(),
               (Block)DABlocks.SKYJADE_CHAIN.get(),
               (Block)DABlocks.SKYJADE_LANTERN.get(),
               (Block)DABlocks.AMBROSIUM_TIKI_TORCH.get(),
               (Block)DABlocks.COMBINER.get(),
               (Block)DABlocks.SKYJADE_ORE.get(),
               (Block)DABlocks.SKYJADE_BLOCK.get(),
               (Block)DABlocks.STRATUS_BLOCK.get(),
               (Block)DABlocks.SQUALL_BLOCK.get(),
               (Block)DABlocks.PACKED_AETHER_MUD.get(),
               (Block)DABlocks.AETHER_MUD_BRICKS.get(),
               (Block)DABlocks.AETHER_MUD_BRICKS_SLAB.get(),
               (Block)DABlocks.AETHER_MUD_BRICKS_STAIRS.get(),
               (Block)DABlocks.AETHER_MUD_BRICKS_WALL.get(),
               (Block)DABlocks.HOLYSTONE_TILES.get(),
               (Block)DABlocks.HOLYSTONE_TILE_SLAB.get(),
               (Block)DABlocks.HOLYSTONE_TILE_STAIRS.get(),
               (Block)DABlocks.HOLYSTONE_TILE_WALL.get(),
               (Block)DABlocks.BIG_HOLYSTONE_BRICKS.get(),
               (Block)DABlocks.BIG_HOLYSTONE_BRICKS_SLAB.get(),
               (Block)DABlocks.BIG_HOLYSTONE_BRICKS_STAIRS.get(),
               (Block)DABlocks.BIG_HOLYSTONE_BRICKS_WALL.get(),
               (Block)DABlocks.CHISELED_HOLYSTONE.get(),
               (Block)DABlocks.HOLYSTONE_PILLAR.get(),
               (Block)DABlocks.HOLYSTONE_PILLAR_UP.get(),
               (Block)DABlocks.HOLYSTONE_PILLAR_DOWN.get(),
               (Block)DABlocks.MOSSY_HOLYSTONE_BRICKS.get(),
               (Block)DABlocks.MOSSY_HOLYSTONE_BRICK_SLAB.get(),
               (Block)DABlocks.MOSSY_HOLYSTONE_BRICK_STAIRS.get(),
               (Block)DABlocks.MOSSY_HOLYSTONE_BRICK_WALL.get(),
               (Block)DABlocks.MOSSY_HOLYSTONE_TILES.get(),
               (Block)DABlocks.MOSSY_HOLYSTONE_TILE_SLAB.get(),
               (Block)DABlocks.MOSSY_HOLYSTONE_TILE_STAIRS.get(),
               (Block)DABlocks.MOSSY_HOLYSTONE_TILE_WALL.get(),
               (Block)DABlocks.GILDED_HOLYSTONE_BRICKS.get(),
               (Block)DABlocks.GILDED_HOLYSTONE_BRICK_SLAB.get(),
               (Block)DABlocks.GILDED_HOLYSTONE_BRICK_STAIRS.get(),
               (Block)DABlocks.GILDED_HOLYSTONE_BRICK_WALL.get(),
               (Block)DABlocks.GILDED_HOLYSTONE_TILES.get(),
               (Block)DABlocks.GILDED_HOLYSTONE_TILE_SLAB.get(),
               (Block)DABlocks.GILDED_HOLYSTONE_TILE_STAIRS.get(),
               (Block)DABlocks.GILDED_HOLYSTONE_TILE_WALL.get(),
               (Block)DABlocks.BLIGHTMOSS_HOLYSTONE_BRICKS.get(),
               (Block)DABlocks.BLIGHTMOSS_HOLYSTONE_BRICK_SLAB.get(),
               (Block)DABlocks.BLIGHTMOSS_HOLYSTONE_BRICK_STAIRS.get(),
               (Block)DABlocks.BLIGHTMOSS_HOLYSTONE_BRICK_WALL.get(),
               (Block)DABlocks.BLIGHTMOSS_HOLYSTONE_TILES.get(),
               (Block)DABlocks.BLIGHTMOSS_HOLYSTONE_TILE_SLAB.get(),
               (Block)DABlocks.BLIGHTMOSS_HOLYSTONE_TILE_STAIRS.get(),
               (Block)DABlocks.BLIGHTMOSS_HOLYSTONE_TILE_WALL.get(),
               (Block)DABlocks.NIMBUS_STONE.get(),
               (Block)DABlocks.LIGHT_NIMBUS_STONE.get(),
               (Block)DABlocks.NIMBUS_PILLAR.get(),
               (Block)DABlocks.LIGHT_NIMBUS_PILLAR.get(),
               (Block)DABlocks.NIMBUS_STAIRS.get(),
               (Block)DABlocks.NIMBUS_SLAB.get(),
               (Block)DABlocks.NIMBUS_WALL.get()
            }
         );
      this.tag(BlockTags.NEEDS_STONE_TOOL)
         .add(
            new Block[]{
               (Block)DABlocks.NIMBUS_STONE.get(),
               (Block)DABlocks.LIGHT_NIMBUS_STONE.get(),
               (Block)DABlocks.NIMBUS_PILLAR.get(),
               (Block)DABlocks.LIGHT_NIMBUS_PILLAR.get(),
               (Block)DABlocks.NIMBUS_STAIRS.get(),
               (Block)DABlocks.NIMBUS_SLAB.get(),
               (Block)DABlocks.NIMBUS_WALL.get()
            }
         );
      this.tag(BlockTags.NEEDS_IRON_TOOL)
         .add(new Block[]{(Block)DABlocks.SKYJADE_ORE.get(), (Block)DABlocks.SKYJADE_BLOCK.get(), (Block)DABlocks.SQUALL_BLOCK.get()});
      this.tag(BlockTags.NEEDS_DIAMOND_TOOL).add((Block)DABlocks.STRATUS_BLOCK.get());
      this.tag(BlockTags.SNAPS_GOAT_HORN)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_LOG.get(),
               (Block)DABlocks.ROSEROOT_WOOD.get(),
               (Block)DABlocks.STRIPPED_ROSEROOT_LOG.get(),
               (Block)DABlocks.STRIPPED_ROSEROOT_WOOD.get(),
               (Block)DABlocks.YAGROOT_LOG.get(),
               (Block)DABlocks.YAGROOT_WOOD.get(),
               (Block)DABlocks.STRIPPED_YAGROOT_LOG.get(),
               (Block)DABlocks.STRIPPED_YAGROOT_WOOD.get(),
               (Block)DABlocks.CRUDEROOT_LOG.get(),
               (Block)DABlocks.CRUDEROOT_WOOD.get(),
               (Block)DABlocks.STRIPPED_CRUDEROOT_LOG.get(),
               (Block)DABlocks.STRIPPED_CRUDEROOT_WOOD.get(),
               (Block)DABlocks.CONBERRY_LOG.get(),
               (Block)DABlocks.CONBERRY_WOOD.get(),
               (Block)DABlocks.STRIPPED_CONBERRY_LOG.get(),
               (Block)DABlocks.STRIPPED_CONBERRY_WOOD.get(),
               (Block)DABlocks.SUNROOT_LOG.get(),
               (Block)DABlocks.SUNROOT_WOOD.get(),
               (Block)DABlocks.STRIPPED_SUNROOT_LOG.get(),
               (Block)DABlocks.STRIPPED_SUNROOT_WOOD.get(),
               (Block)DABlocks.SKYJADE_ORE.get(),
               (Block)DABlocks.RAW_CLORITE.get(),
               (Block)DABlocks.ASETERITE.get()
            }
         );
      this.tag(BlockTags.SNAPS_GOAT_HORN)
         .addTags(
            new TagKey[]{
               DATags.Blocks.ROSEROOT_LOGS, DATags.Blocks.YAGROOT_LOGS, DATags.Blocks.CRUDEROOT_LOGS, DATags.Blocks.CONBERRY_LOGS, DATags.Blocks.SUNROOT_LOGS
            }
         );
      this.tag(BlockTags.CLIMBABLE)
         .add(
            new Block[]{
               (Block)DABlocks.YAGROOT_VINE.get(),
               (Block)DABlocks.GLOWING_VINE.get(),
               (Block)DABlocks.GOLDEN_VINES.get(),
               (Block)DABlocks.GOLDEN_VINES_PLANT.get(),
               (Block)DABlocks.SUNROOT_HANGER.get()
            }
         );
      this.tag(DATags.Blocks.HAS_GLOWING_SPORES).add(new Block[]{(Block)DABlocks.GLOWING_VINE.get(), (Block)DABlocks.TALL_GLOWING_GRASS.get()});
      this.tag(BlockTags.MAINTAINS_FARMLAND).add((Block)DABlocks.SQUASH_STEM.get());
      this.tag(BlockTags.BEE_GROWABLES).add((Block)DABlocks.SQUASH_STEM.get());
      this.tag(BlockTags.CROPS).add((Block)DABlocks.SQUASH_STEM.get());
      this.tag(net.neoforged.neoforge.common.Tags.Blocks.CHAINS).add((Block)DABlocks.SKYJADE_CHAIN.get());
      this.tag(Blocks.ALLOWED_BUCKET_PICKUP).add(new Block[]{(Block)DABlocks.POISON_BLOCK.get(), (Block)DABlocks.VIRULENT_QUICKSAND.get()});
      this.tag(DATags.Blocks.STERLING_AERCLOUD_REPLACEABLE).add((Block)DABlocks.RAIN_AERCLOUD.get());
      this.tag(DATags.Blocks.TOTEMS)
         .add(new Block[]{(Block)DABlocks.MOA_TOTEM.get(), (Block)DABlocks.ZEPHYR_TOTEM.get(), (Block)DABlocks.AERWHALE_TOTEM.get()});
      this.tag(BlockTags.DIRT)
         .add(
            new Block[]{
               (Block)DABlocks.AETHER_MUD.get(),
               (Block)DABlocks.AETHER_MOSS_BLOCK.get(),
               (Block)DABlocks.GOLDEN_GRASS_BLOCK.get(),
               (Block)DABlocks.AERCLOUD_GRASS_BLOCK.get(),
               (Block)DABlocks.AETHER_COARSE_DIRT.get()
            }
         );
      this.tag(BlockTags.BEACON_BASE_BLOCKS)
         .add(new Block[]{(Block)DABlocks.SKYJADE_BLOCK.get(), (Block)DABlocks.STRATUS_BLOCK.get(), (Block)DABlocks.SQUALL_BLOCK.get()});
      this.tag(net.neoforged.neoforge.common.Tags.Blocks.STORAGE_BLOCKS)
         .add(new Block[]{(Block)DABlocks.SKYJADE_BLOCK.get(), (Block)DABlocks.STRATUS_BLOCK.get(), (Block)DABlocks.SQUALL_BLOCK.get()});
      this.tag(net.neoforged.neoforge.common.Tags.Blocks.ORES).add((Block)DABlocks.SKYJADE_ORE.get());
      this.tag(net.neoforged.neoforge.common.Tags.Blocks.ORE_RATES_SINGULAR).add((Block)DABlocks.SKYJADE_ORE.get());
      this.tag(Blocks.AERCLOUDS)
         .add(
            new Block[]{
               (Block)DABlocks.CHROMATIC_AERCLOUD.get(),
               (Block)DABlocks.STERLING_AERCLOUD.get(),
               (Block)DABlocks.AERSMOG.get(),
               (Block)DABlocks.RAIN_AERCLOUD.get()
            }
         );
      this.tag(BlockTags.FALL_DAMAGE_RESETTING)
         .add(
            new Block[]{
               (Block)DABlocks.CHROMATIC_AERCLOUD.get(),
               (Block)DABlocks.STERLING_AERCLOUD.get(),
               (Block)DABlocks.AERSMOG.get(),
               (Block)DABlocks.RAIN_AERCLOUD.get()
            }
         );
      this.tag(com.aetherteam.beyondparity.BeyondParityTags.Blocks.LOG_WALLS)
         .add(
            new Block[]{
               (Block)DABlocks.ROSEROOT_WOOD_WALL.get(),
               (Block)DABlocks.STRIPPED_ROSEROOT_WOOD_WALL.get(),
               (Block)DABlocks.YAGROOT_WOOD_WALL.get(),
               (Block)DABlocks.STRIPPED_YAGROOT_WOOD_WALL.get(),
               (Block)DABlocks.CRUDEROOT_WOOD_WALL.get(),
               (Block)DABlocks.STRIPPED_CRUDEROOT_WOOD_WALL.get(),
               (Block)DABlocks.CONBERRY_WOOD_WALL.get(),
               (Block)DABlocks.STRIPPED_CONBERRY_WOOD_WALL.get(),
               (Block)DABlocks.SUNROOT_WOOD_WALL.get(),
               (Block)DABlocks.STRIPPED_SUNROOT_WOOD_WALL.get(),
               (Block)DABlocks.ROSEROOT_LOG_WALL.get(),
               (Block)DABlocks.STRIPPED_ROSEROOT_LOG_WALL.get(),
               (Block)DABlocks.YAGROOT_LOG_WALL.get(),
               (Block)DABlocks.STRIPPED_YAGROOT_LOG_WALL.get(),
               (Block)DABlocks.CRUDEROOT_LOG_WALL.get(),
               (Block)DABlocks.STRIPPED_CRUDEROOT_LOG_WALL.get(),
               (Block)DABlocks.CONBERRY_LOG_WALL.get(),
               (Block)DABlocks.STRIPPED_CONBERRY_LOG_WALL.get(),
               (Block)DABlocks.SUNROOT_LOG_WALL.get(),
               (Block)DABlocks.STRIPPED_SUNROOT_LOG_WALL.get()
            }
         );
   }
}
