package com.aetherteam.aether.item;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.block.AetherBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AetherCreativeTabs {
   public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "aether");
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> AETHER_BUILDING_BLOCKS = CREATIVE_MODE_TABS.register(
      "building_blocks",
      () -> CreativeModeTab.builder()
         .withTabsBefore(new ResourceKey[]{CreativeModeTabs.SPAWN_EGGS})
         .withTabsAfter(new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath("aether", "dungeon_blocks")})
         .icon(() -> new ItemStack((ItemLike)AetherBlocks.HOLYSTONE_BRICKS.get()))
         .title(Component.translatable("itemGroup.aether.building_blocks"))
         .displayItems((features, output) -> {
            output.accept((ItemLike)AetherBlocks.SKYROOT_LOG.get());
            output.accept((ItemLike)AetherBlocks.SKYROOT_WOOD.get());
            output.accept((ItemLike)AetherBlocks.STRIPPED_SKYROOT_LOG.get());
            output.accept((ItemLike)AetherBlocks.STRIPPED_SKYROOT_WOOD.get());
            output.accept((ItemLike)AetherBlocks.SKYROOT_PLANKS.get());
            output.accept((ItemLike)AetherBlocks.SKYROOT_STAIRS.get());
            output.accept((ItemLike)AetherBlocks.SKYROOT_SLAB.get());
            output.accept((ItemLike)AetherBlocks.SKYROOT_FENCE.get());
            output.accept((ItemLike)AetherBlocks.SKYROOT_FENCE_GATE.get());
            output.accept((ItemLike)AetherBlocks.SKYROOT_DOOR.get());
            output.accept((ItemLike)AetherBlocks.SKYROOT_TRAPDOOR.get());
            output.accept((ItemLike)AetherBlocks.SKYROOT_PRESSURE_PLATE.get());
            output.accept((ItemLike)AetherBlocks.SKYROOT_BUTTON.get());
            output.accept((ItemLike)AetherBlocks.GOLDEN_OAK_LOG.get());
            output.accept((ItemLike)AetherBlocks.GOLDEN_OAK_WOOD.get());
            output.accept((ItemLike)AetherBlocks.QUICKSOIL_GLASS.get());
            output.accept((ItemLike)AetherBlocks.QUICKSOIL_GLASS_PANE.get());
            output.accept((ItemLike)AetherBlocks.HOLYSTONE.get());
            output.accept((ItemLike)AetherBlocks.HOLYSTONE_STAIRS.get());
            output.accept((ItemLike)AetherBlocks.HOLYSTONE_SLAB.get());
            output.accept((ItemLike)AetherBlocks.HOLYSTONE_WALL.get());
            output.accept((ItemLike)AetherBlocks.HOLYSTONE_PRESSURE_PLATE.get());
            output.accept((ItemLike)AetherBlocks.HOLYSTONE_BUTTON.get());
            output.accept((ItemLike)AetherBlocks.MOSSY_HOLYSTONE.get());
            output.accept((ItemLike)AetherBlocks.MOSSY_HOLYSTONE_STAIRS.get());
            output.accept((ItemLike)AetherBlocks.MOSSY_HOLYSTONE_SLAB.get());
            output.accept((ItemLike)AetherBlocks.MOSSY_HOLYSTONE_WALL.get());
            output.accept((ItemLike)AetherBlocks.HOLYSTONE_BRICKS.get());
            output.accept((ItemLike)AetherBlocks.HOLYSTONE_BRICK_STAIRS.get());
            output.accept((ItemLike)AetherBlocks.HOLYSTONE_BRICK_SLAB.get());
            output.accept((ItemLike)AetherBlocks.HOLYSTONE_BRICK_WALL.get());
            output.accept((ItemLike)AetherBlocks.ICESTONE.get());
            output.accept((ItemLike)AetherBlocks.ICESTONE_STAIRS.get());
            output.accept((ItemLike)AetherBlocks.ICESTONE_SLAB.get());
            output.accept((ItemLike)AetherBlocks.ICESTONE_WALL.get());
            output.accept((ItemLike)AetherBlocks.AMBROSIUM_BLOCK.get());
            output.accept((ItemLike)AetherBlocks.ZANITE_BLOCK.get());
            output.accept((ItemLike)AetherBlocks.ENCHANTED_GRAVITITE.get());
            output.accept((ItemLike)AetherBlocks.AEROGEL.get());
            output.accept((ItemLike)AetherBlocks.AEROGEL_STAIRS.get());
            output.accept((ItemLike)AetherBlocks.AEROGEL_SLAB.get());
            output.accept((ItemLike)AetherBlocks.AEROGEL_WALL.get());
         })
         .build()
   );
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> AETHER_DUNGEON_BLOCKS = CREATIVE_MODE_TABS.register(
      "dungeon_blocks",
      () -> CreativeModeTab.builder()
         .withTabsBefore(new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath("aether", "building_blocks")})
         .withTabsAfter(new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath("aether", "natural_blocks")})
         .icon(() -> new ItemStack((ItemLike)AetherBlocks.LIGHT_ANGELIC_STONE.get()))
         .title(Component.translatable("itemGroup.aether.dungeon_blocks"))
         .displayItems((features, output) -> {
            output.accept((ItemLike)AetherBlocks.CARVED_STONE.get());
            output.accept((ItemLike)AetherBlocks.LOCKED_CARVED_STONE.get());
            output.accept((ItemLike)AetherBlocks.TRAPPED_CARVED_STONE.get());
            output.accept((ItemLike)AetherBlocks.BOSS_DOORWAY_CARVED_STONE.get());
            output.accept((ItemLike)AetherBlocks.TREASURE_DOORWAY_CARVED_STONE.get());
            output.accept((ItemLike)AetherBlocks.CARVED_STAIRS.get());
            output.accept((ItemLike)AetherBlocks.CARVED_SLAB.get());
            output.accept((ItemLike)AetherBlocks.CARVED_WALL.get());
            output.accept((ItemLike)AetherBlocks.SENTRY_STONE.get());
            output.accept((ItemLike)AetherBlocks.LOCKED_SENTRY_STONE.get());
            output.accept((ItemLike)AetherBlocks.TRAPPED_SENTRY_STONE.get());
            output.accept((ItemLike)AetherBlocks.BOSS_DOORWAY_SENTRY_STONE.get());
            output.accept((ItemLike)AetherBlocks.TREASURE_DOORWAY_SENTRY_STONE.get());
            output.accept((ItemLike)AetherBlocks.ANGELIC_STONE.get());
            output.accept((ItemLike)AetherBlocks.LOCKED_ANGELIC_STONE.get());
            output.accept((ItemLike)AetherBlocks.TRAPPED_ANGELIC_STONE.get());
            output.accept((ItemLike)AetherBlocks.BOSS_DOORWAY_ANGELIC_STONE.get());
            output.accept((ItemLike)AetherBlocks.TREASURE_DOORWAY_ANGELIC_STONE.get());
            output.accept((ItemLike)AetherBlocks.ANGELIC_STAIRS.get());
            output.accept((ItemLike)AetherBlocks.ANGELIC_SLAB.get());
            output.accept((ItemLike)AetherBlocks.ANGELIC_WALL.get());
            output.accept((ItemLike)AetherBlocks.LIGHT_ANGELIC_STONE.get());
            output.accept((ItemLike)AetherBlocks.LOCKED_LIGHT_ANGELIC_STONE.get());
            output.accept((ItemLike)AetherBlocks.TRAPPED_LIGHT_ANGELIC_STONE.get());
            output.accept((ItemLike)AetherBlocks.BOSS_DOORWAY_LIGHT_ANGELIC_STONE.get());
            output.accept((ItemLike)AetherBlocks.TREASURE_DOORWAY_LIGHT_ANGELIC_STONE.get());
            output.accept((ItemLike)AetherBlocks.PILLAR.get());
            output.accept((ItemLike)AetherBlocks.PILLAR_TOP.get());
            output.accept((ItemLike)AetherBlocks.HELLFIRE_STONE.get());
            output.accept((ItemLike)AetherBlocks.LOCKED_HELLFIRE_STONE.get());
            output.accept((ItemLike)AetherBlocks.TRAPPED_HELLFIRE_STONE.get());
            output.accept((ItemLike)AetherBlocks.BOSS_DOORWAY_HELLFIRE_STONE.get());
            output.accept((ItemLike)AetherBlocks.TREASURE_DOORWAY_HELLFIRE_STONE.get());
            output.accept((ItemLike)AetherBlocks.HELLFIRE_STAIRS.get());
            output.accept((ItemLike)AetherBlocks.HELLFIRE_SLAB.get());
            output.accept((ItemLike)AetherBlocks.HELLFIRE_WALL.get());
            output.accept((ItemLike)AetherBlocks.LIGHT_HELLFIRE_STONE.get());
            output.accept((ItemLike)AetherBlocks.LOCKED_LIGHT_HELLFIRE_STONE.get());
            output.accept((ItemLike)AetherBlocks.TRAPPED_LIGHT_HELLFIRE_STONE.get());
            output.accept((ItemLike)AetherBlocks.BOSS_DOORWAY_LIGHT_HELLFIRE_STONE.get());
            output.accept((ItemLike)AetherBlocks.TREASURE_DOORWAY_LIGHT_HELLFIRE_STONE.get());
            output.accept((ItemLike)AetherBlocks.TREASURE_CHEST.get());
            output.accept((ItemLike)AetherBlocks.CHEST_MIMIC.get());
         })
         .build()
   );
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> AETHER_NATURAL_BLOCKS = CREATIVE_MODE_TABS.register(
      "natural_blocks",
      () -> CreativeModeTab.builder()
         .withTabsBefore(new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath("aether", "dungeon_blocks")})
         .withTabsAfter(new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath("aether", "functional_blocks")})
         .icon(() -> new ItemStack((ItemLike)AetherBlocks.AETHER_GRASS_BLOCK.get()))
         .title(Component.translatable("itemGroup.aether.natural_blocks"))
         .displayItems((features, output) -> {
            output.accept((ItemLike)AetherBlocks.AETHER_GRASS_BLOCK.get());
            output.accept((ItemLike)AetherBlocks.ENCHANTED_AETHER_GRASS_BLOCK.get());
            output.accept((ItemLike)AetherBlocks.AETHER_DIRT_PATH.get());
            output.accept((ItemLike)AetherBlocks.AETHER_DIRT.get());
            output.accept((ItemLike)AetherBlocks.AETHER_FARMLAND.get());
            output.accept((ItemLike)AetherBlocks.QUICKSOIL.get());
            output.accept((ItemLike)AetherBlocks.HOLYSTONE.get());
            output.accept((ItemLike)AetherBlocks.MOSSY_HOLYSTONE.get());
            output.accept((ItemLike)AetherBlocks.ICESTONE.get());
            output.accept((ItemLike)AetherBlocks.AMBROSIUM_ORE.get());
            output.accept((ItemLike)AetherBlocks.ZANITE_ORE.get());
            output.accept((ItemLike)AetherBlocks.GRAVITITE_ORE.get());
            output.accept((ItemLike)AetherBlocks.SKYROOT_LOG.get());
            output.accept((ItemLike)AetherBlocks.GOLDEN_OAK_LOG.get());
            output.accept((ItemLike)AetherBlocks.SKYROOT_LEAVES.get());
            output.accept((ItemLike)AetherBlocks.GOLDEN_OAK_LEAVES.get());
            output.accept((ItemLike)AetherBlocks.CRYSTAL_LEAVES.get());
            output.accept((ItemLike)AetherBlocks.CRYSTAL_FRUIT_LEAVES.get());
            output.accept((ItemLike)AetherBlocks.HOLIDAY_LEAVES.get());
            output.accept((ItemLike)AetherBlocks.DECORATED_HOLIDAY_LEAVES.get());
            output.accept((ItemLike)AetherBlocks.SKYROOT_SAPLING.get());
            output.accept((ItemLike)AetherBlocks.GOLDEN_OAK_SAPLING.get());
            output.accept((ItemLike)AetherBlocks.BERRY_BUSH_STEM.get());
            output.accept((ItemLike)AetherBlocks.BERRY_BUSH.get());
            output.accept((ItemLike)AetherBlocks.PURPLE_FLOWER.get());
            output.accept((ItemLike)AetherBlocks.WHITE_FLOWER.get());
            output.accept((ItemLike)AetherBlocks.COLD_AERCLOUD.get());
            output.accept((ItemLike)AetherBlocks.BLUE_AERCLOUD.get());
            output.accept((ItemLike)AetherBlocks.GOLDEN_AERCLOUD.get());
            output.accept((ItemLike)AetherBlocks.PRESENT.get());
         })
         .build()
   );
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> AETHER_FUNCTIONAL_BLOCKS = CREATIVE_MODE_TABS.register(
      "functional_blocks",
      () -> CreativeModeTab.builder()
         .withTabsBefore(new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath("aether", "natural_blocks")})
         .withTabsAfter(new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath("aether", "redstone_blocks")})
         .icon(() -> new ItemStack((ItemLike)AetherBlocks.SKYROOT_SIGN.get()))
         .title(Component.translatable("itemGroup.aether.functional_blocks"))
         .displayItems((features, output) -> {
            output.accept((ItemLike)AetherBlocks.AMBROSIUM_TORCH.get());
            output.accept((ItemLike)AetherBlocks.ALTAR.get());
            output.accept((ItemLike)AetherBlocks.FREEZER.get());
            output.accept((ItemLike)AetherBlocks.INCUBATOR.get());
            output.accept((ItemLike)AetherBlocks.SUN_ALTAR.get());
            output.accept((ItemLike)AetherBlocks.SKYROOT_BOOKSHELF.get());
            output.accept((ItemLike)AetherBlocks.SKYROOT_SIGN.get());
            output.accept((ItemLike)AetherBlocks.SKYROOT_HANGING_SIGN.get());
            output.accept((ItemLike)AetherBlocks.SKYROOT_BED.get());
            output.accept((ItemLike)AetherBlocks.TREASURE_CHEST.get());
            output.accept((ItemLike)AetherBlocks.CHEST_MIMIC.get());
            output.accept((ItemLike)AetherBlocks.PRESENT.get());
            output.accept(AetherItems.createSwetBannerItemStack(features.holders().lookupOrThrow(Registries.BANNER_PATTERN)));
            output.accept((ItemLike)AetherItems.AETHER_PORTAL_FRAME.get());
         })
         .build()
   );
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> AETHER_REDSTONE_BLOCKS = CREATIVE_MODE_TABS.register(
      "redstone_blocks",
      () -> CreativeModeTab.builder()
         .withTabsBefore(new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath("aether", "functional_blocks")})
         .withTabsAfter(new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath("aether", "tools_and_utilities")})
         .icon(() -> new ItemStack((ItemLike)AetherBlocks.SKYROOT_FENCE_GATE.get()))
         .title(Component.translatable("itemGroup.aether.redstone_blocks"))
         .displayItems((features, output) -> {
            output.accept((ItemLike)AetherBlocks.SKYROOT_BUTTON.get());
            output.accept((ItemLike)AetherBlocks.HOLYSTONE_BUTTON.get());
            output.accept((ItemLike)AetherBlocks.SKYROOT_PRESSURE_PLATE.get());
            output.accept((ItemLike)AetherBlocks.HOLYSTONE_PRESSURE_PLATE.get());
            output.accept((ItemLike)AetherBlocks.ALTAR.get());
            output.accept((ItemLike)AetherBlocks.FREEZER.get());
            output.accept((ItemLike)AetherBlocks.INCUBATOR.get());
            output.accept((ItemLike)AetherBlocks.TREASURE_CHEST.get());
            output.accept((ItemLike)AetherItems.SKYROOT_CHEST_BOAT.get());
            output.accept((ItemLike)AetherBlocks.SKYROOT_DOOR.get());
            output.accept((ItemLike)AetherBlocks.SKYROOT_FENCE_GATE.get());
            output.accept((ItemLike)AetherBlocks.SKYROOT_TRAPDOOR.get());
            output.accept((ItemLike)AetherBlocks.ENCHANTED_GRAVITITE.get());
         })
         .build()
   );
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> AETHER_EQUIPMENT_AND_UTILITIES = CREATIVE_MODE_TABS.register(
      "equipment_and_utilities",
      () -> CreativeModeTab.builder()
         .withTabsBefore(new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath("aether", "redstone_blocks")})
         .withTabsAfter(new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath("aether", "armor_and_accessories")})
         .icon(() -> new ItemStack((ItemLike)AetherItems.GRAVITITE_PICKAXE.get()))
         .title(Component.translatable("itemGroup.aether.equipment_and_utilities"))
         .displayItems((features, output) -> {
            output.accept((ItemLike)AetherItems.SKYROOT_SWORD.get());
            output.accept((ItemLike)AetherItems.SKYROOT_SHOVEL.get());
            output.accept((ItemLike)AetherItems.SKYROOT_PICKAXE.get());
            output.accept((ItemLike)AetherItems.SKYROOT_AXE.get());
            output.accept((ItemLike)AetherItems.SKYROOT_HOE.get());
            output.accept((ItemLike)AetherItems.HOLYSTONE_SWORD.get());
            output.accept((ItemLike)AetherItems.HOLYSTONE_SHOVEL.get());
            output.accept((ItemLike)AetherItems.HOLYSTONE_PICKAXE.get());
            output.accept((ItemLike)AetherItems.HOLYSTONE_AXE.get());
            output.accept((ItemLike)AetherItems.HOLYSTONE_HOE.get());
            output.accept((ItemLike)AetherItems.ZANITE_SWORD.get());
            output.accept((ItemLike)AetherItems.ZANITE_SHOVEL.get());
            output.accept((ItemLike)AetherItems.ZANITE_PICKAXE.get());
            output.accept((ItemLike)AetherItems.ZANITE_AXE.get());
            output.accept((ItemLike)AetherItems.ZANITE_HOE.get());
            output.accept((ItemLike)AetherItems.GRAVITITE_SWORD.get());
            output.accept((ItemLike)AetherItems.GRAVITITE_SHOVEL.get());
            output.accept((ItemLike)AetherItems.GRAVITITE_PICKAXE.get());
            output.accept((ItemLike)AetherItems.GRAVITITE_AXE.get());
            output.accept((ItemLike)AetherItems.GRAVITITE_HOE.get());
            output.accept((ItemLike)AetherItems.VALKYRIE_LANCE.get());
            output.accept((ItemLike)AetherItems.VALKYRIE_SHOVEL.get());
            output.accept((ItemLike)AetherItems.VALKYRIE_PICKAXE.get());
            output.accept((ItemLike)AetherItems.VALKYRIE_AXE.get());
            output.accept((ItemLike)AetherItems.VALKYRIE_HOE.get());
            output.accept((ItemLike)AetherItems.GOLDEN_DART_SHOOTER.get());
            output.accept((ItemLike)AetherItems.GOLDEN_DART.get());
            output.accept((ItemLike)AetherItems.POISON_DART_SHOOTER.get());
            output.accept((ItemLike)AetherItems.POISON_DART.get());
            output.accept((ItemLike)AetherItems.ENCHANTED_DART_SHOOTER.get());
            output.accept((ItemLike)AetherItems.ENCHANTED_DART.get());
            output.accept((ItemLike)AetherItems.CANDY_CANE_SWORD.get());
            output.accept((ItemLike)AetherItems.HOLY_SWORD.get());
            output.accept((ItemLike)AetherItems.VAMPIRE_BLADE.get());
            output.accept((ItemLike)AetherItems.LIGHTNING_SWORD.get());
            output.accept((ItemLike)AetherItems.LIGHTNING_KNIFE.get());
            output.accept((ItemLike)AetherItems.FLAMING_SWORD.get());
            output.accept((ItemLike)AetherItems.PHOENIX_BOW.get());
            output.accept((ItemLike)AetherItems.PIG_SLAYER.get());
            output.accept((ItemLike)AetherItems.HAMMER_OF_KINGBDOGZ.get());
            output.accept((ItemLike)AetherItems.CLOUD_STAFF.get());
            output.accept((ItemLike)AetherItems.SKYROOT_BUCKET.get());
            output.accept((ItemLike)AetherItems.SKYROOT_WATER_BUCKET.get());
            output.accept((ItemLike)AetherItems.SKYROOT_PUFFERFISH_BUCKET.get());
            output.accept((ItemLike)AetherItems.SKYROOT_SALMON_BUCKET.get());
            output.accept((ItemLike)AetherItems.SKYROOT_COD_BUCKET.get());
            output.accept((ItemLike)AetherItems.SKYROOT_TROPICAL_FISH_BUCKET.get());
            output.accept((ItemLike)AetherItems.SKYROOT_AXOLOTL_BUCKET.get());
            output.accept((ItemLike)AetherItems.SKYROOT_TADPOLE_BUCKET.get());
            output.accept((ItemLike)AetherItems.SKYROOT_POWDER_SNOW_BUCKET.get());
            output.accept((ItemLike)AetherItems.SKYROOT_MILK_BUCKET.get());
            output.accept((ItemLike)AetherItems.SKYROOT_REMEDY_BUCKET.get());
            output.accept((ItemLike)AetherItems.SKYROOT_POISON_BUCKET.get());
            output.accept((ItemLike)AetherItems.BOOK_OF_LORE.get());
            output.accept((ItemLike)AetherItems.COLD_PARACHUTE.get());
            output.accept((ItemLike)AetherItems.GOLDEN_PARACHUTE.get());
            output.accept((ItemLike)AetherItems.AMBROSIUM_SHARD.get());
            output.accept((ItemLike)AetherItems.SWET_BALL.get());
            output.accept((ItemLike)AetherItems.BLUE_MOA_EGG.get());
            output.accept((ItemLike)AetherItems.WHITE_MOA_EGG.get());
            output.accept((ItemLike)AetherItems.BLACK_MOA_EGG.get());
            output.accept((ItemLike)AetherItems.NATURE_STAFF.get());
            output.accept((ItemLike)AetherItems.SKYROOT_BOAT.get());
            output.accept((ItemLike)AetherItems.SKYROOT_CHEST_BOAT.get());
            output.accept((ItemLike)AetherItems.BRONZE_DUNGEON_KEY.get());
            output.accept((ItemLike)AetherItems.SILVER_DUNGEON_KEY.get());
            output.accept((ItemLike)AetherItems.GOLD_DUNGEON_KEY.get());
            output.accept((ItemLike)AetherItems.VICTORY_MEDAL.get());
            output.accept((ItemLike)AetherItems.MUSIC_DISC_AETHER_TUNE.get());
            output.accept((ItemLike)AetherItems.MUSIC_DISC_ASCENDING_DAWN.get());
            output.accept((ItemLike)AetherItems.MUSIC_DISC_SLIDERS_WRATH.get());
         })
         .build()
   );
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> AETHER_ARMOR_AND_ACCESSORIES = CREATIVE_MODE_TABS.register(
      "armor_and_accessories",
      () -> CreativeModeTab.builder()
         .withTabsBefore(new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath("aether", "equipment_and_utilities")})
         .withTabsAfter(new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath("aether", "food_and_drinks")})
         .icon(() -> new ItemStack((ItemLike)AetherItems.VALKYRIE_CHESTPLATE.get()))
         .title(Component.translatable("itemGroup.aether.armor_and_accessories"))
         .displayItems((features, output) -> {
            output.accept((ItemLike)AetherItems.ZANITE_HELMET.get());
            output.accept((ItemLike)AetherItems.ZANITE_CHESTPLATE.get());
            output.accept((ItemLike)AetherItems.ZANITE_LEGGINGS.get());
            output.accept((ItemLike)AetherItems.ZANITE_BOOTS.get());
            output.accept((ItemLike)AetherItems.ZANITE_GLOVES.get());
            output.accept((ItemLike)AetherItems.GRAVITITE_HELMET.get());
            output.accept((ItemLike)AetherItems.GRAVITITE_CHESTPLATE.get());
            output.accept((ItemLike)AetherItems.GRAVITITE_LEGGINGS.get());
            output.accept((ItemLike)AetherItems.GRAVITITE_BOOTS.get());
            output.accept((ItemLike)AetherItems.GRAVITITE_GLOVES.get());
            output.accept((ItemLike)AetherItems.NEPTUNE_HELMET.get());
            output.accept((ItemLike)AetherItems.NEPTUNE_CHESTPLATE.get());
            output.accept((ItemLike)AetherItems.NEPTUNE_LEGGINGS.get());
            output.accept((ItemLike)AetherItems.NEPTUNE_BOOTS.get());
            output.accept((ItemLike)AetherItems.NEPTUNE_GLOVES.get());
            output.accept((ItemLike)AetherItems.VALKYRIE_HELMET.get());
            output.accept((ItemLike)AetherItems.VALKYRIE_CHESTPLATE.get());
            output.accept((ItemLike)AetherItems.VALKYRIE_LEGGINGS.get());
            output.accept((ItemLike)AetherItems.VALKYRIE_BOOTS.get());
            output.accept((ItemLike)AetherItems.VALKYRIE_GLOVES.get());
            output.accept((ItemLike)AetherItems.PHOENIX_HELMET.get());
            output.accept((ItemLike)AetherItems.PHOENIX_CHESTPLATE.get());
            output.accept((ItemLike)AetherItems.PHOENIX_LEGGINGS.get());
            output.accept((ItemLike)AetherItems.PHOENIX_BOOTS.get());
            output.accept((ItemLike)AetherItems.PHOENIX_GLOVES.get());
            output.accept((ItemLike)AetherItems.OBSIDIAN_HELMET.get());
            output.accept((ItemLike)AetherItems.OBSIDIAN_CHESTPLATE.get());
            output.accept((ItemLike)AetherItems.OBSIDIAN_LEGGINGS.get());
            output.accept((ItemLike)AetherItems.OBSIDIAN_BOOTS.get());
            output.accept((ItemLike)AetherItems.OBSIDIAN_GLOVES.get());
            output.accept((ItemLike)AetherItems.SENTRY_BOOTS.get());
            output.accept((ItemLike)AetherItems.IRON_RING.get());
            output.accept((ItemLike)AetherItems.IRON_PENDANT.get());
            output.accept((ItemLike)AetherItems.GOLDEN_RING.get());
            output.accept((ItemLike)AetherItems.GOLDEN_PENDANT.get());
            output.accept((ItemLike)AetherItems.ZANITE_RING.get());
            output.accept((ItemLike)AetherItems.ZANITE_PENDANT.get());
            output.accept((ItemLike)AetherItems.ICE_RING.get());
            output.accept((ItemLike)AetherItems.ICE_PENDANT.get());
            output.accept((ItemLike)AetherItems.WHITE_CAPE.get());
            output.accept((ItemLike)AetherItems.YELLOW_CAPE.get());
            output.accept((ItemLike)AetherItems.RED_CAPE.get());
            output.accept((ItemLike)AetherItems.BLUE_CAPE.get());
            output.accept((ItemLike)AetherItems.AGILITY_CAPE.get());
            output.accept((ItemLike)AetherItems.SWET_CAPE.get());
            output.accept((ItemLike)AetherItems.INVISIBILITY_CLOAK.get());
            if ((Boolean)AetherConfig.SERVER.spawn_valkyrie_cape.get()) {
               output.accept((ItemLike)AetherItems.VALKYRIE_CAPE.get());
            }

            if ((Boolean)AetherConfig.SERVER.spawn_golden_feather.get()) {
               output.accept((ItemLike)AetherItems.GOLDEN_FEATHER.get());
            }

            output.accept((ItemLike)AetherItems.REGENERATION_STONE.get());
            output.accept((ItemLike)AetherItems.IRON_BUBBLE.get());
            output.accept((ItemLike)AetherItems.SHIELD_OF_REPULSION.get());
         })
         .build()
   );
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> AETHER_FOOD_AND_DRINKS = CREATIVE_MODE_TABS.register(
      "food_and_drinks",
      () -> CreativeModeTab.builder()
         .withTabsBefore(new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath("aether", "combat")})
         .withTabsAfter(new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath("aether", "ingredients")})
         .icon(() -> new ItemStack((ItemLike)AetherItems.BLUE_GUMMY_SWET.get()))
         .title(Component.translatable("itemGroup.aether.food_and_drinks"))
         .displayItems((features, output) -> {
            output.accept((ItemLike)AetherItems.BLUE_BERRY.get());
            output.accept((ItemLike)AetherItems.ENCHANTED_BERRY.get());
            output.accept((ItemLike)AetherItems.WHITE_APPLE.get());
            if ((Boolean)AetherConfig.SERVER.edible_ambrosium.get()) {
               output.accept((ItemLike)AetherItems.AMBROSIUM_SHARD.get());
            }

            output.accept((ItemLike)AetherItems.HEALING_STONE.get());
            output.accept((ItemLike)AetherItems.BLUE_GUMMY_SWET.get());
            output.accept((ItemLike)AetherItems.GOLDEN_GUMMY_SWET.get());
            output.accept((ItemLike)AetherItems.GINGERBREAD_MAN.get());
            output.accept((ItemLike)AetherItems.CANDY_CANE.get());
            output.accept((ItemLike)AetherItems.SKYROOT_MILK_BUCKET.get());
            output.accept((ItemLike)AetherItems.SKYROOT_REMEDY_BUCKET.get());
            output.accept((ItemLike)AetherItems.SKYROOT_POISON_BUCKET.get());
            output.accept((ItemLike)AetherItems.LIFE_SHARD.get());
         })
         .build()
   );
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> AETHER_INGREDIENTS = CREATIVE_MODE_TABS.register(
      "ingredients",
      () -> CreativeModeTab.builder()
         .withTabsBefore(new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath("aether", "food_and_drinks")})
         .withTabsAfter(new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath("aether", "spawn_eggs")})
         .icon(() -> new ItemStack((ItemLike)AetherItems.AMBROSIUM_SHARD.get()))
         .title(Component.translatable("itemGroup.aether.ingredients"))
         .displayItems((features, output) -> {
            output.accept((ItemLike)AetherItems.AMBROSIUM_SHARD.get());
            output.accept((ItemLike)AetherItems.ZANITE_GEMSTONE.get());
            output.accept((ItemLike)AetherBlocks.ENCHANTED_GRAVITITE.get());
            output.accept((ItemLike)AetherItems.SKYROOT_STICK.get());
            output.accept((ItemLike)AetherItems.GOLDEN_AMBER.get());
            output.accept((ItemLike)AetherItems.AECHOR_PETAL.get());
            output.accept((ItemLike)AetherItems.SKYROOT_POISON_BUCKET.get());
            output.accept((ItemLike)AetherItems.SWET_BALL.get());
         })
         .build()
   );
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> AETHER_SPAWN_EGGS = CREATIVE_MODE_TABS.register(
      "spawn_eggs",
      () -> CreativeModeTab.builder()
         .withTabsBefore(new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath("aether", "ingredients")})
         .icon(() -> new ItemStack((ItemLike)AetherItems.AERBUNNY_SPAWN_EGG.get()))
         .title(Component.translatable("itemGroup.aether.spawn_eggs"))
         .displayItems((features, output) -> {
            output.accept((ItemLike)AetherItems.BLUE_MOA_EGG.get());
            output.accept((ItemLike)AetherItems.WHITE_MOA_EGG.get());
            output.accept((ItemLike)AetherItems.BLACK_MOA_EGG.get());
            output.accept((ItemLike)AetherItems.AECHOR_PLANT_SPAWN_EGG.get());
            output.accept((ItemLike)AetherItems.AERBUNNY_SPAWN_EGG.get());
            output.accept((ItemLike)AetherItems.AERWHALE_SPAWN_EGG.get());
            output.accept((ItemLike)AetherItems.BLUE_SWET_SPAWN_EGG.get());
            output.accept((ItemLike)AetherItems.COCKATRICE_SPAWN_EGG.get());
            output.accept((ItemLike)AetherItems.EVIL_WHIRLWIND_SPAWN_EGG.get());
            output.accept((ItemLike)AetherItems.FIRE_MINION_SPAWN_EGG.get());
            output.accept((ItemLike)AetherItems.FLYING_COW_SPAWN_EGG.get());
            output.accept((ItemLike)AetherItems.GOLDEN_SWET_SPAWN_EGG.get());
            output.accept((ItemLike)AetherItems.MIMIC_SPAWN_EGG.get());
            output.accept((ItemLike)AetherItems.MOA_SPAWN_EGG.get());
            output.accept((ItemLike)AetherItems.PHYG_SPAWN_EGG.get());
            output.accept((ItemLike)AetherItems.SENTRY_SPAWN_EGG.get());
            output.accept((ItemLike)AetherItems.SHEEPUFF_SPAWN_EGG.get());
            output.accept((ItemLike)AetherItems.WHIRLWIND_SPAWN_EGG.get());
            output.accept((ItemLike)AetherItems.VALKYRIE_SPAWN_EGG.get());
            output.accept((ItemLike)AetherItems.ZEPHYR_SPAWN_EGG.get());
         })
         .build()
   );

   public static void buildCreativeModeTabs(BuildCreativeModeTabContentsEvent event) {
      ResourceKey<CreativeModeTab> tab = event.getTabKey();
      if (tab == CreativeModeTabs.COMBAT) {
         event.insertAfter(new ItemStack(Items.LEATHER_BOOTS), new ItemStack((ItemLike)AetherItems.LEATHER_GLOVES.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
         event.insertAfter(
            new ItemStack(Items.CHAINMAIL_BOOTS), new ItemStack((ItemLike)AetherItems.CHAINMAIL_GLOVES.get()), TabVisibility.PARENT_AND_SEARCH_TABS
         );
         event.insertAfter(new ItemStack(Items.IRON_BOOTS), new ItemStack((ItemLike)AetherItems.IRON_GLOVES.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
         event.insertAfter(new ItemStack(Items.GOLDEN_BOOTS), new ItemStack((ItemLike)AetherItems.GOLDEN_GLOVES.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
         event.insertAfter(new ItemStack(Items.DIAMOND_BOOTS), new ItemStack((ItemLike)AetherItems.DIAMOND_GLOVES.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
         event.insertAfter(
            new ItemStack(Items.NETHERITE_BOOTS), new ItemStack((ItemLike)AetherItems.NETHERITE_GLOVES.get()), TabVisibility.PARENT_AND_SEARCH_TABS
         );
      }
   }
}
