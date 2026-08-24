package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.kubejs.recipe.viewer.RemoveEntriesKubeEvent;
import dev.latvian.mods.rhino.Context;
import java.util.List;
import java.util.function.Predicate;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;

public class REIRemoveEntriesKubeEvent implements RemoveEntriesKubeEvent {
   private final RecipeViewerEntryType type;
   private final EntryRegistry registry;
   private final List<EntryStack<?>> allEntries;

   public REIRemoveEntriesKubeEvent(RecipeViewerEntryType type, EntryRegistry registry, List<EntryStack<?>> allEntries) {
      this.type = type;
      this.registry = registry;
      this.allEntries = allEntries;
   }

   @Override
   public void remove(Context cx, Object filter) {
      Predicate predicate = (Predicate)this.type.wrapPredicate(cx, filter);

      for (EntryStack<?> entry : this.allEntries) {
         if (predicate.test(entry.getValue())) {
            this.registry.removeEntry(entry);
         }
      }
   }
}
