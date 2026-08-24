package snownee.jade.impl.lookup;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;
import net.minecraft.core.IdMapper;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import snownee.jade.Jade;
import snownee.jade.api.IJadeProvider;
import snownee.jade.impl.PriorityStore;
import snownee.jade.impl.WailaCommonRegistration;

public class HierarchyLookup<T extends IJadeProvider> implements IHierarchyLookup<T> {
   private final Class<?> baseClass;
   private final Cache<Class<?>, List<T>> resultCache = CacheBuilder.newBuilder().build();
   private final boolean singleton;
   protected boolean idMapped;
   @Nullable
   protected IdMapper<T> idMapper;
   private ListMultimap<Class<?>, T> objects = ArrayListMultimap.create();

   public HierarchyLookup(Class<?> baseClass) {
      this(baseClass, false);
   }

   public HierarchyLookup(Class<?> baseClass, boolean singleton) {
      this.baseClass = baseClass;
      this.singleton = singleton;
   }

   @Override
   public void idMapped() {
      this.idMapped = true;
   }

   @Nullable
   @Override
   public IdMapper<T> idMapper() {
      return this.idMapper;
   }

   @Override
   public void register(Class<?> clazz, T provider) {
      Preconditions.checkArgument(this.isClassAcceptable(clazz), "Class %s is not acceptable", clazz);
      Objects.requireNonNull(provider.getUid());
      WailaCommonRegistration.instance().priorities.put(provider);
      this.objects.put(clazz, provider);
   }

   @Override
   public boolean isClassAcceptable(Class<?> clazz) {
      return this.baseClass.isAssignableFrom(clazz);
   }

   @Override
   public List<T> get(Class<?> clazz) {
      try {
         return (List<T>)this.resultCache.get(clazz, () -> {
            List<T> list = Lists.newArrayList();
            this.getInternal(clazz, list);
            list = ImmutableList.sortedCopyOf(COMPARATOR, list);
            return (List)(this.singleton && !list.isEmpty() ? ImmutableList.of((IJadeProvider)list.getFirst()) : list);
         });
      } catch (ExecutionException var3) {
         Jade.LOGGER.error("", var3);
         return List.of();
      }
   }

   private void getInternal(Class<?> clazz, List<T> list) {
      if (clazz != this.baseClass && clazz != Object.class) {
         this.getInternal(clazz.getSuperclass(), list);
      }

      list.addAll(this.objects.get(clazz));
   }

   @Override
   public boolean isEmpty() {
      return this.objects.isEmpty();
   }

   @Override
   public Stream<Entry<Class<?>, Collection<T>>> entries() {
      return this.objects.asMap().entrySet().stream();
   }

   @Override
   public void invalidate() {
      this.resultCache.invalidateAll();
   }

   @Override
   public void loadComplete(PriorityStore<ResourceLocation, IJadeProvider> priorityStore) {
      this.objects
         .asMap()
         .forEach(
            (clazz, list) -> {
               if (list.size() >= 2) {
                  Set<ResourceLocation> set = Sets.newHashSetWithExpectedSize(list.size());

                  for (T provider : list) {
                     if (set.contains(provider.getUid())) {
                        throw new IllegalStateException(
                           "Duplicate UID: %s for %s"
                              .formatted(
                                 provider.getUid(), list.stream().filter(p -> p.getUid().equals(provider.getUid())).map(p -> p.getClass().getName()).toList()
                              )
                        );
                     }

                     set.add(provider.getUid());
                  }
               }
            }
         );
      this.objects = ImmutableListMultimap.builder().orderValuesBy(Comparator.comparingInt(priorityStore::byValue)).putAll(this.objects).build();
      if (this.idMapped) {
         this.idMapper = this.createIdMapper();
      }
   }
}
