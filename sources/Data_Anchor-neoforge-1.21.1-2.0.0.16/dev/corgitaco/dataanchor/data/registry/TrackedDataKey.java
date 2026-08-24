package dev.corgitaco.dataanchor.data.registry;

import dev.corgitaco.dataanchor.data.TrackedData;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public class TrackedDataKey<T extends TrackedData<?>> {
   private static final Map<TrackedDataRegistry<?, ?>, Map<ResourceLocation, TrackedDataKey<?>>> TRACKED_DATA = new Reference2ObjectOpenHashMap();
   private final ResourceLocation id;

   private TrackedDataKey(ResourceLocation id) {
      this.id = id;
   }

   public static <E, T extends TrackedData<E>, KEY extends TrackedDataKey<T>> KEY of(
      TrackedDataRegistry<E, T> dataRegistry, Class<? extends T> clazz, ResourceLocation id
   ) {
      if (clazz.isInterface()) {
         throw new IllegalArgumentException("TrackedData class must not be an interface");
      } else if (Modifier.isAbstract(clazz.getModifiers())) {
         throw new IllegalArgumentException("TrackedData class must not be an abstract class!");
      } else if (clazz.isAnonymousClass()) {
         throw new IllegalArgumentException("TrackedData class must not be an anonymous class!");
      } else {
         TrackedDataKey<? extends TrackedData> trackedDataKey = (TrackedDataKey<? extends TrackedData>)TRACKED_DATA.computeIfAbsent(
               dataRegistry, k -> new HashMap<>()
            )
            .get(id);
         if (trackedDataKey != null) {
            return (KEY)trackedDataKey;
         } else if (TRACKED_DATA.computeIfAbsent(dataRegistry, k -> new HashMap<>()).containsKey(id)) {
            throw new IllegalArgumentException("Attempted to register a key with a duplicate ID");
         } else {
            TrackedDataKey<T> key = new TrackedDataKey<>(id);
            TRACKED_DATA.computeIfAbsent(dataRegistry, k -> new HashMap<>()).put(id, key);
            return (KEY)key;
         }
      }
   }

   public static <E, T extends TrackedData<E>> TrackedDataKey<T> fromID(TrackedDataRegistry<E, T> dataRegistry, ResourceLocation id) {
      return (TrackedDataKey<T>)TRACKED_DATA.computeIfAbsent(dataRegistry, k -> new HashMap<>()).get(id);
   }

   public ResourceLocation getId() {
      return this.id;
   }
}
