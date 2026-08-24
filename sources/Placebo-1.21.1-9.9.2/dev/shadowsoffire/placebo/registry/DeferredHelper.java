package dev.shadowsoffire.placebo.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.shadowsoffire.placebo.Placebo;
import dev.shadowsoffire.placebo.block_entity.TickingBlockEntity;
import dev.shadowsoffire.placebo.block_entity.TickingBlockEntityType;
import dev.shadowsoffire.placebo.menu.MenuUtil;
import dev.shadowsoffire.placebo.util.DeferredSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicate.Type;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.MenuType.MenuSupplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Experimental;

public class DeferredHelper {
   protected static final ResourceKey<? extends Registry<?>> ROOT_REGISTRY_KEY = ResourceKey.createRegistryKey(Registries.ROOT_REGISTRY_NAME);
   protected static final ResourceKey<Registry<DataMapType<?, ?>>> DATA_MAP_KEY = ResourceKey.createRegistryKey(
      ResourceLocation.fromNamespaceAndPath("neoforge", "data_map_type")
   );
   protected final String modid;
   protected final Map<ResourceKey<? extends Registry<?>>, List<DeferredHelper.Registrar<?>>> objects;
   protected final Map<ResourceKey<? extends Registry<?>>, List<Holder<?>>> resolvedObjects;

   public static DeferredHelper create(String modid) {
      return new DeferredHelper(modid);
   }

   protected DeferredHelper(String modid) {
      this.modid = modid;
      this.objects = new IdentityHashMap<>();
      this.resolvedObjects = new IdentityHashMap<>();
   }

   public <T> Registry<T> registry(String registryPath, UnaryOperator<RegistryBuilder<T>> config) {
      ResourceKey<? extends Registry<T>> registryKey = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(this.modid, registryPath));
      Registry<T> registry = config.apply(new RegistryBuilder(registryKey)).create();
      this.registerRegistry(registryKey, registry);
      return registry;
   }

   public <T extends Block> DeferredBlock<T> block(String path, Supplier<T> factory) {
      this.register(path, Registries.BLOCK, factory);
      return DeferredBlock.createBlock(ResourceLocation.fromNamespaceAndPath(this.modid, path));
   }

   public <T extends Block> DeferredBlock<T> block(String path, Function<Properties, T> ctor, UnaryOperator<Properties> properties) {
      return this.block(path, () -> ctor.apply(properties.apply(Properties.of())));
   }

   public <T extends Fluid> DeferredHolder<Fluid, T> fluid(String path, Supplier<T> factory) {
      return this.registerDH(path, Registries.FLUID, factory);
   }

   public <T extends Item> DeferredItem<T> item(String path, Supplier<T> factory) {
      this.register(path, Registries.ITEM, factory);
      return DeferredItem.createItem(ResourceLocation.fromNamespaceAndPath(this.modid, path));
   }

   public <T extends Item> DeferredItem<T> item(
      String path, Function<net.minecraft.world.item.Item.Properties, T> ctor, UnaryOperator<net.minecraft.world.item.Item.Properties> properties
   ) {
      return this.item(path, () -> ctor.apply(properties.apply(new net.minecraft.world.item.Item.Properties())));
   }

   public <T extends Item> DeferredItem<T> item(String path, Function<net.minecraft.world.item.Item.Properties, T> ctor) {
      return this.item(path, ctor, UnaryOperator.identity());
   }

   public <T extends BlockItem> DeferredItem<T> blockItem(
      String path,
      Holder<Block> block,
      BiFunction<Block, net.minecraft.world.item.Item.Properties, T> ctor,
      UnaryOperator<net.minecraft.world.item.Item.Properties> properties
   ) {
      return this.item(path, () -> ctor.apply((Block)block.value(), properties.apply(new net.minecraft.world.item.Item.Properties())));
   }

   public DeferredItem<BlockItem> blockItem(String path, Holder<Block> block, UnaryOperator<net.minecraft.world.item.Item.Properties> properties) {
      return this.blockItem(path, block, BlockItem::new, properties);
   }

   public DeferredItem<BlockItem> blockItem(String path, Holder<Block> block) {
      return this.blockItem(path, block, UnaryOperator.identity());
   }

   public <T extends MobEffect> DeferredHolder<MobEffect, T> effect(String path, Supplier<T> factory) {
      return this.registerDH(path, Registries.MOB_EFFECT, factory);
   }

   public <T extends SoundEvent> DeferredHolder<SoundEvent, T> sound(String path, Supplier<T> factory) {
      return this.registerDH(path, Registries.SOUND_EVENT, factory);
   }

   public Holder<SoundEvent> sound(String path) {
      return this.<SoundEvent>sound(path, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(this.modid, path)));
   }

   public <T extends Potion> DeferredHolder<Potion, T> potion(String path, Supplier<T> factory) {
      return this.registerDH(path, Registries.POTION, factory);
   }

   public DeferredHolder<Potion, Potion> singlePotion(String path, Supplier<MobEffectInstance> factory) {
      return this.registerDH(path, Registries.POTION, () -> {
         MobEffectInstance inst = factory.get();
         ResourceLocation key = inst.getEffect().getKey().location();
         return new Potion(key.toLanguageKey(), new MobEffectInstance[]{inst});
      });
   }

   public DeferredHolder<Potion, Potion> multiPotion(String path, Supplier<List<MobEffectInstance>> factory) {
      String key = ResourceLocation.fromNamespaceAndPath(this.modid, path).toLanguageKey("potion");
      return this.registerDH(path, Registries.POTION, () -> new Potion(key, factory.get().toArray(new MobEffectInstance[0])));
   }

   public <U extends Entity, T extends EntityType<U>> DeferredHolder<EntityType<?>, T> entity(String path, Supplier<T> factory) {
      return this.registerDH(path, Registries.ENTITY_TYPE, factory);
   }

   public <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> entity(
      String path, EntityFactory<T> factory, MobCategory category, UnaryOperator<Builder<T>> op
   ) {
      String key = ResourceLocation.fromNamespaceAndPath(this.modid, path).toLanguageKey("entity");
      return this.entity(path, () -> op.apply(Builder.of(factory, category)).build(key));
   }

   public <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> blockEntity(
      String path, BlockEntitySupplier<T> factory, Supplier<Set<Block>> validBlocks
   ) {
      return this.registerDH(path, Registries.BLOCK_ENTITY_TYPE, () -> new BlockEntityType(factory, validBlocks.get(), null));
   }

   @SafeVarargs
   public final <T extends BlockEntity> BlockEntityType<T> blockEntity(String path, BlockEntitySupplier<T> factory, Holder<Block>... validBlocks) {
      unfreezeBETypeRegistry();
      BlockEntityType<T> type = new BlockEntityType(
         factory, new DeferredSet(() -> Arrays.stream(validBlocks).map(Holder::value).collect(Collectors.toSet())), null
      );
      this.register(path, Registries.BLOCK_ENTITY_TYPE, () -> {
         type.getValidBlocks();
         return type;
      });
      return type;
   }

   @SafeVarargs
   public final <T extends BlockEntity & TickingBlockEntity> TickingBlockEntityType<T> tickingBlockEntity(
      String path, BlockEntitySupplier<T> factory, TickingBlockEntityType.TickSide side, Holder<Block>... validBlocks
   ) {
      unfreezeBETypeRegistry();
      TickingBlockEntityType<T> type = new TickingBlockEntityType<>(
         factory, new DeferredSet<>(() -> Arrays.stream(validBlocks).<Block>map(Holder::value).collect(Collectors.toSet())), side
      );
      this.register(path, Registries.BLOCK_ENTITY_TYPE, () -> {
         type.getValidBlocks();
         return type;
      });
      return type;
   }

   public <U extends ParticleOptions, T extends ParticleType<U>> DeferredHolder<ParticleType<?>, T> particle(String path, Supplier<T> factory) {
      return this.registerDH(path, Registries.PARTICLE_TYPE, factory);
   }

   public SimpleParticleType simpleParticle(String path, boolean overrideLimit) {
      SimpleParticleType type = new SimpleParticleType(overrideLimit);
      this.register(path, Registries.PARTICLE_TYPE, () -> type);
      return type;
   }

   public <T extends ParticleOptions> ParticleType<T> particle(
      String path,
      boolean overrideLimit,
      final Function<ParticleType<T>, MapCodec<T>> codec,
      final Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodec
   ) {
      var type = new ParticleType<T>(overrideLimit) {
         public MapCodec<T> codec() {
            return codec.apply(this);
         }

         public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
            return streamCodec.apply(this);
         }
      };
      this.register(path, Registries.PARTICLE_TYPE, () -> type);
      return type;
   }

   public <U extends AbstractContainerMenu, T extends MenuType<U>> T menuType(String path, T type) {
      this.register(path, Registries.MENU, () -> type);
      return type;
   }

   public <T extends AbstractContainerMenu> MenuType<T> menu(String path, MenuSupplier<T> factory) {
      return this.menuType(path, MenuUtil.type(factory));
   }

   public <T extends AbstractContainerMenu> MenuType<T> menuWithPos(String path, MenuUtil.PosFactory<T> factory) {
      return this.menuType(path, MenuUtil.posType(factory));
   }

   public <T extends AbstractContainerMenu> MenuType<T> menuWithData(String path, IContainerFactory<T> factory) {
      return this.menuType(path, MenuUtil.bufType(factory));
   }

   public <C extends RecipeInput, U extends Recipe<C>, T extends RecipeType<U>> DeferredHolder<RecipeType<?>, T> recipe(String path, Supplier<T> factory) {
      return this.registerDH(path, Registries.RECIPE_TYPE, factory);
   }

   public <C extends RecipeInput, U extends Recipe<C>> RecipeType<U> recipe(String path) {
      RecipeType<U> type = RecipeType.simple(ResourceLocation.fromNamespaceAndPath(this.modid, path));
      this.recipe(path, () -> type);
      return type;
   }

   public <C extends RecipeInput, U extends Recipe<C>, T extends RecipeSerializer<U>> DeferredHolder<RecipeSerializer<?>, T> recipeSerializer(
      String path, Supplier<T> factory
   ) {
      return this.registerDH(path, Registries.RECIPE_SERIALIZER, factory);
   }

   public <T extends Attribute> DeferredHolder<Attribute, T> attribute(String path, Supplier<T> factory) {
      return this.registerDH(path, Registries.ATTRIBUTE, factory);
   }

   public DeferredHolder<Attribute, RangedAttribute> rangedAttribute(String path, double defaultValue, double min, double max) {
      String key = ResourceLocation.fromNamespaceAndPath(this.modid, path).toLanguageKey("attribute");
      return this.attribute(path, () -> new RangedAttribute(key, defaultValue, min, max));
   }

   public <S, U extends StatType<S>, T extends StatType<U>> DeferredHolder<StatType<?>, T> stat(String path, Supplier<T> factory) {
      return this.registerDH(path, Registries.STAT_TYPE, factory);
   }

   public ResourceLocation customStat(String path, StatFormatter formatter) {
      ResourceLocation id = ResourceLocation.fromNamespaceAndPath(this.modid, path);
      this.register(path, Registries.CUSTOM_STAT, () -> id, key -> Stats.CUSTOM.get(key, formatter));
      return id;
   }

   public <U extends FeatureConfiguration, T extends Feature<U>> DeferredHolder<Feature<?>, T> feature(String path, Supplier<T> factory) {
      return this.registerDH(path, Registries.FEATURE, factory);
   }

   public DeferredHolder<CreativeModeTab, CreativeModeTab> creativeTab(String path, UnaryOperator<net.minecraft.world.item.CreativeModeTab.Builder> operator) {
      return this.registerDH(path, Registries.CREATIVE_MODE_TAB, () -> operator.apply(CreativeModeTab.builder()).build());
   }

   public <T> DataComponentType<T> enchantmentEffect(String path, UnaryOperator<net.minecraft.core.component.DataComponentType.Builder<T>> operator) {
      DataComponentType<T> type = operator.apply(DataComponentType.builder()).build();
      this.register(path, Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, () -> type);
      return type;
   }

   public <T> DataComponentType<T> component(String path, UnaryOperator<net.minecraft.core.component.DataComponentType.Builder<T>> operator) {
      DataComponentType<T> type = operator.apply(DataComponentType.builder()).build();
      this.register(path, Registries.DATA_COMPONENT_TYPE, () -> type);
      return type;
   }

   public <T> AttachmentType<T> attachment(
      String path, Supplier<T> defaultValue, UnaryOperator<net.neoforged.neoforge.attachment.AttachmentType.Builder<T>> operator
   ) {
      AttachmentType<T> type = operator.apply(AttachmentType.builder(defaultValue)).build();
      this.register(path, Keys.ATTACHMENT_TYPES, () -> type);
      return type;
   }

   public <T> AttachmentType<T> attachment(
      String path, Function<IAttachmentHolder, T> defaultValue, UnaryOperator<net.neoforged.neoforge.attachment.AttachmentType.Builder<T>> operator
   ) {
      AttachmentType<T> type = operator.apply(AttachmentType.builder(defaultValue)).build();
      this.register(path, Keys.ATTACHMENT_TYPES, () -> type);
      return type;
   }

   public LootPoolEntryType lootPoolEntry(String path, LootPoolEntryType type) {
      this.register(path, Registries.LOOT_POOL_ENTRY_TYPE, () -> type);
      return type;
   }

   public <T extends IGlobalLootModifier> MapCodec<T> lootModifier(String path, MapCodec<T> codec) {
      this.register(path, Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, () -> codec);
      return codec;
   }

   public LootItemConditionType lootCondition(String path, MapCodec<? extends LootItemCondition> codec) {
      LootItemConditionType type = new LootItemConditionType(codec);
      this.register(path, Registries.LOOT_CONDITION_TYPE, () -> type);
      return type;
   }

   public <T extends ICustomIngredient> IngredientType<T> ingredient(String path, IngredientType<T> type) {
      this.register(path, Keys.INGREDIENT_TYPES, () -> type);
      return type;
   }

   public <T extends CriterionTrigger<?>> T criteriaTrigger(String path, T trigger) {
      this.register(path, Registries.TRIGGER_TYPE, () -> trigger);
      return trigger;
   }

   public <T extends ItemSubPredicate> Type<T> itemSubPredicate(String path, Codec<T> codec) {
      Type<T> type = new Type(codec);
      this.register(path, Registries.ITEM_SUB_PREDICATE_TYPE, () -> type);
      return type;
   }

   public <T extends StructureProcessor> StructureProcessorType<T> structureProcessor(String path, MapCodec<T> codec) {
      StructureProcessorType<T> type = () -> codec;
      this.register(path, Registries.STRUCTURE_PROCESSOR, () -> type);
      return type;
   }

   public <K, V> DataMapType<K, V> dataMap(
      String path,
      ResourceKey<? extends Registry<K>> targetRegistry,
      Codec<V> codec,
      UnaryOperator<net.neoforged.neoforge.registries.datamaps.DataMapType.Builder<V, K>> config
   ) {
      ResourceLocation id = ResourceLocation.fromNamespaceAndPath(this.modid, path);
      ResourceKey<? extends DataMapType<?, ?>> registryKey = ResourceKey.create(DATA_MAP_KEY, id);
      DataMapType<K, V> dataMapType = config.apply(DataMapType.builder(id, targetRegistry, codec)).build();
      this.registerDataMap(registryKey, dataMapType);
      return dataMapType;
   }

   @Deprecated(
      forRemoval = true
   )
   public <R, T extends R> DeferredHolder<R, T> custom(String path, ResourceKey<? extends Registry<R>> registry, Supplier<T> factory) {
      return this.registerDH(path, registry, factory);
   }

   public <R, T extends R> DeferredHolder<R, T> customDH(String path, ResourceKey<? extends Registry<R>> registry, Supplier<T> factory) {
      return this.registerDH(path, registry, factory);
   }

   public <R, T extends R> T custom(String path, ResourceKey<? extends Registry<R>> registry, T object) {
      this.register(path, registry, () -> object);
      return object;
   }

   @Experimental
   public <R> List<Holder<R>> getRegisteredObjects(ResourceKey<? extends Registry<R>> key) {
      return Collections.unmodifiableList(this.resolvedObjects.getOrDefault(key, List.of()));
   }

   protected <R, T extends R> void register(String path, ResourceKey<? extends Registry<R>> regKey, Supplier<T> factory, @Nullable Consumer<T> callback) {
      List<DeferredHelper.Registrar<?>> registrars = this.objects.computeIfAbsent(regKey, k -> new ArrayList<>());
      ResourceLocation id = ResourceLocation.fromNamespaceAndPath(this.modid, path);
      registrars.add(new DeferredHelper.Registrar<>(id, factory, callback));
   }

   protected <R, T extends R> void register(String path, ResourceKey<? extends Registry<R>> regKey, Supplier<T> factory) {
      this.register(path, regKey, factory, null);
   }

   protected <R, T extends R> DeferredHolder<R, T> registerDH(String path, ResourceKey<? extends Registry<R>> regKey, Supplier<T> factory) {
      this.register(path, regKey, factory);
      return DeferredHolder.create(regKey, ResourceLocation.fromNamespaceAndPath(this.modid, path));
   }

   protected <T> void registerRegistry(ResourceKey<? extends Registry<T>> key, Registry<T> registry) {
      List<DeferredHelper.Registrar<?>> registrars = this.objects.computeIfAbsent(ROOT_REGISTRY_KEY, k -> new ArrayList<>());
      ResourceLocation id = key.location();
      registrars.add(new DeferredHelper.Registrar<>(id, () -> registry));
   }

   protected <K, V> void registerDataMap(ResourceKey<? extends DataMapType<?, ?>> key, DataMapType<K, V> type) {
      List<DeferredHelper.Registrar<?>> registrars = this.objects.computeIfAbsent(DATA_MAP_KEY, k -> new ArrayList<>());
      ResourceLocation id = key.location();
      registrars.add(new DeferredHelper.Registrar<>(id, () -> type));
   }

   @SubscribeEvent
   public void register(RegisterEvent e) {
      Registry registry = e.getRegistry();

      for (DeferredHelper.Registrar<?> registrar : this.objects.getOrDefault(e.getRegistryKey(), Collections.emptyList())) {
         try {
            Object obj = registrar.factory.get();
            Registry.register(registry, registrar.id, obj);
            this.resolvedObjects.computeIfAbsent(e.getRegistryKey(), k -> new ArrayList<>()).add(registry.wrapAsHolder(obj));
            if (registrar.callback != null) {
               ((Consumer<Object>)registrar.callback).accept(obj);
            }
         } catch (Throwable var6) {
            Placebo.LOGGER.error("Exception thrown during registration of {}", registrar.id);
            throw var6;
         }
      }

      this.objects.remove(e.getRegistryKey());
   }

   @SubscribeEvent
   public void registerRegistries(NewRegistryEvent e) {
      for (DeferredHelper.Registrar<?> registrar : this.objects.getOrDefault(ROOT_REGISTRY_KEY, Collections.emptyList())) {
         try {
            Registry<?> obj = (Registry<?>)registrar.factory.get();
            e.register(obj);
         } catch (Throwable var5) {
            Placebo.LOGGER.error("Exception thrown during registration of registry {}", registrar.id);
            throw var5;
         }
      }

      this.objects.remove(ROOT_REGISTRY_KEY);
   }

   @SubscribeEvent
   public void registerDataMaps(RegisterDataMapTypesEvent e) {
      for (DeferredHelper.Registrar<?> registrar : this.objects.getOrDefault(DATA_MAP_KEY, Collections.emptyList())) {
         try {
            DataMapType<?, ?> obj = (DataMapType<?, ?>)registrar.factory.get();
            e.register(obj);
         } catch (Throwable var5) {
            Placebo.LOGGER.error("Exception thrown during registration of data map type {}", registrar.id);
            throw var5;
         }
      }

      this.objects.remove(DATA_MAP_KEY);
   }

   private static void unfreezeBETypeRegistry() {
      ((MappedRegistry)BuiltInRegistries.BLOCK_ENTITY_TYPE).unfreeze();
   }

   protected record Registrar<T>(ResourceLocation id, Supplier<T> factory, @Nullable Consumer<T> callback) {
      protected Registrar(ResourceLocation id, Supplier<T> factory) {
         this(id, factory, null);
      }
   }
}
