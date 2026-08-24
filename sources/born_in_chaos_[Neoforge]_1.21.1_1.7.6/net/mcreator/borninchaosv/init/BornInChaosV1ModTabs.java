package net.mcreator.borninchaosv.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BornInChaosV1ModTabs {
   public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "born_in_chaos_v1");
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BORNIN_CHAOS_MOBS = REGISTRY.register(
      "bornin_chaos_mobs",
      () -> CreativeModeTab.builder()
         .title(Component.translatable("item_group.born_in_chaos_v1.bornin_chaos_mobs"))
         .icon(() -> new ItemStack((ItemLike)BornInChaosV1ModItems.SHATTERED_SKULL.get()))
         .displayItems((parameters, tabData) -> {
            tabData.accept((ItemLike)BornInChaosV1ModItems.DECREPIT_SKELETON_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SKELETON_DEMOMAN_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.BABY_SKELETON_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.BONE_IMP_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SIAMESE_SKELETONS_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SKELETON_THRASHER_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.BONESCALLER_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SUPREME_BONESCALLER_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SPIRIT_GUIDE_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.LIFESTEALER_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.DECAYING_ZOMBIE_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.BARREL_ZOMBIE_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.DOOR_KNIGHT_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.ZOMBIE_CLOWN_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.ZOMBIE_FISHERMAN_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.ZOMBIE_LUMBERJACK_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.ZOMBIE_BRUISER_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.FALLEN_CHAOS_KNIGHT_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.MAGGOT_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.CORPSE_FLY_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.BLOODY_GADFLY_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SWARMER_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.DIAMOND_TERMITE_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.FIRELIGHT_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.BABY_SPIDER_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.MOTHER_SPIDER_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.DREAD_HOUND_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.DIRE_HOUND_LEADER_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.THORNSHELL_CRAB_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.CORPSE_FISH_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.GLUTTON_FISH_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.RESTLESS_SPIRIT_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SCARLET_PERSECUTOR_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.PHANTOM_CREEPER_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.DARK_VORTEX_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SPIRITOF_CHAOS_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.NIGHTMARE_STALKER_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.MISSIONER_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.PUMPKIN_SPIRIT_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SEARED_SPIRIT_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.KRAMPUS_HENCHMAN_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.KRAMPUS_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.MR_PUMPKIN_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SIR_PUMPKINHEAD_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SENOR_PUMPKIN_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.LORD_PUMPKINHEAD_SPAWN_EGG.get());
         })
         .build()
   );
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BLOCK_BORIN_CHAOS = REGISTRY.register(
      "block_borin_chaos",
      () -> CreativeModeTab.builder()
         .title(Component.translatable("item_group.born_in_chaos_v1.block_borin_chaos"))
         .icon(() -> new ItemStack((ItemLike)BornInChaosV1ModBlocks.DARK_METAL_BLOCK.get()))
         .displayItems((parameters, tabData) -> {
            tabData.accept(((Block)BornInChaosV1ModBlocks.BLACK_ARGILLITE.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.BLACK_ARGILLITE_STAIRS.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.BLACK_ARGILLITE_SLAB.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.BLACK_ARGILLITE_WALL.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.BLACK_ARGILLITEPRESSUREPLATE.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.BLACK_ARGILLITE_BUTTON.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.FIRED_BLACK_ARGILLITE.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.BLACK_ARGILLITE_BRICK.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.CHIPPED_BLACK_ARGILLITE_BRICK.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.MOSSY_BLACK_ARGILLITE_BRICK.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.STAINED_BLACK_ARGILLITE_BRICK.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.BLACK_ARGILLITE_BRICK_STAIRS.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.BLACK_ARGILLITE_BRICK_SLAB.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.BLACK_ARGILLITE_BRICK_WALL.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.BLACK_ARGILLITE_COLUMN.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.BLACK_ARGILLITE_COLUMN_SLAB.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.CARVED_BLACK_ARGILLITE.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.SCORCHED_LOG.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.SCORCHED_WOOD.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.SMOLDERING_SCORCHED_LOG.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.SMOLDERING_SCORCHED_WOOD.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.STRIPPED_SCORCHED_LOG.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.STRIPPED_SCORCHED_WOOD.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.SCORCHED_PLANKS.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.SCORCHED_PLANKS_STAIRS.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.SCORCHED_PLANKS_SLAB.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.SCORCHED_PLANKS_FENCE.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.SCORCHED_PLANKS_FENCE_GATE.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.SCORCHED_PLANKS_PRESSURE_PLATES.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.SCORCHED_PLANKS_BUTTON.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.SCORCHED_PLANKS_TRAPDOOR.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.SCORCHED_PLANKS_DOOR.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.MESH_DOOR.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.DARK_METAL_DEPOSIT.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.DARK_METAL_BLOCK.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.DARK_GRID.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.CULTIVATED_PUMPKIN.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.EVIL_CARVED_PUMPKIN.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.FLAMING_EVIL_PUMPKIN.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.INFERNAL_EVIL_PUMPKIN.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.ROTTEN_INFERNAL_PUMPKIN.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.SPOOKY_SNOWMAN_HEAD.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.CREEPY_NUTCRACKER.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.BUNDLE_OF_BONES.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.PILE_OF_SKULLS.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.GNAWED_BONES.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.NIGHTMARE_STALKER_SKULL.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.LIFESTEALER_SKULL.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.ARGILLITE_LAMP.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.DARK_STAINED_GLASS.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.DARK_STAINED_GLASS_PANEL.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.SCARLET_STAINED_GLASS.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.SCARLET_STAINED_GLASS_PANEL.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.ORANGE_STAINED_GLASS.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.ORANGE_STAINED_GLASS_PANEL.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.GREEN_STAINED_GLASS.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.GREEN_STAINED_GLASS_PANEL.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.PURPLE_STAINED_GLASS.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.PURPLE_STAINED_GLASS_PANEL.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.FEL_SOIL.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.ROTTEN_SOIL.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.WEBBED_COBBLESTONE.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.COBWEB_COVER.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.MARIGOLDS.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.RIVER_MINT.get()).asItem());
         })
         .withTabsBefore(new ResourceLocation[]{BORNIN_CHAOS_MOBS.getId()})
         .build()
   );
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BORNIN_CHAOS_ITEMS = REGISTRY.register(
      "bornin_chaos_items",
      () -> CreativeModeTab.builder()
         .title(Component.translatable("item_group.born_in_chaos_v1.bornin_chaos_items"))
         .icon(() -> new ItemStack((ItemLike)BornInChaosV1ModItems.FIRE_DUST.get()))
         .displayItems((parameters, tabData) -> {
            tabData.accept((ItemLike)BornInChaosV1ModItems.SHATTERED_SKULL.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.FUSED_BONE.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.BONE_HEART.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.BONE_HANDLE.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.DARK_ROD.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.DARK_CHARGE.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.LIFESTEALER_BONE.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.DARK_ATRIUM.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.ETHEREAL_SPIRIT.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SPIRITUAL_DUST.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.FIRE_DUST.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.PHANTOM_POWDER.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SEEDOF_CHAOS.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.TRANSFORMATIVE_FLOWER.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.ORBOFTHE_SUMMONER.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.PIECEOFDARKMETAL.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.PILEOF_DARK_METAL.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.DARK_METAL_INGOT.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.DARK_METAL_NUGGET.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.ARMOR_PLATE_FROM_DARK_METAL.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.DARK_UPGRADE.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.DIAMOND_TERMITE_SHARD.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.PERMAFROST_SHARD.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.NIGHTMARE_CLAW.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.MONSTER_SKIN.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.FANGOFTHE_HOUND_LEADER.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SPINY_SHELL.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.CORPSE_MAGGOT.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.FRIED_MAGGOT.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.MONSTER_FLESH.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SMOKED_MONSTER_FLESH.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.ROTTEN_FISH.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SMOKED_FISH.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SMOKED_FLESH.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SEA_TERROR_STOMACH.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SEA_TERROR_EYE.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.BLOODY_GADFLY_EYE.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.KRAMPUS_HORN.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SPIDER_MANDIBLE.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.KRAMPUSS_BAG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.BAGOF_CANDY.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.MINT_CANDY.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.MINT_ICE_CREAM.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.COFFEE_CANDY.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.GUMMY_VAMPIRE_TEETH.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.CHOCOLATE_HEART.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.CARAMEL_PEPPER.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.HOLIDAY_CANDY.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.MAGICAL_HOLIDAY_CANDY.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.ETERNAL_CANDY.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.CREEPY_COOKIES_WITH_MILK.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SPIRITUAL_GINGERBREAD.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.TRANSFORMING_EASTER_CAKE.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.EVILOMETER.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.CHAOS_COMPONENT.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.BOTTLE_OF_MAGICAL_ENERGY.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.ELIXIROF_INSECT_PROTECTION.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.POTION_OF_RAMPAGE.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.ELIXIR_OF_VAMPIRISM.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.ELIXIROF_WITHER_RESISTANCE.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.ELIXIROF_ICE_BARRIER.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.INTOXICATING_DECOCTION.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.STIMULATING_DECOCTION.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.TRANSMUTING_ELIXIR.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.CHARMOF_POWER.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.CHARMOF_RESISTANCE.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.CHARMOF_STEALTH.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.CHARMOF_ENDURANCE.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.CHARMOF_FURY.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SMOLDERING_INFERNAL_EMBER.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.FEL_LAMP.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.EMPTY_FEL_LAMP.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.LORD_PUMPKINHEADS_LAMP.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.LORD_PUMPKINHEADS_EMPTY_LAMP.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.ANLUKA_DOORS.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SERPUMPKINHEAD_M.get());
         })
         .withTabsBefore(new ResourceLocation[]{BLOCK_BORIN_CHAOS.getId()})
         .build()
   );
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WEAPONS_BORNIN_CHAOS = REGISTRY.register(
      "weapons_bornin_chaos",
      () -> CreativeModeTab.builder()
         .title(Component.translatable("item_group.born_in_chaos_v1.weapons_bornin_chaos"))
         .icon(() -> new ItemStack((ItemLike)BornInChaosV1ModItems.DARK_METAL_ARMOR_HELMET.get()))
         .displayItems((parameters, tabData) -> {
            tabData.accept((ItemLike)BornInChaosV1ModItems.DARK_METAL_ARMOR_HELMET.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.DARK_METAL_ARMOR_CHESTPLATE.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.DARK_METAL_ARMOR_LEGGINGS.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.DARK_METAL_ARMOR_BOOTS.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.DARKWARBLADE.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SKULLBREAKER_HAMMER.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.GREAT_REAPER_AXE.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_HELMET.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_CHESTPLATE.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_LEGGINGS.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_BOOTS.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.NIGHTMARE_SCYTHE.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SOUL_CUTLASS.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.FROSTBITTEN_BLADE.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.DARK_RITUAL_DAGGER.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SPIRITUAL_SWORD.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.STAFF_OF_MAGIC_ARROWS.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.BONESCALLER_STAFF.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.DEATH_TOTEM.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SHARPENED_DARK_METAL_SWORD.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SPIDER_BITE_SWORD.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.INTOXICATING_DAGGER.get());
            tabData.accept(((Block)BornInChaosV1ModBlocks.HOUND_TRAP.get()).asItem());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SPINY_SHELL_ARMOR_HELMET.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SPINY_SHELL_ARMOR_CHESTPLATE.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SHELL_MACE.get());
            tabData.accept(((Block)BornInChaosV1ModBlocks.SPINY_SHELL_TRAP.get()).asItem());
            tabData.accept((ItemLike)BornInChaosV1ModItems.DAMNED_DEMOMANS_HAT_HELMET.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.MISSIONARY_HAT_HELMET.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SPIRITUAL_GUIDE_SOMBRERO_HELMET.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.LORD_PUMPKINHEADS_HAT_HELMET.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SOULBANE.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.PUMPKINSTAFFA.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.PUMPKINHANDGUN.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.PUMPKIN_BULLET.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SWEET_SWORD.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SWEET_AXE.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.TRIDENT_HAYFORK.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.WOOD_SPLITTER_AXE.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.NUT_HAMMER.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.BIRCH_BRANCHES.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.STORMCALLERS_HORN.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.PHANTOM_BOMB.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.INTOXICATIND_BOMB.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.STIMULATING_BOMB.get());
         })
         .withTabsBefore(new ResourceLocation[]{BORNIN_CHAOS_ITEMS.getId()})
         .build()
   );
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BORNIN_CHAOS_CUSTOMIZATION = REGISTRY.register(
      "bornin_chaos_customization",
      () -> CreativeModeTab.builder()
         .title(Component.translatable("item_group.born_in_chaos_v1.bornin_chaos_customization"))
         .icon(() -> new ItemStack((ItemLike)BornInChaosV1ModItems.TRANSFORMATIVE_FLOWER.get()))
         .displayItems((parameters, tabData) -> {
            tabData.accept((ItemLike)BornInChaosV1ModItems.TRANSFORMATIVE_FLOWER.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.ICY_SWEETNESS.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.CARROT_SWORD.get());
         })
         .withTabsBefore(new ResourceLocation[]{WEAPONS_BORNIN_CHAOS.getId()})
         .build()
   );
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DEBUGGING_BORNIN_CHAOS = REGISTRY.register(
      "debugging_bornin_chaos",
      () -> CreativeModeTab.builder()
         .title(Component.translatable("item_group.born_in_chaos_v1.debugging_bornin_chaos"))
         .icon(() -> new ItemStack((ItemLike)BornInChaosV1ModItems.MAGICSKULL.get()))
         .displayItems((parameters, tabData) -> {
            tabData.accept(((Block)BornInChaosV1ModBlocks.BLACK_ARGILLITE_N.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.BLACK_ARGILLITE_STAIRS_N.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.BLACK_ARGILLITE_SLAB_N.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.BLACK_ARGILLITE_WALL_N.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.FIRED_BLACK_ARGILLITE_N.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.BLACK_ARGILLITE_BRICK_N.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.C_BLACK_ARGILLITE_BRICK_N.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.MOSSY_BLACK_ARGILLITE_BRICK_N.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.STAINED_BLACK_ARGILLITE_BRICK_N.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.BLACK_ARGILLITE_BRICK_STAIRS_N.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.BLACK_ARGILLITE_BRICK_SLAB_N.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.BLACK_ARGILLITE_BRICK_WALL_N.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.BLACK_ARGILLITE_COLUMN_N.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.BLACK_ARGILLITE_COLUMN_SLAB_N.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.CARVED_BLACK_ARGILLITE_N.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.CURSED_SCARLET_ORNATE_GLASS_PANE.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.FRAGILE_SCORCHED_BOARDS.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.DARK_ICE.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.INFECTED_DIAMOND_ORE.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.INFECTED_DEEPSLATE_DIAMOND_ORE.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.INFERNAL_EVIL_PUMPKIN_S.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.PUDDLEOFINTOXICATION.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.PUDDLEOF_STIMULATION.get()).asItem());
            tabData.accept((ItemLike)BornInChaosV1ModItems.DECAYING_ZOMBIE_NOT_DESPAWN_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.BONESCALLER_NOT_DESPAWN_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SKELETON_THRASHER_NOT_DESPAWN_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.DREAD_HOUND_NOT_DESPAWN_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.INFERNAL_SPIRIT_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.PUMPKIN_BOMB_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.PUMPKIN_DUNCE_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.MRS_PUMPKIN_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.PUMPKIN_BRUISER_SPAWN_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SPAWN_STRUCTURES_FIREWELL.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SPAWN_STRUCTURES_LOOKOUT_TOWER.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SPAWN_STRUCTURES_DARK_TOWER.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SPAWN_STRUCTURES_FARM.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SPAWN_STRUCTURES_CLOWN_CART.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SPAWN_STRUCTURE_MOUND_HOUNDS.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.STAFFOF_BLINDNESS.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.CREEPY_GIFT.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.ROTTEN_EASTER_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SPIRITUAL_EASTER_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.MONSTROUS_EASTER_EGG.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.KILLER_RABBIT_EARS_HELMET.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.SUPREME_MEASURE.get());
            tabData.accept((ItemLike)BornInChaosV1ModItems.STOP_HAMMER.get());
            tabData.accept(((Block)BornInChaosV1ModBlocks.TOMBSTONE_DARK_TITAN_066.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.TOMBSTONE_PETASI.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.TOMBSTONE_2003WISE.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.TOMBSTONE_ORION.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.TOMBSTONE_DERIVAS.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.TOMBSTONE_FUBUKI_BANZAI.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.TOMBSTONE_SOMFUNAMBULIST.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.TOMBSTONE_MEMESUS.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.TOMBSTONE_TEM_187.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.TOMBSTONE_CARRION.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.TOMBSTONE_2_DLING.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.TOMBSTONE_EUTHYMIA.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.TOMBSTONE_FERAL.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.TOMBSTONE_MIKE_RORY.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.TOMBSTONE_D_4RKDEVILX.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.TOMBSTONE_ROTBORNE.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.TOMBSTONE_THE_GENTLEMAN_FROG.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.TOMBSTONE_PLUG.get()).asItem());
            tabData.accept(((Block)BornInChaosV1ModBlocks.TOMBSTONE_NINO_4416.get()).asItem());
         })
         .withTabsBefore(new ResourceLocation[]{BORNIN_CHAOS_CUSTOMIZATION.getId()})
         .build()
   );
}
