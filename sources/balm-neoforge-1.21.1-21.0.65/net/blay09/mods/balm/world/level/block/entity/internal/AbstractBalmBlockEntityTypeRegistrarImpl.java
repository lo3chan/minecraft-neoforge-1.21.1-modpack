package net.blay09.mods.balm.world.level.block.entity.internal;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.blay09.mods.balm.core.BalmRegistrar;
import net.blay09.mods.balm.world.level.block.BlockLike;
import net.blay09.mods.balm.world.level.block.entity.BalmBlockEntityTypeRegistrar;
import net.blay09.mods.balm.world.level.block.entity.BalmBlockEntityTypeRegistration;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public abstract class AbstractBalmBlockEntityTypeRegistrarImpl implements BalmBlockEntityTypeRegistrar {
   private final BalmRegistrar registrar;
   private final String namespace;

   public AbstractBalmBlockEntityTypeRegistrarImpl(BalmRegistrar registrar, String namespace) {
      this.registrar = registrar;
      this.namespace = namespace;
   }

   @Override
   public <T extends BlockEntity> BalmBlockEntityTypeRegistration<T> register(
      String name, BalmBlockEntityTypeRegistrar.BlockEntitySupplier<T> constructor, BlockLike... blocks
   ) {
      return this.register(name, constructor, Set.of(blocks));
   }

   @Override
   public <T extends BlockEntity> BalmBlockEntityTypeRegistration<T> register(
      String name, BalmBlockEntityTypeRegistrar.BlockEntitySupplier<T> constructor, Iterable<? extends BlockLike> blocks
   ) {
      return this.register(name, constructor, () -> {
         HashSet<Block> resolvedBlocks = new HashSet<>();

         for (BlockLike blockLike : blocks) {
            resolvedBlocks.add(blockLike.asBlock());
         }

         return resolvedBlocks;
      });
   }

   @Override
   public <T extends BlockEntity> BalmBlockEntityTypeRegistration<T> register(
      String name, BalmBlockEntityTypeRegistrar.BlockEntitySupplier<T> constructor, Supplier<Set<Block>> blocksSupplier
   ) {
      ResourceLocation identifier = ResourceLocation.fromNamespaceAndPath(this.namespace, name);
      ResourceKey<BlockEntityType<?>> resourceKey = ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, identifier);
      Holder<BlockEntityType<?>> holder = this.registrar.register(resourceKey, id -> this.createBlockEntityType(constructor, blocksSupplier.get()));
      return new AbstractBalmBlockEntityTypeRegistrarImpl.BalmBlockEntityTypeRegistrationImpl<>(holder);
   }

   @Override
   public void addAlias(ResourceLocation oldId, ResourceLocation newId) {
      this.registrar.addAlias(Registries.BLOCK_ENTITY_TYPE, oldId, newId);
   }

   @Override
   public void addAlias(String oldName, String newName) {
      this.addAlias(ResourceLocation.fromNamespaceAndPath(this.namespace, oldName), ResourceLocation.fromNamespaceAndPath(this.namespace, newName));
   }

   private static final class BalmBlockEntityTypeRegistrationImpl<T extends BlockEntity>
      implements BalmBlockEntityTypeRegistration<T>,
      Supplier<BlockEntityType<T>> {
      private final Holder<BlockEntityType<T>> holder;

      private BalmBlockEntityTypeRegistrationImpl(Holder<?> holder) {
         this.holder = (Holder<BlockEntityType<T>>)holder;
      }

      @Override
      public Holder<BlockEntityType<T>> asHolder() {
         return this.holder;
      }

      public BlockEntityType<T> get() {
         return (BlockEntityType<T>)this.holder.value();
      }

      @Override
      public Supplier<BlockEntityType<T>> asSupplier() {
         return this;
      }
   }
}
