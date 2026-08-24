package mezz.jei.api.search;

public interface ISearchStorageBuilder<T> {
   void put(String var1, T var2);

   ISearchStorage<T> build();
}
