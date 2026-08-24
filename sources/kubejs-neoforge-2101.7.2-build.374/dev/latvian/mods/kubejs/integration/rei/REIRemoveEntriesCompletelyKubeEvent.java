package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.kubejs.recipe.viewer.RemoveEntriesKubeEvent;
import dev.latvian.mods.rhino.Context;
import java.util.List;
import java.util.function.Predicate;
import me.shedaniel.rei.api.client.entry.filtering.base.BasicFilteringRule;
import me.shedaniel.rei.api.common.entry.EntryStack;

public class REIRemoveEntriesCompletelyKubeEvent implements RemoveEntriesKubeEvent {
   private final RecipeViewerEntryType type;
   private final List<EntryStack<?>> allEntries;
   private final BasicFilteringRule<?> rule;

   public REIRemoveEntriesCompletelyKubeEvent(RecipeViewerEntryType type, List<EntryStack<?>> allEntries, BasicFilteringRule<?> rule) {
      this.type = type;
      this.allEntries = allEntries;
      this.rule = rule;
   }

   @Override
   public void remove(Context cx, Object filter) {
      Predicate predicate = (Predicate)this.type.wrapPredicate(cx, filter);
      this.rule.hide(this.allEntries.stream().filter(e -> predicate.test(e.getValue())).toList());
   }
}
