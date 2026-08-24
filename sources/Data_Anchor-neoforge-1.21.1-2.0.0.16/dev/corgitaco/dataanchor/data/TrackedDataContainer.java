package dev.corgitaco.dataanchor.data;

import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistry;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TrackedDataContainer<O, T extends TrackedData<O>> {
   <E extends T> Optional<E> dataAnchor$getTrackedData(TrackedDataKey<E> var1);

   void dataAnchor$createTrackedData();

   Collection<TrackedDataKey<T>> dataAnchor$getTrackedDataKeys();

   static <O, T extends TrackedData<O>> TrackedDataContainer<O, T> makeBasicContainer(TrackedDataRegistry<O, T> registry, O o, boolean isClient) {
      return makeBasicContainer(registry, o, isClient, false);
   }

   static <O, T extends TrackedData<O>> TrackedDataContainer<O, T> makeBasicContainer(
      TrackedDataRegistry<O, T> registry, O o, boolean isClient, boolean lazyLoad
   ) {
      return new TrackedDataContainer<O, T>() {
         private boolean lazyLoaded = !lazyLoad;
         private final Map<TrackedDataKey<T>, T> trackedDataMap = new Reference2ReferenceOpenHashMap();
         private final List<TrackedDataKey<T>> keys = List.copyOf(registry.factories().keySet());

         @Override
         public <E extends T> Optional<E> dataAnchor$getTrackedData(TrackedDataKey<E> key) {
            if (!this.lazyLoaded) {
               this.dataAnchor$createTrackedData();
               this.lazyLoaded = true;
            }

            T t = this.trackedDataMap.get(key);
            return t == null ? Optional.empty() : Optional.of((E)t);
         }

         @Override
         public void dataAnchor$createTrackedData() {
            registry.factories().forEach((key, factory) -> {
               T trackedData = factory.create((TrackedDataKey<T>)key, o);
               if (trackedData != null) {
                  if (isClient) {
                     if (trackedData instanceof ClientTrackedData) {
                        this.trackedDataMap.put((TrackedDataKey<T>)key, trackedData);
                     }
                  } else if (trackedData instanceof ServerTrackedData) {
                     this.trackedDataMap.put((TrackedDataKey<T>)key, trackedData);
                  }
               }
            });
         }

         @Override
         public Collection<TrackedDataKey<T>> dataAnchor$getTrackedDataKeys() {
            if (!this.lazyLoaded) {
               this.dataAnchor$createTrackedData();
               this.lazyLoaded = true;
            }

            return this.keys;
         }
      };
   }
}
