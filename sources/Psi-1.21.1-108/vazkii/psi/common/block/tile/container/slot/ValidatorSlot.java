package vazkii.psi.common.block.tile.container.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ValidatorSlot extends Slot {
   public ValidatorSlot(Container inventoryIn, int index, int xPosition, int yPosition) {
      super(inventoryIn, index, xPosition, yPosition);
   }

   public boolean mayPlace(@NotNull ItemStack stack) {
      return this.container.canPlaceItem(this.getSlotIndex(), stack);
   }
}
