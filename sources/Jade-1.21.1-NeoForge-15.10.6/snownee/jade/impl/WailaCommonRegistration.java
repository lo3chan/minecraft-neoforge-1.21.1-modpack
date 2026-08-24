package snownee.jade.impl;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IJadeProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.impl.config.PluginConfig;
import snownee.jade.impl.lookup.HierarchyLookup;
import snownee.jade.impl.lookup.PairHierarchyLookup;
import snownee.jade.impl.lookup.WrappedHierarchyLookup;

public class WailaCommonRegistration implements IWailaCommonRegistration {
   private static final WailaCommonRegistration INSTANCE = new WailaCommonRegistration();
   public final PairHierarchyLookup<IServerDataProvider<BlockAccessor>> blockDataProviders = new PairHierarchyLookup<>(
      new HierarchyLookup<>(Block.class), new HierarchyLookup<>(BlockEntity.class)
   );
   public final HierarchyLookup<IServerDataProvider<EntityAccessor>> entityDataProviders;
   public final PriorityStore<ResourceLocation, IJadeProvider> priorities;
   public final WrappedHierarchyLookup<IServerExtensionProvider<ItemStack>> itemStorageProviders;
   public final WrappedHierarchyLookup<IServerExtensionProvider<CompoundTag>> fluidStorageProviders;
   public final WrappedHierarchyLookup<IServerExtensionProvider<CompoundTag>> energyStorageProviders;
   public final WrappedHierarchyLookup<IServerExtensionProvider<CompoundTag>> progressProviders;
   private CommonRegistrationSession session;

   WailaCommonRegistration() {
      this.blockDataProviders.idMapped();
      this.entityDataProviders = new HierarchyLookup<>(Entity.class);
      this.entityDataProviders.idMapped();
      this.priorities = new PriorityStore<>(IJadeProvider::getDefaultPriority, IJadeProvider::getUid);
      this.priorities
         .setSortingFunction(
            (store, allKeys) -> {
               List<ResourceLocation> keys = allKeys.stream()
                  .filter(PluginConfig::isPrimaryKey)
                  .sorted(Comparator.comparingInt(store::byKey))
                  .collect(Collectors.toCollection(ArrayList::new));
               allKeys.stream().filter(Predicate.not(PluginConfig::isPrimaryKey)).forEach($ -> {
                  int index = keys.indexOf(PluginConfig.getPrimaryKey($));
                  keys.add(index + 1, $);
               });
               return keys;
            }
         );
      this.priorities.configurable("jade/sort-order", ResourceLocation.CODEC);
      this.itemStorageProviders = WrappedHierarchyLookup.forAccessor();
      this.fluidStorageProviders = WrappedHierarchyLookup.forAccessor();
      this.energyStorageProviders = WrappedHierarchyLookup.forAccessor();
      this.progressProviders = WrappedHierarchyLookup.forAccessor();
   }

   public static WailaCommonRegistration instance() {
      return INSTANCE;
   }

   @Override
   public void registerBlockDataProvider(IServerDataProvider<BlockAccessor> dataProvider, Class<?> blockOrBlobkEntityClass) {
      if (this.isSessionActive()) {
         this.session.registerBlockDataProvider(dataProvider, blockOrBlobkEntityClass);
      } else {
         this.blockDataProviders.register(blockOrBlobkEntityClass, dataProvider);
      }
   }

   @Override
   public void registerEntityDataProvider(IServerDataProvider<EntityAccessor> dataProvider, Class<? extends Entity> entityClass) {
      if (this.isSessionActive()) {
         this.session.registerEntityDataProvider(dataProvider, entityClass);
      } else {
         this.entityDataProviders.register(entityClass, dataProvider);
      }
   }

   public List<IServerDataProvider<BlockAccessor>> getBlockNBTProviders(Block block, @Nullable BlockEntity blockEntity) {
      return blockEntity == null ? this.blockDataProviders.first.get(block) : this.blockDataProviders.getMerged(block, blockEntity);
   }

   public List<IServerDataProvider<EntityAccessor>> getEntityNBTProviders(Entity entity) {
      return this.entityDataProviders.get(entity);
   }

   public void loadComplete() {
      this.blockDataProviders.loadComplete(this.priorities);
      this.entityDataProviders.loadComplete(this.priorities);
      this.itemStorageProviders.loadComplete(this.priorities);
      this.fluidStorageProviders.loadComplete(this.priorities);
      this.energyStorageProviders.loadComplete(this.priorities);
      this.progressProviders.loadComplete(this.priorities);
      this.session = null;
   }

   @Override
   public <T> void registerItemStorage(IServerExtensionProvider<ItemStack> provider, Class<? extends T> clazz) {
      if (this.isSessionActive()) {
         this.session.registerItemStorage(provider, clazz);
      } else {
         this.itemStorageProviders.register(clazz, provider);
      }
   }

   @Override
   public <T> void registerFluidStorage(IServerExtensionProvider<CompoundTag> provider, Class<? extends T> clazz) {
      if (this.isSessionActive()) {
         this.session.registerFluidStorage(provider, clazz);
      } else {
         this.fluidStorageProviders.register(clazz, provider);
      }
   }

   @Override
   public <T> void registerEnergyStorage(IServerExtensionProvider<CompoundTag> provider, Class<? extends T> clazz) {
      if (this.isSessionActive()) {
         this.session.registerEnergyStorage(provider, clazz);
      } else {
         this.energyStorageProviders.register(clazz, provider);
      }
   }

   @Override
   public <T> void registerProgress(IServerExtensionProvider<CompoundTag> provider, Class<? extends T> clazz) {
      if (this.isSessionActive()) {
         this.session.registerProgress(provider, clazz);
      } else {
         this.progressProviders.register(clazz, provider);
      }
   }

   public void startSession() {
      if (this.session == null) {
         this.session = new CommonRegistrationSession(this);
      }

      this.session.reset();
   }

   public void endSession() {
      Preconditions.checkState(this.session != null, "Session not started");
      this.session.end();
   }

   public boolean isSessionActive() {
      return this.session != null && this.session.isActive();
   }
}
