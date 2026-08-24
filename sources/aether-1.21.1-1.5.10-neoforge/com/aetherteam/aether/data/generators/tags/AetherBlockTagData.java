package com.aetherteam.aether.data.generators.tags;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.block.AetherBlocks;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class AetherBlockTagData extends BlockTagsProvider {
   public AetherBlockTagData(PackOutput output, CompletableFuture<Provider> registries, @Nullable ExistingFileHelper helper) {
      super(output, registries, "aether", helper);
   }

   public void addTags(Provider provider) {
      this.tag(AetherTags.Blocks.TREATED_AS_VANILLA_BLOCK)
         .add(new Block[]{(Block)AetherBlocks.CHEST_MIMIC.get(), (Block)AetherBlocks.FROSTED_ICE.get(), (Block)AetherBlocks.UNSTABLE_OBSIDIAN.get()});
      this.tag(AetherTags.Blocks.AETHER_PORTAL_BLOCKS).add(Blocks.GLOWSTONE);
      this.tag(AetherTags.Blocks.AETHER_PORTAL_BLACKLIST)
         .add((Block)AetherBlocks.BLUE_AERCLOUD.get())
         .addTags(
            new TagKey[]{
               AetherTags.Blocks.LOCKED_DUNGEON_BLOCKS, AetherTags.Blocks.BOSS_DOORWAY_DUNGEON_BLOCKS, AetherTags.Blocks.TREASURE_DOORWAY_DUNGEON_BLOCKS
            }
         );
      this.tag(AetherTags.Blocks.AETHER_ISLAND_BLOCKS)
         .add(new Block[]{(Block)AetherBlocks.AETHER_DIRT.get(), (Block)AetherBlocks.AETHER_GRASS_BLOCK.get(), (Block)AetherBlocks.HOLYSTONE.get()});
      this.tag(AetherTags.Blocks.AETHER_DIRT)
         .add(
            new Block[]{
               (Block)AetherBlocks.AETHER_GRASS_BLOCK.get(), (Block)AetherBlocks.ENCHANTED_AETHER_GRASS_BLOCK.get(), (Block)AetherBlocks.AETHER_DIRT.get()
            }
         );
      this.tag(AetherTags.Blocks.ENCHANTED_GRASS).add((Block)AetherBlocks.ENCHANTED_AETHER_GRASS_BLOCK.get());
      this.tag(AetherTags.Blocks.HOLYSTONE).add(new Block[]{(Block)AetherBlocks.HOLYSTONE.get(), (Block)AetherBlocks.MOSSY_HOLYSTONE.get()});
      this.tag(AetherTags.Blocks.AERCLOUDS)
         .add(new Block[]{(Block)AetherBlocks.COLD_AERCLOUD.get(), (Block)AetherBlocks.BLUE_AERCLOUD.get(), (Block)AetherBlocks.GOLDEN_AERCLOUD.get()});
      this.tag(AetherTags.Blocks.SKYROOT_LOGS)
         .add(
            new Block[]{
               (Block)AetherBlocks.SKYROOT_LOG.get(),
               (Block)AetherBlocks.SKYROOT_WOOD.get(),
               (Block)AetherBlocks.STRIPPED_SKYROOT_LOG.get(),
               (Block)AetherBlocks.STRIPPED_SKYROOT_WOOD.get()
            }
         );
      this.tag(AetherTags.Blocks.GOLDEN_OAK_LOGS).add(new Block[]{(Block)AetherBlocks.GOLDEN_OAK_LOG.get(), (Block)AetherBlocks.GOLDEN_OAK_WOOD.get()});
      this.tag(AetherTags.Blocks.ALLOWED_BUCKET_PICKUP).add(Blocks.POWDER_SNOW);
      this.tag(AetherTags.Blocks.AEROGEL)
         .add(
            new Block[]{
               (Block)AetherBlocks.AEROGEL.get(),
               (Block)AetherBlocks.AEROGEL_WALL.get(),
               (Block)AetherBlocks.AEROGEL_STAIRS.get(),
               (Block)AetherBlocks.AEROGEL_SLAB.get()
            }
         );
      this.tag(AetherTags.Blocks.DUNGEON_BLOCKS)
         .add(
            new Block[]{
               (Block)AetherBlocks.CARVED_STONE.get(),
               (Block)AetherBlocks.SENTRY_STONE.get(),
               (Block)AetherBlocks.ANGELIC_STONE.get(),
               (Block)AetherBlocks.LIGHT_ANGELIC_STONE.get(),
               (Block)AetherBlocks.HELLFIRE_STONE.get(),
               (Block)AetherBlocks.LIGHT_HELLFIRE_STONE.get()
            }
         );
      this.tag(AetherTags.Blocks.LOCKED_DUNGEON_BLOCKS)
         .add(
            new Block[]{
               (Block)AetherBlocks.LOCKED_CARVED_STONE.get(),
               (Block)AetherBlocks.LOCKED_SENTRY_STONE.get(),
               (Block)AetherBlocks.LOCKED_ANGELIC_STONE.get(),
               (Block)AetherBlocks.LOCKED_LIGHT_ANGELIC_STONE.get(),
               (Block)AetherBlocks.LOCKED_HELLFIRE_STONE.get(),
               (Block)AetherBlocks.LOCKED_LIGHT_HELLFIRE_STONE.get()
            }
         );
      this.tag(AetherTags.Blocks.TRAPPED_DUNGEON_BLOCKS)
         .add(
            new Block[]{
               (Block)AetherBlocks.TRAPPED_CARVED_STONE.get(),
               (Block)AetherBlocks.TRAPPED_SENTRY_STONE.get(),
               (Block)AetherBlocks.TRAPPED_ANGELIC_STONE.get(),
               (Block)AetherBlocks.TRAPPED_LIGHT_ANGELIC_STONE.get(),
               (Block)AetherBlocks.TRAPPED_HELLFIRE_STONE.get(),
               (Block)AetherBlocks.TRAPPED_LIGHT_HELLFIRE_STONE.get()
            }
         );
      this.tag(AetherTags.Blocks.BOSS_DOORWAY_DUNGEON_BLOCKS)
         .add(
            new Block[]{
               (Block)AetherBlocks.BOSS_DOORWAY_CARVED_STONE.get(),
               (Block)AetherBlocks.BOSS_DOORWAY_SENTRY_STONE.get(),
               (Block)AetherBlocks.BOSS_DOORWAY_ANGELIC_STONE.get(),
               (Block)AetherBlocks.BOSS_DOORWAY_LIGHT_ANGELIC_STONE.get(),
               (Block)AetherBlocks.BOSS_DOORWAY_HELLFIRE_STONE.get(),
               (Block)AetherBlocks.BOSS_DOORWAY_LIGHT_HELLFIRE_STONE.get()
            }
         );
      this.tag(AetherTags.Blocks.TREASURE_DOORWAY_DUNGEON_BLOCKS)
         .add(
            new Block[]{
               (Block)AetherBlocks.TREASURE_DOORWAY_CARVED_STONE.get(),
               (Block)AetherBlocks.TREASURE_DOORWAY_SENTRY_STONE.get(),
               (Block)AetherBlocks.TREASURE_DOORWAY_ANGELIC_STONE.get(),
               (Block)AetherBlocks.TREASURE_DOORWAY_LIGHT_ANGELIC_STONE.get(),
               (Block)AetherBlocks.TREASURE_DOORWAY_HELLFIRE_STONE.get(),
               (Block)AetherBlocks.TREASURE_DOORWAY_LIGHT_HELLFIRE_STONE.get()
            }
         );
      this.tag(AetherTags.Blocks.SENTRY_BLOCKS)
         .add(
            new Block[]{
               (Block)AetherBlocks.CARVED_STONE.get(),
               (Block)AetherBlocks.SENTRY_STONE.get(),
               (Block)AetherBlocks.LOCKED_CARVED_STONE.get(),
               (Block)AetherBlocks.LOCKED_SENTRY_STONE.get(),
               (Block)AetherBlocks.TRAPPED_CARVED_STONE.get(),
               (Block)AetherBlocks.TRAPPED_SENTRY_STONE.get(),
               (Block)AetherBlocks.BOSS_DOORWAY_CARVED_STONE.get(),
               (Block)AetherBlocks.BOSS_DOORWAY_SENTRY_STONE.get(),
               (Block)AetherBlocks.TREASURE_DOORWAY_CARVED_STONE.get(),
               (Block)AetherBlocks.TREASURE_DOORWAY_SENTRY_STONE.get(),
               (Block)AetherBlocks.CARVED_STAIRS.get(),
               (Block)AetherBlocks.CARVED_SLAB.get(),
               (Block)AetherBlocks.CARVED_WALL.get()
            }
         );
      this.tag(AetherTags.Blocks.ANGELIC_BLOCKS)
         .add(
            new Block[]{
               (Block)AetherBlocks.ANGELIC_STONE.get(),
               (Block)AetherBlocks.LIGHT_ANGELIC_STONE.get(),
               (Block)AetherBlocks.LOCKED_ANGELIC_STONE.get(),
               (Block)AetherBlocks.LOCKED_LIGHT_ANGELIC_STONE.get(),
               (Block)AetherBlocks.TRAPPED_ANGELIC_STONE.get(),
               (Block)AetherBlocks.TRAPPED_LIGHT_ANGELIC_STONE.get(),
               (Block)AetherBlocks.BOSS_DOORWAY_ANGELIC_STONE.get(),
               (Block)AetherBlocks.BOSS_DOORWAY_LIGHT_ANGELIC_STONE.get(),
               (Block)AetherBlocks.TREASURE_DOORWAY_ANGELIC_STONE.get(),
               (Block)AetherBlocks.TREASURE_DOORWAY_LIGHT_ANGELIC_STONE.get(),
               (Block)AetherBlocks.ANGELIC_STAIRS.get(),
               (Block)AetherBlocks.ANGELIC_SLAB.get(),
               (Block)AetherBlocks.ANGELIC_WALL.get()
            }
         );
      this.tag(AetherTags.Blocks.HELLFIRE_BLOCKS)
         .add(
            new Block[]{
               (Block)AetherBlocks.HELLFIRE_STONE.get(),
               (Block)AetherBlocks.LIGHT_HELLFIRE_STONE.get(),
               (Block)AetherBlocks.LOCKED_HELLFIRE_STONE.get(),
               (Block)AetherBlocks.LOCKED_LIGHT_HELLFIRE_STONE.get(),
               (Block)AetherBlocks.TRAPPED_HELLFIRE_STONE.get(),
               (Block)AetherBlocks.TRAPPED_LIGHT_HELLFIRE_STONE.get(),
               (Block)AetherBlocks.BOSS_DOORWAY_HELLFIRE_STONE.get(),
               (Block)AetherBlocks.BOSS_DOORWAY_LIGHT_HELLFIRE_STONE.get(),
               (Block)AetherBlocks.TREASURE_DOORWAY_HELLFIRE_STONE.get(),
               (Block)AetherBlocks.TREASURE_DOORWAY_LIGHT_HELLFIRE_STONE.get(),
               (Block)AetherBlocks.HELLFIRE_STAIRS.get(),
               (Block)AetherBlocks.HELLFIRE_SLAB.get(),
               (Block)AetherBlocks.HELLFIRE_WALL.get()
            }
         );
      this.tag(AetherTags.Blocks.SLIDER_UNBREAKABLE)
         .add(
            new Block[]{
               Blocks.BARRIER,
               Blocks.BEDROCK,
               Blocks.END_PORTAL,
               Blocks.END_PORTAL_FRAME,
               Blocks.END_GATEWAY,
               Blocks.COMMAND_BLOCK,
               Blocks.REPEATING_COMMAND_BLOCK,
               Blocks.CHAIN_COMMAND_BLOCK,
               Blocks.STRUCTURE_BLOCK,
               Blocks.JIGSAW,
               Blocks.MOVING_PISTON,
               Blocks.LIGHT,
               Blocks.REINFORCED_DEEPSLATE
            }
         )
         .addTags(
            new TagKey[]{
               AetherTags.Blocks.LOCKED_DUNGEON_BLOCKS,
               AetherTags.Blocks.TRAPPED_DUNGEON_BLOCKS,
               AetherTags.Blocks.BOSS_DOORWAY_DUNGEON_BLOCKS,
               AetherTags.Blocks.TREASURE_DOORWAY_DUNGEON_BLOCKS
            }
         );
      this.tag(AetherTags.Blocks.VALKYRIE_QUEEN_UNBREAKABLE)
         .add(
            new Block[]{
               Blocks.WATER,
               Blocks.BEDROCK,
               Blocks.END_PORTAL,
               Blocks.END_PORTAL_FRAME,
               Blocks.END_GATEWAY,
               Blocks.COMMAND_BLOCK,
               Blocks.REPEATING_COMMAND_BLOCK,
               Blocks.CHAIN_COMMAND_BLOCK,
               Blocks.STRUCTURE_BLOCK,
               Blocks.JIGSAW,
               Blocks.MOVING_PISTON,
               Blocks.LIGHT,
               Blocks.REINFORCED_DEEPSLATE
            }
         )
         .addTags(
            new TagKey[]{
               AetherTags.Blocks.LOCKED_DUNGEON_BLOCKS,
               AetherTags.Blocks.TRAPPED_DUNGEON_BLOCKS,
               AetherTags.Blocks.BOSS_DOORWAY_DUNGEON_BLOCKS,
               AetherTags.Blocks.TREASURE_DOORWAY_DUNGEON_BLOCKS
            }
         );
      this.tag(AetherTags.Blocks.SUN_SPIRIT_UNBREAKABLE)
         .add(
            new Block[]{
               Blocks.WATER,
               Blocks.BEDROCK,
               Blocks.END_PORTAL,
               Blocks.END_PORTAL_FRAME,
               Blocks.END_GATEWAY,
               Blocks.COMMAND_BLOCK,
               Blocks.REPEATING_COMMAND_BLOCK,
               Blocks.CHAIN_COMMAND_BLOCK,
               Blocks.STRUCTURE_BLOCK,
               Blocks.JIGSAW,
               Blocks.MOVING_PISTON,
               Blocks.LIGHT,
               Blocks.REINFORCED_DEEPSLATE
            }
         )
         .addTags(
            new TagKey[]{
               AetherTags.Blocks.LOCKED_DUNGEON_BLOCKS,
               AetherTags.Blocks.TRAPPED_DUNGEON_BLOCKS,
               AetherTags.Blocks.BOSS_DOORWAY_DUNGEON_BLOCKS,
               AetherTags.Blocks.TREASURE_DOORWAY_DUNGEON_BLOCKS
            }
         );
      this.tag(AetherTags.Blocks.NON_RUINED_PORTAL_SPAWNABLE);
      this.tag(AetherTags.Blocks.RUINED_PORTAL_GROUND_REPLACEABLE)
         .addTag(BlockTags.BASE_STONE_OVERWORLD)
         .addTag(BlockTags.DIRT)
         .addTag(BlockTags.SAND)
         .add(Blocks.GRAVEL)
         .add(Blocks.CALCITE)
         .add(Blocks.SMOOTH_BASALT)
         .add(Blocks.CLAY)
         .add(Blocks.DRIPSTONE_BLOCK)
         .add(Blocks.RED_SANDSTONE)
         .add(Blocks.SANDSTONE);
      this.tag(AetherTags.Blocks.NON_BRONZE_DUNGEON_REPLACEABLE)
         .add(new Block[]{Blocks.AIR, Blocks.WATER, Blocks.CHEST, (Block)AetherBlocks.CHEST_MIMIC.get(), (Block)AetherBlocks.TREASURE_CHEST.get()})
         .addTags(
            new TagKey[]{
               AetherTags.Blocks.LOCKED_DUNGEON_BLOCKS, AetherTags.Blocks.BOSS_DOORWAY_DUNGEON_BLOCKS, AetherTags.Blocks.TREASURE_DOORWAY_DUNGEON_BLOCKS
            }
         );
      this.tag(AetherTags.Blocks.NON_BRONZE_DUNGEON_SPAWNABLE).add(Blocks.WATER);
      this.tag(AetherTags.Blocks.GRAVITITE_ABILITY_BLACKLIST)
         .addTags(new TagKey[]{BlockTags.BUTTONS, BlockTags.PRESSURE_PLATES, BlockTags.TRAPDOORS, BlockTags.FENCE_GATES});
      this.tag(AetherTags.Blocks.AETHER_ANIMALS_SPAWNABLE_ON).add((Block)AetherBlocks.AETHER_GRASS_BLOCK.get());
      this.tag(AetherTags.Blocks.SWET_SPAWNABLE_ON).add((Block)AetherBlocks.AETHER_GRASS_BLOCK.get());
      this.tag(AetherTags.Blocks.AECHOR_PLANT_SPAWNABLE_ON).add((Block)AetherBlocks.AETHER_GRASS_BLOCK.get());
      this.tag(AetherTags.Blocks.AECHOR_PLANT_SPAWNABLE_DETERRENT)
         .add(new Block[]{(Block)AetherBlocks.WHITE_FLOWER.get(), (Block)AetherBlocks.PURPLE_FLOWER.get()});
      this.tag(AetherTags.Blocks.INFINIBURN).addTag(BlockTags.INFINIBURN_OVERWORLD);
      this.tag(AetherTags.Blocks.ALLOWED_FLAMMABLES)
         .add(new Block[]{Blocks.SOUL_SAND, Blocks.SOUL_SOIL})
         .addTags(new TagKey[]{AetherTags.Blocks.INFINIBURN, AetherTags.Blocks.HELLFIRE_BLOCKS});
      this.tag(AetherTags.Blocks.VALKYRIE_TELEPORTABLE_ON)
         .add(new Block[]{(Block)AetherBlocks.LOCKED_ANGELIC_STONE.get(), (Block)AetherBlocks.LOCKED_LIGHT_ANGELIC_STONE.get()});
      this.tag(AetherTags.Blocks.TREATED_AS_AETHER_BLOCK);
      this.tag(AetherTags.Blocks.DOUBLE_DROPS_OVERRIDE);
      this.tag(AetherTags.Blocks.HOLYSTONE_ORE_REPLACEABLES).add((Block)AetherBlocks.HOLYSTONE.get());
      this.tag(AetherTags.Blocks.ORE_BEARING_GROUND_HOLYSTONE).add((Block)AetherBlocks.HOLYSTONE.get());
      this.tag(AetherTags.Blocks.ORES_AMBROSIUM).add((Block)AetherBlocks.AMBROSIUM_ORE.get());
      this.tag(AetherTags.Blocks.ORES_ZANITE).add((Block)AetherBlocks.ZANITE_ORE.get());
      this.tag(AetherTags.Blocks.ORES_GRAVITITE).add((Block)AetherBlocks.GRAVITITE_ORE.get());
      this.tag(AetherTags.Blocks.ORES_IN_GROUND_HOLYSTONE)
         .add(new Block[]{(Block)AetherBlocks.AMBROSIUM_ORE.get(), (Block)AetherBlocks.ZANITE_ORE.get(), (Block)AetherBlocks.GRAVITITE_ORE.get()});
      this.tag(AetherTags.Blocks.STORAGE_BLOCKS_AMBROSIUM).add((Block)AetherBlocks.AMBROSIUM_BLOCK.get());
      this.tag(AetherTags.Blocks.STORAGE_BLOCKS_ZANITE).add((Block)AetherBlocks.ZANITE_BLOCK.get());
      this.tag(BlockTags.WOODEN_STAIRS).add((Block)AetherBlocks.SKYROOT_STAIRS.get());
      this.tag(BlockTags.WOODEN_SLABS).add((Block)AetherBlocks.SKYROOT_SLAB.get());
      this.tag(BlockTags.WOODEN_FENCES).add((Block)AetherBlocks.SKYROOT_FENCE.get());
      this.tag(BlockTags.WOODEN_DOORS).add((Block)AetherBlocks.SKYROOT_DOOR.get());
      this.tag(BlockTags.WOODEN_TRAPDOORS).add((Block)AetherBlocks.SKYROOT_TRAPDOOR.get());
      this.tag(BlockTags.WOODEN_BUTTONS).add((Block)AetherBlocks.SKYROOT_BUTTON.get());
      this.tag(BlockTags.WOODEN_PRESSURE_PLATES).add((Block)AetherBlocks.SKYROOT_PRESSURE_PLATE.get());
      this.tag(BlockTags.BUTTONS).add(new Block[]{(Block)AetherBlocks.SKYROOT_BUTTON.get(), (Block)AetherBlocks.HOLYSTONE_BUTTON.get()});
      this.tag(BlockTags.STONE_BUTTONS).add((Block)AetherBlocks.HOLYSTONE_BUTTON.get());
      this.tag(BlockTags.PRESSURE_PLATES)
         .add(new Block[]{(Block)AetherBlocks.SKYROOT_PRESSURE_PLATE.get(), (Block)AetherBlocks.HOLYSTONE_PRESSURE_PLATE.get()});
      this.tag(BlockTags.SAPLINGS).add(new Block[]{(Block)AetherBlocks.SKYROOT_SAPLING.get(), (Block)AetherBlocks.GOLDEN_OAK_SAPLING.get()});
      this.tag(BlockTags.LOGS_THAT_BURN).addTags(new TagKey[]{AetherTags.Blocks.SKYROOT_LOGS, AetherTags.Blocks.GOLDEN_OAK_LOGS});
      this.tag(BlockTags.STAIRS)
         .add(
            new Block[]{
               (Block)AetherBlocks.SKYROOT_STAIRS.get(),
               (Block)AetherBlocks.CARVED_STAIRS.get(),
               (Block)AetherBlocks.ANGELIC_STAIRS.get(),
               (Block)AetherBlocks.HELLFIRE_STAIRS.get(),
               (Block)AetherBlocks.HOLYSTONE_STAIRS.get(),
               (Block)AetherBlocks.MOSSY_HOLYSTONE_STAIRS.get(),
               (Block)AetherBlocks.ICESTONE_STAIRS.get(),
               (Block)AetherBlocks.HOLYSTONE_BRICK_STAIRS.get(),
               (Block)AetherBlocks.AEROGEL_STAIRS.get()
            }
         );
      this.tag(BlockTags.SLABS)
         .add(
            new Block[]{
               (Block)AetherBlocks.SKYROOT_SLAB.get(),
               (Block)AetherBlocks.CARVED_SLAB.get(),
               (Block)AetherBlocks.ANGELIC_SLAB.get(),
               (Block)AetherBlocks.HELLFIRE_SLAB.get(),
               (Block)AetherBlocks.HOLYSTONE_SLAB.get(),
               (Block)AetherBlocks.MOSSY_HOLYSTONE_SLAB.get(),
               (Block)AetherBlocks.ICESTONE_SLAB.get(),
               (Block)AetherBlocks.HOLYSTONE_BRICK_SLAB.get(),
               (Block)AetherBlocks.AEROGEL_SLAB.get()
            }
         );
      this.tag(BlockTags.WALLS)
         .add(
            new Block[]{
               (Block)AetherBlocks.CARVED_WALL.get(),
               (Block)AetherBlocks.ANGELIC_WALL.get(),
               (Block)AetherBlocks.HELLFIRE_WALL.get(),
               (Block)AetherBlocks.HOLYSTONE_WALL.get(),
               (Block)AetherBlocks.MOSSY_HOLYSTONE_WALL.get(),
               (Block)AetherBlocks.ICESTONE_WALL.get(),
               (Block)AetherBlocks.HOLYSTONE_BRICK_WALL.get(),
               (Block)AetherBlocks.AEROGEL_WALL.get()
            }
         );
      this.tag(BlockTags.LEAVES)
         .add(
            new Block[]{
               (Block)AetherBlocks.SKYROOT_LEAVES.get(),
               (Block)AetherBlocks.GOLDEN_OAK_LEAVES.get(),
               (Block)AetherBlocks.CRYSTAL_LEAVES.get(),
               (Block)AetherBlocks.CRYSTAL_FRUIT_LEAVES.get(),
               (Block)AetherBlocks.HOLIDAY_LEAVES.get(),
               (Block)AetherBlocks.DECORATED_HOLIDAY_LEAVES.get()
            }
         );
      this.tag(BlockTags.SMALL_FLOWERS).add(new Block[]{(Block)AetherBlocks.PURPLE_FLOWER.get(), (Block)AetherBlocks.WHITE_FLOWER.get()});
      this.tag(BlockTags.BEDS).add((Block)AetherBlocks.SKYROOT_BED.get());
      this.tag(BlockTags.DIRT).addTag(AetherTags.Blocks.AETHER_DIRT);
      this.tag(BlockTags.FALL_DAMAGE_RESETTING).addTag(AetherTags.Blocks.AERCLOUDS);
      this.tag(BlockTags.FLOWER_POTS)
         .add(
            new Block[]{
               (Block)AetherBlocks.POTTED_BERRY_BUSH.get(),
               (Block)AetherBlocks.POTTED_BERRY_BUSH_STEM.get(),
               (Block)AetherBlocks.POTTED_PURPLE_FLOWER.get(),
               (Block)AetherBlocks.POTTED_WHITE_FLOWER.get(),
               (Block)AetherBlocks.POTTED_SKYROOT_SAPLING.get(),
               (Block)AetherBlocks.POTTED_GOLDEN_OAK_SAPLING.get()
            }
         );
      this.tag(BlockTags.ENDERMAN_HOLDABLE)
         .addTag(AetherTags.Blocks.AETHER_DIRT)
         .add(new Block[]{(Block)AetherBlocks.QUICKSOIL.get(), (Block)AetherBlocks.PURPLE_FLOWER.get(), (Block)AetherBlocks.WHITE_FLOWER.get()});
      this.tag(BlockTags.VALID_SPAWN).addTag(AetherTags.Blocks.AETHER_DIRT);
      this.tag(BlockTags.IMPERMEABLE).add((Block)AetherBlocks.QUICKSOIL_GLASS.get());
      this.tag(BlockTags.BAMBOO_PLANTABLE_ON).addTag(AetherTags.Blocks.AETHER_DIRT);
      this.tag(BlockTags.SIGNS).add(new Block[]{(Block)AetherBlocks.SKYROOT_SIGN.get(), (Block)AetherBlocks.SKYROOT_WALL_SIGN.get()});
      this.tag(BlockTags.STANDING_SIGNS).add((Block)AetherBlocks.SKYROOT_SIGN.get());
      this.tag(BlockTags.WALL_SIGNS).add((Block)AetherBlocks.SKYROOT_WALL_SIGN.get());
      this.tag(BlockTags.CEILING_HANGING_SIGNS).add((Block)AetherBlocks.SKYROOT_HANGING_SIGN.get());
      this.tag(BlockTags.WALL_HANGING_SIGNS).add((Block)AetherBlocks.SKYROOT_WALL_HANGING_SIGN.get());
      this.tag(BlockTags.DRAGON_IMMUNE)
         .addTags(
            new TagKey[]{
               AetherTags.Blocks.LOCKED_DUNGEON_BLOCKS,
               AetherTags.Blocks.TRAPPED_DUNGEON_BLOCKS,
               AetherTags.Blocks.BOSS_DOORWAY_DUNGEON_BLOCKS,
               AetherTags.Blocks.TREASURE_DOORWAY_DUNGEON_BLOCKS,
               AetherTags.Blocks.AEROGEL
            }
         );
      this.tag(BlockTags.WITHER_IMMUNE)
         .addTags(
            new TagKey[]{
               AetherTags.Blocks.LOCKED_DUNGEON_BLOCKS,
               AetherTags.Blocks.TRAPPED_DUNGEON_BLOCKS,
               AetherTags.Blocks.BOSS_DOORWAY_DUNGEON_BLOCKS,
               AetherTags.Blocks.TREASURE_DOORWAY_DUNGEON_BLOCKS
            }
         );
      this.tag(BlockTags.BEE_GROWABLES).add((Block)AetherBlocks.BERRY_BUSH_STEM.get());
      this.tag(BlockTags.PORTALS).add((Block)AetherBlocks.AETHER_PORTAL.get());
      this.tag(BlockTags.BEACON_BASE_BLOCKS).add(new Block[]{(Block)AetherBlocks.ZANITE_BLOCK.get(), (Block)AetherBlocks.ENCHANTED_GRAVITITE.get()});
      this.tag(BlockTags.WALL_POST_OVERRIDE).add((Block)AetherBlocks.AMBROSIUM_TORCH.get());
      this.tag(BlockTags.FENCE_GATES).add((Block)AetherBlocks.SKYROOT_FENCE_GATE.get());
      this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
         .add(
            new Block[]{
               (Block)AetherBlocks.HOLYSTONE.get(),
               (Block)AetherBlocks.MOSSY_HOLYSTONE.get(),
               (Block)AetherBlocks.ICESTONE.get(),
               (Block)AetherBlocks.AMBROSIUM_ORE.get(),
               (Block)AetherBlocks.ZANITE_ORE.get(),
               (Block)AetherBlocks.GRAVITITE_ORE.get(),
               (Block)AetherBlocks.HOLYSTONE_BRICKS.get(),
               (Block)AetherBlocks.AEROGEL.get(),
               (Block)AetherBlocks.AMBROSIUM_BLOCK.get(),
               (Block)AetherBlocks.ZANITE_BLOCK.get(),
               (Block)AetherBlocks.ENCHANTED_GRAVITITE.get(),
               (Block)AetherBlocks.ALTAR.get(),
               (Block)AetherBlocks.FREEZER.get(),
               (Block)AetherBlocks.INCUBATOR.get(),
               (Block)AetherBlocks.CARVED_STONE.get(),
               (Block)AetherBlocks.SENTRY_STONE.get(),
               (Block)AetherBlocks.ANGELIC_STONE.get(),
               (Block)AetherBlocks.LIGHT_ANGELIC_STONE.get(),
               (Block)AetherBlocks.HELLFIRE_STONE.get(),
               (Block)AetherBlocks.LIGHT_HELLFIRE_STONE.get(),
               (Block)AetherBlocks.TREASURE_CHEST.get(),
               (Block)AetherBlocks.PILLAR.get(),
               (Block)AetherBlocks.PILLAR_TOP.get(),
               (Block)AetherBlocks.HOLYSTONE_BUTTON.get(),
               (Block)AetherBlocks.HOLYSTONE_PRESSURE_PLATE.get(),
               (Block)AetherBlocks.CARVED_WALL.get(),
               (Block)AetherBlocks.ANGELIC_WALL.get(),
               (Block)AetherBlocks.HELLFIRE_WALL.get(),
               (Block)AetherBlocks.HOLYSTONE_WALL.get(),
               (Block)AetherBlocks.MOSSY_HOLYSTONE_WALL.get(),
               (Block)AetherBlocks.ICESTONE_WALL.get(),
               (Block)AetherBlocks.HOLYSTONE_BRICK_WALL.get(),
               (Block)AetherBlocks.AEROGEL_WALL.get(),
               (Block)AetherBlocks.CARVED_STAIRS.get(),
               (Block)AetherBlocks.ANGELIC_STAIRS.get(),
               (Block)AetherBlocks.HELLFIRE_STAIRS.get(),
               (Block)AetherBlocks.HOLYSTONE_STAIRS.get(),
               (Block)AetherBlocks.MOSSY_HOLYSTONE_STAIRS.get(),
               (Block)AetherBlocks.ICESTONE_STAIRS.get(),
               (Block)AetherBlocks.HOLYSTONE_BRICK_STAIRS.get(),
               (Block)AetherBlocks.AEROGEL_STAIRS.get(),
               (Block)AetherBlocks.CARVED_SLAB.get(),
               (Block)AetherBlocks.ANGELIC_SLAB.get(),
               (Block)AetherBlocks.HELLFIRE_SLAB.get(),
               (Block)AetherBlocks.HOLYSTONE_SLAB.get(),
               (Block)AetherBlocks.MOSSY_HOLYSTONE_SLAB.get(),
               (Block)AetherBlocks.ICESTONE_SLAB.get(),
               (Block)AetherBlocks.HOLYSTONE_BRICK_SLAB.get(),
               (Block)AetherBlocks.AEROGEL_SLAB.get(),
               (Block)AetherBlocks.SUN_ALTAR.get()
            }
         );
      this.tag(BlockTags.MINEABLE_WITH_AXE)
         .add(
            new Block[]{
               (Block)AetherBlocks.SKYROOT_LOG.get(),
               (Block)AetherBlocks.GOLDEN_OAK_LOG.get(),
               (Block)AetherBlocks.STRIPPED_SKYROOT_LOG.get(),
               (Block)AetherBlocks.SKYROOT_WOOD.get(),
               (Block)AetherBlocks.GOLDEN_OAK_WOOD.get(),
               (Block)AetherBlocks.STRIPPED_SKYROOT_WOOD.get(),
               (Block)AetherBlocks.SKYROOT_PLANKS.get(),
               (Block)AetherBlocks.SKYROOT_SIGN.get(),
               (Block)AetherBlocks.SKYROOT_WALL_SIGN.get(),
               (Block)AetherBlocks.SKYROOT_HANGING_SIGN.get(),
               (Block)AetherBlocks.SKYROOT_WALL_HANGING_SIGN.get(),
               (Block)AetherBlocks.BERRY_BUSH_STEM.get(),
               (Block)AetherBlocks.CHEST_MIMIC.get(),
               (Block)AetherBlocks.SKYROOT_FENCE.get(),
               (Block)AetherBlocks.SKYROOT_FENCE_GATE.get(),
               (Block)AetherBlocks.SKYROOT_DOOR.get(),
               (Block)AetherBlocks.SKYROOT_TRAPDOOR.get(),
               (Block)AetherBlocks.SKYROOT_BUTTON.get(),
               (Block)AetherBlocks.SKYROOT_PRESSURE_PLATE.get(),
               (Block)AetherBlocks.SKYROOT_STAIRS.get(),
               (Block)AetherBlocks.SKYROOT_SLAB.get(),
               (Block)AetherBlocks.SKYROOT_BOOKSHELF.get(),
               (Block)AetherBlocks.SKYROOT_BED.get()
            }
         );
      this.tag(BlockTags.MINEABLE_WITH_SHOVEL)
         .add(
            new Block[]{
               (Block)AetherBlocks.AETHER_GRASS_BLOCK.get(),
               (Block)AetherBlocks.ENCHANTED_AETHER_GRASS_BLOCK.get(),
               (Block)AetherBlocks.AETHER_DIRT.get(),
               (Block)AetherBlocks.QUICKSOIL.get(),
               (Block)AetherBlocks.AETHER_FARMLAND.get(),
               (Block)AetherBlocks.AETHER_DIRT_PATH.get()
            }
         );
      this.tag(BlockTags.MINEABLE_WITH_HOE)
         .add(
            new Block[]{
               (Block)AetherBlocks.COLD_AERCLOUD.get(),
               (Block)AetherBlocks.BLUE_AERCLOUD.get(),
               (Block)AetherBlocks.GOLDEN_AERCLOUD.get(),
               (Block)AetherBlocks.SKYROOT_LEAVES.get(),
               (Block)AetherBlocks.GOLDEN_OAK_LEAVES.get(),
               (Block)AetherBlocks.CRYSTAL_LEAVES.get(),
               (Block)AetherBlocks.CRYSTAL_FRUIT_LEAVES.get(),
               (Block)AetherBlocks.HOLIDAY_LEAVES.get(),
               (Block)AetherBlocks.DECORATED_HOLIDAY_LEAVES.get(),
               (Block)AetherBlocks.BERRY_BUSH.get()
            }
         );
      this.tag(BlockTags.SWORD_EFFICIENT).add(new Block[]{(Block)AetherBlocks.BERRY_BUSH.get(), (Block)AetherBlocks.BERRY_BUSH_STEM.get()});
      this.tag(BlockTags.NEEDS_STONE_TOOL)
         .add(
            new Block[]{
               (Block)AetherBlocks.ICESTONE.get(),
               (Block)AetherBlocks.ICESTONE_STAIRS.get(),
               (Block)AetherBlocks.ICESTONE_SLAB.get(),
               (Block)AetherBlocks.ICESTONE_WALL.get(),
               (Block)AetherBlocks.ZANITE_ORE.get(),
               (Block)AetherBlocks.ZANITE_BLOCK.get(),
               (Block)AetherBlocks.CARVED_STONE.get(),
               (Block)AetherBlocks.SENTRY_STONE.get(),
               (Block)AetherBlocks.CARVED_STAIRS.get(),
               (Block)AetherBlocks.CARVED_SLAB.get(),
               (Block)AetherBlocks.CARVED_WALL.get(),
               (Block)AetherBlocks.ANGELIC_STONE.get(),
               (Block)AetherBlocks.LIGHT_ANGELIC_STONE.get(),
               (Block)AetherBlocks.ANGELIC_STAIRS.get(),
               (Block)AetherBlocks.ANGELIC_SLAB.get(),
               (Block)AetherBlocks.ANGELIC_WALL.get(),
               (Block)AetherBlocks.HELLFIRE_STONE.get(),
               (Block)AetherBlocks.LIGHT_HELLFIRE_STONE.get(),
               (Block)AetherBlocks.HELLFIRE_STAIRS.get(),
               (Block)AetherBlocks.HELLFIRE_SLAB.get(),
               (Block)AetherBlocks.HELLFIRE_WALL.get(),
               (Block)AetherBlocks.TREASURE_CHEST.get(),
               (Block)AetherBlocks.PILLAR.get(),
               (Block)AetherBlocks.PILLAR_TOP.get()
            }
         );
      this.tag(BlockTags.NEEDS_IRON_TOOL).add(new Block[]{(Block)AetherBlocks.GRAVITITE_ORE.get(), (Block)AetherBlocks.ENCHANTED_GRAVITITE.get()});
      this.tag(BlockTags.NEEDS_DIAMOND_TOOL)
         .add(
            new Block[]{
               (Block)AetherBlocks.AEROGEL.get(),
               (Block)AetherBlocks.AEROGEL_STAIRS.get(),
               (Block)AetherBlocks.AEROGEL_SLAB.get(),
               (Block)AetherBlocks.AEROGEL_WALL.get()
            }
         );
      this.tag(BlockTags.CONVERTABLE_TO_MUD).add((Block)AetherBlocks.AETHER_DIRT.get());
      this.tag(BlockTags.SCULK_REPLACEABLE)
         .addTag(AetherTags.Blocks.HOLYSTONE)
         .add(new Block[]{(Block)AetherBlocks.AETHER_DIRT.get(), (Block)AetherBlocks.QUICKSOIL.get()});
      this.tag(BlockTags.SNAPS_GOAT_HORN)
         .addTags(new TagKey[]{AetherTags.Blocks.SKYROOT_LOGS, AetherTags.Blocks.GOLDEN_OAK_LOGS})
         .add(
            new Block[]{
               (Block)AetherBlocks.HOLYSTONE.get(),
               (Block)AetherBlocks.ICESTONE.get(),
               (Block)AetherBlocks.AMBROSIUM_ORE.get(),
               (Block)AetherBlocks.ZANITE_ORE.get(),
               (Block)AetherBlocks.GRAVITITE_ORE.get()
            }
         );
      this.tag(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON).add((Block)AetherBlocks.ICESTONE.get());
      this.tag(BlockTags.ENCHANTMENT_POWER_PROVIDER).add((Block)AetherBlocks.SKYROOT_BOOKSHELF.get());
      this.tag(BlockTags.SNIFFER_DIGGABLE_BLOCK)
         .add(
            new Block[]{
               (Block)AetherBlocks.AETHER_GRASS_BLOCK.get(), (Block)AetherBlocks.AETHER_DIRT.get(), (Block)AetherBlocks.ENCHANTED_AETHER_GRASS_BLOCK.get()
            }
         );
      this.tag(net.neoforged.neoforge.common.Tags.Blocks.BOOKSHELVES).add((Block)AetherBlocks.SKYROOT_BOOKSHELF.get());
      this.tag(net.neoforged.neoforge.common.Tags.Blocks.FENCE_GATES_WOODEN).add((Block)AetherBlocks.SKYROOT_FENCE_GATE.get());
      this.tag(net.neoforged.neoforge.common.Tags.Blocks.FENCES_WOODEN).add((Block)AetherBlocks.SKYROOT_FENCE.get());
      this.tag(net.neoforged.neoforge.common.Tags.Blocks.GLASS_BLOCKS_COLORLESS).add((Block)AetherBlocks.QUICKSOIL_GLASS.get());
      this.tag(net.neoforged.neoforge.common.Tags.Blocks.GLASS_BLOCKS_CHEAP).add((Block)AetherBlocks.QUICKSOIL_GLASS.get());
      this.tag(net.neoforged.neoforge.common.Tags.Blocks.GLASS_PANES_COLORLESS).add((Block)AetherBlocks.QUICKSOIL_GLASS_PANE.get());
      this.tag(net.neoforged.neoforge.common.Tags.Blocks.ORE_RATES_SINGULAR)
         .add(new Block[]{(Block)AetherBlocks.AMBROSIUM_ORE.get(), (Block)AetherBlocks.ZANITE_ORE.get(), (Block)AetherBlocks.GRAVITITE_ORE.get()});
      this.tag(net.neoforged.neoforge.common.Tags.Blocks.ORES)
         .add(new Block[]{(Block)AetherBlocks.AMBROSIUM_ORE.get(), (Block)AetherBlocks.ZANITE_ORE.get(), (Block)AetherBlocks.GRAVITITE_ORE.get()});
      this.tag(net.neoforged.neoforge.common.Tags.Blocks.RELOCATION_NOT_SUPPORTED)
         .addTags(
            new TagKey[]{
               AetherTags.Blocks.TRAPPED_DUNGEON_BLOCKS,
               AetherTags.Blocks.LOCKED_DUNGEON_BLOCKS,
               AetherTags.Blocks.BOSS_DOORWAY_DUNGEON_BLOCKS,
               AetherTags.Blocks.TREASURE_DOORWAY_DUNGEON_BLOCKS
            }
         );
      this.tag(net.neoforged.neoforge.common.Tags.Blocks.STONES).addTag(AetherTags.Blocks.HOLYSTONE);
      this.tag(net.neoforged.neoforge.common.Tags.Blocks.STORAGE_BLOCKS)
         .add(new Block[]{(Block)AetherBlocks.AMBROSIUM_BLOCK.get(), (Block)AetherBlocks.ZANITE_BLOCK.get()});
      this.tag(net.neoforged.neoforge.common.Tags.Blocks.STRIPPED_LOGS).add((Block)AetherBlocks.STRIPPED_SKYROOT_LOG.get());
      this.tag(net.neoforged.neoforge.common.Tags.Blocks.STRIPPED_WOODS).add((Block)AetherBlocks.STRIPPED_SKYROOT_WOOD.get());
   }
}
