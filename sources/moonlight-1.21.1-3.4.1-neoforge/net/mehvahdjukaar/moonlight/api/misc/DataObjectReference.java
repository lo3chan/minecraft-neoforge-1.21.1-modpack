package net.mehvahdjukaar.moonlight.api.misc;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class DataObjectReference<T> extends DynamicHolder<T> {
   public DataObjectReference(String id, ResourceKey<Registry<T>> registry) {
      this(ResourceLocation.tryParse(id), registry);
   }

   public DataObjectReference(ResourceLocation location, ResourceKey<Registry<T>> registry) {
      super(registry, ResourceKey.create(registry, location));
   }

   public DataObjectReference(ResourceKey<T> key) {
      super(ResourceKey.createRegistryKey(key.registry()), key);
   }

   public Holder<T> getHolder() {
      return this.getInstance();
   }

   @Deprecated(
      forRemoval = true
   )
   @Nullable
   public T getUnchecked() {
      return this.get();
   }
}
