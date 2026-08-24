package vectorwing.farmersdelight.integration.emi;

import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.world.level.ItemLike;
import vectorwing.farmersdelight.common.registry.ModItems;

public class FDRecipeWorkstations {
   public static final EmiStack COOKING_POT = EmiStack.of((ItemLike)ModItems.COOKING_POT.get());
   public static final EmiStack CUTTING_BOARD = EmiStack.of((ItemLike)ModItems.CUTTING_BOARD.get());
   public static final EmiStack ORGANIC_COMPOST = EmiStack.of((ItemLike)ModItems.ORGANIC_COMPOST.get());
}
