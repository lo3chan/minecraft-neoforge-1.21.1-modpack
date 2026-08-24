package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.recipe.viewer.AddInformationKubeEvent;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.rhino.Context;
import java.util.List;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.plugin.client.BuiltinClientPlugin;
import net.minecraft.network.chat.Component;

public class REIAddInformationKubeEvent implements AddInformationKubeEvent {
   private final RecipeViewerEntryType type;

   public REIAddInformationKubeEvent(RecipeViewerEntryType type) {
      this.type = type;
   }

   @Override
   public void add(Context cx, Object filter, List<Component> info) {
      if (!info.isEmpty()) {
         EntryIngredient in = REIIntegration.ingredientOf(cx, this.type, filter);
         if (!in.isEmpty()) {
            BuiltinClientPlugin.getInstance().registerInformation(in, (Component)info.getFirst(), components -> {
               for (int i = 1; i < info.size(); i++) {
                  components.add(info.get(i));
               }

               return components;
            });
         }
      }
   }
}
