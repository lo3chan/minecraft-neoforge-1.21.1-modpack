package net.mehvahdjukaar.moonlight.api.misc;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

public class OptHolderRef<T> extends HolderRef<T> {
   public static <A> OptHolderRef<A> of(ResourceLocation location, ResourceKey<Registry<A>> registry) {
      return new OptHolderRef((ResourceKey<Registry<T>>)registry, ResourceKey.create(registry, location));
   }

   public static <A> OptHolderRef<A> of(ResourceKey<A> key) {
      return new OptHolderRef(ResourceKey.createRegistryKey(key.registry()), (ResourceKey<T>)key);
   }

   protected OptHolderRef(ResourceKey<Registry<T>> registryKey, ResourceKey<T> key) {
      super(registryKey, key);
   }

   public boolean isPresent(Provider r) {
      return this.getHolder(r) != null;
   }

   public boolean isPresent(LevelReader level) {
      return this.getHolder(level) != null;
   }

   public void ifPresent(Provider r, Consumer<T> consumer) {
      Holder<T> h = this.getHolder(r);
      if (h != null) {
         consumer.accept((T)h.value());
      }
   }

   public Optional<Holder<T>> asOptionalHolder(Provider r) {
      return Optional.ofNullable(this.getHolder(r));
   }

   public Optional<T> asOptional(Provider r) {
      return Optional.ofNullable(this.get(r));
   }

   @Nullable
   @Override
   public T get(Provider r) {
      Holder<T> h = super.getHolder(r);
      return (T)(h != null ? h.value() : null);
   }

   @Nullable
   @Override
   public T get(Level level) {
      return super.get(level);
   }

   @Nullable
   @Override
   public T get(Entity entity) {
      return super.get(entity);
   }

   @Override
   public boolean is(T object, Provider r) {
      Holder<T> holder = this.getHolder(r);
      return holder == null ? false : holder.value() == object;
   }

   @Override
   public boolean is(TagKey<T> tag, Provider r) {
      Holder<T> holder = this.getHolder(r);
      return holder == null ? false : holder.is(tag);
   }

   @Nullable
   @Override
   public Holder<T> getHolder(Provider r) {
      return super.getHolder(r);
   }

   @Nullable
   @Override
   public Holder<T> getHolder(Level level) {
      return super.getHolder(level);
   }

   @Nullable
   @Override
   public Holder<T> getHolder(Entity entity) {
      return super.getHolder(entity);
   }

   @Nullable
   @Override
   public Holder<T> lookup(HolderGetter<T> lookup) {
      try {
         return super.lookup(lookup);
      } catch (Exception var3) {
         return null;
      }
   }
}
