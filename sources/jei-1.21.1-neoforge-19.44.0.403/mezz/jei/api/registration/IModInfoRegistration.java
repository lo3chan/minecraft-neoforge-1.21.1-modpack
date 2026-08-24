package mezz.jei.api.registration;

import java.util.Collection;
import java.util.Set;

public interface IModInfoRegistration {
   void addModAliases(String var1, Collection<String> var2);

   default void addModAliases(String modId, String... aliases) {
      this.addModAliases(modId, Set.of(aliases));
   }
}
