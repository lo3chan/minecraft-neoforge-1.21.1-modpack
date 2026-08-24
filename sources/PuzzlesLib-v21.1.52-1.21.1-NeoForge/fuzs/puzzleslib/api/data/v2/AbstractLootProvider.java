package fuzs.puzzleslib.api.data.v2;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Lifecycle;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.init.v3.family.BlockSetFamily;
import fuzs.puzzleslib.api.init.v3.family.BlockSetVariant;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.RegistryAccess.ImmutableRegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.PathProvider;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ProblemReporter.Collector;
import net.minecraft.world.RandomSequence;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.RandomSupport.Seed128bit;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction.Source;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.apache.commons.lang3.StringUtils;

public final class AbstractLootProvider {
   private AbstractLootProvider() {
   }

   public abstract static class Blocks extends BlockLootSubProvider implements AbstractLootProvider.LootTableDataProvider {
      public static final Map<BlockSetVariant, BiConsumer<AbstractLootProvider.Blocks, Block>> VARIANT_PROVIDERS = ImmutableMap.builder()
         .put(BlockSetVariant.CHISELED, BlockLootSubProvider::dropSelf)
         .put(BlockSetVariant.CRACKED, BlockLootSubProvider::dropSelf)
         .put(BlockSetVariant.POLISHED, BlockLootSubProvider::dropSelf)
         .put(BlockSetVariant.CUT, BlockLootSubProvider::dropSelf)
         .put(BlockSetVariant.MOSAIC, BlockLootSubProvider::dropSelf)
         .put(BlockSetVariant.STAIRS, BlockLootSubProvider::dropSelf)
         .put(BlockSetVariant.SLAB, (BiConsumer<AbstractLootProvider.Blocks, Block>)(provider, block) -> provider.add(block, provider::createSlabItemTable))
         .put(BlockSetVariant.WALL, BlockLootSubProvider::dropSelf)
         .put(BlockSetVariant.FENCE, BlockLootSubProvider::dropSelf)
         .put(BlockSetVariant.FENCE_GATE, BlockLootSubProvider::dropSelf)
         .put(BlockSetVariant.DOOR, (BiConsumer<AbstractLootProvider.Blocks, Block>)(provider, block) -> provider.add(block, provider::createDoorTable))
         .put(BlockSetVariant.TRAPDOOR, BlockLootSubProvider::dropSelf)
         .put(BlockSetVariant.BUTTON, BlockLootSubProvider::dropSelf)
         .put(BlockSetVariant.PRESSURE_PLATE, BlockLootSubProvider::dropSelf)
         .put(BlockSetVariant.SIGN, BlockLootSubProvider::dropSelf)
         .put(BlockSetVariant.HANGING_SIGN, BlockLootSubProvider::dropSelf)
         .build();
      private final Set<ResourceKey<LootTable>> skipValidation = new HashSet<>();
      private final PathProvider pathProvider;
      private final CompletableFuture<Provider> registries;
      private final String modId;

      public Blocks(DataProviderContext context) {
         this(context.getModId(), context.getPackOutput(), context.getRegistries());
      }

      public Blocks(String modId, PackOutput packOutput, CompletableFuture<Provider> registries) {
         super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags(), RegistryAccess.EMPTY);
         this.pathProvider = packOutput.createRegistryElementsPathProvider(Registries.LOOT_TABLE);
         this.registries = registries;
         this.modId = modId;
      }

      public CompletableFuture<?> run(CachedOutput output) {
         return this.registries
            .<Provider>thenApply(registries -> super.registries = registries)
            .thenCompose(registries -> this.run(output, registries))
            .thenRun(() -> super.registries = RegistryAccess.EMPTY);
      }

      public String getName() {
         return "Block Loot Tables";
      }

      public final void generate() {
         this.addLootTables();
      }

      public abstract void addLootTables();

      public void generate(BiConsumer<ResourceKey<LootTable>, Builder> consumer) {
         this.generate();
         Set<ResourceKey<LootTable>> lootTables = new HashSet<>();
         this.getRegistryEntries().forEach(holder -> {
            ResourceKey<LootTable> resourceKey = ((Block)holder.value()).getLootTable();
            if (resourceKey != BuiltInLootTables.EMPTY && lootTables.add(resourceKey)) {
               Builder builder = (Builder)this.map.remove(resourceKey);
               if (builder != null) {
                  consumer.accept(resourceKey, builder);
               } else if (!this.skipValidationFor(resourceKey)) {
                  throw new IllegalStateException("Missing loot table '%s' for '%s'".formatted(resourceKey, holder.key().location()));
               }
            }
         });
         if (!this.map.isEmpty()) {
            throw new IllegalStateException("Created block loot tables for non-blocks: " + this.map.keySet());
         }
      }

      @Override
      public Provider registries() {
         Preconditions.checkState(super.registries != RegistryAccess.EMPTY, "registry access is empty");
         return super.registries;
      }

      @Override
      public PathProvider pathProvider() {
         return this.pathProvider;
      }

      @Override
      public LootContextParamSet paramSet() {
         return LootContextParamSets.BLOCK;
      }

      @Override
      public boolean skipValidationFor(ResourceKey<LootTable> resourceKey) {
         return this.skipValidation.contains(resourceKey);
      }

      public void skipValidation(ResourceLocation resourceLocation) {
         this.skipValidation(ResourceKey.create(Registries.LOOT_TABLE, resourceLocation));
      }

      public void skipValidation(ResourceKey<LootTable> resourceKey) {
         this.skipValidation.add(resourceKey);
      }

      public void skipValidation(Block block) {
         this.skipValidation(block.getLootTable());
      }

      public void dropNothing(Block block) {
         this.add(block, noDrop());
      }

      public void dropNameable(Block block) {
         this.add(block, this::createNameableBlockEntityTable);
      }

      public Builder createHeadDrop(Block block) {
         return LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(block)
                        .apply(
                           CopyComponentsFunction.copyComponents(Source.BLOCK_ENTITY)
                              .include(DataComponents.NOTE_BLOCK_SOUND)
                              .include(DataComponents.CUSTOM_NAME)
                        )
                  )
                  .unwrap()
            );
      }

      public final void generateFor(BlockSetFamily blockSetFamily, Map<BlockSetVariant, BiConsumer<AbstractLootProvider.Blocks, Block>> variants) {
         blockSetFamily.getBlockVariants().forEach((variant, block) -> {
            BiConsumer<AbstractLootProvider.Blocks, Block> provider = variants.get(variant);
            if (provider != null) {
               provider.accept(this, (Block)block.value());
            }
         });
      }

      protected Stream<Reference<Block>> getRegistryEntries() {
         return BuiltInRegistries.BLOCK.holders().filter(holder -> holder.key().location().getNamespace().equals(this.modId));
      }
   }

   public abstract static class EntityTypes extends EntityLootSubProvider implements AbstractLootProvider.LootTableDataProvider {
      private final Set<ResourceKey<LootTable>> skipValidation = new HashSet<>();
      private final PathProvider pathProvider;
      private final CompletableFuture<Provider> registries;
      private final String modId;

      public EntityTypes(DataProviderContext context) {
         this(context.getModId(), context.getPackOutput(), context.getRegistries());
      }

      public EntityTypes(String modId, PackOutput packOutput, CompletableFuture<Provider> registries) {
         super(FeatureFlags.REGISTRY.allFlags(), RegistryAccess.EMPTY);
         this.pathProvider = packOutput.createRegistryElementsPathProvider(Registries.LOOT_TABLE);
         this.registries = registries;
         this.modId = modId;
      }

      public CompletableFuture<?> run(CachedOutput output) {
         return this.registries.thenCompose(registries -> {
            super.registries = registries;
            return this.run(output, registries).thenRun(() -> super.registries = RegistryAccess.EMPTY);
         });
      }

      public String getName() {
         return "Entity Type Loot Tables";
      }

      public final void generate() {
         this.addLootTables();
      }

      public abstract void addLootTables();

      public void generate(BiConsumer<ResourceKey<LootTable>, Builder> consumer) {
         this.generate();
         Set<ResourceKey<LootTable>> lootTables = new HashSet<>();
         this.getRegistryEntries()
            .forEach(
               holder -> {
                  EntityType<?> entityType = (EntityType<?>)holder.value();
                  Map<ResourceKey<LootTable>, Builder> map = (Map<ResourceKey<LootTable>, Builder>)this.map.remove(entityType);
                  if (this.canHaveLootTable(entityType)) {
                     ResourceKey<LootTable> resourceKey = entityType.getDefaultLootTable();
                     if (!resourceKey.equals(BuiltInLootTables.EMPTY) && !this.skipValidationFor(resourceKey) && (map == null || !map.containsKey(resourceKey))
                        )
                      {
                        throw new IllegalStateException(String.format(Locale.ROOT, "Missing loot table '%s' for '%s'", resourceKey, holder.key().location()));
                     }

                     if (map != null) {
                        map.forEach(
                           (resourceLocation, builder) -> {
                              if (!lootTables.add((ResourceKey<LootTable>)resourceLocation)) {
                                 throw new IllegalStateException(
                                    String.format(Locale.ROOT, "Duplicate loot table '%s' for '%s'", resourceLocation, holder.key().location())
                                 );
                              } else {
                                 consumer.accept((ResourceKey<LootTable>)resourceLocation, builder);
                              }
                           }
                        );
                     }
                  } else if (map != null) {
                     throw new IllegalStateException(
                        String.format(
                           Locale.ROOT,
                           "Weird loot table(s) '%s' for '%s', not a LivingEntity so should not have loot",
                           map.keySet().stream().map(ResourceKey::location).<CharSequence>map(ResourceLocation::toString).collect(Collectors.joining(",")),
                           holder.key().location()
                        )
                     );
                  }
               }
            );
         if (!this.map.isEmpty()) {
            throw new IllegalStateException("Created loot tables for entities not supported by data pack: " + this.map.keySet());
         }
      }

      @Override
      public Provider registries() {
         Preconditions.checkState(super.registries != RegistryAccess.EMPTY, "registry access is empty");
         return super.registries;
      }

      @Override
      public PathProvider pathProvider() {
         return this.pathProvider;
      }

      @Override
      public LootContextParamSet paramSet() {
         return LootContextParamSets.ENTITY;
      }

      @Override
      public boolean skipValidationFor(ResourceKey<LootTable> resourceKey) {
         return this.skipValidation.contains(resourceKey);
      }

      public void skipValidation(ResourceLocation resourceLocation) {
         this.skipValidation(ResourceKey.create(Registries.LOOT_TABLE, resourceLocation));
      }

      public void skipValidation(ResourceKey<LootTable> resourceKey) {
         this.skipValidation.add(resourceKey);
      }

      public void skipValidation(EntityType<?> entityType) {
         this.skipValidation(entityType.getDefaultLootTable());
      }

      protected boolean canHaveLootTable(EntityType<?> entityType) {
         return entityType.getCategory() != MobCategory.MISC;
      }

      protected Stream<Reference<EntityType<?>>> getRegistryEntries() {
         return BuiltInRegistries.ENTITY_TYPE.holders().filter(holder -> holder.key().location().getNamespace().equals(this.modId));
      }
   }

   public interface LootTableDataProvider extends DataProvider, LootTableSubProvider {
      Provider registries();

      PathProvider pathProvider();

      LootContextParamSet paramSet();

      boolean skipValidationFor(ResourceKey<LootTable> var1);

      default CompletableFuture<?> run(CachedOutput output, Provider registries) {
         DefaultedMappedRegistry<LootTable> registry = new DefaultedMappedRegistry("empty", Registries.LOOT_TABLE, Lifecycle.experimental(), false);
         ResourceKey<LootTable> defaultKey = ResourceKey.create(Registries.LOOT_TABLE, registry.getDefaultKey());
         registry.register(defaultKey, LootTable.EMPTY, RegistrationInfo.BUILT_IN);
         Map<Seed128bit, ResourceLocation> seeds = new Object2ObjectOpenHashMap();
         this.generate((resourceKey, builder) -> {
            ResourceLocation resourceLocation = resourceKey.location();
            ResourceLocation oldResourceLocation = seeds.put(RandomSequence.seedForKey(resourceLocation), resourceLocation);
            if (oldResourceLocation != null) {
               Util.logAndPauseIfInIde("Loot table random sequence seed collision on " + oldResourceLocation + " and " + resourceKey);
            }

            builder.setRandomSequence(resourceLocation);
            LootTable lootTable = builder.setParamSet(this.paramSet()).build();
            registry.register(resourceKey, lootTable, RegistrationInfo.BUILT_IN);
         });
         registry.freeze();
         this.validate(registry);
         return CompletableFuture.allOf(registry.entrySet().stream().filter(entry -> entry.getKey() != defaultKey).map(entry -> {
            ResourceKey<LootTable> resourceKey = (ResourceKey<LootTable>)entry.getKey();
            LootTable lootTable = (LootTable)entry.getValue();
            Path path = this.pathProvider().json(resourceKey.location());
            return DataProvider.saveStable(output, registries, LootTable.DIRECT_CODEC, lootTable, path);
         }).toArray(CompletableFuture[]::new));
      }

      default void validate(Registry<LootTable> registry) {
         Collector collector = new Collector();
         net.minecraft.core.HolderGetter.Provider registries = new ImmutableRegistryAccess(List.of(registry)).freeze().asGetterLookup();
         ValidationContext validationContext = new ValidationContext(collector, LootContextParamSets.ALL_PARAMS, registries);
         registry.holders().forEach(holder -> this.validate((Reference<LootTable>)holder, validationContext));
         Multimap<String, String> multimap = collector.get();
         if (!multimap.isEmpty()) {
            multimap.forEach((string, string2) -> LOGGER.warn("Found validation problem in {}: {}", string, string2));
            throw new IllegalStateException("Failed to validate loot tables, see logs");
         }
      }

      default void validate(Reference<LootTable> holder, ValidationContext validationContext) {
         if (!this.skipValidationFor(holder.key())) {
            ((LootTable)holder.value())
               .validate(validationContext.setParams(((LootTable)holder.value()).getParamSet()).enterElement("{" + holder.key().location() + "}", holder.key()));
         }
      }
   }

   public abstract static class Simple implements AbstractLootProvider.LootTableDataProvider {
      private final Map<ResourceKey<LootTable>, Builder> tables = new HashMap<>();
      private final Set<ResourceKey<LootTable>> skipValidation = new HashSet<>();
      private final LootContextParamSet paramSet;
      private final PathProvider pathProvider;
      private final CompletableFuture<Provider> registries;
      private Provider registryAccess;

      public Simple(LootContextParamSet paramSet, DataProviderContext context) {
         this(paramSet, context.getPackOutput(), context.getRegistries());
      }

      public Simple(LootContextParamSet paramSet, PackOutput packOutput, CompletableFuture<Provider> registries) {
         this.paramSet = paramSet;
         this.pathProvider = packOutput.createRegistryElementsPathProvider(Registries.LOOT_TABLE);
         this.registries = registries;
         this.registryAccess = RegistryAccess.EMPTY;
      }

      public CompletableFuture<?> run(CachedOutput output) {
         return this.registries.thenCompose(registries -> {
            this.registryAccess = registries;
            return this.run(output, registries).thenRun(() -> this.registryAccess = RegistryAccess.EMPTY);
         });
      }

      public String getName() {
         return String.join(" ", StringUtils.splitByCharacterTypeCamelCase(this.getClass().getSimpleName()));
      }

      public void generate(BiConsumer<ResourceKey<LootTable>, Builder> exporter) {
         this.addLootTables();
         this.tables.forEach(exporter);
      }

      @Override
      public Provider registries() {
         Preconditions.checkState(this.registryAccess != RegistryAccess.EMPTY, "registry access is empty");
         return this.registryAccess;
      }

      @Override
      public PathProvider pathProvider() {
         return this.pathProvider;
      }

      @Override
      public LootContextParamSet paramSet() {
         return this.paramSet;
      }

      @Override
      public boolean skipValidationFor(ResourceKey<LootTable> resourceKey) {
         return this.skipValidation.contains(resourceKey);
      }

      public void skipValidation(ResourceLocation resourceLocation) {
         this.skipValidation(ResourceKey.create(Registries.LOOT_TABLE, resourceLocation));
      }

      public void skipValidation(ResourceKey<LootTable> resourceKey) {
         this.skipValidation.add(resourceKey);
      }

      protected void add(ResourceKey<LootTable> table, Builder builder) {
         this.tables.put(table, builder);
      }

      public abstract void addLootTables();
   }
}
