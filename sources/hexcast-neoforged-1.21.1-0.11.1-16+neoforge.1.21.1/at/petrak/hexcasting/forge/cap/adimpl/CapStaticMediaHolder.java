package at.petrak.hexcasting.forge.cap.adimpl;

import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import java.util.function.Supplier;
import net.minecraft.world.item.ItemStack;

public record CapStaticMediaHolder(Supplier<Long> baseWorth, int consumptionPriority, ItemStack stack) implements ADMediaHolder {
   @Override
   public long getMedia() {
      return this.baseWorth.get() * this.stack.getCount();
   }

   @Override
   public long getMaxMedia() {
      return this.getMedia();
   }

   @Override
   public void setMedia(long media) {
   }

   @Override
   public boolean canRecharge() {
      return false;
   }

   @Override
   public boolean canProvide() {
      return true;
   }

   @Override
   public int getConsumptionPriority() {
      return this.consumptionPriority;
   }

   @Override
   public boolean canConstructBattery() {
      return true;
   }

   @Override
   public long withdrawMedia(long cost, boolean simulate) {
      long worth = this.baseWorth.get();
      if (cost < 0L) {
         cost = worth * this.stack.getCount();
      }

      double itemsRequired = (double)cost / worth;
      int itemsUsed = Math.min((int)Math.ceil(itemsRequired), this.stack.getCount());
      if (!simulate) {
         this.stack.shrink(itemsUsed);
      }

      return itemsUsed * worth;
   }
}
