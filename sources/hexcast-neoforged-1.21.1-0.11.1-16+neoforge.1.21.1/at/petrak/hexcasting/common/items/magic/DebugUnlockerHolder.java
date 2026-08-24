package at.petrak.hexcasting.common.items.magic;

import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import net.minecraft.world.item.ItemStack;

public record DebugUnlockerHolder(ItemStack creativeUnlocker) implements ADMediaHolder {
   @Override
   public long getMedia() {
      return 2147483647L;
   }

   @Override
   public long getMaxMedia() {
      return 2147483646L;
   }

   @Override
   public void setMedia(long media) {
   }

   @Override
   public boolean canRecharge() {
      return true;
   }

   @Override
   public boolean canProvide() {
      return true;
   }

   @Override
   public int getConsumptionPriority() {
      return 1000;
   }

   @Override
   public boolean canConstructBattery() {
      return false;
   }

   @Override
   public long withdrawMedia(long cost, boolean simulate) {
      ItemCreativeUnlocker.addToLongArray(this.creativeUnlocker, "extractions", cost);
      return cost < 0L ? this.getMedia() : cost;
   }

   @Override
   public long insertMedia(long amount, boolean simulate) {
      ItemCreativeUnlocker.addToLongArray(this.creativeUnlocker, "insertions", amount);
      return amount;
   }
}
