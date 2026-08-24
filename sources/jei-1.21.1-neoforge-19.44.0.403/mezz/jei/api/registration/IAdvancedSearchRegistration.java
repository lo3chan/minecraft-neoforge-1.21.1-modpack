package mezz.jei.api.registration;

import mezz.jei.api.search.ISearchStorageBuilderFactory;
import mezz.jei.api.search.ISearchStorageFactory;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface IAdvancedSearchRegistration {
   ISearchStorageBuilderFactory getDefaultSearchStorageBuilderFactory();

   void replaceSearchStorage(ISearchStorageFactory var1);

   void replaceSearchStorage(ISearchStorageBuilderFactory var1);
}
