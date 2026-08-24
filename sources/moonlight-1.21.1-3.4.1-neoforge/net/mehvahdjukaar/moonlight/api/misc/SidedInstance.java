package net.mehvahdjukaar.moonlight.api.misc;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatType;
import org.jetbrains.annotations.ApiStatus.Internal;

public class SidedInstance<T> {
   private static final WeakHashSet<SidedInstance<?>> ALL = new WeakHashSet<>();
   private final Cache<ChatType, T> instances = CacheBuilder.newBuilder().weakKeys().build();
   private final Function<Provider, T> factory;

   private SidedInstance(Function<Provider, T> factory) {
      this.factory = factory;
   }

   public static <T> SidedInstance<T> of(Function<Provider, T> factory) {
      SidedInstance<T> instance = new SidedInstance<>(factory);
      ALL.add(instance);
      return instance;
   }

   @Internal
   public static void clearAll() {
      for (SidedInstance<?> i : ALL) {
         i.instances.invalidateAll();
      }
   }

   @Internal
   public static void clearAll(Provider ra) {
      ChatType key;
      try {
         key = getDummyKey(ra);
      } catch (Exception var4) {
         return;
      }

      for (SidedInstance<?> i : ALL) {
         i.instances.invalidate(key);
      }
   }

   public T get(Provider ra) {
      try {
         return (T)this.instances.get(getDummyKey(ra), () -> this.factory.apply(ra));
      } catch (ExecutionException var3) {
         throw new RuntimeException(var3);
      }
   }

   public void invalidate(Provider ra) {
      ChatType dummyKey = getDummyKey(ra);
      T instance = (T)this.instances.getIfPresent(dummyKey);
      if (instance != null) {
         this.instances.invalidate(dummyKey);
      }
   }

   public void set(Provider ra, T instance) {
      this.instances.put(getDummyKey(ra), instance);
   }

   private static ChatType getDummyKey(Provider ra) {
      try {
         return (ChatType)ra.lookupOrThrow(Registries.CHAT_TYPE).getOrThrow(ChatType.CHAT).value();
      } catch (Exception var2) {
         throw new IllegalStateException("Failed to find CHAT_TYPE registry! This is a VANILLA datapack registry! How is this possible??");
      }
   }
}
