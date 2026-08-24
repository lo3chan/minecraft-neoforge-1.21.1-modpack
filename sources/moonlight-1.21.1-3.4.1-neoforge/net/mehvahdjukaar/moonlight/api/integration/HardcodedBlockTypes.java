package net.mehvahdjukaar.moonlight.api.integration;

import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.set.leaves.LeavesTypeRegistry;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodTypeRegistry;
import net.mehvahdjukaar.moonlight.api.util.INamedSupplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class HardcodedBlockTypes {
   public static final INamedSupplier<WoodType> BURNT;

   public static void init() {
   }

   static {
      WoodTypeRegistry woodReg = WoodTypeRegistry.INSTANCE;
      LeavesTypeRegistry leafReg = LeavesTypeRegistry.INSTANCE;
      woodReg.addSimpleFinder("shroomcraft", "shroomwood").planksSuffix("_planks").log("stripped_mushroom_stem").childBlock("wood", "stripped_mushroom_hyphae");
      woodReg.addSimpleFinder("shroomcraft", "blue_shroomwood")
         .planksSuffix("_planks")
         .log("blue_mushroom_stem")
         .childBlock("stripped_log", "stripped_blue_mushroom_stem")
         .childBlock("stripped_wood", "stripped_blue_mushroom_hyphae");
      woodReg.addSimpleFinder("shroomcraft", "orange_shroomwood")
         .planksSuffix("_planks")
         .log("orange_mushroom_stem")
         .childBlock("stripped_log", "stripped_orange_mushroom_stem")
         .childBlock("stripped_wood", "stripped_orange_mushroom_hyphae");
      woodReg.addSimpleFinder("shroomcraft", "purple_shroomwood")
         .planksSuffix("_planks")
         .log("purple_mushroom_stem")
         .childBlock("stripped_log", "stripped_purple_mushroom_stem")
         .childBlock("stripped_wood", "stripped_purple_mushroom_hyphae");
      woodReg.addSimpleFinder("abundant_atmosphere", "red_bamboo").log("red_bamboo_block").childBlock("stripped_log", "stripped_red_bamboo_block");
      woodReg.addSimpleFinder("dungeonsdelight", "wormwood").bambooLike(true).log("wormroots_block");
      woodReg.addSimpleFinder("sniffed_out", "vessel")
         .log("crude_vessel_stem")
         .childBlock("wood", "crude_vessel_cuticle")
         .childBlock("stripped_wood", "stripped_vessel_cuticle");
      woodReg.addSimpleFinder("mofus_better_end_", "weepingstar").childBlockSuffix("leaves", "_leaf");
      woodReg.addSimpleFinder("mofus_better_end_", "frost_root").planksSuffix("_plank");
      BURNT = woodReg.addSimpleFinder("burnt", "smoldering_bamboo").logSuffix("_block").build();
      woodReg.addSimpleFinder("caverns_and_chasms", "azalea").childBlockSuffix("leaves", "_leaves");
      woodReg.addSimpleFinder("outer_end", "azure").childBlockSuffix("wood", "_pith").childBlockSuffix("stripped_wood", "_stripped_pith");
      woodReg.addSimpleFinder("deeperdarker", "bloom").log("blooming_stem").childBlock("stripped_log", "stripped_blooming_stem");
      woodReg.addSimpleFinder("blocksplus", "chorus");
      woodReg.addSimpleFinder("blocksplus", "bamboo");
      woodReg.addSimpleFinder("blocksplus", "mushroom");
      woodReg.addSimpleFinder("integrateddynamics", "menril");
      woodReg.addSimpleFinder("domum_ornamentum", "cactus").planks("green_cactus_extra").log((Supplier<Block>)(() -> Blocks.CACTUS));
      woodReg.addSimpleFinder("domum_ornamentum", "cactus_extra").planks("cactus_extra").log((Supplier<Block>)(() -> Blocks.CACTUS));
      woodReg.addSimpleFinder("netherexp", "claret").log("cerebrage_claret_stem").childBlock("wood", "cerebrage_claret_hyphae");
      woodReg.addSimpleFinder("piglin_ruins", "ominous").log("ominous_stalk_block");
      woodReg.addSimpleFinder("unusualend", "chorus_nest")
         .planks("chorus_nest_planks")
         .log("chorus_cane_block")
         .childBlock("stripped_log", "stripped_chorus_cane_block")
         .childBlock("fence", "chorus_nest_mosaic_fence");
      woodReg.addSimpleFinder("spectrum", "ivory_noxwood")
         .log("ivory_noxcap_stem")
         .childBlock("stripped_log", "stripped_ivory_noxcap_stem")
         .childBlock("wood", "ivory_noxcap_hyphae")
         .childBlock("stripped_wood", "stripped_ivory_noxcap_hyphae");
      woodReg.addSimpleFinder("spectrum", "slate_noxwood")
         .log("slate_noxcap_stem")
         .childBlock("stripped_log", "stripped_slate_noxcap_stem")
         .childBlock("wood", "slate_noxcap_hyphae")
         .childBlock("stripped_wood", "stripped_slate_noxcap_hyphae");
      woodReg.addSimpleFinder("spectrum", "ebony_noxwood")
         .log("ebony_noxcap_stem")
         .childBlock("stripped_log", "stripped_ebony_noxcap_stem")
         .childBlock("wood", "ebony_noxcap_hyphae")
         .childBlock("stripped_wood", "stripped_ebony_noxcap_hyphae");
      woodReg.addSimpleFinder("spectrum", "chestnut_noxwood")
         .log("chestnut_noxcap_stem")
         .childBlock("stripped_log", "stripped_chestnut_noxcap_stem")
         .childBlock("wood", "chestnut_noxcap_hyphae")
         .childBlock("stripped_wood", "stripped_chestnut_noxcap_hyphae");
      if (!PlatHelper.isModLoaded("archwood_good")) {
         woodReg.addSimpleFinder("ars_nouveau", "archwood")
            .log("blue_archwood_log")
            .childBlock("stripped_log", "stripped_blue_archwood_log")
            .childBlock("wood", "blue_archwood_wood")
            .childBlock("stripped_wood", "stripped_blue_archwood_wood")
            .childBlock("leaves", "blue_archwood_leaves")
            .childBlock("sapling", "blue_archwood_sapling");
      }

      woodReg.addSimpleFinder("blue_skies", "crystallized");
      woodReg.addSimpleFinder("darkerdepths", "petrified");
      woodReg.addSimpleFinder("pokecube_legends", "concrete");
      woodReg.addSimpleFinder("terraqueous", "storm_cloud").planks("storm_cloud").log("storm_cloud_column");
      woodReg.addSimpleFinder("terraqueous", "light_cloud").planks("light_cloud").log("light_cloud_column");
      woodReg.addSimpleFinder("terraqueous", "dense_cloud").planks("dense_cloud").log("dense_cloud_column");
      woodReg.addSimpleFinder("rats", "pirat");
      woodReg.addSimpleFinder("byg", "embur").planks("embur_pedu").log("embur_pedu_top");
      woodReg.addSimpleFinder("nethers_exoticism", "jabuticaba").planks("jaboticaba_planks").log("jabuticaba_log");
      woodReg.addSimpleFinder("mynethersdelight", "powdery").bambooLike(true).logSuffix("_block").childBlockAffix("stripped_log", "stripped_", "_block");
      woodReg.addSimpleFinder("nourished_end", "verdant").logSuffix("_stalk").childBlock("wood", "verdant_hyphae");
      woodReg.addSimpleFinder("nourished_end", "cerulean")
         .logSuffix("_stem_thick")
         .childBlockSuffix("stripped_log", "_stem_stripped")
         .childBlockSuffix("wood", "_hyphae")
         .childBlockSuffix("stripped_wood", "_hyphae");
      woodReg.addSimpleFinder("gardens_of_the_dead", "whistlecane")
         .bambooLike(true)
         .planks("whistlecane_planks")
         .log("whistlecane_block")
         .childItem("stick", "whistlecane");
      woodReg.addSimpleFinder("blazingbamboo", "blazing_bamboo").planks("blazingbamboo:blazing_bamboo_planks").log("blazingbamboo:blazing_bamboo_bundle");
      woodReg.addSimpleFinder("luminous_nether", "mushroom")
         .planks("mushroom_planks")
         .log("goldenstem")
         .childBlock("stripped_log", "shredded_stem")
         .childBlock("wood", "goldmushroom")
         .childBlock("sapling", "golden_mushroom")
         .childItem("stick", "whistlecane");
      woodReg.addSimpleFinder("desolation", "charred").log("charredlog");
      woodReg.addSimpleFinder("dawnoftimebuilder", "waxed_oak").log("waxed_oak_log_stripped").planks("waxed_oak_planks");
      woodReg.addSimpleFinder("dawnoftimebuilder", "charred_spruce").log("charred_spruce_log_stripped").planks("charred_spruce_planks");
      woodReg.addSimpleFinder("habitat", "fairy_ring_mushroom").planks("fairy_ring_mushroom_planks").log("enhanced_fairy_ring_mushroom_stem");
      woodReg.addSimpleFinder("ecologics", "flowering_azalea").child("leaves", () -> Blocks.FLOWERING_AZALEA_LEAVES);
      woodReg.addSimpleFinder("ecologics", "azalea").child("leaves", () -> Blocks.AZALEA_LEAVES);
      woodReg.addSimpleFinder("quark", "azalea").child("leaves", () -> Blocks.AZALEA_LEAVES);
      leafReg.addSimpleFinder("nomansland", "autumnal_oak").childBlock("log", ResourceLocation.withDefaultNamespace("oak_log"));
      leafReg.addSimpleFinder("nomansland", "frosted").childBlock("log", "pine_log");
      leafReg.addSimpleFinder("nomansland", "pale_cherry").childBlock("log", ResourceLocation.withDefaultNamespace("cherry_log"));
      leafReg.addSimpleFinder("nomansland", "red_maple").childBlock("log", "maple_log");
      leafReg.addSimpleFinder("nomansland", "yellow_birch").childBlock("log", ResourceLocation.withDefaultNamespace("birch_log"));
      leafReg.addSimpleFinder("biomeswevegone", "flowering_palo_verde").childBlock("log", "palo_verde_log");
      leafReg.addLeavesToWoodMapping("biomeswevegone", "araucaria", "pine");
      leafReg.addLeavesToWoodMapping("biomeswevegone", "holly_berry", "holly");
      leafReg.addLeavesToWoodMapping("biomeswevegone:firecracker", "minecraft:oak");
      leafReg.addLeavesToWoodMapping("biomeswevegone:yucca", "minecraft:oak");
      leafReg.addLeavesToWoodMapping("biomeswevegone:ripe_yucca", "minecraft:oak");
      leafReg.addLeavesToWoodMapping("biomeswevegone:flowering_yucca", "minecraft:oak");
      leafReg.addLeavesToWoodMapping("biomeswevegone:orchard", "minecraft:oak");
      leafReg.addLeavesToWoodMapping("biomeswevegone:ripe_orchard", "minecraft:oak");
      leafReg.addLeavesToWoodMapping("biomeswevegone:flowering_orchard", "minecraft:oak");
      leafReg.addLeavesToWoodMapping("luminous_nether:ash", "luminous_nether:withered");
      leafReg.addLeavesToWoodMapping("fruitfulfun:apple", "minecraft:oak");
      leafReg.addLeavesToWoodMapping("fruitfulfun:pomegranate", "minecraft:jungle");
      leafReg.addLeavesToWoodMapping("fruitfulfun", "grapefruit", "citrus");
      leafReg.addLeavesToWoodMapping("fruitfulfun", "lemon", "citrus");
      leafReg.addLeavesToWoodMapping("fruitfulfun", "tangerine", "citrus");
      leafReg.addLeavesToWoodMapping("fruitfulfun", "lime", "citrus");
      leafReg.addLeavesToWoodMapping("fruitfulfun", "citron", "citrus");
      leafReg.addLeavesToWoodMapping("fruitfulfun", "pomelo", "citrus");
      leafReg.addLeavesToWoodMapping("fruitfulfun", "orange", "citrus");
      leafReg.addSimpleFinder("environmental", "pink_wisteria").childBlock("log", "wisteria_log");
      leafReg.addSimpleFinder("environmental", "blue_wisteria").childBlock("log", "wisteria_log");
      leafReg.addSimpleFinder("environmental", "purple_wisteria").childBlock("log", "wisteria_log");
      leafReg.addSimpleFinder("environmental", "white_wisteria").childBlock("log", "wisteria_log");
      leafReg.addLeavesToWoodMapping("environmental", "cheerful_plum", "plum");
      leafReg.addLeavesToWoodMapping("environmental", "moody_plum", "plum");
      leafReg.addSimpleFinder("ecologics", "coconut").childBlock("sapling", "coconut_seedling");
      leafReg.addLeavesToWoodMapping("biomesoplenty:origin", "minecraft:oak");
      leafReg.addLeavesToWoodMapping("blue_skies", "crystallized", "crystallized");
      leafReg.addLeavesToWoodMapping("blue_skies", "crescent_fruit", "dusk");
      leafReg.addLeavesToWoodMapping("colorfulazaleas", "blue_azalea", "azule_azalea");
      leafReg.addLeavesToWoodMapping("colorfulazaleas", "blue_blooming_azalea", "azule_azalea");
      leafReg.addLeavesToWoodMapping("colorfulazaleas", "blue_flowering_azalea", "azule_azalea");
      leafReg.addLeavesToWoodMapping("colorfulazaleas", "orange_azalea", "tecal_azalea");
      leafReg.addLeavesToWoodMapping("colorfulazaleas", "orange_blooming_azalea", "tecal_azalea");
      leafReg.addLeavesToWoodMapping("colorfulazaleas", "orange_flowering_azalea", "tecal_azalea");
      leafReg.addLeavesToWoodMapping("colorfulazaleas", "pink_azalea", "bright_azalea");
      leafReg.addLeavesToWoodMapping("colorfulazaleas", "pink_blooming_azalea", "bright_azalea");
      leafReg.addLeavesToWoodMapping("colorfulazaleas", "pink_flowering_azalea", "bright_azalea");
      leafReg.addLeavesToWoodMapping("colorfulazaleas", "purple_azalea", "walnut_azalea");
      leafReg.addLeavesToWoodMapping("colorfulazaleas", "purple_blooming_azalea", "walnut_azalea");
      leafReg.addLeavesToWoodMapping("colorfulazaleas", "purple_flowering_azalea", "walnut_azalea");
      leafReg.addLeavesToWoodMapping("colorfulazaleas", "red_azalea", "roze_azalea");
      leafReg.addLeavesToWoodMapping("colorfulazaleas", "red_blooming_azalea", "roze_azalea");
      leafReg.addLeavesToWoodMapping("colorfulazaleas", "red_flowering_azalea", "roze_azalea");
      leafReg.addLeavesToWoodMapping("colorfulazaleas", "white_azalea", "titanium_azalea");
      leafReg.addLeavesToWoodMapping("colorfulazaleas", "white_blooming_azalea", "titanium_azalea");
      leafReg.addLeavesToWoodMapping("colorfulazaleas", "white_flowering_azalea", "titanium_azalea");
      leafReg.addLeavesToWoodMapping("colorfulazaleas", "yellow_azalea", "fiss_azalea");
      leafReg.addLeavesToWoodMapping("colorfulazaleas", "yellow_blooming_azalea", "fiss_azalea");
      leafReg.addLeavesToWoodMapping("colorfulazaleas", "yellow_flowering_azalea", "fiss_azalea");
      leafReg.addSimpleFinder("pokecube_legends", "dyna_pastel_pink").leaves("dyna_leaves_pastel_pink").equivalentWood("pokecube_legends:aged");
      leafReg.addSimpleFinder("pokecube_legends", "dyna_pink").leaves("dyna_leaves_pink").equivalentWood("pokecube_legends:aged");
      leafReg.addSimpleFinder("pokecube_legends", "dyna_red").leaves("dyna_leaves_red").equivalentWood("pokecube_legends:aged");
      leafReg.addLeavesToWoodMapping("regions_unexplored:bamboo", "minecraft:jungle");
      leafReg.addLeavesToWoodMapping("twilightforest", "beanstalk", "twilight_oak");
      leafReg.addLeavesToWoodMapping("twilightforest", "thorn", "twilight_oak");
      leafReg.addLeavesToWoodMapping("ulterlands:souldrained", "minecraft:oak");
      String skyroot_or_crystal = PlatHelper.isModLoaded("aether_redux") ? "aether_redux:crystal" : "aether:skyroot";
      String skyroot_or_glacia = PlatHelper.isModLoaded("aether_redux") ? "aether_redux:glacia" : "aether:skyroot";
      leafReg.addLeavesToWoodMapping("aether:crystal", skyroot_or_crystal);
      leafReg.addLeavesToWoodMapping("aether:crystal_fruit", skyroot_or_crystal);
      leafReg.addLeavesToWoodMapping("aether", "golden_oak", "skyroot");
      leafReg.addLeavesToWoodMapping("aether:holiday", skyroot_or_glacia);
      leafReg.addLeavesToWoodMapping("aether:decorated_holiday", skyroot_or_glacia);
      leafReg.addLeavesToWoodMapping("aether:crystal", skyroot_or_crystal);
      leafReg.addLeavesToWoodMapping("aether:crystal_fruit", skyroot_or_crystal);
      leafReg.addLeavesToWoodMapping("aether", "gilded_oak", "skyroot");
      leafReg.addLeavesToWoodMapping("aether_redux:gilded_oak", "aether:skyroot");
      leafReg.addLeavesToWoodMapping("aether_redux:blighted_skyroot", "aether:skyroot");
      leafReg.addLeavesToWoodMapping("aether_genesis:purple_crystal", skyroot_or_crystal);
      leafReg.addLeavesToWoodMapping("aether_genesis:purple_crystal_fruit", skyroot_or_crystal);
      leafReg.addLeavesToWoodMapping("ancient_aether:crystal_skyroot", "aether:skyroot");
      leafReg.addLeavesToWoodMapping("ancient_aether:enchanted_skyroot", "aether:skyroot");
      leafReg.addLeavesToWoodMapping("ancient_aether:skyroot_pine", "aether:skyroot");
      leafReg.addLeavesToWoodMapping("ancient_aether:blue_skyroot_pine", "aether:skyroot");
      leafReg.addLeavesToWoodMapping("ancient_aether:wyndcaps_holiday_tree", "aether:skyroot");
      leafReg.addLeavesToWoodMapping("autumnity", "yellow_maple", "maple");
      leafReg.addLeavesToWoodMapping("autumnity", "orange_maple", "maple");
      leafReg.addLeavesToWoodMapping("autumnity", "red_maple", "maple");
      leafReg.addLeavesToWoodMapping("alexscaves:ancient", "minecraft:jungle");
      leafReg.addSimpleFinder("ars_elemental", "yellow_archwood").childBlock("log", ResourceLocation.parse("ars_nouveau:archwood_log"));
   }
}
