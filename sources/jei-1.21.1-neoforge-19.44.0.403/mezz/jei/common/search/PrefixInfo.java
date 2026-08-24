package mezz.jei.common.search;

import java.util.Collection;
import mezz.jei.api.search.ISearchStorageBuilder;
import mezz.jei.api.search.ISearchStorageBuilderFactory;
import org.jetbrains.annotations.Unmodifiable;

public class PrefixInfo<T, I> {
   private final String id;
   private final char prefix;
   private final PrefixInfo.IModeGetter modeGetter;
   private final PrefixInfo.IStringsGetter<T> stringsGetter;
   private final ISearchStorageBuilderFactory searchStorageBuilderFactory;

   public PrefixInfo(
      String id,
      char prefix,
      PrefixInfo.IModeGetter modeGetter,
      PrefixInfo.IStringsGetter<T> stringsGetter,
      ISearchStorageBuilderFactory searchStorageBuilderFactory
   ) {
      this.id = id;
      this.prefix = prefix;
      this.modeGetter = modeGetter;
      this.stringsGetter = stringsGetter;
      this.searchStorageBuilderFactory = searchStorageBuilderFactory;
   }

   public char getPrefix() {
      return this.prefix;
   }

   public SearchMode getMode() {
      return this.modeGetter.getMode();
   }

   public ISearchStorageBuilder<I> createStorageBuilder() {
      return this.searchStorageBuilderFactory.create(this.id);
   }

   @Unmodifiable
   public Collection<String> getStrings(T element) {
      return this.stringsGetter.getStrings(element);
   }

   @Override
   public String toString() {
      return "PrefixInfo{" + this.id + "}";
   }

   @FunctionalInterface
   public interface IModeGetter {
      SearchMode getMode();
   }

   @FunctionalInterface
   public interface IStringsGetter<T> {
      @Unmodifiable
      Collection<String> getStrings(T var1);
   }
}
