package mezz.jei.common.search;

import java.util.Collection;
import java.util.function.Consumer;
import mezz.jei.api.search.ISearchStorage;

public class PrefixedSearchable<T, I> implements ISearchable<I> {
   private final ISearchStorage<I> searchStorage;
   private final PrefixInfo<T, I> prefixInfo;

   public PrefixedSearchable(ISearchStorage<I> searchStorage, PrefixInfo<T, I> prefixInfo) {
      this.searchStorage = searchStorage;
      this.prefixInfo = prefixInfo;
   }

   public ISearchStorage<I> getSearchStorage() {
      return this.searchStorage;
   }

   public Collection<String> getStrings(T element) {
      return this.prefixInfo.getStrings(element);
   }

   @Override
   public SearchMode getMode() {
      return this.prefixInfo.getMode();
   }

   @Override
   public void getSearchResults(String token, Consumer<Collection<I>> resultsConsumer) {
      this.searchStorage.getSearchResults(token, resultsConsumer);
   }

   @Override
   public void getAllElements(Consumer<Collection<I>> resultsConsumer) {
      this.searchStorage.getAllElements(resultsConsumer);
   }
}
