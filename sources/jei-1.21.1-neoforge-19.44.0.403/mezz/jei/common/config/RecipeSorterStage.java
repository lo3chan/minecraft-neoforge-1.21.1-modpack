package mezz.jei.common.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public enum RecipeSorterStage {
   BOOKMARKED,
   CRAFTABLE;

   public static final List<RecipeSorterStage> defaultStages = List.of(BOOKMARKED, CRAFTABLE);

   public boolean isEnabled(IClientConfig clientConfig) {
      return clientConfig.recipeSorterStages().getValue().contains(this);
   }

   public void setEnabled(IClientConfig clientConfig, boolean enabled) {
      List<RecipeSorterStage> recipeSorterStages = clientConfig.recipeSorterStages().getValue();
      boolean currentlyEnabled = recipeSorterStages.contains(this);
      if (enabled != currentlyEnabled) {
         List<RecipeSorterStage> var5 = new ArrayList<>(recipeSorterStages);
         if (enabled) {
            var5.add(this);
         } else {
            var5.remove(this);
         }

         clientConfig.recipeSorterStages().set(var5);
      }
   }

   public static Set<RecipeSorterStage> getEnabled(IClientConfig clientConfig) {
      return Set.copyOf(clientConfig.recipeSorterStages().getValue());
   }
}
