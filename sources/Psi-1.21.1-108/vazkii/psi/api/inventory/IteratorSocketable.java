package vazkii.psi.api.inventory;

import java.util.Iterator;
import net.minecraft.world.item.ItemStack;
import vazkii.psi.api.cad.ISocketable;

public class IteratorSocketable implements Iterator<ItemStack> {
   private final ISocketable socketable;
   private int index = -1;
   private boolean removed = false;

   public IteratorSocketable(ISocketable socketable) {
      this.socketable = socketable;
   }

   @Override
   public boolean hasNext() {
      return this.socketable.isSocketSlotAvailable(this.index + 1);
   }

   public ItemStack next() {
      this.removed = false;
      return this.socketable.getBulletInSocket(this.index++);
   }

   @Override
   public void remove() {
      if (this.index >= 0 && !this.removed) {
         this.removed = true;
         this.socketable.setBulletInSocket(this.index, ItemStack.EMPTY);
      } else {
         throw new IllegalStateException();
      }
   }
}
