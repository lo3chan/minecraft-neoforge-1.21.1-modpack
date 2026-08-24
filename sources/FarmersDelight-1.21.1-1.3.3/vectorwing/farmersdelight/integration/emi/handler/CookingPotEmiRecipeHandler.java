package vectorwing.farmersdelight.integration.emi.handler;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.block.entity.container.CookingPotMenu;
import vectorwing.farmersdelight.integration.emi.FDRecipeCategories;

public class CookingPotEmiRecipeHandler implements StandardRecipeHandler<CookingPotMenu> {
   public List<Slot> getInputSources(CookingPotMenu handler) {
      List<Slot> slots = new ArrayList<>();

      for (int i = 0; i < 7; i++) {
         slots.add(handler.getSlot(i));
      }

      for (int i = 9; i < 45; i++) {
         slots.add(handler.getSlot(i));
      }

      return slots;
   }

   public List<Slot> getCraftingSlots(CookingPotMenu handler) {
      List<Slot> slots = new ArrayList<>();

      for (int i = 0; i < 7; i++) {
         slots.add(handler.getSlot(i));
      }

      return slots;
   }

   @Nullable
   public Slot getOutputSlot(CookingPotMenu handler) {
      return (Slot)handler.slots.get(8);
   }

   public boolean supportsRecipe(EmiRecipe recipe) {
      return recipe.getCategory() == FDRecipeCategories.COOKING && recipe.supportsRecipeTree();
   }
}
