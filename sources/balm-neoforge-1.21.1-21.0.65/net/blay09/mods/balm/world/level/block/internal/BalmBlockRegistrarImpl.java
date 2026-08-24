package net.blay09.mods.balm.world.level.block.internal;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.blay09.mods.balm.core.BalmRegistrar;
import net.blay09.mods.balm.world.level.block.BalmBlockRegistrar;
import net.blay09.mods.balm.world.level.block.BalmBlockRegistration;
import net.blay09.mods.balm.world.level.block.BalmDiscriminatedBlockRegistration;
import net.blay09.mods.balm.world.level.block.DeferredBlock;
import net.blay09.mods.balm.world.level.block.DiscriminatedBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BalmBlockRegistrarImpl implements BalmBlockRegistrar {
   private final BalmRegistrar registrar;
   private final String namespace;

   public BalmBlockRegistrarImpl(BalmRegistrar registrar, String namespace) {
      this.registrar = registrar;
      this.namespace = namespace;
   }

   @Override
   public BalmBlockRegistration register(String name, Function<Properties, Block> constructor, Supplier<Properties> properties) {
      ResourceLocation identifier = ResourceLocation.fromNamespaceAndPath(this.namespace, name);
      ResourceKey<Block> resourceKey = ResourceKey.create(Registries.BLOCK, identifier);
      Holder<Block> holder = this.registrar.register(resourceKey, id -> constructor.apply(properties.get()));
      return new BalmBlockRegistrarImpl.BalmBlockRegistrationImpl(this.namespace, this.registrar, holder);
   }

   @Override
   public void addAlias(ResourceLocation oldId, ResourceLocation newId) {
      this.registrar.addAlias(Registries.BLOCK, oldId, newId);
   }

   @Override
   public void addAlias(String oldName, String newName) {
      this.addAlias(ResourceLocation.fromNamespaceAndPath(this.namespace, oldName), ResourceLocation.fromNamespaceAndPath(this.namespace, newName));
   }

   @Override
   public <T> BalmDiscriminatedBlockRegistration<T> registerDiscriminated(
      Set<T> values, Function<T, String> nameFunction, BiFunction<T, Properties, Block> constructor, BiFunction<T, Properties, Properties> propertiesFunction
   ) {
      BalmBlockRegistrarImpl.BalmDiscriminatedBlockRegistrationImpl<T> map = new BalmBlockRegistrarImpl.BalmDiscriminatedBlockRegistrationImpl<>();

      for (T value : values) {
         String name = nameFunction.apply(value);
         BalmBlockRegistration registration = this.register(
            name, properties -> constructor.apply(value, properties), properties -> propertiesFunction.apply(value, properties)
         );
         map.put(value, registration);
      }

      return map;
   }

   private static final class BalmBlockRegistrationImpl implements BalmBlockRegistration {
      private final String namespace;
      private final BalmRegistrar registrar;
      private final Holder<Block> holder;
      private DeferredBlock deferredBlock;

      private BalmBlockRegistrationImpl(String namespace, BalmRegistrar registrar, Holder<Block> holder) {
         this.namespace = namespace;
         this.registrar = registrar;
         this.holder = holder;
      }

      @Override
      public BalmBlockRegistration withItem(
         BiFunction<Block, net.minecraft.world.item.Item.Properties, BlockItem> constructor, Supplier<net.minecraft.world.item.Item.Properties> properties
      ) {
         ResourceKey<Block> blockResourceKey = (ResourceKey<Block>)this.holder.unwrapKey().orElseThrow();
         ResourceKey<Item> itemResourceKey = ResourceKey.create(Registries.ITEM, blockResourceKey.location());
         this.registrar.register(itemResourceKey, id -> (Item)constructor.apply((Block)this.holder.value(), properties.get()));
         return this;
      }

      @Override
      public BalmBlockRegistration withItem(
         String name,
         BiFunction<Block, net.minecraft.world.item.Item.Properties, BlockItem> constructor,
         Function<net.minecraft.world.item.Item.Properties, net.minecraft.world.item.Item.Properties> propertiesBuilder
      ) {
         ResourceLocation itemIdentifier = ResourceLocation.fromNamespaceAndPath(this.namespace, name);
         ResourceKey<Item> itemResourceKey = ResourceKey.create(Registries.ITEM, itemIdentifier);
         this.registrar
            .register(
               itemResourceKey,
               id -> (Item)constructor.apply((Block)this.holder.value(), propertiesBuilder.apply(new net.minecraft.world.item.Item.Properties()))
            );
         return this;
      }

      @Override
      public Holder<Block> asHolder() {
         return this.holder;
      }

      @Override
      public DeferredBlock asDeferredBlock() {
         if (this.deferredBlock == null) {
            this.deferredBlock = new DeferredBlockImpl(this.holder);
         }

         return this.deferredBlock;
      }
   }

   private static class BalmDiscriminatedBlockRegistrationImpl<T> extends HashMap<T, BalmBlockRegistration> implements BalmDiscriminatedBlockRegistration<T> {
      @Override
      public DiscriminatedBlocks<T> asDiscriminatedBlocks() {
         BalmBlockRegistrarImpl.DiscriminatedBlocksImpl<T> map = new BalmBlockRegistrarImpl.DiscriminatedBlocksImpl<>();
         this.forEach((key, registration) -> map.put((T)key, registration.asDeferredBlock()));
         return map;
      }
   }

   private static class DiscriminatedBlocksImpl<T> extends HashMap<T, DeferredBlock> implements DiscriminatedBlocks<T> {
      @Override
      public Stream<Entry<T, DeferredBlock>> sortedEntries(Comparator<T> comparator) {
         return this.entrySet().stream().sorted(Entry.comparingByKey(comparator));
      }

      @Override
      public Stream<Entry<T, DeferredBlock>> filterNonNullDiscriminatorEntries() {
         return this.entrySet().stream().filter(it -> it.getKey() != null);
      }

      @Override
      public Stream<DeferredBlock> filterNonNullDiscriminators() {
         return this.entrySet().stream().filter(it -> it.getKey() != null).map(Entry::getValue);
      }
   }
}
