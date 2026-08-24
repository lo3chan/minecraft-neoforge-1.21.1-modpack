package mezz.jei.api.helpers;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.network.chat.Component;

public interface IModIdHelper {
   String getModNameForModId(String var1);

   boolean isDisplayingModNameEnabled();

   @Deprecated(
      since = "19.31.0",
      forRemoval = true
   )
   String getFormattedModNameForModId(String var1);

   default Component getFormattedModNameComponentForModId(String modId) {
      return Component.literal(this.getFormattedModNameForModId(modId));
   }

   Set<String> getModAliases(String var1);

   @Deprecated(
      since = "19.5.4",
      forRemoval = true
   )
   <T> List<Component> addModNameToIngredientTooltip(List<Component> var1, T var2, IIngredientHelper<T> var3);

   @Deprecated(
      since = "19.5.4",
      forRemoval = true
   )
   <T> List<Component> addModNameToIngredientTooltip(List<Component> var1, ITypedIngredient<T> var2);

   <T> Optional<Component> getModNameForTooltip(ITypedIngredient<T> var1);
}
