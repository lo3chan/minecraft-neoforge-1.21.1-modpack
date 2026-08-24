package at.petrak.hexcasting.forge.cap.adimpl;

import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.item.MediaHolderItem;
import net.minecraft.world.item.ItemStack;

public record CapItemMediaHolder(MediaHolderItem holder, ItemStack stack) implements ADMediaHolder {
   @Override
   public long getMedia() {
      return this.holder.getMedia(this.stack);
   }

   @Override
   public long getMaxMedia() {
      return this.holder.getMaxMedia(this.stack);
   }

   @Override
   public void setMedia(long media) {
      this.holder.setMedia(this.stack, media);
   }

   @Override
   public boolean canRecharge() {
      return this.holder.canRecharge(this.stack);
   }

   @Override
   public boolean canProvide() {
      return this.holder.canProvideMedia(this.stack);
   }

   @Override
   public int getConsumptionPriority() {
      return 4000;
   }

   @Override
   public boolean canConstructBattery() {
      return false;
   }

   @Override
   public long withdrawMedia(long cost, boolean simulate) {
      return this.holder.withdrawMedia(this.stack, cost, simulate);
   }

   @Override
   public long insertMedia(long amount, boolean simulate) {
      return this.holder.insertMedia(this.stack, amount, simulate);
   }
}
