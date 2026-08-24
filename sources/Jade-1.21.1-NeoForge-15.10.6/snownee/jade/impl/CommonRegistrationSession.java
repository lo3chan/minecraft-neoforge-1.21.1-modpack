package snownee.jade.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IJadeProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.impl.lookup.IHierarchyLookup;

public class CommonRegistrationSession {
   private final WailaCommonRegistration registration;
   private boolean active;
   private final List<Pair<IServerDataProvider<BlockAccessor>, Class<?>>> blockDataProviders = Lists.newArrayList();
   private final List<Pair<IServerDataProvider<EntityAccessor>, Class<? extends Entity>>> entityDataProviders = Lists.newArrayList();
   private final List<Pair<IServerExtensionProvider<ItemStack>, Class<?>>> itemStorageProviders = Lists.newArrayList();
   private final List<Pair<IServerExtensionProvider<CompoundTag>, Class<?>>> fluidStorageProviders = Lists.newArrayList();
   private final List<Pair<IServerExtensionProvider<CompoundTag>, Class<?>>> energyStorageProviders = Lists.newArrayList();
   private final List<Pair<IServerExtensionProvider<CompoundTag>, Class<?>>> progressProviders = Lists.newArrayList();

   public CommonRegistrationSession(WailaCommonRegistration registration) {
      this.registration = registration;
   }

   private static <T extends IJadeProvider, C> void register(
      T provider, List<Pair<T, Class<? extends C>>> list, IHierarchyLookup<T> lookup, Class<? extends C> clazz
   ) {
      Preconditions.checkArgument(lookup.isClassAcceptable(clazz), "Class %s is not acceptable", clazz);
      Objects.requireNonNull(provider.getUid());
      list.add(Pair.of(provider, clazz));
   }

   public void registerBlockDataProvider(IServerDataProvider<BlockAccessor> dataProvider, Class<?> blockOrBlobkEntityClass) {
      register(dataProvider, this.blockDataProviders, this.registration.blockDataProviders, blockOrBlobkEntityClass);
   }

   public void registerEntityDataProvider(IServerDataProvider<EntityAccessor> dataProvider, Class<? extends Entity> entityClass) {
      register(dataProvider, this.entityDataProviders, this.registration.entityDataProviders, entityClass);
   }

   public <T> void registerEnergyStorage(IServerExtensionProvider<CompoundTag> provider, Class<? extends T> clazz) {
      register(provider, this.energyStorageProviders, this.registration.energyStorageProviders, clazz);
   }

   public <T> void registerItemStorage(IServerExtensionProvider<ItemStack> provider, Class<? extends T> clazz) {
      register(provider, this.itemStorageProviders, this.registration.itemStorageProviders, clazz);
   }

   public <T> void registerFluidStorage(IServerExtensionProvider<CompoundTag> provider, Class<? extends T> clazz) {
      register(provider, this.fluidStorageProviders, this.registration.fluidStorageProviders, clazz);
   }

   public <T> void registerProgress(IServerExtensionProvider<CompoundTag> provider, Class<? extends T> clazz) {
      register(provider, this.progressProviders, this.registration.progressProviders, clazz);
   }

   public void reset() {
      this.blockDataProviders.clear();
      this.entityDataProviders.clear();
      this.itemStorageProviders.clear();
      this.fluidStorageProviders.clear();
      this.energyStorageProviders.clear();
      this.progressProviders.clear();
      this.active = true;
   }

   public void end() {
      Preconditions.checkState(this.active, "Session is not active");
      this.active = false;
      this.blockDataProviders
         .forEach(pair -> this.registration.registerBlockDataProvider((IServerDataProvider<BlockAccessor>)pair.getFirst(), (Class<?>)pair.getSecond()));
      this.entityDataProviders
         .forEach(
            pair -> this.registration
               .registerEntityDataProvider((IServerDataProvider<EntityAccessor>)pair.getFirst(), (Class<? extends Entity>)pair.getSecond())
         );
      this.itemStorageProviders
         .forEach(pair -> this.registration.registerItemStorage((IServerExtensionProvider<ItemStack>)pair.getFirst(), (Class)pair.getSecond()));
      this.fluidStorageProviders
         .forEach(pair -> this.registration.registerFluidStorage((IServerExtensionProvider<CompoundTag>)pair.getFirst(), (Class)pair.getSecond()));
      this.energyStorageProviders
         .forEach(pair -> this.registration.registerEnergyStorage((IServerExtensionProvider<CompoundTag>)pair.getFirst(), (Class)pair.getSecond()));
      this.progressProviders
         .forEach(pair -> this.registration.registerProgress((IServerExtensionProvider<CompoundTag>)pair.getFirst(), (Class)pair.getSecond()));
   }

   public boolean isActive() {
      return this.active;
   }
}
