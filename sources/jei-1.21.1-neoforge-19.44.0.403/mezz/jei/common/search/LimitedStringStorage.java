package mezz.jei.common.search;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.function.Consumer;
import mezz.jei.api.search.ISearchStorage;
import mezz.jei.common.collect.SetMultiMap;

public class LimitedStringStorage<T> implements ISearchStorage<T> {
   private final SetMultiMap<String, T> multiMap;
   private final ISearchStorage<Set<T>> backingStorage;

   public LimitedStringStorage(ISearchStorage<Set<T>> searchStorage) {
      this.backingStorage = searchStorage;
      this.multiMap = new SetMultiMap<>(() -> Collections.newSetFromMap(new IdentityHashMap<>()));
   }

   public LimitedStringStorage(ISearchStorage<Set<T>> searchStorage, SetMultiMap<String, T> multiMap) {
      this.backingStorage = searchStorage;
      this.multiMap = multiMap;
   }

   @Override
   public void getSearchResults(String token, Consumer<Collection<T>> resultsConsumer) {
      this.backingStorage.getSearchResults(token, resultSet -> {
         for (Collection<T> result : resultSet) {
            resultsConsumer.accept(result);
         }
      });
   }

   @Override
   public void getAllElements(Consumer<Collection<T>> resultsConsumer) {
      Collection<T> values = this.multiMap.allValues();
      resultsConsumer.accept(values);
   }

   @Override
   public void put(String key, T value) {
      boolean isNewKey = !this.multiMap.containsKey(key);
      this.multiMap.put(key, value);
      if (isNewKey) {
         Set<T> set = this.multiMap.get(key);
         this.backingStorage.put(key, set);
      }
   }

   @Override
   public String statistics() {
      return "LimitedStringStorage: " + this.backingStorage.statistics();
   }
}
