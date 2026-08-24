package io.github.razordevs.deep_aether.init;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.item.AetherCreativeTabs;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.protect_your_moa.item.ProtectItems;
import com.aetherteam.treasure_reforging.block.ReforgingBlocks;
import com.aetherteam.treasure_reforging.item.ReforgingItems;
import io.github.razordevs.deep_aether.item.component.DADataComponentTypes;
import io.github.razordevs.deep_aether.item.component.MoaFodder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.WallBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@EventBusSubscriber(
   bus = Bus.MOD,
   value = {Dist.CLIENT},
   modid = "deep_aether"
)
public class DATabs {
   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void buildCreativeModeTabs(BuildCreativeModeTabContentsEvent event) {
      ResourceKey<CreativeModeTab> tab = event.getTabKey();
      if (tab == AetherCreativeTabs.AETHER_BUILDING_BLOCKS.getKey()) {
         addToTab(
            ((RotatedPillarBlock)AetherBlocks.GOLDEN_OAK_WOOD.get()).asItem(),
            new Block[]{
               (Block)DABlocks.ROSEROOT_LOG.get(),
               (Block)DABlocks.ROTTEN_ROSEROOT_LOG.get(),
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
               (Block)DABlocks.SUNROOT_BUTTON.get()
            },
            event
         );
         addToTab(
            ((WallBlock)AetherBlocks.HOLYSTONE_BRICK_WALL.get()).asItem(),
            new Block[]{
               (Block)DABlocks.HOLYSTONE_TILES.get(),
               (Block)DABlocks.HOLYSTONE_TILE_STAIRS.get(),
               (Block)DABlocks.HOLYSTONE_TILE_SLAB.get(),
               (Block)DABlocks.HOLYSTONE_TILE_WALL.get(),
               (Block)DABlocks.BIG_HOLYSTONE_BRICKS.get(),
               (Block)DABlocks.BIG_HOLYSTONE_BRICKS_STAIRS.get(),
               (Block)DABlocks.BIG_HOLYSTONE_BRICKS_SLAB.get(),
               (Block)DABlocks.BIG_HOLYSTONE_BRICKS_WALL.get(),
               (Block)DABlocks.HOLYSTONE_PILLAR.get(),
               (Block)DABlocks.HOLYSTONE_PILLAR_UP.get(),
               (Block)DABlocks.HOLYSTONE_PILLAR_DOWN.get(),
               (Block)DABlocks.CHISELED_HOLYSTONE.get(),
               (Block)DABlocks.COBBLED_ASETERITE.get(),
               (Block)DABlocks.COBBLED_ASETERITE_STAIRS.get(),
               (Block)DABlocks.COBBLED_ASETERITE_SLAB.get(),
               (Block)DABlocks.COBBLED_ASETERITE_WALL.get(),
               (Block)DABlocks.ASETERITE.get(),
               (Block)DABlocks.ASETERITE_STAIRS.get(),
               (Block)DABlocks.ASETERITE_SLAB.get(),
               (Block)DABlocks.ASETERITE_WALL.get(),
               (Block)DABlocks.POLISHED_ASETERITE.get(),
               (Block)DABlocks.POLISHED_ASETERITE_STAIRS.get(),
               (Block)DABlocks.POLISHED_ASETERITE_SLAB.get(),
               (Block)DABlocks.POLISHED_ASETERITE_WALL.get(),
               (Block)DABlocks.ASETERITE_BRICKS.get(),
               (Block)DABlocks.ASETERITE_BRICKS_STAIRS.get(),
               (Block)DABlocks.ASETERITE_BRICKS_SLAB.get(),
               (Block)DABlocks.ASETERITE_BRICKS_WALL.get(),
               (Block)DABlocks.RAW_CLORITE.get(),
               (Block)DABlocks.RAW_CLORITE_STAIRS.get(),
               (Block)DABlocks.RAW_CLORITE_SLAB.get(),
               (Block)DABlocks.RAW_CLORITE_WALL.get(),
               (Block)DABlocks.CLORITE.get(),
               (Block)DABlocks.CLORITE_STAIRS.get(),
               (Block)DABlocks.CLORITE_SLAB.get(),
               (Block)DABlocks.CLORITE_WALL.get(),
               (Block)DABlocks.POLISHED_CLORITE.get(),
               (Block)DABlocks.POLISHED_CLORITE_STAIRS.get(),
               (Block)DABlocks.POLISHED_CLORITE_SLAB.get(),
               (Block)DABlocks.POLISHED_CLORITE_WALL.get(),
               (Block)DABlocks.CLORITE_PILLAR.get()
            },
            event
         );
         addToTab(
            ((WallBlock)AetherBlocks.MOSSY_HOLYSTONE_WALL.get()).asItem(),
            new Block[]{
               (Block)DABlocks.MOSSY_HOLYSTONE_BRICKS.get(),
               (Block)DABlocks.MOSSY_HOLYSTONE_BRICK_STAIRS.get(),
               (Block)DABlocks.MOSSY_HOLYSTONE_BRICK_SLAB.get(),
               (Block)DABlocks.MOSSY_HOLYSTONE_BRICK_WALL.get(),
               (Block)DABlocks.MOSSY_HOLYSTONE_TILES.get(),
               (Block)DABlocks.MOSSY_HOLYSTONE_TILE_STAIRS.get(),
               (Block)DABlocks.MOSSY_HOLYSTONE_TILE_SLAB.get(),
               (Block)DABlocks.MOSSY_HOLYSTONE_TILE_WALL.get()
            },
            event
         );
         if (ModList.get().isLoaded("aether_redux")) {
            addToTab(
               ((Block)DABlocks.MOSSY_HOLYSTONE_TILE_WALL.get()).asItem(),
               new Block[]{
                  (Block)DABlocks.GILDED_HOLYSTONE_BRICKS.get(),
                  (Block)DABlocks.GILDED_HOLYSTONE_BRICK_STAIRS.get(),
                  (Block)DABlocks.GILDED_HOLYSTONE_BRICK_SLAB.get(),
                  (Block)DABlocks.GILDED_HOLYSTONE_BRICK_WALL.get(),
                  (Block)DABlocks.GILDED_HOLYSTONE_TILES.get(),
                  (Block)DABlocks.GILDED_HOLYSTONE_TILE_STAIRS.get(),
                  (Block)DABlocks.GILDED_HOLYSTONE_TILE_SLAB.get(),
                  (Block)DABlocks.GILDED_HOLYSTONE_TILE_WALL.get(),
                  (Block)DABlocks.BLIGHTMOSS_HOLYSTONE_BRICKS.get(),
                  (Block)DABlocks.BLIGHTMOSS_HOLYSTONE_BRICK_STAIRS.get(),
                  (Block)DABlocks.BLIGHTMOSS_HOLYSTONE_BRICK_SLAB.get(),
                  (Block)DABlocks.BLIGHTMOSS_HOLYSTONE_BRICK_WALL.get(),
                  (Block)DABlocks.BLIGHTMOSS_HOLYSTONE_TILES.get(),
                  (Block)DABlocks.BLIGHTMOSS_HOLYSTONE_TILE_STAIRS.get(),
                  (Block)DABlocks.BLIGHTMOSS_HOLYSTONE_TILE_SLAB.get(),
                  (Block)DABlocks.BLIGHTMOSS_HOLYSTONE_TILE_WALL.get()
               },
               event
            );
         }

         addToTab(
            ((WallBlock)AetherBlocks.HOLYSTONE_BRICK_WALL.get()).asItem(),
            new Block[]{
               (Block)DABlocks.PACKED_AETHER_MUD.get(),
               (Block)DABlocks.AETHER_MUD_BRICKS.get(),
               (Block)DABlocks.AETHER_MUD_BRICKS_STAIRS.get(),
               (Block)DABlocks.AETHER_MUD_BRICKS_SLAB.get(),
               (Block)DABlocks.AETHER_MUD_BRICKS_WALL.get()
            },
            event
         );
         addToTab(((Block)AetherBlocks.ZANITE_BLOCK.get()).asItem(), ((Block)DABlocks.SKYJADE_BLOCK.get()).asItem(), event);
         addToTab(((Block)AetherBlocks.ENCHANTED_GRAVITITE.get()).asItem(), ((Block)DABlocks.STRATUS_BLOCK.get()).asItem(), event);
         if (ModList.get().isLoaded("aether_treasure_reforging")) {
            addToTab(ReforgingBlocks.PYRAL_BLOCK.asItem(), DABlocks.SQUALL_BLOCK.asItem(), event);
         }

         if (ModList.get().isLoaded("aether_beyond_parity")) {
            addToTab(DABlocks.ROSEROOT_LOG.asItem(), DABlocks.ROSEROOT_LOG_WALL.asItem(), event);
            addToTab(DABlocks.STRIPPED_ROSEROOT_LOG.asItem(), DABlocks.STRIPPED_ROSEROOT_LOG_WALL.asItem(), event);
            addToTab(DABlocks.ROSEROOT_WOOD.asItem(), DABlocks.ROSEROOT_WOOD_WALL.asItem(), event);
            addToTab(DABlocks.STRIPPED_ROSEROOT_WOOD.asItem(), DABlocks.STRIPPED_ROSEROOT_WOOD_WALL.asItem(), event);
            addToTab(DABlocks.YAGROOT_LOG.asItem(), DABlocks.YAGROOT_LOG_WALL.asItem(), event);
            addToTab(DABlocks.STRIPPED_YAGROOT_LOG.asItem(), DABlocks.STRIPPED_YAGROOT_LOG_WALL.asItem(), event);
            addToTab(DABlocks.YAGROOT_WOOD.asItem(), DABlocks.YAGROOT_WOOD_WALL.asItem(), event);
            addToTab(DABlocks.STRIPPED_YAGROOT_WOOD.asItem(), DABlocks.STRIPPED_YAGROOT_WOOD_WALL.asItem(), event);
            addToTab(DABlocks.CRUDEROOT_LOG.asItem(), DABlocks.CRUDEROOT_LOG_WALL.asItem(), event);
            addToTab(DABlocks.STRIPPED_CRUDEROOT_LOG.asItem(), DABlocks.STRIPPED_CRUDEROOT_LOG_WALL.asItem(), event);
            addToTab(DABlocks.CRUDEROOT_WOOD.asItem(), DABlocks.CRUDEROOT_WOOD_WALL.asItem(), event);
            addToTab(DABlocks.STRIPPED_CRUDEROOT_WOOD.asItem(), DABlocks.STRIPPED_CRUDEROOT_WOOD_WALL.asItem(), event);
            addToTab(DABlocks.CONBERRY_LOG.asItem(), DABlocks.CONBERRY_LOG_WALL.asItem(), event);
            addToTab(DABlocks.STRIPPED_CONBERRY_LOG.asItem(), DABlocks.STRIPPED_CONBERRY_LOG_WALL.asItem(), event);
            addToTab(DABlocks.CONBERRY_WOOD.asItem(), DABlocks.CONBERRY_WOOD_WALL.asItem(), event);
            addToTab(DABlocks.STRIPPED_CONBERRY_WOOD.asItem(), DABlocks.STRIPPED_CONBERRY_WOOD_WALL.asItem(), event);
            addToTab(DABlocks.SUNROOT_LOG.asItem(), DABlocks.SUNROOT_LOG_WALL.asItem(), event);
            addToTab(DABlocks.STRIPPED_SUNROOT_LOG.asItem(), DABlocks.STRIPPED_SUNROOT_LOG_WALL.asItem(), event);
            addToTab(DABlocks.SUNROOT_WOOD.asItem(), DABlocks.SUNROOT_WOOD_WALL.asItem(), event);
            addToTab(DABlocks.STRIPPED_SUNROOT_WOOD.asItem(), DABlocks.STRIPPED_SUNROOT_WOOD_WALL.asItem(), event);
         }
      }

      if (tab == AetherCreativeTabs.AETHER_NATURAL_BLOCKS.getKey()) {
         addToTab(((Block)AetherBlocks.ENCHANTED_AETHER_GRASS_BLOCK.get()).asItem(), ((Block)DABlocks.GOLDEN_GRASS_BLOCK.get()).asItem(), event);
         addToTab(((Block)AetherBlocks.AETHER_DIRT_PATH.get()).asItem(), ((Block)DABlocks.GOLDEN_DIRT_PATH.get()).asItem(), event);
         addToTab(((Block)AetherBlocks.AETHER_DIRT.get()).asItem(), ((Block)DABlocks.AETHER_COARSE_DIRT.get()).asItem(), event);
         addToTab(
            ((Block)AetherBlocks.AETHER_FARMLAND.get()).asItem(),
            new Block[]{
               (Block)DABlocks.AETHER_MOSS_BLOCK.get(),
               (Block)DABlocks.AETHER_MOSS_CARPET.get(),
               (Block)DABlocks.CLOUDBLOOM_CARPET.get(),
               (Block)DABlocks.AETHER_MUD.get(),
               (Block)DABlocks.MUDDY_YAGROOT_ROOTS.get(),
               (Block)DABlocks.YAGROOT_ROOTS.get()
            },
            event
         );
         addToTab(((Block)AetherBlocks.ICESTONE.get()).asItem(), new Block[]{(Block)DABlocks.ASETERITE.get(), (Block)DABlocks.CLORITE.get()}, event);
         addToTab(((Block)AetherBlocks.GRAVITITE_ORE.get()).asItem(), ((Block)DABlocks.SKYJADE_ORE.get()).asItem(), event);
         addToTab(
            ((RotatedPillarBlock)AetherBlocks.GOLDEN_OAK_LOG.get()).asItem(),
            new Block[]{
               (Block)DABlocks.ROSEROOT_LOG.get(),
               (Block)DABlocks.ROTTEN_ROSEROOT_LOG.get(),
               (Block)DABlocks.YAGROOT_LOG.get(),
               (Block)DABlocks.CRUDEROOT_LOG.get(),
               (Block)DABlocks.CONBERRY_LOG.get(),
               (Block)DABlocks.SUNROOT_LOG.get()
            },
            event
         );
         addToTab(
            ((Block)AetherBlocks.DECORATED_HOLIDAY_LEAVES.get()).asItem(),
            new Block[]{
               (Block)DABlocks.ROSEROOT_LEAVES.get(),
               (Block)DABlocks.FLOWERING_ROSEROOT_LEAVES.get(),
               (Block)DABlocks.AERGLOW_BLOSSOM_BLOCK.get(),
               (Block)DABlocks.BLUE_ROSEROOT_LEAVES.get(),
               (Block)DABlocks.FLOWERING_BLUE_ROSEROOT_LEAVES.get(),
               (Block)DABlocks.YAGROOT_LEAVES.get(),
               (Block)DABlocks.CRUDEROOT_LEAVES.get(),
               (Block)DABlocks.CONBERRY_LEAVES.get(),
               (Block)DABlocks.SUNROOT_LEAVES.get(),
               (Block)DABlocks.LIGHTCAP_MUSHROOM_BLOCK.get()
            },
            event
         );
         addToTab(
            ((SaplingBlock)AetherBlocks.GOLDEN_OAK_SAPLING.get()).asItem(),
            new Block[]{
               (Block)DABlocks.ROSEROOT_SAPLING.get(),
               (Block)DABlocks.BLUE_ROSEROOT_SAPLING.get(),
               (Block)DABlocks.YAGROOT_SAPLING.get(),
               (Block)DABlocks.YAGROOT_VINE.get(),
               (Block)DABlocks.CRUDEROOT_SAPLING.get(),
               (Block)DABlocks.CONBERRY_SAPLING.get(),
               (Block)DABlocks.SUNROOT_SAPLING.get(),
               (Block)DABlocks.SUNROOT_HANGER.get()
            },
            event
         );
         addToTab(((Block)AetherBlocks.BERRY_BUSH_STEM.get()).asItem(), (Item)DAItems.SQUASH_SEEDS.get(), event);
         addToTab(((Block)AetherBlocks.BERRY_BUSH.get()).asItem(), ((Block)DABlocks.LIGHTCAP_MUSHROOMS.get()).asItem(), event);
         addToTab(
            ((Block)AetherBlocks.WHITE_FLOWER.get()).asItem(),
            new Block[]{
               (Block)DABlocks.RADIANT_ORCHID.get(),
               (Block)DABlocks.AERLAVENDER.get(),
               (Block)DABlocks.TALL_AERLAVENDER.get(),
               (Block)DABlocks.AETHER_CATTAILS.get(),
               (Block)DABlocks.TALL_AETHER_CATTAILS.get(),
               (Block)DABlocks.GOLDEN_FLOWER.get(),
               (Block)DABlocks.ENCHANTED_BLOSSOM.get(),
               (Block)DABlocks.FEATHER_GRASS.get(),
               (Block)DABlocks.TALL_FEATHER_GRASS.get(),
               (Block)DABlocks.SKY_TULIPS.get(),
               (Block)DABlocks.IASPOVE.get(),
               (Block)DABlocks.GOLDEN_ASPESS.get(),
               (Block)DABlocks.ECHAISY.get(),
               (Block)DABlocks.MINI_GOLDEN_GRASS.get(),
               (Block)DABlocks.SHORT_GOLDEN_GRASS.get(),
               (Block)DABlocks.MEDIUM_GOLDEN_GRASS.get(),
               (Block)DABlocks.TALL_GOLDEN_GRASS.get(),
               (Block)DABlocks.BLUE_SQUASH.get(),
               (Block)DABlocks.GREEN_SQUASH.get(),
               (Block)DABlocks.PURPLE_SQUASH.get(),
               (Block)DABlocks.CARVED_BLUE_SQUASH.get(),
               (Block)DABlocks.CARVED_GREEN_SQUASH.get(),
               (Block)DABlocks.CARVED_PURPLE_SQUASH.get()
            },
            event
         );
         addToTab(
            ((Block)AetherBlocks.GOLDEN_AERCLOUD.get()).asItem(),
            new Block[]{(Block)DABlocks.AERSMOG.get(), (Block)DABlocks.STERLING_AERCLOUD.get(), (Block)DABlocks.CHROMATIC_AERCLOUD.get()},
            event
         );
      }

      if (tab == AetherCreativeTabs.AETHER_FUNCTIONAL_BLOCKS.getKey()) {
         addToTab(
            ((Block)AetherBlocks.AMBROSIUM_TORCH.get()).asItem(),
            new Block[]{(Block)DABlocks.AMBROSIUM_TIKI_TORCH.get(), (Block)DABlocks.SKYJADE_LANTERN.get(), (Block)DABlocks.SKYJADE_CHAIN.get()},
            event
         );
         addToTab(
            ((CeilingHangingSignBlock)AetherBlocks.SKYROOT_HANGING_SIGN.get()).asItem(),
            new Block[]{
               (Block)DABlocks.ROSEROOT_SIGN.get(),
               (Block)DABlocks.ROSEROOT_HANGING_SIGN.get(),
               (Block)DABlocks.YAGROOT_SIGN.get(),
               (Block)DABlocks.YAGROOT_HANGING_SIGN.get(),
               (Block)DABlocks.CRUDEROOT_SIGN.get(),
               (Block)DABlocks.CRUDEROOT_HANGING_SIGN.get(),
               (Block)DABlocks.CONBERRY_SIGN.get(),
               (Block)DABlocks.CONBERRY_HANGING_SIGN.get(),
               (Block)DABlocks.SUNROOT_SIGN.get(),
               (Block)DABlocks.SUNROOT_HANGING_SIGN.get(),
               (Block)DABlocks.COMBINER.get()
            },
            event
         );
      }

      if (tab == AetherCreativeTabs.AETHER_EQUIPMENT_AND_UTILITIES.getKey()) {
         addToTab(
            (Item)AetherItems.ZANITE_HOE.get(),
            new Item[]{
               (Item)DAItems.SKYJADE_TOOLS_SWORD.get(),
               (Item)DAItems.SKYJADE_TOOLS_SHOVEL.get(),
               (Item)DAItems.SKYJADE_TOOLS_PICKAXE.get(),
               (Item)DAItems.SKYJADE_TOOLS_AXE.get(),
               (Item)DAItems.SKYJADE_TOOLS_HOE.get()
            },
            event
         );
         addToTab(
            (Item)AetherItems.CLOUD_STAFF.get(),
            new Item[]{(Item)DAItems.STORM_SWORD.get(), (Item)DAItems.STORM_BOW.get(), (Item)DAItems.BLADE_OF_LUCK.get()},
            event
         );
         addToTab(
            (Item)AetherItems.GRAVITITE_HOE.get(),
            new Item[]{
               (Item)DAItems.STRATUS_SWORD.get(),
               (Item)DAItems.STRATUS_SHOVEL.get(),
               (Item)DAItems.STRATUS_PICKAXE.get(),
               (Item)DAItems.STRATUS_AXE.get(),
               (Item)DAItems.STRATUS_HOE.get()
            },
            event
         );
         addToTab((Item)AetherItems.BRONZE_DUNGEON_KEY.get(), (Item)DAItems.BRASS_DUNGEON_KEY.get(), event);
         addToTab((Item)AetherItems.SKYROOT_POWDER_SNOW_BUCKET.get(), (Item)DAItems.SKYROOT_VIRULENT_QUICKSAND_BUCKET.get(), event);
         addToTab((Item)AetherItems.SKYROOT_AXOLOTL_BUCKET.get(), (Item)DAItems.SKYROOT_AERGLOW_FISH_BUCKET.get(), event);
         addToTab(
            (Item)AetherItems.SKYROOT_POISON_BUCKET.get(),
            new Item[]{
               (Item)DAItems.VIRULENT_QUICKSAND_BUCKET.get(),
               (Item)DAItems.REMEDY_BUCKET.get(),
               (Item)DAItems.PLACEABLE_POISON_BUCKET.get(),
               (Item)DAItems.AERGLOW_FISH_BUCKET.get()
            },
            event
         );
         addToTab((Item)AetherItems.BLACK_MOA_EGG.get(), (Item)DAItems.QUAIL_EGG.get(), event);
         addToTab(
            (Item)AetherItems.SKYROOT_CHEST_BOAT.get(),
            new Item[]{
               (Item)DAItems.ROSEROOT_BOAT.get(),
               (Item)DAItems.ROSEROOT_CHEST_BOAT.get(),
               (Item)DAItems.YAGROOT_BOAT.get(),
               (Item)DAItems.YAGROOT_CHEST_BOAT.get(),
               (Item)DAItems.CRUDEROOT_BOAT.get(),
               (Item)DAItems.CRUDEROOT_CHEST_BOAT.get(),
               (Item)DAItems.CONBERRY_BOAT.get(),
               (Item)DAItems.CONBERRY_CHEST_BOAT.get(),
               (Item)DAItems.SUNROOT_BOAT.get(),
               (Item)DAItems.SUNROOT_CHEST_BOAT.get()
            },
            event
         );
         addToTab(
            (Item)AetherItems.MUSIC_DISC_SLIDERS_WRATH.get(),
            new Item[]{
               (Item)DAItems.MUSIC_DISC_A_MORNING_WISH.get(),
               (Item)DAItems.MUSIC_DISC_NABOORU.get(),
               (Item)DAItems.MUSIC_DISC_CYCLONE.get(),
               (Item)DAItems.MUSIC_DISC_ABOVE_THE_RAIN.get(),
               (Item)DAItems.MUSIC_DISC_ATTA.get(),
               (Item)DAItems.MUSIC_DISC_FAENT.get(),
               (Item)DAItems.MUSIC_DISC_HIMININN.get()
            },
            event
         );
         if (ModList.get().isLoaded("aether_treasure_reforging")) {
            addToTab(ReforgingItems.PHOENIX_UPGRADE_SMITHING_TEMPLATE.asItem(), DAItems.STORMFORGED_SMITHING_TEMPLATE.asItem(), event);
         }
      }

      if (tab == AetherCreativeTabs.AETHER_ARMOR_AND_ACCESSORIES.getKey()) {
         addToTab(
            (Item)AetherItems.ZANITE_GLOVES.get(),
            new Item[]{
               (Item)DAItems.SKYJADE_HELMET.get(),
               (Item)DAItems.SKYJADE_CHESTPLATE.get(),
               (Item)DAItems.SKYJADE_LEGGINGS.get(),
               (Item)DAItems.SKYJADE_BOOTS.get(),
               (Item)DAItems.SKYJADE_GLOVES.get()
            },
            event
         );
         addToTab(
            (Item)AetherItems.GRAVITITE_GLOVES.get(),
            new Item[]{
               (Item)DAItems.STRATUS_HELMET.get(),
               (Item)DAItems.STRATUS_CHESTPLATE.get(),
               (Item)DAItems.STRATUS_LEGGINGS.get(),
               (Item)DAItems.STRATUS_BOOTS.get(),
               (Item)DAItems.STRATUS_GLOVES.get()
            },
            event
         );
         addToTab(
            (Item)AetherItems.NEPTUNE_GLOVES.get(),
            new Item[]{
               (Item)DAItems.STORMFORGED_HELMET.get(),
               (Item)DAItems.STORMFORGED_CHESTPLATE.get(),
               (Item)DAItems.STORMFORGED_LEGGINGS.get(),
               (Item)DAItems.STORMFORGED_BOOTS.get(),
               (Item)DAItems.STORMFORGED_GLOVES.get()
            },
            event
         );
         addToTab(
            (Item)AetherItems.ZANITE_PENDANT.get(),
            new Item[]{(Item)DAItems.SKYJADE_RING.get(), (Item)DAItems.GRAVITITE_RING.get(), (Item)DAItems.STRATUS_RING.get()},
            event
         );
         addToTab((Item)AetherItems.ICE_PENDANT.get(), (Item)DAItems.SPOOKY_RING.get(), event);
         addToTab((Item)AetherItems.SWET_CAPE.get(), (Item)DAItems.CLOUD_CAPE.get(), event);
         addToTab(
            (Item)AetherItems.SHIELD_OF_REPULSION.get(),
            new Item[]{
               (Item)DAItems.SLIDER_EYE.get(),
               (Item)DAItems.MEDAL_OF_HONOR.get(),
               (Item)DAItems.SUN_CORE.get(),
               (Item)DAItems.AFTERBURNER.get(),
               (Item)DAItems.AERWHALE_SADDLE.get(),
               (Item)DAItems.AERCLOUD_NECKLACE.get(),
               (Item)DAItems.WIND_SHIELD.get(),
               (Item)DAItems.FLOATY_SCARF.get()
            },
            event
         );
         if (ModList.get().isLoaded("aether_protect_your_moa")) {
            addToTab((Item)ProtectItems.ZANITE_MOA_ARMOR.get(), (Item)DAItems.SKYJADE_MOA_ARMOR.get(), event);
         }
      }

      if (tab == AetherCreativeTabs.AETHER_FOOD_AND_DRINKS.getKey()) {
         addToTab(
            (Item)AetherItems.CANDY_CANE.get(),
            new Item[]{
               (Item)DAItems.RAW_QUAIL.get(), (Item)DAItems.COOKED_QUAIL.get(), (Item)DAItems.RAW_AERGLOW_FISH.get(), (Item)DAItems.COOKED_AERGLOW_FISH.get()
            },
            event
         );
         event.insertAfter(
            new ItemStack((ItemLike)DAItems.COOKED_AERGLOW_FISH.get()), getMoaFodderStack(MobEffects.FIRE_RESISTANCE), TabVisibility.PARENT_AND_SEARCH_TABS
         );
         event.insertAfter(new ItemStack((ItemLike)DAItems.COOKED_AERGLOW_FISH.get()), getMoaFodderStack(MobEffects.JUMP), TabVisibility.PARENT_AND_SEARCH_TABS);
         event.insertAfter(
            new ItemStack((ItemLike)DAItems.COOKED_AERGLOW_FISH.get()), getMoaFodderStack(DAMobEffects.MOA_BONUS_JUMPS), TabVisibility.PARENT_AND_SEARCH_TABS
         );
         addToTab(
            (Item)AetherItems.WHITE_APPLE.get(),
            new Item[]{
               (Item)DAItems.GOLDEN_BERRIES.get(),
               (Item)DAItems.FROZEN_GOLDEN_BERRIES.get(),
               (Item)DAItems.BLUE_SQUASH_SLICE.get(),
               (Item)DAItems.GREEN_SQUASH_SLICE.get(),
               (Item)DAItems.PURPLE_SQUASH_SLICE.get(),
               (Item)DAItems.ANTIDOTE.get(),
               (Item)DAItems.ENCHANTED_ANTIDOTE.get()
            },
            event
         );
      }

      if (tab == AetherCreativeTabs.AETHER_INGREDIENTS.getKey()) {
         addToTab(
            (Item)AetherItems.ZANITE_GEMSTONE.get(),
            new Item[]{(Item)DAItems.SKYJADE.get(), (Item)DAItems.SKYJADE_NUGGET.get(), (Item)DAItems.CLOUDBLOOM_BOUQUET.get()},
            event
         );
         addToTab(
            ((Block)AetherBlocks.ENCHANTED_GRAVITITE.get()).asItem(),
            new Item[]{(Item)DAItems.STRATUS_INGOT.get(), ((Block)DABlocks.CHROMATIC_AERCLOUD.get()).asItem()},
            event
         );
         addToTab(
            (Item)AetherItems.GOLDEN_AMBER.get(),
            new Item[]{(Item)DAItems.GOLDEN_GRASS_SEEDS.get(), (Item)DAItems.GOLDEN_SWET_BALL.get(), ((Block)DABlocks.GLOWING_SPORES.get()).asItem()},
            event
         );
         addToTab((Item)AetherItems.AECHOR_PETAL.get(), (Item)DAItems.AERGLOW_BLOSSOM.get(), event);
         addToTab(
            (Item)AetherItems.SWET_BALL.get(),
            new Item[]{(Item)DAItems.QUAIL_EGG.get(), (Item)DAItems.BIO_CRYSTAL.get(), (Item)DAItems.STRATUS_SMITHING_TEMPLATE.get()},
            event
         );
         if (ModList.get().isLoaded("aether_treasure_reforging")) {
            addToTab(ReforgingItems.PYRAL_INGOT.asItem(), DAItems.SQUALL_PLATE.asItem(), event);
         }
      }

      if (tab == AetherCreativeTabs.AETHER_SPAWN_EGGS.getKey()) {
         addToTab((Item)AetherItems.AERBUNNY_SPAWN_EGG.get(), new Item[]{(Item)DAItems.AETHER_FISH_SPAWN_EGG.get()}, event);
         addToTab((Item)AetherItems.MOA_SPAWN_EGG.get(), new Item[]{(Item)DAItems.QUAIL_SPAWN_EGG.get()}, event);
         addToTab(
            (Item)AetherItems.VALKYRIE_SPAWN_EGG.get(),
            new Item[]{(Item)DAItems.VENOMITE_SPAWN_EGG.get(), (Item)DAItems.BABY_ZEPHYR_SPAWN_EGG.get(), (Item)DAItems.GENTLE_WIND_SPAWN_EGG.get()},
            event
         );
      }

      if (tab == AetherCreativeTabs.AETHER_DUNGEON_BLOCKS.getKey()) {
         addToTab(
            ((Block)AetherBlocks.TREASURE_DOORWAY_SENTRY_STONE.get()).asItem(),
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
            },
            event
         );
      }
   }

   private static void addToTab(Item parent, Item stack, BuildCreativeModeTabContentsEvent event) {
      event.insertAfter(new ItemStack(parent), new ItemStack(stack), TabVisibility.PARENT_AND_SEARCH_TABS);
   }

   private static void addToTab(Item parent, Item[] stack, BuildCreativeModeTabContentsEvent event) {
      event.insertAfter(new ItemStack(parent), new ItemStack(stack[0]), TabVisibility.PARENT_AND_SEARCH_TABS);

      for (int i = 1; i < stack.length; i++) {
         event.insertAfter(new ItemStack(stack[i - 1]), new ItemStack(stack[i]), TabVisibility.PARENT_AND_SEARCH_TABS);
      }
   }

   private static void addToTab(Item parent, Block[] stack, BuildCreativeModeTabContentsEvent event) {
      event.insertAfter(new ItemStack(parent), new ItemStack(stack[0]), TabVisibility.PARENT_AND_SEARCH_TABS);

      for (int i = 1; i < stack.length; i++) {
         event.insertAfter(new ItemStack(stack[i - 1]), new ItemStack(stack[i]), TabVisibility.PARENT_AND_SEARCH_TABS);
      }
   }

   private static ItemStack getMoaFodderStack(Holder<MobEffect> effect) {
      ItemStack stack = new ItemStack((ItemLike)DAItems.MOA_FODDER.get());
      stack.set(DADataComponentTypes.MOA_FODDER, new MoaFodder(new MobEffectInstance(effect, 14400, 1)));
      return stack;
   }
}
