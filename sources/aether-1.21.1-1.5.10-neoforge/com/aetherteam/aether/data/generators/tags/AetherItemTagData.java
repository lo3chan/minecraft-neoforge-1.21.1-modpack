package com.aetherteam.aether.data.generators.tags;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.item.AetherItems;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider.TagLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class AetherItemTagData extends ItemTagsProvider {
   public AetherItemTagData(
      PackOutput output, CompletableFuture<Provider> registries, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper helper
   ) {
      super(output, registries, blockTags, "aether", helper);
   }

   public void addTags(Provider provider) {
      this.copy(AetherTags.Blocks.AETHER_DIRT, AetherTags.Items.AETHER_DIRT);
      this.copy(AetherTags.Blocks.HOLYSTONE, AetherTags.Items.HOLYSTONE);
      this.copy(AetherTags.Blocks.AERCLOUDS, AetherTags.Items.AERCLOUDS);
      this.copy(AetherTags.Blocks.SKYROOT_LOGS, AetherTags.Items.SKYROOT_LOGS);
      this.copy(AetherTags.Blocks.GOLDEN_OAK_LOGS, AetherTags.Items.GOLDEN_OAK_LOGS);
      this.copy(AetherTags.Blocks.AEROGEL, AetherTags.Items.AEROGEL);
      this.copy(AetherTags.Blocks.DUNGEON_BLOCKS, AetherTags.Items.DUNGEON_BLOCKS);
      this.copy(AetherTags.Blocks.LOCKED_DUNGEON_BLOCKS, AetherTags.Items.LOCKED_DUNGEON_BLOCKS);
      this.copy(AetherTags.Blocks.TRAPPED_DUNGEON_BLOCKS, AetherTags.Items.TRAPPED_DUNGEON_BLOCKS);
      this.copy(AetherTags.Blocks.BOSS_DOORWAY_DUNGEON_BLOCKS, AetherTags.Items.BOSS_DOORWAY_DUNGEON_BLOCKS);
      this.copy(AetherTags.Blocks.TREASURE_DOORWAY_DUNGEON_BLOCKS, AetherTags.Items.TREASURE_DOORWAY_DUNGEON_BLOCKS);
      this.copy(AetherTags.Blocks.SENTRY_BLOCKS, AetherTags.Items.SENTRY_BLOCKS);
      this.copy(AetherTags.Blocks.ANGELIC_BLOCKS, AetherTags.Items.ANGELIC_BLOCKS);
      this.copy(AetherTags.Blocks.HELLFIRE_BLOCKS, AetherTags.Items.HELLFIRE_BLOCKS);
      this.copy(AetherTags.Blocks.AECHOR_PLANT_SPAWNABLE_DETERRENT, AetherTags.Items.AECHOR_PLANT_SPAWNABLE_DETERRENT);
      this.copy(AetherTags.Blocks.ORE_BEARING_GROUND_HOLYSTONE, AetherTags.Items.ORE_BEARING_GROUND_HOLYSTONE);
      this.copy(AetherTags.Blocks.ORES_AMBROSIUM, AetherTags.Items.ORES_AMBROSIUM);
      this.copy(AetherTags.Blocks.ORES_ZANITE, AetherTags.Items.ORES_ZANITE);
      this.copy(AetherTags.Blocks.ORES_GRAVITITE, AetherTags.Items.ORES_GRAVITITE);
      this.copy(AetherTags.Blocks.ORES_IN_GROUND_HOLYSTONE, AetherTags.Items.ORES_IN_GROUND_HOLYSTONE);
      this.tag(AetherTags.Items.CRAFTS_SKYROOT_PLANKS).addTags(new TagKey[]{AetherTags.Items.SKYROOT_LOGS, AetherTags.Items.GOLDEN_OAK_LOGS});
      this.tag(AetherTags.Items.PLANKS_CRAFTING).add(((Block)AetherBlocks.SKYROOT_PLANKS.get()).asItem());
      this.tag(AetherTags.Items.SKYROOT_STICK_CRAFTING).add(((Block)AetherBlocks.SKYROOT_PLANKS.get()).asItem());
      this.tag(AetherTags.Items.SKYROOT_TOOL_CRAFTING).add(((Block)AetherBlocks.SKYROOT_PLANKS.get()).asItem());
      this.tag(AetherTags.Items.MILK_BUCKET_CRAFTING).add(new Item[]{(Item)AetherItems.SKYROOT_MILK_BUCKET.get(), Items.MILK_BUCKET});
      this.tag(AetherTags.Items.WATER_BUCKET_CRAFTING).add(new Item[]{(Item)AetherItems.SKYROOT_WATER_BUCKET.get(), Items.WATER_BUCKET});
      this.tag(AetherTags.Items.AETHER_PORTAL_ACTIVATION_ITEMS);
      this.tag(AetherTags.Items.BOOK_OF_LORE_MATERIALS)
         .addTag(net.neoforged.neoforge.common.Tags.Items.DUSTS_GLOWSTONE)
         .add(new Item[]{Items.FLINT, (Item)AetherItems.AMBROSIUM_SHARD.get()});
      this.tag(AetherTags.Items.SKYROOT_STICKS).add((Item)AetherItems.SKYROOT_STICK.get());
      this.tag(AetherTags.Items.SWET_BALLS).add((Item)AetherItems.SWET_BALL.get());
      this.tag(AetherTags.Items.GOLDEN_AMBER_HARVESTERS)
         .add(new Item[]{(Item)AetherItems.ZANITE_AXE.get(), (Item)AetherItems.GRAVITITE_AXE.get(), (Item)AetherItems.VALKYRIE_AXE.get()});
      this.tag(AetherTags.Items.TREATED_AS_AETHER_ITEM);
      this.tag(AetherTags.Items.NO_SKYROOT_DOUBLE_DROPS)
         .addTag(AetherTags.Items.DUNGEON_KEYS)
         .add(
            new Item[]{
               (Item)AetherItems.VICTORY_MEDAL.get(),
               (Item)AetherItems.SKYROOT_PICKAXE.get(),
               (Item)AetherItems.IRON_RING.get(),
               (Item)AetherItems.GOLDEN_AMBER.get(),
               (Item)AetherItems.ZANITE_GEMSTONE.get(),
               (Item)AetherItems.HOLYSTONE_PICKAXE.get(),
               Items.PLAYER_HEAD,
               Items.SKELETON_SKULL,
               Items.CREEPER_HEAD,
               Items.ZOMBIE_HEAD,
               Items.WITHER_SKELETON_SKULL,
               Items.DRAGON_HEAD,
               Items.NETHER_STAR
            }
         );
      this.tag(AetherTags.Items.PIG_DROPS).add(new Item[]{Items.PORKCHOP, Items.COOKED_PORKCHOP});
      this.tag(AetherTags.Items.DARTS)
         .add(new Item[]{(Item)AetherItems.GOLDEN_DART.get(), (Item)AetherItems.POISON_DART.get(), (Item)AetherItems.ENCHANTED_DART.get()});
      this.tag(AetherTags.Items.DART_SHOOTERS)
         .add(
            new Item[]{
               (Item)AetherItems.GOLDEN_DART_SHOOTER.get(), (Item)AetherItems.POISON_DART_SHOOTER.get(), (Item)AetherItems.ENCHANTED_DART_SHOOTER.get()
            }
         );
      this.tag(AetherTags.Items.DEPLOYABLE_PARACHUTES).add(new Item[]{(Item)AetherItems.COLD_PARACHUTE.get(), (Item)AetherItems.GOLDEN_PARACHUTE.get()});
      this.tag(AetherTags.Items.DUNGEON_KEYS)
         .add(new Item[]{(Item)AetherItems.BRONZE_DUNGEON_KEY.get(), (Item)AetherItems.SILVER_DUNGEON_KEY.get(), (Item)AetherItems.GOLD_DUNGEON_KEY.get()});
      this.tag(AetherTags.Items.ACCEPTED_MUSIC_DISCS)
         .add(
            new Item[]{
               Items.MUSIC_DISC_11,
               Items.MUSIC_DISC_13,
               Items.MUSIC_DISC_BLOCKS,
               Items.MUSIC_DISC_CHIRP,
               Items.MUSIC_DISC_FAR,
               Items.MUSIC_DISC_MALL,
               Items.MUSIC_DISC_MELLOHI,
               Items.MUSIC_DISC_STAL,
               Items.MUSIC_DISC_WAIT,
               Items.MUSIC_DISC_WARD,
               Items.MUSIC_DISC_OTHERSIDE
            }
         );
      this.tag(AetherTags.Items.SAVE_NBT_IN_RECIPE)
         .add(new Item[]{(Item)AetherItems.ENCHANTED_DART_SHOOTER.get(), (Item)AetherItems.ICE_RING.get(), (Item)AetherItems.ICE_PENDANT.get()});
      this.tag(AetherTags.Items.MOA_EGGS)
         .add(new Item[]{(Item)AetherItems.BLUE_MOA_EGG.get(), (Item)AetherItems.WHITE_MOA_EGG.get(), (Item)AetherItems.BLACK_MOA_EGG.get()});
      this.tag(AetherTags.Items.FREEZABLE_RINGS).add((Item)AetherItems.IRON_RING.get()).add((Item)AetherItems.GOLDEN_RING.get());
      this.tag(AetherTags.Items.FREEZABLE_PENDANTS).add((Item)AetherItems.IRON_PENDANT.get()).add((Item)AetherItems.GOLDEN_PENDANT.get());
      this.tag(AetherTags.Items.PACIFIES_SWETS).add((Item)AetherItems.SWET_CAPE.get());
      this.tag(AetherTags.Items.SLIDER_DAMAGING_ITEMS).addTag(ItemTags.PICKAXES);
      this.tag(AetherTags.Items.BRONZE_DUNGEON_LOOT)
         .add(
            new Item[]{
               (Item)AetherItems.VALKYRIE_LANCE.get(),
               (Item)AetherItems.FLAMING_SWORD.get(),
               (Item)AetherItems.HAMMER_OF_KINGBDOGZ.get(),
               (Item)AetherItems.NEPTUNE_HELMET.get(),
               (Item)AetherItems.NEPTUNE_CHESTPLATE.get(),
               (Item)AetherItems.NEPTUNE_LEGGINGS.get(),
               (Item)AetherItems.NEPTUNE_BOOTS.get(),
               (Item)AetherItems.NEPTUNE_GLOVES.get(),
               (Item)AetherItems.SENTRY_BOOTS.get(),
               (Item)AetherItems.AGILITY_CAPE.get(),
               (Item)AetherItems.SWET_CAPE.get(),
               (Item)AetherItems.SHIELD_OF_REPULSION.get(),
               (Item)AetherItems.MUSIC_DISC_AETHER_TUNE.get(),
               (Item)AetherItems.MUSIC_DISC_SLIDERS_WRATH.get(),
               (Item)AetherItems.CLOUD_STAFF.get(),
               (Item)AetherItems.LIGHTNING_KNIFE.get(),
               (Item)AetherItems.PHOENIX_BOW.get(),
               (Item)AetherItems.BLUE_GUMMY_SWET.get(),
               (Item)AetherItems.GOLDEN_GUMMY_SWET.get()
            }
         );
      this.tag(AetherTags.Items.SILVER_DUNGEON_LOOT)
         .add(
            new Item[]{
               (Item)AetherItems.LIGHTNING_SWORD.get(),
               (Item)AetherItems.HOLY_SWORD.get(),
               (Item)AetherItems.VALKYRIE_HELMET.get(),
               (Item)AetherItems.VALKYRIE_CHESTPLATE.get(),
               (Item)AetherItems.VALKYRIE_LEGGINGS.get(),
               (Item)AetherItems.VALKYRIE_BOOTS.get(),
               (Item)AetherItems.VALKYRIE_GLOVES.get(),
               (Item)AetherItems.INVISIBILITY_CLOAK.get(),
               (Item)AetherItems.VALKYRIE_CAPE.get(),
               (Item)AetherItems.GOLDEN_FEATHER.get(),
               (Item)AetherItems.REGENERATION_STONE.get(),
               (Item)AetherItems.MUSIC_DISC_AETHER_TUNE.get(),
               (Item)AetherItems.MUSIC_DISC_ASCENDING_DAWN.get(),
               (Item)AetherItems.BLUE_GUMMY_SWET.get(),
               (Item)AetherItems.GOLDEN_GUMMY_SWET.get(),
               (Item)AetherItems.VALKYRIE_PICKAXE.get(),
               (Item)AetherItems.VALKYRIE_AXE.get(),
               (Item)AetherItems.VALKYRIE_SHOVEL.get(),
               (Item)AetherItems.VALKYRIE_HOE.get()
            }
         );
      this.tag(AetherTags.Items.GOLD_DUNGEON_LOOT)
         .add(
            new Item[]{
               (Item)AetherItems.VAMPIRE_BLADE.get(),
               (Item)AetherItems.PIG_SLAYER.get(),
               (Item)AetherItems.PHOENIX_HELMET.get(),
               (Item)AetherItems.PHOENIX_CHESTPLATE.get(),
               (Item)AetherItems.PHOENIX_LEGGINGS.get(),
               (Item)AetherItems.PHOENIX_BOOTS.get(),
               (Item)AetherItems.PHOENIX_GLOVES.get(),
               (Item)AetherItems.IRON_BUBBLE.get(),
               (Item)AetherItems.LIFE_SHARD.get()
            }
         );
      this.tag(AetherTags.Items.PHYG_TEMPTATION_ITEMS).add((Item)AetherItems.BLUE_BERRY.get());
      this.tag(AetherTags.Items.FLYING_COW_TEMPTATION_ITEMS).add((Item)AetherItems.BLUE_BERRY.get());
      this.tag(AetherTags.Items.SHEEPUFF_TEMPTATION_ITEMS).add((Item)AetherItems.BLUE_BERRY.get());
      this.tag(AetherTags.Items.AERBUNNY_TEMPTATION_ITEMS).add((Item)AetherItems.BLUE_BERRY.get());
      this.tag(AetherTags.Items.MOA_TEMPTATION_ITEMS).add((Item)AetherItems.NATURE_STAFF.get());
      this.tag(AetherTags.Items.MOA_FOOD_ITEMS).add((Item)AetherItems.AECHOR_PETAL.get());
      this.tag(AetherTags.Items.SKYROOT_REPAIRING).add(((Block)AetherBlocks.SKYROOT_PLANKS.get()).asItem());
      this.tag(AetherTags.Items.HOLYSTONE_REPAIRING).add(((Block)AetherBlocks.HOLYSTONE.get()).asItem());
      this.tag(AetherTags.Items.ZANITE_REPAIRING).add((Item)AetherItems.ZANITE_GEMSTONE.get());
      this.tag(AetherTags.Items.GRAVITITE_REPAIRING).add(((Block)AetherBlocks.ENCHANTED_GRAVITITE.get()).asItem());
      this.tag(AetherTags.Items.VALKYRIE_REPAIRING);
      this.tag(AetherTags.Items.FLAMING_REPAIRING);
      this.tag(AetherTags.Items.LIGHTNING_REPAIRING);
      this.tag(AetherTags.Items.HOLY_REPAIRING);
      this.tag(AetherTags.Items.VAMPIRE_REPAIRING);
      this.tag(AetherTags.Items.PIG_SLAYER_REPAIRING);
      this.tag(AetherTags.Items.HAMMER_OF_KINGBDOGZ_REPAIRING);
      this.tag(AetherTags.Items.CANDY_CANE_REPAIRING).add((Item)AetherItems.CANDY_CANE.get());
      this.tag(AetherTags.Items.NEPTUNE_REPAIRING);
      this.tag(AetherTags.Items.PHOENIX_REPAIRING);
      this.tag(AetherTags.Items.OBSIDIAN_REPAIRING);
      this.tag(AetherTags.Items.SENTRY_REPAIRING);
      this.tag(AetherTags.Items.ICE_REPAIRING);
      this.tag(AetherTags.Items.GEMS_AMBROSIUM).add((Item)AetherItems.AMBROSIUM_SHARD.get());
      this.tag(AetherTags.Items.GEMS_ZANITE).add((Item)AetherItems.ZANITE_GEMSTONE.get());
      this.tag(AetherTags.Items.PROCESSED_GRAVITITE).add(((Block)AetherBlocks.ENCHANTED_GRAVITITE.get()).asItem());
      this.tag(AetherTags.Items.TOOLS_LANCES).add((Item)AetherItems.VALKYRIE_LANCE.get());
      this.tag(AetherTags.Items.TOOLS_HAMMERS).add((Item)AetherItems.HAMMER_OF_KINGBDOGZ.get());
      this.tag(AetherTags.Items.ACCESSORIES_RINGS)
         .add(
            new Item[]{
               (Item)AetherItems.IRON_RING.get(), (Item)AetherItems.GOLDEN_RING.get(), (Item)AetherItems.ZANITE_RING.get(), (Item)AetherItems.ICE_RING.get()
            }
         );
      this.tag(AetherTags.Items.ACCESSORIES_PENDANTS)
         .add(
            new Item[]{
               (Item)AetherItems.IRON_PENDANT.get(),
               (Item)AetherItems.GOLDEN_PENDANT.get(),
               (Item)AetherItems.ZANITE_PENDANT.get(),
               (Item)AetherItems.ICE_PENDANT.get()
            }
         );
      this.tag(AetherTags.Items.ACCESSORIES_GLOVES)
         .add(
            new Item[]{
               (Item)AetherItems.LEATHER_GLOVES.get(),
               (Item)AetherItems.CHAINMAIL_GLOVES.get(),
               (Item)AetherItems.IRON_GLOVES.get(),
               (Item)AetherItems.GOLDEN_GLOVES.get(),
               (Item)AetherItems.DIAMOND_GLOVES.get(),
               (Item)AetherItems.NETHERITE_GLOVES.get(),
               (Item)AetherItems.ZANITE_GLOVES.get(),
               (Item)AetherItems.GRAVITITE_GLOVES.get(),
               (Item)AetherItems.NEPTUNE_GLOVES.get(),
               (Item)AetherItems.PHOENIX_GLOVES.get(),
               (Item)AetherItems.OBSIDIAN_GLOVES.get(),
               (Item)AetherItems.VALKYRIE_GLOVES.get()
            }
         );
      this.tag(AetherTags.Items.ACCESSORIES_CAPES)
         .add(
            new Item[]{
               (Item)AetherItems.RED_CAPE.get(),
               (Item)AetherItems.BLUE_CAPE.get(),
               (Item)AetherItems.YELLOW_CAPE.get(),
               (Item)AetherItems.WHITE_CAPE.get(),
               (Item)AetherItems.SWET_CAPE.get(),
               (Item)AetherItems.INVISIBILITY_CLOAK.get(),
               (Item)AetherItems.AGILITY_CAPE.get(),
               (Item)AetherItems.VALKYRIE_CAPE.get()
            }
         );
      this.tag(AetherTags.Items.ACCESSORIES_MISCELLANEOUS)
         .add(new Item[]{(Item)AetherItems.GOLDEN_FEATHER.get(), (Item)AetherItems.REGENERATION_STONE.get(), (Item)AetherItems.IRON_BUBBLE.get()});
      this.tag(AetherTags.Items.ACCESSORIES_SHIELDS).add((Item)AetherItems.SHIELD_OF_REPULSION.get());
      this.tag(AetherTags.Items.ACCESSORIES)
         .addTags(
            new TagKey[]{
               AetherTags.Items.ACCESSORIES_RINGS,
               AetherTags.Items.ACCESSORIES_PENDANTS,
               AetherTags.Items.ACCESSORIES_GLOVES,
               AetherTags.Items.ACCESSORIES_CAPES,
               AetherTags.Items.ACCESSORIES_MISCELLANEOUS,
               AetherTags.Items.ACCESSORIES_SHIELDS
            }
         );
      this.tag(net.neoforged.neoforge.common.Tags.Items.BOOKSHELVES).add(((Block)AetherBlocks.SKYROOT_BOOKSHELF.get()).asItem());
      this.tag(net.neoforged.neoforge.common.Tags.Items.BUCKETS)
         .add(new Item[]{(Item)AetherItems.SKYROOT_POISON_BUCKET.get(), (Item)AetherItems.SKYROOT_REMEDY_BUCKET.get()});
      this.tag(net.neoforged.neoforge.common.Tags.Items.BUCKETS_EMPTY).add((Item)AetherItems.SKYROOT_BUCKET.get());
      this.tag(net.neoforged.neoforge.common.Tags.Items.BUCKETS_WATER).add((Item)AetherItems.SKYROOT_WATER_BUCKET.get());
      this.tag(net.neoforged.neoforge.common.Tags.Items.BUCKETS_MILK).add((Item)AetherItems.SKYROOT_MILK_BUCKET.get());
      this.tag(net.neoforged.neoforge.common.Tags.Items.BUCKETS_POWDER_SNOW).add((Item)AetherItems.SKYROOT_POWDER_SNOW_BUCKET.get());
      this.tag(net.neoforged.neoforge.common.Tags.Items.BUCKETS_ENTITY_WATER)
         .add(
            new Item[]{
               (Item)AetherItems.SKYROOT_COD_BUCKET.get(),
               (Item)AetherItems.SKYROOT_SALMON_BUCKET.get(),
               (Item)AetherItems.SKYROOT_PUFFERFISH_BUCKET.get(),
               (Item)AetherItems.SKYROOT_TROPICAL_FISH_BUCKET.get(),
               (Item)AetherItems.SKYROOT_AXOLOTL_BUCKET.get(),
               (Item)AetherItems.SKYROOT_TADPOLE_BUCKET.get()
            }
         );
      this.tag(net.neoforged.neoforge.common.Tags.Items.FENCE_GATES_WOODEN).add(((FenceGateBlock)AetherBlocks.SKYROOT_FENCE_GATE.get()).asItem());
      this.tag(net.neoforged.neoforge.common.Tags.Items.FENCES_WOODEN).add(((FenceBlock)AetherBlocks.SKYROOT_FENCE.get()).asItem());
      this.tag(net.neoforged.neoforge.common.Tags.Items.FENCE_GATES).add(((FenceGateBlock)AetherBlocks.SKYROOT_FENCE_GATE.get()).asItem());
      this.tag(net.neoforged.neoforge.common.Tags.Items.FENCES).add(((FenceBlock)AetherBlocks.SKYROOT_FENCE.get()).asItem());
      this.tag(net.neoforged.neoforge.common.Tags.Items.EGGS).addTag(AetherTags.Items.MOA_EGGS);
      this.tag(net.neoforged.neoforge.common.Tags.Items.FOODS_FRUIT).add((Item)AetherItems.WHITE_APPLE.get());
      this.tag(net.neoforged.neoforge.common.Tags.Items.FOODS_BERRY)
         .add(new Item[]{(Item)AetherItems.BLUE_BERRY.get(), (Item)AetherItems.ENCHANTED_BERRY.get()});
      this.tag(net.neoforged.neoforge.common.Tags.Items.FOODS_COOKIE).add((Item)AetherItems.GINGERBREAD_MAN.get());
      this.tag(net.neoforged.neoforge.common.Tags.Items.FOODS_CANDY)
         .add(new Item[]{(Item)AetherItems.BLUE_GUMMY_SWET.get(), (Item)AetherItems.GOLDEN_GUMMY_SWET.get(), (Item)AetherItems.CANDY_CANE.get()});
      this.tag(net.neoforged.neoforge.common.Tags.Items.FOODS).add((Item)AetherItems.HEALING_STONE.get());
      this.tag(net.neoforged.neoforge.common.Tags.Items.ANIMAL_FOODS)
         .addTags(
            new TagKey[]{
               AetherTags.Items.PHYG_TEMPTATION_ITEMS,
               AetherTags.Items.FLYING_COW_TEMPTATION_ITEMS,
               AetherTags.Items.SHEEPUFF_TEMPTATION_ITEMS,
               AetherTags.Items.AERBUNNY_TEMPTATION_ITEMS,
               AetherTags.Items.MOA_FOOD_ITEMS
            }
         );
      this.tag(net.neoforged.neoforge.common.Tags.Items.GEMS).add(new Item[]{(Item)AetherItems.AMBROSIUM_SHARD.get(), (Item)AetherItems.ZANITE_GEMSTONE.get()});
      this.tag(net.neoforged.neoforge.common.Tags.Items.HIDDEN_FROM_RECIPE_VIEWERS).add(((Item)AetherItems.AETHER_PORTAL_FRAME.get()).asItem());
      this.tag(net.neoforged.neoforge.common.Tags.Items.GLASS_BLOCKS_COLORLESS).add(((TransparentBlock)AetherBlocks.QUICKSOIL_GLASS.get()).asItem());
      this.tag(net.neoforged.neoforge.common.Tags.Items.GLASS_BLOCKS_CHEAP).add(((TransparentBlock)AetherBlocks.QUICKSOIL_GLASS.get()).asItem());
      this.tag(net.neoforged.neoforge.common.Tags.Items.GLASS_PANES_COLORLESS).add(((IronBarsBlock)AetherBlocks.QUICKSOIL_GLASS_PANE.get()).asItem());
      this.tag(net.neoforged.neoforge.common.Tags.Items.MUSIC_DISCS)
         .add(
            new Item[]{
               (Item)AetherItems.MUSIC_DISC_AETHER_TUNE.get(),
               (Item)AetherItems.MUSIC_DISC_ASCENDING_DAWN.get(),
               (Item)AetherItems.MUSIC_DISC_CHINCHILLA.get(),
               (Item)AetherItems.MUSIC_DISC_HIGH.get(),
               (Item)AetherItems.MUSIC_DISC_KLEPTO.get(),
               (Item)AetherItems.MUSIC_DISC_SLIDERS_WRATH.get()
            }
         );
      this.tag(net.neoforged.neoforge.common.Tags.Items.ORE_RATES_SINGULAR)
         .add(
            new Item[]{
               ((Block)AetherBlocks.AMBROSIUM_ORE.get()).asItem(),
               ((Block)AetherBlocks.ZANITE_ORE.get()).asItem(),
               ((Block)AetherBlocks.GRAVITITE_ORE.get()).asItem()
            }
         );
      this.tag(net.neoforged.neoforge.common.Tags.Items.ORES)
         .add(
            new Item[]{
               ((Block)AetherBlocks.AMBROSIUM_ORE.get()).asItem(),
               ((Block)AetherBlocks.ZANITE_ORE.get()).asItem(),
               ((Block)AetherBlocks.GRAVITITE_ORE.get()).asItem()
            }
         );
      this.tag(net.neoforged.neoforge.common.Tags.Items.RODS_WOODEN).add((Item)AetherItems.SKYROOT_STICK.get());
      this.tag(net.neoforged.neoforge.common.Tags.Items.SLIME_BALLS).addTag(AetherTags.Items.SWET_BALLS);
      this.tag(net.neoforged.neoforge.common.Tags.Items.STONES).addTag(AetherTags.Items.HOLYSTONE);
      this.tag(net.neoforged.neoforge.common.Tags.Items.STORAGE_BLOCKS)
         .add(new Item[]{((Block)AetherBlocks.AMBROSIUM_BLOCK.get()).asItem(), ((Block)AetherBlocks.ZANITE_BLOCK.get()).asItem()});
      this.tag(net.neoforged.neoforge.common.Tags.Items.TOOLS).addTag(AetherTags.Items.TOOLS_HAMMERS);
      this.tag(net.neoforged.neoforge.common.Tags.Items.TOOLS_BOW).add((Item)AetherItems.PHOENIX_BOW.get());
      this.tag(net.neoforged.neoforge.common.Tags.Items.MELEE_WEAPON_TOOLS)
         .add(
            new Item[]{
               (Item)AetherItems.SKYROOT_SWORD.get(),
               (Item)AetherItems.HOLYSTONE_SWORD.get(),
               (Item)AetherItems.ZANITE_SWORD.get(),
               (Item)AetherItems.GRAVITITE_SWORD.get(),
               (Item)AetherItems.FLAMING_SWORD.get(),
               (Item)AetherItems.LIGHTNING_SWORD.get(),
               (Item)AetherItems.HOLY_SWORD.get(),
               (Item)AetherItems.VAMPIRE_BLADE.get(),
               (Item)AetherItems.PIG_SLAYER.get(),
               (Item)AetherItems.CANDY_CANE_SWORD.get(),
               (Item)AetherItems.VALKYRIE_LANCE.get(),
               (Item)AetherItems.HAMMER_OF_KINGBDOGZ.get(),
               (Item)AetherItems.SKYROOT_AXE.get(),
               (Item)AetherItems.HOLYSTONE_AXE.get(),
               (Item)AetherItems.ZANITE_AXE.get(),
               (Item)AetherItems.GRAVITITE_AXE.get(),
               (Item)AetherItems.VALKYRIE_AXE.get()
            }
         );
      this.tag(net.neoforged.neoforge.common.Tags.Items.RANGED_WEAPON_TOOLS)
         .add(
            new Item[]{
               (Item)AetherItems.GOLDEN_DART_SHOOTER.get(),
               (Item)AetherItems.POISON_DART_SHOOTER.get(),
               (Item)AetherItems.ENCHANTED_DART_SHOOTER.get(),
               (Item)AetherItems.PHOENIX_BOW.get(),
               (Item)AetherItems.HAMMER_OF_KINGBDOGZ.get(),
               (Item)AetherItems.LIGHTNING_KNIFE.get()
            }
         );
      this.tag(net.neoforged.neoforge.common.Tags.Items.MINING_TOOL_TOOLS)
         .add(
            new Item[]{
               (Item)AetherItems.SKYROOT_PICKAXE.get(),
               (Item)AetherItems.HOLYSTONE_PICKAXE.get(),
               (Item)AetherItems.ZANITE_PICKAXE.get(),
               (Item)AetherItems.GRAVITITE_PICKAXE.get(),
               (Item)AetherItems.VALKYRIE_PICKAXE.get()
            }
         );
      this.tag(AetherTags.Items.RANDOMIUM_BLACKLIST)
         .addTags(
            new TagKey[]{
               AetherTags.Items.LOCKED_DUNGEON_BLOCKS,
               AetherTags.Items.TRAPPED_DUNGEON_BLOCKS,
               AetherTags.Items.BOSS_DOORWAY_DUNGEON_BLOCKS,
               AetherTags.Items.TREASURE_DOORWAY_DUNGEON_BLOCKS
            }
         )
         .add(new Item[]{((Block)AetherBlocks.CHEST_MIMIC.get()).asItem(), ((Block)AetherBlocks.TREASURE_CHEST.get()).asItem()});
      this.tag(ItemTags.STONE_CRAFTING_MATERIALS).add(((Block)AetherBlocks.HOLYSTONE.get()).asItem());
      this.tag(ItemTags.WOODEN_STAIRS).add(((StairBlock)AetherBlocks.SKYROOT_STAIRS.get()).asItem());
      this.tag(ItemTags.WOODEN_SLABS).add(((SlabBlock)AetherBlocks.SKYROOT_SLAB.get()).asItem());
      this.tag(ItemTags.WOODEN_FENCES).add(((FenceBlock)AetherBlocks.SKYROOT_FENCE.get()).asItem());
      this.tag(ItemTags.WOODEN_DOORS).add(((DoorBlock)AetherBlocks.SKYROOT_DOOR.get()).asItem());
      this.tag(ItemTags.WOODEN_TRAPDOORS).add(((TrapDoorBlock)AetherBlocks.SKYROOT_TRAPDOOR.get()).asItem());
      this.tag(ItemTags.WOODEN_BUTTONS).add(((ButtonBlock)AetherBlocks.SKYROOT_BUTTON.get()).asItem());
      this.tag(ItemTags.STONE_BUTTONS).add(((ButtonBlock)AetherBlocks.HOLYSTONE_BUTTON.get()).asItem());
      this.tag(ItemTags.WOODEN_PRESSURE_PLATES).add(((PressurePlateBlock)AetherBlocks.SKYROOT_PRESSURE_PLATE.get()).asItem());
      this.tag(ItemTags.SAPLINGS)
         .add(new Item[]{((SaplingBlock)AetherBlocks.SKYROOT_SAPLING.get()).asItem(), ((SaplingBlock)AetherBlocks.GOLDEN_OAK_SAPLING.get()).asItem()});
      this.tag(ItemTags.LOGS_THAT_BURN).addTags(new TagKey[]{AetherTags.Items.SKYROOT_LOGS, AetherTags.Items.GOLDEN_OAK_LOGS});
      this.tag(ItemTags.STAIRS)
         .add(
            new Item[]{
               ((StairBlock)AetherBlocks.SKYROOT_STAIRS.get()).asItem(),
               ((StairBlock)AetherBlocks.CARVED_STAIRS.get()).asItem(),
               ((StairBlock)AetherBlocks.ANGELIC_STAIRS.get()).asItem(),
               ((StairBlock)AetherBlocks.HELLFIRE_STAIRS.get()).asItem(),
               ((StairBlock)AetherBlocks.HOLYSTONE_STAIRS.get()).asItem(),
               ((StairBlock)AetherBlocks.MOSSY_HOLYSTONE_STAIRS.get()).asItem(),
               ((StairBlock)AetherBlocks.ICESTONE_STAIRS.get()).asItem(),
               ((StairBlock)AetherBlocks.HOLYSTONE_BRICK_STAIRS.get()).asItem(),
               ((StairBlock)AetherBlocks.AEROGEL_STAIRS.get()).asItem()
            }
         );
      this.tag(ItemTags.SLABS)
         .add(
            new Item[]{
               ((SlabBlock)AetherBlocks.SKYROOT_SLAB.get()).asItem(),
               ((SlabBlock)AetherBlocks.CARVED_SLAB.get()).asItem(),
               ((SlabBlock)AetherBlocks.ANGELIC_SLAB.get()).asItem(),
               ((SlabBlock)AetherBlocks.HELLFIRE_SLAB.get()).asItem(),
               ((SlabBlock)AetherBlocks.HOLYSTONE_SLAB.get()).asItem(),
               ((SlabBlock)AetherBlocks.MOSSY_HOLYSTONE_SLAB.get()).asItem(),
               ((SlabBlock)AetherBlocks.ICESTONE_SLAB.get()).asItem(),
               ((SlabBlock)AetherBlocks.HOLYSTONE_BRICK_SLAB.get()).asItem(),
               ((SlabBlock)AetherBlocks.AEROGEL_SLAB.get()).asItem()
            }
         );
      this.tag(ItemTags.WALLS)
         .add(
            new Item[]{
               ((WallBlock)AetherBlocks.CARVED_WALL.get()).asItem(),
               ((WallBlock)AetherBlocks.ANGELIC_WALL.get()).asItem(),
               ((WallBlock)AetherBlocks.HELLFIRE_WALL.get()).asItem(),
               ((WallBlock)AetherBlocks.HOLYSTONE_WALL.get()).asItem(),
               ((WallBlock)AetherBlocks.MOSSY_HOLYSTONE_WALL.get()).asItem(),
               ((WallBlock)AetherBlocks.ICESTONE_WALL.get()).asItem(),
               ((WallBlock)AetherBlocks.HOLYSTONE_BRICK_WALL.get()).asItem(),
               ((WallBlock)AetherBlocks.AEROGEL_WALL.get()).asItem()
            }
         );
      this.tag(ItemTags.LEAVES)
         .add(
            new Item[]{
               ((Block)AetherBlocks.SKYROOT_LEAVES.get()).asItem(),
               ((Block)AetherBlocks.GOLDEN_OAK_LEAVES.get()).asItem(),
               ((Block)AetherBlocks.CRYSTAL_LEAVES.get()).asItem(),
               ((Block)AetherBlocks.CRYSTAL_FRUIT_LEAVES.get()).asItem(),
               ((Block)AetherBlocks.HOLIDAY_LEAVES.get()).asItem(),
               ((Block)AetherBlocks.DECORATED_HOLIDAY_LEAVES.get()).asItem()
            }
         );
      this.tag(ItemTags.SMALL_FLOWERS).add(new Item[]{((Block)AetherBlocks.PURPLE_FLOWER.get()).asItem(), ((Block)AetherBlocks.WHITE_FLOWER.get()).asItem()});
      this.tag(ItemTags.BEDS).add(((BedBlock)AetherBlocks.SKYROOT_BED.get()).asItem());
      this.tag(ItemTags.PIGLIN_LOVED)
         .add(
            new Item[]{
               (Item)AetherItems.VICTORY_MEDAL.get(),
               (Item)AetherItems.GOLDEN_RING.get(),
               (Item)AetherItems.GOLDEN_PENDANT.get(),
               (Item)AetherItems.GOLDEN_GLOVES.get()
            }
         );
      this.tag(ItemTags.FOX_FOOD).add(new Item[]{(Item)AetherItems.BLUE_BERRY.get(), (Item)AetherItems.ENCHANTED_BERRY.get()});
      this.tag(ItemTags.SIGNS).add(((StandingSignBlock)AetherBlocks.SKYROOT_SIGN.get()).asItem());
      this.tag(ItemTags.HANGING_SIGNS).add(((CeilingHangingSignBlock)AetherBlocks.SKYROOT_HANGING_SIGN.get()).asItem());
      this.tag(ItemTags.BOOKSHELF_BOOKS).add((Item)AetherItems.BOOK_OF_LORE.get());
      this.tag(ItemTags.BEACON_PAYMENT_ITEMS)
         .add(new Item[]{(Item)AetherItems.ZANITE_GEMSTONE.get(), ((Block)AetherBlocks.ENCHANTED_GRAVITITE.get()).asItem()});
      this.tag(ItemTags.BOATS).add((Item)AetherItems.SKYROOT_BOAT.get());
      this.tag(ItemTags.CHEST_BOATS).add((Item)AetherItems.SKYROOT_CHEST_BOAT.get());
      this.tag(ItemTags.CLUSTER_MAX_HARVESTABLES)
         .add(
            new Item[]{
               (Item)AetherItems.SKYROOT_PICKAXE.get(),
               (Item)AetherItems.HOLYSTONE_PICKAXE.get(),
               (Item)AetherItems.ZANITE_PICKAXE.get(),
               (Item)AetherItems.GRAVITITE_PICKAXE.get(),
               (Item)AetherItems.VALKYRIE_PICKAXE.get()
            }
         );
      this.tag(ItemTags.SWORDS)
         .add(
            new Item[]{
               (Item)AetherItems.SKYROOT_SWORD.get(),
               (Item)AetherItems.HOLYSTONE_SWORD.get(),
               (Item)AetherItems.ZANITE_SWORD.get(),
               (Item)AetherItems.GRAVITITE_SWORD.get(),
               (Item)AetherItems.FLAMING_SWORD.get(),
               (Item)AetherItems.LIGHTNING_SWORD.get(),
               (Item)AetherItems.HOLY_SWORD.get(),
               (Item)AetherItems.VAMPIRE_BLADE.get(),
               (Item)AetherItems.PIG_SLAYER.get(),
               (Item)AetherItems.CANDY_CANE_SWORD.get()
            }
         )
         .addTag(AetherTags.Items.TOOLS_LANCES);
      this.tag(ItemTags.AXES)
         .add(
            new Item[]{
               (Item)AetherItems.SKYROOT_AXE.get(),
               (Item)AetherItems.HOLYSTONE_AXE.get(),
               (Item)AetherItems.ZANITE_AXE.get(),
               (Item)AetherItems.GRAVITITE_AXE.get(),
               (Item)AetherItems.VALKYRIE_AXE.get()
            }
         );
      this.tag(ItemTags.PICKAXES)
         .add(
            new Item[]{
               (Item)AetherItems.SKYROOT_PICKAXE.get(),
               (Item)AetherItems.HOLYSTONE_PICKAXE.get(),
               (Item)AetherItems.ZANITE_PICKAXE.get(),
               (Item)AetherItems.GRAVITITE_PICKAXE.get(),
               (Item)AetherItems.VALKYRIE_PICKAXE.get()
            }
         );
      this.tag(ItemTags.SHOVELS)
         .add(
            new Item[]{
               (Item)AetherItems.SKYROOT_SHOVEL.get(),
               (Item)AetherItems.HOLYSTONE_SHOVEL.get(),
               (Item)AetherItems.ZANITE_SHOVEL.get(),
               (Item)AetherItems.GRAVITITE_SHOVEL.get(),
               (Item)AetherItems.VALKYRIE_SHOVEL.get()
            }
         );
      this.tag(ItemTags.HOES)
         .add(
            new Item[]{
               (Item)AetherItems.SKYROOT_HOE.get(),
               (Item)AetherItems.HOLYSTONE_HOE.get(),
               (Item)AetherItems.ZANITE_HOE.get(),
               (Item)AetherItems.GRAVITITE_HOE.get(),
               (Item)AetherItems.VALKYRIE_HOE.get()
            }
         );
      this.tag(ItemTags.HEAD_ARMOR)
         .add(
            new Item[]{
               (Item)AetherItems.ZANITE_HELMET.get(),
               (Item)AetherItems.GRAVITITE_HELMET.get(),
               (Item)AetherItems.NEPTUNE_HELMET.get(),
               (Item)AetherItems.PHOENIX_HELMET.get(),
               (Item)AetherItems.OBSIDIAN_HELMET.get(),
               (Item)AetherItems.VALKYRIE_HELMET.get()
            }
         );
      this.tag(ItemTags.CHEST_ARMOR)
         .add(
            new Item[]{
               (Item)AetherItems.ZANITE_CHESTPLATE.get(),
               (Item)AetherItems.GRAVITITE_CHESTPLATE.get(),
               (Item)AetherItems.NEPTUNE_CHESTPLATE.get(),
               (Item)AetherItems.PHOENIX_CHESTPLATE.get(),
               (Item)AetherItems.OBSIDIAN_CHESTPLATE.get(),
               (Item)AetherItems.VALKYRIE_CHESTPLATE.get()
            }
         );
      this.tag(ItemTags.LEG_ARMOR)
         .add(
            new Item[]{
               (Item)AetherItems.ZANITE_LEGGINGS.get(),
               (Item)AetherItems.GRAVITITE_LEGGINGS.get(),
               (Item)AetherItems.NEPTUNE_LEGGINGS.get(),
               (Item)AetherItems.PHOENIX_LEGGINGS.get(),
               (Item)AetherItems.OBSIDIAN_LEGGINGS.get(),
               (Item)AetherItems.VALKYRIE_LEGGINGS.get()
            }
         );
      this.tag(ItemTags.FOOT_ARMOR)
         .add(
            new Item[]{
               (Item)AetherItems.ZANITE_BOOTS.get(),
               (Item)AetherItems.GRAVITITE_BOOTS.get(),
               (Item)AetherItems.NEPTUNE_BOOTS.get(),
               (Item)AetherItems.PHOENIX_BOOTS.get(),
               (Item)AetherItems.OBSIDIAN_BOOTS.get(),
               (Item)AetherItems.VALKYRIE_BOOTS.get(),
               (Item)AetherItems.SENTRY_BOOTS.get()
            }
         );
      this.tag(ItemTags.TRIMMABLE_ARMOR)
         .add(
            new Item[]{
               (Item)AetherItems.ZANITE_HELMET.get(),
               (Item)AetherItems.ZANITE_CHESTPLATE.get(),
               (Item)AetherItems.ZANITE_LEGGINGS.get(),
               (Item)AetherItems.ZANITE_BOOTS.get(),
               (Item)AetherItems.ZANITE_GLOVES.get(),
               (Item)AetherItems.GRAVITITE_HELMET.get(),
               (Item)AetherItems.GRAVITITE_CHESTPLATE.get(),
               (Item)AetherItems.GRAVITITE_LEGGINGS.get(),
               (Item)AetherItems.GRAVITITE_BOOTS.get(),
               (Item)AetherItems.GRAVITITE_GLOVES.get(),
               (Item)AetherItems.NEPTUNE_HELMET.get(),
               (Item)AetherItems.NEPTUNE_CHESTPLATE.get(),
               (Item)AetherItems.NEPTUNE_LEGGINGS.get(),
               (Item)AetherItems.NEPTUNE_BOOTS.get(),
               (Item)AetherItems.NEPTUNE_GLOVES.get(),
               (Item)AetherItems.PHOENIX_HELMET.get(),
               (Item)AetherItems.PHOENIX_CHESTPLATE.get(),
               (Item)AetherItems.PHOENIX_LEGGINGS.get(),
               (Item)AetherItems.PHOENIX_BOOTS.get(),
               (Item)AetherItems.PHOENIX_GLOVES.get(),
               (Item)AetherItems.OBSIDIAN_HELMET.get(),
               (Item)AetherItems.OBSIDIAN_CHESTPLATE.get(),
               (Item)AetherItems.OBSIDIAN_LEGGINGS.get(),
               (Item)AetherItems.OBSIDIAN_BOOTS.get(),
               (Item)AetherItems.OBSIDIAN_GLOVES.get(),
               (Item)AetherItems.LEATHER_GLOVES.get(),
               (Item)AetherItems.IRON_GLOVES.get(),
               (Item)AetherItems.GOLDEN_GLOVES.get(),
               (Item)AetherItems.DIAMOND_GLOVES.get(),
               (Item)AetherItems.NETHERITE_GLOVES.get(),
               (Item)AetherItems.CHAINMAIL_GLOVES.get()
            }
         );
      this.tag(ItemTags.TRIM_MATERIALS)
         .add(
            new Item[]{(Item)AetherItems.ZANITE_GEMSTONE.get(), ((Block)AetherBlocks.ENCHANTED_GRAVITITE.get()).asItem(), (Item)AetherItems.GOLDEN_AMBER.get()}
         );
      this.tag(ItemTags.DYEABLE).add((Item)AetherItems.LEATHER_GLOVES.get());
      this.tag(ItemTags.DURABILITY_ENCHANTABLE)
         .addTag(AetherTags.Items.DART_SHOOTERS)
         .addTag(AetherTags.Items.ACCESSORIES_GLOVES)
         .add(
            new Item[]{
               (Item)AetherItems.HAMMER_OF_KINGBDOGZ.get(),
               (Item)AetherItems.LIGHTNING_KNIFE.get(),
               (Item)AetherItems.ZANITE_RING.get(),
               (Item)AetherItems.ICE_RING.get(),
               (Item)AetherItems.ZANITE_PENDANT.get(),
               (Item)AetherItems.ICE_PENDANT.get(),
               (Item)AetherItems.SHIELD_OF_REPULSION.get(),
               (Item)AetherItems.COLD_PARACHUTE.get(),
               (Item)AetherItems.GOLDEN_PARACHUTE.get(),
               (Item)AetherItems.NATURE_STAFF.get(),
               (Item)AetherItems.CLOUD_STAFF.get()
            }
         );
      this.tag(ItemTags.BOW_ENCHANTABLE).addTag(AetherTags.Items.DART_SHOOTERS).add((Item)AetherItems.PHOENIX_BOW.get());
      this.tag(ItemTags.VANISHING_ENCHANTABLE).addTag(AetherTags.Items.ACCESSORIES);
   }
}
