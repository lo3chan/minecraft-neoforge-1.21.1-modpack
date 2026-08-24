package snownee.jade.impl.lookup;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;
import net.minecraft.core.IdMapper;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import snownee.jade.Jade;
import snownee.jade.api.IJadeProvider;
import snownee.jade.impl.PriorityStore;

public class PairHierarchyLookup<T extends IJadeProvider> implements IHierarchyLookup<T> {
   public final IHierarchyLookup<T> first;
   public final IHierarchyLookup<T> second;
   private final Cache<Pair<Class<?>, Class<?>>, List<T>> mergedCache = CacheBuilder.newBuilder().build();
   protected boolean idMapped;
   @Nullable
   protected IdMapper<T> idMapper;

   public PairHierarchyLookup(IHierarchyLookup<T> first, IHierarchyLookup<T> second) {
      this.first = first;
      this.second = second;
   }

   public <ANY> List<ANY> getMerged(Object first, Object second) {
      Objects.requireNonNull(first);
      Objects.requireNonNull(second);

      try {
         return (List<ANY>)this.mergedCache.get(Pair.of(first.getClass(), second.getClass()), () -> {
            List<T> firstList = this.first.get(first);
            List<T> secondList = this.second.get(second);
            if (firstList.isEmpty()) {
               return secondList;
            } else {
               return (List<T>)(secondList.isEmpty() ? firstList : ImmutableList.sortedCopyOf(COMPARATOR, Iterables.concat(firstList, secondList)));
            }
         });
      } catch (ExecutionException var4) {
         Jade.LOGGER.error("", var4);
         return List.of();
      }
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
      if (this.first.isClassAcceptable(clazz)) {
         this.first.register(clazz, provider);
      } else {
         if (!this.second.isClassAcceptable(clazz)) {
            throw new IllegalArgumentException("Class " + clazz + " is not acceptable");
         }

         this.second.register(clazz, provider);
      }
   }

   @Override
   public boolean isClassAcceptable(Class<?> clazz) {
      return this.first.isClassAcceptable(clazz) || this.second.isClassAcceptable(clazz);
   }

   @Override
   public List<T> get(Class<?> clazz) {
      List<T> result = this.first.get(clazz);
      if (result.isEmpty()) {
         result = this.second.get(clazz);
      }

      return result;
   }

   @Override
   public boolean isEmpty() {
      return this.first.isEmpty() && this.second.isEmpty();
   }

   @Override
   public Stream<Entry<Class<?>, Collection<T>>> entries() {
      return Stream.concat(this.first.entries(), this.second.entries());
   }

   @Override
   public void invalidate() {
      this.first.invalidate();
      this.second.invalidate();
      this.mergedCache.invalidateAll();
   }

   @Override
   public void loadComplete(PriorityStore<ResourceLocation, IJadeProvider> priorityStore) {
      this.first.loadComplete(priorityStore);
      this.second.loadComplete(priorityStore);
      if (this.idMapped) {
         this.idMapper = this.createIdMapper();
      }
   }
}
