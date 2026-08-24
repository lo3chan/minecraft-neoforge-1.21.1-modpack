package dev.shadowsoffire.placebo.mixin;

import dev.shadowsoffire.placebo.util.CachedObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ItemStack.class})
public class ItemStackMixin implements CachedObject.CachedObjectSource {
   private volatile Map<ResourceLocation, CachedObject<?>> cachedObjects = null;

   @Override
   public <T> T getOrCreate(ResourceLocation id, Function<ItemStack, T> deserializer, ToIntFunction<ItemStack> hasher) {
      CachedObject<?> cachedObj = this.getOrCreate().computeIfAbsent(id, key -> new CachedObject<>(key, deserializer, hasher));
      return (T)cachedObj.get((ItemStack)this);
   }

   private Map<ResourceLocation, CachedObject<?>> getOrCreate() {
      if (this.cachedObjects == null) {
         synchronized (this) {
            if (this.cachedObjects == null) {
               this.cachedObjects = new ConcurrentHashMap<>();
            }
         }
      }

      return this.cachedObjects;
   }
}
