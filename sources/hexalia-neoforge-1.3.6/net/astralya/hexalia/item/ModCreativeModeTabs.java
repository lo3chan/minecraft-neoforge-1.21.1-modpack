package net.astralya.hexalia.item;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.astralya.hexalia.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.level.ItemLike;

public final class ModCreativeModeTabs {
   public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create("hexalia", Registries.CREATIVE_MODE_TAB);
   public static final RegistrySupplier<CreativeModeTab> HEXALIA_TAB = TABS.register(
      "main",
      () -> CreativeTabRegistry.create(
         builder -> builder.title(Component.translatable("itemGroup.hexalia"))
            .icon(() -> new ItemStack((ItemLike)ModItems.HEX_FOCUS.get()))
            .displayItems((parameters, output) -> {
               acceptHerbsAndFlora(output);
               acceptProcessedIngredients(output);
               acceptSeedsAndCrops(output);
               acceptFood(output);
               acceptBrewsAndAlchemy(output);
               acceptMagicComponents(output);
               acceptToolsAndRelics(output);
               acceptWearables(output);
               acceptFunctionalBlocks(output);
               acceptDecor(output);
               acceptRareItems(output);
               acceptCottonwood(output);
               acceptWillow(output);
               acceptSpawnEggs(output);
            })
      )
   );

   private ModCreativeModeTabs() {
   }

   private static void acceptHerbsAndFlora(Output output) {
      output.accept((ItemLike)ModBlocks.SPIRIT_BLOOM.get());
      output.accept((ItemLike)ModBlocks.DREAMSHROOM.get());
      output.accept((ItemLike)ModItems.SIREN_KELP.get());
      output.accept((ItemLike)ModBlocks.GHOST_FERN.get());
      output.accept((ItemLike)ModBlocks.CELESTIAL_BLOOM.get());
      output.accept((ItemLike)ModItems.LOTUS_FLOWER.get());
      output.accept((ItemLike)ModItems.LOTUS_BLOSSOM.get());
      output.accept((ItemLike)ModBlocks.WITCHWEED.get());
      output.accept((ItemLike)ModBlocks.MORPHORA.get());
      output.accept((ItemLike)ModBlocks.GRIMSHADE.get());
      output.accept((ItemLike)ModItems.NAUTILITE.get());
      output.accept((ItemLike)ModBlocks.WINDSONG.get());
      output.accept((ItemLike)ModBlocks.ASTRYLIS.get());
      output.accept((ItemLike)ModBlocks.LOURDES.get());
      output.accept((ItemLike)ModBlocks.AEGIFLORA.get());
      output.accept((ItemLike)ModBlocks.BEGONIA.get());
      output.accept((ItemLike)ModBlocks.LAVENDER.get());
      output.accept((ItemLike)ModBlocks.DAHLIA.get());
      output.accept((ItemLike)ModBlocks.PALE_MUSHROOM.get());
      output.accept((ItemLike)ModBlocks.NIGHTSHADE_BUSH.get());
   }

   private static void acceptProcessedIngredients(Output output) {
      output.accept((ItemLike)ModItems.SPIRIT_POWDER.get());
      output.accept((ItemLike)ModItems.DREAM_PASTE.get());
      output.accept((ItemLike)ModItems.SIREN_PASTE.get());
      output.accept((ItemLike)ModItems.GHOST_POWDER.get());
      output.accept((ItemLike)ModItems.FRAGRANT_NECTAR.get());
   }

   private static void acceptSeedsAndCrops(Output output) {
      output.accept((ItemLike)ModItems.MANDRAKE_SEEDS.get());
      output.accept((ItemLike)ModItems.SUNFIRE_TOMATO_SEEDS.get());
      output.accept((ItemLike)ModItems.RABBAGE_SEEDS.get());
      output.accept((ItemLike)ModItems.MANDRAKE.get());
      output.accept((ItemLike)ModItems.SUNFIRE_TOMATO.get());
      output.accept((ItemLike)ModItems.CHILLBERRIES.get());
      output.accept((ItemLike)ModItems.RABBAGE.get());
      output.accept((ItemLike)ModItems.SALTSPROUT.get());
      output.accept((ItemLike)ModItems.GALEBERRIES.get());
   }

   private static void acceptFood(Output output) {
      output.accept((ItemLike)ModItems.MANDRAKE_STEW.get());
      output.accept((ItemLike)ModItems.SPICY_SANDWICH.get());
      output.accept((ItemLike)ModItems.CHILLBERRY_PIE.get());
      output.accept((ItemLike)ModItems.GALEBERRIES_COOKIE.get());
   }

   private static void acceptBrewsAndAlchemy(Output output) {
      output.accept((ItemLike)ModItems.RUSTIC_BOTTLE.get());
      output.accept((ItemLike)ModItems.BREW_OF_SPIKESKIN.get());
      output.accept((ItemLike)ModItems.BREW_OF_BLOODLUST.get());
      output.accept((ItemLike)ModItems.BREW_OF_SLIMEWALKER.get());
      output.accept((ItemLike)ModItems.BREW_OF_HOMESTEAD.get());
      output.accept((ItemLike)ModItems.BREW_OF_SIPHON.get());
      output.accept((ItemLike)ModItems.BREW_OF_DAYBLOOM.get());
      output.accept((ItemLike)ModItems.BREW_OF_ARACHNID_GRACE.get());
      output.accept((ItemLike)ModItems.BREW_OF_HOLLOW_SILENCE.get());
      output.accept((ItemLike)ModItems.BRAMBLEGUARD_SALVE.get());
      output.accept((ItemLike)ModItems.MENDERS_SALVE.get());
      output.accept((ItemLike)ModItems.SALT.get());
      output.accept((ItemLike)ModBlocks.SALT_BLOCK.get());
      output.accept((ItemLike)ModItems.PURIFYING_SAC.get());
      output.accept((ItemLike)ModItems.FOUL_SAC.get());
      output.accept((ItemLike)ModItems.FROST_SAC.get());
      output.accept((ItemLike)ModItems.SEARING_SAC.get());
   }

   private static void acceptMagicComponents(Output output) {
      output.accept((ItemLike)ModItems.FIRE_NODE.get());
      output.accept((ItemLike)ModItems.WATER_NODE.get());
      output.accept((ItemLike)ModItems.AIR_NODE.get());
      output.accept((ItemLike)ModItems.EARTH_NODE.get());
      output.accept((ItemLike)ModItems.TREE_RESIN.get());
      output.accept((ItemLike)ModItems.CELESTIAL_CRYSTAL.get());
      output.accept((ItemLike)ModBlocks.CELESTIAL_CRYSTAL_BLOCK.get());
      output.accept((ItemLike)ModItems.SILK_FIBER.get());
      output.accept((ItemLike)ModItems.SILKWORM.get());
   }

   private static void acceptToolsAndRelics(Output output) {
      output.accept((ItemLike)ModItems.MORTAR_AND_PESTLE.get());
      output.accept((ItemLike)ModItems.ATHAME.get());
      output.accept((ItemLike)ModItems.HEX_FOCUS.get());
      output.accept((ItemLike)ModItems.LADLE.get());
      output.accept((ItemLike)ModItems.SILK_IDOL.get());
      output.accept((ItemLike)ModItems.RAINFALL_IDOL.get());
      output.accept((ItemLike)ModItems.CLARITY_IDOL.get());
      output.accept((ItemLike)ModItems.TEMPEST_IDOL.get());
      output.accept((ItemLike)ModItems.PURITY_IDOL.get());
      output.accept((ItemLike)ModItems.MUTAVIS.get());
      output.accept((ItemLike)ModItems.SPIRITROOT_TETHER.get());
      output.accept((ItemLike)ModItems.VERDANT_GRIMOIRE.get());
   }

   private static void acceptWearables(Output output) {
      output.accept((ItemLike)ModItems.EARPLUGS.get());
      output.accept((ItemLike)ModItems.GHOSTVEIL.get());
      output.accept((ItemLike)ModItems.BOGSHADE_BOOTS.get());
      output.accept((ItemLike)ModItems.SILKWEAVE_HOOD.get());
      output.accept((ItemLike)ModItems.SILKWEAVE_MANTLE.get());
      output.accept((ItemLike)ModItems.SILKWEAVE_BINDINGS.get());
      output.accept((ItemLike)ModItems.SILKWEAVE_FOOTWRAPS.get());
      output.accept((ItemLike)ModItems.BLOOMWRAP_HAT.get());
      output.accept((ItemLike)ModItems.BLOOMWRAP_ROBES.get());
      output.accept((ItemLike)ModItems.BLOOMWRAP_LEGGINGS.get());
      output.accept((ItemLike)ModItems.BLOOMWRAP_BOOTS.get());
      output.accept((ItemLike)ModItems.MOONWEAVE_HOOD.get());
      output.accept((ItemLike)ModItems.MOONWEAVE_MANTLE.get());
      output.accept((ItemLike)ModItems.MOONWEAVE_BINDINGS.get());
      output.accept((ItemLike)ModItems.MOONWEAVE_FOOTWRAPS.get());
   }

   private static void acceptFunctionalBlocks(Output output) {
      output.accept((ItemLike)ModItems.SMALL_CAULDRON.get());
      output.accept((ItemLike)ModBlocks.SHELF.get());
      output.accept((ItemLike)ModBlocks.RITUAL_TABLE.get());
      output.accept((ItemLike)ModBlocks.INFUSED_DIRT.get());
      output.accept((ItemLike)ModBlocks.INFUSED_FARMLAND.get());
      output.accept((ItemLike)ModBlocks.RITUAL_BRAZIER.get());
      output.accept((ItemLike)ModBlocks.CENSER.get());
      output.accept((ItemLike)ModBlocks.DREAMCATCHER.get());
      output.accept((ItemLike)ModBlocks.NESTING_BLOCK.get());
   }

   private static void acceptDecor(Output output) {
      output.accept((ItemLike)ModItems.CANDLE_SKULL.get());
      output.accept((ItemLike)ModItems.WITHER_CANDLE_SKULL.get());
      output.accept((ItemLike)ModItems.SALT_LAMP.get());
   }

   private static void acceptRareItems(Output output) {
      output.accept((ItemLike)ModItems.ANCIENT_SEED.get());
      output.accept((ItemLike)ModItems.KELPWEAVE_BLADE.get());
      output.accept((ItemLike)ModItems.ROOTSHAPER.get());
      output.accept((ItemLike)ModItems.SAGE_PENDANT.get());
      output.accept((ItemLike)ModItems.THORNBOW.get());
      output.accept((ItemLike)ModItems.BRIAR_SICKLE.get());
   }

   private static void acceptCottonwood(Output output) {
      output.accept((ItemLike)ModBlocks.COTTONWOOD_SAPLING.get());
      output.accept((ItemLike)ModBlocks.COTTONWOOD_LEAVES.get());
      output.accept((ItemLike)ModBlocks.COTTONWOOD_LOG.get());
      output.accept((ItemLike)ModBlocks.COTTONWOOD_WOOD.get());
      output.accept((ItemLike)ModBlocks.STRIPPED_COTTONWOOD_LOG.get());
      output.accept((ItemLike)ModBlocks.STRIPPED_COTTONWOOD_WOOD.get());
      output.accept((ItemLike)ModBlocks.COTTONWOOD_PLANKS.get());
      output.accept((ItemLike)ModBlocks.COTTONWOOD_STAIRS.get());
      output.accept((ItemLike)ModBlocks.COTTONWOOD_SLAB.get());
      output.accept((ItemLike)ModBlocks.COTTONWOOD_FENCE.get());
      output.accept((ItemLike)ModBlocks.COTTONWOOD_FENCE_GATE.get());
      output.accept((ItemLike)ModBlocks.COTTONWOOD_DOOR.get());
      output.accept((ItemLike)ModBlocks.COTTONWOOD_TRAPDOOR.get());
      output.accept((ItemLike)ModBlocks.COTTONWOOD_PRESSURE_PLATE.get());
      output.accept((ItemLike)ModBlocks.COTTONWOOD_BUTTON.get());
      output.accept((ItemLike)ModItems.COTTONWOOD_SIGN.get());
      output.accept((ItemLike)ModItems.COTTONWOOD_HANGING_SIGN.get());
      output.accept((ItemLike)ModItems.COTTONWOOD_BOAT.get());
      output.accept((ItemLike)ModItems.COTTONWOOD_CHEST_BOAT.get());
   }

   private static void acceptWillow(Output output) {
      output.accept((ItemLike)ModBlocks.WILLOW_SAPLING.get());
      output.accept((ItemLike)ModBlocks.WILLOW_LEAVES.get());
      output.accept((ItemLike)ModBlocks.WILLOW_LOG.get());
      output.accept((ItemLike)ModBlocks.WILLOW_WOOD.get());
      output.accept((ItemLike)ModBlocks.STRIPPED_WILLOW_LOG.get());
      output.accept((ItemLike)ModBlocks.STRIPPED_WILLOW_WOOD.get());
      output.accept((ItemLike)ModBlocks.WILLOW_PLANKS.get());
      output.accept((ItemLike)ModBlocks.WILLOW_STAIRS.get());
      output.accept((ItemLike)ModBlocks.WILLOW_SLAB.get());
      output.accept((ItemLike)ModBlocks.WILLOW_FENCE.get());
      output.accept((ItemLike)ModBlocks.WILLOW_FENCE_GATE.get());
      output.accept((ItemLike)ModBlocks.WILLOW_DOOR.get());
      output.accept((ItemLike)ModBlocks.WILLOW_TRAPDOOR.get());
      output.accept((ItemLike)ModBlocks.WILLOW_PRESSURE_PLATE.get());
      output.accept((ItemLike)ModBlocks.WILLOW_BUTTON.get());
      output.accept((ItemLike)ModItems.WILLOW_SIGN.get());
      output.accept((ItemLike)ModItems.WILLOW_HANGING_SIGN.get());
      output.accept((ItemLike)ModItems.WILLOW_BOAT.get());
      output.accept((ItemLike)ModItems.WILLOW_CHEST_BOAT.get());
   }

   private static void acceptSpawnEggs(Output output) {
      output.accept((ItemLike)ModItems.SILK_MOTH_SPAWN_EGG.get());
      output.accept((ItemLike)ModItems.CACOFEY_SPAWN_EGG.get());
   }

   public static void init() {
      TABS.register();
   }
}
