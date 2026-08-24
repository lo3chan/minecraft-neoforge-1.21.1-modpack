package mezz.jei.api.search;

import java.util.Objects;

@FunctionalInterface
public interface ISearchStorageBuilderFactory {
   <T> ISearchStorageBuilder<T> create();

   default <T> ISearchStorageBuilder<T> create(String id) {
      Objects.requireNonNull(id, "id");
      return this.create();
   }
}
