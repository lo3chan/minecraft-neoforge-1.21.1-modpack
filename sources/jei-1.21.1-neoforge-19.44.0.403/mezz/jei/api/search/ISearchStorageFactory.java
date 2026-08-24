package mezz.jei.api.search;

@FunctionalInterface
public interface ISearchStorageFactory {
   <T> ISearchStorage<T> createSearchStorage();
}
