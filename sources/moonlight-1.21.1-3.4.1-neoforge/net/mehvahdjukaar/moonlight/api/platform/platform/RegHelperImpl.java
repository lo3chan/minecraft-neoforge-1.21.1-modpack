package net.mehvahdjukaar.moonlight.api.platform.platform;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.mehvahdjukaar.moonlight.api.fluids.ModFlowingFluid;
import net.mehvahdjukaar.moonlight.api.misc.IAttachmentType;
import net.mehvahdjukaar.moonlight.api.misc.RegSupplier;
import net.mehvahdjukaar.moonlight.api.misc.Registrator;
import net.mehvahdjukaar.moonlight.api.misc.TriFunction;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.resources.recipe.platform.OptionalRecipeCondition;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.misc.AttachmentBuilderImpl;
import net.mehvahdjukaar.moonlight.platform.MoonlightForge;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Kind;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.world.entity.SpawnPlacements.SpawnPredicate;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.MenuType.MenuSupplier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.Builder;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.component.FireworkExplosion.Shape;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.FireworkStarRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer.Factory;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.common.world.poi.ExtendPoiTypesEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent.Operation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent.NewRegistry;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RegHelperImpl {
   private static final Map<ResourceKey<? extends Registry<?>>, Map<String, DeferredRegister<?>>> REGISTRIES = new ConcurrentHashMap<>();
   private static final List<Pair<String, Consumer<IEventBus>>> RUN_LATER = new ArrayList<>();

   public static <T, E extends T> RegSupplier<E> register(ResourceLocation name, Supplier<E> supplier, Registry<T> reg) {
      return register(name, supplier, reg.key());
   }

   public static <T, E extends T> RegSupplier<E> register(ResourceLocation name, Supplier<E> supplier, ResourceKey<? extends Registry<T>> regKey) {
      if (supplier == null) {
         throw new IllegalArgumentException("Registry entry Supplier for " + name + " can't be null");
      } else if (name.getNamespace().equals("minecraft")) {
         throw new IllegalArgumentException("Registering under minecraft namespace is not supported");
      } else {
         Map<String, DeferredRegister<?>> m = REGISTRIES.computeIfAbsent(regKey, h -> new ConcurrentHashMap<>());
         String modId = name.getNamespace();
         DeferredRegister<T> registry = (DeferredRegister<T>)m.computeIfAbsent(modId, c -> {
            Moonlight.addDependent(modId);
            DeferredRegister<T> r = DeferredRegister.create(regKey, modId);
            doWithBus(modId, r::register);
            return r;
         });
         DeferredHolder<T, E> register = registry.register(name.getPath(), () -> {
            E obj = supplier.get();
            if (regKey.equals(Registries.FLUID) && obj instanceof ModFlowingFluid fluid) {
               register(name, fluid::getFluidType, Keys.FLUID_TYPES);
            }

            return obj;
         });
         return new RegHelperImpl.Wrapper<>(register);
      }
   }

   private static void doWithBus(String modId, Consumer<IEventBus> consumer) {
      if (Moonlight.isInitPhase()) {
         consumer.accept(getModEventBus(modId));
      } else {
         RUN_LATER.add(Pair.of(modId, consumer));
      }
   }

   public static void runTasksOnInit() {
      for (Pair<String, Consumer<IEventBus>> e : RUN_LATER) {
         ((Consumer)e.getSecond()).accept(getModEventBus((String)e.getFirst()));
      }

      RUN_LATER.clear();
   }

   private static IEventBus getModEventBus(String modId) {
      ModList modList = ModList.get();
      if (modId.equals("fabric") || modId.equals("neoforge")) {
         modId = "moonlight";
      }

      Preconditions.checkNotNull(modList, "ModList was null. This means that some mod registry classes were loaded way too early, likely by mixins");
      ModContainer cont = (ModContainer)modList.getModContainerById(modId).get();
      IEventBus bus;
      if (cont instanceof FMLModContainer container) {
         bus = container.getEventBus();
      } else {
         Moonlight.LOGGER.warn("Failed to get mod container for mod {}", modId);
         bus = MoonlightForge.getCurrentBus();
      }

      return bus;
   }

   public static <T, E extends T> RegSupplier<E> registerAsync(ResourceLocation name, Supplier<E> supplier, ResourceKey<? extends Registry<T>> reg) {
      return register(name, supplier, reg);
   }

   public static <T> void registerInBatch(Registry<T> reg, Consumer<Registrator<T>> eventListener) {
      Consumer<RegisterEvent> eventConsumer = event -> {
         if (event.getRegistry() == reg) {
            eventListener.accept((r, o) -> Registry.register(reg, r, o));
         }
      };
      MoonlightForge.getCurrentBus().addListener(eventConsumer);
   }

   public static <C extends AbstractContainerMenu> RegSupplier<MenuType<C>> registerMenuType(
      ResourceLocation name, TriFunction<Integer, Inventory, FriendlyByteBuf, C> containerFactory
   ) {
      return register(name, () -> IMenuTypeExtension.create(containerFactory::apply), Registries.MENU);
   }

   public static <C extends AbstractContainerMenu> RegSupplier<MenuType<C>> registerSimpleMenuType(ResourceLocation name, MenuSupplier<C> containerFactory) {
      return register(name, () -> new MenuType(containerFactory, FeatureFlags.DEFAULT_FLAGS), Registries.MENU);
   }

   public static <T extends Entity> RegSupplier<EntityType<T>> registerEntityType(
      ResourceLocation name, EntityFactory<T> factory, MobCategory category, float width, float height, int clientTrackingRange, int updateInterval
   ) {
      return register(
         name, () -> net.minecraft.world.entity.EntityType.Builder.of(factory, category).sized(width, height).build(name.toString()), Registries.ENTITY_TYPE
      );
   }

   public static <T extends Fluid> RegSupplier<T> registerFluid(ResourceLocation name, Supplier<T> fluid) {
      return register(name, fluid, Registries.FLUID);
   }

   public static <T extends CraftingRecipe> RegSupplier<RecipeSerializer<T>> registerSpecialRecipe(ResourceLocation name, Factory<T> factory) {
      return RegHelper.registerRecipeSerializer(name, () -> new SimpleCraftingRecipeSerializer(factory));
   }

   public static RegSupplier<CreativeModeTab> registerCreativeModeTab(
      ResourceLocation name, boolean hasSearchBar, List<ResourceLocation> afterEntries, List<ResourceLocation> beforeEntries, Consumer<Builder> configurator
   ) {
      return register(name, () -> {
         Builder b = CreativeModeTab.builder();
         configurator.accept(b);
         if (!beforeEntries.isEmpty()) {
            b.withTabsBefore(beforeEntries.toArray(ResourceLocation[]::new));
         }

         if (!afterEntries.isEmpty()) {
            b.withTabsBefore(afterEntries.toArray(ResourceLocation[]::new));
         }

         if (hasSearchBar) {
            b.withSearchBar();
         }

         return b.build();
      }, Registries.CREATIVE_MODE_TAB);
   }

   public static void registerItemBurnTime(Item item, int burnTime) {
   }

   public static void registerBlockFlammability(Block item, int igniteOdds, int burnOdds) {
      ((FireBlock)Blocks.FIRE).setFlammable(item, igniteOdds, burnOdds);
   }

   public static void addAttributeRegistration(Consumer<RegHelper.AttributeEvent> eventListener) {
      Moonlight.assertInitPhase();
      Consumer<EntityAttributeCreationEvent> eventConsumer = event -> eventListener.accept((e, b) -> event.put(e, b.build()));
      MoonlightForge.getCurrentBus().addListener(eventConsumer);
   }

   public static void addCommandRegistration(RegHelper.CommandRegistration eventListener) {
      Moonlight.assertInitPhase();
      Consumer<RegisterCommandsEvent> eventConsumer = event -> eventListener.accept(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
      NeoForge.EVENT_BUS.addListener(eventConsumer);
   }

   public static void addSpawnPlacementsRegistration(Consumer<RegHelper.SpawnPlacementEvent> eventListener) {
      Moonlight.assertInitPhase();
      Consumer<RegisterSpawnPlacementsEvent> eventConsumer = event -> {
         RegHelper.SpawnPlacementEvent spawnPlacementEvent = new RegHelperImpl.PlacementEventImpl(event);
         eventListener.accept(spawnPlacementEvent);
      };
      MoonlightForge.getCurrentBus().addListener(eventConsumer);
   }

   public static void registerSimpleRecipeCondition(ResourceLocation id, Predicate<String> predicate) {
      register(id, () -> OptionalRecipeCondition.createCodec(id, predicate), Keys.CONDITION_CODECS);
   }

   public static <A> Registry<A> registerRegistry(ResourceKey<Registry<A>> key, boolean synced) {
      String modId = key.location().getNamespace();
      DeferredRegister<A> defer = DeferredRegister.create(key, modId);
      Registry<A> reg = defer.makeRegistry(b -> b.sync(synced));
      doWithBus(modId, defer::register);
      return reg;
   }

   public static <T> void registerDataPackRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec, @Nullable Codec<T> networkCodec) {
      Moonlight.assertInitPhase();
      Consumer<NewRegistry> eventConsumer = event -> event.dataPackRegistry(registryKey, codec, networkCodec);
      IEventBus bus = MoonlightForge.getCurrentBus();
      bus.addListener(eventConsumer);
   }

   public static void addItemsToTabsRegistration(Consumer<RegHelper.ItemToTabEvent> eventListener) {
      Moonlight.assertInitPhase();
      Consumer<BuildCreativeModeTabContentsEvent> eventConsumer = event -> {
         RegHelper.ItemToTabEvent itemToTabEvent = new RegHelperImpl.ItemToTabEventImpl(event);
         eventListener.accept(itemToTabEvent);
      };
      MoonlightForge.getCurrentBus().addListener(EventPriority.LOWEST, eventConsumer);
   }

   public static void addLootTableInjects(Consumer<RegHelper.LootInjectEvent> eventListener) {
      Moonlight.assertInitPhase();
      Consumer<LootTableLoadEvent> eventConsumer = event -> eventListener.accept(new RegHelper.LootInjectEvent() {
         @Override
         public ResourceLocation getTable() {
            return event.getName();
         }

         @Override
         public void addTableReference(ResourceLocation targetId) {
            LootPool pool = LootPool.lootPool().add(NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, targetId))).build();
            event.getTable().addPool(pool);
         }
      });
      NeoForge.EVENT_BUS.addListener(eventConsumer);
   }

   public static void registerFireworkRecipe(Shape shape, Item ingredient) {
      FireworkStarRecipe.SHAPE_BY_ITEM = new HashMap(FireworkStarRecipe.SHAPE_BY_ITEM);
      FireworkStarRecipe.SHAPE_BY_ITEM.put(ingredient, shape);
      FireworkStarRecipe.SHAPE_INGREDIENT = CompoundIngredient.of(
         new Ingredient[]{FireworkStarRecipe.SHAPE_INGREDIENT, Ingredient.of(new ItemLike[]{ingredient})}
      );
   }

   @Deprecated(
      forRemoval = true
   )
   public static void startRegisteringFor(Object bus) {
      if (bus instanceof IEventBus b) {
         MoonlightForge.startRegistering(b);
      } else {
         throw new IllegalArgumentException("Invalid bus type. Must be of IEventBus type: " + bus);
      }
   }

   public static <T> Supplier<EntityDataSerializer<T>> registerEntityDataSerializer(ResourceLocation name, Supplier<EntityDataSerializer<T>> serializer) {
      return RegHelper.register(name, serializer, Keys.ENTITY_DATA_SERIALIZERS);
   }

   @Deprecated(
      forRemoval = true
   )
   public static void addBlocksToPOI(ResourceKey<PoiType> poi, Iterable<? extends Block> blocks) {
      MoonlightForge.addPoi(poi, blocks);
   }

   public static void addExtraPOIStatesRegistration(Consumer<RegHelper.ExtraPOIStatesEvent> eventListener) {
      Moonlight.assertInitPhase();
      Consumer<ExtendPoiTypesEvent> eventConsumer = event -> eventListener.accept(new RegHelper.ExtraPOIStatesEvent() {
         @Override
         public void addBlock(ResourceKey<PoiType> typeKey, Block block) {
            event.addBlockToPoi(typeKey, block);
         }

         @Override
         public void addStates(ResourceKey<PoiType> typeKey, Set<BlockState> states) {
            event.addStatesToPoi(typeKey, states);
         }
      });
      MoonlightForge.getCurrentBus().addListener(eventConsumer);
   }

   public static void registerResourcePackSource(PackType packType, RepositorySource packSource) {
      Moonlight.assertInitPhase();
      IEventBus bus = MoonlightForge.getCurrentBus();
      Consumer<AddPackFindersEvent> consumer = event -> {
         if (event.getPackType() == packType) {
            event.addRepositorySource(packSource);
         }
      };
      bus.addListener(consumer);
   }

   public static <A, T> IAttachmentType<A, T> registerDataAttachment(ResourceLocation id, Supplier<RegHelper.AttachmentBuilder<A>> config, Class<T> targetClass) {
      if (!IAttachmentHolder.class.isAssignableFrom(targetClass)) {
         Moonlight.LOGGER.warn("Registering data attachment for invalid class {} that does not implements IAttachmentHolder. ", targetClass.getName());
      }

      RegSupplier<AttachmentType<A>> attachment = RegHelper.register(id, () -> makeDataAttachmentBuilder(config).build(), Keys.ATTACHMENT_TYPES);
      return new RegHelperImpl.AttachmentWrapper<>(attachment);
   }

   private static <A> net.neoforged.neoforge.attachment.AttachmentType.Builder<A> makeDataAttachmentBuilder(Supplier<RegHelper.AttachmentBuilder<A>> config) {
      AttachmentBuilderImpl<A> c = (AttachmentBuilderImpl<A>)config.get();
      net.neoforged.neoforge.attachment.AttachmentType.Builder<A> b = AttachmentType.builder(c.initializer);
      if (c.sync != null) {
         b.sync((iAttachmentHolder, player) -> ((BiPredicate)c.sync.getSecond()).test(iAttachmentHolder, player), (StreamCodec)c.sync.getFirst());
      }

      if (c.persistentCodec != null) {
         b.serialize(c.persistentCodec);
      }

      if (c.copyOnDeath) {
         b.copyOnDeath();
      }

      return b;
   }

   public static void addExtraBEBlockStatesRegistration(Consumer<RegHelper.ExtraBEStatesEvent> eventListener) {
      Moonlight.assertInitPhase();
      Consumer<BlockEntityTypeAddBlocksEvent> eventConsumer = event -> eventListener.accept(event::modify);
      MoonlightForge.getCurrentBus().addListener(eventConsumer);
   }

   private record AttachmentWrapper<A, T>(Supplier<AttachmentType<A>> typeSupplier) implements IAttachmentType<A, T> {
      @Override
      public A getOrCreate(T attachmentHolder) {
         if (attachmentHolder instanceof IAttachmentHolder h) {
            return (A)h.getData(this.typeSupplier);
         } else {
            throw new IllegalArgumentException("Object " + attachmentHolder + " is not an attachment holder");
         }
      }

      @Override
      public A getOrNull(T attachmentHolder) {
         return (A)(attachmentHolder instanceof IAttachmentHolder h ? h.getExistingDataOrNull(this.typeSupplier) : null);
      }

      @Override
      public void set(T attachmentHolder, @Nullable A data) {
         if (attachmentHolder instanceof IAttachmentHolder h) {
            if (data == null) {
               h.removeData(this.typeSupplier);
            } else {
               h.setData(this.typeSupplier, data);
            }
         } else {
            throw new IllegalArgumentException("Object " + attachmentHolder + " is not an attachment holder");
         }
      }

      @Override
      public void sync(T attachmentHolder) {
         if (attachmentHolder instanceof IAttachmentHolder h) {
            h.syncData(this.typeSupplier);
         } else {
            throw new IllegalArgumentException("Object " + attachmentHolder + " is not an attachment holder");
         }
      }
   }

   private record ItemToTabEventImpl(BuildCreativeModeTabContentsEvent event) implements RegHelper.ItemToTabEvent {
      @Override
      public ItemDisplayParameters getParameters() {
         return this.event.getParameters();
      }

      @Override
      public CreativeModeTab getTab() {
         return this.event.getTab();
      }

      @Override
      public void remove(ResourceKey<CreativeModeTab> tab, Predicate<ItemStack> target) {
         if (this.event.getTabKey() == tab) {
            ;
         }
      }

      @Override
      public void addItems(ResourceKey<CreativeModeTab> tab, @Nullable Predicate<ItemStack> target, boolean after, List<ItemStack> items) {
         if (this.event.getTabKey() == tab) {
            if (target != null) {
               if (after) {
                  ItemStack last = this.findLast(this.event, target);
                  if (!last.isEmpty()) {
                     TabVisibility vis = this.getTabVisibility(last);

                     for (int j = items.size(); j > 0; j--) {
                        this.event.insertAfter(last, items.get(j - 1), vis);
                     }

                     return;
                  }

                  Moonlight.logIfInDev("Failed to find target item before for items: " + items);
               } else {
                  ItemStack first = this.findFirst(this.event, target);
                  if (!first.isEmpty()) {
                     TabVisibility vis = this.getTabVisibility(first);

                     for (ItemStack s : items) {
                        this.event.insertBefore(first, s, vis);
                     }

                     return;
                  }

                  Moonlight.logIfInDev("Failed to find target item after for items: " + items);
               }
            }

            this.event.acceptAll(items);
         }
      }

      @NotNull
      private TabVisibility getTabVisibility(ItemStack first) {
         TabVisibility vis;
         if (this.event.getSearchEntries().contains(first)) {
            vis = TabVisibility.PARENT_AND_SEARCH_TABS;
         } else {
            vis = TabVisibility.PARENT_TAB_ONLY;
            Moonlight.LOGGER.warn("Found an item that was in parent tab but not in search tab. This might be a bug? {}", first);
         }

         return vis;
      }

      private ItemStack findFirst(BuildCreativeModeTabContentsEvent event, Predicate<ItemStack> target) {
         ObjectBidirectionalIterator var3 = event.getParentEntries().iterator();

         while (var3.hasNext()) {
            ItemStack s = (ItemStack)var3.next();
            if (target.test(s)) {
               return s;
            }
         }

         return ItemStack.EMPTY;
      }

      private ItemStack findLast(BuildCreativeModeTabContentsEvent event, Predicate<ItemStack> target) {
         boolean foundOne = false;
         ItemStack previous = ItemStack.EMPTY;
         ObjectBidirectionalIterator var5 = event.getParentEntries().iterator();

         while (var5.hasNext()) {
            ItemStack s = (ItemStack)var5.next();
            if (target.test(s)) {
               foundOne = true;
               previous = s;
            } else if (foundOne) {
               return previous;
            }
         }

         return previous;
      }
   }

   record PlacementEventImpl(RegisterSpawnPlacementsEvent event) implements RegHelper.SpawnPlacementEvent {
      @Override
      public <T extends Mob> void register(
         EntityType<T> entityType, SpawnPlacementType decoratorType, Types heightMapType, SpawnPredicate<T> decoratorPredicate
      ) {
         this.event.register(entityType, decoratorType, heightMapType, decoratorPredicate, Operation.AND);
      }
   }

   public record Wrapper<T>(DeferredHolder<T, ? extends T> registryObject) implements RegSupplier<T> {
      @Override
      public T get() {
         return (T)this.registryObject.get();
      }

      @Override
      public ResourceLocation getId() {
         return this.registryObject.getId();
      }

      @Nullable
      @Override
      public ResourceKey<T> getKey() {
         return this.registryObject.getKey();
      }

      public T value() {
         return (T)this.registryObject.get();
      }

      public boolean isBound() {
         return this.registryObject.isBound();
      }

      public boolean is(ResourceLocation location) {
         return this.registryObject.is(location);
      }

      public boolean is(ResourceKey<T> resourceKey) {
         return this.registryObject.is(resourceKey);
      }

      public boolean is(Predicate<ResourceKey<T>> predicate) {
         return this.registryObject.is(predicate);
      }

      public boolean is(TagKey<T> tagKey) {
         return this.registryObject.is(tagKey);
      }

      public boolean is(Holder<T> holder) {
         return this.registryObject.is(holder);
      }

      public Stream<TagKey<T>> tags() {
         return this.registryObject.tags();
      }

      public Either<ResourceKey<T>, T> unwrap() {
         return this.registryObject.unwrap();
      }

      public Optional<ResourceKey<T>> unwrapKey() {
         return this.registryObject.unwrapKey();
      }

      public Kind kind() {
         return this.registryObject.kind();
      }

      public boolean canSerializeIn(HolderOwner<T> owner) {
         return this.registryObject.canSerializeIn(owner);
      }

      @Override
      public boolean equals(Object obj) {
         return this.registryObject.equals(obj);
      }

      @Override
      public int hashCode() {
         return this.registryObject.hashCode();
      }

      public Holder<T> getDelegate() {
         return this.registryObject.getDelegate();
      }

      @Override
      public String toString() {
         return this.registryObject.toString();
      }

      @Nullable
      public <T1> T1 getData(DataMapType<T, T1> type) {
         return (T1)this.registryObject.getData(type);
      }
   }
}
