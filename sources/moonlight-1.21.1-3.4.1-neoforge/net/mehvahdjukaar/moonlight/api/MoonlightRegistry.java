package net.mehvahdjukaar.moonlight.api;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.api.item.additional_placements.BlockPlacerItem;
import net.mehvahdjukaar.moonlight.api.map.MLMapDecorationsComponent;
import net.mehvahdjukaar.moonlight.api.misc.WorldSavedDataType;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.trades.ModItemListing;
import net.mehvahdjukaar.moonlight.api.util.PotionBottleType;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.criteria_triggers.GrindItemTrigger;
import net.mehvahdjukaar.moonlight.core.loot.ConfigItemPoolEntry;
import net.mehvahdjukaar.moonlight.core.loot.OptionalItemPoolEntry;
import net.mehvahdjukaar.moonlight.core.loot.OptionalPropertyCondition;
import net.mehvahdjukaar.moonlight.core.loot.PatternMatchLootItemCondition;
import net.mehvahdjukaar.moonlight.core.loot.ResourceLootItemCondition;
import net.mehvahdjukaar.moonlight.core.worldgen.CaveFilter;
import net.mehvahdjukaar.moonlight.core.worldgen.HeightRangeFilter;
import net.mehvahdjukaar.moonlight.core.worldgen.SpawnBoxBlock;
import net.mehvahdjukaar.moonlight.core.worldgen.SpawnBoxBlockEntity;
import net.mehvahdjukaar.moonlight.core.worldgen.SpawnBoxPoolElement;
import net.mehvahdjukaar.moonlight.core.worldgen.SpawnBoxStructurePiece;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.item.GameMasterBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.ApiStatus.Internal;

public class MoonlightRegistry {
   public static final Registry<MapCodec<? extends ModItemListing>> VILLAGER_TRADES_REGISTRY = RegHelper.registerRegistry(
      Moonlight.res("villager_trades"), false
   );
   public static final Registry<WorldSavedDataType<?>> WORLD_SAVED_DATA_TYPE_REGISTRY = RegHelper.registerRegistry(Moonlight.res("world_saved_data_type"), true);
   public static final Supplier<PlacementModifierType<CaveFilter>> CAVE_MODIFIER = RegHelper.registerPlacementModifier(
      Moonlight.res("below_heightmaps"), CaveFilter.CODEC
   );
   public static final Supplier<PlacementModifierType<HeightRangeFilter>> HEIGHT_RANGE = RegHelper.registerPlacementModifier(
      Moonlight.res("height_range_filter"), HeightRangeFilter.CODEC
   );
   public static final Supplier<BlockPlacerItem> BLOCK_PLACER = RegHelper.registerItem(
      Moonlight.res("placeable_item"), () -> new BlockPlacerItem(Blocks.VOID_AIR, new Properties())
   );
   public static final Supplier<LootPoolEntryType> LAZY_ITEM = RegHelper.registerLootPoolEntry(
      Moonlight.res("optional_item"), () -> OptionalItemPoolEntry.CODEC
   );
   public static final Supplier<LootPoolEntryType> CONFIG_ITEM = RegHelper.registerLootPoolEntry(Moonlight.res("config_item"), () -> ConfigItemPoolEntry.CODEC);
   public static final Supplier<LootItemConditionType> LAZY_PROPERTY = RegHelper.registerLootCondition(
      Moonlight.res("optional_block_state_property"), () -> OptionalPropertyCondition.CODEC
   );
   public static final Supplier<GrindItemTrigger> GRIND_TRIGGER = RegHelper.registerTriggerType(Moonlight.res("grind_item"), GrindItemTrigger::new);
   @Deprecated(
      forRemoval = true
   )
   public static final Supplier<LootItemConditionType> ICONDITION_LOOT_CONDITION = RegHelper.registerLootCondition(
      Moonlight.res("iconditions"), () -> ResourceLootItemCondition.CODEC
   );
   public static final Supplier<LootItemConditionType> RESOURCE_CONDITION_LOOT_ITEM_CONDITION = RegHelper.registerLootCondition(
      Moonlight.res("load_conditions"), () -> ResourceLootItemCondition.CODEC
   );
   public static final Supplier<LootItemConditionType> PATTERN_MATCH_CONDITION = RegHelper.registerLootCondition(
      Moonlight.res("loot_table_id_pattern"), () -> PatternMatchLootItemCondition.CODEC
   );
   public static final Supplier<SpawnBoxBlock> SPAWN_BOX_BLOCK = RegHelper.registerBlock(Moonlight.res("spawn_box"), SpawnBoxBlock::new);
   public static final Supplier<Item> STRUCTURE_BLOCK = RegHelper.registerItem(
      Moonlight.res("spawn_box"), () -> new GameMasterBlockItem(SPAWN_BOX_BLOCK.get(), new Properties().rarity(Rarity.EPIC))
   );
   public static final Supplier<BlockEntityType<SpawnBoxBlockEntity>> SPAWN_BOX_BLOCK_ENTITY = RegHelper.registerBlockEntityType(
      Moonlight.res("spawn_box"), SpawnBoxBlockEntity::new, SPAWN_BOX_BLOCK
   );
   public static final Supplier<StructurePieceType> SPAWN_BOX_PIECE = RegHelper.registerStructurePiece(Moonlight.res("spawn_box"), SpawnBoxStructurePiece::new);
   public static final Supplier<StructurePoolElementType<SpawnBoxPoolElement>> SPAWN_BOX_POOL_ELEMENT = RegHelper.registerStructurePoolElement(
      Moonlight.res("spawn_box"), SpawnBoxPoolElement.CODEC
   );
   public static final Supplier<DataComponentType<PotionBottleType>> BOTTLE_TYPE = RegHelper.registerDataComponent(
      Moonlight.res("bottle_type"), () -> DataComponentType.builder().persistent(PotionBottleType.CODEC).build()
   );
   public static final Supplier<DataComponentType<MLMapDecorationsComponent>> CUSTOM_MAP_DECORATIONS = RegHelper.registerDataComponent(
      Moonlight.res("custom_map_decorations"), () -> DataComponentType.builder().persistent(MLMapDecorationsComponent.CODEC).cacheEncoding().build()
   );
   public static final Supplier<Schedule> CUSTOM_VILLAGER_SCHEDULE = RegHelper.registerSchedule(Moonlight.res("custom_villager_schedule"), Schedule::new);

   @Internal
   public static void init() {
   }
}
