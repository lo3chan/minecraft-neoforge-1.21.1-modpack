package dev.shadowsoffire.placebo.util;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class CachedObject<T> {
   public static final int HAS_NEVER_BEEN_INITIALIZED = -2;
   protected final ResourceLocation id;
   protected final Function<ItemStack, T> deserializer;
   protected final ToIntFunction<ItemStack> hasher;
   protected volatile T data = (T)null;
   protected volatile int lastNbtHash = -2;

   public CachedObject(ResourceLocation id, Function<ItemStack, T> deserializer, ToIntFunction<ItemStack> hasher) {
      this.id = id;
      this.deserializer = deserializer;
      this.hasher = hasher;
   }

   @Nullable
   public T get(ItemStack stack) {
      if (this.lastNbtHash == -2) {
         this.compute(stack);
         return this.data;
      } else {
         if (this.hasher.applyAsInt(stack) != this.lastNbtHash) {
            this.compute(stack);
         }

         return this.data;
      }
   }

   public void reset() {
      this.data = null;
      this.lastNbtHash = -2;
   }

   protected void compute(ItemStack stack) {
      synchronized (this) {
         this.data = this.deserializer.apply(stack);
         this.lastNbtHash = this.hasher.applyAsInt(stack);
      }
   }

   public static ToIntFunction<ItemStack> hashComponents(DataComponentType<?>... types) {
      List<DataComponentType<?>> typeList = Arrays.asList(types);
      return stack -> Arrays.hashCode(typeList.stream().<Object>map(stack::get).filter(Objects::nonNull).toArray());
   }

   public interface CachedObjectSource {
      <T> T getOrCreate(ResourceLocation var1, Function<ItemStack, T> var2, ToIntFunction<ItemStack> var3);

      static <T> T getOrCreate(ItemStack stack, ResourceLocation id, Function<ItemStack, T> deserializer, ToIntFunction<ItemStack> hasher) {
         return ((CachedObject.CachedObjectSource)stack).getOrCreate(id, deserializer, hasher);
      }
   }
}
