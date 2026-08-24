package jeresources.jei.dungeon;

import java.util.List;
import jeresources.entry.DungeonEntry;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class DungeonTooltip implements IRecipeSlotTooltipCallback {
   private final DungeonEntry entry;

   public DungeonTooltip(DungeonEntry entry) {
      this.entry = entry;
   }

   public void onTooltip(IRecipeSlotView recipeSlotView, List<Component> tooltip) {
      tooltip.add(this.entry.getChestDrop((ItemStack)((ITypedIngredient)recipeSlotView.getDisplayedIngredient().get()).getIngredient()).toStringTextComponent());
   }
}
