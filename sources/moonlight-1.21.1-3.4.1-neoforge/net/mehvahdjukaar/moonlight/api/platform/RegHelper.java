package net.mehvahdjukaar.moonlight.api.platform;

import com.google.common.collect.ImmutableSet;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.api.MoonlightRegistry;
import net.mehvahdjukaar.moonlight.api.block.ModStairBlock;
import net.mehvahdjukaar.moonlight.api.misc.IAttachmentType;
import net.mehvahdjukaar.moonlight.api.misc.RegSupplier;
import net.mehvahdjukaar.moonlight.api.misc.Registrator;
import net.mehvahdjukaar.moonlight.api.misc.TriFunction;
import net.mehvahdjukaar.moonlight.api.misc.WorldSavedData;
import net.mehvahdjukaar.moonlight.api.misc.WorldSavedDataType;
import net.mehvahdjukaar.moonlight.api.platform.platform.RegHelperImpl;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicResourcesProvider;
import net.mehvahdjukaar.moonlight.api.resources.pack.SimplePackProvider;
import net.mehvahdjukaar.moonlight.api.trades.ItemListingManager;
import net.mehvahdjukaar.moonlight.api.trades.ModItemListing;
import net.mehvahdjukaar.moonlight.api.util.DispenserHelper;
import net.mehvahdjukaar.moonlight.core.MoonlightClient;
import net.mehvahdjukaar.moonlight.core.misc.AttachmentBuilderImpl;
import net.mehvahdjukaar.moonlight.core.pack.DynamicResourcesInternals;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands.CommandSelection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.world.entity.SpawnPlacements.SpawnPredicate;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.MenuType.MenuSupplier;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.component.FireworkExplosion.Shape;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer.Factory;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

public class RegHelper {
   private static final List<ResourceLocation> DEFAULT_AFTER_ENTRIES = List.of(CreativeModeTabs.SPAWN_EGGS.location());

   public static <T extends Block> RegSupplier<T> registerBlock(ResourceLocation name, Supplier<T> block) {
      return register(name, block, Registries.BLOCK);
   }

   public static <T extends Block> RegSupplier<T> registerBlockWithItem(ResourceLocation name, Supplier<T> blockFactory) {
      return registerBlockWithItem(name, blockFactory, new net.minecraft.world.item.Item.Properties());
   }

   public static <T extends Block> RegSupplier<T> registerBlockWithItem(
      ResourceLocation name, Supplier<T> blockFactory, net.minecraft.world.item.Item.Properties properties
   ) {
      RegSupplier<T> block = registerBlock(name, blockFactory);
      registerItem(name, () -> (T)(new BlockItem(block.get(), properties)));
      return block;
   }

   public static <T extends SimpleCriterionTrigger<?>> RegSupplier<T> registerTriggerType(ResourceLocation name, Supplier<T> instance) {
      return register(name, instance, Registries.TRIGGER_TYPE);
   }

   @Deprecated(
      forRemoval = true
   )
   public static <T extends PlacementModifierType<?>> RegSupplier<T> registerPlacementModifier(ResourceLocation name, Supplier<T> instance) {
      return register(name, instance, Registries.PLACEMENT_MODIFIER_TYPE);
   }

   public static <T extends PlacementModifier> RegSupplier<PlacementModifierType<T>> registerPlacementModifier(ResourceLocation name, MapCodec<T> codec) {
      return register(name, () -> () -> codec, Registries.PLACEMENT_MODIFIER_TYPE);
   }

   public static <T extends LootPoolEntryContainer> RegSupplier<LootPoolEntryType> registerLootPoolEntry(ResourceLocation name, Supplier<MapCodec<T>> instance) {
      return register(name, () -> new LootPoolEntryType(instance.get()), Registries.LOOT_POOL_ENTRY_TYPE);
   }

   public static <T extends LootItemCondition> RegSupplier<LootItemConditionType> registerLootCondition(ResourceLocation name, Supplier<MapCodec<T>> instance) {
      return register(name, () -> new LootItemConditionType(instance.get()), Registries.LOOT_CONDITION_TYPE);
   }

   public static <T> Supplier<DataComponentType<T>> registerDataComponent(ResourceLocation name, Supplier<DataComponentType<T>> component) {
      return register(name, component, Registries.DATA_COMPONENT_TYPE);
   }

   public static RegSupplier<VillagerProfession> registerVillagerProfession(
      ResourceLocation name,
      Predicate<Holder<PoiType>> heldJobSite,
      Predicate<Holder<PoiType>> acquirableJobSite,
      ImmutableSet<Item> requestedItems,
      ImmutableSet<Block> secondaryWorkSite,
      @Nullable Supplier<SoundEvent> workSound
   ) {
      Supplier<VillagerProfession> factory = () -> new VillagerProfession(
         name.getPath(), heldJobSite, acquirableJobSite, requestedItems, secondaryWorkSite, workSound == null ? null : workSound.get()
      );
      return register(name, factory, Registries.VILLAGER_PROFESSION);
   }

   public static RegSupplier<VillagerProfession> registerVillagerProfession(
      ResourceLocation name,
      Supplier<PoiType> heldJobSite,
      Supplier<PoiType> acquirableJobSite,
      ImmutableSet<Item> requestedItems,
      ImmutableSet<Block> secondaryPoi,
      @Nullable Supplier<SoundEvent> workSound
   ) {
      return registerVillagerProfession(
         name, holder -> holder.value() == heldJobSite.get(), holder -> holder.value() == acquirableJobSite.get(), requestedItems, secondaryPoi, workSound
      );
   }

   public static <T extends StructurePoolElement> Supplier<StructurePoolElementType<T>> registerStructurePoolElement(ResourceLocation id, MapCodec<T> codec) {
      return register(id, () -> () -> codec, Registries.STRUCTURE_POOL_ELEMENT);
   }

   public static RegSupplier<StructurePieceType> registerStructurePiece(ResourceLocation name, StructurePieceType pieceType) {
      return register(name, () -> pieceType, Registries.STRUCTURE_PIECE);
   }

   public static <T extends StructureProcessor> RegSupplier<StructureProcessorType<T>> registerStructureProcessor(ResourceLocation name, MapCodec<T> codec) {
      return register(name, () -> () -> codec, Registries.STRUCTURE_PROCESSOR);
   }

   @Deprecated(
      forRemoval = true
   )
   public static <T extends StructureProcessor> RegSupplier<StructureProcessorType<T>> registerStructurePiece(ResourceLocation name, MapCodec<T> codec) {
      return register(name, () -> () -> codec, Registries.STRUCTURE_PROCESSOR);
   }

   public static <T extends StructurePlacement> RegSupplier<StructurePlacementType<T>> registerStructurePlacementType(ResourceLocation name, MapCodec<T> codec) {
      return register(name, () -> () -> codec, Registries.STRUCTURE_PLACEMENT);
   }

   @Deprecated(
      forRemoval = true
   )
   public static RegSupplier<StructurePieceType> register(ResourceLocation name, StructurePieceType pieceType) {
      return register(name, () -> pieceType, Registries.STRUCTURE_PIECE);
   }

   public static RegSupplier<PoiType> registerPOI(ResourceLocation name, Supplier<PoiType> poi) {
      return register(name, poi, Registries.POINT_OF_INTEREST_TYPE);
   }

   public static RegSupplier<PoiType> registerPOI(ResourceLocation name, int searchDistance, int maxTickets, Block... blocks) {
      return registerPOI(name, () -> {
         com.google.common.collect.ImmutableSet.Builder<BlockState> builder = ImmutableSet.builder();

         for (Block block : blocks) {
            builder.addAll(block.getStateDefinition().getPossibleStates());
         }

         return new PoiType(builder.build(), searchDistance, maxTickets);
      });
   }

   @SafeVarargs
   public static RegSupplier<PoiType> registerPOI(ResourceLocation name, int searchDistance, int maxTickets, Supplier<Block>... blocks) {
      return registerPOI(name, () -> {
         com.google.common.collect.ImmutableSet.Builder<BlockState> builder = ImmutableSet.builder();

         for (Supplier<Block> block : blocks) {
            builder.addAll(block.get().getStateDefinition().getPossibleStates());
         }

         return new PoiType(builder.build(), searchDistance, maxTickets);
      });
   }

   public static <T extends Item> RegSupplier<T> registerItem(ResourceLocation name, Supplier<T> item) {
      return register(name, item, Registries.ITEM);
   }

   public static <T extends Feature<?>> RegSupplier<T> registerFeature(ResourceLocation name, Supplier<T> feature) {
      return register(name, feature, Registries.FEATURE);
   }

   public static <T extends StructureType<?>> RegSupplier<T> registerStructure(ResourceLocation name, Supplier<T> feature) {
      return registerAsync(name, feature, Registries.STRUCTURE_TYPE);
   }

   public static <T extends SoundEvent> RegSupplier<T> registerSound(ResourceLocation name, Supplier<T> sound) {
      return register(name, sound, Registries.SOUND_EVENT);
   }

   public static RegSupplier<SoundEvent> registerSound(ResourceLocation name) {
      return registerSound(name, () -> SoundEvent.createVariableRangeEvent(name));
   }

   public static RegSupplier<SoundEvent> registerSound(ResourceLocation name, float fixedRange) {
      return registerSound(name, () -> SoundEvent.createFixedRangeEvent(name, fixedRange));
   }

   public static <T extends MobEffect> RegSupplier<T> registerEffect(ResourceLocation name, Supplier<T> effect) {
      return register(name, effect, Registries.MOB_EFFECT);
   }

   public static <T extends Enchantment> RegSupplier<T> registerEnchantment(ResourceLocation name, Supplier<T> enchantment) {
      return register(name, enchantment, Registries.ENCHANTMENT);
   }

   public static <T extends SensorType<? extends Sensor<?>>> RegSupplier<T> registerSensor(ResourceLocation name, Supplier<T> sensorType) {
      return register(name, sensorType, Registries.SENSOR_TYPE);
   }

   public static <T extends Sensor<?>> RegSupplier<SensorType<T>> registerSensorI(ResourceLocation name, Supplier<T> sensor) {
      return register(name, () -> new SensorType(sensor), Registries.SENSOR_TYPE);
   }

   public static <T extends Activity> RegSupplier<T> registerActivity(ResourceLocation name, Supplier<T> activity) {
      return register(name, activity, Registries.ACTIVITY);
   }

   public static RegSupplier<Activity> registerActivity(ResourceLocation name) {
      return registerActivity(name, () -> new Activity(name.getPath()));
   }

   public static <T extends Schedule> RegSupplier<T> registerSchedule(ResourceLocation name, Supplier<T> schedule) {
      return register(name, schedule, Registries.SCHEDULE);
   }

   public static <T extends MemoryModuleType<?>> RegSupplier<T> registerMemoryModule(ResourceLocation name, Supplier<T> memory) {
      return register(name, memory, Registries.MEMORY_MODULE_TYPE);
   }

   public static <U> RegSupplier<MemoryModuleType<U>> registerMemoryModule(ResourceLocation name, @Nullable Codec<U> codec) {
      return register(name, () -> new MemoryModuleType(Optional.ofNullable(codec)), Registries.MEMORY_MODULE_TYPE);
   }

   public static <T extends RecipeSerializer<?>> RegSupplier<T> registerRecipeSerializer(ResourceLocation name, Supplier<T> recipe) {
      return register(name, recipe, Registries.RECIPE_SERIALIZER);
   }

   public static <T extends Recipe<?>> Supplier<RecipeType<T>> registerRecipeType(ResourceLocation name) {
      return register(name, () -> {
         final String id = name.toString();
         return new RecipeType<T>() {
            @Override
            public String toString() {
               return id;
            }
         };
      }, Registries.RECIPE_TYPE);
   }

   public static <T extends BlockEntityType<E>, E extends BlockEntity> RegSupplier<T> registerBlockEntityType(ResourceLocation name, Supplier<T> blockEntity) {
      return register(name, blockEntity, Registries.BLOCK_ENTITY_TYPE);
   }

   public static <E extends BlockEntity> RegSupplier<BlockEntityType<E>> registerBlockEntityType(
      ResourceLocation name, BiFunction<BlockPos, BlockState, E> blockEntitySupplier, Block... blocks
   ) {
      return registerBlockEntityType(name, () -> PlatHelper.newBlockEntityType(blockEntitySupplier::apply, blocks));
   }

   @SafeVarargs
   public static <E extends BlockEntity> RegSupplier<BlockEntityType<E>> registerBlockEntityType(
      ResourceLocation name, BiFunction<BlockPos, BlockState, E> blockEntitySupplier, Supplier<? extends Block>... blocks
   ) {
      return registerBlockEntityType(
         name, () -> PlatHelper.newBlockEntityType(blockEntitySupplier::apply, Arrays.stream(blocks).map(Supplier::get).toArray(Block[]::new))
      );
   }

   public static <A> Registry<A> registerRegistry(ResourceLocation key, boolean synced) {
      return registerRegistry(ResourceKey.createRegistryKey(key), synced);
   }

   @Deprecated(
      forRemoval = true
   )
   public static <A extends WorldSavedData> WorldSavedDataType<A> registerWorldSavedData(
      ResourceLocation key, Function<ServerLevel, A> constructor, Codec<A> codec, @Nullable StreamCodec<? super RegistryFriendlyByteBuf, A> networkCodec
   ) {
      return registerWorldSavedData(key, constructor, codec, networkCodec, false);
   }

   @Deprecated(
      forRemoval = true
   )
   public static <A extends WorldSavedData> WorldSavedDataType<A> registerWorldSavedData(
      ResourceLocation key,
      Function<ServerLevel, A> constructor,
      Codec<A> codec,
      @Nullable StreamCodec<? super RegistryFriendlyByteBuf, A> networkCodec,
      boolean perLevel
   ) {
      return registerWorldSavedData(key, constructor, () -> codec, networkCodec == null ? null : () -> networkCodec, perLevel);
   }

   public static <A extends WorldSavedData> WorldSavedDataType<A> registerWorldSavedData(
      ResourceLocation key,
      Function<ServerLevel, A> constructor,
      Supplier<Codec<A>> codec,
      @Nullable Supplier<StreamCodec<? super RegistryFriendlyByteBuf, A>> networkCodec
   ) {
      return registerWorldSavedData(key, constructor, codec, networkCodec, false);
   }

   public static <A extends WorldSavedData> WorldSavedDataType<A> registerWorldSavedData(
      ResourceLocation key,
      Function<ServerLevel, A> constructor,
      Supplier<Codec<A>> codec,
      @Nullable Supplier<StreamCodec<? super RegistryFriendlyByteBuf, A>> networkCodec,
      boolean perLevel
   ) {
      WorldSavedDataType<A> instance = new WorldSavedDataType<>(
         key, constructor, codec, networkCodec, perLevel ? WorldSavedDataType.Scope.PER_LEVEL : WorldSavedDataType.Scope.SINGLE_OVERWORLD
      );
      register(key, () -> instance, MoonlightRegistry.WORLD_SAVED_DATA_TYPE_REGISTRY.key());
      return instance;
   }

   public static RegSupplier<SimpleParticleType> registerParticle(ResourceLocation name) {
      return register(name, PlatHelper::newSimpleParticle, Registries.PARTICLE_TYPE);
   }

   public static <T extends ParticleOptions> RegSupplier<ParticleType<T>> registerParticle(
      ResourceLocation name, MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec
   ) {
      return register(name, () -> PlatHelper.newParticle(codec, streamCodec, false), Registries.PARTICLE_TYPE);
   }

   public static <T extends ParticleOptions> RegSupplier<ParticleType<T>> registerParticle(
      ResourceLocation name,
      boolean overrideLimiter,
      Function<ParticleType<T>, MapCodec<T>> codecGetter,
      Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodecGetter
   ) {
      return register(name, () -> PlatHelper.newParticle(codecGetter, streamCodecGetter, overrideLimiter), Registries.PARTICLE_TYPE);
   }

   public static <T extends LootItemFunction> RegSupplier<LootItemFunctionType<T>> registerLootFunction(ResourceLocation name, MapCodec<T> codec) {
      return register(name, () -> new LootItemFunctionType(codec), Registries.LOOT_FUNCTION_TYPE);
   }

   public static <T extends Entity> RegSupplier<EntityType<T>> registerEntityType(ResourceLocation name, Builder<T> builder) {
      return register(name, () -> builder.build(name.getPath()), Registries.ENTITY_TYPE);
   }

   @Deprecated(
      forRemoval = true
   )
   public static <T extends Entity> RegSupplier<EntityType<T>> registerEntityType(ResourceLocation name, Supplier<EntityType<T>> type) {
      return register(name, type, Registries.ENTITY_TYPE);
   }

   @Deprecated(
      forRemoval = true
   )
   public static <T extends Entity> RegSupplier<EntityType<T>> registerEntityType(
      ResourceLocation name, EntityFactory<T> factory, MobCategory category, float width, float height
   ) {
      return registerEntityType(name, factory, category, width, height, 5);
   }

   @Deprecated(
      forRemoval = true
   )
   public static <T extends Entity> RegSupplier<EntityType<T>> registerEntityType(
      ResourceLocation name, EntityFactory<T> factory, MobCategory category, float width, float height, int clientTrackingRange
   ) {
      return registerEntityType(name, factory, category, width, height, clientTrackingRange, 3);
   }

   public static RegSupplier<ArmorMaterial> registerArmorMaterial(
      ResourceLocation name,
      int totalDefense,
      Supplier<Ingredient> ingredient,
      int enchValue,
      Supplier<Holder<SoundEvent>> sound,
      float toughness,
      float knockbackResistance
   ) {
      return register(
         name,
         () -> new ArmorMaterial(
            calculateStandardDefence(totalDefense), enchValue, sound.get(), ingredient, List.of(new Layer(name)), toughness, knockbackResistance
         ),
         Registries.ARMOR_MATERIAL
      );
   }

   private static EnumMap<Type, Integer> calculateStandardDefence(int totalDefense) {
      EnumMap<Type, Integer> defenseMap = new EnumMap<>(Type.class);
      Map<Type, Double> proportions = new LinkedHashMap<>();
      proportions.put(Type.CHESTPLATE, 0.41);
      proportions.put(Type.LEGGINGS, 0.32);
      proportions.put(Type.HELMET, 0.14);
      proportions.put(Type.BOOTS, 0.13);

      for (Entry<Type, Double> entry : proportions.entrySet()) {
         Type type = entry.getKey();
         int defenseValue = (int)(entry.getValue() * totalDefense);
         defenseMap.put(type, defenseValue);
      }

      int remainder = totalDefense - defenseMap.values().stream().mapToInt(Integer::intValue).sum();

      while (remainder > 0) {
         for (Entry<Type, Double> entry : proportions.entrySet()) {
            Type type = entry.getKey();
            int maxDefense = (int)Math.ceil(entry.getValue() * totalDefense);
            int currentDefense = defenseMap.get(type);
            if (currentDefense < maxDefense) {
               defenseMap.put(type, currentDefense + 1);
               if (--remainder <= 0) {
                  break;
               }
            }
         }
      }

      defenseMap.put(Type.BODY, defenseMap.get(Type.CHESTPLATE) + defenseMap.get(Type.BOOTS));
      return defenseMap;
   }

   @Deprecated(
      forRemoval = true
   )
   public static void registerCompostable(ItemLike itemLike, float chance) {
      ComposterBlock.COMPOSTABLES.put(itemLike.asItem(), chance);
   }

   public static <T> ResourceKey<Registry<T>> registerDataPackRegistry(ResourceLocation id, Codec<T> codec, @Nullable Codec<T> networkCodec) {
      ResourceKey<Registry<T>> key = ResourceKey.createRegistryKey(id);
      registerDataPackRegistry(key, codec, networkCodec);
      return key;
   }

   public static RegSupplier<CreativeModeTab> registerCreativeModeTab(
      ResourceLocation name, Consumer<net.minecraft.world.item.CreativeModeTab.Builder> configurator
   ) {
      return registerCreativeModeTab(name, false, configurator);
   }

   public static RegSupplier<CreativeModeTab> registerCreativeModeTab(
      ResourceLocation name, boolean searchBar, Consumer<net.minecraft.world.item.CreativeModeTab.Builder> configurator
   ) {
      return registerCreativeModeTab(name, searchBar, DEFAULT_AFTER_ENTRIES, List.of(), configurator);
   }

   public static EnumMap<RegHelper.VariantType, Supplier<Block>> registerBaseBlockSet(ResourceLocation baseName, Block parentBlock) {
      return registerBaseBlockSet(baseName, Properties.ofFullCopy(parentBlock));
   }

   public static EnumMap<RegHelper.VariantType, Supplier<Block>> registerBaseBlockSet(ResourceLocation baseName, Properties properties) {
      return registerBlockSet(new RegHelper.VariantType[]{RegHelper.VariantType.BLOCK, RegHelper.VariantType.SLAB}, baseName, properties);
   }

   public static EnumMap<RegHelper.VariantType, Supplier<Block>> registerReducedBlockSet(ResourceLocation baseName, Block parentBlock) {
      return registerReducedBlockSet(baseName, Properties.ofFullCopy(parentBlock));
   }

   public static EnumMap<RegHelper.VariantType, Supplier<Block>> registerReducedBlockSet(ResourceLocation baseName, Properties properties) {
      return registerBlockSet(
         new RegHelper.VariantType[]{RegHelper.VariantType.BLOCK, RegHelper.VariantType.STAIRS, RegHelper.VariantType.SLAB}, baseName, properties
      );
   }

   public static EnumMap<RegHelper.VariantType, Supplier<Block>> registerFullBlockSet(ResourceLocation baseName, Block parentBlock) {
      return registerFullBlockSet(baseName, Properties.ofFullCopy(parentBlock));
   }

   public static EnumMap<RegHelper.VariantType, Supplier<Block>> registerFullBlockSet(ResourceLocation baseName, Properties properties) {
      return registerBlockSet(RegHelper.VariantType.values(), baseName, properties);
   }

   public static EnumMap<RegHelper.VariantType, Supplier<Block>> registerBlockSet(
      RegHelper.VariantType[] types, ResourceLocation baseName, Properties properties
   ) {
      if (!new ArrayList<>(List.of(types)).contains(RegHelper.VariantType.BLOCK)) {
         throw new IllegalStateException("Must contain base variant type");
      } else {
         RegSupplier<Block> block = registerBlock(baseName, () -> RegHelper.VariantType.BLOCK.create(properties, null));
         registerItem(baseName, () -> new BlockItem(block.get(), new net.minecraft.world.item.Item.Properties()));
         EnumMap<RegHelper.VariantType, Supplier<Block>> m = registerBlockSet(types, block, baseName.getNamespace());
         m.put(RegHelper.VariantType.BLOCK, block);
         return m;
      }
   }

   public static EnumMap<RegHelper.VariantType, Supplier<Block>> registerBlockSet(
      RegHelper.VariantType[] types, RegSupplier<? extends Block> baseBlock, String modId
   ) {
      ResourceLocation baseName = baseBlock.getId();
      EnumMap<RegHelper.VariantType, Supplier<Block>> map = new EnumMap<>(RegHelper.VariantType.class);

      for (RegHelper.VariantType type : types) {
         if (!type.equals(RegHelper.VariantType.BLOCK)) {
            String name = baseName.getPath();
            name = name + "_" + type.name().toLowerCase(Locale.ROOT);
            ResourceLocation blockId = ResourceLocation.fromNamespaceAndPath(modId, name);
            RegSupplier<Block> block = registerBlock(blockId, () -> type.create(Properties.ofFullCopy((BlockBehaviour)baseBlock.get()), baseBlock::get));
            registerItem(blockId, () -> new BlockItem(block.get(), new net.minecraft.world.item.Item.Properties()));
            map.put(type, block);
         }
      }

      return map;
   }

   public static void addDynamicDispenserBehaviorRegistration(Consumer<DispenserHelper.Event> eventListener) {
      DispenserHelper.addListener(eventListener, DispenserHelper.Priority.NORMAL);
   }

   public static void addDynamicDispenserBehaviorRegistration(Consumer<DispenserHelper.Event> eventListener, DispenserHelper.Priority priority) {
      DispenserHelper.addListener(eventListener, priority);
   }

   public static void registerDynamicItemListingSerializer(ResourceLocation id, MapCodec<? extends ModItemListing> trade) {
      ItemListingManager.registerSerializer(id, trade);
   }

   public static void registerDynamicItemListingSerializer(ResourceLocation id, ItemListing instance, int level) {
      ItemListingManager.registerSimple(id, instance, level);
   }

   public static void registerResourcePack(PackType packType, Supplier<Pack> packSupplier) {
      if (packSupplier != null) {
         registerResourcePackSource(packType, loader -> {
            Pack t = packSupplier.get();
            if (t != null) {
               loader.accept(t);
            }
         });
      }
   }

   public static void registerDynamicResourceProvider(DynamicResourcesProvider provider) {
      DynamicResourcesInternals.registerProvider(provider);
      SimplePackProvider packSupplier = provider;
      PackType packType = provider.getPackType();
      if (packType == PackType.CLIENT_RESOURCES) {
         SimplePackProvider maybeMerged = MoonlightClient.mergePackSupplier(provider);
         if (maybeMerged == null) {
            return;
         }

         packSupplier = maybeMerged;
      }

      registerResourcePack(packType, packSupplier::createPack);
   }

   @Deprecated(
      forRemoval = true
   )
   public static <A> IAttachmentType<A, Object> regDataAttachment(ResourceLocation id, Supplier<RegHelper.AttachmentBuilder<A>> config) {
      return registerDataAttachment(id, config, Object.class);
   }

   public static <T, E extends T> RegSupplier<E> register(ResourceLocation var0, Supplier<E> var1, ResourceKey<? extends Registry<T>> var2) {
      return RegHelperImpl.register(var0, var1, var2);
   }

   public static <T> void registerInBatch(Registry<T> var0, Consumer<Registrator<T>> var1) {
      RegHelperImpl.registerInBatch(var0, var1);
   }

   public static <T, E extends T> RegSupplier<E> registerAsync(ResourceLocation var0, Supplier<E> var1, ResourceKey<? extends Registry<T>> var2) {
      return RegHelperImpl.registerAsync(var0, var1, var2);
   }

   public static <T> Supplier<EntityDataSerializer<T>> registerEntityDataSerializer(ResourceLocation var0, Supplier<EntityDataSerializer<T>> var1) {
      return RegHelperImpl.registerEntityDataSerializer(var0, var1);
   }

   /** @deprecated */
   public static void addBlocksToPOI(ResourceKey<PoiType> var0, Iterable<? extends Block> var1) {
      RegHelperImpl.addBlocksToPOI(var0, var1);
   }

   public static void addExtraPOIStatesRegistration(Consumer<RegHelper.ExtraPOIStatesEvent> var0) {
      RegHelperImpl.addExtraPOIStatesRegistration(var0);
   }

   public static void addExtraBEBlockStatesRegistration(Consumer<RegHelper.ExtraBEStatesEvent> var0) {
      RegHelperImpl.addExtraBEBlockStatesRegistration(var0);
   }

   public static <T extends Fluid> RegSupplier<T> registerFluid(ResourceLocation var0, Supplier<T> var1) {
      return RegHelperImpl.registerFluid(var0, var1);
   }

   public static <C extends AbstractContainerMenu> RegSupplier<MenuType<C>> registerMenuType(
      ResourceLocation var0, TriFunction<Integer, Inventory, FriendlyByteBuf, C> var1
   ) {
      return RegHelperImpl.registerMenuType(var0, var1);
   }

   public static <C extends AbstractContainerMenu> RegSupplier<MenuType<C>> registerSimpleMenuType(ResourceLocation var0, MenuSupplier<C> var1) {
      return RegHelperImpl.registerSimpleMenuType(var0, var1);
   }

   public static <T extends CraftingRecipe> RegSupplier<RecipeSerializer<T>> registerSpecialRecipe(ResourceLocation var0, Factory<T> var1) {
      return RegHelperImpl.registerSpecialRecipe(var0, var1);
   }

   public static <A> Registry<A> registerRegistry(ResourceKey<Registry<A>> var0, boolean var1) {
      return RegHelperImpl.registerRegistry(var0, var1);
   }

   /** @deprecated */
   public static <T extends Entity> RegSupplier<EntityType<T>> registerEntityType(
      ResourceLocation var0, EntityFactory<T> var1, MobCategory var2, float var3, float var4, int var5, int var6
   ) {
      return RegHelperImpl.registerEntityType(var0, var1, var2, var3, var4, var5, var6);
   }

   /** @deprecated */
   public static void registerItemBurnTime(Item var0, int var1) {
      RegHelperImpl.registerItemBurnTime(var0, var1);
   }

   public static void registerBlockFlammability(Block var0, int var1, int var2) {
      RegHelperImpl.registerBlockFlammability(var0, var1, var2);
   }

   public static void registerSimpleRecipeCondition(ResourceLocation var0, Predicate<String> var1) {
      RegHelperImpl.registerSimpleRecipeCondition(var0, var1);
   }

   public static <T> void registerDataPackRegistry(ResourceKey<Registry<T>> var0, Codec<T> var1, Codec<T> var2) {
      RegHelperImpl.registerDataPackRegistry(var0, var1, var2);
   }

   public static RegSupplier<CreativeModeTab> registerCreativeModeTab(
      ResourceLocation var0,
      boolean var1,
      List<ResourceLocation> var2,
      List<ResourceLocation> var3,
      Consumer<net.minecraft.world.item.CreativeModeTab.Builder> var4
   ) {
      return RegHelperImpl.registerCreativeModeTab(var0, var1, var2, var3, var4);
   }

   public static void addItemsToTabsRegistration(Consumer<RegHelper.ItemToTabEvent> var0) {
      RegHelperImpl.addItemsToTabsRegistration(var0);
   }

   public static void addAttributeRegistration(Consumer<RegHelper.AttributeEvent> var0) {
      RegHelperImpl.addAttributeRegistration(var0);
   }

   public static void addSpawnPlacementsRegistration(Consumer<RegHelper.SpawnPlacementEvent> var0) {
      RegHelperImpl.addSpawnPlacementsRegistration(var0);
   }

   public static void addCommandRegistration(RegHelper.CommandRegistration var0) {
      RegHelperImpl.addCommandRegistration(var0);
   }

   public static void addLootTableInjects(Consumer<RegHelper.LootInjectEvent> var0) {
      RegHelperImpl.addLootTableInjects(var0);
   }

   public static void registerFireworkRecipe(Shape var0, Item var1) {
      RegHelperImpl.registerFireworkRecipe(var0, var1);
   }

   /** @deprecated */
   public static void startRegisteringFor(Object var0) {
      RegHelperImpl.startRegisteringFor(var0);
   }

   public static void registerResourcePackSource(PackType var0, RepositorySource var1) {
      RegHelperImpl.registerResourcePackSource(var0, var1);
   }

   public static <A, T> IAttachmentType<A, T> registerDataAttachment(ResourceLocation var0, Supplier<RegHelper.AttachmentBuilder<A>> var1, Class<T> var2) {
      return RegHelperImpl.registerDataAttachment(var0, var1, var2);
   }

   @NonExtendable
   public interface AttachmentBuilder<A> {
      static <A> RegHelper.AttachmentBuilder<A> create(Supplier<A> initializer) {
         return new AttachmentBuilderImpl<>(initializer);
      }

      RegHelper.AttachmentBuilder<A> persistent(Codec<A> var1);

      RegHelper.AttachmentBuilder<A> copyOnDeath();

      RegHelper.AttachmentBuilder<A> syncWith(StreamCodec<? super RegistryFriendlyByteBuf, A> var1, BiPredicate<Object, ServerPlayer> var2);

      default RegHelper.AttachmentBuilder<A> syncWith(StreamCodec<? super RegistryFriendlyByteBuf, A> packetCodec) {
         return this.syncWith(packetCodec, (provider, player) -> true);
      }
   }

   @FunctionalInterface
   public interface AttributeEvent {
      void register(EntityType<? extends LivingEntity> var1, net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder var2);
   }

   @FunctionalInterface
   public interface CommandRegistration {
      void accept(CommandDispatcher<CommandSourceStack> var1, CommandBuildContext var2, CommandSelection var3);
   }

   public interface ExtraBEStatesEvent {
      void addBlocks(BlockEntityType<?> var1, Block... var2);
   }

   public interface ExtraPOIStatesEvent {
      @Deprecated(
         forRemoval = true
      )
      default void addStatesToPoi(ResourceKey<PoiType> typeKey, Set<BlockState> states) {
         this.addStates(typeKey, states);
      }

      @Deprecated(
         forRemoval = true
      )
      default void addBlockToPoi(ResourceKey<PoiType> typeKey, Block block) {
         this.addBlock(typeKey, block);
      }

      void addBlock(ResourceKey<PoiType> var1, Block var2);

      void addStates(ResourceKey<PoiType> var1, Set<BlockState> var2);

      default void addBlocks(ResourceKey<PoiType> typeKey, Collection<Block> blocks) {
         for (Block b : blocks) {
            this.addBlock(typeKey, b);
         }
      }
   }

   public interface ItemToTabEvent {
      ItemDisplayParameters getParameters();

      CreativeModeTab getTab();

      void addItems(ResourceKey<CreativeModeTab> var1, @Nullable Predicate<ItemStack> var2, boolean var3, List<ItemStack> var4);

      default void add(ResourceKey<CreativeModeTab> tab, ItemLike... items) {
         this.addAfter(tab, null, items);
      }

      default void add(ResourceKey<CreativeModeTab> tab, ItemStack... items) {
         this.addAfter(tab, null, items);
      }

      default void addAfter(ResourceKey<CreativeModeTab> tab, Predicate<ItemStack> target, ItemLike... items) {
         List<ItemStack> stacks = new ArrayList<>();

         for (ItemLike i : items) {
            if (i.asItem().getDefaultInstance().isEmpty()) {
               throw new IllegalStateException(
                  "Attempted to add empty item " + i + " to item tabs. It's likely that some mod tried to call asItem before items were registered\");"
               );
            }

            stacks.add(i.asItem().getDefaultInstance());
         }

         this.addItems(tab, target, true, stacks);
      }

      default void addAfter(ResourceKey<CreativeModeTab> tab, Predicate<ItemStack> target, ItemStack... items) {
         this.addItems(tab, target, true, List.of(items));
      }

      default void addBefore(ResourceKey<CreativeModeTab> tab, Predicate<ItemStack> target, ItemLike... items) {
         List<ItemStack> stacks = new ArrayList<>();

         for (ItemLike i : items) {
            if (i.asItem().getDefaultInstance().isEmpty()) {
               throw new IllegalStateException(
                  "Attempted to add empty item " + i + " to item tabs. It's likely that some mod tried to call asItem before items were registered"
               );
            }

            stacks.add(i.asItem().getDefaultInstance());
         }

         this.addItems(tab, target, false, stacks);
      }

      default void addBefore(ResourceKey<CreativeModeTab> tab, Predicate<ItemStack> target, ItemStack... items) {
         this.addItems(tab, target, false, List.of(items));
      }

      default void remove(ResourceKey<CreativeModeTab> tab, ItemLike... items) {
         this.remove(tab, stack -> {
            for (ItemLike i : items) {
               if (stack.is(i.asItem())) {
                  return true;
               }
            }

            return false;
         });
      }

      default void remove(ResourceKey<CreativeModeTab> tab, ItemStack... items) {
         this.remove(tab, stack -> {
            for (ItemStack i : items) {
               if (ItemStack.isSameItemSameComponents(stack, i)) {
                  return true;
               }
            }

            return false;
         });
      }

      void remove(ResourceKey<CreativeModeTab> var1, Predicate<ItemStack> var2);
   }

   public interface LootInjectEvent {
      ResourceLocation getTable();

      void addTableReference(ResourceLocation var1);
   }

   @FunctionalInterface
   public interface SpawnPlacementEvent {
      <T extends Mob> void register(EntityType<T> var1, SpawnPlacementType var2, Types var3, SpawnPredicate<T> var4);
   }

   public static enum VariantType {
      BLOCK(Block::new),
      STAIRS(ModStairBlock::new),
      SLAB(SlabBlock::new),
      WALL(WallBlock::new);

      private final BiFunction<Supplier<Block>, Properties, Block> constructor;

      private VariantType(BiFunction<Supplier<Block>, Properties, Block> constructor) {
         this.constructor = constructor;
      }

      private VariantType(Function<Properties, Block> constructor) {
         this.constructor = (b, p) -> constructor.apply(p);
      }

      public Block create(Properties properties, @Nullable Supplier<Block> parent) {
         return this.constructor.apply(parent, properties);
      }

      public static void addToTab(RegHelper.ItemToTabEvent event, Map<RegHelper.VariantType, Supplier<Block>> blocks) {
         Map<RegHelper.VariantType, Supplier<Block>> m = new EnumMap<>(blocks);
         event.add(CreativeModeTabs.BUILDING_BLOCKS, (ItemLike[])m.values().stream().map(Supplier::get).toArray(Block[]::new));
      }
   }
}
