package net.mehvahdjukaar.moonlight.api.misc;

import com.mojang.datafixers.util.Either;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Holder.Kind;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.ApiStatus.Internal;

@Deprecated(
   forRemoval = true
)
public class DynamicHolder<T> implements Supplier<T>, Holder<T> {
   private static final WeakHashSet<DynamicHolder<?>> REFERENCES = new WeakHashSet<>();
   private final ResourceKey<Registry<T>> registryKey;
   private final ResourceKey<T> key;
   protected final ThreadLocal<Holder<T>> instance = new ThreadLocal<>();

   @Internal
   public static void clearCache() {
      REFERENCES.forEach(DynamicHolder::invalidateInstance);
   }

   protected DynamicHolder(ResourceKey<Registry<T>> registryKey, ResourceKey<T> key) {
      this.registryKey = registryKey;
      this.key = key;
      REFERENCES.add(this);
   }

   public static <A> DynamicHolder<A> of(String id, ResourceKey<Registry<A>> registry) {
      return of(ResourceLocation.tryParse(id), registry);
   }

   public static <A> DynamicHolder<A> of(ResourceLocation location, ResourceKey<Registry<A>> registry) {
      return new DynamicHolder((ResourceKey<Registry<T>>)registry, ResourceKey.create(registry, location));
   }

   public static <A> DynamicHolder<A> of(ResourceKey<A> key) {
      return new DynamicHolder(ResourceKey.createRegistryKey(key.registry()), (ResourceKey<T>)key);
   }

   protected void invalidateInstance() {
      this.instance.remove();
   }

   protected Holder<T> getInstance() {
      Holder<T> value = this.instance.get();
      if (value == null) {
         RegistryAccess r = Utils.hackyGetRegistryAccess();
         Registry<T> reg = r.registryOrThrow(this.registryKey);

         try {
            value = reg.getHolderOrThrow(this.key);
            this.instance.set(value);
         } catch (Exception var5) {
            throw new RuntimeException(
               "Failed to get object from registry: "
                  + this.key
                  + ".\nCalled from "
                  + Thread.currentThread()
                  + ".\nRegistry content was: "
                  + reg.entrySet().stream().map(b -> ((ResourceKey)b.getKey()).location()).toList(),
               var5
            );
         }
      }

      return value;
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

   @Override
   public T get() {
      return this.value();
   }

   public T value() {
      return (T)this.getInstance().value();
   }

   public boolean isBound() {
      return true;
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
      return other == this || other.unwrapKey().get() == this.key;
   }

   public boolean is(TagKey<T> tagKey) {
      return this.getInstance().is(tagKey);
   }

   public Stream<TagKey<T>> tags() {
      return this.getInstance().tags();
   }

   public Either<ResourceKey<T>, T> unwrap() {
      return Either.left(this.key);
   }

   public Optional<ResourceKey<T>> unwrapKey() {
      return Optional.of(this.key);
   }

   public Kind kind() {
      return Kind.REFERENCE;
   }

   public boolean canSerializeIn(HolderOwner<T> owner) {
      return this.getInstance().canSerializeIn(owner);
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
         return !(o instanceof DynamicHolder<?> that) ? false : Objects.equals(this.registryKey, that.registryKey) && Objects.equals(this.key, that.key);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.registryKey, this.key);
   }
}
