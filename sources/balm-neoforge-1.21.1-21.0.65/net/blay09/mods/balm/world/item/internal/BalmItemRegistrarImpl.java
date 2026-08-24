package net.blay09.mods.balm.world.item.internal;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.blay09.mods.balm.core.BalmRegistrar;
import net.blay09.mods.balm.world.item.BalmDiscriminatedItemRegistration;
import net.blay09.mods.balm.world.item.BalmItemRegistrar;
import net.blay09.mods.balm.world.item.BalmItemRegistration;
import net.blay09.mods.balm.world.item.DeferredItem;
import net.blay09.mods.balm.world.item.DiscriminatedItems;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;

public class BalmItemRegistrarImpl implements BalmItemRegistrar {
   private final BalmRegistrar registrar;
   private final String namespace;

   public BalmItemRegistrarImpl(BalmRegistrar registrar, String namespace) {
      this.registrar = registrar;
      this.namespace = namespace;
   }

   @Override
   public BalmItemRegistration register(String name, Function<Properties, Item> constructor, Supplier<Properties> properties) {
      ResourceLocation identifier = ResourceLocation.fromNamespaceAndPath(this.namespace, name);
      ResourceKey<Item> resourceKey = ResourceKey.create(Registries.ITEM, identifier);
      Holder<Item> holder = this.registrar.register(resourceKey, id -> constructor.apply(properties.get()));
      return new BalmItemRegistrarImpl.BalmItemRegistrationImpl(holder);
   }

   @Override
   public void addAlias(ResourceLocation oldId, ResourceLocation newId) {
      this.registrar.addAlias(Registries.ITEM, oldId, newId);
   }

   @Override
   public void addAlias(String oldName, String newName) {
      this.addAlias(ResourceLocation.fromNamespaceAndPath(this.namespace, oldName), ResourceLocation.fromNamespaceAndPath(this.namespace, newName));
   }

   @Override
   public <T> BalmDiscriminatedItemRegistration<T> registerDiscriminated(
      Set<T> values, Function<T, String> nameFunction, BiFunction<T, Properties, Item> constructor, BiFunction<T, Properties, Properties> propertiesFunction
   ) {
      BalmItemRegistrarImpl.BalmDiscriminatedItemRegistrationImpl<T> map = new BalmItemRegistrarImpl.BalmDiscriminatedItemRegistrationImpl<>();

      for (T value : values) {
         String name = nameFunction.apply(value);
         BalmItemRegistration registration = this.register(
            name, properties -> constructor.apply(value, properties), properties -> propertiesFunction.apply(value, properties)
         );
         map.put(value, registration);
      }

      return map;
   }

   private static class BalmDiscriminatedItemRegistrationImpl<T> extends HashMap<T, BalmItemRegistration> implements BalmDiscriminatedItemRegistration<T> {
      @Override
      public DiscriminatedItems<T> asDiscriminatedItems() {
         BalmItemRegistrarImpl.DiscriminatedItemsImpl<T> map = new BalmItemRegistrarImpl.DiscriminatedItemsImpl<>();
         this.forEach((key, registration) -> map.put((T)key, registration.asDeferredItem()));
         return map;
      }
   }

   private static class BalmItemRegistrationImpl implements BalmItemRegistration {
      private final Holder<Item> holder;
      private DeferredItem deferredItem;

      private BalmItemRegistrationImpl(Holder<Item> holder) {
         this.holder = holder;
      }

      @Override
      public Holder<Item> asHolder() {
         return this.holder;
      }

      @Override
      public DeferredItem asDeferredItem() {
         if (this.deferredItem == null) {
            this.deferredItem = new DeferredItemImpl(this.holder);
         }

         return this.deferredItem;
      }
   }

   private static class DiscriminatedItemsImpl<T> extends HashMap<T, DeferredItem> implements DiscriminatedItems<T> {
      @Override
      public Stream<Entry<T, DeferredItem>> sortedEntries(Comparator<T> comparator) {
         return this.entrySet().stream().sorted(Entry.comparingByKey(comparator));
      }
   }
}
