package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.recipe.viewer.GroupEntriesKubeEvent;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.rhino.Context;
import java.util.function.Predicate;
import me.shedaniel.rei.api.client.registry.entry.CollapsibleEntryRegistry;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class REIGroupEntriesKubeEvent implements GroupEntriesKubeEvent {
   private final RecipeViewerEntryType type;
   private final EntryType<?> entryType;
   private final CollapsibleEntryRegistry registry;

   public REIGroupEntriesKubeEvent(RecipeViewerEntryType type, EntryType<?> entryType, CollapsibleEntryRegistry registry) {
      this.type = type;
      this.entryType = entryType;
      this.registry = registry;
   }

   @Override
   public void group(Context cx, Object filter, ResourceLocation groupId, Component description) {
      Predicate predicate = (Predicate)this.type.wrapPredicate(cx, filter);
      this.registry.group(groupId, description, e -> e.getType() == this.entryType && predicate.test(e.getValue()));
   }
}
