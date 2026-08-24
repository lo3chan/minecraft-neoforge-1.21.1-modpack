package net.mehvahdjukaar.moonlight.api.misc;

import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

@Deprecated(
   forRemoval = true
)
public class OptionalHolder<T> extends DynamicHolder<T> {
   private boolean resolved = false;
   private boolean empty = false;

   protected OptionalHolder(ResourceKey<Registry<T>> registryKey, ResourceKey<T> key) {
      super(registryKey, key);
   }

   public static <A> OptionalHolder<A> of(ResourceLocation location, ResourceKey<Registry<A>> registry) {
      return new OptionalHolder((ResourceKey<Registry<T>>)registry, ResourceKey.create(registry, location));
   }

   public static <A> OptionalHolder<A> of(ResourceKey<A> key) {
      return new OptionalHolder(ResourceKey.createRegistryKey(key.registry()), (ResourceKey<T>)key);
   }

   public static <A> OptionalHolder<A> of(String id, ResourceKey<Registry<A>> registry) {
      return of(ResourceLocation.tryParse(id), registry);
   }

   @Override
   protected void invalidateInstance() {
      super.invalidateInstance();
      this.resolved = false;
      this.empty = false;
   }

   @Nullable
   @Override
   protected Holder<T> getInstance() {
      if (!this.resolved) {
         this.resolved = true;

         try {
            return super.getInstance();
         } catch (Exception var2) {
            this.empty = this.instance.get() != null;
         }
      }

      return this.instance.get();
   }

   @Override
   public Stream<TagKey<T>> tags() {
      Holder<T> i = this.getInstance();
      return i != null ? i.tags() : Stream.empty();
   }

   @Override
   public boolean is(TagKey<T> tagKey) {
      Holder<T> i = this.getInstance();
      return i != null ? i.is(tagKey) : false;
   }

   @Nullable
   @Override
   public T get() {
      return super.get();
   }

   @Nullable
   @Override
   public T value() {
      Holder<T> i = this.getInstance();
      return (T)(i != null ? i.value() : null);
   }

   public Optional<Holder<T>> asOptional() {
      return Optional.ofNullable(this.getInstance());
   }

   public Optional<T> asOptionalValue() {
      return Optional.ofNullable(this.value());
   }

   public boolean isPresent() {
      return !this.isEmpty();
   }

   public boolean isEmpty() {
      if (!this.resolved) {
         this.getInstance();
      }

      return this.empty;
   }
}
