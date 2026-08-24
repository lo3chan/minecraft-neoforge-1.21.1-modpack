package net.astralya.hexalia.neoforge.datagen;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.effect.ModMobEffects;
import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;

public final class ModLanguageProvider extends LanguageProvider {
   public ModLanguageProvider(PackOutput output) {
      super(output, "hexalia", "en_us");
   }

   protected void addTranslations() {
      this.addCreativeTabTranslations();
      this.addBlockTranslations();
      this.addWoodTranslations();
      this.addItemTranslations();
      this.addEntityTranslations();
      this.addContainerTranslations();
      this.addTooltipTranslations();
      this.addMessageTranslations();
      this.addAdvancementTranslations();
      this.addEffectTranslations();
      this.addJeiTranslations();
      this.addEmiTranslations();
      this.addTagTranslations();
      this.addMiscTranslations();
   }

   private void addCreativeTabTranslations() {
      this.add("itemGroup.hexalia", "Hexalia");
   }

   private void addBlockTranslations() {
      this.add((Block)ModBlocks.INFUSED_DIRT.get(), "Infused Dirt");
      this.add((Block)ModBlocks.INFUSED_FARMLAND.get(), "Infused Farmland");
      this.add((Block)ModBlocks.SILKWORM_COCOON.get(), "Silkworm Cocoon");
      this.add((Block)ModBlocks.EGG_CLUSTER.get(), "Egg Cluster");
      this.add((Block)ModBlocks.SPIRIT_BLOOM.get(), "Spirit Bloom");
      this.add((Block)ModBlocks.POTTED_SPIRIT_BLOOM.get(), "Potted Spirit Bloom");
      this.add((Block)ModBlocks.DREAMSHROOM.get(), "Dreamshroom");
      this.add((Block)ModBlocks.POTTED_DREAMSHROOM.get(), "Potted Dreamshroom");
      this.add((Block)ModBlocks.SIREN_KELP.get(), "Siren Kelp");
      this.add((Block)ModBlocks.GHOST_FERN.get(), "Ghost Fern");
      this.add((Block)ModBlocks.POTTED_GHOST_FERN.get(), "Potted Ghost Fern");
      this.add((Block)ModBlocks.CELESTIAL_BLOOM.get(), "Celestial Bloom");
      this.add((Block)ModBlocks.POTTED_CELESTIAL_BLOOM.get(), "Potted Celestial Bloom");
      this.add((Block)ModBlocks.WITHERED_CELESTIAL_BLOOM.get(), "Withered Celestial Bloom");
      this.add((Block)ModBlocks.POTTED_WITHERED_CELESTIAL_BLOOM.get(), "Potted Withered Celestial Bloom");
      this.add((Block)ModBlocks.LOTUS_FLOWER.get(), "Lotus Flower");
      this.add((Block)ModBlocks.WITCHWEED.get(), "Witchweed");
      this.add((Block)ModBlocks.MORPHORA.get(), "Morphora");
      this.add((Block)ModBlocks.POTTED_MORPHORA.get(), "Potted Morphora");
      this.add((Block)ModBlocks.GRIMSHADE.get(), "Grimshade");
      this.add((Block)ModBlocks.POTTED_GRIMSHADE.get(), "Potted Grimshade");
      this.add((Block)ModBlocks.NAUTILITE.get(), "Nautilite");
      this.add((Block)ModBlocks.WINDSONG.get(), "Windsong");
      this.add((Block)ModBlocks.POTTED_WINDSONG.get(), "Potted Windsong");
      this.add((Block)ModBlocks.ASTRYLIS.get(), "Astrylis");
      this.add((Block)ModBlocks.POTTED_ASTRYLIS.get(), "Potted Astrylis");
      this.add((Block)ModBlocks.LOURDES.get(), "Lourdes");
      this.add((Block)ModBlocks.POTTED_LOURDES.get(), "Potted Lourdes");
      this.add((Block)ModBlocks.AEGIFLORA.get(), "Aegiflora");
      this.add((Block)ModBlocks.POTTED_AEGIFLORA.get(), "Potted Aegiflora");
      this.add((Block)ModBlocks.WITHERED_AEGIFLORA.get(), "Withered Aegiflora");
      this.add((Block)ModBlocks.POTTED_WITHERED_AEGIFLORA.get(), "Potted Withered Aegiflora");
      this.add((Block)ModBlocks.BEGONIA.get(), "Begonia");
      this.add((Block)ModBlocks.POTTED_BEGONIA.get(), "Potted Begonia");
      this.add((Block)ModBlocks.LAVENDER.get(), "Lavender");
      this.add((Block)ModBlocks.POTTED_LAVENDER.get(), "Potted Lavender");
      this.add((Block)ModBlocks.DAHLIA.get(), "Dahlia");
      this.add((Block)ModBlocks.POTTED_DAHLIA.get(), "Potted Dahlia");
      this.add((Block)ModBlocks.PALE_MUSHROOM.get(), "Pale Mushroom");
      this.add((Block)ModBlocks.NIGHTSHADE_BUSH.get(), "Nightshade Bush");
      this.add((Block)ModBlocks.POTTED_NIGHTSHADE_BUSH.get(), "Potted Nightshade Bush");
      this.add((Block)ModBlocks.MANDRAKE_CROP.get(), "Mandrake");
      this.add((Block)ModBlocks.SUNFIRE_TOMATO_CROP.get(), "Sunfire Tomato");
      this.add((Block)ModBlocks.RABBAGE_CROP.get(), "Rabbage");
      this.add((Block)ModBlocks.WILD_MANDRAKE.get(), "Wild Mandrake");
      this.add((Block)ModBlocks.WILD_SUNFIRE_TOMATO.get(), "Wild Sunfire Tomato");
      this.add((Block)ModBlocks.CHILLBERRY_BUSH.get(), "Chillberry Bush");
      this.add((Block)ModBlocks.SALTSPROUT.get(), "Saltsprout");
      this.add((Block)ModBlocks.GALEBERRIES_VINE.get(), "Galeberries Vine");
      this.add((Block)ModBlocks.GALEBERRIES_VINE_PLANT.get(), "Galeberries Vine Plant");
      this.add((Block)ModBlocks.SALT_BLOCK.get(), "Salt Block");
      this.add((Block)ModBlocks.SALT_LAMP.get(), "Salt Lamp");
      this.add((Block)ModBlocks.CELESTIAL_CRYSTAL_BLOCK.get(), "Celestial Crystal Block");
      this.add((Block)ModBlocks.SMALL_CAULDRON.get(), "Small Cauldron");
      this.add((Block)ModBlocks.SHELF.get(), "Shelf");
      this.add((Block)ModBlocks.RUSTIC_OVEN.get(), "Rustic Oven");
      this.add((Block)ModBlocks.RITUAL_TABLE.get(), "Ritual Table");
      this.add((Block)ModBlocks.RITUAL_BRAZIER.get(), "Ritual Brazier");
      this.add((Block)ModBlocks.CENSER.get(), "Censer");
      this.add((Block)ModBlocks.DREAMCATCHER.get(), "Dreamcatcher");
      this.add((Block)ModBlocks.MORTAR_AND_PESTLE.get(), "Mortar and Pestle");
      this.add((Block)ModBlocks.NESTING_BLOCK.get(), "Nesting Block");
      this.add((Block)ModBlocks.CANDLE_SKULL.get(), "Candle Skull");
      this.add((Block)ModBlocks.WITHER_CANDLE_SKULL.get(), "Wither Candle Skull");
   }

   private void addWoodTranslations() {
      this.add((Block)ModBlocks.COTTONWOOD_LEAVES.get(), "Cottonwood Leaves");
      this.add((Block)ModBlocks.COTTONWOOD_CATKIN.get(), "Cottonwood Catkin");
      this.add((Block)ModBlocks.COTTONWOOD_LOG.get(), "Cottonwood Log");
      this.add((Block)ModBlocks.COTTONWOOD_WOOD.get(), "Cottonwood Wood");
      this.add((Block)ModBlocks.STRIPPED_COTTONWOOD_LOG.get(), "Stripped Cottonwood Log");
      this.add((Block)ModBlocks.STRIPPED_COTTONWOOD_WOOD.get(), "Stripped Cottonwood Wood");
      this.add((Block)ModBlocks.COTTONWOOD_PLANKS.get(), "Cottonwood Planks");
      this.add((Block)ModBlocks.COTTONWOOD_SAPLING.get(), "Cottonwood Sapling");
      this.add((Block)ModBlocks.COTTONWOOD_STAIRS.get(), "Cottonwood Stairs");
      this.add((Block)ModBlocks.COTTONWOOD_SLAB.get(), "Cottonwood Slab");
      this.add((Block)ModBlocks.COTTONWOOD_BUTTON.get(), "Cottonwood Button");
      this.add((Block)ModBlocks.COTTONWOOD_PRESSURE_PLATE.get(), "Cottonwood Pressure Plate");
      this.add((Block)ModBlocks.COTTONWOOD_FENCE.get(), "Cottonwood Fence");
      this.add((Block)ModBlocks.COTTONWOOD_FENCE_GATE.get(), "Cottonwood Fence Gate");
      this.add((Block)ModBlocks.COTTONWOOD_TRAPDOOR.get(), "Cottonwood Trapdoor");
      this.add((Block)ModBlocks.COTTONWOOD_DOOR.get(), "Cottonwood Door");
      this.add((Block)ModBlocks.COTTONWOOD_SIGN.get(), "Cottonwood Sign");
      this.add((Block)ModBlocks.COTTONWOOD_HANGING_SIGN.get(), "Cottonwood Hanging Sign");
      this.add((Block)ModBlocks.WILLOW_LEAVES.get(), "Willow Leaves");
      this.add((Block)ModBlocks.WILLOW_LOG.get(), "Willow Log");
      this.add((Block)ModBlocks.WILLOW_WOOD.get(), "Willow Wood");
      this.add((Block)ModBlocks.STRIPPED_WILLOW_LOG.get(), "Stripped Willow Log");
      this.add((Block)ModBlocks.STRIPPED_WILLOW_WOOD.get(), "Stripped Willow Wood");
      this.add((Block)ModBlocks.WILLOW_PLANKS.get(), "Willow Planks");
      this.add((Block)ModBlocks.WILLOW_SAPLING.get(), "Willow Sapling");
      this.add((Block)ModBlocks.WILLOW_STAIRS.get(), "Willow Stairs");
      this.add((Block)ModBlocks.WILLOW_SLAB.get(), "Willow Slab");
      this.add((Block)ModBlocks.WILLOW_BUTTON.get(), "Willow Button");
      this.add((Block)ModBlocks.WILLOW_PRESSURE_PLATE.get(), "Willow Pressure Plate");
      this.add((Block)ModBlocks.WILLOW_FENCE.get(), "Willow Fence");
      this.add((Block)ModBlocks.WILLOW_FENCE_GATE.get(), "Willow Fence Gate");
      this.add((Block)ModBlocks.WILLOW_TRAPDOOR.get(), "Willow Trapdoor");
      this.add((Block)ModBlocks.WILLOW_DOOR.get(), "Willow Door");
      this.add((Block)ModBlocks.WILLOW_SIGN.get(), "Willow Sign");
      this.add((Block)ModBlocks.WILLOW_HANGING_SIGN.get(), "Willow Hanging Sign");
   }

   private void addItemTranslations() {
      this.add((Item)ModItems.SALT.get(), "Salt");
      this.add((Item)ModItems.TREE_RESIN.get(), "Tree Resin");
      this.add((Item)ModItems.SILK_FIBER.get(), "Silk Fiber");
      this.add((Item)ModItems.SILKWORM.get(), "Silkworm");
      this.add((Item)ModItems.CELESTIAL_CRYSTAL.get(), "Celestial Crystal");
      this.add((Item)ModItems.FIRE_NODE.get(), "Fire Node");
      this.add((Item)ModItems.WATER_NODE.get(), "Water Node");
      this.add((Item)ModItems.AIR_NODE.get(), "Air Node");
      this.add((Item)ModItems.EARTH_NODE.get(), "Earth Node");
      this.add((Item)ModItems.ANCIENT_SEED.get(), "Ancient Seed");
      this.add((Item)ModItems.SUNFIRE_TOMATO.get(), "Sunfire Tomato");
      this.add((Item)ModItems.SUNFIRE_TOMATO_SEEDS.get(), "Sunfire Tomato Seeds");
      this.add((Item)ModItems.MANDRAKE.get(), "Mandrake");
      this.add((Item)ModItems.MANDRAKE_SEEDS.get(), "Mandrake Seeds");
      this.add((Item)ModItems.RABBAGE.get(), "Rabbage");
      this.add((Item)ModItems.RABBAGE_SEEDS.get(), "Rabbage Seeds");
      this.add((Item)ModItems.CHILLBERRIES.get(), "Chillberries");
      this.add((Item)ModItems.GALEBERRIES.get(), "Galeberries");
      this.add((Item)ModItems.LOTUS_BLOSSOM.get(), "Lotus Blossom");
      this.add((Item)ModItems.SPIRIT_POWDER.get(), "Spirit Powder");
      this.add((Item)ModItems.SIREN_PASTE.get(), "Siren Paste");
      this.add((Item)ModItems.DREAM_PASTE.get(), "Dream Paste");
      this.add((Item)ModItems.GHOST_POWDER.get(), "Ghost Powder");
      this.add((Item)ModItems.FRAGRANT_NECTAR.get(), "Fragrant Nectar");
      this.add((Item)ModItems.SPICY_SANDWICH.get(), "Spicy Sandwich");
      this.add((Item)ModItems.CHILLBERRY_PIE.get(), "Chillberry Pie");
      this.add((Item)ModItems.MANDRAKE_STEW.get(), "Mandrake Stew");
      this.add((Item)ModItems.GALEBERRIES_COOKIE.get(), "Galeberries Cookie");
      this.add((Item)ModItems.HEX_FOCUS.get(), "Hex Focus");
      this.add((Item)ModItems.ATHAME.get(), "Athame");
      this.add((Item)ModItems.PURIFYING_SAC.get(), "Purifying Sac");
      this.add((Item)ModItems.FOUL_SAC.get(), "Foul Sac");
      this.add((Item)ModItems.FROST_SAC.get(), "Frost Sac");
      this.add((Item)ModItems.SEARING_SAC.get(), "Searing Sac");
      this.add((Item)ModItems.SAGE_PENDANT.get(), "Sage Pendant");
      this.add((Item)ModItems.SILK_IDOL.get(), "Silk Idol");
      this.add((Item)ModItems.CLARITY_IDOL.get(), "Clarity Idol");
      this.add((Item)ModItems.RAINFALL_IDOL.get(), "Rainfall Idol");
      this.add((Item)ModItems.TEMPEST_IDOL.get(), "Tempest Idol");
      this.add((Item)ModItems.PURITY_IDOL.get(), "Purity Idol");
      this.add((Item)ModItems.MUTAVIS.get(), "Mutavis");
      this.add((Item)ModItems.BRIAR_SICKLE.get(), "Briar Sickle");
      this.add((Item)ModItems.ROOTSHAPER.get(), "Rootshaper Pickaxe");
      this.add((Item)ModItems.SPIRITROOT_TETHER.get(), "Spiritroot Tether");
      this.add((Item)ModItems.LADLE.get(), "Ladle");
      this.add((Item)ModItems.KELPWEAVE_BLADE.get(), "Kelpweave Blade");
      this.add((Item)ModItems.THORNBOW.get(), "Thornbow");
      this.add((Item)ModItems.SILKWEAVE_HOOD.get(), "Silkweave Hood");
      this.add((Item)ModItems.SILKWEAVE_MANTLE.get(), "Silkweave Mantle");
      this.add((Item)ModItems.SILKWEAVE_BINDINGS.get(), "Silkweave Bindings");
      this.add((Item)ModItems.SILKWEAVE_FOOTWRAPS.get(), "Silkweave Footwraps");
      this.add((Item)ModItems.MOONWEAVE_HOOD.get(), "Moonweave Hood");
      this.add((Item)ModItems.MOONWEAVE_MANTLE.get(), "Moonweave Mantle");
      this.add((Item)ModItems.MOONWEAVE_BINDINGS.get(), "Moonweave Bindings");
      this.add((Item)ModItems.MOONWEAVE_FOOTWRAPS.get(), "Moonweave Footwraps");
      this.add((Item)ModItems.BLOOMWRAP_HAT.get(), "Bloomwrap Hat");
      this.add((Item)ModItems.BLOOMWRAP_ROBES.get(), "Bloomwrap Robes");
      this.add((Item)ModItems.BLOOMWRAP_LEGGINGS.get(), "Bloomwrap Leggings");
      this.add((Item)ModItems.BLOOMWRAP_BOOTS.get(), "Bloomwrap Boots");
      this.add((Item)ModItems.GHOSTVEIL.get(), "Ghostveil");
      this.add((Item)ModItems.EARPLUGS.get(), "Earplugs");
      this.add((Item)ModItems.BOGSHADE_BOOTS.get(), "Bogshade Boots");
      this.add((Item)ModItems.RUSTIC_BOTTLE.get(), "Rustic Bottle");
      this.add((Item)ModItems.BREW_OF_BLOODLUST.get(), "Brew of Bloodlust");
      this.add((Item)ModItems.BREW_OF_SLIMEWALKER.get(), "Brew of Slimewalker");
      this.add((Item)ModItems.BREW_OF_SPIKESKIN.get(), "Brew of Spikeskin");
      this.add((Item)ModItems.BREW_OF_SIPHON.get(), "Brew of Siphon");
      this.add((Item)ModItems.BREW_OF_DAYBLOOM.get(), "Brew of Daybloom");
      this.add((Item)ModItems.BREW_OF_ARACHNID_GRACE.get(), "Brew of Arachnid Grace");
      this.add((Item)ModItems.BREW_OF_HOMESTEAD.get(), "Brew of Homestead");
      this.add((Item)ModItems.BREW_OF_HOLLOW_SILENCE.get(), "Brew of Hollow Silence");
      this.add((Item)ModItems.BRAMBLEGUARD_SALVE.get(), "Brambleguard Salve");
      this.add((Item)ModItems.MENDERS_SALVE.get(), "Mender's Salve");
      this.add((Item)ModItems.BOTTLED_MOTH.get(), "Bottled Silk Moth");
      this.add((Item)ModItems.SILK_MOTH_SPAWN_EGG.get(), "Silk Moth Spawn Egg");
      this.add((Item)ModItems.CACOFEY_SPAWN_EGG.get(), "Cacofey Spawn Egg");
      this.add((Item)ModItems.COTTONWOOD_BOAT.get(), "Cottonwood Boat");
      this.add((Item)ModItems.COTTONWOOD_CHEST_BOAT.get(), "Cottonwood Chest Boat");
      this.add((Item)ModItems.WILLOW_BOAT.get(), "Willow Boat");
      this.add((Item)ModItems.WILLOW_CHEST_BOAT.get(), "Willow Chest Boat");
      this.add((Item)ModItems.VERDANT_GRIMOIRE.get(), "Verdant Grimoire");
   }

   private void addEntityTranslations() {
      this.add((EntityType)ModEntities.SILK_MOTH.get(), "Silk Moth");
      this.add((EntityType)ModEntities.CACOFEY.get(), "Cacofey");
      this.add((EntityType)ModEntities.THORN_ARROW.get(), "Thorn Arrow");
   }

   private void addContainerTranslations() {
      this.add("container.hexalia.nesting_block", "Nesting Block");
      this.add("container.hexalia.small_cauldron", "Small Cauldron");
   }

   private void addTooltipTranslations() {
      this.add("tooltip.hexalia.heat", "A heat source is required");
      this.add("tooltip.hexalia.hex_focus_gui", "Right-click with a Hex Focus");
      this.add("tooltip.hexalia.mutation", "Right-click with a Mutavis");
      this.add("tooltip.hexalia.homestead_brew", "Teleports to your spawn");
      this.add("tooltip.hexalia.siphon_brew", "Siphon (4:00)");
      this.add("tooltip.hexalia.slimewalker_brew", "Slimewalker (4:00)");
      this.add("tooltip.hexalia.bloodlust_brew", "Bloodlust (4:00)");
      this.add("tooltip.hexalia.spikeskin_brew", "Spikeskin (4:00)");
      this.add("tooltip.hexalia.daybloom", "Daybloom (4:00)");
      this.add("tooltip.hexalia.arachnid_grace", "Arachnid Grace (4:00)");
      this.add("tooltip.hexalia.hollow_silence", "Hollow Silence (4:00)");
      this.add("tooltip.hexalia.menders_salve", "Regeneration (1:30)");
      this.add("tooltip.hexalia.brambleguard_salve", "Brambleguard (1:30)");
      this.add("tooltip.hexalia.hold_shift", "Hold SHIFT for more information");
      this.add("tooltip.hexalia.bogshade_boots", "Full Set Bonus:");
      this.add("tooltip.hexalia.bogged_armor_2", "Poison Immunity and Water Breathing");
      this.add("tooltip.hexalia.ghostveil", "Shrouds the wearer to avoid mob detection");
      this.add("tooltip.hexalia.mandrake", "Stuns in an area on usage");
      this.add("tooltip.hexalia.purifying_sac", "Removes negative status effects");
      this.add("tooltip.hexalia.throwable", "Throwable");
      this.add("tooltip.hexalia.bottled_moth", "Contains: %s");
      this.add("tooltip.hexalia.enchanted_plant", "Enchanted Plant");
      this.add("tooltip.hexalia.shelf", "Can store up to 6 brews or potions");
      this.add("tooltip.hexalia.thornbow.no_arrows", "Consumes no arrows.");
      this.add("tooltip.hexalia.thornbow.bleeding", "Thorn shots inflict bleeding.");
      this.add("tooltip.hexalia.magic_resistance", "+%s Magic Resistance");
      this.add("tooltip.hexalia.magic_resistance_full_set", "Full Set Bonus: +%s Magic Resistance");
      this.add("tooltip.hexalia.magic_resist_piece", "Magic Resistance: +%s");
      this.add("tooltip.hexalia.magic_resist_full_set", "Full Set: +%s Magic Resistance");
      this.add("tooltip.hexalia.spiritroot_tether", "Bound Spirit: %s");
      this.add("tooltip.hexalia.spiritroot_tether.bound", "Bound to: %s, %s, %s");
      this.add("tooltip.hexalia.spiritroot_tether.unknown", "Unknown");
      this.add("tooltip.hexalia.rootshaper.mode_3x3_active", "3×3 Mining: Active");
      this.add("tooltip.hexalia.rootshaper.mode_3x3_hint", "Sneak while mining to enable 3×3 mode");
   }

   private void addMessageTranslations() {
      this.add("message.hexalia.ritual.invalid_crops", "Natural energy is weak; fully grown crops are missing.");
      this.add("message.hexalia.natures_ritual.invalid_crops", "Natural energy is weak; fully grown crops are missing.");
      this.add("message.hexalia.natures_ritual.missing_ingredients", "The ritual table lacks its central ingredient.");
      this.add("message.hexalia.ritual.missing_salt", "The braziers lack the salt to channel energy.");
      this.add("message.hexalia.natures_ritual.missing_salt", "The braziers lack the salt to channel energy.");
      this.add("message.hexalia.ritual.wrong_recipe", "The ritual ingredients resonate incorrectly.");
      this.add("message.hexalia.natures_ritual.wrong_recipe", "The ritual ingredients resonate incorrectly.");
      this.add("message.hexalia.ritual.stopped_ritual", "The ritual falters as an item is removed.");
      this.add("message.hexalia.natures_ritual.stopped_ritual", "The ritual falters as an item is removed.");
      this.add("message.hexalia.rainfall_idol", "The skies darken as rain begins to fall...");
      this.add("message.hexalia.clarity_idol", "The clouds part, revealing a clear sky...");
      this.add("message.hexalia.tempest_idol", "The skies roar as thunder begins to clash...");
      this.add("message.hexalia.astrylis.activation", "Celestial energy seeps into the crops and saplings, charging them softly.");
      this.add("message.hexalia.astrylis.inactive", "A Celestial Crystal is required to activate the plant.");
      this.add("message.hexalia.ritual_brazier.invalid_item", "The item placed on the brazier cannot be imbued.");
      this.add("message.hexalia.ritual_brazier.no_celestial_blooms", "There are not enough Celestial Blooms nearby.");
      this.add("message.hexalia.ritual_brazier.no_sky", "The infusion requires an open view of the sky.");
      this.add("message.hexalia.celestial_infusion.invalid_item", "The item placed on the brazier cannot be infused.");
      this.add("message.hexalia.celestial_infusion.no_celestial_blooms", "There are not enough Celestial Blooms nearby.");
      this.add("message.hexalia.celestial_infusion.no_sky", "The infusion requires an open view of the sky.");
      this.add("message.hexalia.censer_not_full", "The censer must be filled with herbs before lighting!");
      this.add("message.hexalia.invalid_herb_combination", "This combination of herbs does not work.");
      this.add("message.hexalia.censer.tidewarden", "A gentle tide lifts the spirit, carrying it beyond harm");
      this.add("message.hexalia.censer.undead_veil", "Unnatural stillness quiets the restless dead");
      this.add("message.hexalia.censer.ethereal_grazing", "A pale tranquility settles over the herd, and life renews itself without need");
      this.add("message.hexalia.censer.miners_respite", "Metals whisper secrets of perfect balance");
      this.add("message.hexalia.censer.tides_memory", "The water stirs with old memories, offering what the tide once carried");
      this.add("message.hexalia.censer.phantom_drift", "Objects waver like ghosts, vanishing only to reappear where they belong");
      this.add("message.hexalia.censer.withering_calm", "A quiet malaise settles over the land, and even decay feels strangely serene");
      this.add("message.hexalia.censer.hollow_aura", "A hollow hush devours vitality, and blessings falter in its presence");
      this.add("message.hexalia.censer.blighted_bloom", "A blighted bloom seeps into the soil, and fungus quietly takes hold");
      this.add("message.hexalia.censer.tidal_pull", "A heavy current swirls around the censer, drawing bodies and burdens inward");
      this.add("message.hexalia.invalid_item", "This item is not valid for this block.");
      this.add("message.hexalia.patchouli_missing", "Patchouli is required to open the Verdant Grimoire.");
      this.add("message.hexalia.lourdes.activation", "The Lourdes flower blooms with purifying warmth.");
      this.add("message.hexalia.lourdes.inactive", "The flower requires a Lotus Blossom to activate.");
      this.add("message.hexalia.aegiflora.prevented", "Aegiflora drinks the blast before it can take form.");
      this.add("message.hexalia.aegiflora.prevented.withered", "Aegiflora drinks the blast, and its petals wither in sacrifice.");
      this.add("message.hexalia.aegiflora.prevented.dead", "Aegiflora drinks the last blast and crumbles into silence.");
      this.add("message.hexalia.spiritroot_tether.bound", "Tether bound.");
      this.add("message.hexalia.spiritroot_tether.invalid_bind_block", "This block cannot be used as a Spiritroot anchor.");
      this.add("message.hexalia.spiritroot_tether.captured", "Spirit bound.");
      this.add("message.hexalia.spiritroot_tether.already_occupied", "The tether is already bound to a spirit.");
      this.add("message.hexalia.spiritroot_tether.not_bound", "The tether is not bound to a location.");
      this.add("message.hexalia.spiritroot_tether.invalid_dimension", "That location cannot be reached.");
      this.add("message.hexalia.spiritroot_tether.recall_failed", "The tether fails to recall the spirit.");
      this.add("message.hexalia.spiritroot_tether.sent_to_anchor", "Spirit returned to its anchor.");
      this.add("message.hexalia.spiritroot_tether.released", "Spirit released.");
      this.add("message.hexalia.spiritroot_tether.cannot_capture", "This spirit cannot be bound.");
      this.add("message.hexalia.cacofey.stay", "Cacofey is staying put.");
      this.add("message.hexalia.cacofey.follow", "Cacofey is following you.");
      this.add("message.hexalia.cacofey.wander", "Cacofey is wandering.");
      this.add("message.hexalia.cacofey.attuned", "Hex Focus attuned to %s - right-click a container.");
      this.add("message.hexalia.cacofey.anchored", "%s will harvest crops around this container.");
      this.add("message.hexalia.cacofey.invalid_container", "That's not a valid container.");
      this.add("message.hexalia.dreamcatcher_full", "The dreamcatcher cannot hold any more fuel.");
   }

   private void addAdvancementTranslations() {
      this.add("advancements.hexalia.root.title", "Hexalia");
      this.add("advancements.hexalia.root.description", "Obtain a Hex Focus.");
      this.add("advancements.hexalia.salt_of_the_craft.title", "Salt of the Craft");
      this.add("advancements.hexalia.salt_of_the_craft.description", "Obtain Salt, the basis of all rituals.");
      this.add("advancements.hexalia.crush_course.title", "Crush Course");
      this.add("advancements.hexalia.crush_course.description", "Obtain a Mortar & Pestle to crush herbs.");
      this.add("advancements.hexalia.knife_to_tree_you.title", "Bark & Dagger");
      this.add("advancements.hexalia.knife_to_tree_you.description", "Obtain an Athame to collect Resin or Lotus Blossoms.");
      this.add("advancements.hexalia.table_manners.title", "Ritual Etiquette");
      this.add("advancements.hexalia.table_manners.description", "Set the table... for the spirits.");
      this.add("advancements.hexalia.star_power.title", "Starry, Starry Might");
      this.add("advancements.hexalia.star_power.description", "Perform a Celestial Infusion to obtain a Celestial Crystal.");
      this.add("advancements.hexalia.essence_collector.title", "Elemental, My Dear");
      this.add("advancements.hexalia.essence_collector.description", "Obtain all of the Elemental Nodes.");
      this.add("advancements.hexalia.change_of_plans.title", "Crafty Mutations");
      this.add("advancements.hexalia.change_of_plans.description", "Obtain a Mutavis and rewrite nature's rules.");
      this.add("advancements.hexalia.ring_of_change.title", "Crop Circles");
      this.add("advancements.hexalia.ring_of_change.description", "Obtain a Morphora and watch the land rearrange itself.");
      this.add("advancements.hexalia.small_beginnings.title", "Cauldron Calamity");
      this.add("advancements.hexalia.small_beginnings.description", "Craft a Small Cauldron and stir up some trouble.");
      this.add("advancements.hexalia.brewbie_award.title", "First Sip's the Deepest");
      this.add("advancements.hexalia.brewbie_award.description", "Obtain a Rustic Bottle to start brewing.");
      this.add("advancements.hexalia.powder_and_pouch.title", "Bag of Tricks");
      this.add("advancements.hexalia.powder_and_pouch.description", "Craft a throwable Foul Sac. Kaboom, but make it floral.");
      this.add("advancements.hexalia.silken_beginnings.title", "Thread Lightly");
      this.add("advancements.hexalia.silken_beginnings.description", "Weave together your first Silk Idol.");
      this.add("advancements.hexalia.pure_intentions.title", "Exorcise in Style");
      this.add("advancements.hexalia.pure_intentions.description", "Use a Purity Idol to remove curses from enchanted items.");
      this.add("advancements.hexalia.kelp_yourself.title", "Sea What You Did There");
      this.add("advancements.hexalia.kelp_yourself.description", "Obtain the Kelpweave Blade - kelp your enemies!");
      this.add("advancements.hexalia.wise_investment.title", "Pendant Pending");
      this.add("advancements.hexalia.wise_investment.description", "Obtain the Sage Pendant and profit in XP.");
      this.add("advancements.hexalia.herb_nerd.title", "Leaf It to Me");
      this.add("advancements.hexalia.herb_nerd.description", "Collect every Magical Herb out there.");
      this.add("advancements.hexalia.seasoned_farmer.title", "Crop Top");
      this.add("advancements.hexalia.seasoned_farmer.description", "Harvest the full lineup of Magical Crops.");
      this.add("advancements.hexalia.master_or_not.title", "Weather or Not");
      this.add("advancements.hexalia.master_or_not.description", "Use a Rainfall, Clarity, and Tempest Idol. Forecast: spooky.");
      this.add("advancements.hexalia.brewed_awakening.title", "Espresso Patronum");
      this.add("advancements.hexalia.brewed_awakening.description", "Drink every Hexalia Brew - magic beans not included.");
   }

   private void addEffectTranslations() {
      this.add((MobEffect)ModMobEffects.BLOODLUST.get(), "Bloodlust");
      this.add(
         ((MobEffect)ModMobEffects.BLOODLUST.get()).getDescriptionId() + ".description",
         "Increases strength and restores a portion of damage dealt to foes, but the Regeneration effect is disabled."
      );
      this.add((MobEffect)ModMobEffects.STUNNED.get(), "Stunned");
      this.add(
         ((MobEffect)ModMobEffects.STUNNED.get()).getDescriptionId() + ".description", "Immobilizes the target, preventing movement for a short duration."
      );
      this.add((MobEffect)ModMobEffects.OVERFED.get(), "Overfed");
      this.add(
         ((MobEffect)ModMobEffects.OVERFED.get()).getDescriptionId() + ".description",
         "Prevents saturation from decreasing, but reduces movement speed while active."
      );
      this.add((MobEffect)ModMobEffects.SLIMEWALKER.get(), "Slimewalker");
      this.add(
         ((MobEffect)ModMobEffects.SLIMEWALKER.get()).getDescriptionId() + ".description",
         "Negates fall damage and causes the user to bounce when landing. While grounded, movement is slowed."
      );
      this.add((MobEffect)ModMobEffects.SPIKESKIN.get(), "Spikeskin");
      this.add(
         ((MobEffect)ModMobEffects.SPIKESKIN.get()).getDescriptionId() + ".description",
         "Increases armor and reflects a portion of incoming damage, but reduces movement speed."
      );
      this.add((MobEffect)ModMobEffects.SIPHON.get(), "Siphon");
      this.add(
         ((MobEffect)ModMobEffects.SIPHON.get()).getDescriptionId() + ".description",
         "Increases mining speed and attracts nearby items, but breaking blocks increases exhaustion."
      );
      this.add((MobEffect)ModMobEffects.BLEEDING.get(), "Bleeding");
      this.add(((MobEffect)ModMobEffects.BLEEDING.get()).getDescriptionId() + ".description", "Deals damage over time, similar to Poison.");
      this.add((MobEffect)ModMobEffects.DAYBLOOM.get(), "Daybloom");
      this.add(
         ((MobEffect)ModMobEffects.DAYBLOOM.get()).getDescriptionId() + ".description",
         "Grants regeneration and increased speed during daylight, but darkness harms the user."
      );
      this.add((MobEffect)ModMobEffects.ARACHNID_GRACE.get(), "Arachnid Grace");
      this.add(
         ((MobEffect)ModMobEffects.ARACHNID_GRACE.get()).getDescriptionId() + ".description",
         "Allows vertical wall climbing and grants poison immunity. Cobwebs do not slow the user, but water weakens them."
      );
      this.add((MobEffect)ModMobEffects.BRAMBLEGUARD.get(), "Brambleguard");
      this.add(
         ((MobEffect)ModMobEffects.BRAMBLEGUARD.get()).getDescriptionId() + ".description",
         "Grants increased magical and physical resistance per level. Removes and prevents Bleeding while active."
      );
      this.add((MobEffect)ModMobEffects.HOLLOW_SILENCE.get(), "Hollow Silence");
      this.add(
         ((MobEffect)ModMobEffects.HOLLOW_SILENCE.get()).getDescriptionId() + ".description",
         "Silences the user's presence, allowing safe movement near sound-sensitive entities, but periodically clouds vision."
      );
   }

   private void addJeiTranslations() {
      this.add("jei.hexalia.category.mortar_and_pestle", "Mortar & Pestle");
      this.add("jei.hexalia.category.small_cauldron", "Small Cauldron Brewing");
      this.add("jei.hexalia.category.natures_ritual", "Nature's Ritual");
      this.add("jei.hexalia.category.celestial_infusion", "Celestial Infusion");
      this.add("jei.hexalia.category.mutation", "Mutation");
      this.add("jei.hexalia.tooltip.brew_time", "Brew Time: %s ticks");
      this.add("jei.hexalia.tooltip.experience", "Experience: %s");
      this.add("jei.hexalia.tooltip.requires_hex_focus", "Requires activation with a Hex Focus.");
      this.add("jei.hexalia.tooltip.requires_salted_brazier", "Requires a salted Ritual Brazier.");
      this.add("jei.hexalia.tooltip.requires_salted_braziers", "Used Ritual Braziers must be salted.");
      this.add("jei.hexalia.tooltip.requires_celestial_blooms", "Requires nearby Celestial Blooms.");
      this.add("jei.hexalia.tooltip.requires_open_sky", "Requires an open view of the sky.");
      this.add("jei.hexalia.tooltip.requires_mature_crops", "Requires mature crops nearby.");
      this.add("jei.info.wild_sunfire_tomatoes", "Wild Sunfire Tomatoes can be found in savannas.");
      this.add("jei.info.wild_mandrakes", "Wild Mandrakes can be found in forests.");
      this.add("jei.info.chillberry_bushes", "Chillberries can be found in taigas.");
      this.add("jei.info.ritual_brazier", "Can be obtained after right-clicking any planks block with an Athame while crouching.");
      this.add("jei.info.ritual_table", "Can be obtained after right-clicking Deepslate or Cobbled Deepslate with a Hex Focus.");
      this.add("jei.info.lotus_blossom", "Can be obtained after right-clicking Lotus Flowers with an Athame.");
      this.add("jei.info.tree_resin", "Can be obtained after right-clicking Cottonwood Logs or Dark Oak Logs with an Athame.");
      this.add("jei.info.silk_fiber", "Can be obtained after breeding Silk Moths.");
   }

   private void addEmiTranslations() {
      this.add("emi.category.hexalia.mutation", "Mutation");
      this.add("emi.category.hexalia.mortar_and_pestle", "Mortar and Pestle");
      this.add("emi.category.hexalia.ritual_brazier", "Ritual Brazier");
      this.add("emi.category.hexalia.ritual_table", "Ritual Table");
      this.add("emi.category.hexalia.small_cauldron", "Small Cauldron");
   }

   private void addTagTranslations() {
      this.add("tag.item.hexalia.herbs", "Herbs");
      this.add("tag.item.hexalia.crushed_herbs", "Crushed Herbs");
      this.add("tag.item.hexalia.brews", "Brews");
      this.add("tag.item.hexalia.cottonwood_logs", "Cottonwood Logs");
      this.add("tag.item.hexalia.willow_logs", "Willow Logs");
      this.add("tag.item.hexalia.offhand_equipment", "Offhand Equipment");
      this.add("tag.item.hexalia.tulips", "Tulips");
      this.add("tag.item.hexalia.stun_immune_headwear", "Stun-Immune Headwear");
      this.add("tag.item.c.foods", "Foods");
      this.add("tag.item.c.foods.bread", "Bread");
      this.add("tag.item.c.crops", "Crops");
      this.add("tag.item.c.foods.berry", "Berries");
      this.add("tag.item.c.foods.cooked_meat", "Cooked Meats");
      this.add("tag.item.c.foods.soup", "Soups");
      this.add("tag.item.c.foods.pie", "Pies");
      this.add("tag.item.c.foods.food_poisoning", "Food Poisoning Foods");
      this.add("tag.item.c.foods.vegetable", "Vegetables");
      this.add("tag.item.c.salt", "Salt");
      this.add("tag.item.c.seeds", "Seeds");
      this.add("tag.item.c.mushrooms", "Mushrooms");
      this.add("tag.item.c.salt_blocks", "Salt Blocks");
      this.add("tag.item.sereneseasons.autumn_crops", "Autumn Crops");
      this.add("tag.item.sereneseasons.spring_crops", "Spring Crops");
      this.add("tag.item.sereneseasons.summer_crops", "Summer Crops");
      this.add("tag.item.sereneseasons.winter_crops", "Winter Crops");
      this.add("tag.block.hexalia.heating_block", "Heating Blocks");
      this.add("tag.block.hexalia.attracts_moth", "Attracts Moths");
      this.add("tag.block.hexalia.cottonwood_logs", "Cottonwood Logs");
      this.add("tag.block.hexalia.willow_logs", "Willow Logs");
      this.add("tag.block.hexalia.spiritroot_bound_blocks", "Spiritroot Bound Blocks");
      this.add("tag.block.hexalia.bogshade_no_slow", "Bogshade No-Slow Blocks");
      this.add("tag.block.c.salt_blocks", "Salt Blocks");
      this.add("tag.block.sereneseasons.autumn_crops", "Autumn Crops");
      this.add("tag.block.sereneseasons.spring_crops", "Spring Crops");
      this.add("tag.block.sereneseasons.summer_crops", "Summer Crops");
      this.add("tag.block.sereneseasons.winter_crops", "Winter Crops");
      this.add("tag.block.sereneseasons.unbreakable_infertile_crops", "Unbreakable Infertile Crops");
      this.add("tag.entity_type.hexalia.spiritroot_uncapturable", "Spiritroot Uncapturable");
      this.add("tag.entity_type.hexalia.undead_veil_immune", "Undead Veil Immune");
   }

   private void addMiscTranslations() {
      this.add("item.hexalia.verdant_grimoire.landing_text", "A detailed book is your trusted tool in matters of witchcraft and plant care.");
      this.add("sounds.hexalia.mandrake_scream", "Mandrake screamed");
      this.add("sounds.hexalia.ritual_success", "Ritual completed");
      this.add("sounds.hexalia.conversion", "Block converted");
      this.add("sounds.hexalia.sac_impact", "Sac hits the ground");
      this.add("sounds.hexalia.cacofey_giggle", "Cacofey giggling");
   }
}
