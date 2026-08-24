package vectorwing.farmersdelight.common.block.entity.container;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

@ParametersAreNonnullByDefault
public class CookingPotMealSlot extends SlotItemHandler {
   public CookingPotMealSlot(IItemHandler inventoryIn, int index, int xPosition, int yPosition) {
      super(inventoryIn, index, xPosition, yPosition);
   }

   public boolean mayPlace(ItemStack stack) {
      return false;
   }

   public boolean mayPickup(Player playerIn) {
      return false;
   }
}
