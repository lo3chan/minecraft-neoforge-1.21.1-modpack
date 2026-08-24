package net.astralya.hexalia.neoforge.datagen;

import java.util.concurrent.CompletableFuture;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.util.ModTags;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public final class ModBlockTagProvider extends BlockTagsProvider {
   public ModBlockTagProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
      super(output, lookupProvider, "hexalia", existingFileHelper);
   }

   protected void addTags(Provider lookupProvider) {
      this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
         .add((Block)ModBlocks.SMALL_CAULDRON.get())
         .add((Block)ModBlocks.RITUAL_TABLE.get())
         .add((Block)ModBlocks.CANDLE_SKULL.get())
         .add((Block)ModBlocks.WITHER_CANDLE_SKULL.get())
         .add((Block)ModBlocks.SALT_LAMP.get())
         .add((Block)ModBlocks.SALT_BLOCK.get())
         .add((Block)ModBlocks.CELESTIAL_CRYSTAL_BLOCK.get())
         .add((Block)ModBlocks.RUSTIC_OVEN.get());
      this.tag(BlockTags.MINEABLE_WITH_AXE)
         .add((Block)ModBlocks.CENSER.get())
         .add((Block)ModBlocks.SHELF.get())
         .add((Block)ModBlocks.DREAMCATCHER.get())
         .add((Block)ModBlocks.RITUAL_BRAZIER.get())
         .add((Block)ModBlocks.MORTAR_AND_PESTLE.get())
         .add((Block)ModBlocks.LOTUS_FLOWER.get())
         .add((Block)ModBlocks.NESTING_BLOCK.get())
         .add(
            new Block[]{
               (Block)ModBlocks.COTTONWOOD_LOG.get(),
               (Block)ModBlocks.STRIPPED_COTTONWOOD_LOG.get(),
               (Block)ModBlocks.COTTONWOOD_WOOD.get(),
               (Block)ModBlocks.STRIPPED_COTTONWOOD_WOOD.get(),
               (Block)ModBlocks.COTTONWOOD_PLANKS.get(),
               (Block)ModBlocks.COTTONWOOD_STAIRS.get(),
               (Block)ModBlocks.COTTONWOOD_SLAB.get(),
               (Block)ModBlocks.COTTONWOOD_BUTTON.get(),
               (Block)ModBlocks.COTTONWOOD_PRESSURE_PLATE.get(),
               (Block)ModBlocks.COTTONWOOD_FENCE.get(),
               (Block)ModBlocks.COTTONWOOD_FENCE_GATE.get(),
               (Block)ModBlocks.COTTONWOOD_TRAPDOOR.get(),
               (Block)ModBlocks.COTTONWOOD_DOOR.get(),
               (Block)ModBlocks.COTTONWOOD_SIGN.get(),
               (Block)ModBlocks.COTTONWOOD_WALL_SIGN.get(),
               (Block)ModBlocks.COTTONWOOD_HANGING_SIGN.get(),
               (Block)ModBlocks.COTTONWOOD_HANGING_WALL_SIGN.get(),
               (Block)ModBlocks.WILLOW_LOG.get(),
               (Block)ModBlocks.STRIPPED_WILLOW_LOG.get(),
               (Block)ModBlocks.WILLOW_WOOD.get(),
               (Block)ModBlocks.STRIPPED_WILLOW_WOOD.get(),
               (Block)ModBlocks.WILLOW_PLANKS.get(),
               (Block)ModBlocks.WILLOW_STAIRS.get(),
               (Block)ModBlocks.WILLOW_SLAB.get(),
               (Block)ModBlocks.WILLOW_BUTTON.get(),
               (Block)ModBlocks.WILLOW_PRESSURE_PLATE.get(),
               (Block)ModBlocks.WILLOW_FENCE.get(),
               (Block)ModBlocks.WILLOW_FENCE_GATE.get(),
               (Block)ModBlocks.WILLOW_TRAPDOOR.get(),
               (Block)ModBlocks.WILLOW_DOOR.get(),
               (Block)ModBlocks.WILLOW_SIGN.get(),
               (Block)ModBlocks.WILLOW_WALL_SIGN.get(),
               (Block)ModBlocks.WILLOW_HANGING_SIGN.get(),
               (Block)ModBlocks.WILLOW_HANGING_WALL_SIGN.get()
            }
         );
      this.tag(BlockTags.NEEDS_STONE_TOOL)
         .add((Block)ModBlocks.SMALL_CAULDRON.get())
         .add((Block)ModBlocks.CENSER.get())
         .add((Block)ModBlocks.SHELF.get())
         .add((Block)ModBlocks.LOTUS_FLOWER.get())
         .add((Block)ModBlocks.RITUAL_TABLE.get())
         .add((Block)ModBlocks.RITUAL_BRAZIER.get())
         .add((Block)ModBlocks.MORTAR_AND_PESTLE.get())
         .add((Block)ModBlocks.NESTING_BLOCK.get())
         .add((Block)ModBlocks.SALT_LAMP.get())
         .add((Block)ModBlocks.SALT_BLOCK.get())
         .add((Block)ModBlocks.CELESTIAL_CRYSTAL_BLOCK.get())
         .add((Block)ModBlocks.RUSTIC_OVEN.get());
      this.tag(BlockTags.FLOWERS)
         .add((Block)ModBlocks.SPIRIT_BLOOM.get())
         .add((Block)ModBlocks.WITCHWEED.get())
         .add((Block)ModBlocks.GHOST_FERN.get())
         .add((Block)ModBlocks.CELESTIAL_BLOOM.get())
         .add((Block)ModBlocks.WITHERED_CELESTIAL_BLOOM.get())
         .add((Block)ModBlocks.MORPHORA.get())
         .add((Block)ModBlocks.GRIMSHADE.get())
         .add((Block)ModBlocks.NAUTILITE.get())
         .add((Block)ModBlocks.WINDSONG.get())
         .add((Block)ModBlocks.ASTRYLIS.get())
         .add((Block)ModBlocks.LOURDES.get())
         .add((Block)ModBlocks.AEGIFLORA.get())
         .add((Block)ModBlocks.WITHERED_AEGIFLORA.get())
         .add((Block)ModBlocks.NIGHTSHADE_BUSH.get())
         .add((Block)ModBlocks.BEGONIA.get())
         .add((Block)ModBlocks.LAVENDER.get())
         .add((Block)ModBlocks.DAHLIA.get());
      this.tag(BlockTags.SMALL_FLOWERS)
         .add((Block)ModBlocks.SPIRIT_BLOOM.get())
         .add((Block)ModBlocks.WITCHWEED.get())
         .add((Block)ModBlocks.GHOST_FERN.get())
         .add((Block)ModBlocks.CELESTIAL_BLOOM.get())
         .add((Block)ModBlocks.WITHERED_CELESTIAL_BLOOM.get())
         .add((Block)ModBlocks.MORPHORA.get())
         .add((Block)ModBlocks.GRIMSHADE.get())
         .add((Block)ModBlocks.NAUTILITE.get())
         .add((Block)ModBlocks.WINDSONG.get())
         .add((Block)ModBlocks.ASTRYLIS.get())
         .add((Block)ModBlocks.LOURDES.get())
         .add((Block)ModBlocks.AEGIFLORA.get())
         .add((Block)ModBlocks.WITHERED_AEGIFLORA.get())
         .add((Block)ModBlocks.NIGHTSHADE_BUSH.get())
         .add((Block)ModBlocks.BEGONIA.get())
         .add((Block)ModBlocks.LAVENDER.get())
         .add((Block)ModBlocks.DAHLIA.get());
      this.tag(BlockTags.FROG_PREFER_JUMP_TO).add((Block)ModBlocks.LOTUS_FLOWER.get());
      this.tag(BlockTags.INSIDE_STEP_SOUND_BLOCKS).add((Block)ModBlocks.LOTUS_FLOWER.get());
      this.tag(BlockTags.SWORD_EFFICIENT).add((Block)ModBlocks.LOTUS_FLOWER.get());
      this.tag(BlockTags.CROPS)
         .add((Block)ModBlocks.SUNFIRE_TOMATO_CROP.get())
         .add((Block)ModBlocks.MANDRAKE_CROP.get())
         .add((Block)ModBlocks.RABBAGE_CROP.get())
         .add((Block)ModBlocks.SALTSPROUT.get())
         .add((Block)ModBlocks.CHILLBERRY_BUSH.get());
      this.tag(BlockTags.FLOWER_POTS)
         .add((Block)ModBlocks.POTTED_SPIRIT_BLOOM.get())
         .add((Block)ModBlocks.POTTED_DREAMSHROOM.get())
         .add((Block)ModBlocks.POTTED_GHOST_FERN.get())
         .add((Block)ModBlocks.POTTED_CELESTIAL_BLOOM.get())
         .add((Block)ModBlocks.POTTED_WITHERED_CELESTIAL_BLOOM.get())
         .add((Block)ModBlocks.POTTED_MORPHORA.get())
         .add((Block)ModBlocks.POTTED_GRIMSHADE.get())
         .add((Block)ModBlocks.POTTED_WINDSONG.get())
         .add((Block)ModBlocks.POTTED_ASTRYLIS.get())
         .add((Block)ModBlocks.POTTED_LOURDES.get())
         .add((Block)ModBlocks.POTTED_AEGIFLORA.get())
         .add((Block)ModBlocks.POTTED_WITHERED_AEGIFLORA.get())
         .add((Block)ModBlocks.POTTED_NIGHTSHADE_BUSH.get())
         .add((Block)ModBlocks.POTTED_BEGONIA.get())
         .add((Block)ModBlocks.POTTED_LAVENDER.get())
         .add((Block)ModBlocks.POTTED_DAHLIA.get())
         .add((Block)ModBlocks.POTTED_COTTONWOOD_SAPLING.get())
         .add((Block)ModBlocks.POTTED_WILLOW_SAPLING.get());
      this.tag(ModTags.Blocks.CROPS)
         .add((Block)ModBlocks.SUNFIRE_TOMATO_CROP.get())
         .add((Block)ModBlocks.MANDRAKE_CROP.get())
         .add((Block)ModBlocks.RABBAGE_CROP.get())
         .add((Block)ModBlocks.SALTSPROUT.get())
         .add((Block)ModBlocks.CHILLBERRY_BUSH.get());
      this.tag(ModTags.Blocks.SALT_BLOCKS).add((Block)ModBlocks.SALT_BLOCK.get());
      this.tag(ModTags.Blocks.BOGSHADE_NO_SLOW).add(new Block[]{Blocks.MUD, Blocks.SOUL_SAND, Blocks.HONEY_BLOCK});
      this.tag(ModTags.Blocks.RESIN_LOGS)
         .add(
            new Block[]{
               Blocks.DARK_OAK_LOG,
               Blocks.STRIPPED_DARK_OAK_LOG,
               (Block)ModBlocks.COTTONWOOD_LOG.get(),
               (Block)ModBlocks.STRIPPED_COTTONWOOD_LOG.get(),
               (Block)ModBlocks.COTTONWOOD_WOOD.get(),
               (Block)ModBlocks.STRIPPED_COTTONWOOD_WOOD.get(),
               (Block)ModBlocks.WILLOW_LOG.get(),
               (Block)ModBlocks.STRIPPED_WILLOW_LOG.get(),
               (Block)ModBlocks.WILLOW_WOOD.get(),
               (Block)ModBlocks.STRIPPED_WILLOW_WOOD.get()
            }
         );
      this.tag(ModTags.Blocks.COTTONWOOD_LOGS)
         .add(
            new Block[]{
               (Block)ModBlocks.COTTONWOOD_LOG.get(),
               (Block)ModBlocks.STRIPPED_COTTONWOOD_LOG.get(),
               (Block)ModBlocks.COTTONWOOD_WOOD.get(),
               (Block)ModBlocks.STRIPPED_COTTONWOOD_WOOD.get()
            }
         );
      this.tag(ModTags.Blocks.WILLOW_LOGS)
         .add(
            new Block[]{
               (Block)ModBlocks.WILLOW_LOG.get(),
               (Block)ModBlocks.STRIPPED_WILLOW_LOG.get(),
               (Block)ModBlocks.WILLOW_WOOD.get(),
               (Block)ModBlocks.STRIPPED_WILLOW_WOOD.get()
            }
         );
      this.tag(BlockTags.LOGS_THAT_BURN).addTag(ModTags.Blocks.COTTONWOOD_LOGS).addTag(ModTags.Blocks.WILLOW_LOGS);
      this.tag(BlockTags.PLANKS).add(new Block[]{(Block)ModBlocks.COTTONWOOD_PLANKS.get(), (Block)ModBlocks.WILLOW_PLANKS.get()});
      this.tag(BlockTags.WOODEN_STAIRS).add(new Block[]{(Block)ModBlocks.COTTONWOOD_STAIRS.get(), (Block)ModBlocks.WILLOW_STAIRS.get()});
      this.tag(BlockTags.WOODEN_SLABS).add(new Block[]{(Block)ModBlocks.COTTONWOOD_SLAB.get(), (Block)ModBlocks.WILLOW_SLAB.get()});
      this.tag(BlockTags.WOODEN_BUTTONS).add(new Block[]{(Block)ModBlocks.COTTONWOOD_BUTTON.get(), (Block)ModBlocks.WILLOW_BUTTON.get()});
      this.tag(BlockTags.WOODEN_PRESSURE_PLATES)
         .add(new Block[]{(Block)ModBlocks.COTTONWOOD_PRESSURE_PLATE.get(), (Block)ModBlocks.WILLOW_PRESSURE_PLATE.get()});
      this.tag(BlockTags.WOODEN_FENCES).add(new Block[]{(Block)ModBlocks.COTTONWOOD_FENCE.get(), (Block)ModBlocks.WILLOW_FENCE.get()});
      this.tag(BlockTags.FENCE_GATES).add(new Block[]{(Block)ModBlocks.COTTONWOOD_FENCE_GATE.get(), (Block)ModBlocks.WILLOW_FENCE_GATE.get()});
      this.tag(BlockTags.WOODEN_DOORS).add(new Block[]{(Block)ModBlocks.COTTONWOOD_DOOR.get(), (Block)ModBlocks.WILLOW_DOOR.get()});
      this.tag(BlockTags.WOODEN_TRAPDOORS).add(new Block[]{(Block)ModBlocks.COTTONWOOD_TRAPDOOR.get(), (Block)ModBlocks.WILLOW_TRAPDOOR.get()});
      this.tag(BlockTags.SAPLINGS).add(new Block[]{(Block)ModBlocks.COTTONWOOD_SAPLING.get(), (Block)ModBlocks.WILLOW_SAPLING.get()});
      this.tag(BlockTags.LEAVES).add(new Block[]{(Block)ModBlocks.COTTONWOOD_LEAVES.get(), (Block)ModBlocks.WILLOW_LEAVES.get()});
      this.tag(BlockTags.STANDING_SIGNS).add(new Block[]{(Block)ModBlocks.COTTONWOOD_SIGN.get(), (Block)ModBlocks.WILLOW_SIGN.get()});
      this.tag(BlockTags.WALL_SIGNS).add(new Block[]{(Block)ModBlocks.COTTONWOOD_WALL_SIGN.get(), (Block)ModBlocks.WILLOW_WALL_SIGN.get()});
      this.tag(BlockTags.CEILING_HANGING_SIGNS).add(new Block[]{(Block)ModBlocks.COTTONWOOD_HANGING_SIGN.get(), (Block)ModBlocks.WILLOW_HANGING_SIGN.get()});
      this.tag(BlockTags.WALL_HANGING_SIGNS)
         .add(new Block[]{(Block)ModBlocks.COTTONWOOD_HANGING_WALL_SIGN.get(), (Block)ModBlocks.WILLOW_HANGING_WALL_SIGN.get()});
      this.tag(BlockTags.ALL_SIGNS)
         .add(
            new Block[]{
               (Block)ModBlocks.COTTONWOOD_SIGN.get(),
               (Block)ModBlocks.COTTONWOOD_WALL_SIGN.get(),
               (Block)ModBlocks.WILLOW_SIGN.get(),
               (Block)ModBlocks.WILLOW_WALL_SIGN.get()
            }
         );
      this.tag(BlockTags.ALL_HANGING_SIGNS)
         .add(
            new Block[]{
               (Block)ModBlocks.COTTONWOOD_HANGING_SIGN.get(),
               (Block)ModBlocks.COTTONWOOD_HANGING_WALL_SIGN.get(),
               (Block)ModBlocks.WILLOW_HANGING_SIGN.get(),
               (Block)ModBlocks.WILLOW_HANGING_WALL_SIGN.get()
            }
         );
      this.tag(ModTags.Blocks.SPIRITROOT_BOUND_BLOCKS).add((Block)ModBlocks.SPIRIT_BLOOM.get());
      this.tag(ModTags.Blocks.ATTRACTS_MOTH)
         .add(Blocks.LANTERN)
         .add(Blocks.SEA_LANTERN)
         .add(Blocks.SOUL_LANTERN)
         .add((Block)ModBlocks.SALT_LAMP.get())
         .add((Block)ModBlocks.GHOST_FERN.get())
         .add(Blocks.END_ROD)
         .add(Blocks.TORCH);
      this.tag(BlockTags.DIRT).add((Block)ModBlocks.INFUSED_DIRT.get());
      this.tag(BlockTags.MUSHROOM_GROW_BLOCK).add((Block)ModBlocks.INFUSED_DIRT.get());
      this.tag(BlockTags.CLIMBABLE).add((Block)ModBlocks.GALEBERRIES_VINE.get()).add((Block)ModBlocks.GALEBERRIES_VINE_PLANT.get());
      this.tag(BlockTags.CAVE_VINES).add((Block)ModBlocks.GALEBERRIES_VINE.get()).add((Block)ModBlocks.GALEBERRIES_VINE_PLANT.get());
      this.tag(ModTags.Compat.SERENE_SEASONS_SPRING_CROPS_BLOCK).add((Block)ModBlocks.RABBAGE_CROP.get()).add((Block)ModBlocks.MANDRAKE_CROP.get());
      this.tag(ModTags.Compat.SERENE_SEASONS_SUMMER_CROPS_BLOCK)
         .add((Block)ModBlocks.RABBAGE_CROP.get())
         .add((Block)ModBlocks.MANDRAKE_CROP.get())
         .add((Block)ModBlocks.SUNFIRE_TOMATO_CROP.get());
      this.tag(ModTags.Compat.SERENE_SEASONS_AUTUMN_CROPS_BLOCK).add((Block)ModBlocks.RABBAGE_CROP.get()).add((Block)ModBlocks.SUNFIRE_TOMATO_CROP.get());
      this.tag(ModTags.Compat.SERENE_SEASONS_WINTER_CROPS_BLOCK).add((Block)ModBlocks.RABBAGE_CROP.get());
      this.tag(ModTags.Compat.SERENE_SEASONS_UNBREAKABLE_FERTILE_CROPS).add((Block)ModBlocks.MANDRAKE_CROP.get());
   }
}
