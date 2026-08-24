package mezz.jei.common.search;

import java.util.Collection;
import java.util.function.Consumer;
import mezz.jei.api.search.ISearchStorage;
import mezz.jei.modshade.net.mezzdev.bakedsubstring.BakedSubstringIndex;

public class BakedSubstringIndexSearchStorage<T> implements ISearchStorage<T> {
   private final BakedSubstringIndex<T> bakedStorage;
   private final GeneralizedSuffixTreeSearchStorage<T> mutableStorage = new GeneralizedSuffixTreeSearchStorage<>();

   public BakedSubstringIndexSearchStorage(BakedSubstringIndex<T> bakedStorage) {
      this.bakedStorage = bakedStorage;
   }

   @Override
   public void getSearchResults(String token, Consumer<Collection<T>> resultsConsumer) {
      resultsConsumer.accept(this.bakedStorage.getSearchResults(token));
      this.mutableStorage.getSearchResults(token, resultsConsumer);
   }

   @Override
   public void getAllElements(Consumer<Collection<T>> resultsConsumer) {
      resultsConsumer.accept(this.bakedStorage.getAllElements());
      this.mutableStorage.getAllElements(resultsConsumer);
   }

   @Override
   public void put(String key, T value) {
      this.mutableStorage.put(key, value);
   }

   @Override
   public String statistics() {
      return "BakedSubstringIndexSearchStorage: baked=" + this.bakedStorage + ", runtimeStorage=" + this.mutableStorage.statistics();
   }
}
