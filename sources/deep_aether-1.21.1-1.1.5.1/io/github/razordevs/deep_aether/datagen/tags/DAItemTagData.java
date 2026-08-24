package io.github.razordevs.deep_aether.datagen.tags;

import com.aetherteam.aether.AetherTags.Blocks;
import com.aetherteam.aether.AetherTags.Items;
import com.aetherteam.aether.item.AetherItems;
import io.github.razordevs.deep_aether.init.DABlocks;
import io.github.razordevs.deep_aether.init.DAItems;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider.IntrinsicTagAppender;
import net.minecraft.data.tags.TagsProvider.TagLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

public class DAItemTagData extends ItemTagsProvider {
   public DAItemTagData(
      PackOutput output, CompletableFuture<Provider> registries, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper helper
   ) {
      super(output, registries, blockTags, "deep_aether", helper);
   }

   @Nonnull
   public String getName() {
      return "Deep Aether Item Tags";
   }

   protected void addTags(Provider provider) {
      this.copy(DATags.Blocks.ROSEROOT_LOGS, DATags.Items.ROSEROOT_LOGS);
      this.copy(DATags.Blocks.YAGROOT_LOGS, DATags.Items.YAGROOT_LOGS);
      this.copy(DATags.Blocks.CRUDEROOT_LOGS, DATags.Items.CRUDEROOT_LOGS);
      this.copy(DATags.Blocks.CONBERRY_LOGS, DATags.Items.CONBERRY_LOGS);
      this.copy(DATags.Blocks.SUNROOT_LOGS, DATags.Items.SUNROOT_LOGS);
      this.copy(DATags.Blocks.NIMBUS_BLOCKS, DATags.Items.NIMBUS_BLOCKS);
      this.copy(Blocks.AERCLOUDS, Items.AERCLOUDS);
      this.copy(net.neoforged.neoforge.common.Tags.Blocks.CHAINS, net.neoforged.neoforge.common.Tags.Items.CHAINS);
      this.copy(net.neoforged.neoforge.common.Tags.Blocks.STORAGE_BLOCKS, net.neoforged.neoforge.common.Tags.Items.STORAGE_BLOCKS);
      this.copy(BlockTags.LOGS, ItemTags.LOGS);
      this.copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
      this.copy(BlockTags.STAIRS, ItemTags.STAIRS);
      this.copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
      this.copy(BlockTags.SLABS, ItemTags.SLABS);
      this.copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
      this.copy(BlockTags.FENCES, ItemTags.FENCES);
      this.copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
      this.copy(net.neoforged.neoforge.common.Tags.Blocks.FENCES, net.neoforged.neoforge.common.Tags.Items.FENCES);
      this.copy(net.neoforged.neoforge.common.Tags.Blocks.FENCES_WOODEN, net.neoforged.neoforge.common.Tags.Items.FENCES_WOODEN);
      this.copy(BlockTags.FENCE_GATES, ItemTags.FENCE_GATES);
      this.copy(net.neoforged.neoforge.common.Tags.Blocks.FENCE_GATES, net.neoforged.neoforge.common.Tags.Items.FENCE_GATES);
      this.copy(net.neoforged.neoforge.common.Tags.Blocks.FENCE_GATES_WOODEN, net.neoforged.neoforge.common.Tags.Items.FENCE_GATES_WOODEN);
      this.copy(BlockTags.DOORS, ItemTags.DOORS);
      this.copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
      this.copy(BlockTags.TRAPDOORS, ItemTags.TRAPDOORS);
      this.copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
      this.copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
      this.copy(BlockTags.BUTTONS, ItemTags.BUTTONS);
      this.copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
      this.copy(BlockTags.WALLS, ItemTags.WALLS);
      this.copy(BlockTags.LEAVES, ItemTags.LEAVES);
      this.copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
      this.copy(BlockTags.FLOWERS, ItemTags.FLOWERS);
      this.copy(BlockTags.SMALL_FLOWERS, ItemTags.SMALL_FLOWERS);
      this.copy(BlockTags.TALL_FLOWERS, ItemTags.TALL_FLOWERS);
      this.copy(net.neoforged.neoforge.common.Tags.Blocks.ORES, net.neoforged.neoforge.common.Tags.Items.ORES);
      this.copy(net.neoforged.neoforge.common.Tags.Blocks.ORE_RATES_SINGULAR, net.neoforged.neoforge.common.Tags.Items.ORE_RATES_SINGULAR);
      this.copy(com.aetherteam.beyondparity.BeyondParityTags.Blocks.LOG_WALLS, com.aetherteam.beyondparity.BeyondParityTags.Items.LOG_WALLS);
      Collection<DeferredHolder<Item, ? extends Item>> items = DAItems.ITEMS.getEntries();
      IntrinsicTagAppender<Item> tag = this.tag(Items.TREATED_AS_AETHER_ITEM);
      items.forEach(item -> tag.add((Item)item.get()));
      this.tag(Items.PLANKS_CRAFTING)
         .add(
            new Item[]{
               ((Block)DABlocks.ROSEROOT_PLANKS.get()).asItem(),
               ((Block)DABlocks.YAGROOT_PLANKS.get()).asItem(),
               ((Block)DABlocks.CRUDEROOT_PLANKS.get()).asItem(),
               ((Block)DABlocks.CONBERRY_PLANKS.get()).asItem(),
               ((Block)DABlocks.SUNROOT_PLANKS.get()).asItem()
            }
         );
      this.tag(DATags.Items.CRAFTS_ROSEROOT_PLANKS)
         .add(
            new Item[]{
               ((Block)DABlocks.ROSEROOT_LOG.get()).asItem(),
               ((Block)DABlocks.ROSEROOT_WOOD.get()).asItem(),
               ((Block)DABlocks.STRIPPED_ROSEROOT_LOG.get()).asItem(),
               ((Block)DABlocks.STRIPPED_ROSEROOT_WOOD.get()).asItem()
            }
         );
      this.tag(DATags.Items.CRAFTS_YAGROOT_PLANKS)
         .add(
            new Item[]{
               ((Block)DABlocks.YAGROOT_LOG.get()).asItem(),
               ((Block)DABlocks.YAGROOT_WOOD.get()).asItem(),
               ((Block)DABlocks.STRIPPED_YAGROOT_LOG.get()).asItem(),
               ((Block)DABlocks.STRIPPED_YAGROOT_WOOD.get()).asItem()
            }
         );
      this.tag(DATags.Items.CRAFTS_CRUDEROOT_PLANKS)
         .add(
            new Item[]{
               ((Block)DABlocks.CRUDEROOT_LOG.get()).asItem(),
               ((Block)DABlocks.CRUDEROOT_WOOD.get()).asItem(),
               ((Block)DABlocks.STRIPPED_CRUDEROOT_LOG.get()).asItem(),
               ((Block)DABlocks.STRIPPED_CRUDEROOT_WOOD.get()).asItem()
            }
         );
      this.tag(DATags.Items.CRAFTS_CONBERRY_PLANKS)
         .add(
            new Item[]{
               ((Block)DABlocks.CONBERRY_LOG.get()).asItem(),
               ((Block)DABlocks.CONBERRY_WOOD.get()).asItem(),
               ((Block)DABlocks.STRIPPED_CONBERRY_LOG.get()).asItem(),
               ((Block)DABlocks.STRIPPED_CONBERRY_WOOD.get()).asItem()
            }
         );
      this.tag(DATags.Items.CRAFTS_SUNROOT_PLANKS)
         .add(
            new Item[]{
               ((Block)DABlocks.SUNROOT_LOG.get()).asItem(),
               ((Block)DABlocks.SUNROOT_WOOD.get()).asItem(),
               ((Block)DABlocks.STRIPPED_SUNROOT_LOG.get()).asItem(),
               ((Block)DABlocks.STRIPPED_SUNROOT_WOOD.get()).asItem()
            }
         );
      this.tag(Items.SKYROOT_STICK_CRAFTING)
         .add(
            new Item[]{
               ((Block)DABlocks.ROSEROOT_PLANKS.get()).asItem(),
               ((Block)DABlocks.YAGROOT_PLANKS.get()).asItem(),
               ((Block)DABlocks.CRUDEROOT_PLANKS.get()).asItem(),
               ((Block)DABlocks.CONBERRY_PLANKS.get()).asItem(),
               ((Block)DABlocks.SUNROOT_PLANKS.get()).asItem()
            }
         );
      this.tag(Items.SKYROOT_TOOL_CRAFTING)
         .add(
            new Item[]{
               ((Block)DABlocks.ROSEROOT_PLANKS.get()).asItem(),
               ((Block)DABlocks.YAGROOT_PLANKS.get()).asItem(),
               ((Block)DABlocks.CRUDEROOT_PLANKS.get()).asItem(),
               ((Block)DABlocks.CONBERRY_PLANKS.get()).asItem(),
               ((Block)DABlocks.SUNROOT_PLANKS.get()).asItem()
            }
         );
      this.tag(Items.SKYROOT_REPAIRING)
         .add(
            new Item[]{
               ((Block)DABlocks.ROSEROOT_PLANKS.get()).asItem(),
               ((Block)DABlocks.YAGROOT_PLANKS.get()).asItem(),
               ((Block)DABlocks.CRUDEROOT_PLANKS.get()).asItem(),
               ((Block)DABlocks.CONBERRY_PLANKS.get()).asItem(),
               ((Block)DABlocks.SUNROOT_PLANKS.get()).asItem()
            }
         );
      this.tag(ItemTags.HEAD_ARMOR)
         .add(new Item[]{(Item)DAItems.SKYJADE_HELMET.get(), (Item)DAItems.STRATUS_HELMET.get(), (Item)DAItems.STORMFORGED_HELMET.get()});
      this.tag(ItemTags.HEAD_ARMOR_ENCHANTABLE)
         .add(new Item[]{(Item)DAItems.SKYJADE_HELMET.get(), (Item)DAItems.STRATUS_HELMET.get(), (Item)DAItems.STORMFORGED_HELMET.get()});
      this.tag(ItemTags.CHEST_ARMOR)
         .add(new Item[]{(Item)DAItems.SKYJADE_CHESTPLATE.get(), (Item)DAItems.STRATUS_CHESTPLATE.get(), (Item)DAItems.STORMFORGED_CHESTPLATE.get()});
      this.tag(ItemTags.CHEST_ARMOR_ENCHANTABLE)
         .add(new Item[]{(Item)DAItems.SKYJADE_CHESTPLATE.get(), (Item)DAItems.STRATUS_CHESTPLATE.get(), (Item)DAItems.STORMFORGED_CHESTPLATE.get()});
      this.tag(ItemTags.LEG_ARMOR)
         .add(new Item[]{(Item)DAItems.SKYJADE_LEGGINGS.get(), (Item)DAItems.STRATUS_LEGGINGS.get(), (Item)DAItems.STORMFORGED_LEGGINGS.get()});
      this.tag(ItemTags.LEG_ARMOR_ENCHANTABLE)
         .add(new Item[]{(Item)DAItems.SKYJADE_LEGGINGS.get(), (Item)DAItems.STRATUS_LEGGINGS.get(), (Item)DAItems.STORMFORGED_LEGGINGS.get()});
      this.tag(ItemTags.FOOT_ARMOR)
         .add(new Item[]{(Item)DAItems.SKYJADE_BOOTS.get(), (Item)DAItems.STRATUS_BOOTS.get(), (Item)DAItems.STORMFORGED_BOOTS.get()});
      this.tag(ItemTags.FOOT_ARMOR_ENCHANTABLE)
         .add(new Item[]{(Item)DAItems.SKYJADE_BOOTS.get(), (Item)DAItems.STRATUS_BOOTS.get(), (Item)DAItems.STORMFORGED_BOOTS.get()});
      this.tag(ItemTags.TRIMMABLE_ARMOR)
         .add(
            new Item[]{
               (Item)DAItems.SKYJADE_HELMET.get(),
               (Item)DAItems.SKYJADE_CHESTPLATE.get(),
               (Item)DAItems.SKYJADE_LEGGINGS.get(),
               (Item)DAItems.SKYJADE_BOOTS.get(),
               (Item)DAItems.SKYJADE_GLOVES.get(),
               (Item)DAItems.STORMFORGED_HELMET.get(),
               (Item)DAItems.STORMFORGED_CHESTPLATE.get(),
               (Item)DAItems.STORMFORGED_LEGGINGS.get(),
               (Item)DAItems.STORMFORGED_BOOTS.get(),
               (Item)DAItems.STORMFORGED_GLOVES.get(),
               (Item)DAItems.STRATUS_HELMET.get(),
               (Item)DAItems.STRATUS_CHESTPLATE.get(),
               (Item)DAItems.STRATUS_LEGGINGS.get(),
               (Item)DAItems.STRATUS_BOOTS.get(),
               (Item)DAItems.STRATUS_GLOVES.get()
            }
         );
      this.tag(ItemTags.SWORDS)
         .add(
            new Item[]{
               (Item)DAItems.SKYJADE_TOOLS_SWORD.get(), (Item)DAItems.STRATUS_SWORD.get(), (Item)DAItems.BLADE_OF_LUCK.get(), (Item)DAItems.STORM_SWORD.get()
            }
         );
      this.tag(ItemTags.PICKAXES).add(new Item[]{(Item)DAItems.SKYJADE_TOOLS_PICKAXE.get(), (Item)DAItems.STRATUS_PICKAXE.get()});
      this.tag(ItemTags.SHOVELS).add(new Item[]{(Item)DAItems.SKYJADE_TOOLS_SHOVEL.get(), (Item)DAItems.STRATUS_SHOVEL.get()});
      this.tag(ItemTags.AXES).add(new Item[]{(Item)DAItems.SKYJADE_TOOLS_AXE.get(), (Item)DAItems.STRATUS_AXE.get()});
      this.tag(ItemTags.HOES).add(new Item[]{(Item)DAItems.SKYJADE_TOOLS_HOE.get(), (Item)DAItems.STRATUS_HOE.get()});
      this.tag(ItemTags.SHARP_WEAPON_ENCHANTABLE)
         .add(
            new Item[]{
               (Item)DAItems.SKYJADE_TOOLS_SWORD.get(), (Item)DAItems.STRATUS_SWORD.get(), (Item)DAItems.BLADE_OF_LUCK.get(), (Item)DAItems.STORM_SWORD.get()
            }
         );
      this.tag(ItemTags.WEAPON_ENCHANTABLE)
         .add(
            new Item[]{
               (Item)DAItems.SKYJADE_TOOLS_SWORD.get(), (Item)DAItems.STRATUS_SWORD.get(), (Item)DAItems.BLADE_OF_LUCK.get(), (Item)DAItems.STORM_SWORD.get()
            }
         );
      this.tag(ItemTags.FIRE_ASPECT_ENCHANTABLE)
         .add(
            new Item[]{
               (Item)DAItems.SKYJADE_TOOLS_SWORD.get(), (Item)DAItems.STRATUS_SWORD.get(), (Item)DAItems.BLADE_OF_LUCK.get(), (Item)DAItems.STORM_SWORD.get()
            }
         );
      this.tag(ItemTags.SWORD_ENCHANTABLE)
         .add(
            new Item[]{
               (Item)DAItems.SKYJADE_TOOLS_SWORD.get(), (Item)DAItems.STRATUS_SWORD.get(), (Item)DAItems.BLADE_OF_LUCK.get(), (Item)DAItems.STORM_SWORD.get()
            }
         );
      this.tag(ItemTags.MINING_ENCHANTABLE)
         .add(
            new Item[]{
               (Item)DAItems.SKYJADE_TOOLS_PICKAXE.get(),
               (Item)DAItems.STRATUS_PICKAXE.get(),
               (Item)DAItems.SKYJADE_TOOLS_AXE.get(),
               (Item)DAItems.STRATUS_AXE.get(),
               (Item)DAItems.SKYJADE_TOOLS_SHOVEL.get(),
               (Item)DAItems.STRATUS_SHOVEL.get(),
               (Item)DAItems.SKYJADE_TOOLS_HOE.get(),
               (Item)DAItems.STRATUS_HOE.get()
            }
         );
      this.tag(ItemTags.BOW_ENCHANTABLE).add((Item)DAItems.STORM_BOW.get());
      this.tag(ItemTags.DURABILITY_ENCHANTABLE)
         .add(
            new Item[]{
               (Item)DAItems.SKYJADE_TOOLS_SWORD.get(),
               (Item)DAItems.STRATUS_SWORD.get(),
               (Item)DAItems.BLADE_OF_LUCK.get(),
               (Item)DAItems.STORM_SWORD.get(),
               (Item)DAItems.SKYJADE_TOOLS_PICKAXE.get(),
               (Item)DAItems.STRATUS_PICKAXE.get(),
               (Item)DAItems.SKYJADE_TOOLS_SHOVEL.get(),
               (Item)DAItems.STRATUS_SHOVEL.get(),
               (Item)DAItems.SKYJADE_TOOLS_AXE.get(),
               (Item)DAItems.STRATUS_AXE.get(),
               (Item)DAItems.SKYJADE_TOOLS_HOE.get(),
               (Item)DAItems.STRATUS_HOE.get(),
               (Item)DAItems.SKYJADE_HELMET.get(),
               (Item)DAItems.STRATUS_HELMET.get(),
               (Item)DAItems.SKYJADE_CHESTPLATE.get(),
               (Item)DAItems.STRATUS_CHESTPLATE.get(),
               (Item)DAItems.SKYJADE_LEGGINGS.get(),
               (Item)DAItems.STRATUS_LEGGINGS.get(),
               (Item)DAItems.SKYJADE_BOOTS.get(),
               (Item)DAItems.STRATUS_BOOTS.get(),
               (Item)DAItems.STORMFORGED_HELMET.get(),
               (Item)DAItems.STORMFORGED_CHESTPLATE.get(),
               (Item)DAItems.STORMFORGED_LEGGINGS.get(),
               (Item)DAItems.STORMFORGED_BOOTS.get(),
               (Item)DAItems.STORM_BOW.get(),
               (Item)DAItems.SKYJADE_GLOVES.get(),
               (Item)DAItems.STRATUS_GLOVES.get(),
               (Item)DAItems.STORMFORGED_GLOVES.get(),
               (Item)DAItems.SKYJADE_RING.get(),
               (Item)DAItems.STRATUS_RING.get(),
               (Item)DAItems.WIND_SHIELD.get(),
               (Item)DAItems.SLIDER_EYE.get(),
               (Item)DAItems.AFTERBURNER.get()
            }
         );
      this.tag(DATags.Items.CRAFTS_MOSSY_BLOCKS)
         .add(
            new Item[]{
               ((Block)DABlocks.AETHER_MOSS_BLOCK.get()).asItem(),
               net.minecraft.world.level.block.Blocks.MOSS_BLOCK.asItem(),
               net.minecraft.world.level.block.Blocks.VINE.asItem()
            }
         );
      this.tag(ItemTags.SIGNS)
         .add(
            new Item[]{
               ((Block)DABlocks.ROSEROOT_SIGN.get()).asItem(),
               ((Block)DABlocks.YAGROOT_SIGN.get()).asItem(),
               ((Block)DABlocks.CRUDEROOT_SIGN.get()).asItem(),
               ((Block)DABlocks.CONBERRY_SIGN.get()).asItem(),
               ((Block)DABlocks.SUNROOT_SIGN.get()).asItem()
            }
         );
      this.tag(ItemTags.HANGING_SIGNS)
         .add(
            new Item[]{
               (Item)DAItems.CONBERRY_HANGING_SIGN.get(),
               (Item)DAItems.CRUDEROOT_HANGING_SIGN.get(),
               (Item)DAItems.ROSEROOT_HANGING_SIGN.get(),
               (Item)DAItems.YAGROOT_HANGING_SIGN.get(),
               (Item)DAItems.SUNROOT_HANGING_SIGN.get()
            }
         );
      this.tag(DATags.Items.SKYJADE_ARMOR)
         .add(
            new Item[]{
               (Item)DAItems.SKYJADE_HELMET.get(),
               (Item)DAItems.SKYJADE_CHESTPLATE.get(),
               (Item)DAItems.SKYJADE_LEGGINGS.get(),
               (Item)DAItems.SKYJADE_BOOTS.get(),
               (Item)DAItems.SKYJADE_GLOVES.get()
            }
         );
      this.tag(DATags.Items.STRATUS_ARMOR)
         .add(
            new Item[]{
               (Item)DAItems.STRATUS_HELMET.get(),
               (Item)DAItems.STRATUS_CHESTPLATE.get(),
               (Item)DAItems.STRATUS_LEGGINGS.get(),
               (Item)DAItems.STRATUS_BOOTS.get(),
               (Item)DAItems.STRATUS_GLOVES.get()
            }
         );
      this.tag(DATags.Items.STORM_ARMOR)
         .add(
            new Item[]{
               (Item)DAItems.STORMFORGED_HELMET.get(),
               (Item)DAItems.STORMFORGED_CHESTPLATE.get(),
               (Item)DAItems.STORMFORGED_LEGGINGS.get(),
               (Item)DAItems.STORMFORGED_BOOTS.get(),
               (Item)DAItems.STORMFORGED_GLOVES.get()
            }
         );
      this.tag(Items.BRONZE_DUNGEON_LOOT).add((Item)DAItems.MUSIC_DISC_ATTA.get());
      this.tag(Items.SILVER_DUNGEON_LOOT).add((Item)DAItems.MUSIC_DISC_FAENT.get());
      this.tag(Items.GOLD_DUNGEON_LOOT).add(new Item[]{(Item)DAItems.MUSIC_DISC_HIMININN.get(), (Item)DAItems.STRATUS_SMITHING_TEMPLATE.get()});
      this.tag(DATags.Items.BRASS_DUNGEON_LOOT)
         .add(
            new Item[]{
               (Item)DAItems.STORMFORGED_SMITHING_TEMPLATE.get(),
               (Item)DAItems.CLOUD_CAPE.get(),
               (Item)DAItems.WIND_SHIELD.get(),
               (Item)DAItems.AERCLOUD_NECKLACE.get(),
               (Item)DAItems.STORM_SWORD.get(),
               (Item)DAItems.STORM_BOW.get(),
               (Item)DAItems.BLADE_OF_LUCK.get(),
               (Item)DAItems.MUSIC_DISC_CYCLONE.get()
            }
         );
      this.tag(DATags.Items.BRASS_DUNGEON_LOOT).addTag(DATags.Items.STORM_ARMOR);
      this.tag(DATags.Items.FLAWLESS_ITEMS)
         .add(
            new Item[]{
               (Item)DAItems.SLIDER_EYE.get(),
               (Item)DAItems.MEDAL_OF_HONOR.get(),
               (Item)DAItems.SUN_CORE.get(),
               (Item)DAItems.AFTERBURNER.get(),
               (Item)DAItems.AERWHALE_SADDLE.get(),
               (Item)DAItems.FLOATY_SCARF.get()
            }
         );
      this.tag(Items.ACCESSORIES_GLOVES)
         .add(new Item[]{(Item)DAItems.SKYJADE_GLOVES.get(), (Item)DAItems.STORMFORGED_GLOVES.get(), (Item)DAItems.STRATUS_GLOVES.get()});
      this.tag(Items.ACCESSORIES_RINGS)
         .add(
            new Item[]{(Item)DAItems.SKYJADE_RING.get(), (Item)DAItems.STRATUS_RING.get(), (Item)DAItems.SPOOKY_RING.get(), (Item)DAItems.GRAVITITE_RING.get()}
         );
      this.tag(Items.ACCESSORIES_PENDANTS)
         .add(new Item[]{(Item)DAItems.MEDAL_OF_HONOR.get(), (Item)DAItems.AERCLOUD_NECKLACE.get(), (Item)DAItems.FLOATY_SCARF.get()});
      this.tag(Items.ACCESSORIES_CAPES).add((Item)DAItems.CLOUD_CAPE.get());
      this.tag(Items.ACCESSORIES_SHIELDS).add((Item)DAItems.WIND_SHIELD.get());
      this.tag(Items.ACCESSORIES_MISCELLANEOUS).add((Item)DAItems.SLIDER_EYE.get());
      this.tag(net.neoforged.neoforge.common.Tags.Items.TOOLS_BOW).add((Item)DAItems.STORM_BOW.get());
      this.tag(net.neoforged.neoforge.common.Tags.Items.RANGED_WEAPON_TOOLS).add((Item)DAItems.STORM_BOW.get());
      this.tag(ItemTags.BEACON_PAYMENT_ITEMS).add(new Item[]{(Item)DAItems.SKYJADE.get(), (Item)DAItems.STRATUS_INGOT.get(), (Item)DAItems.SQUALL_PLATE.get()});
      this.tag(ItemTags.TRIM_MATERIALS).add(new Item[]{(Item)DAItems.SKYJADE.get(), (Item)DAItems.STRATUS_INGOT.get(), (Item)DAItems.SQUALL_PLATE.get()});
      this.tag(DATags.Items.SKYJADE_REPAIRING).add((Item)DAItems.SKYJADE.get());
      this.tag(net.neoforged.neoforge.common.Tags.Items.GEMS).add((Item)DAItems.SKYJADE.get());
      this.tag(net.neoforged.neoforge.common.Tags.Items.NUGGETS).add((Item)DAItems.SKYJADE_NUGGET.get());
      this.tag(DATags.Items.STRATUS_REPAIRING).add((Item)DAItems.STRATUS_INGOT.get());
      this.tag(net.neoforged.neoforge.common.Tags.Items.INGOTS).add((Item)DAItems.STRATUS_INGOT.get());
      this.tag(DATags.Items.STORM_REPAIRING).add((Item)DAItems.SQUALL_PLATE.get());
      this.tag(Items.ORES_IN_GROUND_HOLYSTONE).add(((Block)DABlocks.SKYJADE_ORE.get()).asItem());
      this.tag(net.neoforged.neoforge.common.Tags.Items.ORE_RATES_SINGULAR).add(((Block)DABlocks.SKYJADE_ORE.get()).asItem());
      this.tag(ItemTags.BOATS)
         .add(
            new Item[]{
               (Item)DAItems.ROSEROOT_BOAT.get(),
               (Item)DAItems.YAGROOT_BOAT.get(),
               (Item)DAItems.CRUDEROOT_BOAT.get(),
               (Item)DAItems.CONBERRY_BOAT.get(),
               (Item)DAItems.SUNROOT_BOAT.get(),
               (Item)DAItems.ROSEROOT_CHEST_BOAT.get(),
               (Item)DAItems.YAGROOT_CHEST_BOAT.get(),
               (Item)DAItems.CRUDEROOT_CHEST_BOAT.get(),
               (Item)DAItems.CONBERRY_CHEST_BOAT.get(),
               (Item)DAItems.SUNROOT_CHEST_BOAT.get()
            }
         );
      this.tag(ItemTags.CHEST_BOATS)
         .add(
            new Item[]{
               (Item)DAItems.ROSEROOT_CHEST_BOAT.get(),
               (Item)DAItems.YAGROOT_CHEST_BOAT.get(),
               (Item)DAItems.CRUDEROOT_CHEST_BOAT.get(),
               (Item)DAItems.CONBERRY_CHEST_BOAT.get(),
               (Item)DAItems.SUNROOT_CHEST_BOAT.get()
            }
         );
      this.tag(net.neoforged.neoforge.common.Tags.Items.FOODS_BERRY)
         .add(new Item[]{(Item)DAItems.FROZEN_GOLDEN_BERRIES.get(), (Item)DAItems.GOLDEN_BERRIES.get()});
      this.tag(ItemTags.FOX_FOOD).add((Item)DAItems.GOLDEN_BERRIES.get());
      this.tag(ItemTags.FISHES).add(new Item[]{(Item)DAItems.RAW_AERGLOW_FISH.get(), (Item)DAItems.COOKED_AERGLOW_FISH.get()});
      this.tag(ItemTags.CAT_FOOD).add((Item)DAItems.RAW_AERGLOW_FISH.get());
      this.tag(ItemTags.OCELOT_FOOD).add((Item)DAItems.RAW_AERGLOW_FISH.get());
      this.tag(net.neoforged.neoforge.common.Tags.Items.FOODS_RAW_FISH).add((Item)DAItems.RAW_AERGLOW_FISH.get());
      this.tag(net.neoforged.neoforge.common.Tags.Items.FOODS_COOKED_FISH).add((Item)DAItems.COOKED_AERGLOW_FISH.get());
      this.tag(ItemTags.WOLF_FOOD).add(new Item[]{(Item)DAItems.RAW_QUAIL.get(), (Item)DAItems.COOKED_QUAIL.get()});
      this.tag(ItemTags.MEAT).add(new Item[]{(Item)DAItems.RAW_QUAIL.get(), (Item)DAItems.COOKED_QUAIL.get()});
      this.tag(net.neoforged.neoforge.common.Tags.Items.FOODS_RAW_MEAT).add((Item)DAItems.RAW_QUAIL.get());
      this.tag(net.neoforged.neoforge.common.Tags.Items.FOODS_COOKED_MEAT).add((Item)DAItems.COOKED_QUAIL.get());
      this.tag(ItemTags.CHICKEN_FOOD).add(new Item[]{(Item)DAItems.SQUASH_SEEDS.get(), (Item)DAItems.GOLDEN_GRASS_SEEDS.get()});
      this.tag(DATags.Items.QUAIL_FOOD)
         .add(
            new Item[]{
               net.minecraft.world.item.Items.WHEAT_SEEDS,
               net.minecraft.world.item.Items.MELON_SEEDS,
               net.minecraft.world.item.Items.PUMPKIN_SEEDS,
               net.minecraft.world.item.Items.BEETROOT_SEEDS,
               net.minecraft.world.item.Items.TORCHFLOWER_SEEDS,
               net.minecraft.world.item.Items.PITCHER_POD,
               (Item)DAItems.SQUASH_SEEDS.get(),
               (Item)DAItems.GOLDEN_GRASS_SEEDS.get()
            }
         );
      this.tag(ItemTags.PARROT_FOOD).add(new Item[]{(Item)DAItems.SQUASH_SEEDS.get(), (Item)DAItems.GOLDEN_GRASS_SEEDS.get()});
      this.tag(net.neoforged.neoforge.common.Tags.Items.SEEDS).add(new Item[]{(Item)DAItems.SQUASH_SEEDS.get(), (Item)DAItems.GOLDEN_GRASS_SEEDS.get()});
      this.tag(net.neoforged.neoforge.common.Tags.Items.EGGS).add((Item)DAItems.QUAIL_EGG.get());
      this.tag(net.neoforged.neoforge.common.Tags.Items.MUSHROOMS).add(((Block)DABlocks.LIGHTCAP_MUSHROOMS.get()).asItem());
      this.tag(net.neoforged.neoforge.common.Tags.Items.CROPS)
         .add(
            new Item[]{
               ((Block)DABlocks.GREEN_SQUASH.get()).asItem(),
               ((Block)DABlocks.BLUE_SQUASH.get()).asItem(),
               ((Block)DABlocks.PURPLE_SQUASH.get()).asItem(),
               ((Block)DABlocks.CARVED_BLUE_SQUASH.get()).asItem(),
               ((Block)DABlocks.CARVED_GREEN_SQUASH.get()).asItem(),
               ((Block)DABlocks.CARVED_PURPLE_SQUASH.get()).asItem()
            }
         );
      this.tag(net.neoforged.neoforge.common.Tags.Items.FOODS_FRUIT)
         .add(new Item[]{(Item)DAItems.GREEN_SQUASH_SLICE.get(), (Item)DAItems.BLUE_SQUASH_SLICE.get(), (Item)DAItems.PURPLE_SQUASH_SLICE.get()});
      this.tag(DATags.Items.SQUASH_SLICE)
         .add(new Item[]{(Item)DAItems.GREEN_SQUASH_SLICE.get(), (Item)DAItems.BLUE_SQUASH_SLICE.get(), (Item)DAItems.PURPLE_SQUASH_SLICE.get()});
      this.tag(net.neoforged.neoforge.common.Tags.Items.ANIMAL_FOODS)
         .add(
            new Item[]{
               (Item)DAItems.SQUASH_SEEDS.get(),
               (Item)DAItems.GOLDEN_GRASS_SEEDS.get(),
               (Item)DAItems.RAW_AERGLOW_FISH.get(),
               (Item)DAItems.GOLDEN_BERRIES.get(),
               (Item)DAItems.RAW_QUAIL.get(),
               (Item)DAItems.COOKED_QUAIL.get()
            }
         );
      this.tag(Items.SLIDER_DAMAGING_ITEMS).add(new Item[]{(Item)DAItems.SKYJADE_TOOLS_PICKAXE.get(), (Item)DAItems.STRATUS_PICKAXE.get()});
      this.tag(ItemTags.COMPASSES).add(new Item[]{(Item)DAItems.BRONZE_COMPASS.get(), (Item)DAItems.SILVER_COMPASS.get(), (Item)DAItems.GOLD_COMPASS.get()});
      this.tag(Items.NO_SKYROOT_DOUBLE_DROPS).add((Item)DAItems.BRASS_DUNGEON_KEY.get());
      this.tag(Items.DUNGEON_KEYS).add((Item)DAItems.BRASS_DUNGEON_KEY.get());
      this.tag(net.neoforged.neoforge.common.Tags.Items.BUCKETS)
         .add(
            new Item[]{
               (Item)DAItems.PLACEABLE_POISON_BUCKET.get(),
               (Item)DAItems.AERGLOW_FISH_BUCKET.get(),
               (Item)DAItems.SKYROOT_AERGLOW_FISH_BUCKET.get(),
               (Item)DAItems.VIRULENT_QUICKSAND_BUCKET.get(),
               (Item)DAItems.SKYROOT_VIRULENT_QUICKSAND_BUCKET.get()
            }
         );
      this.tag(net.neoforged.neoforge.common.Tags.Items.BUCKETS_ENTITY_WATER)
         .add(new Item[]{(Item)DAItems.AERGLOW_FISH_BUCKET.get(), (Item)DAItems.SKYROOT_AERGLOW_FISH_BUCKET.get()});
      this.tag(DATags.Items.IS_GOLDEN_SWET_BALL)
         .add((Item)DAItems.GOLDEN_SWET_BALL.get())
         .addOptional(ResourceLocation.fromNamespaceAndPath("aether_redux", "golden_swet_ball"))
         .addOptional(ResourceLocation.fromNamespaceAndPath("aether_genesis", "golden_swet_ball"));
      this.tag(net.neoforged.neoforge.common.Tags.Items.MUSIC_DISCS)
         .add(
            new Item[]{
               (Item)DAItems.MUSIC_DISC_A_MORNING_WISH.get(),
               (Item)DAItems.MUSIC_DISC_ABOVE_THE_RAIN.get(),
               (Item)DAItems.MUSIC_DISC_ATTA.get(),
               (Item)DAItems.MUSIC_DISC_CYCLONE.get(),
               (Item)DAItems.MUSIC_DISC_FAENT.get(),
               (Item)DAItems.MUSIC_DISC_HIMININN.get(),
               (Item)DAItems.MUSIC_DISC_NABOORU.get()
            }
         );
      this.tag(Items.ACCEPTED_MUSIC_DISCS)
         .remove(net.minecraft.world.item.Items.MUSIC_DISC_OTHERSIDE)
         .remove(net.minecraft.world.item.Items.MUSIC_DISC_PIGSTEP);
      this.tag(DATags.Items.POISON_BUCKET).add(new Item[]{(Item)DAItems.PLACEABLE_POISON_BUCKET.get(), (Item)AetherItems.SKYROOT_POISON_BUCKET.get()});
   }
}
