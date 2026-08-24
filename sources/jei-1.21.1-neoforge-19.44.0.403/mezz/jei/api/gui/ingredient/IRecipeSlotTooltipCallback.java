package mezz.jei.api.gui.ingredient;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import net.minecraft.network.chat.Component;

@Deprecated(
   since = "19.8.5",
   forRemoval = true
)
@FunctionalInterface
public interface IRecipeSlotTooltipCallback {
   @Deprecated(
      since = "19.5.4",
      forRemoval = true
   )
   void onTooltip(IRecipeSlotView var1, List<Component> var2);

   @Deprecated(
      since = "19.8.5",
      forRemoval = true
   )
   default void onRichTooltip(IRecipeSlotView recipeSlotView, ITooltipBuilder tooltip) {
      List<Component> components = tooltip.toLegacyToComponents();
      List<Component> changedComponents = new ArrayList<>(components);
      this.onTooltip(recipeSlotView, changedComponents);
      if (!components.equals(changedComponents)) {
         tooltip.removeAll(components);
         tooltip.addAll(changedComponents);
      }
   }
}
