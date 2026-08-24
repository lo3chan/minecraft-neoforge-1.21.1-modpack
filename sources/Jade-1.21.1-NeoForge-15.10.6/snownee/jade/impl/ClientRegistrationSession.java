package snownee.jade.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IComponentProvider;
import snownee.jade.api.IJadeProvider;
import snownee.jade.api.IToggleableProvider;
import snownee.jade.api.callback.JadeAfterRenderCallback;
import snownee.jade.api.callback.JadeBeforeRenderCallback;
import snownee.jade.api.callback.JadeBeforeTooltipCollectCallback;
import snownee.jade.api.callback.JadeItemModNameCallback;
import snownee.jade.api.callback.JadeRayTraceCallback;
import snownee.jade.api.callback.JadeTooltipCollectedCallback;
import snownee.jade.api.view.EnergyView;
import snownee.jade.api.view.FluidView;
import snownee.jade.api.view.IClientExtensionProvider;
import snownee.jade.api.view.ItemView;
import snownee.jade.api.view.ProgressView;
import snownee.jade.impl.config.PluginConfig;
import snownee.jade.impl.config.entry.BooleanConfigEntry;
import snownee.jade.impl.config.entry.ConfigEntry;
import snownee.jade.impl.config.entry.EnumConfigEntry;
import snownee.jade.impl.config.entry.FloatConfigEntry;
import snownee.jade.impl.config.entry.IntConfigEntry;
import snownee.jade.impl.config.entry.StringConfigEntry;
import snownee.jade.impl.lookup.IHierarchyLookup;

public class ClientRegistrationSession {
   private final WailaClientRegistration registration;
   private boolean active;
   private final List<Pair<IComponentProvider<BlockAccessor>, Class<? extends Block>>> blockIconProviders = Lists.newArrayList();
   private final List<Pair<IComponentProvider<BlockAccessor>, Class<? extends Block>>> blockComponentProviders = Lists.newArrayList();
   private final List<Pair<IComponentProvider<EntityAccessor>, Class<? extends Entity>>> entityIconProviders = Lists.newArrayList();
   private final List<Pair<IComponentProvider<EntityAccessor>, Class<? extends Entity>>> entityComponentProviders = Lists.newArrayList();
   private final List<ConfigEntry<?>> configEntries = Lists.newArrayList();
   private final Set<ResourceLocation> configIds = Sets.newHashSet();
   private final List<Pair<ResourceLocation, Consumer<ResourceLocation>>> configListeners = Lists.newArrayList();
   private final List<Pair<ResourceLocation, Component>> configCategoryOverrides = Lists.newArrayList();
   private final List<IClientExtensionProvider<ItemStack, ItemView>> itemStorageProviders = Lists.newArrayList();
   private final List<IClientExtensionProvider<CompoundTag, FluidView>> fluidStorageProviders = Lists.newArrayList();
   private final List<IClientExtensionProvider<CompoundTag, EnergyView>> energyStorageProviders = Lists.newArrayList();
   private final List<IClientExtensionProvider<CompoundTag, ProgressView>> progressProviders = Lists.newArrayList();
   private final List<Pair<Integer, JadeAfterRenderCallback>> afterRenderCallback = Lists.newArrayList();
   private final List<Pair<Integer, JadeBeforeRenderCallback>> beforeRenderCallback = Lists.newArrayList();
   private final List<Pair<Integer, JadeRayTraceCallback>> rayTraceCallback = Lists.newArrayList();
   private final List<Pair<Integer, JadeTooltipCollectedCallback>> tooltipCollectedCallback = Lists.newArrayList();
   private final List<Pair<Integer, JadeItemModNameCallback>> itemModNameCallback = Lists.newArrayList();
   private final List<Pair<Integer, JadeBeforeTooltipCollectCallback>> beforeTooltipCollectCallback = Lists.newArrayList();

   public ClientRegistrationSession(WailaClientRegistration registration) {
      this.registration = registration;
   }

   private static <T extends IJadeProvider, C> void register(
      T provider, List<Pair<T, Class<? extends C>>> list, IHierarchyLookup<T> lookup, Class<? extends C> clazz
   ) {
      Preconditions.checkArgument(lookup.isClassAcceptable(clazz), "Class %s is not acceptable", clazz);
      Objects.requireNonNull(provider.getUid());
      list.add(Pair.of(provider, clazz));
   }

   public void registerBlockIcon(IComponentProvider<BlockAccessor> provider, Class<? extends Block> blockClass) {
      register(provider, this.blockIconProviders, this.registration.blockIconProviders, blockClass);
      this.tryAddConfig(provider);
   }

   public void registerBlockComponent(IComponentProvider<BlockAccessor> provider, Class<? extends Block> blockClass) {
      register(provider, this.blockComponentProviders, this.registration.blockComponentProviders, blockClass);
      this.tryAddConfig(provider);
   }

   public void registerEntityIcon(IComponentProvider<EntityAccessor> provider, Class<? extends Entity> entityClass) {
      register(provider, this.entityIconProviders, this.registration.entityIconProviders, entityClass);
      this.tryAddConfig(provider);
   }

   public void registerEntityComponent(IComponentProvider<EntityAccessor> provider, Class<? extends Entity> entityClass) {
      register(provider, this.entityComponentProviders, this.registration.entityComponentProviders, entityClass);
      this.tryAddConfig(provider);
   }

   public void addConfig(ResourceLocation key, boolean defaultValue) {
      this.configEntries.add(new BooleanConfigEntry(key, defaultValue));
      this.configIds.add(key);
   }

   public <T extends Enum<T>> void addConfig(ResourceLocation key, T defaultValue) {
      this.configEntries.add(new EnumConfigEntry<>(key, defaultValue));
      this.configIds.add(key);
   }

   public void addConfig(ResourceLocation key, String defaultValue, Predicate<String> validator) {
      this.configEntries.add(new StringConfigEntry(key, defaultValue, validator));
      this.configIds.add(key);
   }

   public void addConfig(ResourceLocation key, int defaultValue, int min, int max, boolean slider) {
      this.configEntries.add(new IntConfigEntry(key, defaultValue, min, max, slider));
      this.configIds.add(key);
   }

   public void addConfig(ResourceLocation key, float defaultValue, float min, float max, boolean slider) {
      this.configEntries.add(new FloatConfigEntry(key, defaultValue, min, max, slider));
      this.configIds.add(key);
   }

   private void tryAddConfig(IToggleableProvider provider) {
      ResourceLocation key = provider.getUid();
      if (!provider.isRequired()) {
         this.configIds.add(key);
      }
   }

   public void addConfigListener(ResourceLocation key, Consumer<ResourceLocation> listener) {
      this.configListeners.add(Pair.of(key, listener));
   }

   public void setConfigCategoryOverride(ResourceLocation key, Component override) {
      Preconditions.checkNotNull(override, "Override cannot be null");
      Preconditions.checkArgument(this.configIds.contains(key) || PluginConfig.INSTANCE.containsKey(key), "Unknown config key: %s", key);
      Preconditions.checkArgument(PluginConfig.isPrimaryKey(key), "Only primary config key can be overridden");
      this.configCategoryOverrides.add(Pair.of(key, override));
   }

   public void setConfigCategoryOverride(ResourceLocation key, List<Component> overrides) {
      for (Component override : overrides) {
         this.setConfigCategoryOverride(key, override);
      }
   }

   public void registerItemStorageClient(IClientExtensionProvider<ItemStack, ItemView> provider) {
      this.itemStorageProviders.add(provider);
   }

   public void registerFluidStorageClient(IClientExtensionProvider<CompoundTag, FluidView> provider) {
      this.fluidStorageProviders.add(provider);
   }

   public void registerEnergyStorageClient(IClientExtensionProvider<CompoundTag, EnergyView> provider) {
      this.energyStorageProviders.add(provider);
   }

   public void registerProgressClient(IClientExtensionProvider<CompoundTag, ProgressView> provider) {
      this.progressProviders.add(provider);
   }

   public void addAfterRenderCallback(int priority, JadeAfterRenderCallback callback) {
      this.afterRenderCallback.add(Pair.of(priority, callback));
   }

   public void addBeforeRenderCallback(int priority, JadeBeforeRenderCallback callback) {
      this.beforeRenderCallback.add(Pair.of(priority, callback));
   }

   public void addRayTraceCallback(int priority, JadeRayTraceCallback callback) {
      this.rayTraceCallback.add(Pair.of(priority, callback));
   }

   public void addTooltipCollectedCallback(int priority, JadeTooltipCollectedCallback callback) {
      this.tooltipCollectedCallback.add(Pair.of(priority, callback));
   }

   public void addItemModNameCallback(int priority, JadeItemModNameCallback callback) {
      this.itemModNameCallback.add(Pair.of(priority, callback));
   }

   public void addBeforeTooltipCollectCallback(int priority, JadeBeforeTooltipCollectCallback callback) {
      this.beforeTooltipCollectCallback.add(Pair.of(priority, callback));
   }

   public void reset() {
      this.blockIconProviders.clear();
      this.blockComponentProviders.clear();
      this.entityIconProviders.clear();
      this.entityComponentProviders.clear();
      this.configEntries.clear();
      this.configIds.clear();
      this.configListeners.clear();
      this.configCategoryOverrides.clear();
      this.itemStorageProviders.clear();
      this.fluidStorageProviders.clear();
      this.energyStorageProviders.clear();
      this.progressProviders.clear();
      this.afterRenderCallback.clear();
      this.beforeRenderCallback.clear();
      this.rayTraceCallback.clear();
      this.tooltipCollectedCallback.clear();
      this.itemModNameCallback.clear();
      this.beforeTooltipCollectCallback.clear();
      this.active = true;
   }

   public void end() {
      Preconditions.checkState(this.active, "Session is not active");
      this.active = false;
      this.blockIconProviders
         .forEach(pair -> this.registration.registerBlockIcon((IComponentProvider<BlockAccessor>)pair.getFirst(), (Class<? extends Block>)pair.getSecond()));
      this.blockComponentProviders
         .forEach(
            pair -> this.registration.registerBlockComponent((IComponentProvider<BlockAccessor>)pair.getFirst(), (Class<? extends Block>)pair.getSecond())
         );
      this.entityIconProviders
         .forEach(pair -> this.registration.registerEntityIcon((IComponentProvider<EntityAccessor>)pair.getFirst(), (Class<? extends Entity>)pair.getSecond()));
      this.entityComponentProviders
         .forEach(
            pair -> this.registration.registerEntityComponent((IComponentProvider<EntityAccessor>)pair.getFirst(), (Class<? extends Entity>)pair.getSecond())
         );
      this.configEntries.forEach(PluginConfig.INSTANCE::addConfig);
      this.configListeners
         .forEach(pair -> this.registration.addConfigListener((ResourceLocation)pair.getFirst(), (Consumer<ResourceLocation>)pair.getSecond()));
      this.configCategoryOverrides.forEach(pair -> this.registration.setConfigCategoryOverride((ResourceLocation)pair.getFirst(), (Component)pair.getSecond()));
      this.itemStorageProviders.forEach(this.registration::registerItemStorageClient);
      this.fluidStorageProviders.forEach(this.registration::registerFluidStorageClient);
      this.energyStorageProviders.forEach(this.registration::registerEnergyStorageClient);
      this.progressProviders.forEach(this.registration::registerProgressClient);
      this.afterRenderCallback.forEach(pair -> this.registration.addAfterRenderCallback((Integer)pair.getFirst(), (JadeAfterRenderCallback)pair.getSecond()));
      this.beforeRenderCallback
         .forEach(pair -> this.registration.addBeforeRenderCallback((Integer)pair.getFirst(), (JadeBeforeRenderCallback)pair.getSecond()));
      this.rayTraceCallback.forEach(pair -> this.registration.addRayTraceCallback((Integer)pair.getFirst(), (JadeRayTraceCallback)pair.getSecond()));
      this.tooltipCollectedCallback
         .forEach(pair -> this.registration.addTooltipCollectedCallback((Integer)pair.getFirst(), (JadeTooltipCollectedCallback)pair.getSecond()));
      this.itemModNameCallback.forEach(pair -> this.registration.addItemModNameCallback((Integer)pair.getFirst(), (JadeItemModNameCallback)pair.getSecond()));
      this.beforeTooltipCollectCallback
         .forEach(pair -> this.registration.addBeforeTooltipCollectCallback((Integer)pair.getFirst(), (JadeBeforeTooltipCollectCallback)pair.getSecond()));
   }

   public boolean isActive() {
      return this.active;
   }
}
