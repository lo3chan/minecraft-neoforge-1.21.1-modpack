package snownee.jade.impl.lookup;

import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.stream.Stream;
import net.minecraft.core.IdMapper;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import snownee.jade.Jade;
import snownee.jade.api.IJadeProvider;
import snownee.jade.impl.PriorityStore;
import snownee.jade.impl.WailaCommonRegistration;

public interface IHierarchyLookup<T extends IJadeProvider> {
   Comparator<IJadeProvider> COMPARATOR = Comparator.comparingInt($ -> WailaCommonRegistration.instance().priorities.byValue($));

   default IHierarchyLookup<? extends T> cast() {
      return this;
   }

   void idMapped();

   @Nullable
   IdMapper<T> idMapper();

   default List<ResourceLocation> mappedIds() {
      return Streams.stream(Objects.requireNonNull(this.idMapper())).map(IJadeProvider::getUid).toList();
   }

   void register(Class<?> var1, T var2);

   boolean isClassAcceptable(Class<?> var1);

   default List<T> get(Object obj) {
      return obj == null ? List.of() : this.get(obj.getClass());
   }

   List<T> get(Class<?> var1);

   boolean isEmpty();

   Stream<Entry<Class<?>, Collection<T>>> entries();

   void invalidate();

   void loadComplete(PriorityStore<ResourceLocation, IJadeProvider> var1);

   default IdMapper<T> createIdMapper() {
      List<T> list = this.entries().flatMap(entry -> entry.getValue().stream()).toList();
      IdMapper<T> idMapper = this.idMapper();
      if (idMapper == null) {
         idMapper = new IdMapper(list.size());
      }

      for (T provider : list) {
         if (idMapper.getId(provider) == -1) {
            idMapper.add(provider);
         }
      }

      return idMapper;
   }

   default void remapIds(List<ResourceLocation> ids) {
      IdMapper<T> idMapper = Objects.requireNonNull(this.idMapper());
      Map<ResourceLocation, T> map = Maps.newHashMapWithExpectedSize(idMapper.size());
      Streams.stream(idMapper)
         .forEach(
            provider -> {
               T oldProvider = map.put(provider.getUid(), (T)provider);
               if (oldProvider != provider && oldProvider != null) {
                  Jade.LOGGER
                     .warn(
                        "Found different data providers with same id {}, this may cause issues: {} and {}",
                        new Object[]{provider.getUid(), oldProvider, provider}
                     );
               }
            }
         );
      int i = 0;

      for (ResourceLocation id : ids) {
         T object = map.get(id);
         if (object != null) {
            idMapper.addMapping(object, i);
         }

         i++;
      }
   }
}
