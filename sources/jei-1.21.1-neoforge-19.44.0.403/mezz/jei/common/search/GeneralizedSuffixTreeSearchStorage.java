package mezz.jei.common.search;

import java.util.Collection;
import java.util.function.Consumer;
import mezz.jei.api.search.ISearchStorage;
import mezz.jei.modshade.net.mezzdev.suffixtree.GeneralizedSuffixTree;

public class GeneralizedSuffixTreeSearchStorage<T> implements ISearchStorage<T> {
   private final GeneralizedSuffixTree<T> generalizedSuffixTree = new GeneralizedSuffixTree<>();

   @Override
   public void getSearchResults(String token, Consumer<Collection<T>> resultsConsumer) {
      this.generalizedSuffixTree.getSearchResults(token, resultsConsumer);
   }

   @Override
   public void getAllElements(Consumer<Collection<T>> resultsConsumer) {
      this.generalizedSuffixTree.getAllElements(resultsConsumer);
   }

   @Override
   public void put(String key, T value) {
      this.generalizedSuffixTree.put(key, value);
   }

   @Override
   public String statistics() {
      return this.generalizedSuffixTree.statistics();
   }
}
