package net.astralya.hexalia.neoforge.datagen;

import java.util.concurrent.CompletableFuture;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.util.ModTags;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider.TagLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public final class ModItemTagProvider extends ItemTagsProvider {
   public ModItemTagProvider(
      PackOutput output, CompletableFuture<Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper existingFileHelper
   ) {
      super(output, lookupProvider, blockTags, "hexalia", existingFileHelper);
   }

   protected void addTags(Provider lookupProvider) {
      this.tag(ItemTags.FLOWERS)
         .add(((Block)ModBlocks.SPIRIT_BLOOM.get()).asItem())
         .add(((Block)ModBlocks.WITCHWEED.get()).asItem())
         .add(((Block)ModBlocks.GHOST_FERN.get()).asItem())
         .add(((Block)ModBlocks.CELESTIAL_BLOOM.get()).asItem())
         .add(((Block)ModBlocks.WITHERED_CELESTIAL_BLOOM.get()).asItem())
         .add(((Block)ModBlocks.MORPHORA.get()).asItem())
         .add(((Block)ModBlocks.GRIMSHADE.get()).asItem())
         .add(((Block)ModBlocks.NAUTILITE.get()).asItem())
         .add(((Block)ModBlocks.WINDSONG.get()).asItem())
         .add(((Block)ModBlocks.ASTRYLIS.get()).asItem())
         .add(((Block)ModBlocks.LOURDES.get()).asItem())
         .add(((Block)ModBlocks.AEGIFLORA.get()).asItem())
         .add(((Block)ModBlocks.WITHERED_AEGIFLORA.get()).asItem())
         .add(((Block)ModBlocks.NIGHTSHADE_BUSH.get()).asItem())
         .add(((Block)ModBlocks.BEGONIA.get()).asItem())
         .add(((Block)ModBlocks.LAVENDER.get()).asItem())
         .add(((Block)ModBlocks.DAHLIA.get()).asItem());
      this.tag(ModTags.Items.COTTONWOOD_LOGS)
         .add(
            new Item[]{
               (Item)ModItems.COTTONWOOD_LOG.get(),
               (Item)ModItems.STRIPPED_COTTONWOOD_LOG.get(),
               (Item)ModItems.COTTONWOOD_WOOD.get(),
               (Item)ModItems.STRIPPED_COTTONWOOD_WOOD.get()
            }
         );
      this.tag(ModTags.Items.WILLOW_LOGS)
         .add(
            new Item[]{
               (Item)ModItems.WILLOW_LOG.get(),
               (Item)ModItems.STRIPPED_WILLOW_LOG.get(),
               (Item)ModItems.WILLOW_WOOD.get(),
               (Item)ModItems.STRIPPED_WILLOW_WOOD.get()
            }
         );
      this.tag(ItemTags.LOGS_THAT_BURN).addTag(ModTags.Items.COTTONWOOD_LOGS).addTag(ModTags.Items.WILLOW_LOGS);
      this.tag(ItemTags.PLANKS).add(new Item[]{(Item)ModItems.COTTONWOOD_PLANKS.get(), (Item)ModItems.WILLOW_PLANKS.get()});
      this.tag(ItemTags.WOODEN_STAIRS).add(new Item[]{(Item)ModItems.COTTONWOOD_STAIRS.get(), (Item)ModItems.WILLOW_STAIRS.get()});
      this.tag(ItemTags.WOODEN_SLABS).add(new Item[]{(Item)ModItems.COTTONWOOD_SLAB.get(), (Item)ModItems.WILLOW_SLAB.get()});
      this.tag(ItemTags.WOODEN_BUTTONS).add(new Item[]{(Item)ModItems.COTTONWOOD_BUTTON.get(), (Item)ModItems.WILLOW_BUTTON.get()});
      this.tag(ItemTags.WOODEN_PRESSURE_PLATES).add(new Item[]{(Item)ModItems.COTTONWOOD_PRESSURE_PLATE.get(), (Item)ModItems.WILLOW_PRESSURE_PLATE.get()});
      this.tag(ItemTags.WOODEN_FENCES).add(new Item[]{(Item)ModItems.COTTONWOOD_FENCE.get(), (Item)ModItems.WILLOW_FENCE.get()});
      this.tag(ItemTags.FENCE_GATES).add(new Item[]{(Item)ModItems.COTTONWOOD_FENCE_GATE.get(), (Item)ModItems.WILLOW_FENCE_GATE.get()});
      this.tag(ItemTags.WOODEN_DOORS).add(new Item[]{(Item)ModItems.COTTONWOOD_DOOR.get(), (Item)ModItems.WILLOW_DOOR.get()});
      this.tag(ItemTags.WOODEN_TRAPDOORS).add(new Item[]{(Item)ModItems.COTTONWOOD_TRAPDOOR.get(), (Item)ModItems.WILLOW_TRAPDOOR.get()});
      this.tag(ItemTags.SAPLINGS).add(new Item[]{(Item)ModItems.COTTONWOOD_SAPLING.get(), (Item)ModItems.WILLOW_SAPLING.get()});
      this.tag(ItemTags.LEAVES).add(new Item[]{(Item)ModItems.COTTONWOOD_LEAVES.get(), (Item)ModItems.WILLOW_LEAVES.get()});
      this.tag(ItemTags.SIGNS).add(new Item[]{(Item)ModItems.COTTONWOOD_SIGN.get(), (Item)ModItems.WILLOW_SIGN.get()});
      this.tag(ItemTags.HANGING_SIGNS).add(new Item[]{(Item)ModItems.COTTONWOOD_HANGING_SIGN.get(), (Item)ModItems.WILLOW_HANGING_SIGN.get()});
      this.tag(ItemTags.BOATS).add(new Item[]{(Item)ModItems.COTTONWOOD_BOAT.get(), (Item)ModItems.WILLOW_BOAT.get()});
      this.tag(ItemTags.CHEST_BOATS).add(new Item[]{(Item)ModItems.COTTONWOOD_CHEST_BOAT.get(), (Item)ModItems.WILLOW_CHEST_BOAT.get()});
      this.tag(ItemTags.SMALL_FLOWERS)
         .add(((Block)ModBlocks.SPIRIT_BLOOM.get()).asItem())
         .add(((Block)ModBlocks.WITCHWEED.get()).asItem())
         .add(((Block)ModBlocks.GHOST_FERN.get()).asItem())
         .add(((Block)ModBlocks.CELESTIAL_BLOOM.get()).asItem())
         .add(((Block)ModBlocks.WITHERED_CELESTIAL_BLOOM.get()).asItem())
         .add(((Block)ModBlocks.MORPHORA.get()).asItem())
         .add(((Block)ModBlocks.GRIMSHADE.get()).asItem())
         .add(((Block)ModBlocks.NAUTILITE.get()).asItem())
         .add(((Block)ModBlocks.WINDSONG.get()).asItem())
         .add(((Block)ModBlocks.ASTRYLIS.get()).asItem())
         .add(((Block)ModBlocks.LOURDES.get()).asItem())
         .add(((Block)ModBlocks.AEGIFLORA.get()).asItem())
         .add(((Block)ModBlocks.WITHERED_AEGIFLORA.get()).asItem())
         .add(((Block)ModBlocks.NIGHTSHADE_BUSH.get()).asItem())
         .add(((Block)ModBlocks.BEGONIA.get()).asItem())
         .add(((Block)ModBlocks.LAVENDER.get()).asItem())
         .add(((Block)ModBlocks.DAHLIA.get()).asItem());
      this.tag(ItemTags.VILLAGER_PLANTABLE_SEEDS)
         .add((Item)ModItems.RABBAGE_SEEDS.get())
         .add((Item)ModItems.SUNFIRE_TOMATO_SEEDS.get())
         .add((Item)ModItems.MANDRAKE_SEEDS.get());
      this.tag(ItemTags.SHOVELS).add((Item)ModItems.ROOTSHAPER.get());
      this.tag(ItemTags.PICKAXES).add((Item)ModItems.ROOTSHAPER.get());
      this.tag(ItemTags.SWORDS).add((Item)ModItems.KELPWEAVE_BLADE.get());
      this.tag(ItemTags.HEAD_ARMOR)
         .add((Item)ModItems.EARPLUGS.get())
         .add((Item)ModItems.SILKWEAVE_HOOD.get())
         .add((Item)ModItems.MOONWEAVE_HOOD.get())
         .add((Item)ModItems.BLOOMWRAP_HAT.get());
      this.tag(ItemTags.CHEST_ARMOR)
         .add((Item)ModItems.GHOSTVEIL.get())
         .add((Item)ModItems.SILKWEAVE_MANTLE.get())
         .add((Item)ModItems.MOONWEAVE_MANTLE.get())
         .add((Item)ModItems.BLOOMWRAP_ROBES.get());
      this.tag(ItemTags.LEG_ARMOR)
         .add((Item)ModItems.SILKWEAVE_BINDINGS.get())
         .add((Item)ModItems.MOONWEAVE_BINDINGS.get())
         .add((Item)ModItems.BLOOMWRAP_LEGGINGS.get());
      this.tag(ItemTags.FOOT_ARMOR)
         .add((Item)ModItems.BOGSHADE_BOOTS.get())
         .add((Item)ModItems.SILKWEAVE_FOOTWRAPS.get())
         .add((Item)ModItems.MOONWEAVE_FOOTWRAPS.get())
         .add((Item)ModItems.BLOOMWRAP_BOOTS.get());
      this.tag(ItemTags.MINING_ENCHANTABLE).add((Item)ModItems.ROOTSHAPER.get()).add((Item)ModItems.BRIAR_SICKLE.get());
      this.tag(ItemTags.DURABILITY_ENCHANTABLE)
         .add((Item)ModItems.ATHAME.get())
         .add((Item)ModItems.ROOTSHAPER.get())
         .add((Item)ModItems.KELPWEAVE_BLADE.get())
         .add((Item)ModItems.BRIAR_SICKLE.get())
         .add((Item)ModItems.SPIRITROOT_TETHER.get())
         .add((Item)ModItems.SAGE_PENDANT.get())
         .add((Item)ModItems.PURIFYING_SAC.get())
         .add((Item)ModItems.THORNBOW.get())
         .add((Item)ModItems.EARPLUGS.get())
         .add((Item)ModItems.GHOSTVEIL.get())
         .add((Item)ModItems.BOGSHADE_BOOTS.get())
         .add((Item)ModItems.SILKWEAVE_HOOD.get())
         .add((Item)ModItems.SILKWEAVE_MANTLE.get())
         .add((Item)ModItems.SILKWEAVE_BINDINGS.get())
         .add((Item)ModItems.SILKWEAVE_FOOTWRAPS.get())
         .add((Item)ModItems.MOONWEAVE_HOOD.get())
         .add((Item)ModItems.MOONWEAVE_MANTLE.get())
         .add((Item)ModItems.MOONWEAVE_BINDINGS.get())
         .add((Item)ModItems.MOONWEAVE_FOOTWRAPS.get())
         .add((Item)ModItems.BLOOMWRAP_HAT.get())
         .add((Item)ModItems.BLOOMWRAP_ROBES.get())
         .add((Item)ModItems.BLOOMWRAP_LEGGINGS.get())
         .add((Item)ModItems.BLOOMWRAP_BOOTS.get());
      this.tag(ItemTags.SWORD_ENCHANTABLE).add((Item)ModItems.KELPWEAVE_BLADE.get());
      this.tag(ItemTags.BOW_ENCHANTABLE).add((Item)ModItems.THORNBOW.get());
      this.tag(ItemTags.HEAD_ARMOR_ENCHANTABLE)
         .add((Item)ModItems.EARPLUGS.get())
         .add((Item)ModItems.SILKWEAVE_HOOD.get())
         .add((Item)ModItems.MOONWEAVE_HOOD.get())
         .add((Item)ModItems.BLOOMWRAP_HAT.get());
      this.tag(ModTags.Items.STUN_IMMUNE_HEADWEAR).add((Item)ModItems.EARPLUGS.get()).add((Item)ModItems.BLOOMWRAP_HAT.get());
      this.tag(ItemTags.CHEST_ARMOR_ENCHANTABLE)
         .add((Item)ModItems.GHOSTVEIL.get())
         .add((Item)ModItems.SILKWEAVE_MANTLE.get())
         .add((Item)ModItems.MOONWEAVE_MANTLE.get())
         .add((Item)ModItems.BLOOMWRAP_ROBES.get());
      this.tag(ItemTags.LEG_ARMOR_ENCHANTABLE)
         .add((Item)ModItems.SILKWEAVE_BINDINGS.get())
         .add((Item)ModItems.MOONWEAVE_BINDINGS.get())
         .add((Item)ModItems.BLOOMWRAP_LEGGINGS.get());
      this.tag(ItemTags.FOOT_ARMOR_ENCHANTABLE)
         .add((Item)ModItems.BOGSHADE_BOOTS.get())
         .add((Item)ModItems.SILKWEAVE_FOOTWRAPS.get())
         .add((Item)ModItems.MOONWEAVE_FOOTWRAPS.get())
         .add((Item)ModItems.BLOOMWRAP_BOOTS.get());
      this.tag(ModTags.Items.HERBS)
         .add(((Block)ModBlocks.SPIRIT_BLOOM.get()).asItem())
         .add((Item)ModItems.SIREN_KELP.get())
         .add(((Block)ModBlocks.DREAMSHROOM.get()).asItem())
         .add(((Block)ModBlocks.GHOST_FERN.get()).asItem())
         .add(((Block)ModBlocks.WITCHWEED.get()).asItem())
         .add(((Block)ModBlocks.CELESTIAL_BLOOM.get()).asItem())
         .add(((Block)ModBlocks.MORPHORA.get()).asItem())
         .add(((Block)ModBlocks.GRIMSHADE.get()).asItem())
         .add(((Block)ModBlocks.NAUTILITE.get()).asItem())
         .add(((Block)ModBlocks.WINDSONG.get()).asItem())
         .add(((Block)ModBlocks.ASTRYLIS.get()).asItem())
         .add(((Block)ModBlocks.LOURDES.get()).asItem())
         .add(((Block)ModBlocks.AEGIFLORA.get()).asItem())
         .add(((Block)ModBlocks.WITHERED_AEGIFLORA.get()).asItem())
         .add(((Block)ModBlocks.NIGHTSHADE_BUSH.get()).asItem())
         .add(((Block)ModBlocks.BEGONIA.get()).asItem())
         .add(((Block)ModBlocks.LAVENDER.get()).asItem())
         .add(((Block)ModBlocks.DAHLIA.get()).asItem());
      this.tag(ModTags.Items.CRUSHED_HERBS)
         .add((Item)ModItems.SPIRIT_POWDER.get())
         .add((Item)ModItems.SIREN_PASTE.get())
         .add((Item)ModItems.DREAM_PASTE.get())
         .add((Item)ModItems.GHOST_POWDER.get());
      this.tag(ModTags.Items.BREWS)
         .add((Item)ModItems.BREW_OF_HOMESTEAD.get())
         .add((Item)ModItems.BREW_OF_BLOODLUST.get())
         .add((Item)ModItems.BREW_OF_SLIMEWALKER.get())
         .add((Item)ModItems.BREW_OF_SPIKESKIN.get())
         .add((Item)ModItems.BREW_OF_SIPHON.get())
         .add((Item)ModItems.BREW_OF_DAYBLOOM.get())
         .add((Item)ModItems.BREW_OF_ARACHNID_GRACE.get())
         .add((Item)ModItems.BREW_OF_HOLLOW_SILENCE.get())
         .add((Item)ModItems.RUSTIC_BOTTLE.get());
      this.tag(ModTags.Items.SALT).add((Item)ModItems.SALT.get());
      this.tag(ModTags.Items.SALT_BLOCKS).add((Item)ModItems.SALT_BLOCK.get());
      this.tag(ModTags.Items.MUSHROOMS).add(((Block)ModBlocks.DREAMSHROOM.get()).asItem()).add(((Block)ModBlocks.PALE_MUSHROOM.get()).asItem());
      this.tag(ModTags.Items.OFFHAND_EQUIPMENT)
         .add((Item)ModItems.HEX_FOCUS.get())
         .add((Item)ModItems.ATHAME.get())
         .add((Item)ModItems.SAGE_PENDANT.get())
         .addTag(ModTags.Items.SALT);
      this.tag(ModTags.Items.TULIPS)
         .add(Blocks.ORANGE_TULIP.asItem())
         .add(Blocks.PINK_TULIP.asItem())
         .add(Blocks.RED_TULIP.asItem())
         .add(Blocks.WHITE_TULIP.asItem());
      this.tag(ModTags.Items.CROPS)
         .add((Item)ModItems.MANDRAKE.get())
         .add((Item)ModItems.SUNFIRE_TOMATO.get())
         .add((Item)ModItems.RABBAGE.get())
         .add((Item)ModItems.SALTSPROUT.get());
      this.tag(ModTags.Items.SEEDS)
         .add((Item)ModItems.MANDRAKE_SEEDS.get())
         .add((Item)ModItems.SUNFIRE_TOMATO_SEEDS.get())
         .add((Item)ModItems.RABBAGE_SEEDS.get());
      this.tag(ModTags.Items.FOODS)
         .add((Item)ModItems.CHILLBERRIES.get())
         .add((Item)ModItems.SUNFIRE_TOMATO.get())
         .add((Item)ModItems.SALTSPROUT.get())
         .add((Item)ModItems.GALEBERRIES.get())
         .add((Item)ModItems.SPICY_SANDWICH.get())
         .add((Item)ModItems.CHILLBERRY_PIE.get())
         .add((Item)ModItems.MANDRAKE_STEW.get())
         .add((Item)ModItems.GALEBERRIES_COOKIE.get());
      this.tag(ModTags.Items.FOODS_BERRY).add((Item)ModItems.CHILLBERRIES.get()).add((Item)ModItems.GALEBERRIES.get());
      this.tag(ModTags.Items.FOODS_SOUP).add((Item)ModItems.MANDRAKE_STEW.get());
      this.tag(ModTags.Items.FOODS_PIE).add((Item)ModItems.CHILLBERRY_PIE.get());
      this.tag(ModTags.Items.FOODS_FOOD_POISONING).add((Item)ModItems.SALTSPROUT.get());
      this.tag(ItemTags.FOX_FOOD).add((Item)ModItems.CHILLBERRIES.get()).add((Item)ModItems.GALEBERRIES.get());
      this.tag(ModTags.Items.FOODS_BREAD).add(Items.BREAD);
      this.tag(ModTags.Items.FOODS_COOKED_MEAT)
         .add(Items.COOKED_BEEF)
         .add(Items.COOKED_CHICKEN)
         .add(Items.COOKED_MUTTON)
         .add(Items.COOKED_PORKCHOP)
         .add(Items.COOKED_RABBIT)
         .add(Items.COOKED_COD)
         .add(Items.COOKED_SALMON);
      this.tag(ModTags.Items.FOODS_VEGETABLE).add((Item)ModItems.SUNFIRE_TOMATO.get()).add((Item)ModItems.RABBAGE.get()).add(Items.CARROT).add(Items.POTATO);
      this.tag(ModTags.Compat.SERENE_SEASONS_SPRING_CROPS).add((Item)ModItems.RABBAGE_SEEDS.get()).add((Item)ModItems.MANDRAKE_SEEDS.get());
      this.tag(ModTags.Compat.SERENE_SEASONS_SUMMER_CROPS)
         .add((Item)ModItems.RABBAGE_SEEDS.get())
         .add((Item)ModItems.MANDRAKE_SEEDS.get())
         .add((Item)ModItems.SUNFIRE_TOMATO_SEEDS.get());
      this.tag(ModTags.Compat.SERENE_SEASONS_AUTUMN_CROPS).add((Item)ModItems.RABBAGE_SEEDS.get()).add((Item)ModItems.SUNFIRE_TOMATO_SEEDS.get());
      this.tag(ModTags.Compat.SERENE_SEASONS_WINTER_CROPS).add((Item)ModItems.RABBAGE_SEEDS.get());
   }
}
