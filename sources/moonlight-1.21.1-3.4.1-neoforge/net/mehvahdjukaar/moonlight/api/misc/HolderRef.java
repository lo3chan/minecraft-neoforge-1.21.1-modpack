package net.mehvahdjukaar.moonlight.api.misc;

import java.util.Objects;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.function.Predicate;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.ApiStatus.Internal;

public class HolderRef<T> {
   private final ResourceKey<Registry<T>> registryKey;
   private final ResourceKey<T> key;
   private final WeakHashMap<Provider, Holder<T>> cache = new WeakHashMap<>();
   private static final WeakHashSet<HolderRef<?>> REFERENCES = new WeakHashSet<>();

   @Internal
   public static void clearCache() {
      REFERENCES.forEach(HolderRef::invalidateCache);
   }

   private void invalidateCache() {
      this.cache.clear();
   }

   protected HolderRef(ResourceKey<Registry<T>> registryKey, ResourceKey<T> key) {
      this.registryKey = registryKey;
      this.key = key;
      REFERENCES.add(this);
   }

   public static <A> HolderRef<A> wrap(A obj, ResourceKey<Registry<A>> registry) {
      return of(Utils.getID(obj), registry);
   }

   public static <A> HolderRef<A> of(String id, ResourceKey<Registry<A>> registry) {
      return of(ResourceLocation.tryParse(id), registry);
   }

   public static <A> HolderRef<A> of(ResourceLocation location, ResourceKey<Registry<A>> registry) {
      return new HolderRef((ResourceKey<Registry<T>>)registry, ResourceKey.create(registry, location));
   }

   public static <A> HolderRef<A> of(ResourceKey<A> key) {
      return new HolderRef(ResourceKey.createRegistryKey(key.registry()), (ResourceKey<T>)key);
   }

   public static <A> OptHolderRef<A> optional(ResourceLocation location, ResourceKey<Registry<A>> registry) {
      return new OptHolderRef((ResourceKey<Registry<T>>)registry, ResourceKey.create(registry, location));
   }

   public static <A> OptHolderRef<A> optional(ResourceKey<A> key) {
      return new OptHolderRef(ResourceKey.createRegistryKey(key.registry()), (ResourceKey<T>)key);
   }

   public T get(Entity entity) {
      return this.get(entity.level());
   }

   public T get(Level level) {
      return this.get(level.registryAccess());
   }

   public T get(LevelReader level) {
      return this.get(level.registryAccess());
   }

   public T get(Provider r) {
      return (T)this.getHolder(r).value();
   }

   public boolean is(T object, Provider r) {
      return this.getHolder(r).value() == object;
   }

   public boolean is(T object, LevelReader level) {
      return this.is(object, level.registryAccess());
   }

   public boolean is(TagKey<T> tag, Provider r) {
      return this.getHolder(r).is(tag);
   }

   public boolean is(TagKey<T> tag, LevelReader level) {
      return this.is(tag, level.registryAccess());
   }

   public Holder<T> getHolder(Entity entity) {
      return this.getHolder(entity.level());
   }

   public Holder<T> getHolder(Level level) {
      return this.getHolder(level.registryAccess());
   }

   public Holder<T> getHolder(LevelReader level) {
      return this.getHolder(level.registryAccess());
   }

   public Holder<T> getHolder(Provider r) {
      Holder<T> holder = this.cache.get(r);
      if (holder != null) {
         return holder;
      } else {
         Optional<RegistryLookup<T>> lookupReg = r.lookup(this.registryKey);
         RegistryLookup<T> reg = lookupReg.get();
         holder = this.lookup(reg);
         this.cache.put(r, holder);
         return holder;
      }
   }

   public Holder<T> lookup(HolderGetter<T> lookup) {
      try {
         return lookup.getOrThrow(this.key);
      } catch (Exception var5) {
         String extra = "";
         if (lookup instanceof HolderLookup<T> l) {
            extra = ".\nRegistry content was: " + l.listElements().map(b -> b.key().location()).toList();
         }

         throw new RuntimeException("Failed to get object from registry: " + this.key + ".\nCalled from " + Thread.currentThread() + ".\n" + extra);
      }
   }

   public String getRegisteredName() {
      return this.key.location().toString();
   }

   public ResourceLocation getID() {
      return this.key.location();
   }

   public ResourceKey<T> getKey() {
      return this.key;
   }

   public boolean is(ResourceLocation location) {
      return this.registryKey.location().equals(location);
   }

   public boolean is(ResourceKey<T> resourceKey) {
      return resourceKey == this.key;
   }

   public boolean is(Predicate<ResourceKey<T>> predicate) {
      return predicate.test(this.key);
   }

   public boolean is(Holder<T> other) {
      return other.unwrapKey().get() == this.key;
   }

   @Override
   public String toString() {
      return "DynamicHolder{" + this.key + "}";
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         return !(o instanceof HolderRef<?> that) ? false : Objects.equals(this.registryKey, that.registryKey) && Objects.equals(this.key, that.key);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.registryKey, this.key);
   }

   @Deprecated(
      forRemoval = true
   )
   public static class Opt<T> extends OptHolderRef<T> {
      protected Opt(ResourceKey<Registry<T>> registryKey, ResourceKey<T> key) {
         super(registryKey, key);
      }
   }
}
