package jeresources.jei.plant;

import java.util.List;
import jeresources.api.drop.PlantDrop;
import jeresources.entry.PlantEntry;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlantTooltip implements IRecipeSlotTooltipCallback {
   private final PlantEntry entry;

   public PlantTooltip(PlantEntry entry) {
      this.entry = entry;
   }

   public void onTooltip(IRecipeSlotView recipeSlotView, @NotNull List<Component> tooltip) {
      if (recipeSlotView.getRole() != RecipeIngredientRole.INPUT) {
         tooltip.add(this.getChanceString((ItemStack)((ITypedIngredient)recipeSlotView.getDisplayedIngredient().get()).getIngredient()));
      }
   }

   public float getChance(ItemStack itemStack) {
      PlantDrop drop = this.entry.getDrop(itemStack);

      return switch (drop.getDropKind()) {
         case chance -> drop.getChance();
         case weight -> (float)drop.getWeight() / this.entry.getTotalWeight();
         case minMax -> 0.0F / 0.0F;
         default -> 0.0F;
      };
   }

   public int[] getMinMax(ItemStack itemStack) {
      PlantDrop drop = this.entry.getDrop(itemStack);
      return new int[]{drop.getMinDrop(), drop.getMaxDrop()};
   }

   private Component getChanceString(ItemStack itemStack) {
      float chance = this.getChance(itemStack);
      String toPrint;
      if (Float.isNaN(chance)) {
         int[] minMax = this.getMinMax(itemStack);
         toPrint = minMax[0] + (minMax[0] == minMax[1] ? "" : " - " + minMax[1]);
      } else {
         toPrint = String.format("%2.2f", chance * 100.0F).replace(",", ".") + "%";
      }

      return Component.literal(toPrint);
   }
}
