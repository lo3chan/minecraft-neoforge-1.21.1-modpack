package mezz.jei.api.gui.ingredient;

import mezz.jei.api.gui.builder.ITooltipBuilder;

@FunctionalInterface
public interface IRecipeSlotRichTooltipCallback {
   void onRichTooltip(IRecipeSlotView var1, ITooltipBuilder var2);
}
