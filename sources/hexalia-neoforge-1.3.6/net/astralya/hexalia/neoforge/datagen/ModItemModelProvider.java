package net.astralya.hexalia.neoforge.datagen;

import net.astralya.hexalia.item.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public final class ModItemModelProvider extends ItemModelProvider {
   public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
      super(output, "hexalia", existingFileHelper);
   }

   protected void registerModels() {
      this.basicItem((Item)ModItems.HEX_FOCUS.get());
      this.basicItem((Item)ModItems.SALT.get());
      this.basicItem((Item)ModItems.TREE_RESIN.get());
      this.basicItem((Item)ModItems.SILK_FIBER.get());
      this.basicItem((Item)ModItems.SILKWORM.get());
      this.basicItem((Item)ModItems.FRAGRANT_NECTAR.get());
      this.basicItem((Item)ModItems.ANCIENT_SEED.get());
      this.basicItem((Item)ModItems.SIREN_PASTE.get());
      this.basicItem((Item)ModItems.DREAM_PASTE.get());
      this.basicItem((Item)ModItems.CELESTIAL_CRYSTAL.get());
      this.basicItem((Item)ModItems.VERDANT_GRIMOIRE.get());
      this.basicItem((Item)ModItems.FIRE_NODE.get());
      this.basicItem((Item)ModItems.WATER_NODE.get());
      this.basicItem((Item)ModItems.AIR_NODE.get());
      this.basicItem((Item)ModItems.EARTH_NODE.get());
      this.basicItem((Item)ModItems.LADLE.get());
      this.handheldItem((Item)ModItems.ATHAME.get());
      this.handheldItem((Item)ModItems.KELPWEAVE_BLADE.get());
      this.handheldItem((Item)ModItems.BRIAR_SICKLE.get());
      this.basicItem((Item)ModItems.SAGE_PENDANT.get());
      this.basicItem((Item)ModItems.SILK_MOTH_SPAWN_EGG.get());
      this.basicItem((Item)ModItems.CACOFEY_SPAWN_EGG.get());
      this.basicItem((Item)ModItems.GHOSTVEIL.get());
      this.basicItem((Item)ModItems.EARPLUGS.get());
      this.basicItem((Item)ModItems.BOGSHADE_BOOTS.get());
      this.basicItem((Item)ModItems.SILKWEAVE_HOOD.get());
      this.basicItem((Item)ModItems.SILKWEAVE_MANTLE.get());
      this.basicItem((Item)ModItems.SILKWEAVE_BINDINGS.get());
      this.basicItem((Item)ModItems.SILKWEAVE_FOOTWRAPS.get());
      this.basicItem((Item)ModItems.MOONWEAVE_HOOD.get());
      this.basicItem((Item)ModItems.MOONWEAVE_MANTLE.get());
      this.basicItem((Item)ModItems.MOONWEAVE_BINDINGS.get());
      this.basicItem((Item)ModItems.MOONWEAVE_FOOTWRAPS.get());
      this.basicItem((Item)ModItems.BLOOMWRAP_HAT.get());
      this.basicItem((Item)ModItems.BLOOMWRAP_ROBES.get());
      this.basicItem((Item)ModItems.BLOOMWRAP_LEGGINGS.get());
      this.basicItem((Item)ModItems.BLOOMWRAP_BOOTS.get());
      this.basicItem((Item)ModItems.SILK_IDOL.get());
      this.basicItem((Item)ModItems.CLARITY_IDOL.get());
      this.basicItem((Item)ModItems.RAINFALL_IDOL.get());
      this.basicItem((Item)ModItems.TEMPEST_IDOL.get());
      this.basicItem((Item)ModItems.PURITY_IDOL.get());
      this.basicItem((Item)ModItems.RUSTIC_BOTTLE.get());
      this.basicItem((Item)ModItems.BREW_OF_SPIKESKIN.get());
      this.basicItem((Item)ModItems.BREW_OF_BLOODLUST.get());
      this.basicItem((Item)ModItems.BREW_OF_SLIMEWALKER.get());
      this.basicItem((Item)ModItems.BREW_OF_SIPHON.get());
      this.basicItem((Item)ModItems.BREW_OF_DAYBLOOM.get());
      this.basicItem((Item)ModItems.BREW_OF_ARACHNID_GRACE.get());
      this.basicItem((Item)ModItems.BREW_OF_HOMESTEAD.get());
      this.basicItem((Item)ModItems.BREW_OF_HOLLOW_SILENCE.get());
      this.basicItem((Item)ModItems.BRAMBLEGUARD_SALVE.get());
      this.basicItem((Item)ModItems.MENDERS_SALVE.get());
      this.basicItem((Item)ModItems.LOTUS_BLOSSOM.get());
      this.basicItem((Item)ModItems.SPIRIT_POWDER.get());
      this.basicItem((Item)ModItems.GHOST_POWDER.get());
      this.basicItem((Item)ModItems.LOTUS_FLOWER.get());
      this.basicItem((Item)ModItems.SIREN_KELP.get());
      this.blockTextureItem((Item)ModItems.MORPHORA.get());
      this.blockTextureItem((Item)ModItems.GRIMSHADE.get());
      this.basicItem((Item)ModItems.NAUTILITE.get());
      this.blockTextureItem((Item)ModItems.WINDSONG.get());
      this.blockTextureItem((Item)ModItems.ASTRYLIS.get());
      this.blockTextureItem((Item)ModItems.LOURDES.get());
      this.blockTextureItem((Item)ModItems.AEGIFLORA.get());
      this.blockTextureItem((Item)ModItems.WITHERED_AEGIFLORA.get());
      this.blockTextureItem((Item)ModItems.BEGONIA.get());
      this.blockTextureItem((Item)ModItems.LAVENDER.get());
      this.blockTextureItem((Item)ModItems.DAHLIA.get());
      this.blockTextureItem((Item)ModItems.NIGHTSHADE_BUSH.get());
      this.blockTextureItem((Item)ModItems.SPIRIT_BLOOM.get());
      this.blockTextureItem((Item)ModItems.DREAMSHROOM.get());
      this.blockTextureItem((Item)ModItems.GHOST_FERN.get());
      this.blockTextureItem((Item)ModItems.CELESTIAL_BLOOM.get());
      this.blockTextureItem((Item)ModItems.WITCHWEED.get());
      this.blockTextureItem((Item)ModItems.WITHERED_CELESTIAL_BLOOM.get());
      this.basicItem((Item)ModItems.MUTAVIS.get());
      this.basicItem((Item)ModItems.MORTAR_AND_PESTLE.get());
      this.basicItem((Item)ModItems.MANDRAKE.get());
      this.basicItem((Item)ModItems.MANDRAKE_SEEDS.get());
      this.basicItem((Item)ModItems.SUNFIRE_TOMATO.get());
      this.basicItem((Item)ModItems.SUNFIRE_TOMATO_SEEDS.get());
      this.basicItem((Item)ModItems.RABBAGE.get());
      this.basicItem((Item)ModItems.RABBAGE_SEEDS.get());
      this.basicItem((Item)ModItems.PURIFYING_SAC.get());
      this.basicItem((Item)ModItems.FOUL_SAC.get());
      this.basicItem((Item)ModItems.FROST_SAC.get());
      this.basicItem((Item)ModItems.SEARING_SAC.get());
      this.basicItem((Item)ModItems.CHILLBERRIES.get());
      this.basicItem((Item)ModItems.GALEBERRIES.get());
      this.basicItem((Item)ModItems.SPICY_SANDWICH.get());
      this.basicItem((Item)ModItems.CHILLBERRY_PIE.get());
      this.basicItem((Item)ModItems.MANDRAKE_STEW.get());
      this.basicItem((Item)ModItems.GALEBERRIES_COOKIE.get());
      this.basicItem((Item)ModItems.COTTONWOOD_BOAT.get());
      this.basicItem((Item)ModItems.COTTONWOOD_CHEST_BOAT.get());
      this.basicItem((Item)ModItems.WILLOW_BOAT.get());
      this.basicItem((Item)ModItems.WILLOW_CHEST_BOAT.get());
      this.basicItem((Item)ModItems.SALTSPROUT.get());
      this.basicItem((Item)ModItems.SALT_LAMP.get());
      this.basicItem((Item)ModItems.CANDLE_SKULL.get());
      this.basicItem((Item)ModItems.WITHER_CANDLE_SKULL.get());
      this.blockTextureItem((Item)ModItems.COTTONWOOD_CATKIN.get());
      this.blockTextureItem((Item)ModItems.COTTONWOOD_SAPLING.get());
      this.basicItem((Item)ModItems.COTTONWOOD_DOOR.get());
      this.blockTextureItem((Item)ModItems.WILLOW_SAPLING.get());
      this.basicItem((Item)ModItems.WILLOW_DOOR.get());
   }

   private void blockTextureItem(Item item) {
      String name = BuiltInRegistries.ITEM.getKey(item).getPath();
      ((ItemModelBuilder)((ItemModelBuilder)this.getBuilder(name)).parent(this.getExistingFile(this.mcLoc("item/generated"))))
         .texture("layer0", this.modLoc("block/" + name));
   }
}
