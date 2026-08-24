package mezz.jei.modshade.net.mezzdev.suffixtree;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.function.Consumer;

public interface ISuffixTree<T> {
   void put(String var1, T var2);

   void getSearchResults(String var1, Consumer<Collection<T>> var2);

   default Set<T> getSearchResults(String token) {
      Set<T> results = Collections.newSetFromMap(new IdentityHashMap<>());
      this.getSearchResults(token, results::addAll);
      return results;
   }

   void getAllElements(Consumer<Collection<T>> var1);

   default Set<T> getAllElements() {
      Set<T> results = Collections.newSetFromMap(new IdentityHashMap<>());
      this.getAllElements(results::addAll);
      return results;
   }

   String statistics();
}
