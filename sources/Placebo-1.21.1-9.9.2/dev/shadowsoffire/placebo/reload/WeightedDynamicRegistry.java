package dev.shadowsoffire.placebo.reload;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import dev.shadowsoffire.placebo.codec.CodecProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.util.random.WeightedEntry.Wrapper;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.Logger;

public abstract class WeightedDynamicRegistry<V extends CodecProvider<? super V> & WeightedDynamicRegistry.ILuckyWeighted> extends DynamicRegistry<V> {
   protected List<Wrapper<V>> zeroLuckList = Collections.emptyList();
   protected int zeroLuckTotalWeight = 0;

   public WeightedDynamicRegistry(Logger logger, String path, boolean synced, boolean subtypes) {
      super(logger, path, synced, subtypes);
   }

   @Override
   protected void beginReload(DynamicRegistry.ReloadType type) {
      super.beginReload(type);
      this.zeroLuckList = Collections.emptyList();
      this.zeroLuckTotalWeight = 0;
   }

   @Override
   protected void validateItem(ResourceLocation key, V item) {
      super.validateItem(key, item);
      Preconditions.checkArgument(item.getQuality() >= 0.0F, "Item may not have negative quality!");
      Preconditions.checkArgument(item.getWeight() >= 0, "Item may not have negative weight!");
   }

   @Override
   protected void onReload(DynamicRegistry.ReloadType type) {
      super.onReload(type);
      this.zeroLuckList = this.registry
         .values()
         .stream()
         .map(item -> WeightedEntry.wrap(item, ((WeightedDynamicRegistry.ILuckyWeighted)item).getWeight()))
         .toList();
      this.zeroLuckTotalWeight = WeightedRandom.getTotalWeight(this.zeroLuckList);
   }

   @Nullable
   public V getRandomItem(RandomSource rand) {
      return this.getRandomItem(rand, 0.0F);
   }

   @Nullable
   public V getRandomItem(RandomSource rand, float luck) {
      return luck == 0.0F
         ? WeightedRandom.getRandomItem(rand, this.zeroLuckList, this.zeroLuckTotalWeight).<V>map(Wrapper::data).orElse(null)
         : this.getRandomItem(rand, luck, Predicates.alwaysTrue());
   }

   @Nullable
   @SafeVarargs
   public final V getRandomItem(RandomSource rand, float luck, Predicate<V>... filters) {
      List<Wrapper<V>> list = new ArrayList<>(this.zeroLuckList.size());
      Stream<V> stream = this.registry.values().stream();

      for (Predicate<V> filter : filters) {
         stream = stream.filter(filter);
      }

      stream.map(l -> l.wrap(luck)).forEach(list::add);
      return WeightedRandom.getRandomItem(rand, list).<V>map(Wrapper::data).orElse(null);
   }

   public interface IDimensional {
      @Nullable
      Set<ResourceLocation> getDimensions();

      static <T extends WeightedDynamicRegistry.IDimensional> Predicate<T> createPredicate(ResourceLocation dimId) {
         return obj -> {
            Set<ResourceLocation> dims = obj.getDimensions();
            return dims == null || dims.isEmpty() || dims.contains(dimId);
         };
      }

      static <T extends WeightedDynamicRegistry.IDimensional> Predicate<T> matches(Level level) {
         return createPredicate(level.dimension().location());
      }
   }

   public interface ILuckyWeighted {
      float getQuality();

      int getWeight();

      default <T extends WeightedDynamicRegistry.ILuckyWeighted> Wrapper<T> wrap(float luck) {
         return wrap((T)this, luck);
      }

      static <T extends WeightedDynamicRegistry.ILuckyWeighted> Wrapper<T> wrap(T item, float luck) {
         return WeightedEntry.wrap(item, Math.max(0, item.getWeight() + (int)(luck * item.getQuality())));
      }
   }
}
