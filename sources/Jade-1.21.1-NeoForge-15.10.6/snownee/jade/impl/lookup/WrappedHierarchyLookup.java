package snownee.jade.impl.lookup;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.tuple.Pair;
import snownee.jade.api.Accessor;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IJadeProvider;
import snownee.jade.impl.PriorityStore;

public class WrappedHierarchyLookup<T extends IJadeProvider> extends HierarchyLookup<T> {
   public final List<Pair<IHierarchyLookup<T>, Function<Accessor<?>, Object>>> overrides = Lists.newArrayList();
   private boolean empty = true;

   public WrappedHierarchyLookup() {
      super(Object.class);
   }

   public static <T extends IJadeProvider> WrappedHierarchyLookup<T> forAccessor() {
      WrappedHierarchyLookup<T> lookup = new WrappedHierarchyLookup<>();
      lookup.overrides
         .add(
            Pair.of(
               new HierarchyLookup(Block.class),
               (Function<Accessor, >)accessor -> accessor instanceof BlockAccessor blockAccessor ? blockAccessor.getBlock() : null
            )
         );
      return lookup;
   }

   public List<T> wrappedGet(Accessor<?> accessor) {
      Set<T> set = Sets.newLinkedHashSet();

      for (Pair<IHierarchyLookup<T>, Function<Accessor<?>, Object>> override : this.overrides) {
         Object o = ((Function)override.getRight()).apply(accessor);
         if (o != null) {
            set.addAll(((IHierarchyLookup)override.getLeft()).get(o));
         }
      }

      set.addAll(this.get(accessor.getTarget()));
      return ImmutableList.sortedCopyOf(COMPARATOR, set);
   }

   public boolean hitsAny(Accessor<?> accessor, BiPredicate<T, Accessor<?>> predicate) {
      for (T provider : this.wrappedGet(accessor)) {
         if (predicate.test(provider, accessor)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void register(Class<?> clazz, T provider) {
      for (Pair<IHierarchyLookup<T>, Function<Accessor<?>, Object>> override : this.overrides) {
         if (((IHierarchyLookup)override.getLeft()).isClassAcceptable(clazz)) {
            ((IHierarchyLookup)override.getLeft()).register(clazz, provider);
            this.empty = false;
            return;
         }
      }

      super.register(clazz, provider);
      this.empty = false;
   }

   @Override
   public boolean isClassAcceptable(Class<?> clazz) {
      for (Pair<IHierarchyLookup<T>, Function<Accessor<?>, Object>> override : this.overrides) {
         if (((IHierarchyLookup)override.getLeft()).isClassAcceptable(clazz)) {
            return true;
         }
      }

      return super.isClassAcceptable(clazz);
   }

   @Override
   public void invalidate() {
      for (Pair<IHierarchyLookup<T>, Function<Accessor<?>, Object>> override : this.overrides) {
         ((IHierarchyLookup)override.getLeft()).invalidate();
      }

      super.invalidate();
   }

   @Override
   public void loadComplete(PriorityStore<ResourceLocation, IJadeProvider> priorityStore) {
      for (Pair<IHierarchyLookup<T>, Function<Accessor<?>, Object>> override : this.overrides) {
         ((IHierarchyLookup)override.getLeft()).loadComplete(priorityStore);
      }

      super.loadComplete(priorityStore);
   }

   @Override
   public boolean isEmpty() {
      return this.empty;
   }

   @Override
   public Stream<Entry<Class<?>, Collection<T>>> entries() {
      Stream<Entry<Class<?>, Collection<T>>> stream = super.entries();

      for (Pair<IHierarchyLookup<T>, Function<Accessor<?>, Object>> override : this.overrides) {
         stream = Stream.concat(stream, ((IHierarchyLookup)override.getLeft()).entries());
      }

      return stream;
   }
}
